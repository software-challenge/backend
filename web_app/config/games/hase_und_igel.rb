def calculate_victories(my_scores, their_scores)
  my_victories = 0
  enemy_victories = 0
  my_scores.each do |score|
    if score.cause == "REGULAR"
      my_victories += score.victory
    else
      # if i violated the rules AND won, enemy gets my victory
      enemy_victories += score.victory
    end
  end
  enemy_scores = their_scores.first
  enemy_scores.each do |score|
    if score.cause == "REGULAR"
      enemy_victories + score.victory
    else
      # if enemy violated the rules AND won, i get his victory
      my_victories += score.victory
    end
  end
  return my_victories, enemy_victories
end

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

      my_victories, enemy_victories = calculate_victories(my_scores, their_scores)

      if my_victories > enemy_victories
        2
      elsif my_victories == enemy_victories
        1
      else
        0
      end
    end

    field :victories, :aggregate => :sum do |my_scores, their_scores|
      my_victories, enemy_victories = calculate_victories(my_scores, their_scores)
      my_victories
    end

    field :average_position, :aggregate => :average do |my_scores, their_scores|
      # for non-regular game-ends set position to 0
      # and the position of the enemy to 64
      sum = 0
      (0..(my_scores.size-1)).each do |i|
        if my_scores[i].cause == "REGULAR"
          if their_scores.first[i].cause != "REGULAR"
            sum += 64
          else
            sum += my_scores[i].position
          end
        else
          sum += 0
        end
      end

      sum / my_scores.count
    end
    field :average_carrots, :average => :carrots, :precision => 2
    field :average_time, :average => :average_time

    main :victories
  end
end
