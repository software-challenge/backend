class PersonMailer < ActionMailer::Base

  def signup_notification(person, contest, plain_password, self_created = false)
    recipients "#{person.name} <#{person.email}>"
    from       "software-challenge@gfxpro.eu"
    subject    "Willkommen auf der Wettbewerbsseite zur Software Challenge"
    sent_on    Time.now
    body({ :person => person, :url => "http://www.software-challenge.de/wettbewerb/#{contest.subdomain}/", :plain_password => plain_password, :self_created => self_created, :contest => contest })
  end

  def password_reset_notification(person, contest, plain_password)
    recipients "#{person.name} <#{person.email}>"
    from       "software-challenge@gfxpro.eu"
    subject    "Ihr Passwort zur Wettbewerbsseite der Software Challenge wurde neu gesetzt"
    sent_on    Time.now
    body({ :person => person, :url => "http://www.software-challenge.de/wettbewerb/#{contest.subdomain}/", :plain_password => plain_password })
  end

end
