class ActiveRecord::Base
  @@current_user = nil
  cattr_accessor :current_user
  def current_user; @@current_user; end
end