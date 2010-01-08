class PeopleController < ApplicationController

  before_filter :fetch_contestant, :only => [:people_for_contestant, :remove_from_contestant]
  before_filter :fetch_person, :only => [:edit, :update, :hide, :remove_from_contestant]
  access_control do
    allow :administrator

    action :show do
      allow logged_in
    end

    actions :new, :create do
      allow :administrator, :teacher, :tutor
    end

    action :people_for_contestant do
      allow :administrator
      allow :tutor, :teacher, :pupil, :of => :contestant
    end

    actions :edit, :update do
      allow :administrator
      allow logged_in, :if => :same_person
    end

    action :remove_from_contestant do
      allow :administrator
      allow :tutor, :teacher, :of => :contestant
    end

  end

  access_control :helper => :may_edit_person? do
    allow :administrator
    allow logged_in, :if => :same_person
  end

  access_control :helper => :may_remove_from_contestant? do
    allow :administrator
    allow :tutor, :teacher, :of => :contestant
  end

  def same_person(as = nil)
    if as.nil?
      current_user == @person
    else
      current_user == as
    end
  end
  helper_method :same_person

  access_control :helper => :may_see_person_details? do
    allow :administrator
    allow :pupil, :of => :person
    allow :teacher, :tutor, :of => :person
  end


  def fetch_contestant
    # contestant needs to be fetched before authorization control
    @contestant = Contestant.find(params[:contestant_id])
  end

  def fetch_person
    # person needs to be fetched before authorization control
    @person = Person.find(params[:id])
  end

  # GET /people
  # GET /people.xml
  def index
    @people = Person.all :order => "email ASC", :conditions => {:hidden => false}

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @people }
    end
  end

  def people_for_contestant
    @contest = @contestant.contest
    @people = @contestant.people.all :order => "email ASC"

    respond_to do |format|
      format.html
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
    # @person is fetched in before_filter
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
        format.html {
          if @person.has_role? :pupil
            redirect_to(:action => :people_for_contestant, :contestant_id => @person.teams.first.to_param)
          else
            redirect_to(people_url)
          end
        }
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
    # @person is fetched in before_filter

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

  def hide
    @person.hidden = true
    if @person.save
      flash[:notice] = I18n.t("messages.hidden_successfully", :name => @person.name)
    end
    redirect_to :back
  end

  # DELETE /people/1
  # DELETE /people/1.xml
  def destroy
    # We need the models to display client.authors etc, so we can't just "delete" them.
    raise "Deletion is not supported right now."
  end

  def remove_from_contestant
    @person.memberships.find_by_contestant_id(@contestant.id).destroy
    redirect_to :action => :people_for_contestant, :contestant_id => @contestant.to_param
  end

end
