class AddingSurveyEmailTemplates < ActiveRecord::Migration
  def self.up
    create_table :email_templates do |t|
      t.text :template
      t.text :description
      t.string :title
      t.string :email_title
      t.string :type
      t.timestamps
    end
  end

  def self.down
    drop_table :email_templates
  end
end
