class Quassum::Comment
  attr_accessor :updated_at, :created_at, :editable_until, :editable, :author_id, :id, :text, :_meta, :ticket_id

  def self.build(args)
    comment = self.new
    comment.apply_attributes(args)
    comment
  end

  def author
    Quassum::ApiUser.find_by_api_user_id(author_id) 
  end

  def apply_attributes(args)
    args.each do |k,v|
      begin send(k+"=",v) rescue nil end
    end
  end

  def html_text
    return @html_txt if @html_txt
    @html_text = BlueCloth.new(text).to_html
  end
end
