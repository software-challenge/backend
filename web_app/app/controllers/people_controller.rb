class PeopleController < ApplicationController
  # GET /people
  # GET /people.xml
  def index
    if (current_user.administrator?)
      @people = Person.all
    else
      if (params[:pupil])
        @people = Person.all :joins => "LEFT JOIN memberships ON memberships.person_id = people.id", :conditions => ["memberships.teacher = ? AND memberships.tutor = ?", false, false]
      end
    end

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

    if current_user.administrator?
      @roles = [">Keine", "Lehrer", "Schüler", "Tutor"]
    else
      @roles = ["Schüler"]
    end

    if current_user.administrator?
      contestants = Contestant.all

      @teams = [">Kein Team"]

      contestants.each do |contestant|
        @teams.push(contestant.name)
      end
    else
      contestants = Contestant.all :joins => "LEFT JOIN memberships ON memberships.contestant_id = contestants.id AND LEFT JOIN people ON people.id = memberships.person_id", :conditions => ["memberships.teacher = ? AND persons.email = ?", true, current_user.email]

      @teams = []

      contestants.each do |contestant|
        @teams.push(contestant.name)
      end
    end

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @person }
    end
  end

  # GET /people/1/edit
  def edit
    @person = Person.find(params[:id])
    @person.password_hash = ""
  end

  # POST /people
  # POST /people.xml
  def create
    @person = Person.new(params[:person])

    @person.password=@person.password_hash

    error = !@person.save

    role_name = params[:role]
    team_name = params[:team]

    if (role_name != ">Keine" && !error)
      if (team_name != ">Kein Team")
        team_obj = Contestant.first :conditions => ["contestants.name = ?", team_name]
        person_obj = Person.first :conditions => ["people.email = ?", @person.email]
        @membership = Membership.new()
        @membership.contestant = team_obj
        @membership.person = person_obj
        if (role_name == "Lehrer")
          @membership.teacher = true
        else
          if (role_name == "Tutor")
            @membership.tutor = true
          end
        end

        error = error || !@membership.save

        if (error)
          flash[:notice] = 'Fehler beim Erstellen von der Membership Beziehung.'
          @person.destroy
          error = true;
        end
      else
        flash[:notice] = 'Team darf für eine Rolle nicht leer sein.'
        @person.destroy
        error = true;
      end
    end

    respond_to do |format|
      if !error
        flash[:notice] = 'Person wurde erfolgreich angelegt.'
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

    updated_person = params[:person]

    @person.password=updated_person.password_hash

    respond_to do |format|
      if @person.update_attributes(params[:person])
        flash[:notice] = 'Person wurde erfolgreich aktualisiert.'
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
    @person = Person.find(params[:id])
    @person.destroy

    respond_to do |format|
      format.html { redirect_to(people_url) }
      format.xml  { head :ok }
    end
  end
end
