class EmailTemplatesController < ApplicationController
  access_control do 
    allow :administrator
  end
  
  before_filter :fetch_template, :except => [:index, :new, :create]

  def index
    @templates = EmailTemplate.all.group_by{|t| t.class} 
  end

  def show
    if params[:preview].present? and params[:preview] == "true"
      render :partial => "template", :locals => {:template => @email_template}
    else
      redirect_to :action => :edit
    end
  end

  def edit
  end

  def new
    @email_template = EmailTemplate.new
  end

  def update
    if @email_template.class.to_s != params[:email_template][:type] and EmailTemplate.allowed_types.map{|t| t.to_s}.include?(params[:email_template][:type])
      @email_template.type = params[:email_template][:type]
    end
    if @email_template.update_attributes(params[:email_template])
      flash[:notice] = "Template wurde erfolgreich bearbeitet"
      redirect_to :action => :index
    else
      render :action => "edit"
    end
  end

  def create
    if @email_template.class.to_s != params[:email_template][:type] and EmailTemplate.allowed_types.map{|t| t.to_s}.include?(params[:email_template][:type])
      template_type = eval(params[:email_template][:type])
      @email_template = template_type.new(params[:email_template])
    end
    if @email_template.save 
      flash[:notice] = "Template wurde erfolgreich erstellt"
      redirect_to :action => :index
    else
      render :action => "new"
    end
  end

  def fetch_template
    @email_template = EmailTemplate.find_by_id(params[:id])
  end
end
