class Role < ActiveRecord::Base
  
  named_scope :helper, :conditions => {:name => "helper"}
  named_scope :tutor, :conditions => {:name => "tutor"}

  named_scope :for, lambda{|d| {:conditions => {:authorizable_type => d.class.to_s, :authorizable_id => d.id}}}

  named_scope :for_contestants, :conditions => {:authorizable_type => "Contestant"}
  named_scope :helper_or_tutor, :conditions => ["name = 'helper' OR name = 'tutor'"]
  acts_as_authorization_role

  named_scope :for_authorizable, lambda { |c| {:conditions => ["authorizable_type = ? and authorizable_id = ?",c.class.to_s,c.id]}} 

  after_save :sweep

  def self.translate(name, options = {})
    category = options[:for]
    category = case category
    when NilClass
      'global'
    when String, Symbol
      category
    when Class
      category.name
    else
      category.class.name
    end

    category = category.downcase

    I18n.t("acl9.roles.#{category}.#{name}", :default => "#{category}.#{name}")
  end

  def to_s
    Role.translate(name, :for => authorizable)
  end

  def sweep
    people.each{|p| p.sweep}
  end
end
