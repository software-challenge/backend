class CreateLoginTokens < ActiveRecord::Migration
  def self.up
    create_table :login_tokens do |t|
      t.integer :person_id
      t.string :code
      t.timestamps
    end
  end

  def self.down
    drop_table :login_tokens
  end
end
