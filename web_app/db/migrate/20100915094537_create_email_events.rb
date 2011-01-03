class CreateEmailEvents < ActiveRecord::Migration
  def self.up
    create_table :email_events do |t|
      t.string :person_id
      t.boolean :rcv_on_matchday_played, :default => false, :null => false
      t.boolean :rcv_client_matchday_warning, :default => true, :null => false
      t.timestamps
    end
  end

  def self.down
    drop_table :email_events
  end
end
