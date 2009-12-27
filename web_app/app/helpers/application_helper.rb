# Methods added to this helper will be available to all templates in the application.
module ApplicationHelper
  def jquery_tab(key, text, enabled = true)
    content_tag :li, :class => (enabled ? nil : "disabled") do
      link_to text, "##{key}"
    end
  end
end
