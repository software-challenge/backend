class AddingFinishedTimestampToFaketests < ActiveRecord::Migration
  def self.up
    add_column :fake_checks, :finished_at, :timestamp
  end

  def self.down
    remove_column :fake_checks, :finished_at
  end
end
