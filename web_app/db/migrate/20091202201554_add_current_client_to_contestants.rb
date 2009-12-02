class AddCurrentClientToContestants < ActiveRecord::Migration
  def self.up
    add_column :contestants, :current_client_id, :integer
  end

  def self.down
    remove_column(:contestants, :current_client_id)
  end
end
