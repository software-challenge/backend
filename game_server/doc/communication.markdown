Kommunikation: Software Challenge 2010
======================================

In dem folgenden Dokument wird ein kurzer Überblick über
die Kommunikationsprotokolle zwischen Server und Client gegeben.

Grundlagen
==========

Der Kommunikation liegt **XML** zu Grunde. Ein einfaches XML-Dokument kann wie folgt aussehen:

	<pc-liste>
	  <hardware:pc>
	    <name>Dimension 3100 </name>
	    <hersteller>Dell</hersteller>
	    <prozessor>AMD</prozessor>
	    <mhz>3060</mhz>
	    <kommentar>Arbeitsplatzrechner</kommentar>
	  </hardware:pc>
	  <hardware:pc>
	    <name>T 42</name>
	    <hersteller>IBM</hersteller>
	    <prozessor>Intel</prozessor>
	    <mhz>1600</mhz>
	    <kommentar>Laptop</kommentar>
	  </hardware:pc>
	</pc-liste>

Das Dokument ist hierarchisch durch sogenannte XML-Knoten gegliedert.
Hier einige Beispiele von XML-Knoten.

* `<b/>`
* `<b />`
* `<b a="aa"/>`
* `<foo:b bar:a="aa"/>`
* `<b>inhalt</b>`
* `<b><x /></b>`

Ein Knoten besteht also aus einem Namen `b`, hat optional einen
Namensraum `foo`, hat optional beliebig viele Parameter `a`
(die wieder einen Namensraum `bar` besitzen können) sowie einen
optionalen Inhalt. Der Inhalt kann beliebig viele (auch weiter verschachtelte)
Knoten oder auch Text enthalten.

**Hinweis** In den meisten Programmiersprachen gibt es für das Lesen/Schreiben
von XML-Dokumenten Bibliotheken. 

Kommunikation
=============

Die Kommunikation vom Client zum Server als auch umgekehrt werden jeweils
über ein XML-Dokument gelöst. Die Dokumente enthalten nur einen direkten Kindknoten. Sobald der 
Kindknoten geschlossen wird, wurde die Verbindung geschlossen. Unter bestimmten Umständen entfällt
der Abschluss aber (unerwarteter Verbindungsabriss). Der Name des Wurzel-Elements kann variieren!

    <wurzel>
      ...    
    </wurzel>

**Hinweis** Bei der Kommunikation liegt also nie ein vollständiges XML-Dokument vor. Dies ist vor allem bei der
Wahl der XML-Bibliothek zu beachten. Sie sollte partielle XML-Dokumente unterstützen. 

Wichtig: Reihenfolge
--------------------

Wenn es nicht explizit angegeben ist, kann
sich die Reihenfolge der Kind-Knoten eines Elements verändern! Das heißt, dass auch wenn
in der Dokumentation nur eine Notation aufgeführt ist, auch andere Reihenfolgen möglich sein können.

	<a>
	  <b>
	  <c>
	</a>
	
Oder auch:
	
	<a>
	  <c>
	  <b>
	</a>
	
Dies gilt ebenso für Parameter.

    <a foo="x" bar="y" />

Oder auch:

    <a bar="y" foo="x" />
	
Wichtig: Undokumentierte Attribute und Elemente
-----------------------------------------------

In einigen Fällen können zusätzliche, hier nicht dokumentierte Kind-Knoten oder Parameter
mitgeschickt werden. Diese sollten dann einfach ignoriert werden.

Befehlssätze
------------

Es gibt zwei Befehlssätze: der allgemeine Befehlssatz, der
sich mit der nächsten Software-Challenge nicht ändern
wird, als auch einen Spiel-spezifischen Befehlssatz,
der sich jedes Jahr ändern wird.

Allgemeiner Befehlssatz
-----------------------

<h3>Beitreten zu einem Spiel mit gegebener Reservierungsnummer</h3>

Clienten werden von einem Skript (und dem grafischen Server) mit speziellen Parametern aufgerufen.

    java -jar C:\HaseUndIgelSC.jar --host localhost --port 13050 --reservation f075dd22-cc18-4baf-9939-5979208a536d  

Der übergebene ReservierungsCode muss an den Server gesendet werden:

    <joinPrepared reservationCode="f075dd22-cc18-4baf-9939-5979208a536d"/>
    
Als Antwort kommt folgende Nachricht zurück

    <joined roomId="9de97435-4ae7-4d08-9b26-327db2423d7b"/>
    
In dieser befindet sich die ID des Spiels, in dem nun gespielt wird.

<h3>Benachrichtigung beim Verlassen von einem Raum</h3>

    <left roomId="9de97435-4ae7-4d08-9b26-327db2423d7b"/>

<h3>Nachrichten von und zu Räumen</h3>

Alle Befehle aus dem speziellen Befehlssatz an Spielräume müssen
in ein `room` XML-Knoten eingebettet werden. Nachrichten von Spielen sind ebenfalls
in solche `room`-Knoten eingeschlossen. Hier zum Beispiel eine Nachricht vom Server an den Client:

	<room roomId="9de97435-4ae7-4d08-9b26-327db2423d7b">
	  <data class="test:welcome">
	    <color>YELLOW</color>
	  </data>
	</room>
	
Das `data` Element enthält die Daten, die gesendet wurden. `class` gibt den Datentyp an.
In diesem Fall ist das eine Willkommensnachricht vom Spiel.
	
<h3>Spielzustände</h3>

Der Zustand eines Spiels wird nicht nur in einen `room`-Knoten eingebettet, sondern auch in ein `<data class="memento">` Memento-Element.

	<room roomId="9de97435-4ae7-4d08-9b26-327db2423d7b">
	  <data class="memento">
	    <state>...|.X.|XOO</state>
	  </data>
	</room>
	
<h3>Spielergebnis</h3>

Das Spielergebnis wird in einem festgelegten Format am Ende des Spiels mitgeteilt.

	<room roomId="9de97435-4ae7-4d08-9b26-327db2423d7b">
	  <data class="result">
	    <definition>
	      <fragment name="Gewinner">
	        <aggregation>SUM</aggregation>
	        <relevantForRanking>true</relevantForRanking>
	      </fragment>
	      ...
	    </definition>
	    <score cause="REGULAR">
	      <part>1</part>
	      ...
	    </score>
	    ...
	  </data>
	</room>
	
Für jeden Spiel wird ein `score` Element ausgegeben. Die Anzahl der `fragment`s sind
identisch zu der Anzahl der `part`s innerhalb eines `score`.

Der Wert von `aggregation` (unter `fragment`) ist entweder

* `SUM`
* `AVERAGE`

Der Wert von `relevantForRanking` (unter `fragment`) ist entweder

* `true`
* `false`

Der Wert von `cause` (unter `score`) ist entweder

* `REGULAR`
* `LEFT`
* `RULE_VIOLATION`
* `UNKNOWN`
	
Ein komplettes Beispiel könnte wie folgt aussehen:

	<room roomId="9de97435-4ae7-4d08-9b26-327db2423d7b">
	  <data class="result">
	    <definition>
	      <fragment name="Gewinner">
	        <aggregation>SUM</aggregation>
	        <relevantForRanking>true</relevantForRanking>
	      </fragment>
	      <fragment name="Ø Feldnummer">
	        <aggregation>AVERAGE</aggregation>
	        <relevantForRanking>true</relevantForRanking>
	      </fragment>
	      <fragment name="Ø Karotten">
	        <aggregation>AVERAGE</aggregation>
	        <relevantForRanking>true</relevantForRanking>
	      </fragment>
	      <fragment name="Ø Züge">
	        <aggregation>AVERAGE</aggregation>
	        <relevantForRanking>true</relevantForRanking>
	      </fragment>
	      <fragment name="Ø Zeit (ms)">
	        <aggregation>AVERAGE</aggregation>
	        <relevantForRanking>true</relevantForRanking>
	      </fragment>
	    </definition>
	    <score cause="REGULAR">
	      <part>1</part>
	      <part>64</part>
	      <part>7</part>
	      <part>30</part>
	      <part>64526</part>
	    </score>
	    <score cause="REGULAR">
	      <part>0</part>
	      <part>57</part>
	      <part>20</part>
	      <part>30</part>
	      <part>25171</part>
	    </score>
	  </data>
	</room>
	
Befehlssatz für Hase und Igel (2010)
------------------------------------

<h3>Server: Willkommensnachricht</h3>

Zu Beginn eines Spiels erhält jeder Spieler eine Nachricht mit seiner Farbe. Danach folgen die Spielzustände.

	<room roomId="9de97435-4ae7-4d08-9b26-327db2423d7b">
	  <data class="hui:welcome">
	    <myColor>BLUE</myColor>
	  </data>
	</room>
	
<h3>Server: Spielzustand</h3>

Ein Spielzustand hat die folgende Struktur (die Player/Board Felder wurden hier ausgelassen).

	<room roomId="9de97435-4ae7-4d08-9b26-327db2423d7b">
	  <data class="memento">
	    <state class="hui:gameState">
	      <game>
	        <player class="hui:player" displayName="Max">
	          ...
	        </player>
	        <player class="hui:player" displayName="Moritz">
	          ...
	        </player>
	        <board>
	          ...
	        </board>
	        <turn>0</turn>
	      </game>
	    </state>
	  </data>
	</room>

<h4>Der Spieler</h4>

Das Element `player` repräsentiert einen Spieler.

	<player class="hui:player" displayName="Max">
	  <color>RED</color>
	  <fieldNumber>0</fieldNumber>
	  <carrots>68</carrots>
	  <saladsToEat>5</saladsToEat>
	  <action>TAKE_OR_DROP_CARROTS</action>
	  <action>EAT_SALAD</action>
	  <action>HURRY_AHEAD</action>
	  <action>FALL_BACK</action>
	  <move n="10" typ="MOVE"/>
	  <move n="3" typ="MOVE"/>
	  <position>TIE</position>
	</player>

Der Wert von `color` ist entweder

* `RED`
* `BLUE`
	
Der Wert von `position` ist entweder

* `FIRST`
* `SECOND`
* `TIE`

Die `action`s repräsentieren die Spielkarten die der Spieler
noch auf der Hand hält. Diese werden im Verlauf des Spiels weniger.
Der Wert von `action` ist entweder

* `TAKE_OR_DROP_CARROTS`
* `EAT_SALAD`
* `HURRY_AHEAD`
* `TIE`

Die Werte von `move` enthalten die Züge des Spielers (korrekte Reihenfolge). Hierzu mehr unter **Client: Zug**.

<h4>Das Spielbrett</h4>

Das Spielbrett enthält die Feldtypen, in **fester Reihenfolge**.

	<board>
	  <field>START</field>
	  <field>RABBIT</field>
	  <field>CARROT</field>
	  <field>CARROT</field>
	  <field>RABBIT</field>
	  <field>POSITION_1</field>
	  <field>POSITION_2</field>
	  <field>RABBIT</field>
	  ...
	</board>
	
Der Wert von `field` ist entweder

* `START`
* `CARROT`
* `RABBIT`
* `HEDGEHOG`
* `POSITION_1`
* `POSITION_2`
* `GOAL`

<h3>Server: Zuganforderung</h3>

	<room roomId="9de97435-4ae7-4d08-9b26-327db2423d7b">
	  <data class="sc.framework.plugins.protocol.MoveRequest"/>
	</room>

<h3>Client: Zug</h3>

	<room roomId="9de97435-4ae7-4d08-9b26-327db2423d7b">
	  <data class="hui:move" n="10" typ="MOVE"/>
	</room>
	
	<room roomId="9de97435-4ae7-4d08-9b26-327db2423d7b">
	  <data class="hui:move" n="10" card="TAKE_OR_DROP_CARROTS"/>
	</room>
	
	<room roomId="9de97435-4ae7-4d08-9b26-327db2423d7b">
	  <data class="hui:move" n="10" typ="TAKE_OR_DROP_CARROTS"/>
	</room>

Der Wert von `typ` ist entweder

* `MOVE`
* `EAT`
* `TAKE_OR_DROP_CARROTS`
* `FALL_BACK`
* `PLAY_CARD`
* `SKIP`

Der Wert von `card` ist (vgl. `action` vom Spielzustand) entweder

* `TAKE_OR_DROP_CARROTS`
* `EAT_SALAD`
* `HURRY_AHEAD`
* `TIE`

Die möglichen, sinnvollen Kombinationen der Parameter ergeben sich aus den Spielregeln.