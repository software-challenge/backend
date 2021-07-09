# Dein Software-Challenge Spieler

In diesem Paket ist alles dabei, was du brauchst, um einen Spieler für die
[Software-Challenge YEAR](https://software-challenge.de) zu entwickeln!

In der [Dokumentation](https://docs.software-challenge.de)
findest du eine Anleitung,
wie du eine [eigene Strategie umsetzen kannst](https://docs.software-challenge.de/_den_simpleclient_erweitern.html).

## Den Spieler einsetzen

Dieses Projekt ist mit [Gradle](https://gradle.org) vorkonfiguriert, wodurch
alle Abhängigkeiten automatisch sichergestellt werden und du dich voll und ganz
auf das konzentrieren kannst, was zählt – deine Strategie umzusetzen :)

Du kannst Gradle entweder über deine IDE oder über die Kommandozeile aufrufen
(`./gradlew`). Dazu gibst du Gradle noch einen Auftrag:
- `run` startet deinen Spieler.
- `shadowJar` erstellt eine eigenständige ("fat") jar im Projektverzeichnis,
  die du ausführen, weitergeben und im [Wettkampfsystem](https://contest.software-challenge.de/saison/latest)
  hochladen kannst.

Außerdem gibt es zwei Möglichkeiten, die Abhängigkeiten aktuell zu halten:
- Wenn du Gradle normal ausführst, wird die in `build.gradle.kts` Zeile 24
  festgelegte Version des Spiel-Plugins genutzt. Diese kannst du nach der
  Veröffentlichung einer neuen Version einfach anpassen.
  Zum erstmaligen Herunterladen der Abhängigkeiten wird hierbei eine
  Internetverbindung benötigt.
- Wenn du Gradle im Offline-Modus ausführst, indem du den Parameter `--offline`
  mitgibst, werden die lokal gespeicherten Bibliotheken aus dem `lib`-Ordner
  genutzt und du benötigst keine Internetverbindung.
  Bitte beachte, dass du diese ggf. manuell aktualisieren musst.