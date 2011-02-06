class FakeCheck < ActiveRecord::Base
  belongs_to :fake_test, :polymorphic => true
  has_many :fragments, :class_name => "CheckResultFragment", :dependent => :destroy
  has_many :clients, :through => :fake_test

  def self.compatible_with_game?(game_identifier)
    defined?(compatible_games).nil? ? true : compatible_games.include?(game_identifier)
  end

  def self.compatible_with_contest?(contest)
    compatible_with_game? contest.game_definition.game_identifier
  end
  
  def done?
   not finished_at.nil?
  end

  def reset!
   fragments.each do |f|
    f.delete
   end
   finished_at = nil
   self.save!
  end
  
  def perform 
  end

  def whitelist
    contest.whitelist
  end
  
  def fake_test
    FakeTest.find_by_id(fake_test_id)
  end

  def contest
    fake_test.contest
  end

  def clients
    fake_test.clients
  end

  # Just a shorter way to generate a fragment!
  def gfrag(name, value, description)
    CheckResultFragment.new(:name => name.to_s, :value => value.to_s, :description => description.to_s)
  end

  def gfrag!(name, value, description)
   fragments << gfrag(name,value,description)
  end

end
