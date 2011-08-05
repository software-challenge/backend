class AddingTicketContext < ActiveRecord::Migration
  def self.up
    create_table :ticket_contexts do |t|
      t.string :project_slug
      t.integer :context_id
      t.string :context_type
      t.integer :ticket_id
    end
  end

  def self.down
    drop_table :ticket_contexts
  end
end
