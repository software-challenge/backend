class SurveyToken < ActiveRecord::Base

  belongs_to :survey
  belongs_to :token_owner, :polymorphic => true 
  belongs_to :response_set, :dependent => :destroy
  has_one :person, :through => :response_set, :source => :user
  
  delegate :contest, :to => :token_owner
  delegate :season, :to => :token_owner
 
  validates_presence_of :token_owner
  validates_presence_of :survey

  named_scope :consumed, :conditions => "response_set_id IS NOT NULL"
  named_scope :available, :conditions => "response_set_id IS NULL" 
  named_scope :for_survey, lambda{|c| {:conditions => { :survey_id => c.id}}}

  def currently_valid?
    valid_on?(Time.now)
  end

  def valid_on?(date)
    if valid_from
     return false if date < valid_from
    end

    if valid_until
     return false if date > valid_until
    end

    return true
  end

  def self.for_person(person)
    SurveyToken.all.select do |t|
      t.allowed_for?(person)
    end
  end
  
  def allowed_for?(person)
     return true if person.has_role?("administrator")
     if token_owner.is_a? Person
       return true if token_owner == person
     elsif token_owner.respond_to? :person
       return token_owner.person == person
     elsif token_owner.is_a? Contestant
       if allow_teacher 
         return true if person.has_role?("teacher",token_owner)  
       end
       if allow_tutor
         return true if person.has_role?("tutor",token_owner)
       end
       if allow_pupil
         return true if person.has_role?("pupil",token_owner)
       end
     end
     false
  end

  def consumed?
    !!response_set
  end

  def complete?
    consumed? and response_set.complete? 
  end
end

