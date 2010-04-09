class AddColumnOverallMemberCountToTableContestants < ActiveRecord::Migration
  def self.up
    add_column :contestants, "overall_member_count", :integer, :null => false, :default => 0
  end

  def self.down
    remove_column :contestants, "overall_member_count"
  end
end
