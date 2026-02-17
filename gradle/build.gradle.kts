import org.gradle.api.GradleException
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.3.0"
    id("org.jetbrains.dokka") version "2.1.0"
    id("org.jetbrains.dokka-javadoc") version "2.1.0"
    id("scripts-task")
    id("idea")
    `maven-publish`

    id("com.github.ben-manes.versions") version "0.53.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.19"
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

// == Core build context (shared via rootProject.extra across split scripts) ==

val gameName by extra { property("socha.gameName") as String }
val versions = arrayOf("year", "minor", "patch").map { property("socha.version.$it").toString().toInt() }
val versionObject by extra { KotlinVersion(versions[0], versions[1], versions[2]) }
version = versionObject.toString() + property("socha.version.suffix").toString().takeUnless { it.isBlank() }?.let { "-$it" }.orEmpty()
val year by extra { "20${versionObject.major}" }
val game by extra { "${gameName}_$year" }

val bundleDir by extra { layout.buildDirectory.dir("bundle").get().asFile }
val bundledPlayer by extra { "randomplayer-$gameName-$version.jar" }
val testingDir by extra { layout.buildDirectory.dir("tests").get().asFile }
val documentedProjects by extra { listOf("sdk", "plugin$year") }

val isBeta by extra { versionObject.minor == 0 }
val enableTestClient by extra { arrayOf("check", "testTestClient").any { gradle.startParameter.taskNames.contains(it) } || !isBeta }
val enableIntegrationTesting by extra { !project.hasProperty("nointegration") && (!isBeta || enableTestClient) }

val javaToolchainVersion by extra { 25 }
val javaTargetVersion by extra { JavaVersion.VERSION_1_8 }
val kotlinJvmTarget by extra { JvmTarget.fromTarget(javaTargetVersion.toString()) }

val javaRuntimeVersion = JavaVersion.current()
println("Current version: $version (unstable: $isBeta) Game: $game (Kotlin ${kotlinExtension.coreLibrariesVersion}, Java runtime $javaRuntimeVersion, toolchain $javaToolchainVersion, target $javaTargetVersion)")
if (!javaRuntimeVersion.isCompatibleWith(JavaVersion.VERSION_17)) {
    System.err.println("Gradle 9+ requires Java 17+ to run. Toolchain is set to $javaToolchainVersion; install it or enable toolchain auto-download.")
}

// == Split script modules ==
apply(from = "gradle/integration-tasks.gradle.kts")
apply(from = "gradle/release-task.gradle.kts")

// == Root orchestration tasks ==

val doAfterEvaluate = ArrayList<(Project) -> Unit>()
tasks {
    register("startServer") {
        dependsOn(":server:run")
        group = "application"
    }

    register<Copy>("doc") {
        dependsOn(documentedProjects.map { ":$it:doc" })
        group = "documentation"
        description = "Collects Javadoc output for documented projects into ${bundleDir.relativeTo(projectDir)}/doc"
        into(bundleDir.resolve("doc"))
        val sdkJavadoc = project(":sdk").tasks.named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationJavadoc")
        val pluginJavadoc = project(":plugin$year").tasks.named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationJavadoc")
        from(sdkJavadoc.flatMap { it.outputDirectory }) { into("sdk") }
        from(pluginJavadoc.flatMap { it.outputDirectory }) { into("plugin-$gameName") }
    }

    register<Copy>("docHtml") {
        dependsOn(documentedProjects.map { ":$it:docHtml" })
        group = "documentation"
        description = "Collects Dokka HTML output for documented projects into ${bundleDir.relativeTo(projectDir)}/doc-html"
        into(bundleDir.resolve("doc-html"))
        val sdkHtml = project(":sdk").tasks.named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationHtml")
        val pluginHtml = project(":plugin$year").tasks.named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationHtml")
        from(sdkHtml.flatMap { it.outputDirectory }) { into("sdk") }
        from(pluginHtml.flatMap { it.outputDirectory }) { into("plugin-$gameName") }
    }

    register("bundle") {
        dependsOn("doc")
        dependOnSubprojects()
        group = "distribution"
        description = "Zips everything up for release into ${bundleDir.relativeTo(projectDir)}"
        outputs.dir(bundleDir)
    }

    clean {
        dependOnSubprojects()
    }
    test {
        dependOnSubprojects()
    }
    check {
        dependOnSubprojects()
    }
    build {
        dependsOn("bundle")
    }

    register("verifyDocs") {
        group = "documentation"
        description = "Verifies generated docs omit Companion classes and include Java+Kotlin SDK docs."
        dependsOn("doc", "docHtml")
        doLast {
            val forbidden = Regex("\\bCompanion\\b")
            val offending = mutableListOf<String>()
            documentedProjects.map { project(":$it") }.forEach { target ->
                val javadocDir = target.tasks.named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationJavadoc")
                    .get().outputDirectory.get().asFile
                val htmlDir = target.tasks.named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationHtml")
                    .get().outputDirectory.get().asFile
                listOf(javadocDir, htmlDir).forEach { docDir ->
                    if (!docDir.exists()) return@forEach
                    docDir.walkTopDown()
                        .filter { it.isFile && (it.extension == "html" || it.extension == "js" || it.name == "package-list" || it.name == "element-list") }
                        .forEach { file ->
                            val text = runCatching { file.readText() }.getOrNull() ?: return@forEach
                            if (forbidden.containsMatchIn(text)) offending.add(file.relativeTo(rootProject.projectDir).path)
                        }
                }
            }
            if (offending.isNotEmpty()) {
                throw GradleException("Companion entries found in docs: ${offending.joinToString(", ")}")
            }

            val sdkJavadocDir = project(":sdk").tasks
                .named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationJavadoc")
                .get().outputDirectory.get().asFile
            val javaDocPage = sdkJavadocDir.resolve("sc/networking/clients/IClient.html")
            val kotlinDocPage = sdkJavadocDir.resolve("sc/protocol/room/WelcomeMessage.html")
            val missing = buildList {
                if (!javaDocPage.exists()) add(javaDocPage.relativeTo(rootProject.projectDir).path)
                if (!kotlinDocPage.exists()) add(kotlinDocPage.relativeTo(rootProject.projectDir).path)
            }
            if (missing.isNotEmpty()) {
                throw GradleException("SDK javadoc is missing expected Java/Kotlin pages: ${missing.joinToString(", ")}")
            }
            if (!javaDocPage.readText().contains("Client interface to send packages to the server.")) {
                throw GradleException("SDK javadoc is missing JavaDoc content for sc.networking.clients.IClient.")
            }
            if (!kotlinDocPage.readText().contains("Nachricht, die zu Beginn eines Spiels")) {
                throw GradleException("SDK javadoc is missing KDoc content for sc.protocol.room.WelcomeMessage.")
            }
            val allClassesPage = sdkJavadocDir.resolve("allclasses.html")
            if (!allClassesPage.exists()) {
                throw GradleException("SDK javadoc is missing allclasses index page.")
            }
            val allClassesText = allClassesPage.readText()
            if (!allClassesText.contains("sc/networking/clients/IClient.html")) {
                throw GradleException("SDK javadoc index is missing Java type link for sc.networking.clients.IClient.")
            }
            if (!allClassesText.contains("sc/protocol/room/WelcomeMessage.html")) {
                throw GradleException("SDK javadoc index is missing Kotlin type link for sc.protocol.room.WelcomeMessage.")
            }
        }
    }
}

// == Cross-project JVM conventions ==

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "kotlin")
    apply(plugin = "com.github.ben-manes.versions")
    apply(plugin = "se.patrikerdes.use-latest-versions")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaToolchainVersion))
        }
    }
    kotlin {
        jvmToolchain(javaToolchainVersion)
    }

    if (name != "sdk" && name != "test-config") {
        dependencies {
            testImplementation(project(":test-config"))
        }
    }

    tasks {
        test {
            useJUnitPlatform()
        }

        withType<JavaCompile> {
            sourceCompatibility = javaTargetVersion.toString()
            targetCompatibility = javaTargetVersion.toString()
        }

        withType<KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(kotlinJvmTarget)
                freeCompilerArgs.add("-Xjvm-default=all")
            }
        }
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.wso2.org/nexus/content/groups/wso2-public/")
    }

    if (name in documentedProjects) {
        apply(plugin = "maven-publish")
        apply(plugin = "org.jetbrains.dokka")
        apply(plugin = "org.jetbrains.dokka-javadoc")

        publishing {
            publications {
                create<MavenPublication>(name) {
                    from(components["java"])
                    version = rootProject.version.toString()
                }
            }
        }

        java {
            withSourcesJar()
            withJavadocJar()
        }

        dokka {
            dokkaPublications.configureEach {
                moduleName.set("Software-Challenge ${project.name} \"$gameName\"")
                moduleVersion.set(rootProject.version.toString())
            }
            dokkaSourceSets.configureEach {
                reportUndocumented.set(false)
                suppressGeneratedFiles.set(true)
                documentedVisibilities.set(setOf(VisibilityModifier.Public))
                jdkVersion.set(javaTargetVersion.majorVersion.toInt())
            }
        }

        tasks {
            val javadocTask = named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationJavadoc")
            val htmlTask = named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationHtml")
            named<Javadoc>("javadoc") { enabled = false }

            val inlineHtmlNav by registering {
                group = "documentation"
                description = "Inlines navigation.html into Dokka HTML to avoid empty sidebars."
                dependsOn(htmlTask)
                doLast {
                    val docDir = htmlTask.get().outputDirectory.get().asFile
                    val navFile = docDir.resolve("navigation.html")
                    if (!navFile.exists()) return@doLast
                    val navHtml = navFile.readText()
                    val marker = "<div class=\"sidebar--inner\" id=\"sideMenu\"></div>"
                    val linkFixScript = """
                        <script>
                        (function () {
                          var path = window.pathToRoot || "";
                          document.querySelectorAll('.toc--link').forEach(function (tocLink) {
                            var href = tocLink.getAttribute('href');
                            if (!href) return;
                            if (!href.startsWith(path)) {
                              tocLink.setAttribute('href', path + href);
                            }
                          });
                          document.querySelectorAll('.toc--skip-link').forEach(function (skipLink) {
                            skipLink.setAttribute('href', '#main');
                          });
                        })();
                        </script>
                    """.trimIndent()
                    docDir.walkTopDown()
                        .filter { it.isFile && it.extension == "html" }
                        .forEach { file ->
                            var updated = file.readText()
                            if (updated.contains(marker)) {
                                updated = updated.replace(marker, "<div class=\"sidebar--inner\" id=\"sideMenu\">$navHtml</div>")
                            }
                            if (!updated.contains("dokka-inline-nav-fix")) {
                                val taggedScript = linkFixScript.replace("<script>", "<script id=\"dokka-inline-nav-fix\">")
                                updated = updated.replace("</body>", "$taggedScript</body>")
                            }
                            file.writeText(updated)
                        }
                }
            }

            val sanitizeJavadoc by registering {
                group = "documentation"
                description = "Removes private field rows from Dokka Javadoc HTML output."
                dependsOn(javadocTask)
                doLast {
                    val docDir = javadocTask.get().outputDirectory.get().asFile
                    if (!docDir.exists()) return@doLast
                    val rowRegex = Regex("(?s)<tr[^>]*>\\s*<td class=\\\"colFirst\\\"><code>private.*?</code>.*?</tr>")
                    fun stripEmptySection(html: String, anchorId: String): String {
                        val sectionRegex = Regex("(?s)<li class=\\\"blockList\\\">\\s*<a id=\\\"$anchorId\\\">.*?</li>")
                        return sectionRegex.replace(html) { matchResult ->
                            val block = matchResult.value
                            if (block.contains("rowColor") || block.contains("altColor")) block else ""
                        }
                    }
                    docDir.walkTopDown()
                        .filter { it.isFile && it.extension == "html" }
                        .forEach { file ->
                            val original = file.readText()
                            var sanitized = original.replace(rowRegex, "")
                            sanitized = stripEmptySection(sanitized, "field.summary")
                            sanitized = stripEmptySection(sanitized, "nested.class.summary")
                            if (sanitized != original) file.writeText(sanitized)
                        }
                }
            }

            register("doc") {
                group = "documentation"
                dependsOn(sanitizeJavadoc)
            }
            register("docHtml") {
                group = "documentation"
                dependsOn(inlineHtmlNav)
            }
            named<Jar>("javadocJar") {
                dependsOn("cleanJavadoc")
                dependsOn(sanitizeJavadoc)
                from(javadocTask.flatMap { it.outputDirectory })
            }
        }
    }

    afterEvaluate {
        doAfterEvaluate.forEach { action -> action(this) }
        tasks {
            forEach { if (!it.name.endsWith("clean", true)) it.mustRunAfter(clean.get()) }
            test { testLogging { showStandardStreams = project.properties["verbose"] != null } }
            withType<Jar> {
                if (plugins.hasPlugin(ApplicationPlugin::class)) {
                    manifest.attributes(
                        "Main-Class" to project.extensions.getByType<JavaApplication>().mainClass.get(),
                        "Add-Opens" to arrayOf(
                            // Expose list internals for xstream conversion: https://github.com/x-stream/xstream/issues/253
                            "java.base/java.util"
                        ).joinToString(" ")
                    )
                }
            }
        }
    }
}

fun Task.dependOnSubprojects() {
    if (project == rootProject) {
        doAfterEvaluate.add {
            if (it != rootProject && it.name != "plugin") {
                dependsOn(it.tasks.findByName(name) ?: return@add)
            }
        }
    }
}
