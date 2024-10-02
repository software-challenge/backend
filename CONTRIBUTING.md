# Beitragen

Wir freuen uns über jede Unterstützung,
sowohl durch [Fehlermeldungen](https://github.com/CAU-Kiel-Tech-Inf/backend/issues)
als auch mit [Code](https://github.com/CAU-Kiel-Tech-Inf/backend/pulls).

Dafür klone zuerst das Projekt lokal
(Submodule sind bisher nur für die Arbeit mit Intellij IDEA wichtig):
```sh
git clone git@github.com:software-challenge/backend.git --recurse-submodules --shallow-submodules
```

## Kollaboration

Unsere Commit-Messages folgen dem Muster `type(scope): summary`
(siehe [Karma Runner Konvention](https://karma-runner.github.io/6.2/dev/git-commit-msg.html)),
wobei die gängigen Scopes in [.dev/scopes.txt](.dev/scopes.txt) definiert werden.
Nach dem Klonen mit git sollte dazu der hook aktiviert werden:

    git config core.hooksPath .dev/githooks

Um bei den Branches die Übersicht zu behalten,
sollten diese ebenfalls nach der Konvention benannt werden,
z. B. könnte ein Branch mit einem Release-Fix für Gradle `chore/gradle/release-fix` heißen
und ein Branch, der ein neues Login-Feature zum Server hinzufügt, `feat/server/login`.

Wenn die einzelnen Commits eines Pull Requests eigenständig funktionieren,
sollte ein rebase merge durchgeführt werden,
ansonsten (gerade bei experimentier-Branches) ein squash merge,
wobei der Titel des Pull Requests der Commit-Message entsprechen sollte.

Detaillierte Informationen zu unserem Kollaborations-Stil
findet ihr in der [Kull Konvention](https://kull.jfischer.org).

## Build

Als Build-Tool wird [Gradle](https://gradle.org) verwendet.
Das gesamte Projekt kann sofort nach dem Klonen per `./gradlew build` gebaut werden,
es ist keine Installation von Programmen nötig.

Die wichtigsten Tasks:

| Task                             | Beschreibung
| ----                             | ------------
| `build`                          | Kompiliert, packt und testet alles
| `bundle`                         | Erstellt hochladbare ZIP-Pakete
| `check`                          | Führt alle Tests aus
| `test`                           | Führt Unittests aus
| `integrationTest`                | Testet ein komplettes Spiel sowie den TestClient
| `startServer` oder `:server:run` | Startet den Server direkt vom Quellcode
| `:server:startProduction`        | Startet den gepackten Server
| `:player:run`                    | Startet den Zufallsspieler direkt vom Quellcode
| `:player:shadowJar`              | Packt den Zufallsspieler zu einer eigenständig ausführbaren Datei
| `:test-client:run`               | Startet den TestClient

### Unterprojekte

Tasks in Unterprojekten können über zwei Wege aufgerufen werden:  
`./gradlew :server:run` führt den Task `run` des Unterprojektes `server` aus.
Alternativ kann man in das Server-Verzeichnis wechseln und dort `./gradlew run` ausführen.

Bei der Ausführung eines Unterprojekts via `run`
können per `-Dargs="Argument1 Argument2"` Argumente mitgegeben werden.
Zum Beispiel kann der TestClient mit folgendem Befehl
direkt aus dem Quellcode getestet werden:

    ./gradlew :test-client:run -Dargs="--player1 ../../player/build/libs/defaultplayer.jar --player2 ../../player/build/libs/defaultplayer.jar --tests 3"

### Arbeit mit Intellij IDEA

Zuerst sollte sichergestellt werden,
dass die neuste Version von IntelliJ IDEA verwendet wird,
da es ansonsten Probleme mit Kotlin geben kann.
In IntelliJ kann man das Projekt bequem via Gradle importieren,
wodurch alle Module und Bibliotheken automatisch geladen werden.

Dann können Gradle-Tasks auch direkt in IntelliJ vom Gradle-Werkzeugfenster ausgeführt werden;
dieses befindet sich normalerweise in der rechten Andockleiste.
