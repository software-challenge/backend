survey "Software Challenge 2012 Voranmeldung - Stufe 2" do
  section "Angaben zum teilnehmenden Kurs" do
    label "Hinweis: Sollten Sie noch keine Teams angemeldet haben, tun Sie die bitte entsprechend ihrer Wünsche"

    q_1 "Anzahl der Teilnehmer"
    a_1 :string

    q_2 "Jahrgangsstufe der Teilnehmer"
    a_1 :string

    q_3 "Wie viele Schuljahre haben die Schüler schon Informatikunterricht?"
    a_1 :integer

    q_4 "Als was wird Informatik unterrichtet?", :pick => :one
    a_1 "Profilgebendes Fach"
    a_2 "Profilergänzendes Fach"
    a_3 "Freizeitprojekt oder eine AG"
    a_4 "Andere Unterrichtsform"

    q_5 "Kursumfang in Schulstunden"
    a_1 :string

  end

  section "Programmiersprachen" do
    q_1 "Welche Programmiersprachen werden verwendet?", :pick => :any
    a_1 "Java"
    a_2 "Delphi"
    a_3 "Python"
    a_4 "Ruby"
    a_5 "C"
    a_6 "C++"
    a_7 "Perl"
    a_8 "Eine andere Sprache:", :string

    grid "Kenntnisse in den gewähten Programmiersprachen" do
     a "gering"
     a "mittel"
     a "gute"
     q "Lehrkraft", :pick => :one
     q "Schüler", :pick => :one
    end    
  end

  section "Bisherige Teilnahmen" do
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
   
   
    q_2a "Wurde zum Wettkampf ein funktionsfähiger Client eingesendet?", :pick => :one
    a "Ja"
    a "Nein"
    dependency :rule => "A"
    condition_A :q_0, "==", :a_1

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
   
  end

  section "Betreuungsbedarf" do

    q_1 "Wir gehen davon aus, dass...", :pick => :one
    a "wir keine weitere Hilfe benötigen."
    a "wir zur Unterstützung einen direkten Ansprechpartner benötigen"

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
