class FakeCheck < ActiveRecord::Base
  belongs_to :fake_test, :polymorphic => true
  has_many :fragments, :class_name => "CheckResultFragment"
  has_many :clients, :through => :fake_test
 
  def done?
   fragments.length > 0
  end

  def reset!
   fragments.each do |f|
    f.delete
   end
  end
  
  def perform 
  end

  def fake_test
    FakeTest.find_by_id(fake_test_id)
  end

end
