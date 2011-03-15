class Review < ActiveRecord::Base
  belongs_to :reviewable, :polymorphic => true
  
  def finished?
    finished
  end

  def finished!
    self.finished = true
    self.save!
  end

  def unfinished!
    self.finished = false
    self.save!
  end
end
