class Matchday < ActiveRecord::Base
  validates_presence_of :order
  validates_presence_of :contest
  validates_presence_of :when
  validates_uniqueness_of :order, :scope => :contest_id

  has_many :matches, :dependent => :destroy, :as => :set
  belongs_to :contest

  def position
    self.contest.matchdays.count(:conditions => ["matchdays.when < ? OR (matchdays.when = ? AND matchdays.order < ?)", self.when, self.when, self.order]) + 1
  end
end
