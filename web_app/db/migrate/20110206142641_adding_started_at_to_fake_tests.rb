class AddingStartedAtToFakeTests < ActiveRecord::Migration
  def self.up
    add_column :fake_tests, :started_at, :timestamp
  end

  def self.down
    remove_column :fake_tests, :started_at
  end
end
