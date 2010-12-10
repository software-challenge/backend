class AddColumnNotifyOnNextStepToSchools < ActiveRecord::Migration
  def self.up
    add_column :schools, :notify_on_next_step, :boolean, :default => false, :null => false
  end

  def self.down
    remove_column :schools, :notify_on_next_step
  end
end
