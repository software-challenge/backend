class AddReplayToRound < ActiveRecord::Migration
  def self.up
    add_column :rounds, :replay_file_name, :string
    add_column :rounds, :replay_content_type, :string
    add_column :rounds, :replay_file_size, :integer
  end

  def self.down
    remove_column :rounds, :replay_file_name
    remove_column :rounds, :replay_content_type
    remove_column :rounds, :replay_file_size
  end

end
