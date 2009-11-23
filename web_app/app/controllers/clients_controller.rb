require 'zip/zip'

class ClientsController < ApplicationController

  # GET /clients
  # GET /clients.xml
  def index
    @contestant = Contestant.find(params[:contestant_id])
    @clients = @contestant.clients.all(:order => "created_at DESC")
    @school = @contestant

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @clients }
    end
  end

  # GET /clients/1
  # GET /clients/1.xml
  def show
    @client = Client.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @client }
    end
  end

  # GET /clients/new
  # GET /clients/new.xml
  def new
    @contestant = Contestant.find(params[:contestant_id])
    @client = @contestant.clients.build

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @client }
    end
  end

  # GET /clients/1/edit
  def edit
    @contestant = Contestant.find(params[:contestant_id])
    @client = @contestant.clients.find(params[:id])
  end

  # POST /clients
  # POST /clients.xml
  def create
    # TODO: http://jimneath.org/2008/05/15/swfupload-paperclip-and-ruby-on-rails/

    @contestant = Contestant.find(params[:contestant_id])
    @client = @contestant.clients.build(params[:client])
    @client.author = current_user

    respond_to do |format|
      if @client.save
        begin
          @client.build_index!
          flash[:notice] = 'Client was successfully created.'
        rescue
          @client.destroy
          flash[:notice] = "Couldn't process ZIP file."
        end
        
        format.html { redirect_to contestant_clients_url(@contestant) }
        format.xml  { render :xml => @client, :status => :created, :location => @client }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @client.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /clients/1
  # PUT /clients/1.xml
  def update
    @client = Client.find(params[:id])

    respond_to do |format|
      if @client.update_attributes(params[:client])
        flash[:notice] = 'Client was successfully updated.'
        format.html { redirect_to(@client) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @client.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /clients/1
  # DELETE /clients/1.xml
  def destroy
    @client = Client.find(params[:id])
    @client.destroy

    respond_to do |format|
      format.html { redirect_to(clients_url) }
      format.xml  { head :ok }
    end
  end

  def browse
    @contestant = Contestant.find(params[:contestant_id])
    @client = @contestant.clients.find(params[:id])

    if params[:entry_id]
      @entry = @client.file_entries.find(params[:entry_id])
      @entries = @entry.children
    else
      @entries = @client.file_entries.with_level(0)
    end

    render :update do |page|
      # todo empty-check
      page.replace_html '#fileList',
        :partial => "file_entry",
        :collection => @entries,
        :locals => { :client => @client }
    end
  end
end