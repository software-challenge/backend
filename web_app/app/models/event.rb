class Event < ActiveRecord::Base
  
  belongs_to :context, :polymorphic => true

  def valid_event?
    false
  end

  def path
    nil
  end

  def text
    "Nothing"
  end

end
