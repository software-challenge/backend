class AddLastSeenAndLoggedInToPeople < ActiveRecord::Migration
  def self.up
    add_column :people, :last_seen, :datetime
    add_column :people, :logged_in, :boolean, :default => false, :null => false
  end

  def self.down
    remove_column :people, :last_seen
    remove_column :people, :logged_in
  end
end
