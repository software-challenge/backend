require 'rubygems'
require 'socket'
require 'builder'
require 'nokogiri'

IP = "127.0.0.1"
PORT = 13050

# Available with Ruby 1.9
# http://blog.jayfields.com/2006/09/ruby-instanceexec-aka-instanceeval.html
class Object
  module InstanceExecHelper; end
  include InstanceExecHelper
  def instance_exec(*args, &block)
    begin
      old_critical, Thread.critical = Thread.critical, true
      n = 0
      n += 1 while respond_to?(mname="__instance_exec#{n}")
      InstanceExecHelper.module_eval{ define_method(mname, &block) }
    ensure
      Thread.critical = old_critical
    end
    begin
      ret = send(mname, *args)
    ensure
      InstanceExecHelper.module_eval{ remove_method(mname) } rescue nil
    end
    ret
  end
end

class MyDocument < Nokogiri::XML::SAX::Document
 
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
    puts "the document has ended"
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
        puts output
      end
    end
  end

  def start_element name, attributes = []
    if @level >= EVENT_LEVEL
      data = ""
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
        data
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

class SoChaClient
  attr_accessor :done

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

  def initialize(connection)
    @connection = connection
    @processor = MyDocument.new { |*args| on_event(*args) }
    @parser = Nokogiri::XML::SAX::PushParser.new(@processor, 'utf-8')
    @response_handlers = {}
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
  
  protected
  
  def write(data)
    @connection.write_nonblock(%{#{data}\n})
  end
  
  def on_event(what, fragment)
    puts "Event #{what} received."
    
    case what
      when "error"
        caused_by = fragment.xpath('//originalRequest').first.attributes['class'].value
        handler = @response_handlers.delete caused_by.to_sym
        handler.call(false, fragment) if handler
      else
        handler = @response_handlers.delete what.to_sym
        
        while handler.is_a? Symbol
          handler = @response_handlers.delete handler
        end
        
        handler.call(true, fragment) if handler
    end
  rescue => e
    puts "EventProcessing failed: #{e}, #{e.backtrace.join("\n")}"
  end
  
  def start
    self.done = false
    write "<object-stream>"

    Thread.new do
      @last_data = Time.now
      
      begin
        while true do
          @parser << @connection.read_nonblock(1024)
          @last_data = Time.now
        end
      rescue Errno::EAGAIN, Errno::EWOULDBLOCK
        sleep 0.01
        
        diff = Time.now - @last_data
        retry if diff <= 3
        puts "Timeout."
      rescue EOFError
        puts "EOF reached."
      rescue => e
        puts "read failed: #{e.class.name} #{e} #{e.backtrace.join("\n")}"
      ensure
        self.done = true
        @parser.finish
        @connection.close
      end
    end
    
    puts "Established connection."
  end
end

socket = TCPSocket.new IP, PORT
client = SoChaClient.new socket

Signal.trap "INT" do
  puts "Terminating."
  socket.close
  exit
end

HUI = 'swc_2010_hase_und_igel'

players = [["p1", "mip.zip"], ["p2", "nam.zip"]]

def start_client()

end

player_names = players.collect &:first

client.prepare HUI, player_names do |success,response|
  if success
    reservations = response.xpath '//reservation'
    codes = reservations.collect &:content
    
    players = players.zip(codes)
    players.each { |p| puts "player: #{p.join(", ")}" }
  end
end

#client.join do |success, response|
#  puts "join: #{success}"
#end

while !client.done
  sleep 0.1
end
