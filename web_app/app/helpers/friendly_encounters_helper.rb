module FriendlyEncountersHelper

def encounter_open_for_str(enc)
  (enc.open_for.nil? ? "Alle" : enc.open_for.name)
end

def encounter_status(enc, options = {})
  str = ""
  case enc.status
    when "open"
      str = "Offen"
    when "ready"
      str = "Bereit"
    when "rejected"
      str = "Abgelehnt"
    when "running"
      str = "Läuft gerade"
      if options[:spinner]
        str += image_tag "ui/spinner.gif", :style => "margin-left: 5px; vertical-align: middle" 
      end
    when "played"
      if options[:with_result]
        res = enc.main_result
        if options[:reverse]
          res.reverse!
        end
        str = link_to "Gespielt (#{res[0]} : #{res[1]})", contest_friendly_encounter_url(current_contest, enc)
      else
        str = "Gespielt"
      end
    else
      str = "Unbekannt"
  end
  str += hidden_field_tag "", enc.status, :class => "state"
  return str.html_safe! 
end

end
