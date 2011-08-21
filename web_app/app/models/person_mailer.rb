class PersonMailer < ActionMailer::Base

  def signup_notification(person, context, plain_password, self_created = false, url = nil)
    unless url
      if context.is_a?(Contest)
        url =  "http://www.software-challenge.de/wettbewerb/#{context.subdomain}/"
      else 
        url =  "http://www.software-challenge.de/saison/#{context.id}/"
      end
    end

    recipients "#{person.name} <#{person.email}>"
    from       "software-challenge@gfxpro.eu"
    subject    "Willkommen auf der Wettbewerbsseite zur Software Challenge"
    sent_on    Time.now
    body({ :person => person, :url => url, :plain_password => plain_password, :self_created => self_created, :context => context })
  end

  def password_reset_notification(person, context, plain_password)
    if context.is_a?(Contest)
      url =  "http://www.software-challenge.de/wettbewerb/#{context.subdomain}/"
    else 
      url =  "http://www.software-challenge.de/saison/#{context.id}/"
    end
    
    recipients "#{person.name} <#{person.email}>"
    from       "software-challenge@gfxpro.eu"
    subject    "Ihr Passwort zur Wettbewerbsseite der Software Challenge wurde neu gesetzt"
    sent_on    Time.now
    body({ :person => person, :url => url, :plain_password => plain_password })
  end

end
