module TicketsHelper
  def show_maybe_tag(a = nil, &block)
    html = "<div class='maybe_show'>"
    html << a if a
    html << capture(&block) if block
    html << "</div>"
    html.html_safe
  end

  def maybe_text_field(name,value=nil,args={})
    html = "<div class='maybe'>"
    html << "<div class='field'>"
    html << text_field_tag(name,value,args)
    html << "</div>"
    html << "<div class='show'>"
    html << value.to_s
    html << "</div></div>"
    html.html_safe 
  end

  def status_for_ticket(ticket)
    tooltip = t("views.quassum.ticket.states.#{ticket.status}")
    pic = case ticket.status
            when "suggested"
              "state/info.png"
            when "open"
              "state/open.png"
            when "done"
              "state/ok.png"
            else 
              "state/warning.png"
            end
    image_tag(pic, :class => "tooltipped", :title => "Status: #{tooltip}")
  end

  def priority_for_ticket(ticket)
    number = case ticket.priority
             when "requirement"
               "+3"
             when "very_important"
               "+2"
             when "important"
               "+1"
             when "average"
               "0"
             when "less_important"
               "-1"
             when "unimportant"
               "-2"
             else 
               " "
            end
    "<span class='tooltipped' title='#{t("views.quassum.ticket.priorities.#{ticket.priority || "avarage"}")}'>#{number}</span>".html_safe
  end
    
end
