# Changelog
All notable changes to this project will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0),

## [Unreleased]

## [21.4.0](https://github.com/CAU-Kiel-Tech-Inf/backend/commits/21.4.0)
- Adjust Gamestate XML (https://github.com/CAU-Kiel-Tech-Inf/backend/pull/374)

## [21.3.0](https://github.com/CAU-Kiel-Tech-Inf/backend/commits/21.3.0) - 2021-01-29
### Fixed
- Send final Gamestate to listeners when a game ends (https://github.com/CAU-Kiel-Tech-Inf/backend/pull/364)
- Remove extra field from Move/Piece XML (23589a153e8cd3c5b1b3257ff35f66ebbb8d3012)
- Player dependency declarations (https://github.com/CAU-Kiel-Tech-Inf/backend/pull/373)
  
## Added
- Implement cloning for GameState & Board (https://github.com/CAU-Kiel-Tech-Inf/backend/pull/356)
- Add README for player (https://github.com/CAU-Kiel-Tech-Inf/backend/pull/373)
- Create CONTRIBUTING & GUIDELINES (https://github.com/CAU-Kiel-Tech-Inf/backend/pull/360)
- Modularize XStream initialization using ServiceLoader (https://github.com/CAU-Kiel-Tech-Inf/backend/pull/352)
  
## Changed
- Improve logback config (https://github.com/CAU-Kiel-Tech-Inf/backend/pull/371)
- Improve gradle configuration & TestClient build (https://github.com/CAU-Kiel-Tech-Inf/backend/pull/368)
- TestClient: Make it work (https://github.com/CAU-Kiel-Tech-Inf/backend/pull/372) & load current Game id from classpath (https://github.com/CAU-Kiel-Tech-Inf/backend/pull/367)

## [21.2.1](https://github.com/CAU-Kiel-Tech-Inf/backend/commits/21.2.1) - 2020-12-18
- improve internal build logic (https://github.com/CAU-Kiel-Tech-Inf/backend/pull/343)
- feat(plugin): improve & translate toString messages
- feat(plugin): use MoveMistakes to construct InvalidMoveExceptions (https://github.com/CAU-Kiel-Tech-Inf/backend/pull/321)

## [21.2.0](https://github.com/CAU-Kiel-Tech-Inf/backend/commits/21.2.0) - 2020-12-14
- converted lots of classes to Kotlin
- internal Protocol updates & game flow adjustments
- updated many tests

## 21 - Game Blokus

## 20 - Game Hive