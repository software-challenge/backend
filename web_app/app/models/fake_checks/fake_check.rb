class FakeCheck < ActiveRecord::Base
  belongs_to :fake_test, :polymorphic => true
  has_many :fragments, :class_name => "CheckResultFragment"
  has_many :clients, :through => :fake_test
 
  def compatible_with_game?(game_identifier)
    defined?(compatible_games).nil? ? true : compatible_games.include?(game_identifier)
  end

  def compatible_with_contest?(contest)
    compatible_with_game? contest.game_definition.game_identifier
  end
  
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
