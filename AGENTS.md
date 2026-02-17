# Repository Guidelines

## Project Specifics
### Structure & Modules
This repository is a multi-module Gradle project for the Software Challenge backend.

- `sdk/`: shared protocol, framework, networking, and player/server API classes.
- `server/`: game server application (`sc.server.Application`).
- `player/`: default player template and packaging tasks.
- `games/`: yearly plugins mapped as modules (`plugin2023`..`plugin2026`).
- `helpers/test-client` and `helpers/test-config`: integration tooling and shared test setup.
- `gradle/` and `gradlew`: build logic and wrapper.

### Build, Test & Development Commands
Run from repo root:

- `./gradlew build`: compile, test, and create distribution bundles.
- `./gradlew check`: run verification tasks across modules.
- `./gradlew test`: run unit tests.
- `./gradlew integrationTest`: run end-to-end game/test-client checks.
- `./gradlew :server:run`: start server from source.
- `./gradlew :player:run`: start default player.
- `./gradlew bundle`: assemble release ZIP artifacts.

For module-scoped work, use qualified tasks (example: `./gradlew :plugin2026:test`).

### Coding Style & Naming
Primary languages are Kotlin and Java, but all new code must be written in Kotlin.

- Follow existing module style and keep changes consistent with surrounding code.
- Use clear domain names (`GameState`, `Move`, `...Request`, `...Response`).
- Keep package naming aligned with module/year conventions (example: `sc.plugin2026`).
- Prefer descriptive test names ending in `Test` (example: `GameRuleLogicTest.kt`).
- No repository-wide formatter is enforced in Gradle; run IDE reformat with project defaults.

## General Workflow Policies
### Testing & Verification
- Tests are mainly Kotest (`WordSpec` preferred, `FunSpec` for algorithmic cases) on JUnit 5; some legacy JUnit 5 tests remain.
- Place new tests under `src/test/kotlin`.
- Name test files `*Test.kt`.
- Write tests before each change except minor cosmetic-only changes.
- Verify tests after each change.
- For server-player interaction or protocol changes, run `./gradlew integrationTest`.
- Before adjusting failing tests, evaluate whether failures indicate regressions versus intentional behavior changes.
- Prefer behavior/outcome tests over implementation-detail tests.

### Commit & PR Discipline
- Use commit messages in format `type(scope): summary` (example: `fix(plugin26): avoid transient hash checks for observers`).
- Enable local hooks: `git config core.hooksPath .dev/githooks`.
- Prefer branch names like `feat/server/login` or `chore/gradle/release-fix`.
- Keep PRs focused, link related issues, and describe behavior impact plus test coverage.
- Use rebase merge when commits are independently valid; otherwise squash merge with PR title matching final commit message.
- Make atomic commits; if a commit only fixes the previous local commit, squash before handoff.

### Changelog & Refactoring
- Keep `CHANGELOG.md` updated for notable user-visible behavior changes.
- Keep an `Unreleased` section at top; move entries into versioned section on release.
- Use semantic version sections and ISO dates (`YYYY-MM-DD`).
- After major milestones, run a focused refactor pass for duplication, consistency, and complexity.
- Prefer separate `refactor:` commits for cleanup when functionality does not depend on refactor changes.

## Agent-Specific Instructions
### Machine-Readable Policy
```yaml
agent_policy:
  source_of_truth:
    - CONTRIBUTING.md
    - GUIDELINES.md
    - settings.gradle.kts
  setup:
    git_hooks: "git config core.hooksPath .dev/githooks"
  project:
    build_system: gradle
    new_code_language: kotlin
    modules:
      - sdk
      - server
      - player
      - plugin2023
      - plugin2024
      - plugin2025
      - plugin2026
      - test-client
      - test-config
  verification:
    default: "./gradlew test"
    interaction_or_protocol: "./gradlew integrationTest"
    test_file_patterns:
      - "src/test/kotlin/**/*Test.kt"
  commit:
    format: "type(scope): summary"
    scopes_file: ".dev/scopes.txt"
  merge:
    rebase_if_independent: true
    squash_if_experimental: true
  constraints:
    - "Do not create new Java source files."
    - "Do not create new Java test files."
```

### Prompt Effort Modes
```yaml
prompt_effort:
  quick:
    - "Minimize testing/refactoring; run focused checks only."
    - "Defer broader cleanup unless explicitly requested."
  long:
    - "Run thorough workflow with broader verification and edge-case checks."
    - "Include refactor/debt review where appropriate."
  default:
    - "Choose balanced effort based on task complexity and risk."
```

### Special Commands
```yaml
special_commands:
  squash:
    - "Inspect recent commits and propose sensible squashes for repetitive fixups."
    - "Do not squash unrelated functional changes."
    - "Rewrite only unpushed local history unless explicitly instructed."
  push:
    - "List unpushed commits: git log origin/<branch>..HEAD --oneline"
    - "Provide one high-level summary across unpushed commits."
    - "Update changelog/version/tag only when explicitly requested."
    - "Ask explicit confirmation before pushing branch or tags."
```

### Plans & Artifacts
```yaml
planning:
  when_user_requests_plan:
    - "Write plan files to plans/ at repo root."
    - "Use descriptive kebab-case filenames."
    - "Do not commit plan files unless explicitly requested."
  after_implementation:
    - "Create a cleanup plan in plans/ summarizing post-implementation refactors or deferred cleanup."
```
