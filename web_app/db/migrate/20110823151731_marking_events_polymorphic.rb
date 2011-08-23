class MarkingEventsPolymorphic < ActiveRecord::Migration
  def self.up
    rename_column :events, :contest_id, :context_id
    add_column :events, :context_type, :string

    Event.all.each do |e| 
	e.context_type = "Contest"
	e.save!
    end
  end

  def self.down
    remove_column :events, :context_type
    rename_column :events, :context_id, :contest_id
  end
end
