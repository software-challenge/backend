class TicketsController < ApplicationController

  before_filter :fetch_ticket, :only => [:update, :show, :attachments, :attachment, :add_comment, :upload_attachment]
  
  access_control do
    action :index, :create, :new, :possible_assignees do
      allow :teacher, :pupil, :if => :allowed_for_api?
    end

    action :update, :show, :attachments, :add_comment, :upload_attachment do
      allow logged_in, :if => :allowed_for_ticket?
    end 

    action :attachment do 
      allow logged_in, :if => :allowed_for_ticket?
      allow :tutor, :helper, :administrator
    end
  end

  before_filter :fetch_possible_contexts, :except => [:possible_assignees]
  
  # set the current host url to a constant
  before_filter :set_host_url, :only => [:create, :update, :upload_attachment]

  # if the current_user has no api user, create one!
  before_filter :create_api_user, :except => [:possible_assignees]

  def index
    @tickets = Quassum::Ticket.all_for_person(@current_user).sort{|a,b| a.priority <=> b.priority}
  end

  def new
    @ticket = Quassum::Ticket.new 
  end

  def show
    ticket = Quassum::Ticket.find(params[:id])
    @ticket = ticket if administrator? or ticket.contexts.any?{|c| @current_user.has_role_for? c}
  end

  def update
    if @ticket.update_attributes(params[:ticket])
      Quassum::Ticket.changed!(@ticket.id)
      respond_to do |format|
        format.html {
          flash[:notice] = "Ticket wurde erfolgreich bearbeitet"
          redirect_to :action => :show
        }
        format.js {
          render :file => "tickets/update_ticket_form.js.erb"
        }
      end
    else
      respond_to do |format|
        format.html {
          flash[:error] = "Beim Bearbeiten des Tickets trat ein Fehler auf!"
          render :action => :show
        }
        format.js {
          render :json => ["Beim Bearbeiten des Tickets trat ein Fehler auf!"] + @ticket.errors.full_messages, :status => 500 
        }
      end
    end
  end

  def create
    @ticket = Quassum::Ticket.create(params[:ticket])
    context_type, context_id = params[:context].split(":")
    if @possible_contexts[context_type] and @possible_contexts[context_type].include?(context_id.to_i)
      @context = Quassum::TicketContext.create(:context_id => context_id, :context_type => context_type, :ticket_id => @ticket.id) 
    else
      @context = nil
    end
    if @ticket.save and @context.save
      Quassum::Ticket.changed!(@ticket.id)
      respond_to do |format|
        format.html {
          flash[:notice] = "Ticket wurde erfolgreich bearbeitet"
          redirect_to :action => :show
        }
        format.js {
          render :text => "Ticket created successfully"
        }
      end
    else
      respond_to do |format|
        format.html {
          flash[:error] = "Beim Bearbeiten des Tickets trat ein Fehler auf!"
          render :action => :new
        }
        format.js {
          render :json => ["Beim Bearbeiten des Tickets trat ein Fehler auf!"] + @ticket.errors.full_messages, :status => 500 
        }
      end
    end
  end

  def add_comment
    if @ticket and @ticket.create_comment(params[:comment][:text], @current_user.api_user)
      Quassum::Ticket.comment_changed!(@ticket.id)
      respond_to do |format|
        format.html { redirect_to ticket_url(:id => @ticket.id) }
        format.js { render :partial => "add_comment" }
      end
    else
      respond_to do |format|
        format.html { render :action => :show }
        format.js { render :text => "error" } 
      end
    end
  end

  def attachments 
    respond_to do |format|
      format.json { render :json => [] }
      format.html
    end
  end

  def attachment
    @attachment = Quassum::TicketAttachment.find_by_id(params[:attachment_id])
    if @attachment.ticket_id == params[:id].to_i
      send_file(@attachment.file.path, :type => @attachment.file.content_type)
    else 
      redirect_to :action => :show
    end
  end

  def upload_attachment
    for file in params[:files]
      att = Quassum::TicketAttachment.create(:file => file, :ticket_id => params[:id]) 
      atts = []
      if att.save
        atts << att 
      else
        render :json => { :result => "error #{att.errors.full_messages}" }, :content_type => 'text/html'
        return
      end
    end
    render :json => atts.map{|a| { :url => attachment_ticket_url(:id => params[:id], :attachment_id => a.id), :name => a.file_file_name, :size => a.file.size }}, :content_type => 'text/html'
  end

  def possible_assignees
    context_type, context_id = params[:context].split(":")
    allowed_types = ["Contestant"]
    if allowed_types.include?(context_type)
      context = eval(context_type).find_by_id(context_id)
    end
    if context and not @current_user.roles.for(context).empty?
      render :partial => "select_assignee", :locals => {:assignees => possible_assignees_for(context)}
    else
      render :text => "Could not find Context", :code => 500
    end
  end

protected

  def possible_assignees_for(context)
    if context.is_a? Contestant
      context.people.map{|p| ["#{p.name} (#{p.roles.for(context).map{|r| r.to_s}.uniq.join(", ")})", "Person:#{p.id}"]}
    else
      []
    end
  end
  def fetch_possible_contexts
    @possible_contexts = {}
    @possible_contexts['Contestant'] = @current_user.contestants.visible.select{|c| @current_user.has_role?("pupil",c) or @current_user.has_role?("teacher",c)}.map{|c| c.id}
  end

  def allowed_for_ticket?
    if @ticket
      @ticket.contexts.any?{|c| @current_user.has_role?("teacher",c.context) or @current_user.has_role?("pupil",c.context)}
    else 
      false
    end
  end

  def fetch_ticket
    @ticket = Quassum::Ticket.find(params[:id]) if params[:id]
  end

  def allowed_for_api?
    (@current_user.api_user and not @current_user.api_user.direct_user?) or (Quassum::ApiUser.possible_api_user?(@current_user))
  end

  def create_api_user
    unless @current_user.api_user
      @api_user = Quassum::ApiUser.new
      @api_user.person = @current_user
      @api_user.api_username = @current_user.name
      @api_user.save!
    end
  end

  def set_host_url
    QUASSUM[:webapp] = request.host_with_port
  end
end
