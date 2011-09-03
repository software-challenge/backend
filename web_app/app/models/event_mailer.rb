class NotSendingMail < StandardError; end

class EventMailer < ActionMailer::Base

  def self.method_missing(*args)
    super
    rescue NotSendingMail => e
    RAILS_DEFAULT_LOGGER.info("Not mailing! #{e}")
  end

  def contest_registration_phase_changed_notification(contest, school, changes)
    recipients school.contact.email
    from "software-challenge@gfxpro.eu"
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
    from "software-challenge@gfxpro.eu"
    subject "Client für kommenden Spieltag vorbereiten"
    sent_on Time.now
    body({ :matchday => matchday, :contestant => contestant, :mdstr => mdstr, :url => contest_contestant_clients_url(matchday.contest_id, contestant) })
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
    from "software-challenge@gfxpro.eu"
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
    from "software-challenge@gfxpro.eu"
    subject "Ein Spieltag wurde gespielt"
    sent_on Time.now
    body({ :matchday => matchday, :mdstr => mdstr, :url => contest_matchday_url(matchday.contest, matchday), :disqualis => disqualis })   
  end


  def survey_invite_notification(person, context, login_token, survey_tokens, email_template = nil)
    survey_tokens = [survey_tokens] unless survey_tokens.is_a? Array
    recipients "#{person.name} <#{person.email}>"
    from       "software-challenge@gfxpro.eu"
    subject    (email_template ? email_template.email_title : (survey_tokens.count == 1 ? "Die Software-Challenge läd Sie zu einer Umfrage ein." : "Die Software-Challenge läd Sie zu #{survey_tokens.count} Umfragen ein."))
    sent_on    Time.now
    body({:context => context, :person => person, :survey => survey_tokens.first.survey, :survey_tokens => survey_tokens, :login_token => login_token, :email_template => email_template })
  end

  def custom_email(title,text,people)
    email_adds = people.compact.uniq.map{|p| "\"#{p.name.strip}\" <#{p.email.strip}>"}.uniq
    from      "software-challenge@gfxpro.eu"
    subject   title
    sent_on   Time.now
    bcc       email_adds.join(", ").strip
    body({:text => text})
  end

  def ticket_or_comment_changed_notification(ticket)
    people = ticket.contexts.map{|c| c.context.people.pupils + c.context.people.teachers}.flatten.uniq.select{|p| p.email_event.rcv_quassum_notification}.compact
    people.delete ActiveRecord::Base.current_user

    email_adds = people.map{|p| "\"#{p.name.strip}\" <#{p.email.strip}>"}.uniq
    from      "software-challenge@gfxpro.eu"
    subject   "[Software-Challenge] Ticket bearbeitet: #{ticket.title}"
    sent_on   Time.now
    bcc       email_adds.join(", ").strip
    body({:ticket => ticket})
  end

end
