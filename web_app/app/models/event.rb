class Event < ActiveRecord::Base
  belongs_to :contest, :polymorphic => true

  def valid_event?
    false
  end
end
