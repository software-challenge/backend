require 'comment'
require 'api_user'

class Quassum::Ticket < Quassum::RemoteModel

  STATES = ["suggested", "open", "done", "rejected"]
  PRIORITIES = ["requirement", "very_important", "important", "average", "less_important", "unimportant"]
  TICKET_TYPES = ["feature", "defect", "todo", "improvement"]

  attr_accessor :title, :description, :status, :priority, :deadline, :ticket_type, :priority_date, :assignee, :done

  validates_presence_of :title, :assignee, :description
  validates_inclusion_of :status, :in => STATES
  validates_inclusion_of :priority, :in => PRIORITIES 
  validates_inclusion_of :ticket_type, :in => TICKET_TYPES 

  def possible_states 
    STATES - ["suggested", "rejected"]
  end

  def possible_priorities
    PRIORITIES
  end
  
  def possible_ticket_types
    TICKET_TYPES
  end

  def html_description
    return @html_desc if @html_desc
    desc = BlueCloth.new(@description).to_html
    begin
      @html_desc =  desc
    rescue
      return desc
    end
  end

  def plain_description
    @description
  end

  def assignee_name
    return nil unless assignee
    if assignee['type'] == "User"
      assigned_person.name
    else
      assignee['name']
    end
  end

  def assignee=(tgt)
    if tgt.is_a? String
      type,id = tgt.split(":")
      if type == "User"
        tgt = Quassum::ApiUser.find_by_api_user_id(id)
      elsif type == "Person"
        tgt = Person.find_by_id(id).get_or_create_api_user
      elsif type == "Group"
        tgt = Quassum::Group.find_by_group_id(id)
      end
    end
    assignee = case tgt
      when Hash 
        tgt
      when Quassum::ApiUser 
        {"name" => tgt.api_username, "id" => tgt.api_user_id, "type" => "User"}
      when Quassum::Group
        {"name" => tgt.name, "id" => tgt.id, "type" => "Group"}
      else 
        raise "Invalid assignee!"
    end
    @assignee = assignee
  end

  def description
    str = @description 
    str += "\n\n\n\n**Meta:**\n\n\n**Context**: #{contexts.first.context.name}" unless contexts.empty?
    unless attachments.empty?
      str += "\n\n**Anhänge**:\n\n"
      links = ""
      attachments.each_with_index do |a,i|
        str += "- [#{a.file_file_name} vom #{I18n.l(a.updated_at.to_date)}][#{i}]\n"
        links += "[#{i}]: #{attachment_ticket_url(:id => id, :attachment_id => a.id, :host => QUASSUM[:webapp])}\n"
      end
      str += "\n\n" + links
    end
    str
  end
  
  def description=(str)
    @description = str.split("**Meta:**")[0]
  end

  def comments
    c = QUASSUM[:cache].read("#{self.class_name}_#{id}_comments") 
    c = fetch_comments unless c 
    c
  end

  def fetch_comments
    comments = (JSON.parse(Quassum::Api.get_comments(QUASSUM[:project_slug], id))).map do |c|
      Quassum::Comment.build(c)
    end
    QUASSUM[:cache].write("#{self.class_name}_#{id}_comments", comments)
    comments
  end

  def sweep_comments
    self.class.sweep_comments(id)
  end

  def self.sweep_comments(id)
    QUASSUM[:cache].delete("#{self.class_name}_#{id}_comments")
  end

  def create_comment(text,api_user)
    begin
      Quassum::Api.create_comment(QUASSUM[:project_slug], id, text, api_user.api_token, api_user.api_password)
      # TODO: fix when content is returned right!
      fetch_comments
      true
    rescue Exception => e
      puts e.message
      puts e.backtrace
      false
    end
  end

  def contexts
    Quassum::TicketContext.find_all_by_ticket_id(id)
  end

  def assigned_person
    if assignee['type'] == "User"
      api_user = Quassum::ApiUser.find_by_api_user_id(assignee['id'])
      api_user.person if api_user
    else
      nil
    end
  end

  def self.all_for_person(person)
    return [] unless person
    contexts = person.contestants.map{|c| c.ticket_contexts}.select{|c| c.length > 0}
    tickets = contexts.flatten.map{|tc| tc.ticket}
    tickets.uniq
  end

  def self.human_attribute_name(attribute)
    begin I18n.t!("views.quassum.ticket.attributes.#{attribute.to_s}") rescue attribute.to_s.capitalize end
  end

  def attachments
    Quassum::TicketAttachment.find_all_by_ticket_id(id)
  end

  def self.changed!(id, args = {})
    puts "Ticket #{id} was changed! => sweeping"
    # If you need the new informations according the ticket sweep first
    EventMailer.deliver_ticket_or_comment_changed_notification(find(id))
    Quassum::Ticket.sweep(id) if args[:sweep]
    true
  end

  def self.comment_changed!(id, args = {})
    puts "Comments of #{id} were changed! => sweeping"
    EventMailer.deliver_ticket_or_comment_changed_notification(find(id))
    Quassum::Ticket.sweep_comments(id) if args[:sweep]
    true
  end

end


include ActionController::UrlWriter
