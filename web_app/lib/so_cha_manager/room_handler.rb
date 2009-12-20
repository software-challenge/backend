module SoChaManager

  class RoomHandler

    def initialize

    end

    def on_state(state); end

    def on_result(result); end

    def on_data(data)
      type = data.attributes['class'].value
      case type
      when "memento"
        on_state(data.xpath('./*').first)
      when "result"
        on_result(data)
      else
        puts "unknown event room-event '#{type}'"
      end
    end

  end

  class ObservingRoomHandler < RoomHandler

    START_TAG = "<object-stream>"
    END_TAG = "</object-stream>"

    def initialize(io, &block)
      super()
      @data = io || ""
      @callback = block if block_given?
      append START_TAG
    end

    attr_reader :data, :result
    
    def done?; @done; end

    def on_state(state)
      append_normalized(state)
    end

    def on_result(result)
      parse_result result
      append_normalized result
      append END_TAG
    ensure
      @done = true
      @data.flush
      @callback.call(self) if @callback
    end

    protected

    def parse_result(result)
      scores = []

      result.xpath('./score').each do |score_data|
        score = []
        score_data.xpath('./part').each do |part|
          score << BigDecimal.new(part.content)
        end
        scores << score
      end

      @result = scores
    end

    # use class-attribute as node-name
    # FIXME: removes other potential attributes!
    #        <foo class="x" bar="z">  #=> <x>
    def append_normalized(data)
      if data.attributes['class'] and data.attributes['class'].value
        normalized_class = data.attributes['class'].value
        append "<#{normalized_class}>"
        data.xpath('./*').each do |element|
          append element
        end
        append "</#{normalized_class}>"
      else
        append data
      end
    end

    def append(data)
      raise "observation already done" if done?
      @data << data
      @data << "\n"
    end
  end

end