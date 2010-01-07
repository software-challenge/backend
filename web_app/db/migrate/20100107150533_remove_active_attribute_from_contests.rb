class RemoveActiveAttributeFromContests < ActiveRecord::Migration
  def self.up
    remove_column :contests, :active
  end

  def self.down
    raise ActiveRecord::MigrationIrreversible
  end
end
