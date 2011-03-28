class AddColumnPublicToContests < ActiveRecord::Migration
  def self.up
    add_column :contests, :public, :boolean, :default => true, :null => false
  end

  def self.down
    remove_column :contests, :public
  end
end
