class AddingHistoryToContestants < ActiveRecord::Migration
  def self.up
    add_column :contestants, :report, :text
  end

  def self.down
    remove_column :contestants, :report
  end
end
