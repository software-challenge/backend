class FinaleMatchday < Matchday

  undef :matches

  has_many :matches, :class_name => "FinaleMatch", :dependent => :destroy, :as => :set
  has_many :mini_jobs, :through => :matches, :source => :job

  delegate :game_definition, :to => :finale
  delegate :contest, :to => :finale


  def do_validation?
    false
  end

  #Override
  def order_scoretable
  end

  #Override
  def update_scoretable
  end

  #Override
  def after_match_played(match)
    logger.info "Finale match played: #{match}"
    match.set_winner!  
    if all_matches_played?
      logger.info "Saving: #{self} ..."
      self.played_at = DateTime.now
      self.save!
      contest.finale.after_matchday_played(self)
    end
  end

  def winners(hash = {})
    winners = matches.collect{|match| match.winner(hash[:multiple])}.flatten

    if hash[:reorder]
      orders = contest.game_definition.match_score.values.collect(&:name)
      winners.sort_by {|s| 
        if s.class == Array
          s = s[0]
        end
        orders.map {|o| s.score.fragments.find(:first, :conditions => {:fragment => o.to_s}).value}}
    end

    return winners
  end

  def losers(hash = {})
    losers = matches.collect{|match| match.loser(hash[:multiple])}.flatten

    if hash[:reorder]
      orders = contest.game_definition.match_score.values.collect(&:name)
      losers.sort_by {|s| 
        if s.class == Array
          s = s[0]
        end
        orders.map {|o| s.score.fragments.find(:first, :conditions => {:fragment => o.to_s}).value}}
    end

    return losers
  end

  def finished?
    played?
  end

  def prepared?
    !matches.empty?
  end
end
