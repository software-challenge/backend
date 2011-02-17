def calculate_victories(my_scores, their_scores)
  my_victories = 0
  enemy_victories = 0

      sum = 0
      (0..(my_scores.size-1)).each do |i|
        if my_scores[i].cause == "REGULAR" and their_scores.first[i].cause == "REGULAR"
          # both played by rules
          my_victories += my_scores[i].victory
          enemy_victories += their_scores.first[i].victory
        elsif my_scores[i].cause != "REGULAR"
          # i violated
          enemy_victories += 1
        else
          # enemy violated
          my_victories += 1
        end
      end
  { :mine => my_victories, :theirs => enemy_victories}
end

GameDefinition.create :"<%= file_name.camelcase %>" do
  players 2
  plugin_guid "<%= options['plugin_guid'] %>"
  test_rounds <%= options['test_rounds'] %>
  tester :file => "<%= options['tester_file'] %>", 
    :executable => "<%= options['tester_executable'] %>",
    :contestant_name => "<%= options['tester_contestant_name'] %>"

  league do
    rounds <%= options['league_rounds'] %>
  end

  finale do
    winner_certain? do |match|
      result = match.result :victories
      if result.nil?
        false
      else
        rounds_to_play = match.rounds.count - match.rounds.played.count
        diff = (result[0].to_f - result[1].to_f).abs
        diff > rounds_to_play * 2
      end
    end 

    day :quarter_final,
        :human_name => "Viertelfinale",
        :order => 1, 
        :use => {:best => 8},
        :from => :contest,
        :reorder_slots => true,  # 1st vs 8th, 2nd vs 7th, ...
        :editable => true,       # admin may change lineup
        :lineup_publishable => true, # lineup may be made public
        :ranking => {
          5 => :losers
        }

    day :half_final,
        :human_name => "Halbfinale",
        :order => 2,
        :depends => [:quarter_final],
        :use => :winners,
        :from => :quarter_final

    day :small_final,
        :human_name => "Kleines Finale",
        :order => 3,
        :depends => [:half_final],
        :use => :losers,
        :from => :half_final,
        :ranking => {
          3 => :winners,
          4 => :losers
        }

    day :final,
        :human_name => "Finale",
        :order => 4,
        :depends => [:half_final],
        :use => :winners,
        :multipleWinners => :true,
        :from => :half_final,
        :ranking => {
          1 => :winners,
          2 => :losers
        }
            

  end

  round_score do
    # NOTE: Only [a-z0-9_], no capitals!
    field :victory, :ordering => "DESC"
    #field :average_time, :ordering => "ASC", :precision => 2
  end

  match_score do
    field :points, :ordering => "DESC", :aggregate => :sum do |my_scores, their_scores|

      victories = calculate_victories(my_scores, their_scores)

      if victories[:mine] > victories[:theirs]
        2
      elsif victories[:mine] == victories[:theirs]
        1
      else
        0
      end
    end

    field :victories, :ordering => "DESC", :aggregate => :sum do |my_scores, their_scores|
      victories = calculate_victories(my_scores, their_scores)
      victories[:mine]
    end

    #field :average_time, :average => :average_time, :ordering => "ASC"

    main :victories
  end
end
