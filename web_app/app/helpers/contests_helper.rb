module ContestsHelper
  def direction_text(direction)
    case direction
    when "asc"
      "Weniger ist besser"
    when "desc"
      "Mehr ist besser"
    when "none"
      "Irrelevant"
    else
      raise "unknown direction #{direction}"
    end
  end
end
