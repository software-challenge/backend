class ContestsController < ApplicationController
  # GET /contests
  # GET /contests.xml
  def index
    @contests = Contest.all

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @contests }
    end
  end

  # GET /contests/1
  # GET /contests/1.xml
  def show
    @contest = Contest.find(params[:id])

    respond_to do |format|
      format.html { redirect_to contest_matchdays_url(@contest) }
      format.xml  { render :xml => @contest }
    end
  end

  # GET /contests/new
  # GET /contests/new.xml
  def new
    @contest = Contest.new

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @contest }
    end
  end

  # GET /contests/1/edit
  def edit
    @contest = Contest.find(params[:id])
  end

  # POST /contests
  # POST /contests.xml
  def create
    @contest = Contest.new(params[:contest])

    respond_to do |format|
      if @contest.save
        flash[:notice] = 'Contest was successfully created.'
        format.html { redirect_to(@contest) }
        format.xml  { render :xml => @contest, :status => :created, :location => @contest }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @contest.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /contests/1
  # PUT /contests/1.xml
  def update
    @contest = Contest.find(params[:id])

    success = false
    begin
      Contest.transaction do
        @contest.update_attributes!(params[:contest])
      end
      success = true
    rescue ActiveRecord::RecordInvalid
      success = false
    end

    respond_to do |format|
      if success
        flash[:notice] = 'Contest was successfully updated.'
        format.html { redirect_to edit_contest_url(@contest) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @contest.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /contests/1
  # DELETE /contests/1.xml
  def destroy
    @contest = Contest.find(params[:id])
    @contest.destroy

    respond_to do |format|
      format.html { redirect_to(contests_url) }
      format.xml  { head :ok }
    end
  end

  def reset_matchdays
    @contest = Contest.find(params[:id])
    
    @contest.matchdays.destroy_all

    redirect_to contest_matchdays_url(@contest)
  end

  def refresh_matchdays
    @contest = Contest.find(params[:id])

    if @contest.matchdays.count.zero?
      @contest.refresh_matchdays!

      if @contest.matchdays.count.zero?
        flash[:error] = "Es sind nicht genug Teilnehmer vorhanden, um einen Spielplan zu erstellen."
      end
    else
      flash[:error] = "Es liegt bereits ein Spielplan vor."
    end

    redirect_to contest_matchdays_url(@contest)
  end

  protected

  def update_definition(definition, fragments)
    if fragments
      fragments.each_with_index do |fragment_data, i|
        @fragment = nil
        if fragment_data[:id].blank?
          unless fragment_data[:name].blank? #skip blank fields
            @fragment = definition.fragments.build(fragment_data)            
          end
        elsif fragment_data[:name].blank?
          id = fragment_data.delete :id
          @fragment = definition.fragments.find(id)
          @fragment.destroy
          @fragment = nil
        else
          id = fragment_data.delete :id
          @fragment = definition.fragments.find(id)
          @fragment.attributes = fragment_data
        end

        if @fragment
          @fragment.main = fragment_data[:main] if fragment_data[:main]
          @fragment.precision = fragment_data[:precision] if fragment_data[:precision]
          @fragment.position = i
          @fragment.save!
        end
      end
    end

    definition.save!
  end
end
