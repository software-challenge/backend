require 'sandbox'

class Round < ActiveRecord::Base

  # named scopes
  named_scope :played, :conditions => "played_at IS NOT NULL"

  # validations
  validates_presence_of :match

  # associnations
  belongs_to :match
  has_many :slots, :class_name => "RoundSlot", :dependent => :destroy, :order => "position"
  has_many :scores, :through => :slots

  has_attached_file :replay
  
  delegate :contest, :to => :match
  delegate :game_definition, :to => :match

  def played?
    !played_at.nil?
  end

  def perform
    begin
      manager = SoChaManager::Manager.new
      manager.connect!
      manager.play self

      while !manager.done?
        sleep 1
      end

      manager.close
    rescue => exception
      manager.close
      raise exception
    end
    
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
   
    logger.info "Round finished: #{self}. Updating scores."

    Round.transaction do
      slots.each_with_index do |slot,index|
        slot.score ||= slot.build_score(:game_definition => contest[:game_definition], :score_type => "round_score")
        score = result[index]
        slot.score.set!(score[:score], score[:cause], score[:error_message])
        slot.save!
      end
      
      self.played_at = DateTime.now
      save!
    end
    
    self.match.after_round_played(self)
  end

  def has_server_log?
    File.exists? File.join(ENV['SERVER_LOGS_FOLDER'], self.id.to_s + ".log")
  end

  def has_disqualified_slot?
    slots.each do |slot|
      return true if slot.score.cause == "LEFT"
    end
    false
  end

  def winner
    return slots.to_ary.find{|s| s.score.fragments.find(:first, :conditions => {:fragment => "victory"}).value == 1}.contestant
  end
end
