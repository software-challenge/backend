class AddColumnRcvContestProgressInfoToEmailEvents < ActiveRecord::Migration
  def self.up
    add_column :email_events, :rcv_contest_progress_info, :boolean, :null => false, :default => false
  end

  def self.down
    remove_column :email_events, :rcv_contest_progress_info
  end
end
