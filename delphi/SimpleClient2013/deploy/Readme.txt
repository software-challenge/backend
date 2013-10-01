
--- Delphi-Client Update 3 ---

Einleitung
===========

Dies ist der Delphi SimpleClient fur das Spiel "Cartagena" 
der Software Challenge 2013.

Der Client ist in Delphi 7 geschrieben, sollte aber auch unter Delphi 6 und
evtl. Delphi 5 problemlos funktionieren.


Entwicklung
============

Das Projekt öffnen
-------------------

Die Projektdatei src/simpleClient/simpleClient2013.dpr kann einfach in
Borland Delphi geöffnet werden. Der Client kann dann direkt aus der
Entwicklungsumgebung heraus gestartet werden.


WICHTIG: 
---------

Bevor der Client ausgeführt werden kann, muss Borland Delphi noch
so konfiguriert werden, dass der Debugger die Exception "EParserException"
ignoriert.
Unter Borland Delphi 7 findet man diese Einstellung unter 
Tools->Debugger Options->Language Exceptions
Dort fügt man per "Add" den Exception Type "EParserException" (ohne Anführungszeichen)
hinzu und bestätigt alles mit "OK".


Bibliotheken
-------------

Im Ordner "src/lib" befinden sich die Units, die der Umsetzung der XML-Kommunikation
mit dem Server und der Darstellung des Spielzustandes und der Spielelemente
(Felder, Spielfiguren, Karten, ...) dienen.

In der Regel sollten diese Dateien nicht bearbeitet werden.


Eigene Logik
-----------------

Die Dateien, in denen die Schulen ihre eigene Logik implementieren sollen,
liegen im Ordner "src/simpleClient".

Die Klasse TMyBoard in der Unit UMyBoard ist eine Erweiterung der Board-Klasse
TBoard (src/lib/UBoard.pas) und enthält bereits einige grundlegende Hilfsfunktionen.
Weitere Funktionen können von den Schulen hier problemlos ergänzt werden.

In der Klasse TClient (src/simpleClient/UClient.pas) wird die KI umgesetzt.
Jedesmal, wenn der Client am Zug ist, wird hier die Methode TClient.zugAngefordert
aufgerufen, die dann einen Zug finden und zuruckgeben muss. In dem Beispielclient
wählt die Strategie lediglich einen zufälligen aus allen möglichen Zügen aus
oder setzt aus, wenn kein Zug möglich ist. Diese einfache "Strategie" dient nur der 
Veranschaulichung und sollte natürlich durch eine eigene, bessere Strategie ersetzt 
werden.


Bei Problemen oder Fragen
==========================

Wenn etwas nicht klappen sollte, steht der Support der Software-Challenge 
jederzeit gerne im Forum oder per E-Mail zur Verfügung.

Bei Fragen, sollten Fehler auftreten oder Sie Informationen/Funktionen vermissen, 
können Sie mich auch jederzeit gerne direkt kontaktieren per E-Mail unter

fewking@paniccrew.de (Sven Casimir)