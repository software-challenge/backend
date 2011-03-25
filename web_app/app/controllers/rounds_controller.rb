class RoundsController < ApplicationController
  access_control do
    default :allow
  end

  access_control :only => [:send_server_log] do
    default :deny
    allow :administrator
  end

  access_control :only => [:disqualify, :requalify, :reset] do
    default :deny
    allow :administrator
  end
  
  def show
    @round = Round.find(params[:id])
    respond_to do |format|
      format.html { send_file @round.replay.path, :disposition => 'inline' }
      format.xml   {
        xml_handler = File.join(RAILS_ROOT, "lib", "replay_viewers", @contest.game_definition.game_identifier.to_s.underscore, "_replay.xml.erb")
        unless File.exists? xml_handler
         xml_handler = File.join(RAILS_ROOT,"lib","replay_viewers","_generic_replay.xml.erb")
        end
        render :file => xml_handler, :locals => {:replay => @round.replay.path}
        response.headers["Content-Type"] = "application/xml; charset=utf-8"}
    end
  end

  def send_server_log
      round_id = params[:round_id].to_i
      file = File.join(ENV['SERVER_LOGS_FOLDER'], round_id.to_s + ".log")
      dateinfo = File.mtime(file).strftime("%y-%m-%d_%H-%M")
      send_file file, :filename => "svr_#{dateinfo}_" + round_id.to_s + ".log", :type => 'text', :stream => "false", :disposition => "attachment"
  end

  def show_replay
    @round = Round.find(params[:id])
    if @round.match.respond_to?(:matchday) 
      replay_url = contest_matchday_match_round_url(@contest,@round.match.matchday,@round.match,@round, :xml)
    elsif @round.match.is_a? CustomMatch 
      replay_url = contest_custom_match_round_url(@contest,@match,@round, :xml)
    end
 
    game_identifier = @contest.game_definition.game_identifier.to_s.underscore  
    render :file => File.join(RAILS_ROOT, "lib", "replay_viewers", game_identifier, "_viewer.erb"), :locals => {:replay_url => replay_url, :autoplay => false, :image_path => "/images/games/viewers/#{game_identifier}", :stylesheet_path => "/stylesheets/replay_viewers/#{game_identifier}"}
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

  def reset
    @round = Round.find_by_id(params[:id])
    @round.reset!
    redirect_to contest_matchday_match_url(@contest,@round.match.matchday,@round.match) 
  end
end
