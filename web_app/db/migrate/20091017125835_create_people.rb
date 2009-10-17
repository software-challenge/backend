class CreatePeople < ActiveRecord::Migration
  def self.up
    create_table :people do |t|
      t.string :name
      t.string :email
      t.string :password_hash
      t.string :password_salt

      t.timestamps
    end
  end

  def self.down
    drop_table :people
  end
end
