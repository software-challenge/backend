# Changelog
All notable changes to this project will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0),

The `x.y.z` version is tracked in [gradle.properties](./gradle.properties) to enable programmatic updating via the Gradle `release` task.
The version should always be in sync with the [GUI](https://github.com/software-challenge/gui) and have a tag in both repositories.

- The major version `x` corresponds to the year of the contest and thus only changes once a year
- `y` is bumped for any major updates or backwards-incompatible changes.  
  A `y` version of 0 indicates the beta of the current year.
  
## [22.1.0](https://github.com/software-challenge/backend/commits/22.1.0) - 2021-09

## [22.0.2](https://github.com/software-challenge/backend/commits/22.0.2) - 2021-07
- Update documentation links
- Various improvements in tests
### Protocol
- Reworked GameResult XML (add player information, include winner as team)
- Simplified Player XML (team as attribute)
- Can join without specifying a gameType
- Removed PlayerScoreRequest and TestModeRequest - add up the GameResults instead ([#385](https://github.com/software-challenge/backend/pull/385))

## [22.0.1](https://github.com/software-challenge/backend/commits/22.0.1) - 2021-06-25
### Ostseeschach
- Implemented proper winner calculation
- Added some utility methods
- Adjusted XML

## [22.0.0](https://github.com/software-challenge/backend/commits/22.0.0) - 2021-06-11
- Restructured folders to follow JVM standards
- Simplified interface for player development

## 2022 Game Ostseeschach - 2021-06-11

## [21.4.0](https://github.com/software-challenge/backend/commits/21.4.0) - 2021-05-28
- Enable preparing games without reservations
- Implement generic replay saving & loading
- Create an AdminClient to interface more conveniently with the server
- Distinguish RoomMessages from ProtocolPackets
- Send GamePaused message after a Game has been paused

## [21.3.3](https://github.com/software-challenge/backend/commits/21.3.3) - 2021-03-01
- Game: Refactor turn advancing logic ([#391](https://github.com/software-challenge/backend/pull/391))
- Networking: Improve Exception handling within games and rooms ([#383](https://github.com/software-challenge/backend/pull/383))

## [21.3.2](https://github.com/software-challenge/backend/commits/21.3.2) - 2021-02-12
### Fixed
- GameState: Alignment of round number with turn number (49676b64c)
- TestClient: Prevent a race-condition that could occur when getting the results of the first game (4f33fc01f)

## [21.3.1](https://github.com/software-challenge/backend/commits/21.3.1) - 2021-02-11
### Fixed
- Fix GameState clone, hashCode and equals to include [undeployedPieceShapes](https://github.com/software-challenge/backend/commit/010f077747d4bba0a9397b536da7f48d88bf1a74) and [validColors](https://github.com/software-challenge/backend/commit/cbda82827932cd288576ba0320c03615cba9dab7)
- Remove superfluous `class` attributes from GameState XML
- Synchronize XML packet sending to prevent messages from interleaving ([219466c0a](https://github.com/software-challenge/backend/commit/219466c0a))
### Infrastructure  
- Extend GameState XML (#381) and Network tests (#363)
- Setup GitHub Actions (#384)

## [21.3.0](https://github.com/software-challenge/backend/commits/21.3.0) - 2021-01-29
### Fixed
- Send final Gamestate to listeners when a game ends ([#364](https://github.com/software-challenge/backend/pull/364))
- Remove extra field from Move/Piece XML ([23589a153](https://github.com/software-challenge/backend/commit/23589a153e8cd3c5b1b3257ff35f66ebbb8d3012))
- Player dependency declarations ([#373](https://github.com/software-challenge/backend/pull/373))
- TestClient: Make it work ([#372](https://github.com/software-challenge/backend/pull/372)) & load current Game id from classpath ([#367](https://github.com/software-challenge/backend/pull/367))
  
### Added
- Implement cloning for GameState & Board ([#356](https://github.com/software-challenge/backend/pull/356))
- Add README for player ([#373](https://github.com/software-challenge/backend/pull/373))
- Create CONTRIBUTING & GUIDELINES ([#360](https://github.com/software-challenge/backend/pull/360))
- Modularize XStream initialization using ServiceLoader ([#352](https://github.com/software-challenge/backend/pull/352))
  
### Changed
- Improve logback config ([#371](https://github.com/software-challenge/backend/pull/371))
- Improve Gradle configuration & TestClient build ([#368](https://github.com/software-challenge/backend/pull/368))

## [21.2.1](https://github.com/software-challenge/backend/commits/21.2.1) - 2020-12-18
- Improve internal build logic ([#343](https://github.com/software-challenge/backend/pull/343))
- Improve & translate toString messages
- Use MoveMistakes to construct InvalidMoveExceptions ([#321](https://github.com/software-challenge/backend/pull/321))

## [21.2.0](https://github.com/software-challenge/backend/commits/21.2.0) - 2020-12-14
- converted lots of classes to Kotlin
- internal Protocol updates & game flow adjustments
- updated many tests

## 2021 Game Blokus

## 2020 Game Hive
