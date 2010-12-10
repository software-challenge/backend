class AddColumnAllowSchoolRegToContest < ActiveRecord::Migration
  def self.up
    add_column :contests, :allow_school_reg, :boolean
  end

  def self.down
    remove_column :contests, :allow_school_reg
  end
end
