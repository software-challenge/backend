class AddingQuassumToEmailEvents < ActiveRecord::Migration
  def self.up
    add_column :email_events, :rcv_quassum_notification, :boolean, :default => true,  :null => false
  end

  def self.down
    remove_column :email_events, :rcv_quassum_notification
  end
end
