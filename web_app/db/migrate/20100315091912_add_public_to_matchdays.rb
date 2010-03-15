class AddPublicToMatchdays < ActiveRecord::Migration
  def self.up
    add_column :matchdays, "public", :boolean, :null => false, :default => false
  end

  def self.down
    remove_column :matchdays, "public"
  end
end
