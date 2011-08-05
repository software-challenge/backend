class DeletingProjectSlugFromContexts < ActiveRecord::Migration
  def self.up
    remove_column :ticket_contexts, :project_slug
  end

  def self.down
    add_column :ticket_contexts, :project_slug, :string 
  end
end
