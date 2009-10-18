class Sandbox
  def initialize(code)
    @code = code
  end

  def invoke(options = {})
    locals = options.delete(:locals)
    define_locals(locals) if locals
    error = result = nil
    code = @code
    Thread.start do begin
        $SAFE = 4
        result = eval(code)
      rescue => e
        error = e
      end
    end

    if error
      raise error
    else
      result
    end
  end

  private

  def define_locals(hash)
    mod = Module.new do
      hash.each_pair do |key, value|
        define_method key do
          value
        end
      end
    end
    self.extend mod
  end
end