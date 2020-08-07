tasks.withType<Test> {
  useJUnitPlatform()
}
dependencies {
    testImplementation(kotlin("script-runtime"))
}