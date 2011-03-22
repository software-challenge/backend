survey "Umfrage: Software Challenge 2012 Voranmeldung " do
  section "Fragen zum teilnehmenden Team" do
  

  label "Wenn sie eine Seite abgeschlossen haben, so werden die Daten schon gespeichert, sie können also mit dem Ausfüllen der nächsten Seiten später fortfahren und auch ihre bisherigen Eingaben editieren."

    q_1 "Anzahl der Teilnehmer"
    a_1 :string

    q_2 "Jahrgangsstufe der Teilnehmer"
    a_1 :string

    q_3 "Wie wieviele Schuljahre haben die Schüler schon Informatikunterricht?"
    a_1 :string

    q_4 "Als was wir Informatik unterrichtet?", :pick => :one
    a_1 "Profilgebendes Fach"
    a_2 "Profilergänzendes Fach"
    a_3 "Freizeitprojekt oder eine AG"
    a_4 "Andere Unterrichtsform"

    q_4a "In welcher anderen Unterrichtsform findet er statt?"
    a_1 :text
    dependency :rule => "A"
    condition_A :q_4, "==", :a_4

    question_5 "Kursumfang in Schulstunden"
    a_1 :string

  end

  section "Verfügbare Infrastruktur und technische Ausrüstung" do
    
    q_6 "Welche organisatorischen Voraussetzungen bestehen?", :pick => :any
    a_1 "Es gibt eine Liste der eMail-Adressen aller Schüler."
    a_2 "Die Schule betreibt ein eigenes Forum"
    a_3 "Der Kurs kommuniziert über Instant Messeger miteinander"
    a_4 "Dem Kurs steht ein Versionsverwaltunssystem zur Verfügung (z.B. Subversion oder Git)"

    q_6a "Welches Versionsverwaltungssystem steht dem Kurs zur verfügung?"
    a_1 :text
    dependency :rule => "A"
    condition_A :q_6, "==", :a_4
   

    q_7 "Im Kursraum ist vorhanden...", :pick => :any
    a_1 "eine Tafel"
    a_2 "ein Beamer"
    a_3 "Computerarbeitsplätze für die Schüler"
    a_4 "ein interaktives Whiteboard"
    a_5 "WLAN welches den Schülern zur Verfügung steht"
    a_6 "Anschlussmöglichkeiten für Schülernotebooks"
    a_7 "weitere technische Mittel"

    q_7a "Welche weiteren technischen Hilfsmittel stehen zur Verfügung"
    a_1 :string
    dependency :rule => "A"
    condition_A :q_7, "==", :a_7

    q_8 "Die nutzbaren Computer..", :pick => :any
    a_1 "haben einen Internetzugang"
    a_2 "sind in einem Netzwerk verbunden"
    a_3 "besitzen die Möglichkeit untereinander Daten auszutauschen"
    dependency :rule => "A"
    condition_A :q_7, "==", :a_3
    
    q_9 "Um welche Art von Computerarbeitsplätzen handelt es sich?", :pick => :one
    a_1 "eigenständige Computer"
    a_2 "ein zentraler Server mit Arbeitsplätzen"
    a_3 "ein Terminalserver mit mehreren Clients"
    a_4 "andere Architektur", :string

    q_10 "Welcher Leistungsklasse gehören die Computerarbeitsplätze an?", :pick => :one
    a_1 "sehr alt (unter 500 Mhz und 128 MB RAM)"
    a_2 "alt (unter 1 Ghz und 256 MB RAM)"
    a_3 "mittel (ab 1,5 Ghz und 512 MB RAM)"
    a_4 "neu (ab 2 Ghz oder mehrere Kerne und 1 GB RAM)"
    a_5 "sehr neu (mindestens 2 Kerne und 2 GB RAM)"

    q_11 "Jeder Schüler besitzt...", :pick => :any
    a_1 "einen eigenen Login"
    a_2 "eine eigene E-Mail-Adresse"
    a_3 "eine eigenes Arbeitsverzeichnis" 

  end
  
  section "Softwareausstattung im Kursraum" do
    q_12 "Welche Betriebssysteme verwenden sie? (Version)", :pick => :any
    a_1 "Microsoft Windows"
    a_2 "Linux/Unix"
    a_3 "Mac OS"
    a_4 "Andere"

    q_12a "In welchen Versionen liegen die verwendeten Betriebssysteme vor?"
    a "Beschreibung", :text
    dependency :rule => "A"
    condition_A :q_12, "count>0"
 
    q_13 "Welche Programmiersprachen werden verwendet?", :pick => :any
    a_1 "Java"
    a_2 "Delphi"
    a_3 "Python"
    a_4 "Ruby"
    a_5 "C"
    a_6 "C++"
    a_7 "Perl"
    a_8 "Eine andere Sprache:", :string

    q_13a "Welche andere Programmiersprache wird verwendet?"
    a_1 :text
    dependency :rule => "A"
    condition_A :q_13, "==", :a_8

    q_14 "Verwenden Sie Entwicklungsumgebungen?", :pick => :one
    a_1 "Eclipse"
    a_2 "Netbeans"
    a_3 "Java Editor"
    a_4 "Delphi IDE"
    a_5 "Andere", :string

  end
  
  section "Bisheriger Informatik Unterricht" do

   label "Bitte berücksichtigen Sie auch Inhalte, welche die Schüler in vergangenen Jahrgangsstufen (ggf. auch ei anderen Lehrkräften) im Unterricht behandelt haben."

   q_1 "Behandelte grundlegene Aspekte der Programmierung", :pick => :any
   a_1 "arithmetische und Vergleichsoperatoren (+,-,div,<,<=,...)"
   a_2 "(bitweise) logische Operatoren (AND, OR, ...)"
   a_3 "primitive Datentypen (int, boolean, char, ..)"
   a_4 "Kontrollstrukturen (if,for,while, ...)"
   a_5 "Prozeduren / Funktionen"
   
   q_2 "Behandelte objektorientierte Aspekte der Programmierung", :pick => :any
   a_1 "Vererbung"
   a_2 "Überladen/überschreiben von Funktionen (Polymorphie)"
   a_3 "Abstrakte Klassen / Interfaces"
   a_4 "Zugriffsrechte (private,public, ...)"
   a_5 "Generische Klassen"

   q_3 "Behandelte fortgeschrittene Aspekte der Programmierung", :pick => :any
   a_1 "Rekursion"
   a_2 "Funktionale Programmierung"
   a_3 "Server/Client-Programmierung"
   a_4 "Datenbankprogrammierung"

   q_4 "Behandelte Datenstrukturen"
   a_1 "Arrays, Enums, Structs, ..."
   a_2 "Listen"
   a_3 "Sets, Maps"
   a_4 "Bäume"
   a_5 "Graphen"

   q_5 "Folgende Entwurfshilfen wurden behandelt", :pick => :any
   a_1 "Struktogramme"
   a_2 "Flussdiagramme"
   a_3 "UML-Klassendiagramme"
   a_4 "andere UML-Diagramme:", :string
   a_5 "Entwurfsmuster"

   q_6 "Welche sonstigen Themen wurden noch im Unterricht behandelt?"
   a_1 :text

  end

  section "Selbsteinschätzung" do 
    label "Eine grobe Selbsteinschätzung der Fähigkeiten (insbesondere der Schüler, aber auch der Lehrkraft) erlaubt uns entsprechende Anpassungen an den Kenntnisstand der Teams."

    grid "Selbsteinschätzung der Lehrkraft" do
     a "gering"
     a "mittel"
     a "gut"
     q "Erfahrung mit der verwenden Programmiersprache", :pick => :one
     q "Grundlegende Aspekte der Programmierung", :pick => :one
     q "Objektorientierte Aspekte der Programmierung", :pick => :one
     q "Fortgeschrittene Aspekte der Programmierung", :pick => :one
     q "Datenstrukturen", :pick => :one
     q "Entwurfshilfen", :pick => :one
    end

    grid "Selbsteinschätzung der Schüler" do
     a "gering"
     a "mittel"
     a "gut"
     q "Erfahrung mit der verwenden Programmiersprache", :pick => :one
     q "Grundlegende Aspekte der Programmierung", :pick => :one
     q "Objektorientierte Aspekte der Programmierung", :pick => :one
     q "Fortgeschrittene Aspekte der Programmierung", :pick => :one
     q "Datenstrukturen", :pick => :one
     q "Entwurfshilfen", :pick => :one
    end
  end
  
  section "Bisherige Teilnahmen und Betreuungsbedarf" do
    
    q_0 "Haben Sie oder die Schüler schon einmal an der Software-Challenge teilgenommen?", :pick => :one
    a_1 "Ja"
    a_2 "Nein"

    q_1 "Teilnahmen der Lehrkraft", :pick => :one, :display_type => :dropdown
    a "Keine Teilnahme"
    a "1"
    a "2"
    a "3"
    a "4"
    a "5"
    a "6"
    dependency :rule => "A"
    condition_A :q_0, "==", :a_1
   
    q_2 "Teilnahmen der Schüler", :pick => :one, :display_type => :dropdown
    a "Keine Teilnahme"
    a "1"
    a "2"
    a "3"
    a "4"
    dependency :rule => "A"
    condition_A :q_0, "==", :a_1
   
   
    q_2a "Wie oft zum Wettkampf ein funktionsfähiger Client eingesendet?", :pick => :one
    a "Ja"
    a "Nein"
    dependency :rule => "A or B"
    condition_A :q_1, "count>0"
    condition_B :q_2, "count>0"

    q_3 "Wurde von der Möglichkeit gebrauch gemacht, den Client während der Wettkampfphase zu aktualisieren?", :pick => :one
    a "Ja"
    a "Nein"
    dependency :rule => "A"
    condition_A :q_0, "==", :a_1
   

    q_4 "Das Forum zur Software-Challenge wurde aktiv benutzt", :pick => :one
    a "Ja"
    a "Nein"
    dependency :rule => "A"
    condition_A :q_0, "==", :a_1
   

    q_5 "Wir benötigen", :pick => :one
    a_1 "umfangreiche Betreuung durch eine Hilfskraft"
    a_2 "gelegentliche Betreuung durch eine Hilfskraft"
    a_3 "nur einen E-Mail-Ansprechpartner für Fragen"
    a_4 "keine Betreuung durch eine Hilfskraft"
    dependency :rule => "A"
    condition_A :q_0, "==", :a_1
   
    
    q_6 "Anmerkungen"
    a "zur benötigten Betreuung", :text
    dependency :rule => "A"
    condition_A :q_0, "==", :a_1
   

  end

  section "Software-Challenge" do
    
    q_1 "Wie sind sie auf die Software-Challenge aufmerksam geworden?", :pick => :any
    a_1 "Bildungsserver (ggfs. welcher)", :string
    a_2 "Schulverwaltungsblatt(ggfs. welches)", :string
    a_3 "Zeitung", :string
    a_4 "Freunde"
    a_5 "E-Mail des Institut für Informatik"
    a_6 "CEBIT Präsenz"
    a_7 "Sonstiges", :string
    
    q_2 "Wir würden uns vom Software-Challenge Team wünschen", :pick => :any
    a_1 "Weitere Funktionen im Wettkampfsystem, z.B. im Bezug auf Freundschaftsspiele"
    a_2 "Unterstützung weiterer Programmiersprachen, nämlich:", :string
    a_3 "Eine schnellere Antwort auf Fragen"
    a_4 "Die Bereitstellung von eines Versionsverwaltungssystems für die Teams"
    a_5 "Eine bessere Dokumentation"
    a_6 "Weiteres"

    q_2a "Beschreiben sie ihre weiteren Wünsche:"
    a "Beschreibung", :text
    dependency :rule => "A"
    condition_A :q_2, "==", :a_6

  end
end
