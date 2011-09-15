class ContractsController < ApplicationController

  access_control do 
    allow :administrator
  end

  before_filter :fetch_person, :fetch_contract

  def index
    @contracts = @person.contracts 
  end

  def edit

  end

  def new
    @contract = Contract.new
  end

  def create
    if Contract.create(params[:contract].merge(:person => @person)).save
      flash[:notice] = "Arbeitsvertrag wurde erfolgreich erstellt."
      redirect_to :action => :index
    else
      flash[:error] = "Arbeitsvertrag konnte nicht erstellt werden!"
      render :action => :edit
    end
  end

  def update
    if @contract.update_attributes(params[:contract])    
      flash[:notice] = "Arbeitsvertrag wurde erfolgreich bearbeitet."
      redirect_to :action => :index
    else
      flash[:error] = "Arbeitsvertrag konnte nicht bearbeitet werden!"
      render :action => :edit
    end
  end

  def fetch_person
    @person = Person.find_by_id(params[:person_id])
  end

  def fetch_contract
    @contract = Contract.find_by_id(params[:id])
  end
end
