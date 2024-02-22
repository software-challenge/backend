# Dein Software-Challenge Spieler

In diesem Paket ist alles dabei, was du brauchst,
um einen Spieler für die [Software-Challenge YEAR](https://software-challenge.de) zu entwickeln!
Erster Anlaufpunkt dafür ist die
[offizielle Dokumentation](https://docs.software-challenge.de).

Dieses Projekt ist mit [Gradle](https://gradle.org) vorkonfiguriert,
wodurch alle Abhängigkeiten automatisch bereitgestellt werden
und du dich voll und ganz auf das konzentrieren kannst,
was wirklich zählt –
[deine Strategie umsetzen](https://docs.software-challenge.de/entwicklung/simpleclient-erweitern) :)

Du kannst Gradle entweder über deine IDE oder über die Kommandozeile aufrufen (`./gradlew`).
Dazu gibst du Gradle noch einen Auftrag:
- `run` startet deinen Spieler.
- `shadowJar` erstellt eine eigenständige ("fat") jar im Projektverzeichnis,
  die du ausführen, weitergeben
  und im [Wettkampfsystem](https://contest.software-challenge.de) hochladen kannst.

Außerdem gibt es zwei Möglichkeiten, die Abhängigkeiten aktuell zu halten:
- Wenn du Gradle normal ausführst, wird die in `build.gradle.kts` Zeile 24
  festgelegte Version des Spiel-Plugins genutzt.
  Diese kannst du nach der Veröffentlichung einer neuen Version einfach anpassen.
  Zum erstmaligen Herunterladen der Abhängigkeiten
  wird hierbei eine Internetverbindung benötigt.
- Wenn du Gradle im Offline-Modus ausführst,
  indem du den Parameter `--offline` mitgibst,
  werden die lokal gespeicherten Bibliotheken aus dem `lib`-Ordner genutzt
  und du benötigst keine Internetverbindung.
  Bitte beachte, dass du diese manuell aktualisieren musst.
