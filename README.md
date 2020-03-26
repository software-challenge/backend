# Software-Challenge [![Build Status](https://travis-ci.com/CAU-Kiel-Tech-Inf/socha.svg?branch=master)](https://travis-ci.com/CAU-Kiel-Tech-Inf/socha)

Das offizielle Repository der [Software-Challenge](https://www.software-challenge.de/), welches aus Server, Client und Spiel-Plugins besteht.

## Struktur

| Ordner | Beschreibung |
| ------ | ------------ |
| helpers | Zusätzliche Tools (aktuell nur der TestClient) |
| player | Simpleclient dieses Jahres |
| plugin | Plugin dieses Jahres |
| server | Spielserver |
| socha-sdk | Projektübergreifend verwendete Klassen |

## Collaboration

Wir nutzen den type-scope commit message Syntax nach [Karma Runner](http://karma-runner.github.io/4.0/dev/git-commit-msg.html), wobei die verfügbaren scopes in [.dev/scopes.txt](.dev/scopes.txt) definiert werden.  
Bitte führe nach dem klonen des repositories einmal folgendes im Terminal aus, damit die entsprechenden git hooks aktiv werden:  
 `git config core.hooksPath .dev/githooks`

Um bei den Branches die Übersicht zu behalten ist es hilfreich, diese ebenfalls ähnlich dieser Konvention zu benennen, also könnte ein Branch mit einem Release-fix für gradle `fix/gradle-release` heißen und ein Branch, der ein neues login-feature zum server hinzufügt, `feat/server-login`.  
Branches werden normalerweise beim mergen gesquashed, außer die einzelnen commits des branches haben jeweils eine alleinstehende Aussagekraft.

Detaillierte Informationen zu unserem Kollaborations-stil findet ihr in der [Kull Convention](xerus2000.github.io/kull).

## Build

Als Build-Tool wird [Gradle](https://gradle.org/) verwendet.

Das gesamte Projekt kann sofort nach dem checkout per `./gradlew build`
gebaut werden, es ist keine Installation von Programmen nötig.

Die wichtigsten Tasks:

| Task | Beschreibung
| ------ | ------------
| `build` | baut alles, deployt und testet
| `test` | führt tests aus
| `deploy` | erstellt hochladbare ZIP-Pakete
| `integrationTest` | testet ein komplettes Spiel sowie den TestClient
| `startServer` oder `:server:run` | führt den Server direkt vom Quellcode aus
| `:server:startProduction` | startet den gepackten Server
| `:player:run` | startet den SimpleClient direkt vom Sourcecode
| `:player:shadowJar` | baut eine jar des SimpleClient
| `:test-client:run` | startet den Testclient

Tasks der Subprojekte können in zwei Wegen aufgerufen werden:
`./gradlew :server:run` führt die Task "run" des Subprojektes "server" aus.
Das gleiche kann auch erreicht werden, in dem man in das server-Verzeichnis
wechselt und dort `./gradlew run` ausführt.

Wenn notwendig, können bei der Ausführung eines Subprojektes via `run`
per `-Dargs="Argument1 Argument2"`Argumente mitgegeben werden.

Der TestClient kann z.B. mit dem Befehl `./gradlew :test-client:run -Dargs="--player1 ../../player/build/libs/defaultplayer.jar --player2 ../../player/build/libs/defaultplayer.jar --tests 3"`

### Arbeiten mit Intellij IDEA

Zuerst sollte sichergestellt werden, dass die neuste Version von
Intellij IDEA verwendet wird, da es ansonsten Probleme mit Kotlin
geben kann.

In Intellij kann man das Projekt bequem von Gradle importieren,
wodurch alle Module und Bibliotheken automatisch geladen werden.
Dazu sind folgende Schritte notwendig:

- Projekt klonen `git clone git@github.com:CAU-Kiel-Tech-Inf/socha.git`
- In IDEA auf "File" > "New" > "Project from existing sources" > socha Verzeichnis auswählen
  - Import project from external model, Gradle auswählen
  - Im folgenden Fenster:
    - "Use auto-import" ankreuzen
    - bei "Gradle JVM" JDK 8 auswählen, wenn sie nicht schon ausgewählt ist
    - "Finish" drücken
- Warten, bis das Gradle build fertig ist
- Einmal im Terminal `git checkout .idea` ausführen, um sich die codeStyles zurückzuholen

Nun können Gradle tasks auch direkt in IDEA vom Gradle Tool Window
(befindet sich normalerweise in der rechten Andockleiste) ausgeführt werden.
