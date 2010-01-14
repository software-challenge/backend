class PersonMailer < ActionMailer::Base

  def signup_notification(person, contest, plain_password)
    recipients "#{person.name} <#{person.email}>"
    from       "software-challenge@gfxpro.eu"
    subject    "Willkommen auf der Wettbewerbsseite zur Software Challenge"
    sent_on    Time.now
    body({ :person => person, :url => "http://#{contest.subdomain}.software-challenge.de", :plain_password => plain_password })
  end

end
