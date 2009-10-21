class CorrectPersonAttributes < ActiveRecord::Migration
  def self.up
    remove_column :people, :showEmailToOthers
    remove_column :people, :firstname
    remove_column :people, :lastname
    remove_column :people, :name

    add_column :people, :first_name, :string, :default => '', :null => false
    add_column :people, :last_name, :string, :default => '', :null => false
    add_column :people, :nick_name, :string, :default => '', :null => false
    add_column :people, :show_email_to_others, :boolean, :default => false, :null => false
  end

  def self.down
    remove_column :people, :nick_name
    remove_column :people, :first_name
    remove_column :people, :last_name
    remove_column :people, :show_email_to_others

    add_column :people, :name, :string, :default => '', :null => false
    add_column :people, :showEmailToOthers, :boolean, :default => false, :null => false
    add_column :people, :firstname, :string, :default => '', :null => false
    add_column :people, :lastname, :string, :default => '', :null => false
  end
end
