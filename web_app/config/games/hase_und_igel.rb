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
      my_victories = my_scores.inject(0) { |sum,x| sum + x.victory }
      enemy_scores = their_scores.first
      enemy_victories = enemy_scores.inject(0) { |sum,x| sum + x.victory }

      if my_victories > enemy_victories
        3
      elsif my_victories == enemy_victories
        1
      else
        0
      end
    end

    field :victories, :sum => :victory
    field :average_position, :average => :position, :precision => 2
    field :average_carrots, :average => :carrots, :precision => 2
    field :average_time, :average => :average_time

    main :victories
  end
end
