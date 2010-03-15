require 'test_helper'

class GameDefinitionTest < ActiveSupport::TestCase
  # Replace this with your real tests.
  test "empty aggregations" do
    definition = GameDefinition.all.first
    result = definition.aggregate_matches([])
    puts "r: #{result}"
    result.each_with_index do |part, i|
      if part.respond_to? :nan?
        assert_equal false, part.nan?, "ScoreFragment ##{i} (in #{result.join(',')}) was NaN"
      end
    end
  end

  test "should extend by cause" do
    definition = GameDefinition.all.first
    score = [0,1,2,3,"CAUSE"]
    definition.send(:extend_by_cause, [score])
    assert_equal [0,1,2,3], score
    assert_equal true, score.respond_to?(:cause)
    assert_equal "CAUSE", score.cause
  end

  test "should aggregate rounds correctly" do
    definition = GameDefinition.create :"HaseUndIgel" do
      players 2
      plugin_guid "test"
      test_rounds 2

      league do
        rounds 6
      end

      round_score do
        field :points, :ordering => "DESC"
      end

      match_score do
        field :points, :ordering => "DESC", :aggregate => :sum do |mine,others|
          mine.inject(0) do |sum, score|
            begin
            if score.cause == "OK"
              sum + score.points
            else
              sum - score.points
            end
            rescue => e
              raise score.inspect
            end
          end
        end
        
        main :points
      end
    end

    mine  = [[2,"OK"], [10,"NAY"]]
    enemy = [[1,"OK"], [3,"OK"]]

    # if CAUSE = NAY, the value is multiplied by -1 before added
    # so 2 - 10 = -8
    results = definition.send(:aggregate_rounds, mine, [enemy])
    assert_equal [-8], results
    assert_equal [[2],[10]], mine
    assert_equal ["OK", "NAY"], mine.collect(&:cause)

    # we need to reset it, since the arrays have been modified
    mine  = [[2,"OK"], [10,"NAY"]]
    enemy = [[1,"OK"], [3,"OK"]]

    results = definition.send(:aggregate_rounds, enemy, [mine])
    assert_equal [4], results
  end
end
