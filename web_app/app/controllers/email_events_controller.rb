class EmailEventsController < ApplicationController

  protected

  before_filter :fetch_person, :fetch_contestant, :fetch_context

  access_control do
    allow :administrator

    actions :edit, :update, :create do
      allow :administrator
      allow logged_in, :if => :same_person
    end
  end

  def same_person(as = nil)
    if as.nil?
      current_user == @person
    else
      current_user == as
    end
  end
  helper_method :same_person

  def fetch_person
    unless params[:person_id].nil?
      @person = Person.find(params[:person_id])
    else
      @person = nil
    end
  end 

  def fetch_contestant
    unless params[:contestant_id].nil?
      @contestant = Contestant.find(params[:contestant_id])
    else
      @contestant = nil
    end
  end
 
  def clean_params
    cleaned_up = params[:email_event].clone
    unless administrator?
      cleaned_up.delete :rcv_on_matchday_played
      cleaned_up.delete :rcv_survey_token_notification
    end
    cleaned_up
  end

  public

  def edit
    redirect_to :action => 'index' if @person.nil?
    @person.build_email_event if @person.email_event.nil?
    @email_event = @person.email_event
  end
  
  def create
    redirect_to :action => 'index' if @person.nil?
    unless @person.email_event.nil?
      update
      return
    end
    @email_event = EmailEvent.new(clean_params)
    success = false
    EmailEvent.transaction do
      success = @email_event.save!
      @person.email_event = @email_event 
      success = success and @person.save!
      @person.reload
    end
    respond_to do |format|
      if success
        flash[:notice] = t("messages.email_notification_settings_updated")
        format.html do 
          if @contestant.nil?
            redirect_to edit_contest_person_email_event_url(@contest, @person)
          else
            redirect_to edit_contest_contestant_person_email_event_url(@contest, @contestant, @person)
          end
        end
        format.xml { render :xml => @email_event, :status => :created, :location => @email_event }
      else
        format.html { render :action => "edit" }
        format.xml { render :xml => @email_event.errors, :status => :unprocessable_entity }
      end
    end
  end  

  def update
    redirect_to :action => 'index' if @person.nil?
    @email_event = @person.email_event

    events_params = clean_params
    success = @email_event.update_attributes(events_params)

    respond_to do |format|
      if success
        flash[:notice] = t("messages.email_notification_settings_updated")
        format.html do
          if @contestant.nil?
            redirect_to edit_contest_person_email_event_url(@contest, @person)
          else
            redirect_to edit_contest_contestant_person_email_event_url(@contest, @contestant, @person)
          end
        end
        format.xml { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml { render :xml => @email_event.errors, :status => :unprocessable_entity }
      end
    end
  end

  def fetch_context
    @context = @contest ? @contest : @season
  end
end
