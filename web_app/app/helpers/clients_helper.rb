module ClientsHelper
  def get_session_key_name
    ActionController::Base.session_options[:key]
  end
end
