class Quassum::RemoteModel 
  attr_accessor :id
  attr_accessor :errors # for active record validation

  def self.find(id)
    ticket = QUASSUM[:cache].read("#{class_name}_#{id}")
    if ticket.nil?
      begin
         ticket = build(JSON.parse(Quassum::Api.send("get_#{class_name}",QUASSUM[:project_slug],id))[class_name])
      rescue Exception => e
         puts e.message
         puts e.backtrace
       end
    end
    ticket
  end

  def self.class_name 
    self.to_s.split("::").last.downcase.to_s
  end

  def class_name
    self.class.class_name
  end

  def new_record?
   !id
  end

  def initialize(opts = {})
    @errors = ActiveRecord::Errors.new(self)
  end

  def self.sweep_all
    ind = index
    for i in ind.keys
      QUASSUM[:cache].delete("#{class_name}_#{i}")
    end
    QUASSUM[:cache].delete(index_name)
  end

  def sweep
    self.class.sweep(id)
  end  

  def self.sweep(id)
    QUASSUM[:cache].delete("#{class_name}_#{id}") and remove_from_index(id) 
  end

  # build the Quassum::Ticket object for a hash
  def self.build(hash)
     t = new
     t.apply_attributes(hash)
     t.cache_save
     t
  end

  def update_attributes(args = {})
    args.each do |k,v|
      send(k.to_s+"=",v) if respond_to? k.to_s+"="
    end
    save
  end

  def update_attribute(attribute, value)
    send(attribute.to_s+"=",valu) if respond_to? attribute.to_s+"="
  end

  def self.create(args = {})
    model = new
    model.update_attributes(args)
    # FIXME update should return new object
    model
  end

  def attributes
    atts = {}
    instance_variables.each do |i|
      k = i.delete("@")
      next if k == "errors"
      atts[k.to_sym] = send(k) if respond_to? k
    end
    atts
  end

  def save!
    raise "Validations failed!" unless valid?
    if new_record?
      t = Quassum::Api.send("create_#{class_name}", QUASSUM[:project_slug], QUASSUM[:user][:token], QUASSUM[:user][:password], attributes)
    else
      t = Quassum::Api.send("update_#{class_name}", QUASSUM[:project_slug], QUASSUM[:user][:token], QUASSUM[:user][:password], attributes)
    end
    apply_attributes(JSON.parse(t)["ticket"])
    cache_save
  end

  def save
    begin
      save!
      true
    rescue Exception => e
      puts e.message
      puts e.backtrace
      false
    end
  end

  def cache_save
    QUASSUM[:cache].write("#{class_name}_#{id}",self)
    self.class.add_to_index(id)
  end

  def self.index
    QUASSUM[:cache].read(index_name) || {}
  end

  def self.index_name
    class_name.pluralize
  end

  def self.add_to_index(id)
    ind = index
    ind[id] = true
    QUASSUM[:cache].write(index_name, ind)
  end

  def self.remove_from_index(id)
    ind = index
    ind[id] = nil
    QUASSUM[:cache].write(index_name,ind)
  end

  def try_set(attribute_name,value)
    begin 
      send(attribute_name+"=", value); true 
    rescue 
      false 
    end
  end

  def apply_attributes(hash)
    hash.each do |k,v|
      if k == "to_param"
        apply_attributes(v)
      else
        next if k == "id" and id
        k = "id" if k == "scoped_id"
        try_set(k,v)
      end
    end
  end

  def self.human_name
    self.to_s.capitalize
  end

  def self.human_attribute_name(attribute)
    attribute.to_s
  end

  def self.self_and_descendants_from_active_record
    [self]
  end

  include ActiveRecord::Validations
end
