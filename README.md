# <a target="_blank" rel="noopener noreferrer" href="https://www.software-challenge.de"><img width="128" src="https://software-challenge.de/site/themes/freebird/img/logo.png" alt="Software-Challenge Logo"></a> Spiel-Infrastruktur der Software-Challenge Germany ![.github/workflows/gradle.yml](https://github.com/software-challenge/backend/workflows/.github/workflows/gradle.yml/badge.svg)

In diesem Repository befindet sich
die Spiel-Infrastruktur der [Software-Challenge](https://www.software-challenge.de),
ein Programmierwettbewerb für Schüler.
Dabei wird für ein jährlich wechselndes Spiel eine künstliche Intelligenz entwickelt,
die sich dann in Duellen gegen andere durchsetzen muss.

Der Code teilt sich auf in gemeinsames SDK, Server, Spieler(vorlage) und Spiel-Plugins.

<<<<<<< HEAD
| Ordner   | Beschreibung                                                      |
|----------|-------------------------------------------------------------------|
| games    | Spiel-Plugins pro Jahr          |
| helpers  | Zusätzliche Tools (`test-client`, `test-config`)                  |
| player   | Spielervorlage                                                    |
| server   | Spielserver                                                       |
| sdk      | Projektübergreifend verwendete Klassen                            |
| gradle   | Build-Logik, Konventionen und Wrapper-Konfiguration              |

Die Struktur der Plugins wird aktuell im Einklang mit der GUI neu strukturiert.

Wir sind immer für Mithilfe dankbar!
Eine Entwickler-Anleitung findet sich in [CONTRIBUTING](CONTRIBUTING.md).

Mehr Informationen zu Code-Strukturen und Standards findet ihr in den [GUIDELINES](GUIDELINES.md).
