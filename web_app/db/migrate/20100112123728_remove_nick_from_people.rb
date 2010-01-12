class RemoveNickFromPeople < ActiveRecord::Migration
  def self.up
    remove_column :people, :nick_name
  end

  def self.down
    raise ActiveRecord::IrreversibleMigration
  end
end
