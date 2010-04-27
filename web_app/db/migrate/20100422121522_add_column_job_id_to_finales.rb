class AddColumnJobIdToFinales < ActiveRecord::Migration
  def self.up
    add_column :finales, :job_id, :integer
  end

  def self.down
    remove_column :finales, :job_id
  end
end
