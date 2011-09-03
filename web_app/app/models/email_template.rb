class EmailTemplate < ActiveRecord::Base
  
  validates_presence_of :template
  validates_presence_of :email_title
  validates_presence_of :title
  validates_uniqueness_of :title

  def render(arguments = {})
    if arguments[:show_dummy]
      t = template.gsub("{{","<").gsub("}}",">")
    end
    Liquid::Template.parse(t||template).render(arguments)
  end


  def self.allowed_types 
    [SurveyNotificationTemplate]
  end
end
