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

  def action_list(&block)
    #instance_eval(&block)
    @has_actions = false
    content = capture(&block) 
    puts content
    if @has_actions
      concat content.html_safe
    end
  end

  def action(cond, &block)
    if cond
      @has_actions = true
      html = "<li>\n"
      html += capture(&block)
      html += "</li>\n"
      concat html.html_safe
    end
  end

end
