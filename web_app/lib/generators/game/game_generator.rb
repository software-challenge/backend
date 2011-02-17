class GameGenerator < Rails::Generator::NamedBase
  def manifest
    puts "\n\n"
    @options= {'plugin_guid' => 'PLUGIN_GUID',
	       'test_rounds' => 2,
	       'tester_file' => 'TEST_CLIENT_ARCHIVE',
	       'tester_executable' => 'EXECUTABLE_PATH_IN_TEST_CLIENT_ARCHIVE',
	       'tester_contestant_name' => 'TEST_CONTESTANT_NAME',
	       'league_rounds' => 6,
               'replay_viewer' => 'true'}
    parseArgs
    record do |m|
      if @options['replay_viewer'] == 'true'
        m.directory lib_path
        m.template "_viewer.erb", File.join(lib_path,"_viewer.erb")
        m.directory img_path
          m.file "images/bg.png", File.join(img_path,"bg.png")
          m.file "images/board.png", File.join(img_path,"board.png")
          m.file "images/next.png", File.join(img_path,"next.png")
          m.file "images/pause.png", File.join(img_path,"pause.png")
          m.file "images/play.png", File.join(img_path,"play.png")
          m.file "images/prev.png", File.join(img_path,"prev.png")
          m.file "images/reset.png", File.join(img_path,"reset.png")
        m.directory css_path
        m.template "viewer.css.erb", File.join(css_path,"viewer.css")
      elsif @options['replay_viewer'] == "false"
        # ignore
      else
        puts "INVALID VALUE FOR replay_viewer, should be 'true' or 'false', given: #{@options['replay_viewer']}"
        printUsage
        exit
      end

      m.template "game_definition.rb", "config/games/"+file_name+".rb"
    end
  end
  
  def parseArgs
    args.each_with_index do |arg,i|
       parts = arg.split(":")
       if (parts.count == 2) and !!@options[parts[0]]
         @options[parts[0]] = parts[1]
       else
         puts "INVALID ARGUMENT FOUND: #{arg} \n\n"
         printUsage  
         exit
      end
     end
  end

  def lib_path
     File.join("lib","replay_viewers",file_name)
  end

  def img_path
     File.join("public","images","games","viewers",file_name) 
  end
  
  def css_path
     File.join("public","stylesheets","replay_viewers",file_name)
  end

  def printUsage
   File.open(RAILS_ROOT+"/lib/generators/game/USAGE","r") do |usage|
     while line = usage.gets
       puts line
     end
   end
  end
end
