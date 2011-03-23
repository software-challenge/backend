class NotSendingMail < StandardError; end

class EventMailer < ActionMailer::Base

  def self.method_missing(*args)
    super
    rescue NotSendingMail => e
    RAILS_DEFAULT_LOGGER.info("Not mailing! #{e}")
  end

  def contest_registration_phase_changed_notification(contest, school, changes)
    recipients school.contact.email
    from "software-challenge@gfxpro.de"
    subject "Änderung der Anmeldungsoptionen"
    sent_on Time.now
    body({:contest => contest, :school => school, :changes => changes})
  end

  def client_matchday_warning_notification(contestant, matchday)
    recips = ""
    rcv = EmailEvent.rcvs_client_matchday_warning.collect(&:email)
    contestant.memberships.each do |ms|
      if rcv.include?(ms.person.email)
        recips << ", " unless recips.blank?
        recips << "#{ms.person.email}"
      end
    end
    raise NotSendingMail if recips.blank?  
    if matchday.trial
      mdstr = "#{matchday.position}. Probespieltag"
    else
      mdstr = "#{matchday.position - matchday.contest.matchdays.trials.count}. Spieltag"
    end
    bcc recips
    recipients "software-challenge@gfxpro.de"
    from "software-challenge@gfxpro.de"
    subject "Client für kommenden Spieltag vorbereiten"
    sent_on Time.now
    body({ :matchday => matchday, :contestant => contestant, :mdstr => mdstr, :url => contest_contestant_clients_url(contestant.contest_id, contestant) })
  end
  
  def on_matchday_played_notification(matchday)
    recips = ""
    EmailEvent.rcvs_on_matchday_played.select{|e| e.person and e.person.has_role? :administrator}.collect(&:email).each do |rcp|
      recips << ", " unless recips.blank?
      recips << "#{rcp}"
    end
    raise NotSendingMail if recips.blank? or not matchday.played?
    if matchday.trial
      mdstr = "#{matchday.position}. Probespieltag"
    else
      mdstr = "#{matchday.position - matchday.contest.matchdays.trials.count}. Spieltag"
    end
    disqualis = 0
    matchday.matches.each do |match|
      match.rounds.each do |round|
        round.scores.each do |score|
          disqualis += 1 if score.cause != "REGULAR"
        end
      end
    end
    bcc recips
    recipients "software-challenge@gfxpro.de"
    from "software-challenge@gfxpro.de"
    subject "Ein Spieltag wurde gespielt"
    sent_on Time.now
    body({ :matchday => matchday, :mdstr => mdstr, :url => contest_matchday_url(matchday.contest, matchday), :disqualis => disqualis })
  end

  def on_matchday_published_notification(matchday)
    recips = ""
    EmailEvent.rcvs_on_matchday_published.select{|e| e.person.has_memberships_in?(matchday.contest) or e.person.has_role? :administrator}.collect(&:email).each do |rcp|
      recips << ", " unless recips.blank?
      recips << "#{rcp}"
    end
    raise NotSendingMail if recips.blank? or not matchday.published?
    if matchday.trial
      mdstr = "#{matchday.position}. Probespieltag"
    else
      mdstr = "#{matchday.position - matchday.contest.matchdays.trials.count}. Spieltag"
    end
    disqualis = 0
    matchday.matches.each do |match|
      match.rounds.each do |round|
        round.scores.each do |score|
          disqualis += 1 if score.cause != "REGULAR"
        end
      end
    end
    bcc recips
    recipients "software-challenge@gfxpro.de"
    from "software-challenge@gfxpro.de"
    subject "Ein Spieltag wurde gespielt"
    sent_on Time.now
    body({ :matchday => matchday, :mdstr => mdstr, :url => contest_matchday_url(matchday.contest, matchday), :disqualis => disqualis })   
  end


  def survey_invite_notification(person, contest, login_token, survey_token)
    recipients "#{person.name} #<#{person.email}>"
    from       "software-challenge@gfxpro.eu"
    subject    "Die Software-Challenge läd Sie zu einer Umfrage ein."
    sent_on    Time.now
    body({:contest => contest, :person => person, :survey => survey_token.survey, :survey_token => survey_token, :login_token => login_token })
  end
end
