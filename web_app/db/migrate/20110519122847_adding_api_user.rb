class AddingApiUser < ActiveRecord::Migration
  def self.up
    create_table :api_users do |t|
      t.integer :person_id
      t.string :api_username
      t.string :api_token
      t.string :api_password
      t.integer :api_user_id
    end
  end

  def self.down
    drop_table :api_users
  end
end
