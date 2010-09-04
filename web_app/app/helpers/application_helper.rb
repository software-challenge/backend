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

  def tooltip_init 
    concat(
      javascript_tag do
      '$(function() {
        $(".tooltip").each(function() {
          $(this).bind(\'mouseenter\', function() {
            $(".tooltip_text", $(this)).fadeIn(250)
          })
              
          $(".tooltip").bind(\'mouseleave\', function() {
            $(".tooltip_text", $(this)).fadeOut(250)
          })
        })
      })'
      end
    )
  end

  def tooltip(text, options = {}, &block)
    inline = (not options[:inline].nil? and options[:inline])
    if block_given?
      content = capture(&block)
      html = "<div style=\"#{(inline ? "display: inline" : "")}\" class=\"tooltip\">"
      html += text
      html += "<div class=\"tooltip_text\" style=\"position: absolute; background-color: #DDDDFF; border: 1px solid; padding 5px; display: none\">"
      html += content
      html += "</div></div>"
      concat html.html_safe!
    end
  end

  def logfile_folder(match)
    case match.type.to_s
    when "LegaueMatch", "FinaleMatch"
      "match"
    when "CustomMatch"
      "custom"
    when "FriendlyMatch"
      "friendly"
    else
      "unknown"
    end
  end

  def event_text(event)
    case event.type
    when "ClientActivatedEvent"
      link_to "#{event.contestant.name} #{I18n.t("events.client_activated")}", contest_contestant_clients_url(@contest, event.contestant)
    when "ClientUploadedEvent"
      link_to "#{event.contestant.name} #{I18n.t("events.client_uploaded")}", contest_contestant_clients_url(@contest, event.contestant)
    end
  end
end
