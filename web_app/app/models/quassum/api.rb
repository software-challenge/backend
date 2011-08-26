class Quassum::Api

  def self.merge_user_attributes_with_defaults(atts = {})
     {
      'name' => atts[:name],
      'email' => atts[:email],
      'password' => atts[:password],
      'authentication_token' => atts[:authentication_token],
      'language' => atts[:language] || nil,
      'time_zone' => atts[:time_zone] || nil,
      'skip_welcome' => atts[:skip_welcome] || true,
      'suspended' => atts[:suspended] || false,
      'administrator' => atts[:administrator] || false,
      'wants_daily_briefing' => atts[:wants_daily_briefing] || false,
      'is_visible' => atts[:is_visible] || false,
      'send_mails' => atts[:send_mails] || false
    }
  end

  def self.filter_ticket_attributes(atts = {})
    {
      'title' => atts[:title],
      'assignee_id' => atts[:assignee]['id'],
      'assignee_type' => atts[:assignee]['type'],
      'description' => atts[:description],
      'status' => atts[:status],
      'priority' => atts[:priority],
      'deadline' => atts[:deadline],
      'ticket_type' => atts[:ticket_type],
      'priority_date' => atts[:priority_date]
    }
  end

  #QUASSUM['domain'] = "sunlight.informatik.uni-kiel.de" #"acme.quassum.local"
   
  # an API user can be any user in quassum
  #API_USERNAME = "sven@koschnicke.de"
  #API_AUTHENTICATION_TOKEN = "db1e123fa15bc06bfb85bcf70e99cfd7" # you have to save the token when you create the user, it is not retrieveable by the api for existing users
  #API_PASSWORD = "cd960b526"

  # currently, you have to know the project slug of the project you want to access
  # you should be able to get that by using the get_projects function (attribute is named "shortcut")
  #PROJECT_SLUG = "ein-projekt"

  # ticket_type should be in ["feature", "defect", "todo", "improvement"]
  # status should be in ["suggested", "open", "done", "rejected"]
  # priority should be in ["requirement", "very_important", "important", "average", "less_important", "unimportant"]
  def self.create_ticket(project_slug, api_user_token, api_user_password, attributes)
    ticket = filter_ticket_attributes(attributes)
    generic_post("/projects/#{project_slug}/tickets.json", {"ticket" => ticket}, api_user_token, api_user_password)
  end

  def self.update_ticket(project_slug, api_user_token, api_user_password, attributes)
    changed_ticket = filter_ticket_attributes(attributes)
    generic_put("/projects/#{project_slug}/tickets/#{attributes[:id]}.json", {"ticket" => changed_ticket}, api_user_token, api_user_password)
  end

  def self.create_comment(project_slug, ticket_id, comment_text, api_user_token, api_user_password)
    generic_post("/projects/#{project_slug}/tickets/#{ticket_id}/comments.json", {'comment' => {'ticket_id' => ticket_id, 'text' => comment_text}}, api_user_token, api_user_password)
    # FIXME: no data is returned on success
  end

  def self.get_projects
    generic_get("/projects.json")
  end

  # per_page: integer, how many tickets to get back, maximum is 100
  # page_number: which part of tickets to give back (for pagination)
  # returned is also total_entries and total_pages
  def self.get_tickets(project_slug, per_page = nil, page_number = nil)

    parameters = {}
    parameters["per_page"] = per_page.to_s unless per_page.nil?
    parameters["page_number"] = page_number.to_s unless page_number.nil?

    generic_get("/projects/#{project_slug}/tickets.json" + (parameters.nil? ? "" : "?" + parameters.map{|k,v| k + "=" + v}.join("&")))
  end

  def self.get_ticket(project_slug, ticket_id)
    generic_get("/projects/#{project_slug}/tickets/#{ticket_id}.json")
  end

  def self.get_comments(project_slug, ticket_id)
    generic_get("/projects/#{project_slug}/tickets/#{ticket_id}/comments.json")
  end

  def self.get_users
    generic_get("/users.json")
  end

  def self.get_users_of_project(project_slug)
    generic_get("/projects/#{project_slug}/users.json")
  end

  def self.create_user(api_user_token, api_user_password, attributes = {})
    # for api-users, set is_visible and send_mails to false. api-users can login through the website, so make sure the password is not exposed to the user
    # for accessing the system you need the password *and* the authentication token, you should always set both, there is no way to retrieve a generated authentication token or password
    # remember to grant the user access to the project after creation (set_user_rights function)
    puts attributes.inspect
    new_user = merge_user_attributes_with_defaults(attributes)
    generic_post("/users.json", {'user' => new_user}, api_user_token, api_user_password)
  end

  def self.update_user(user_id, api_user_token, api_user_password, attributes = {})
    changed_user = merge_user_attributes_with_defaults(attributes) 
    generic_put("/users/#{user_id}.json", {'user' => changed_user},api_user_token, api_user_password)
  end

  # role should be in ["administrator", "moderate", "edit_all", "comment_all", "see_all", "none"]
  def self.set_user_rights(user_id, project_slug, role, api_user_token, api_user_password)
    generic_put("/projects/#{project_slug}/users/#{user_id}/membership", {"membership" => {"role" => role}},api_user_token, api_user_password)
  end

  def self.get_user_memberships(user_id)
    generic_get("/users/#{user_id}/memberships.json")
  end

  # --------- generic helpers ----------------------------------
  def self.generic_get(url)
    puts "requesting #{url}..."
    http = Net::HTTP.new(QUASSUM[:domain], QUASSUM[:port])
    #http.set_debug_output $stderr

    resp = data = nil

    headers = {
      'Content-Type' => 'application/json',
      'Accept' => 'application/json'
    }

    http.start do |http|
       req = Net::HTTP::Get.new(url, headers)
       req.basic_auth QUASSUM[:user][:token], QUASSUM[:user][:password]
       resp, data = http.request(req)
    end

    puts JSON.parse(data).inspect

    raise "Quassum-API: Request failed on #{url}: #{resp.message}" unless resp.is_a? Net::HTTPSuccess 

    return data
  end

  def self.generic_post(url, post_data_hash, api_user_token, api_user_password)
    puts post_data_hash.inspect
    puts "requesting #{url}..."
    http = Net::HTTP.new(QUASSUM[:domain], QUASSUM[:port])
    #http.set_debug_output $stderr

    resp = data = nil

    headers = {
      'Content-Type' => 'application/json',
      'Accept' => 'application/json'
    }

    http.start do |http|
       req = Net::HTTP::Post.new(url, headers)
       req.basic_auth api_user_token, api_user_password
       # send data as body payload in json format
       req.body = JSON.generate post_data_hash

       resp, data = http.request(req)
    end

    puts data
    puts resp

    raise "Quassum-API: Request failed on #{url}: #{resp.message}" unless resp.is_a? Net::HTTPSuccess or resp.is_a? Net::HTTPNotAcceptable or resp.is_a? Net::HTTPRedirection 
    
    return data
  end

  def self.generic_put(url, put_data_hash, api_user_token, api_user_password)
    puts "requesting #{url}..."
    http = Net::HTTP.new(QUASSUM[:domain], QUASSUM[:port])
    #http.set_debug_output $stderr

    resp = data = nil

    headers = {
      'Content-Type' => 'application/json',
      'Accept' => 'application/json'
    }

    http.start do |http|
       req = Net::HTTP::Put.new(url, headers)
       req.basic_auth api_user_token, api_user_password
       # send data as body payload in json format
       req.body = JSON.generate put_data_hash

       resp, data = http.request(req)
    end
    puts data 
    raise "Quassum-API: Request failed on #{url}: #{resp.message}" unless resp.is_a? Net::HTTPSuccess or resp.is_a? Net::HTTPNotAcceptable or resp.is_a? Net::HTTPRedirection

    return data
  end
end
