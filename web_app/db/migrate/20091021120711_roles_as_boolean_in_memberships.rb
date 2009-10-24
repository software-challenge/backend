class RolesAsBooleanInMemberships < ActiveRecord::Migration
  def self.up
    add_column :memberships, :tutor, :boolean, :default => false, :null => false
    add_column :memberships, :teacher, :boolean, :default => false, :null => false
    remove_column :memberships, :role
  end

  def self.down
    add_column :memberships, :role, :string, :default => '', :null => false
    remove_column :memberships, :tutor
    remove_column :memberships, :teacher
  end
end
