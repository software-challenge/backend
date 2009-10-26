# language: de

Funktionalität: Aggregate Rounds
  In order to display the complete result of a match
  the contest holder
  wants to aggregate the results of rounds.

  Szenario: Aggregate fresh rounds
    Gegeben sei ich bin ein Administrator
    Und BigCup ist ein Wettbewerb
    Und BigCup hat 5 Teilnehmer
    Und BigCup hat ein einfaches Punktesystem
    Und BigCup hat einen Spielplan
    Und der 1. Spieltag von BigCup wurde gespielt
    Und der 1. Spieltag von BigCup ist fehlerhaft
    Und ich bin auf der Seite vom 1. Spieltag von BigCup
    Wenn ich "reaggregateAction" folge
    Dann sollte ich nicht "-1 : -1" sehen

  Szenario: Unplayed Matches
    Gegeben sei ich bin ein Administrator
    Und BigCup ist ein Wettbewerb
    Und BigCup hat 5 Teilnehmer
    Und BigCup hat ein einfaches Punktesystem
    Und BigCup hat einen Spielplan
    Wenn ich auf die Seite vom 1. Spieltag von BigCup gehe
    Dann sollte ich "Kein Ergebnis" sehen
    Und sollte ich ein "#playAction" Element haben

  Szenario: Play round
    Gegeben sei ich bin ein Administrator
    Und BigCup ist ein Wettbewerb
    Und BigCup hat 5 Teilnehmer
    Und BigCup hat ein einfaches Punktesystem
    Und BigCup hat einen Spielplan
    Und ich bin auf der Seite vom 1. Spieltag von BigCup
    Wenn ich "playAction" folge
    Dann sollte ich nicht "Kein Ergebnis" sehen
    Und sollte ich nicht ein "#playAction" Element haben
    Und sollte ich ein "#reaggregateAction" Element haben
    Und sollte ich ein "#resetAction" Element haben
