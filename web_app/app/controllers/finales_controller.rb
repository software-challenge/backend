class FinalesController < ApplicationController

  before_filter :fetch_context
  before_filter :fetch_finale

  access_control do
    allow :administrator 
   
    action :index, :match_results, :ranking, :get_finale, :get_matchday do
      allow all
    end

    action :lineup do
      allow all
    end
  end
   
  def publish
    @finale.publish
    redirect_to contest_finale_url(@contest)
  end

  def publish_lineup
    day = FinaleMatchday.find(params[:id])
    day.public = true
    day.save!
    redirect_to contest_finale_url(@contest)
  end

  def hide_lineup
    day = FinaleMatchday.find(params[:id])
    day.public = false
    day.save!
    redirect_to contest_finale_url(@contest)
  end

  def hide
    @finale.hide
    redirect_to contest_finale_url(@contest)
  end 

  def delete
    if not @finale.started?
      @contest.finale.destroy
    else
      flash[:error] = "Finals have already begun"
    end
    redirect_to contest_finale_url(@contest)
  end

  def fetch_finale
    @finale = @current_contest.finale
  end

  def index
    if (not (@contest.regular_phase_finished?)) or 
      (@finale.nil? or not @finale.has_published_lineup? and (current_user.nil? or not current_user.has_role?(:administrator)))
      redirect_to contest_url(@contest)
    end

    if not @finale.nil? and (@finale.nil? or not @finale.published?) and (current_user.nil? or not current_user.has_role?(:administrator)) and (@finale.has_published_lineup?)
      redirect_to lineup_contest_finale_url(@contest)
    end
  end

  def lineup
  end

  def match_results
    redirect_to contest_url(@contest) unless ((@contest.regular_phase_finished? and not current_user.nil? and current_user.has_role?(:administrator)) or (not @finale.nil? and @finale.published?))
    @match = FinaleMatch.find(params[:id].to_i)
  end

  def ranking
    redirect_to contest_url(@contest) unless ((@contest.regular_phase_finished? and not current_user.nil? and current_user.has_role?(:administrator)) or (not @finale.nil? and @finale.published?))
  end

  def prepare
    @contest.prepare_finale
    redirect_to contest_finale_url(@contest)
  end

  def prepare_day
    day = params[:dayname]
    @contest.finale.prepare_day day.to_sym
    redirect_to contest_finale_url(@contest)
  end

  def delete_matchday
    @matchday = Matchday.find(params[:id])
    @matchday.reset!
    if not @matchday.nil?
      @matchday.slots.destroy_all
      @matchday.matches.destroy_all
    end
    redirect_to contest_finale_url(@contest)  
  end

  def play
    @matchday = FinaleMatchday.find(params[:id])
      
    if @matchday.running?
      flash[:error] = I18n.t("messages.matchday_playing_in_progress")
    elsif @matchday.played?
      flash[:error] = I18n.t("messages.matchday_already_played")
    else
      @matchday.load_active_clients!
      Matchday.transaction do
        if @matchday.perform_delayed!
          flash[:notice] = I18n.t("messages.job_started_successfully")
        else
          flash[:error] = I18n.t("messages.job_starting_failed")
        end
      end
    end

    redirect_to contest_finale_url(@contest)
  end

  def play_all
    if not @finale.running?
      @finale.play_all 
    end
    redirect_to contest_finale_url(@contest)
  end

  def get_finale
    render :partial => "finale"
  end

  def get_matchday
    dayname = params[:dayname].to_sym
    setting = finale.settings_for(dayname)  
    render :partial => "matchday", :locals => {:dayname => daytype, :setting => setting}
  end

  def send_archive
    if @finale.finished?
      file = @finale.to_file
      send_file(file, :filename => File.basename(file), :type => 'application/zip', :stream => "false", :disposition => "attachment")
    else
      flash[:error] = "Finale not finished yet"
      redirect_to contest_finale_url(@contest) 
    end
  end

  # Lineup

  def switch_contestants
    c1 = params[:c1].to_i
    c2 = params[:c2].to_i
    day = FinaleMatchday.find(params[:day].to_i)
    slots = day.slots.find_all{|slot| slot.contestant.id == c1 || slot.contestant.id == c2}
    #if slots.count == 2
      ctemp = slots[0].contestant
      slots[0].contestant = slots[1].contestant
      slots[1].contestant = ctemp
      slots[0].save!
      slots[1].save!
    #end
    render :partial => "match_lineup", :locals => {:day => day}
  end

  protected 

  def fetch_context
    @context = @contest ? @contest : @season
  end

end
