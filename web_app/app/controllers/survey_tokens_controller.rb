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
 end

 def destroy
   @token.destroy
   redirect_to :action => :index
 end

 def create
  begin
   token_count = 0
   SurveyToken.transaction do
    args = {:survey => Survey.find_by_id(params[:survey])}
    args[:valid_from] = begin params[:valid_from].to_date rescue Time.now end 
    args[:valid_until] = begin params[:valid_until].to_date rescue nil end
    case params[:token_owner_group]
      when "people"
        (params[:select_people] || []).each do |person_id|
         person = Person.find_by_id(person_id)
         token = SurveyToken.create(args)
         token.token_owner = person
         token.allow_teacher = false
         token.save!
         token_count += 1
        end
      when "preliminary"
        people = {} 
        @contest.preliminary_contestants.each do |prelim|
          people[prelim.person] = true
        end
        @contest.schools.each do |school|
          if school.preliminary_contestants.empty?
            people[school.person] = true
          end
        end
        people.each_key do |person|
          token = SurveyToken.create(args)
          token.allow_teacher = false
          token.owner = person
          token.save!
          token_count += 1
          PersonMailer.deliver_survey_invite_notification(person,person.generate_login_token, token) 
        end
      when "contestants"
        (params[:select_contestants] || []).each do |cont_id|
         contestant = Contestant.find_by_id(cont_id)
         (params[:quantity].to_i || 1).times do 
          token = SurveyToken.create(args)
          token.allow_teacher = params[:allow_teacher] == "1"
          token.allow_pupil = params[:allow_pupil] == "1"
          token.allow_tutor = params[:allow_tutor] == "1"
          token.token_owner = contestant
          token.save!
          token_count += 1
        end
        end
      else
        raise "Unknown owner group!"
      end
    flash[:notice] = "Es wurden erfolgreich #{token_count} Tokens erstellt!"
    redirect_to :actions => :index
   end
  rescue
    flash[:error] = "Fehler beim Erstellen der Tokens!"
    render :action => :new
  end
 end
end
