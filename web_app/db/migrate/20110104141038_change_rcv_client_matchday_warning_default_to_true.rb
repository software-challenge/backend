class ChangeRcvClientMatchdayWarningDefaultToTrue < ActiveRecord::Migration
  def self.up
    change_column :email_events, :rcv_client_matchday_warning, :boolean, :default => true, :null => false
  end

  def self.down
    change_column :email_events, :rcv_client_matchday_warning, :boolean, :default => false, :null => false
  end
end
