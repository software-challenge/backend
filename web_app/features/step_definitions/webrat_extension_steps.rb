Then /^sollte ich ein "(.+)" Element haben$/ do |selector|
  response.should have_selector(selector)
end

Then /^sollte ich nicht ein "(.+)" Element haben$/ do |selector|
  response.should_not have_selector(selector)
end

# EN

Then /^I should have a "(.+)" element$/ do |selector|
  response.should have_selector(selector)
end

Then /^I should not have a "(.+)" element$/ do |selector|
  response.should_not have_selector(selector)
end