require 'sandbox'

class Round < ActiveRecord::Base
  validates_presence_of :match

  belongs_to :match
  has_many :slots, :class_name => "RoundSlot", :dependent => :destroy, :order => "position"
  has_many :scores, :through => :slots

  has_attached_file :replay
  
  delegate :contest, :to => :match
  delegate :game_definition, :to => :contest

  def played?; played_at; end

  def perform
    manager = SoChaManager::Manager.new
    manager.connect!
    manager.play self

    while !manager.done?
      sleep 0.1
    end

    manager.close
    
    raise "no game result" unless manager.last_result
    update_scores!(manager.last_result)
  end

  def reset!
    slots.each do |slot|
      slot.reset!
    end

    self.played_at = nil
    save!
  end

  def score_definition
    game_definition.round_score
  end

  def update_scores!(result)
    raise "result must not be nil" unless result
    raise "result (#{result.count}) must have same size as there are slots (#{slots.count})" if result.count != slots.count
    
    Round.transaction do
      slots.each_with_index do |slot,index|
        slot.score ||= slot.build_score(:game_definition => contest[:game_definition], :score_type => "round_score")
        score = result[index]
        slot.score.set!(score[:score], score[:cause])
        slot.save!
      end
      
      self.played_at = DateTime.now
      save!
    end
    
    self.match.after_round_played(self)
  end
end
