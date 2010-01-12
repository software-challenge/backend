require 'rubygems'
require 'socket'
require 'builder'
require 'logger'
require 'active_support'

gem 'nokogiri'
gem 'rubyzip'

require 'nokogiri'
require 'zip/zip'

$:.unshift File.join(File.dirname(__FILE__), 'so_cha_manager')

require 'loggable'
require 'room_handler'
require 'xml_fragment_reader'
require 'client'
require 'manager'