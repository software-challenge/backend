module SoChaManager

  class Client
    
    include Loggable
    
    MAX_WAIT = 2.minutes
    
    def done?; @done; end
    def done=(x); @done=x; end

    def self.method_with_callback(method, response = nil, options = {}, &block)
      define_method "__real__#{method}" do |*args|
        self.instance_exec(*args, &block)
      end

      class_name = options.delete(:class_name) || method
      class_name = class_name.to_sym

      module_eval %{
        def #{method}(*args, &block)
          if block_given?
            @response_handlers[:'#{class_name}'] = block
            #{"@response_handlers[:'#{response}'] = :'#{class_name}'" if response}
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
      @room_handlers = {}
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

    method_with_callback :observe, :observed do |room_id, passphrase|
      write %{<observe roomId="#{room_id}" passphrase="#{passphrase}" />}
    end

    def register_room_handler(room_id, handler)
      @room_handlers[room_id] = handler
    end

    def close
      if done?
        logger.warn "Tried to close an already closed connection."
      else
        logger.info "Closing client."
        self.done = true
        write "</object-stream>"
        @connection.close
      end
    rescue => e
      logger.log_formatted_exception e
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
      logger.log_formatted_exception e
    end

    def invoke_room_handler(room_id, data)
      handler = @room_handlers[room_id]

      unless handler
        logger.warn "no room handler registered for #{room_id}"
        return
      end

      handler.on_data(data)
    rescue => e
      logger.log_formatted_exception e
    end
    
    def on_event(what, document)
      logger.debug "Event '#{what}' received"
      
      case what
      when "error"
        caused_by = document.root.xpath('./originalRequest').first.attributes['class'].value
        invoke_handler(caused_by, false, document.root)
      when "room"
        room_id = document.root.attributes['roomId'].value
        invoke_room_handler(room_id, document.root.xpath('./*').first)
      else
        invoke_handler(what, true, document.root)
      end
    rescue => e
      logger.log_formatted_exception e
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