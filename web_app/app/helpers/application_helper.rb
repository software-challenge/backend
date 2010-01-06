# Methods added to this helper will be available to all templates in the application.
module ApplicationHelper
  # enable Acl9 helpers for helpers and views (show_to)
  include Acl9Helpers

  def jquery_tab(key, text, enabled = true)
    content_tag :li, :class => (enabled ? nil : "disabled") do
      link_to text, "##{key}"
    end
  end

  def associated_with_additional(existing, all, on_attribute = :id)
    result = ActiveSupport::OrderedHash.new

    all.each do |element|
      result[element.send(on_attribute)] = element
    end

    existing.each do |element|
      result[element.send(on_attribute)] = element
    end

    result.values
  end
end
