module SurveyTokensHelper
  def token_receiver_tooltip(token)
     text = content_tag :b, token.token_owner.class.human_name
     if token.token_owner.is_a? Person
       text += ":<br> E-Mail: "
       text += token.token_owner.email
       text += "<br>Zuletzt online: #{I18n.l token.token_owner.last_seen.to_date}" if token.token_owner.last_seen 
     elsif token.token_owner.is_a? PreliminaryContestant
       text += "<b>-Voranmeldung:</b><br>Schule: "
       text += token.token_owner.school.name
       text += "<br>Bundesland: "
       text += token.token_owner.school.state
       text += "<br>Anmelder: "
       text += token.token_owner.person.name
     end
     text.html_safe
  end
end
