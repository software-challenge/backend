class AddColumnContestIdToFinale < ActiveRecord::Migration
  def self.up
    add_column :finales, :contest_id, :integer
  end

  def self.down
    remove_column :finales, :contest_id
  end
end
