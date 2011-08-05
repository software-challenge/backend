class DeletingContestIdFromSchools < ActiveRecord::Migration
  def self.up
    remove_column :schools, :contest_id
  end

  def self.down
    add_column :school, :contest_id, :integer
  end
end
