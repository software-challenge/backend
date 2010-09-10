class RoundsController < ApplicationController
  access_control do
    default :allow
  end

  access_control :only => [:send_server_log] do
    default :deny
    allow :administrator
  end

  access_control :only => [:disqualify, :requalify] do
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

  def disqualify
    slot = RoundSlot.find(params[:slot].to_i)
    if slot.disqualify
      flash[:notice] = t "messages.contestant_disqualified"
    else
      flash[:error] = t "messages.contestant_could_not_be_disqualified"
    end
    
    redirect_to contest_matchday_match_url(@contest, slot.round.match.matchday, slot.round.match) 
  end

  def requalify
    slot = RoundSlot.find(params[:slot].to_i)
    if slot.requalify
      flash[:notice] = t "messages.contestant_requalified"
    else
      flash[:error] = t "messages.contestant_could_not_be_requalified"
    end
    redirect_to contest_matchday_match_url(@contest, slot.round.match.matchday, slot.round.match)
  end
end
