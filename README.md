# Software-Challenge [![Build Status](https://travis-ci.com/CAU-Kiel-Tech-Inf/backend.svg?branch=master)](https://travis-ci.com/CAU-Kiel-Tech-Inf/backend)

Dies ist das offizielle Repository der [Software-Challenge](https://www.software-challenge.de), ein Programmierwettbewerb für Schüler.
Ziel hierbei ist, für ein jährlich wechselndes Spiel eine künstliche Intelligenz zu entwickeln, die den Gegenspieler besiegt.
Das Repository besteht aus Server, Client und Spiel-Plugins.

## Struktur

| Ordner | Beschreibung |
| ------ | ------------ |
| helpers | Zusätzliche Tools (aktuell nur der TestClient) |
| player | SimpleClient dieses Jahres |
| plugin | Plugin dieses Jahres |
| server | Spielserver |
| socha-sdk | Projektübergreifend verwendete Klassen |

## Collaboration

Unsere Commit-Messages folgen dem Muster `type(scope): summary` (siehe [Karma Runner Konvention](http://karma-runner.github.io/latest/dev/git-commit-msg.html)), wobei die verfügbaren Scopes in [.dev/scopes.txt](.dev/scopes.txt) definiert werden. Bitte führe nach dem Klonen des Repository's einmal Folgendes im Terminal aus, damit die entsprechenden Git-Hooks aktiv werden:  

    git config core.hooksPath .dev/githooks

Um bei den Branches die Übersicht zu behalten, sollten diese ebenfalls nach der Konvention benannt werden - z.B. könnte ein Branch mit einem Release-Fix für Gradle `fix/gradle-release` heißen und ein Branch, der ein neues Login-Feature zum Server hinzufügt, `feat/server-login`.  
Branches werden normalerweise beim Mergen zu einem einzelnen Commit zusammengefügt (Squash-Merge), es sei denn, die einzelnen Commits des Branches haben jeweils eine alleinstehende Aussagekraft.

Detaillierte Informationen zu unserem Kollaborations-Stil findet ihr in der [Kull Konvention](https://xerus2000.github.io/kull).

## Build

Als Build-Tool wird [Gradle](https://gradle.org) verwendet. Das gesamte Projekt kann sofort nach dem Checkout per `./gradlew build` gebaut werden, es ist keine Installation von Programmen nötig.

Die wichtigsten Tasks:

| Task | Beschreibung
| ------ | ------------
| `build` | Baut alles, deployt und testet
| `deploy` | Erstellt hochladbare ZIP-Pakete
| `check` | Führt alle Tests aus
| `test` | Führt Unittests aus
| `integrationTest` | Testet ein komplettes Spiel sowie den TestClient
| `startServer` oder `:server:run` | Führt den Server direkt vom Quellcode aus
| `:server:startProduction` | Startet den gepackten Server
| `:player:run` | Startet den SimpleClient direkt vom Sourcecode
| `:player:shadowJar` | Baut den SimpleClient als fat-jar
| `:test-client:run` | Startet den TestClient

### Unterprojekte

Tasks in Unterprojekten können über zwei Wege aufgerufen werden:  
`./gradlew :server:run` führt den Task "run" des Unterprojektes "server" aus.
Alternativ kann man in das Server-Verzeichnis wechseln und dort `./gradlew run` ausführen.

Bei der Ausführung eines Unterprojekts via `run` können per `-Dargs="Argument1 Argument2"` zusätzlich Argumente mitgegeben werden. Zum Beispiel kann der TestClient mit folgendem Befehl direkt aus dem Sourcecode getestet werden:

    ./gradlew :test-client:run -Dargs="--player1 ../../player/build/libs/defaultplayer.jar --player2 ../../player/build/libs/defaultplayer.jar --tests 3"

### Tests

Unsere Unittests nutzen das [Kotest-Framework](https://kotest.io) mit [JUnit](https://junit.org) im Hintergrund.

Dabei setzen wir auf die [ShouldSpec](https://kotest.io/styles/#should-spec) und ggf. [BehaviorSpec](https://kotest.io/styles/#behavior-spec).
Bisherige Tests nutzen die StringSpec, welche jedoch wegen fehlendem nesting auf Dauer zu unübersichtlich ist.

### Arbeiten mit IntelliJ IDEA

Zuerst sollte sichergestellt werden, dass die neuste Version von IntelliJ IDEA verwendet wird, da es ansonsten Probleme mit Kotlin geben kann.

In IntelliJ kann man das Projekt bequem von Gradle importieren, wodurch alle Module und Bibliotheken automatisch geladen werden.
Dazu sind folgende Schritte notwendig:

- Projekt klonen:
  ```sh
  git clone git@github.com:CAU-Kiel-Tech-Inf/backend.git --recurse-submodules --shallow-submodules
  ```
- In IntelliJ: "File" > "New" > "Project from existing sources" > Projektverzeichnis auswählen
  - "Import project from external model" > "Gradle" auswählen
  - Im folgenden Fenster:
    - "Use auto-import" ankreuzen
    - bei "Gradle JVM" JDK 8 auswählen, wenn sie nicht schon ausgewählt ist
    - "Finish" drücken
- Warten, bis das Gradle-Build fertig ist

Nun können Gradle-Tasks auch direkt in IntelliJ vom Gradle-Tool-Fenster ausgeführt werden; dieses befindet sich normalerweise in der rechten Andockleiste.