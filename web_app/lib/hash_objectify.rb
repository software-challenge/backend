class Hash
  def to_mod
    hash = self
    Module.new do
      hash.each_pair do |key, value|
        define_method key do
          value
        end
      end
    end
  end

  def to_obj
    Object.new.extend self.to_mod
  end
end