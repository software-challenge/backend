class Match < ActiveRecord::Base
  has_many :slots, :dependent => :destroy, :class_name => "MatchSlot"
  
  belongs_to :set, :polymorphic => true

  alias :matchday :set
  alias :matchday= :set=
end
