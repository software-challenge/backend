# Changelog
All notable changes to this project will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0),

The `x.y.z` version is tracked in [gradle.properties](./gradle.properties) to enable programmatic updating via the Gradle release task.
The version should always be in sync with the [GUI](https://github.com/CAU-Kiel-Tech-Inf/gui) and have a tag in both repositories.

- The major version `x` corresponds to the year of the contest and thus only changes once a year
- `y` is bumped for any major updates or backwards-incompatible changes

## [Unreleased]

## [21.3.1](https://github.com/CAU-Kiel-Tech-Inf/backend/commits/21.3.1)
- Fix GameState clone, hashCode and equals to include [undeployedPieceShapes](https://github.com/CAU-Kiel-Tech-Inf/backend/commit/010f077747d4bba0a9397b536da7f48d88bf1a74) and [validColors](https://github.com/CAU-Kiel-Tech-Inf/backend/commit/cbda82827932cd288576ba0320c03615cba9dab7)
- Remove superfluous `class` attributes from GameState XML
- Extend GameState XML tests

## [21.3.0](https://github.com/CAU-Kiel-Tech-Inf/backend/commits/21.3.0) - 2021-01-29
### Fixed
- Send final Gamestate to listeners when a game ends ([#364](https://github.com/CAU-Kiel-Tech-Inf/backend/pull/364))
- Remove extra field from Move/Piece XML ([23589a153](https://github.com/CAU-Kiel-Tech-Inf/backend/commit/23589a153e8cd3c5b1b3257ff35f66ebbb8d3012))
- Player dependency declarations ([#373](https://github.com/CAU-Kiel-Tech-Inf/backend/pull/373))
  
### Added
- Implement cloning for GameState & Board ([#356](https://github.com/CAU-Kiel-Tech-Inf/backend/pull/356))
- Add README for player ([#373](https://github.com/CAU-Kiel-Tech-Inf/backend/pull/373))
- Create CONTRIBUTING & GUIDELINES ([#360](https://github.com/CAU-Kiel-Tech-Inf/backend/pull/360))
- Modularize XStream initialization using ServiceLoader ([#352](https://github.com/CAU-Kiel-Tech-Inf/backend/pull/352))
  
### Changed
- Improve logback config ([#371](https://github.com/CAU-Kiel-Tech-Inf/backend/pull/371))
- Improve Gradle configuration & TestClient build ([#368](https://github.com/CAU-Kiel-Tech-Inf/backend/pull/368))
- TestClient: Make it work ([#372](https://github.com/CAU-Kiel-Tech-Inf/backend/pull/372)) & load current Game id from classpath ([#367](https://github.com/CAU-Kiel-Tech-Inf/backend/pull/367))

## [21.2.1](https://github.com/CAU-Kiel-Tech-Inf/backend/commits/21.2.1) - 2020-12-18
- Improve internal build logic ([#343](https://github.com/CAU-Kiel-Tech-Inf/backend/pull/343))
- Improve & translate toString messages
- Use MoveMistakes to construct InvalidMoveExceptions ([#321](https://github.com/CAU-Kiel-Tech-Inf/backend/pull/321))

## [21.2.0](https://github.com/CAU-Kiel-Tech-Inf/backend/commits/21.2.0) - 2020-12-14
- converted lots of classes to Kotlin
- internal Protocol updates & game flow adjustments
- updated many tests

## 21 - Game Blokus

## 20 - Game Hive