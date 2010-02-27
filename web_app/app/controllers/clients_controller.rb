class ClientsController < ApplicationController
  # skip_before_filter :verify_authenticity_token

  before_filter :load_contestant
  access_control do
    default :deny
    allow :administrator
    allow :pupil, :tutor, :teacher, :of => :contestant
  end
  access_control :helper => :may_delete_client? do
    allow :administrator
    allow :pupil, :tutor, :teacher, :of => :contestant
  end

  def index
    @clients = @contestant.clients.all(:order => "created_at DESC")

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @clients }
    end
  end

  def show
    @client = @contestant.clients.find(params[:id])

    respond_to do |format|
      format.html { redirect_to contest_contestant_clients_url }
      format.xml  { render :xml => @client }
    end
  end

  def new
    @client = @contestant.clients.build

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @client }
    end
  end

  def edit
    @client = @contestant.clients.find(params[:id])
    @entries = @client.file_entries.with_level(0).file_ordering
  end

  def create
    @client = @contestant.clients.build(params[:client])
    @client.author = current_user

    success = @client.save
    if success
      begin
        @client.build_index!
      rescue => e
        @client.destroy
        @client = @contestant.clients.build(params[:client])
        @client.errors.add :file, :invalid_zip
        success = false
      end
    end

    if requested_by_flash?
      # flash will only handle 200 as a valid response
      render :nothing => true, :status => (success ? :ok : :unprocessable_entity )
    else
      respond_to do |format|
        if success
          format.html { redirect_to contest_contestant_clients_url(@contestant) }
          format.xml  { render :xml => @client, :status => :created, :location => @client }
        else
          format.html { render :action => "new" }
          format.xml  { render :xml => @client.errors, :status => :unprocessable_entity }
        end
      end
    end
  end

  def update
    @client = @contestant.clients.find(params[:id])

    respond_to do |format|
      if @client.update_attributes(params[:client])
        flash[:notice] = 'Client was successfully updated.'
        format.html { redirect_to contest_contestant_clients_url(@client.contestant) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @client.errors, :status => :unprocessable_entity }
      end
    end
  end

  def destroy
    raise "not supported"

    @client = @contestant.clients.find(params[:id])
    @client.destroy

    respond_to do |format|
      format.html { redirect_to(clients_url) }
      format.xml  { head :ok }
    end
  end

  def test
    @client = @contestant.clients.find(params[:id])

    if @client.tested?
      flash[:notice] = I18n.t("messages.client_has_already_been_tested") 
    else
      @client.test_delayed!
    end

    redirect_to contest_contestant_clients_url(@contestant)
  end

  def browse
    @client = @contestant.clients.find(params[:id])

    if params[:entry_id]
      @parent = @client.file_entries.find(params[:entry_id])
      @entries = @parent.children.file_ordering
    else
      @entries = @client.file_entries.with_level(0).file_ordering
    end

    render :update do |page|
      # TODO: check if empty
      page.replace_html '#fileList',
        :partial => "file_entries",
        :locals => { :client => @client }
    end
  end

  def select_main
    @client = @contestant.clients.find(params[:id])
    @main_entry = @client.file_entries.find(params[:main_id])

    @client.main_file_entry = @main_entry
    @client.save!

    redirect_to contest_contestant_clients_url(@contestant)
  end

  def select
    @client = @contestant.clients.find(params[:id])

    @contestant.current_client = @client
    @contestant.save!

    redirect_to contest_contestant_clients_url(@contestant)
  end

  def status
    @client = @contestant.clients.find(params[:client_id])

    render :update do |page|
      page.replace "client-#{@client.id}",
        :partial => "client", :locals => { :client => @client, :render_comments => false }

      if @contestant.has_running_tests?
        page << %{$('.testActions .disabled').show();}
        page << %{$('.testActions .enabled').hide();}
      else
        page << %{$('.testActions .enabled').show();}
        page << %{$('.testActions .disabled').hide();}
      end

      page << %{update_comment_icon(} + @client.id.to_s + ");"
    end
  end

  def hide
    @client = @contestant.clients.find(params[:id])
    generic_hide(@client, :file_file_name)
  end

  def get_comments
    @client = Client.find(params[:client_id].to_i)
    @comments = @client.comments.all(:order => "created_at DESC")
    render :partial => "client_comment_list"
  end

  def create_comment
    comment = Comment.new
    person = Person.find(params["person_id"].to_i)
    client = Client.find(params["client_id"].to_i)
    comment.person = person
    comment.text = params["text"]
    client.comments << comment
    client.save!
    render :text => ""
  end

  def delete_comment
      if current_user.has_role? :administrator
        comment = Comment.find(params["comment_id"].to_i)
        if comment.destroy
          render :text => "true"
        else
          render :text => "false"
        end
      else
        render :text => "false"
      end
  end

  def get_logs
    #path = RAILS_ROOT + "/clientlogs/" + params[:id] + "_"
    path = "/home/scadmin/clientlogs/" + params[:id] + "_"
    number = 0
    logfiles = []
    while File.exists?(path + number.to_s + ".log")
      logfiles << (path + number.to_s + ".log")
      number += 1
    end
    render :partial => "clientlogs", :locals => {:id => params[:id], :logfiles => logfiles}
  end

  def send_log
    send_file(params[:file], :type => 'text', :stream => "false", :disposition => "attachment")
  end

  protected

  def load_contestant
    @contestant = current_contest.contestants.find(params[:contestant_id])
  end

  def requested_by_flash?
    request.env['HTTP_USER_AGENT'] =~ /^(Adobe|Shockwave) Flash/
  end
end
