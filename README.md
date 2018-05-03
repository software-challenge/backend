# Software-Challenge
Das offizielle Repository der [Software-Challenge](https://www.software-challenge.de/), welches aus Server, Client und Spiel-Plugins besteht.

## Struktur
| Ordner | Beschreibung |
| ------ | ------------ |
| game_gui | Eine Java-Oberfläche für den Server |
| game_helper_clients | Zusätzliche Clients (z.B. der TestClient) |
| game_players | Spielspezifische Clients |
| game_plugins | Spielspezifische Plugins |
| game_server | Der Spielserver |
| organisation | Projektverwaltung, Todo-Listen, Formales, ... |
| software_challenge_sdk | Plattformübergreifend verwendete Klassen |
| vm_administration | Verwaltung und Konfiguration der Client-VM |

## Build
Als Build-Tool wird [Apache Ant](https://ant.apache.org/) verwendet. Um Teile des Projektes zu bauen, liegen im Hauptordner und in den Unterordnern Buildfiles mit dem Namen "build.xml".

Ein vollwertiger Build kann über `ant main` oder eines der Shellskripte ausgeführt werden.

| Target | Beschreibung |
| ------ | ------------ |
| clean | Säubert alle Zielordner die während eines Builds erstellt werden |
| dependencies | Kompiliert erforderliche Abhängigkeiten |
| runnable | Kompiliert Spieler, TestClient und Server zu ausführbaren Dateien |
| build | Kompiliert alle Projekte |
| build-jar | Kompiliert alle Projekte und verpackt sie zu JAR-Dateien |
| deploy | Kompiliert alle Projekte und verpackt sie zu produktionsbereiten ZIP-Paketen |
| build-doc | Generiert Javadoc für alle Projekte |
| build-clean | Kompiliert und säubert alle Projekte |
| build-deploy | Kompiliert, säubert, generiert Javadoc und packt ZIP-Pakete für alle Projekte |
| main | Alias für "build-deploy" |