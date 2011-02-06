class WhitelistEntriesController < ApplicationController

  access_control do
    allow :administrator
  end

  before_filter :fetch_whitelist
  before_filter :fetch_whitelist_entry
  before_filter :fetch_whitelist_entries

  def new
    @whitelist_entry = WhitelistEntry.new
  end

  def index
  end

  def create
    begin 
      @whitelist.upload_file(params[:whitelist_entry])
      flash[:notice] = "Eintrag wurde erfolgreich hinzugefuegt!"
    rescue 
      flash[:error] = "Beim Hinzufügen des Eintrags trat ein Fehler auf!"
    end
    redirect_to :action => :index
  end

  def ajax_delete
    begin 
      @entry = WhitelistEntry.find_by_id(params[:id])
      @entry.delete
      respond_to do |format|
        format.js 
      end
    rescue
      flash[:error] = "Beim Entfernen des Eintrags trat ein Fehler auf!"
      redirect_to :action => :index
    end
 
  end

  def destroy
    begin 
      @entry = WhitelistEntry.find_by_id(params[:id])
      @entry.delete
      respond_to do |format|
        format.html {      
          flash[:notice] = "Eintrag wurde erfolgreich entfernt."
          redirect_to :action => :show }
      end
    rescue
      flash[:error] = "Beim Entfernen des Eintrags trat ein Fehler auf!"
      redirect_to :action => :index
    end
  end

  def reset
     WhitelistEntry.transaction do
       begin
         @whitelist_entries.each{|e| e.destroy}
         flash[:notice] = "Einträge wurden erfolgreich entfernt!"
       rescue
         flash[:error] = "Beim Entfernen der Einträge trat ein Fehler auf!"
       end
     end
     redirect_to :action => :index
   end

  def fetch_whitelist
    @whitelist = @contest.whitelist
    if @whitelist.nil?
      @whitelist = Whitelist.new
      @whitelist.contest = @contest
      @whitelist.save!
    end
  end

  def fetch_whitelist_entry
    @whitelist_entry = WhitelistEntry.find_by_id(params[:id])
  end

  def fetch_whitelist_entries
    @whitelist_entries = @whitelist.entries
  end
end
