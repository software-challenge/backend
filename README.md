# Software-Challenge

Das offizielle Repository der [Software-Challenge](https://www.software-challenge.de/), welches aus Server, Client und Spiel-Plugins besteht.

## Struktur
| Ordner | Beschreibung |
| ------ | ------------ |
| helpers | Zusätzliche Tools (z.B. der TestClient) |
| players | Spielspezifische Clients |
| plugins | Spielspezifische Plugins |
| server | Der Spielserver |
| socha-sdk | Projektübergreifend verwendete Klassen |

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
| `run` oder `testDeployed` | startet den server und 2 simpleclients und überpüft, ob das Spiel normal endet
| `startServer` oder `:server:run` | führt den Server direkt vom Quellcode aus
| `:server:startProduction` | startet den gepackten Server
| `:players:run` | startet den SimpleClient direkt vom Sourcecode
| `:test-client:run` | startet den Testclient

Tasks der Subprojekte können in zwei Wegen aufgerufen werden:
`./gradlew :server:run` führt die Task "run" des Subprojektes "server" aus.  
Das gleiche kann auch erreicht werden, in dem man in das server-Verzeichnis 
wechselt und dort `./gradlew run` ausführt.

Wenn notwendig, können bei der Ausführung eines Subprojektes via `run` per `-Dargs="Argument1 Argument2"`
Argumente mitgegeben werden.

## Release

Ein Release kann durch dieses Command initiiert werden:

`./gradlew release -Pv=X.X -Pdesc="Versionsbeschreibung"`

- `-Pv` gibt die Version an, beginnend bei 0.0 - die Jahresnummer wird automatisch davorgesetzt
- `-Pdesc` ist eine kurze Beschreibung der Version. Sie wird als Nachricht des Tags verwendet und 
  im vorgeschlagenen Text für Ankündigungen auf verschiedenen Platformen eingefügt.

Dann wird die Version (in `gradle.properties`) automatisch angepasst, ein commit gemacht, git tag gesetzt und gepusht.  
Im Anschluss gibt das Skript Hinweise, wie weiter zu verfahren ist.
