# Software-Challenge [![Build Status](https://travis-ci.com/CAU-Kiel-Tech-Inf/socha.svg?branch=master)](https://travis-ci.com/CAU-Kiel-Tech-Inf/socha)

Dies ist das offizielle Repository der [Software-Challenge](https://www.software-challenge.de/), ein Programmierwettbewerb für Schüler.
Ziel hierbei ist, für ein jährlich wechselndes Spiel eine künstliche Intelligenz zu entwickeln, die den Gegenspieler besiegt.
Das Repository besteht aus Server, Client und Spiel-Plugins.

## Struktur

| Ordner | Beschreibung |
| ------ | ------------ |
| helpers | Zusätzliche Tools (aktuell nur der TestClient) |
| player | Simpleclient dieses Jahres |
| plugin | Plugin dieses Jahres |
| server | Spielserver |
| socha-sdk | Projektübergreifend verwendete Klassen |

## Collaboration

Die Type-Scopes unserer Commit-Messages folgen der [Karma Runner](http://karma-runner.github.io/4.0/dev/git-commit-msg.html) Konvention, wobei die verfügbaren Scopes in [.dev/scopes.txt](.dev/scopes.txt) definiert werden. Bitte führe nach dem Klonen des Repositories einmal Folgendes im Terminal aus, damit die entsprechenden Git Hooks aktiv werden:  

    git config core.hooksPath .dev/githooks

Um bei den Branches die Übersicht zu behalten, sollten diese ebenfalls nach der Konvention benannt werden - z.B. könnte ein Branch mit einem Release-Fix für Gradle `fix/gradle-release` heißen und ein Branch, der ein neues Login-Feature zum Server hinzufügt, `feat/server-login`.  
Branches werden normalerweise beim Mergen zu einem einselnen Commit zusammengefügt (Squash-Merge), es sei denn, die einzelnen Commits des Branches haben jeweils eine alleinstehende Aussagekraft.

Detaillierte Informationen zu unserem Kollaborations-Stil findet ihr in der [Kull Convention](https://xerus2000.github.io/kull).

## Build

Als Build-Tool wird [Gradle](https://gradle.org/) verwendet. Das gesamte Projekt kann sofort nach dem Checkout per `./gradlew build` gebaut werden, es ist keine Installation von Programmen nötig.

Die wichtigsten Tasks:

| Task | Beschreibung
| ------ | ------------
| `build` | Baut alles, deployt und testet
| `test` | Führt Tests aus
| `deploy` | Erstellt hochladbare ZIP-Pakete
| `integrationTest` | Testet ein komplettes Spiel sowie den TestClient
| `startServer` oder `:server:run` | Führt den Server direkt vom Quellcode aus
| `:server:startProduction` | Startet den gepackten Server
| `:player:run` | Startet den SimpleClient direkt vom Sourcecode
| `:player:shadowJar` | Baut eine jar des SimpleClient
| `:test-client:run` | Startet den Testclient

### Unterprojekte

Tasks in Unterprojekten können über zwei Wege aufgerufen werden:  
`./gradlew :server:run` führt den Task "run" des Unterprojektes "server" aus.
Alternativ kann man in das Server-Verzeichnis wechseln und dort `./gradlew run` ausführen.

Bei der Ausführung eines Unterprojekts via `run` können per `-Dargs="Argument1 Argument2"` zusätzlich Argumente mitgegeben werden. Zum Beispiel kann der TestClient mit folgendem Befehl direkt aus dem Sourcecode getestet werden:

    ./gradlew :test-client:run -Dargs="--player1 ../../player/build/libs/defaultplayer.jar --player2 ../../player/build/libs/defaultplayer.jar --tests 3"

### Arbeiten mit Intellij IDEA

Zuerst sollte sichergestellt werden, dass die neuste Version von Intellij IDEA verwendet wird, da es ansonsten Probleme mit Kotlin geben kann.

In Intellij kann man das Projekt bequem von Gradle importieren, wodurch alle Module und Bibliotheken automatisch geladen werden. Dazu sind folgende Schritte notwendig:

- Projekt klonen: `git clone git@github.com:CAU-Kiel-Tech-Inf/socha.git`
- In IDEA auf "File" > "New" > "Project from existing sources" > socha Verzeichnis auswählen
  - "Import project from external model" > "Gradle" auswählen
  - Im folgenden Fenster:
    - "Use auto-import" ankreuzen
    - bei "Gradle JVM" JDK 8 auswählen, wenn sie nicht schon ausgewählt ist
    - "Finish" drücken
- Warten, bis der Gradle build fertig ist
- Einmal im Terminal `git checkout .idea` ausführen, um sich die codeStyles zurückzuholen

Nun können Gradle Tasks auch direkt in IDEA vom Gradle Tool Window ausgeführt werden; dieses befindet sich normalerweise in der rechten Andockleistefix.
