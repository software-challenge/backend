class Client < ActiveRecord::Base
  belongs_to :contestant
  belongs_to :author, :class_name => "Person"
end
