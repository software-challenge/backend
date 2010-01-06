ActiveRecord::Base.transaction do
  #contest = Contest.new(
  #  :name => "Software-Challenge 2010",
  #  :game_definition => "HaseUndIgel",
  #  :active => true)
  #contest.save!

  Person.create!(:nick_name => "The Company Guy",
    :last_name => "Dude", :first_name => "Der",
    :email => "root@example.com",
    :password => "swordfish",
    :administrator => true)
end