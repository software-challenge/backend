class AddColumnSchoolIdToContestant < ActiveRecord::Migration
  def self.up
    add_column :contestants, :school_id, :integer
  end

  def self.down
    remove_column :contestants, :school_id
  end
end
