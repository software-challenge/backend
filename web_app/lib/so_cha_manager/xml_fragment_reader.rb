module SoChaManager

  class XmlFragmentReader < Nokogiri::XML::SAX::Document
    
    include Loggable
    
    EVENT_LEVEL = 1
   
    def initialize(&block)
      @level = 0
      @calls = []
      @procs = []
      @processor = block
    end
    
    def characters(string)
      if @level > EVENT_LEVEL
        msg = string.strip
        @calls << msg unless msg.empty?
      end
    end

    def end_document
      logger.info "the document has ended"
    end

    def end_element name
      @level -= 1
      
      if @level >= EVENT_LEVEL
        old_calls = @calls
        @calls = @procs.pop
        last = @calls.pop
        @calls.push(Proc.new do |xml,p|
          last.call(xml, old_calls)
        end) if last
      end
      
      if @level == EVENT_LEVEL
        output = ""
        builder = Builder::XmlMarkup.new(:target => output, :indent => 2)
        @calls.first.call(builder, [])
        @calls = []
        
        if @processor
          dom = Nokogiri::XML(output)
          @processor.call dom.root.name, dom
        else
          logger.warn "No processor registered: " + output
        end
      end
    end

    def start_element name, attributes = []
      if @level >= EVENT_LEVEL
        @calls << Proc.new do |*args|
          xml, procs = *args
          hash = {}
          0.upto(attributes.count/2 - 1) do |i|
            k,v = *attributes.values_at(i*2, i*2+1)
            hash[k] = v
          end
          
          if procs.count == 0
            xml.__send__(name, hash)
          elsif procs.count == 1 and procs.first.is_a? String
            xml.__send__(name, procs.first, hash)
          else
            xml.__send__(name, hash) do |xml|
              procs.each do |proc|
                proc.call xml, []
              end
            end
          end
        end
        
        @procs.push @calls
        @calls = []
      end
      
      @level += 1
    end
    
    def error(message)
      raise "Invalid XML: #{message}"
    end
  end

end