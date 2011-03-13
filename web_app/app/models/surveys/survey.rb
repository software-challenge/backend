class Survey < ActiveRecord::Base
  include Surveyor::Models::SurveyMethods
  has_many :tokens, :dependent => :destroy, :class_name => "SurveyToken"
  
  def available_for?(pers)
    tokens.each do |t|
      return true if t.currently_valid? and t.allowed_for?
    end
    return false
  end
end
