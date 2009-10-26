Given /^the following contests:$/ do |contests|
  contest = Contest.create!(contests.hashes)
end

Given /(.+) ist ein Wettbewerb$/ do |contest_name|
  Given "#{contest_name} ist ein Wettbewerb mit 3 Runden pro Spiel"
end

Given /(.+) ist ein Wettbewerb mit (\d+) Runden pro Spiel/ do |contest_name, rounds_per_match|
  Contest.create!(:name => contest_name, :rounds_per_match => rounds_per_match.to_i)
end

Given /(.+) hat (\d+) Teilnehmer/ do |contest_name, contestants_count|
  contest = Contest.find_by_name(contest_name) || raise("contest not found")
  contestants_count.to_i.times do |i|
    contest.contestants.create!(:name => "#{contest.name}_#{i}")
  end
end

#And BigCup's 1st matchday has been played
Given /der (\d+)\. Spieltag von (.+) wurde gespielt/ do |matchday_position, contest_name|
  contest = Contest.find_by_name(contest_name) || raise("contest not found")
  matchday = contest.matchdays.find_by_position(matchday_position) || raise("matchday not found")

  matchday.perform
end

#And BigCup's 1st matchday has been played
Given /der (\d+)\. Spieltag von (.+) ist fehlerhaft/ do |matchday_position, contest_name|
  contest = Contest.find_by_name(contest_name) || raise("contest not found")
  matchday = contest.matchdays.find_by_position(matchday_position) || raise("matchday not found")
  matchday.slots.each do |slot|
    slot.score.fragments.each do |fragment|
      fragment.value = -1
      fragment.save!
    end
  end
end

Given /^(.+) hat ein einfaches Punktesystem$/ do |contest_name|
  contest = Contest.find_by_name(contest_name) || raise("contest not found")
  contest.create_match_score_definition || raise("couldn't create")
  contest.create_round_score_definition || raise("couldn't create")
  contest.match_score_definition.fragments.create!(:name => "foo", :direction => "asc", :main => true)
  contest.round_score_definition.fragments.create!(:name => "foo", :direction => "asc", :main => true)
  contest.script_to_aggregate_rounds = "sum_all(mine)"
  contest.script_to_aggregate_matches = "sum_all(elements)"
  contest.save!
end

Given /^(.+) hat einen Spielplan$/ do |contest_name|
  contest = Contest.find_by_name(contest_name) || raise("contest not found")
  contest.refresh_matchdays!
end