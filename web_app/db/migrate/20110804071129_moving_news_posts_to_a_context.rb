class MovingNewsPostsToAContext < ActiveRecord::Migration
  def self.up
    add_column :news_posts, :context_id, :integer
    add_column :news_posts, :context_type, :string
    NewsPost.all.each do |n|
      n.context = Contest.find_by_subdomain(2011) || Contest.last
      n.save
    end
  end

  def self.down
    remove_column :news_posts, :context_id
    remove_column :news_posts, :context_type
  end
end
