Given /^ich bin ein Administrator$/ do
  email = "foo"
  password = "bar"
  name = "nemo"
  Person.create!(:nick_name => name, :email => email, :administrator => true) do |p|
    p.password = password
  end

  Given 'I am on the login page'
  Given 'I fill in "user_email" with "' + email + '"'
  Given 'I fill in "user_password" with "' + password + '"'
  Given 'I press "login"'
end
