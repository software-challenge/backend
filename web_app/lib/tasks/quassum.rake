require "faye"
require "eventmachine"

namespace :quassum do
  desc "Sweep the Quassum-Cache so that all cached tickets, comments etc. are being deleted!"
  task :sweep_cache => :environment do
    puts "Sweeping Quassum-Cache"
    QUASSUM[:cache].clear
  end

  desc "Starts a faye-client that listens for changes made in Quassum and sweeps the cache"
  task :client => [:environment, :sweep_cache] do
    puts "Starting client"
    client = Faye::Client.new(QUASSUM[:faye][:url])
    EM.run {
      puts "Listening for messages on #{QUASSUM[:faye][:url]}/changes"
      client.subscribe('/changes') do |message|
        key, value = message.to_a[0]
        case key
          when "ticket": 
            puts "Ticket changed: #{value}"
            Quassum::Ticket.changed!(value, :sweep => true)
          when "comment": 
            puts "New or changed comment on ticket #{value}"
            Quassum::Ticket.comment_changed!(value, :sweept => true)
          else
            puts "got unknown message #{message.inspect}"
          end
      end
    }
  end

  namespace :development do
    desc "Starts a faye server that can be used for development"
    task :faye_server do
      sh "rackup -s thin -E production faye_server.ru"
    end
    
    desc "Send a test-requests to the faye server"
    task :simulate => :environment do
      ticket_id = Quassum::Ticket.index.keys.first || "1"
      client = Faye::Client.new(QUASSUM[:faye][:url])
      puts client.inspect
      EM.run{
        loop do
          puts "Publishing changes on ticket #{ticket_id}"
          client.publish("/changes", 'ticket' => ticket_id.to_s)
          puts "Publishing changes on comments of ticket #{ticket_id}"
          client.publish("/changes", 'comment' => ticket_id.to_s)
          puts "Publishing nonsens!"
          client.publish("/changes", 'car' => 'brumm') 
          puts "Sleeping for 10 seconds"
          sleep(10)
        end
      }
    end

    desc "View messages on changes channel"
    task :inspect => :environment do
      client = Faye::Client.new(QUASSUM[:faye][:url])
      puts "Listening on #{QUASSUM[:faye][:url]}/changes"
      EM.run {
        client.subscribe('/changes'){|message| puts message.inspect}
      }
    end
  end
end
