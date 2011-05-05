class SurveyTokensController < ApplicationController

 before_filter :fetch_token, :only => [:edit, :update, :destroy, :show]

 access_control do
   
   action :index do
     allow logged_in
   end

   action :show do
     allow :administrator
     allow logged_in, :if => :token_allowed_for_user
   end

   action :new, :create, :update, :destroy do
     allow :administrator
   end

 end

 def fetch_token
  @token = SurveyToken.find_by_id(params[:id])
 end
  
 def token_allowed_for_user
  @token.allowed_for @current_user
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
   @contest.schools.sort_by(&:name).each do |school|
    unless school.preliminary_contestants.empty?
      school.preliminary_contestants.each{|p| @prelims << ["#{school.name} - #{p.name}", "preliminary_contestant|#{p.id}"]}
    else
      @prelims << ["#{school.name} - Keine Teams!", "school|#{school.id}"]
    end
   end
 end

 def destroy
   @token.destroy
   redirect_to :action => :index
 end

 def create
  begin
   token_count = 0
   SurveyToken.transaction do
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
          redirect_url = surveys_contest_school_url(@contest,school)
          token = SurveyToken.create(args)
          token.token_owner = school
          token.finished_redirect_url = redirect_url 
          token.save!
          token_count += 1
          people[school.person] = [] if not people[school.person]
          people[school.person] << token
        end
        @prelims.each do |prelim|
           redirect_url = surveys_contest_school_url(@contest,prelim.school)
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
        EventMailer.deliver_survey_invite_notification(person,@contest,person.generate_login_token, tokens) if params[:send_email_notification] == "1"
      end
    flash[:notice] = "Es wurden erfolgreich #{token_count} Tokens erstellt!"
    redirect_to :actions => :index
   end
  rescue
    flash[:error] = "Fehler beim Erstellen der Tokens!"
    redirect_to :action => :new
  end
 end
end
