module ContestsHelper
  def direction_text(direction)
    case direction
    when "asc"
      I18n.t("helpers.less_is_better")
    when "desc"
      I18n.t("helpers.more_is_better")
    when "none"
      I18n.t("helpers.irrelevant")
    else
      raise "unknown direction #{direction}"
    end
  end
end
