class ContestantsController < ApplicationController
  # GET /contestants
  # GET /contestants.xml
  def index
    @contestants = Contestant.all

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @contestants }
    end
  end

  # GET /contestants/1
  # GET /contestants/1.xml
  def show
    @contestant = Contestant.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @contestant }
    end
  end

  # GET /contestants/new
  # GET /contestants/new.xml
  def new
    @contestant = Contestant.new

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @contestant }
    end
  end

  # GET /contestants/1/edit
  def edit
    @contestant = Contestant.find(params[:id])
  end

  # POST /contestants
  # POST /contestants.xml
  def create
    @contestant = Contestant.new(params[:contestant])

    respond_to do |format|
      if @contestant.save
        flash[:notice] = 'Contestant was successfully created.'
        format.html { redirect_to(@contestant) }
        format.xml  { render :xml => @contestant, :status => :created, :location => @contestant }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @contestant.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /contestants/1
  # PUT /contestants/1.xml
  def update
    @contestant = Contestant.find(params[:id])

    respond_to do |format|
      if @contestant.update_attributes(params[:contestant])
        flash[:notice] = 'Contestant was successfully updated.'
        format.html { redirect_to(@contestant) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @contestant.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /contestants/1
  # DELETE /contestants/1.xml
  def destroy
    @contestant = Contestant.find(params[:id])
    @contestant.destroy

    respond_to do |format|
      format.html { redirect_to(contestants_url) }
      format.xml  { head :ok }
    end
  end
end
