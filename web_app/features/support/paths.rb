module NavigationHelpers
  # Maps a name to a path. Used by the
  #
  #   When /^I go to (.+)$/ do |page_name|
  #
  # step definition in webrat_steps.rb
  #
  def path_to(page_name)
    case page_name
    
    when /the home\s?page/
      '/'
    when /the login page/
      login_path
    when /(?:der|die) Seite vom (.+)\. Spieltag von (.+)/
      contest = Contest.find_by_name($2)
      matchday = contest.matchdays.find_by_position($1)
      raise "#{$2} matchday does not exist" unless matchday
      contest_matchday_path(contest, matchday)

    
      # Add more mappings here.
      # Here is a more fancy example:
      #
      #   when /^(.*)'s profile page$/i
      #     user_profile_path(User.find_by_login($1))

    else
      raise "Can't find mapping from \"#{page_name}\" to a path.\n" +
        "Now, go and add a mapping in #{__FILE__}"
    end
  end
end

World(NavigationHelpers)
