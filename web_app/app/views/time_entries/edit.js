$("#entry_<%= @time_entry.id %>").hide().after("<%= escape_javascript( render :partial => "form", :locals => {:time_entry => @time_entry} ) %>")
