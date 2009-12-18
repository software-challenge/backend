module SoChaManager
  class Manager
    
    IP = "127.0.0.1"
    PORT = 13050
    HUI = 'swc_2010_hase_und_igel'

    def initialize
      # nothing to do
    end
    
    def connect!(ip = IP, port = PORT, game = HUI)
      @ip, @port, @game = ip, port, game
      @client = Client.new ip, port
    end

    def play(players = [])
      player_names = players.collect &:first

      @client.prepare HUI, player_names do |success,response|
        if success
          reservations = response.xpath '//reservation'
          codes = reservations.collect &:content
          
          players = players.zip(codes)
          players.each { |p| puts "player: #{p.join(", ")}" }
        else
          exit
        end
      end
    end

    def done?
      @client.done?
    end
    
    def close
      @client.close
    end
  end
end