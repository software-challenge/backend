GameDefinition.create :"HaseUndIgel" do
  players 2
  plugin_guid "swc_2010_hase_und_igel"
  test_rounds 2

  league do
    rounds 6
  end

  round_score do
    field :victory, :ordering => "DESC"
    field :position, :ordering => "DESC"
    field :carrots, :ordering => "ASC"
    field :average_time, :ordering => "ASC", :precision => 2
  end

  match_score do
    field :points, :ordering => "DESC", :aggregate => :sum do |my_scores, their_scores|
      my_victories = my_scores.inject(0) do |sum,x|
        if x.cause == "REGULAR"
          sum + x.victory
        else
          sum
        end
      end
      enemy_scores = their_scores.first
      enemy_victories = enemy_scores.inject(0) do |sum,x|
        if x.cause == "REGULAR"
          sum + x.victory
        else
          sum
        end
      end

      if my_victories > enemy_victories
        2
      elsif my_victories == enemy_victories
        1
      else
        0
      end
    end

    field :victories, :sum => :victory
    field :average_position, :aggregate => :average do |my_scores, their_scores|
      # for non-regular game-ends set position to 0
      sum = my_scores.inject(0) do |sum, score|
        if score.cause == "REGULAR"
          sum + score.position
        else
          sum
        end
      end

      sum / my_scores.count
    end
    field :average_carrots, :average => :carrots, :precision => 2
    field :average_time, :average => :average_time

    main :victories
  end
end
