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
end
