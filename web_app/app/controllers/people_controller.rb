class PeopleController < ApplicationController

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
    @people = Person.all :order => "email ASC"

    respond_to do |format|
      format.html # index.html.erb
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
  end

  # POST /people
  # POST /people.xml
  def create
    # cleanup params
    person_params = params[:person].clone
    person_params[:teams].reject! { |x| x.blank? } if person_params[:teams]

    @person = Person.new(person_params)
    success = @person.save

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

    # cleanup params
    person_params = params[:person].clone
    person_params[:teams].reject! { |k,v| !current_user.manageable_teams.find(k) } if person_params[:teams]

    success = @person.update_attributes(person_params)

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

  # DELETE /people/1
  # DELETE /people/1.xml
  def destroy
    # We need the models to display client.authors etc, so we can't just "delete" them.
    raise "Deletion is not supported right now."
  end

end
