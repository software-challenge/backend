class FinaleMatch < LeagueMatch
  
  undef slots
  has_many :slots,
    :class_name => "LeagueMatchSlot",
    :foreign_key => :match_id,
    :dependent => :destroy,
    :order => "id"

  def winner(multiple = false)
    winner = slots.find(:first, :conditions => {:position => 1})
    loser = slots.find(:first, :conditions => {:position => 2})
    same_score = (winner.has_same_score_as? loser)

    if same_score and multiple
      return [winner, loser]
    else
      return [winner]
    end
  end

  def loser(multiple = false)
    winner = slots.find(:first, :conditions => {:position => 1})
    loser = slots.find(:first, :conditions => {:position => 2})
    same_score = (winner.has_same_score_as? loser)

    if same_score and multiple
      return []
    else
      return [loser]
    end
  end

  def set_winner!
    definition_fragments = contest.game_definition.match_score.values
    joins = ["INNER JOIN scores AS order_scores ON order_scores.id = match_slots.score_id"]

    orders = []
    definition_fragments.each_with_index do |fragment, i|
      if fragment.ordering
        orders << "fragment_#{i}.value #{fragment.ordering.upcase}"
        joins << ("INNER JOIN score_fragments AS fragment_#{i} ON (fragment_#{i}.score_id = order_scores.id AND fragment_#{i}.fragment = '#{fragment.name.to_s}')")
      end
    end

    unless orders.empty?
      all_slots = slots(:reload).all(:order => orders.join(', '), :joins => joins.join(' '), :group => "match_slots.id")
      all_slots.each_with_index do |slot,i|
        writeable_slot = slots.find(slot.id)
        writeable_slot.position = i.next
        writeable_slot.save!
      end    
    else
      logger.warn "ORDER was empty - cannot order." 
    end

    winner = slots.find(:first, :conditions => {:position => 1})
    loser = slots.find(:first, :conditions => {:position => 2})
    if winner.has_same_score_as? loser then
      # Lucky one will be the winner
      # So there will be a 50% chance that slots get switched
      logger.info "Scores were the same for #{winner} and #{loser}...rolling"
      if rand(1000) % 2 == 0 then
        logger.info "Winner and loser got switched."
        winner.position = 2
        loser.position = 1
        winner.save!
        loser.save!
      end
    end
  end

  def run_match
    rounds.each do |round|
      if game_definition.finale_winner_certain?(self)
        round.destroy
        next
      end
      if not round.played?
        round.perform
      end   
    end
    reload
    self.after_round_played(nil)
  end

end 
