class LoginToken < ActiveRecord::Base
  belongs_to :person
   
  liquid_methods :person, :code
  
  validates_presence_of :person

  def initialize(params)
    super(params)
    generate_code!
  end

  def expired?
    self.created_at < 2.weeks.ago
  end

  private

  def generate_code!
   chars = ("a".."z").to_a + ("A".."Z").to_a + ("0".."9").to_a
   max_offset = rand(20)
   code = ""
   1.upto(20+max_offset) { |i| code << chars[rand(chars.size-1)]}
   self.code = code
   self.save!
  end
end
