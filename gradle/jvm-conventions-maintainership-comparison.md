# JVM Conventions: Groovy vs Kotlin DSL

This document compares the existing `gradle/jvm-conventions.gradle` with the side-by-side Kotlin DSL alternative `gradle/jvm-conventions.gradle.kts`.

## Side-by-side Summary

| Area | Groovy script (`.gradle`) | Kotlin DSL script (`.gradle.kts`) | Maintainer impact |
| --- | --- | --- | --- |
| Authoring speed for dynamic Gradle APIs | Faster | Slower | Groovy wins for quick, reflective task/property wiring. |
| Type safety and refactorability | Low | High | Kotlin wins for safer API usage and IDE-assisted edits. |
| Kotlin task configuration style | Dynamic property checks by name | Typed `KotlinJvmCompile` API | Kotlin wins for future edits and discoverability. |
| Failure mode for API changes | More runtime surprises | More compile-time feedback | Kotlin wins for long-term stability. |
| Compatibility with mixed plugin classpaths | Very tolerant | Requires proper imports/classpath | Groovy wins when classpath composition is uncertain. |
| Readability for Kotlin-heavy contributors | Moderate | High | Kotlin generally wins in this repository. |
| Initial migration effort | None (already in place) | Moderate | Groovy wins short term, Kotlin wins after migration. |

## Practical Complexity in This Repository

1. Current Groovy file is compact and intentionally dynamic, which keeps it resilient to plugin class references but pushes checks to runtime.
2. Kotlin alternative is more explicit and stricter, especially for compiler options, which lowers future regression risk when editing the script.
3. Day-2 maintenance cost is likely lower with Kotlin DSL because this repository is already Kotlin-first and root build logic is in `.kts`.
4. If frequent Gradle/Kotlin plugin upgrades are expected, Kotlin DSL offers better signal through compilation errors rather than task-time surprises.

## Recommendation

- Keep Groovy script if priority is minimal immediate change and maximum tolerance for dynamic APIs.
- Switch to Kotlin DSL if priority is maintainability and safer refactors by contributors working mostly in Kotlin.
- A low-risk path is to keep both files temporarily, then switch `apply(from = ...)` after validating with `./gradlew test` and `./gradlew integrationTest`.
