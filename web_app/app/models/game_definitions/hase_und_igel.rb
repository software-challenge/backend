GameDefinition.create :"HaseUndIgel" do
  players 2
  
  league do
    rounds 6
  end
  
  round_score do
    field :victory, :direction => "DESC"
    field :field, :direction => "DESC"
    field :carrots, :direction => "ASC"
    field :average_time, :direction => "ASC", :precision => 2
  end
  
  match_score do
    
    field :points, :direction => "DESC" do |me, rounds|
      my_scores = rounds.scores_for(me)
      enemy_scores = rounds.scores_not_for(me).first
      
      my_score = my_scores.collect &:victory
      enemy_score = enemy_scores.collect &:victory
      
      # WIN = 3, TIE = 1, LOSE = 0
      (my_score > enemy_score ? 3 : (my_score == enemy_score ? 1 : 0))
    end
    
    field :victories, :sum => :victory
    field :average_field, :average => :field
    field :average_carrots, :average => :carrots
    field :average_time, :average => :average_time
  end
end