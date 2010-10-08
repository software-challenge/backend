class CreateScheduledJobs < ActiveRecord::Migration
  def self.up
    create_table :scheduled_jobs do |t|
      t.string :name, :null => false
      t.boolean :running, :default => false
      t.datetime :last_check, :default => nil
      t.timestamps
    end
  end

  def self.down
    drop_table :scheduled_jobs
  end
end
