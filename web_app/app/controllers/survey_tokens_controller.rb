class SurveyTokensController < ApplicationController

 before_filter :fetch_context
 before_filter :fetch_token, :only => [:edit, :update, :destroy, :show]

 access_control do

   action :index do
     allow logged_in
   end

   action :show do
     allow :administrator
     allow logged_in, :if => :token_allowed_for_user
   end

   action :new, :create, :update, :destroy, :preview_template do
     allow :administrator
   end

 end

 def fetch_token
  @token = SurveyToken.find_by_id(params[:id])
 end
  
 def token_allowed_for_user
    @token and @token.allowed_for?(@current_user)
 end
 
 def index
   @tokens = SurveyToken.for_person(@current_user)
 end

 def show
  
 end

 def new
   @token = SurveyToken.new
   @surveys = Survey.all
   @prelims = []
   if @context.is_a? Season
     @season.schools.sort_by(&:name).each do |school|
      unless school.preliminary_contestants.empty?
        school.preliminary_contestants.each{|p| @prelims << ["#{school.name} - #{p.name}", "preliminary_contestant|#{p.id}"]}
      else
        @prelims << ["#{school.name} - Keine Teams!", "school|#{school.id}"]
      end
     end
     @prelims.sort!{|a,b| a[0] <=> b[0]} unless @prelims.empty?
   elsif @context.is_a? Contest
     @prelims = []
   end
 end

 def destroy
   @token.destroy
   redirect_to :action => :index
 end

 def create
  #begin
   token_count = 0
   #SurveyToken.transaction do
    people = {}
    args = {:survey => Survey.find_by_id(params[:survey])}
    args[:valid_from] = begin params[:valid_from].to_date rescue Time.now end 
    args[:valid_until] = begin params[:use_valid_until] ? params[:valid_until].to_date : nil rescue nil end
    case params[:token_owner_group]
      when "people"
        (params[:select_people] || []).each do |person_id|
         person = Person.find_by_id(person_id)
         token = SurveyToken.create(args)
         token.token_owner = person
         token.allow_teacher = false
         token.save!
         token_count += 1
         people[person] = [token]
        end
      when "preliminary"
        @schools = []
        @prelims = []
        params[:select_preliminaries].each do |p|
          id = p.split("|")[1]
          if p.include? "school"
            @schools << School.find_by_id(id)
          else
            @prelims << PreliminaryContestant.find_by_id(id)
          end
        end
        @schools.each do |school|
          redirect_url = url_for [:surveys, @context, school]
          token = SurveyToken.create(args)
          token.token_owner = school
          token.finished_redirect_url = redirect_url 
          token.save!
          token_count += 1
          people[school.person] = [] if not people[school.person]
          people[school.person] << token
        end
        @prelims.each do |prelim|
           redirect_url = url_for [:surveys, @context, prelim.school]
           token = SurveyToken.create(args)
           token.token_owner = prelim
           token.finished_redirect_url = redirect_url
           token.save!
           token_count += 1
           people[prelim.person] = [] if not people[prelim.person]
           people[prelim.person] << token
        end
      when "contestants"
        (params[:select_contestants] || []).each do |cont_id|
         contestant = Contestant.find_by_id(cont_id)
          contestant.people.uniq.each do |person|
            if (params[:allow_teacher] and person.has_role? "teacher", contestant) or (params[:allow_pupil] and person.has_role? "pupil", contestant) or (params[:allow_tutor] and person.has_role? "tutor")
              token = SurveyToken.create(args)
              token.allow_teacher = params[:allow_teacher] ? true : false
              token.allow_pupil = true if  params[:allow_pupil]
              token.allow_tutors = true if params[:allow_tutor]
              token.token_owner = person
              token.save!
              token_count += 1
              people[person] = [] if not people[person]
              people[person] << token
            end
          end
        end
      else
        raise "Unknown owner group!"
      end
      people.each do |person,tokens|
        if params[:send_email_notifications] == "1" and person.email_event.rcv_survey_token_notification
          if params[:custom_template] and params[:custom_template].length > 0  
            EventMailer.send("deliver_custom_survey_invite_notification_#{params[:custom_template]}",person,@context,person.generate_login_token, tokens, ((params[:custom_email_title] and not params[:custom_email_title].empty?) ? params[:custom_email_title] : nil))
          else 
            EventMailer.deliver_survey_invite_notification(person,@context,person.generate_login_token, tokens) 
          end
        end
      end
    flash[:notice] = "Es wurden erfolgreich #{token_count} Tokens erstellt!"
    redirect_to :actions => :index
   #end
  #rescue Exception => e
  #  logger.warn("ERROR while creating SurveyTokens: #{e}")
  #  flash[:error] = "Fehler beim Erstellen der Tokens!"
  #  redirect_to :action => :new
  #end
 end

 def preview_template
    @survey_tokens = SurveyToken.all 
    @login_token = LoginToken.new(:person => @current_user)
    render :inline => "<%= raw BlueCloth.new(render :file => 'event_mailer/custom_survey_invite_notification_#{params[:template]}.rhtml').to_html %>"
    @login_token.destroy
 end

  def fetch_context
    @context = @contest ? @contest : @season
  end
end
