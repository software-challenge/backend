class MainController < ApplicationController

  skip_before_filter :require_current_user

  def index
    
  end

  def login
    
  end

  # TODO: make this real
  def do_login
    email = params[:user][:email]
    password = params[:user][:password]

    begin
      person = Person.find_by_email(email)
      raise ActiveRecord::RecordNotFound unless person
      raise ActiveRecord::RecordNotFound unless person.password_match?(password)
      session[:user_id] = person.id
      redirect_to root_url
    rescue ActiveRecord::RecordNotFound
      @user = { :email => email, :password => "" }.to_obj
      flash[:notice] = "Der Benutzer konnte nicht gefunden werden!"
      render :action => "login"
    end
  end
end