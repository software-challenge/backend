class AddedPlayedAtToMatch < ActiveRecord::Migration
  def self.up
    add_column :matches, :played_at, :datetime, :null => true, :default => nil
    add_column :matchdays, :played_at, :datetime, :null => true, :default => nil

    add_column :matches, :job_id, :integer, :null => true, :default => nil
    add_column :matchdays, :job_id, :integer, :null => true, :default => nil
    
    remove_column :matchdays, :played
  end

  def self.down
    raise ActiveRecord::MigrationIrreversible
  end
end
