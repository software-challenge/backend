# Changelog
All notable changes to this project are documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0),

The `x.y.z` version is tracked in [gradle.properties](./gradle.properties) to enable programmatic updating via the Gradle `release` task.
The version should always be in sync with the [GUI](https://github.com/software-challenge/gui) and have a tag in both repositories.

- The major version `x` corresponds to the year of the contest and thus only changes once a year
- `y` is bumped for any major updates or backwards-incompatible changes.  
  A `y` version of 0 marks the beta of the current year
  and likely contains breaking changes between patches.

### 24.2.0 - 2023-10-XX
- Disqualify oversped ship
- Allow other player to move on when one is disqualified
- Grey out winning ship so second player can follow

### 24.1.2 Stable Release - 2023-09-20

### 24.1.1 Helper Adjustments - 2023-09-14
- Fully rename simpleclient to playertemplate
- Fix segment bounds special cases

### 24.1.0 Add New Helpers - 2023-09-11

### 24.0.8 All The Moves - 2023-09-04
- moveIterator should now generate all possible Moves
- Reduce usage of magic numbers
- Fix some test cases and update kotest
- Fix oversight in SimplePlayer template

### 24.0.7 Game Procedure - 2023-08-29
- Make SimplePlayer template more convenient
- Clarify game over edge case conditions

### 24.0.6 Refine Adjuncts - 2023-08-24
- Properly handle consecutive advances
- Fix ScoreDefinition

### 24.0.5 Eliminating Oversights - 2023-08-23
- Revamp calculation structure for available Moves
- Add some helpers and documentation
- Fix revealing of Segments including nextDirection
- Fix passing on of nextDirection in Board

### 24.0.3 Rule Fixes - 2023-08-12
- Properly deep copy board
- Allow picking up passengers at speed 2 in current
- Skip player turn if ship is immovable
- Extend tests

### 24.0.0 New Game with adjusted rules - 2023-08-10
- Fix Orientation of Coordinates and Directions for Cartesian, DoubledHex and Cube
- Implement basic version of Mississippi Queen - XML and Rules are subject to change!

## 2024 Game Mississippi Queen - 2023-08

### 23.0.3 Final Fixes
- Fix Deep Cloning of GameState
- Update Board generation counts to prevent missing ice floes

### [23.0.2](https://github.com/software-challenge/backend/commits/23.0.2) New Board generation - 2022-08-21
- Update Board generation: 
  The Board is now mirrored with holes,
  with more holes in the middle and more fish at the edge.
- Improve automated checks

### [23.0.1](https://github.com/software-challenge/backend/commits/23.0.1) Proper Penguins - 2022-08-06
- Proper rule implementation for Penguins with some helpers
- Improve XML

### [23.0.0](https://github.com/software-challenge/backend/commits/23.0.0) Rough Penguins - 2022-07-28
- Generify some plugin functionality to the SDK 
  (RectangularBoard, Team.color)
- Initial version of game "Hey, Danke für den Fisch" (Codename Penguins)
  with simplified game rules (can only move to adjacent fields)
- Switch from unavailable maven repo for jargs

## 2023 Game Hey, Danke für den Fisch (Penguins) - 2022-07

### [22.1.0](https://github.com/software-challenge/backend/commits/22.1.0) Polish - 2021-11-15
- Ensure compatibility beyond Java 16
- Fix game ending a turn too late on round limit (7b096f105)
- Fix irritating error messages at game end
- Release TestClient

### [22.0.3](https://github.com/software-challenge/backend/commits/22.0.3) - 2021-07-26
- Fix simpleclient setup
- Only end game after full rounds

### [22.0.2](https://github.com/software-challenge/backend/commits/22.0.2) Protocol Revamp - 2021-07-16
- Update documentation links
- Various improvements around testing
#### Protocol
- Reworked GameResult XML (add player information, include winner as team)
- Simpler Player XML (team as attribute)
- Join without specifying a gameType
- Removed test mode - add up the GameResults instead ([#385](https://github.com/software-challenge/backend/pull/385))
- Do not send repeated MoveRequests when pausing/unpausing
- Stop terminating client connections upon protocol issues ([6c6d6fa51](https://github.com/software-challenge/backend/commit/6c6d6fa51af71eea3914303cb886bd8b78be53a0))

### [22.0.1](https://github.com/software-challenge/backend/commits/22.0.1) Adjust Ostseeschach plugin - 2021-06-25
- Implemented proper winner calculation
- Added some utility methods
- Adjusted XML

### [22.0.0](https://github.com/software-challenge/backend/commits/22.0.0) - 2021-06-11
- Restructured folders to follow JVM standards
- Simplified interface for player development

## 2022 Game Ostseeschach - 2021-06

### [21.4.0](https://github.com/software-challenge/backend/commits/21.4.0) - 2021-05-28
- Enable preparing games without reservations
- Implement generic replay saving & loading
- Create an AdminClient to interface more conveniently with the server
- Distinguish RoomMessages from ProtocolPackets
- Send GamePaused message after a Game has been paused

### [21.3.3](https://github.com/software-challenge/backend/commits/21.3.3) - 2021-03-01
- Game: Refactor turn advancing logic ([#391](https://github.com/software-challenge/backend/pull/391))
- Networking: Improve Exception handling within games and rooms ([#383](https://github.com/software-challenge/backend/pull/383))

### [21.3.2](https://github.com/software-challenge/backend/commits/21.3.2) - 2021-02-12
#### Fixed
- GameState: Alignment of round number with turn number (49676b64c)
- TestClient: Prevent a race-condition that could occur when getting the results of the first game (4f33fc01f)

### [21.3.1](https://github.com/software-challenge/backend/commits/21.3.1) - 2021-02-11
#### Fixed
- Fix GameState clone, hashCode and equals to include [undeployedPieceShapes](https://github.com/software-challenge/backend/commit/010f077747d4bba0a9397b536da7f48d88bf1a74) and [validColors](https://github.com/software-challenge/backend/commit/cbda82827932cd288576ba0320c03615cba9dab7)
- Remove superfluous `class` attributes from GameState XML
- Synchronize XML packet sending to prevent messages from interleaving ([219466c0a](https://github.com/software-challenge/backend/commit/219466c0a))
#### Infrastructure  
- Extend GameState XML (#381) and Network tests (#363)
- Setup GitHub Actions (#384)

### [21.3.0](https://github.com/software-challenge/backend/commits/21.3.0) - 2021-01-29
#### Fixed
- Send final Gamestate to listeners when a game ends ([#364](https://github.com/software-challenge/backend/pull/364))
- Remove extra field from Move/Piece XML ([23589a153](https://github.com/software-challenge/backend/commit/23589a153e8cd3c5b1b3257ff35f66ebbb8d3012))
- Player dependency declarations ([#373](https://github.com/software-challenge/backend/pull/373))
- TestClient: Make it work ([#372](https://github.com/software-challenge/backend/pull/372)) & load current Game id from classpath ([#367](https://github.com/software-challenge/backend/pull/367))
  
#### Added
- Implement cloning for GameState & Board ([#356](https://github.com/software-challenge/backend/pull/356))
- Add README for player ([#373](https://github.com/software-challenge/backend/pull/373))
- Create CONTRIBUTING & GUIDELINES ([#360](https://github.com/software-challenge/backend/pull/360))
- Modularize XStream initialization using ServiceLoader ([#352](https://github.com/software-challenge/backend/pull/352))
  
#### Changed
- Improve logback config ([#371](https://github.com/software-challenge/backend/pull/371))
- Improve Gradle configuration & TestClient build ([#368](https://github.com/software-challenge/backend/pull/368))

### [21.2.1](https://github.com/software-challenge/backend/commits/21.2.1) - 2020-12-18
- Improve internal build logic ([#343](https://github.com/software-challenge/backend/pull/343))
- Improve & translate toString messages
- Use MoveMistakes to construct InvalidMoveExceptions ([#321](https://github.com/software-challenge/backend/pull/321))

### [21.2.0](https://github.com/software-challenge/backend/commits/21.2.0) - 2020-12-14
- converted lots of classes to Kotlin
- internal Protocol updates & game flow adjustments
- updated many tests

## 2021 Game Blokus

## 2020 Game Hive
