class ContestantPeopleController < ApplicationController
  before_filter :fetch_contestant

  access_control do
    default :deny
    allow :administrator
  end

  access_control :only => [:show] do
    allow logged_in
  end

  access_control :only => [:new, :create] do
    allow :administrator, :teacher, :tutor
  end

  access_control :only => [:edit, :update] do
    allow :administrator
    allow :pupil, :of => Person
    allow :teacher, :tutor, :of => Person
  end

  # GET /people
  # GET /people.xml
  def index
    @people = @contestant.people.all :order => "email ASC"

    respond_to do |format|
      format.html { render :controller => "people", :action => "index" }
      format.xml  { render :xml => @people }
    end
  end

  # GET /people/1
  # GET /people/1.xml
  def show
    @person = Person.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @person }
    end
  end

  # GET /people/new
  # GET /people/new.xml
  def new
    @person = Person.new

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @person }
    end
  end

  # GET /people/1/edit
  def edit
    @person = Person.find(params[:id])

    # Rights
    if current_user.administrator? || current_user.teacher? || current_user.tutor? || current_user == @person

      # only his pupil
      if !current_user.administrator? and current_user != @person
        if !select_pupils.include?(@person)
          raise "not allowed"
        end
      end
      # Rights end
    else
      return "not allowed"
    end
  end

  # POST /people
  # POST /people.xml
  def create
    # cleanup params
    person_params = params[:person].clone
    person_params[:teams].reject! { |x| x.blank? } if person_params[:teams]
    role = person_params.delete :role

    @person = Person.new(person_params)
    success = @person.save
    @person.role = role if success and role # do it after we created the memberships

    respond_to do |format|
      if success
        flash[:notice] = @person.name + ' wurde erfolgreich angelegt.'
        format.html { redirect_to(people_url) }
        format.xml  { render :xml => @person, :status => :created, :location => @person }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @person.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /people/1
  # PUT /people/1.xml
  def update
    @person = Person.find(params[:id])
    check_permissions_on(@person)

    # cleanup params
    person_params = params[:person].clone
    person_params[:teams].reject! { |x| x.blank? } if person_params[:teams]
    role = person_params.delete :role

    success = @person.update_attributes(person_params)
    @person.role = role if success and role # do it after we created the memberships

    respond_to do |format|
      if success
        flash[:notice] = @person.name + ' wurde erfolgreich aktualisiert.'
        format.html { redirect_to(@person) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @person.errors, :status => :unprocessable_entity }
      end
    end
  end

  protected

  def fetch_contestant
    @contestant = Contestant.find(params[:contestant_id])
    @contest = @contestant.contest
  end

end
