require 'rubygems'
require 'socket'
require 'builder'
require 'logger'

gem 'nokogiri'
gem 'rubyzip'

require 'nokogiri'
require 'zip/zip'

$:.unshift File.join(File.dirname(__FILE__), 'so_cha_manager')

require 'core_ext'
require 'loggable'
require 'room_handler'
require 'xml_fragment_reader'
require 'client'
require 'manager'