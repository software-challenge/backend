class AddAcl9Tables < ActiveRecord::Migration
  def self.up
    create_table "roles", :force => true do |t|
      t.string   :name,              :limit => 40
      t.string   :authorizable_type, :limit => 40
      t.integer  :authorizable_id
      t.timestamps
    end

    create_table "people_roles", :id => false, :force => true do |t|
      t.references  :person
      t.references  :role
      t.timestamps
    end
  end

  def self.down
    drop_table :roles
    drop_table :people_roles
  end
end
