module FinaleHelper

  def contestants_needed_for_finale(contest = @contest)
    count = 0
    contest.game_definition.final_days.values.each do |day|
      if day[:use].is_a? Hash and day[:from] == :contest
        best = day[:use][:best]
        unless best.nil?
          if best.to_i > count
            count = best.to_i
          end
        end
      end
    end
    count
  end

end
