class AddingContestDependencyToFakeTests < ActiveRecord::Migration
  def self.up
    add_column :fake_tests, :contest_id, :integer
  end

  def self.down
    remove_column :fake_tests, :contest_id
  end
end
