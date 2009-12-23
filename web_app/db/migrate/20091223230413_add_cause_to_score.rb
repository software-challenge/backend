class AddCauseToScore < ActiveRecord::Migration
  def self.up
    add_column :scores, :cause, :string
  end

  def self.down
    remove_column :scores, :cause
  end
end
