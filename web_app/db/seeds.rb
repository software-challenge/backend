ActiveRecord::Base.transaction do
  contest = Contest.new(:name => "Software-Challenge 2010", :active => true)
  contest.save!

  password = "swordfish"
  salt = "the_salt"
  encrypted_password = Person.encrypt_password(password, salt)
  person = Person.new(:nick_name => "The Company Guy", :last_name => "Dude", :first_name => "Der", :email => "root@example.com", :password_hash => encrypted_password, :password_salt => salt, :administrator => true)
  person.save!
end