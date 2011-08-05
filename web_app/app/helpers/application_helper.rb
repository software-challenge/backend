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

  def action_button(options = {}, &block)
    content = capture(&block)
    html = "<div style=\"#{options[:style]}\" class=\"actions\">"
    html += content
    html += "</div>"
    concat html.html_safe
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

  def combobox(id, collection, options = {})
    width = options[:width] || "200"
    html = ""
    html << "<div id=\"#{id}_container\" style=\"position: relative;\">\n"
    unless options[:label].nil?
      html << label_tag(options[:label])
    end
    html << text_field_tag(id, "", :style => "width: #{width}px") + "\n"
    html << "<br>\n"
    html << "<div id=\"#{id}_select\" style=\"position: absolute; left: 0px; background-color: #fff; border: 1px solid #000; display: none; z-index: 50\">\n"
    collection.each do |item|
      html << "  <div class=\"combobox_item #{id}_item\">#{item}</div>\n"
    end
    html << "</div>\n"
    html << "</div>\n"
    html << "<script language=\"javascript\">\n"
    html << "var #{id}_positioned = false;\n"
    html << "function position_#{id}_box() {\n"
    html << "  if(!#{id}_positioned) {\n"
    html << "    var diff = $(\"##{id}\").offset().left - $(\"##{id}_select\").offset().left;\n"
    html << "    $(\"##{id}_select\").css('left', diff + \"px\");\n"
    html << "    #{id}_positioned = true;\n"
    html << "  }\n"
    html << "}\n"
    html << "$(function() {\n"
    html << "  $(\"div##{id}_select\").width($(\"#location_filter\").width())\n"
    html << "  $(\"input##{id}\").blur(function() {\n"
    html << "    if(!$(\"##{id}_select\").data('isover')) {\n"
    html << "      $(\"##{id}_select\").hide()\n"
    html << "    }\n"
    html << "  })\n"
    html << "  $(\".#{id}_item\").hover(function() { $(\"##{id}_select\").data('isover', 1); },\n"
    html << "    function() { $(\"##{id}_select\").data('isover', 0); })\n"
    html << "  $(\".#{id}_item\").bind('click', function() {\n"
    html << "    $(\"##{id}\").val($(this).html())\n"
    html << "    $(\"##{id}_select\").hide()\n"
    html << "  })\n"
    html << "  $(\"##{id}\").bind('dblclick', function() {\n"
    html << "    $(\"##{id}_select\").show()\n"
    html << "    position_#{id}_box();\n"
    html << "  })\n"
    html << "  $(\"##{id}\").keyup(function() {\n"
    html << "    var str = $(this).val()\n"
    html << "    $(\"div.#{id}_item\", $(\"##{id}_select\")).each(function(index) {\n"
    html << "      if($(this).html().match(new RegExp(str, \"i\")) != null) {\n"
    html << "        $(this).show()\n"
    html << "      } else {\n"
    html << "        $(this).hide()\n"
    html << "      }\n"
    html << "    })\n"
    html << "    $(\"##{id}_select\").show()\n"
    html << "    position_#{id}_box();\n"
    html << "  })\n"
    html << "})\n"
    html << "</script>\n"
    concat html.html_safe!
  end

  def replace_variables(arr)
    arr.each_with_index do |ele,i|
      if ele.is_a? Symbol
        arr[i] = eval "@#{ele.to_s}"
      end
    end
  end

  def list_item_for(object, attrib)
    list_item object.class.human_attribute_name(attrib.to_s), object.send(attrib)
  end

  def list_item(title, value)
    html = ""
    html << "<li>\n"
    html << "  <label>\n"
    html << "    #{title}:\n"
    html << "  </label>\n"
    html << "  <span>\n"
    html << "    #{value}\n"
    html << "  </span>\n"
    html << "</li>\n"
    return html.html_safe!
  end

  def update_my_survey_path
   contest_survey_token_survey_url(@contest,@survey_token,:id => 0)
  end

  def eurl_for(url_arr, args = {})
    url = url_for(url_arr)
    url += ".#{args[:format]}" if args[:format]
    url += "?#{args[:params].to_param}" if args[:params]
    url
  end

end
