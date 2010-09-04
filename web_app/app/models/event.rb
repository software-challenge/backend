class Event < ActiveRecord::Base
  belongs_to :contest, :polymorphic => true
end
