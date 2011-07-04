class Role < ActiveRecord::Base
  acts_as_authorization_role

  named_scope :for_authorizable, lambda { |c| {:conditions => ["authorizable_type = ? and authorizable_id = ?",c.class.to_s,c.id]}} 

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
end
