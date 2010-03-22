class RoundsController < ApplicationController
  access_control do
    default :allow
  end

  access_control :only => [:send_server_log] do
    default :deny
    allow :administrator
  end
  
  def show
    @round = Round.find(params[:id])
    send_file @round.replay.path, :disposition => 'inline'
  end

  def send_server_log
      round_id = params[:round_id].to_i
      file = File.join(ENV['SERVER_LOGS_FOLDER'], round_id.to_s + ".log")
      dateinfo = File.mtime(file).strftime("%y-%m-%d_%H-%M")
      send_file file, :filename => "svr_#{dateinfo}_" + round_id.to_s + ".log", :type => 'text', :stream => "false", :disposition => "attachment"
  end
end
