class PeopleController < ApplicationController
  # GET /people
  # GET /people.xml
  def index
    raise NotAllowed unless current_user.administrator?
    
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
    @person =
      check_permissions_on(@person)

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

  # DELETE /people/1
  # DELETE /people/1.xml
  def destroy
    # We need the models to display client.authors etc, so we can't just "delete" them.
    raise "Deletion is not supported right now."
  end

  protected

  def select_pupils
    Person.all :joins => ["INNER JOIN memberships m1 ON m1.person_id = people.id INNER JOIN memberships m2 ON m2.contestant_id = m1.contestant_id"], :conditions => ["m2.person_id = ? AND m1.role = 'pupil' AND m2.role <> 'pupil'", current_user.id], :order => "email ASC"
  end

  def valid_password(pass)
    pass != "" && pass.length > 5
  end

  def check_permissions_on(person)
    # Rights
    unless current_user.administrator? || current_user == person
      raise "not allowed" unless select_pupils.include?(person)
    end
  end
end
