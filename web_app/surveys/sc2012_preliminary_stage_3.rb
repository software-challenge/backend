survey "Software Challenge 2012 Voranmeldung - Stufe 3", {:access_code => "2012_rerecall", :description => "Die letzte Umfrage vor der endgültigen Anmeldung der Software-Challenge 2012. Dabei werden genauere Informationen über den Kurs und dessen Erfahrung erhoben."}  do

  section "Angaben zum teilnehmenden Team" do

    q_1 "Als was wird Informatik unterrichtet?", :pick => :one
    a_1 "Reguläres Fach"
    a_2 "Freizeitprojekt oder AG"

    q_2 "Wann findet der Unterricht statt? (z.B. Mittwoch von 13:30 bis 14:30 Uhr)"
    a_1 :string

    q_3 "Wie viele wöchentliche Schulstunden umfasst der Kurs?"
    a_1 :integer
  end

  section "Angaben zur technischen Ausstattung des Kursraumes" do

    q_1 "Ist ein Beamer vorhanden?", :pick => :one
    a_1 "Ja"
    a_2 "Nein"

    q_2 "Die Computer im Kursraum", :pick => :any
    a_1 "sind in einem Netzwerk verbunden"
    a_2 "haben Internetzugang"

    q_3 "Welche ungefähre Leistung haben die Computer im Kursraum (in MHz)?"
    a_1 :integer

    q_3 "Es handelt sich um", :pick => :one
    a_1 "eigenständige Computer"
    a_2 "einen zentralen Server mit Arbeitsplätzen"

    q_4 "Jeder Schüler hat", :pick => :any
    a_1 "ein eigenes Arbeitsverzeichnis"
    a_2 "einen eigenen Login"
  end

  section "Softwareausstattung im Kursraum" do

    q_1 "Betriebssystem", :pick => :one
    a_1 "Microsoft Windows, Version", :string
    a_2 "Linux, Distribution & Version", :string
    a_3 "MacOS, Version", :string
    a_4 "Ein anderes System:", :string

    q_2 "Programmiersprachen", :pick => :any
    a_1 "Java, Version", :string
    a_2 "Delphi, Version", :string
    a_3 "Eine andere:",:string

    q_3 "Entwicklungsumgebung", :pick => :any
    a_1 "Eclipse, Version", :string
    a_2 "Netbeans, Version", :string
    a_3 "Java Editor, Version", :string
    a_3 "Delphi IDE, Version", :string
    a_4 "Eine andere:", :string
  end

  section "Bisheriger Unterricht" do

    label "Bitte berücksichtigen Sie auch Inhalte, welche Schüler in vergangenen Jahrgangsstufen im Unterricht behandelt haben"

    q_1 "Bisher wurden folgende, grundlegende Aspekte der Programmierung behandelt", :pick => :any
    a_1 "arithmetische und Vergleichsoperatoren (+, -, div, <, <=, ...)"
    a_2 "(bitweise) logische Operatoren (AND, OR, ...)"
    a_3 "primitive Datentypen (int, boolean, char, ...)"
    a_4 "Kontrollstrukturen (if, for, while, ...)"
    a_5 "Prozeduren / Funktionen"

    q_2 "Bisher wurden folgende, objektorientierte Aspekte der Programmierung behandelt", :pick => :any
    a_1 "Vererbung"
    a_2 "Überladen / Überschreiben von Funktionen (Polymorphie)"
    a_3 "Abstrakte Klassen / Interfaces"
    a_4 "Zugriffsrechte (private, public, ...)"
    a_5 "Generische Klassen"

    q_3 "Bisher wurden folgende, fortgeschrittene Aspekte der Programmierung behandelt", :pick => :any
    a_1 "Rekursion"
    a_2 "funktionale Programmierung"
    a_3 "Server/Client-Programmierung"
    a_4 "Datenbankprogrammierung"

    q_4 "Bisher wurden folgende Datenstrukturen behandelt", :pick => :any
    a_1 "Arrays, Enums, Structs, ..."
    a_2 "Listen"
    a_3 "Sets, Maps"
    a_4 "Bäume"
    a_5 "Graphen"

    q_5 "Bisher wurden folgende Entwurfshilfen behandelt", :pick => :any
    a_1 "Struktogramme"
    a_2 "Flussdiagramme"
    a_3 "UML-Klassendiagramme"
    a_4 "andere UML-Diagramme"
    a_5 "Entwurfsmuster"

    q_6 "Welche sonstigen Themen wurden im Unterricht behandelt?"
    a :text

  end

  section "Selbsteinschätzung" do
    grid "Kenntnisse der Schüler in den Bereichen" do
     a "gering"
     a "mittel"
     a "gut"
     q "Grundlegende Aspekte der Programmierung (bzgl. der oben angegebenen)", :pick => :one
     q "Objektorientierte Aspekte der Programmierung (bzgl. der oben angegebenen)", :pick => :one
     q "Fortgeschrittene Aspekte der Programmierung (bzgl. der oben angegebenen)", :pick => :one
     q "Datenstrukturen (bzgl. der oben angegebenen)", :pick => :one
     q "Entwurfshilfen (bzgl. der oben angegebenen)", :pick => :one
    end

    grid "Kenntnisse der Lehrkraft in den Bereichen" do
     a "gering"
     a "mittel"
     a "gut"
     q "Grundlegende Aspekte der Programmierung (bzgl. der oben angegebenen)", :pick => :one
     q "Objektorientierte Aspekte der Programmierung (bzgl. der oben angegebenen)", :pick => :one
     q "Fortgeschrittene Aspekte der Programmierung (bzgl. der oben angegebenen)", :pick => :one
     q "Datenstrukturen (bzgl. der oben angegebenen)", :pick => :one
     q "Entwurfshilfen (bzgl. der oben angegebenen)", :pick => :one
    end
  end

end
