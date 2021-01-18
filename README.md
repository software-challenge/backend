# Software-Challenge [![Build Status](https://travis-ci.com/CAU-Kiel-Tech-Inf/backend.svg?branch=master)](https://travis-ci.com/CAU-Kiel-Tech-Inf/backend)

Dies ist das offizielle Repository der [Software-Challenge](https://www.software-challenge.de), ein Programmierwettbewerb für Schüler.
Ziel hierbei ist, für ein jährlich wechselndes Spiel eine künstliche Intelligenz zu entwickeln, die den Gegenspieler besiegt.
Das Repository besteht aus Server, Client und Spiel-Plugins.

| Ordner | Beschreibung |
| ------ | ------------ |
| helpers | Zusätzliche Tools (aktuell nur der TestClient) |
| player | SimpleClient dieses Jahres |
| plugin | Plugin dieses Jahres |
| server | Spielserver |
| sdk | Projektübergreifend verwendete Klassen |

## Kollaboration

Unsere Commit-Messages folgen dem Muster `type(scope): summary` (siehe [Karma Runner Konvention](http://karma-runner.github.io/latest/dev/git-commit-msg.html)), wobei die gängigen Scopes in [.dev/scopes.txt](.dev/scopes.txt) definiert werden. 
Nach dem Klonen mit git sollte dazu der hook aktiviert werden:

    git config core.hooksPath .dev/githooks

Um bei den Branches die Übersicht zu behalten, sollten diese ebenfalls nach der Konvention benannt werden,
z. B. könnte ein Branch mit einem Release-Fix für Gradle `chore/gradle/release-fix` heißen und ein Branch, der ein neues Login-Feature zum Server hinzufügt, `feat/server/login`.

Wenn die einzelnen Commits eines Pull Requests eigenständig funktionierten, sollte ein rebase-merge durchgeführt werden,
ansonsten (gerade bei experimentier-Branches) ein squash-merge, wobei der Titel des Pull Requests der Commit Message entsprechen sollte.

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

Dabei setzen wir auf die [WordSpec](https://kotest.io/styles/#word-spec), da man damit semantisch übersichtlich sowohl einfache Tests als auch Behavior Driven Development umsetzen kann.
Bisherige Tests nutzen die StringSpec, welche jedoch schnell unübersichtlich wird da sie keine Verschachtelung erlaubt, und in manchen Fällen auch noch direkt JUnit.
Diese sollten bei größeren Änderungen direkt zum neuen Stil migriert werden.

### Mitarbeiten

Projekt klonen, inklusive der submodule:
```sh
git clone git@github.com:CAU-Kiel-Tech-Inf/backend.git --recurse-submodules --shallow-submodules
```

#### Intellij IDEA
Zuerst sollte sichergestellt werden, dass die neuste Version von IntelliJ IDEA verwendet wird, da es ansonsten Probleme mit Kotlin geben kann.
In IntelliJ kann man das Projekt bequem von Gradle importieren, wodurch alle Module und Bibliotheken automatisch geladen werden.

Nun können Gradle-Tasks auch direkt in IntelliJ vom Gradle-Tool-Fenster ausgeführt werden; dieses befindet sich normalerweise in der rechten Andockleiste.
