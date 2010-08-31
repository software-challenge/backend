Dies ist der Delphi SimpleClient fur das Spiel "Schafchen im Trockenen" 
der Software Challenge 2011.
Der Client ist vom Aufbau her der gleiche wie letztes Jahr.
Im Ordner "src/lib" befinden sich die Units, die der Umsetzung der XML-Kommunikation
mit dem Server und der Darstellung des Spielbrettes und der darauf
befindlichen Objekte (Felder, Schafe, Blumen, ...) dienen.

In der Regel solltet ihr diese Dateien nicht bearbeiten mussen.
Die Dateien, mit denen ihr arbeiten solltet, liegen im Ordner "src/simpleClient".
Die Klasse TMyBoard in der Unit UMyBoard ist eine Erweiterung der Board-Klasse
TBoard (src/lib/UBoard.pas) und enthalt bereits einige Hilfsfunktionen zur
Arbeit mit dem Spielbrett. Weitere Funktionen konnt ihr hier nach Belieben hinzufugen.
In der Klasse TClient (src/simpleClient/UClient.pas) wird die KI umgesetzt.
Jedesmal, wenn euer Client am Zug ist, wird hier die Methode TClient.zugAngefordert
aufgerufen, die dann einen Zug finden und zuruckgeben muss.

Um den Einstieg zu erleichtern, wurde bereits eine rudimentare KI implementiert.
Diese fuhrt einfach den erstbesten gultigen Zug aus, den sie finden kann. Diese
"Strategie" solltet ihr naturlich durch eure eigene ersetzen.

Wenn etwas nicht klappt und ihr nicht weiter wisst, schreibt ins
Forum oder direkt mir eine E-Mail mit Beschreibung.
Meine E-Mail: fewking@paniccrew.de