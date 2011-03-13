class CreateNewsPosts < ActiveRecord::Migration
  def self.up
    create_table :news_posts do |t|
      t.string :title
      t.text :text
      t.text :html
      t.timestamp :published_at
      t.integer :person_id
      t.timestamps
    end
  end

  def self.down
    drop_table :news_posts
  end
end
