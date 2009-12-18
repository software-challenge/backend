module SoChaManager

  class Client
    
    include Loggable
    
    MAX_WAIT = 30
    
    def done?; @done; end
    def done=(x); @done=x; end

    def self.method_with_callback(method, response, &block)
      define_method "__real__#{method}" do |*args|
        self.instance_exec *args, &block
      end
    
      module_eval %{
        def #{method}(*args, &block)
          if block_given?
            @response_handlers[:#{method}] = block
            @response_handlers[:#{response}] = :#{method}
          end
          self.__real__#{method}(*args)
        end
      }
    end

    def initialize(ip, port)
      @connection = TCPSocket.new ip, port
      @processor = XmlFragmentReader.new { |*args| on_event(*args) }
      @parser = Nokogiri::XML::SAX::PushParser.new(@processor, 'utf-8')
      @response_handlers = {}
      @done = false
      start
    end
    
    method_with_callback :join, :joined do |game_type|
      write %{
        <join gameType="#{game_type}" />
      }
    end
    
    method_with_callback :prepare, :prepared do |game_type, player_names|
      write %{<prepare gameType="#{game_type}">}
      
      player_names.each do |player_name|
        write %{<slot displayName="#{player_name}" canTimeout="true" shouldBePaused="true" />}
      end
   
      write %{</prepare>}
    end
    
    def finalize
      write "</object-stream>"
    end
    
    def close
      if done?
        logger.warn "Tried to closed an already closed client."
      else
        self.done = true
        @connection.close
      end
    rescue => e
      logger.error e
    end
    
    protected
    
    def write(data)
      @connection.write_nonblock(%{#{data}\n})
    end
    
    def invoke_handler(handler, success, data)
      handler = @response_handlers.delete handler.to_sym
      if handler.is_a? Symbol
        invoke_handler(handler, success, data)
      elsif handler
        handler.call(success, data)
      end
    rescue => e
      logger.error "Failed to invoke handler '#{handler}': #{e.class.name}\n#{e}\n#{e.backtrace.join("\n")}"
    end
    
    def on_event(what, fragment)
      logger.info "Event '#{what}' received"
      
      case what
        when "error"
          caused_by = fragment.xpath('//originalRequest').first.attributes['class'].value
          invoke_handler(caused_by, false, fragment)
        else
          invoke_handler(what, true, fragment)
      end
    rescue => e
      logger.warn "EventProcessing failed: #{e}, #{e.backtrace.join("\n")}"
    end
    
    def start
      self.done = false
      write "<object-stream>"

      Thread.new do
        @last_data = Time.now
        
        begin
          while !done? do
            @parser << @connection.read_nonblock(1024)
            @last_data = Time.now
          end
        rescue Errno::EAGAIN, Errno::EWOULDBLOCK
          diff = Time.now - @last_data
          sleep 0.01
          retry if diff <= MAX_WAIT
          logger.warn "Timeout."
        rescue EOFError
          logger.info "EOF reached."
          @parser.finish
        rescue => e
          logger.error "Could not read:\n#{e.class.name}: #{e}\n#{e.backtrace.join("\n")}"
        ensure
          logger.info "Finalizing SoChaClient thread. #{$!}"
          close
        end
      end
      
      logger.info "Established connection."
    end
  end

end