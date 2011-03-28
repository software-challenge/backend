class School < ActiveRecord::Base

  STATES = %w{Schleswig-Holstein Mecklenburg-Vorpommern Hamburg Bremen Brandenburg  Niedersachsen Berlin Sachsen-Anhalt Nordrhein-Westfalen Sachsen Thüringen Hessen Rheinland-Pfalz Saarland Bayern Baden-Württemberg}

  belongs_to :contest
  has_many :contestants
  belongs_to :person
  has_many :preliminary_contestants, :dependent => :destroy
  has_many :survey_tokens, :as => :token_owner, :dependent => :destroy 

  alias :teams :preliminary_contestants
  
  validates_presence_of :contest
  validates_presence_of :name  
  validates_presence_of :zipcode
  validates_presence_of :location
  validates_presence_of :state
  validates_presence_of :contact_function
  validates_presence_of :person
  validates_uniqueness_of :name, :scope => :contest_id
  validates_inclusion_of :state, :in => STATES

  named_scope :without_teams, :conditions => "schools.id NOT IN (SELECT school_id FROM preliminary_contestants)"

  def before_destroy
    Role.find(:all, :conditions => {:authorizable_type => "School", :authorizable_id => self.id}).each do |role|
      role.destroy
    end
    #person.has_no_roles_for! self
    #person.save
  end

  def contact=(p)
    self.person = p
  end

  def contact
    self.person
  end
end
