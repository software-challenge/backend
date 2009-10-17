class MainController < ApplicationController

  skip_before_filter :require_current_user

  def index
    
  end

  def login
    
  end

  # TODO: make this real
  def do_login
    user_id = params[:user_id]

    begin
      @person = Person.find(user_id)
      session[:user_id] = @person.id
    rescue ActiveRecord::RecordNotFound
      flash[:notice] = "Der Benutzer konnte nicht gefunden werden!"
      redirect_to login_url
    end
  end
end