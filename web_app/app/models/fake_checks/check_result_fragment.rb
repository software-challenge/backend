class CheckResultFragment < ActiveRecord::Base
 belongs_to :check, :class_name => "FakeCheck" 
end
