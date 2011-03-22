class AddingMatchdayReleasedEvent < ActiveRecord::Migration
  def self.up
    add_column :email_events, "rcv_on_matchday_published", :boolean, :default => false, :null => false
    add_column :matchdays, :first_published_at, :timestamp
  end

  def self.down
    remove_column :email_events, "rcv_on_matchday_released"
    remove_column :matchdays, :first_published_at
  end
end
