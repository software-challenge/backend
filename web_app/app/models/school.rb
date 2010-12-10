class School < ActiveRecord::Base

  STATES = %w{Schleswig-Holstein Mecklenburg-Vorpommern Hamburg Bremen Brandenburg  Niedersachsen Berlin Sachsen-Anhalt Nordrhein-Westfalen Sachsen Thüringen Hessen Rheinland-Pfalz Saarland Bayern Baden-Württemberg}

  PROBS = ["Sehr sicher", "Sicher", "Wahrscheinlich", "Vielleicht", "Eher nicht", "Sicher nicht"]

  belongs_to :contest
  has_many :contestants
  belongs_to :person
  
  validates_presence_of :contest
  validates_presence_of :name  
  validates_presence_of :estimated_team_count
  validates_presence_of :zipcode
  validates_presence_of :location
  validates_presence_of :state
  validates_presence_of :participation_probability
  validates_presence_of :contact_function
  validates_presence_of :person
  validates_uniqueness_of :name, :scope => :contest_id
  validates_inclusion_of :state, :in => STATES
  validates_inclusion_of :participation_probability, :in => PROBS

  def before_destroy
    person.has_no_roles_for! self
    person.save
  end

  def contact=(p)
    self.person = p
  end

  def contact
    self.person
  end
end
