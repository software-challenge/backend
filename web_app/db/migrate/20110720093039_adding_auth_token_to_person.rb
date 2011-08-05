class AddingAuthTokenToPerson < ActiveRecord::Migration
  def self.up
    add_column :people, :auth_token, :string
  end

  def self.down
    remove_column :people, :auth_token
  end
end
