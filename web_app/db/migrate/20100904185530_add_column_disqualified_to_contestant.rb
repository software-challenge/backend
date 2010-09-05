class AddColumnDisqualifiedToContestant < ActiveRecord::Migration
  def self.up
    add_column :contestants, :disqualified, :boolean, :default => false
  end

  def self.down
    remove_column :contestants, :disqualified
  end
end
