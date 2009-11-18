class PeopleController < ApplicationController
  # GET /people
  # GET /people.xml
  def index
    if (current_user.administrator?)
      @people = Person.all :order => "email ASC"
    else
      if (current_user.teacher? || current_user.tutor?)
        @people = select_pupils
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

  def select_pupils
    Person.all :joins => ["INNER JOIN memberships m1 ON m1.person_id = people.id INNER JOIN memberships m2 ON m2.contestant_id = m1.contestant_id"], :conditions => ["m2.person_id = ? AND m1.teacher = ? AND m1.tutor = ? AND (m2.teacher = ? OR m2.tutor = ?)", current_user.id, false, false, true, true], :order => "email ASC"
  end

  def valid_password(pass)
    pass != "" && pass.length > 5
  end

  def only_one_admin?
    admins = Person.all :conditions => ["administrator = ?", true]
    admins.length == 1
  end

  def last_admin(person)
    if only_one_admin?
      last = Person.first :conditions => ["administrator = ?", true]
      last == person
    end
  end
  
  def set_roles
    if current_user.administrator?
      @roles = [">Keine", "Lehrer", "Schüler", "Tutor"]
    else
      @roles = ["Schüler"]
    end
  end

  def set_teams
    if current_user.administrator?
      contestants = Contestant.all

      @teams = []

      contestants.each do |contestant|
        @teams.push(contestant.name)
      end
    else
      @teams = current_user.getteams
    end
  end
  
  # GET /people/new
  # GET /people/new.xml
  def new
    @person = Person.new

    set_roles
    set_teams

    @person.password_hash = ""

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
      if current_user.teacher? || current_user.tutor?
        if !select_pupils.include?(@person)
          error = true
        end
      end
      # Rights end
      
      set_roles
      set_teams

      @teams_selected = @person.getteams
      @roles_selected = @person.getrole

      @person.password_hash = ""
    else
      error = true
    end

    if error
      redirect_to(current_user)
    end
  end

  # POST /people
  # POST /people.xml
  def create
    @person = Person.new(params[:person])

    # Rights
    if current_user.administrator? || current_user.teacher? || current_user.tutor?
      # Rights end

      if (valid_password(@person.password_hash))
        @person.password=@person.password_hash
      else
        flash[:error] = 'Passwort muss mindestens 6 Zeichen haben.'
        error = true
      end

      if !error
        error = !@person.save

        role_name = params[:role]
        team_names = params[:team]

        if (role_name != ">Keine" && !error)
          if (team_names.length > 0)
            Membership.transaction do
              team_names.each do |team_name|
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
              end
            end

            if (error)
              flash[:error] = 'Fehler beim Erstellen einer Membership Beziehung.'
              @person.destroy
              error = true
            end
          else
            flash[:error] = 'Team darf für eine Rolle nicht leer sein.'
            @person.destroy
            error = true
          end
        end
      end
    else
      flash[:error] = "Nicht genügend Rechte um zu erstellen"
      error = true
    end

    respond_to do |format|
      if !error
        flash[:notice] = @person.name + ' wurde erfolgreich angelegt.'
        format.html { redirect_to(people_url) }
        format.xml  { render :xml => @person, :status => :created, :location => @person }
      else
        set_roles
        set_teams
        @person.password_hash = ""
        format.html { render :action => "new" }
        format.xml  { render :xml => @person.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /people/1
  # PUT /people/1.xml
  def update
    @person = Person.find(params[:id])

    # Rights
    if current_user.administrator? || current_user.teacher? || current_user.tutor? || current_user == @person
      # only his pupils
      if (current_user.teacher? || current_user.tutor?) && current_user != @person
        if !select_pupils.include?(@person)
          error = true
        end
      end
      # Rights end

      # is this the last admin?
      if (last_admin(@person) && (!params[:administrator] || params[:blocked]))
        flash[:error] = "Der letzte Administrator darf seine Rechte nicht verlieren und nicht gesperrt sein!"
        error = true
      end

      # create membership relations
      if (!error)
        role_name = params[:role]
        team_names = params[:team]

        if (role_name != ">Keine" && role_name != nil && !error)
          if (team_names != nil && team_names.length > 0)
            Membership.transaction do
              Membership.destroy_all :person_id => params[:id]
         
              team_names.each do |team_name|
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
              end
            end

            if (error)
              flash[:error] = 'Fehler beim Erstellen einer Membership Beziehung.'
              error = true
            end
          else
            flash[:error] = 'Team darf für eine Rolle nicht leer sein.'
            error = true
          end
        end
      end

      # update attributes
      if (!error)
        pass = params[:password]

        if (pass[:value] != "")
          if (valid_password(pass[:value]))
            @person.password = pass[:value]
            error = !@person.update_attributes(params[:person])
          else
            flash[:error] = 'Passwort muss mindestens 6 Zeichen haben.'
            error = true
          end
        else
          error = !@person.update_attributes(params[:person])
        end

      end

    else
      flash[:error] = "Nicht genügend Rechte um zu bearbeiten"
      error = true
    end

    respond_to do |format|
      if !error
        flash[:notice] = @person.name + ' wurde erfolgreich aktualisiert.'
        format.html { redirect_to(@person) }
        format.xml  { head :ok }
      else
        set_roles
        set_teams
        format.html { render :action => "edit" }
        format.xml  { render :xml => @person.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /people/1
  # DELETE /people/1.xml
  def destroy
    @person = Person.find(params[:id])

    # Rights
    if current_user.administrator? || current_user.teacher? || current_user.tutor?
      # only his pupils
      if current_user.teacher? || current_user.tutor?
        if !select_pupils.include?(@person)
          error = true
        end
      end
      # Rights end
      
      if (last_admin(@person))
        flash[:error] = "Der letzte Administrator darf seine Rechte nicht verlieren!"
        error = true
      end

      if (!error)
        Membership.destroy_all :person_id => params[:id]
        @person.destroy
      end
    else
      flash[:error] = "Nicht genügend Rechte um zu löschen!"
    end

    respond_to do |format|
      format.html { redirect_to(people_url) }
      format.xml  { head :ok }
    end
  end
end
