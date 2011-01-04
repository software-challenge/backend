class RemoveColumnEstimatedTeamCountFromSchools < ActiveRecord::Migration
  def self.up
    remove_column :schools, :estimated_team_count
  end

  def self.down
    add_column :schools, :estimated_team_count, :integer, :null => false
  end
end
