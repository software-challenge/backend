xml.instruct! :xml, :version => "1.0"
xml.rss :version => "2.0" do
  xml.channel do
    xml.title "Software-Challenge - News #{@context.name}" 
    xml.description "Aktuelles zur Software Challenge"
    xml.link url_for @context

    for post in @news_posts
      xml.item do
        xml.title post.title
        xml.description post.html
        xml.pubDate post.published_at
        xml.link url_for [@context, post]
        xml.guid url_for [@context, post]
        xml.author post.person.name
      end
    end
  end
end
