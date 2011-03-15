module PreliminaryContestantsHelper

  def show_probability(prob)
    img = case prob
      when "Sicher", "Wahrscheinlich"
        "ok"
      when "Vielleicht"
        "info"
      else 
        "warning"
    end

    image_tag("state/#{img}.png", :title => prob, :class => "tooltipped")
  end
end
