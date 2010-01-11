ActiveRecord::Base.transaction do
  admin = Person.create!(:nick_name => "The Company Guy",
    :last_name => "Dude", :first_name => "Der",
    :email => "root@example.com",
    :password => "swordfish")

  admin.has_role!(:administrator)

  ActiveRecord::Base.current_user = admin

  contest = Contest.create!(
    :name => "Software-Challenge 2010",
    :game_definition => "HaseUndIgel", :subdomain => "2010")

end
