survey "Software Challenge 2012 Voranmeldung - Stufe 2", {:access_code => "2012_recall", :description => "Eine Umfrage welche sich an die vorangemeldeten Teams der Software-Challenge 2012 wendet. Dabei werden Informationen über den Kurs, Programmiersprachen usw. erhoben."}  do

  section "Angaben zum teilnehmenden Team" do
    
    label "Hinweis: Sollten Sie noch keine Teams angemeldet haben, tun Sie die bitte entsprechend Ihrer Wünsche"

    q_1 "Anzahl der Teilnehmer"
    a_1 :string

    q_2 "Jahrgangsstufe der Teilnehmer"
    a_1 :string

    q_3 "Wie viele Schuljahre haben die Schüler schon Informatikunterricht?"
    a_1 :integer
  end

  section "Programmiersprachen" do
    q_1 "Welche Programmiersprachen werden verwendet?", :pick => :one
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
     a "gut"
     q "Lehrkraft", :pick => :one
     q "Schüler", :pick => :one
    end    
  end

  section "Bisherige Teilnahmen" do
    q_0 "Haben Lehrkraft oder Schüler schon einmal an der Software-Challenge teilgenommen?", :pick => :one
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

  end

  section "Betreuungsbedarf" do

    q_1 "Wir benötigen vorraussichtlich außer Dokumentation, Forum und Wiki", :pick => :one
    a "keine weitere Hilfe."
    a "zusätzlich einen direkten Ansprechpartner."

  end

  section "Zum Schluss" do
    
    q_1 "Wie sind sie auf die Software-Challenge aufmerksam geworden?", :pick => :any
    a_1 "Bildungsserver (ggfs. welcher)", :string
    a_2 "Schulverwaltungsblatt(ggfs. welches)", :string
    a_3 "Zeitung (ggfs. welche)", :string
    a_4 "Freunde"
    a_5 "E-Mail des Institut für Informatik"
    a_6 "CEBIT"
    a_7 "Sonstiges"

    #q_1a "Beschreiben Sie genauer, wie Sie auf die Software-Challenge aufmerksam geworden sind", :custom_class => "textarea"
    #a :text
    #dependency :rule => "A"
    #condition_A :q_1, "==", :a_7
   
  end

end
