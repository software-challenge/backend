class FriendlyEncountersController < ApplicationController

before_filter :fetch_contestants
before_filter :ensure_login

access_control do
  allow all

  action :all do
    allow :administrator
  end
end

def fetch_contestants
  if logged_in?
    @contestants = current_user.contestants
  end
end

def ensure_login
  redirect_to contest_url if not logged_in?
  #redirect_to contest_url if not @contest.name.downcase.include?("test")
end

def show
  fe = FriendlyEncounter.find(params[:id])
  redirect_to contest_friendly_encounters if not fe.played?
  @match = fe.friendly_match
end

def status
  fe = FriendlyEncounter.find(params[:id])
  with_result = params[:with_result] || false
  reverse = (params[:reverse] and params[:reverse] == "true" ? true : false)
  spinner = params[:spinner] || false
  render :text => @template.encounter_status(fe, :with_result => with_result, :reverse => reverse, :spinner => spinner)
end 

def hide
  con = Contestant.find(params[:contestant])
  enc = FriendlyEncounter.find(params[:id])
  slot = enc.slot_for(con)
  unless slot.nil?
    slot.hidden = true
    slot.save!
  end
  redirect_to contest_friendly_encounters_url
end

def unhide
  con = Contestant.find(params[:contestant])
  enc = FriendlyEncounter.find(params[:id])
  slot = enc.slot_for(con)
  unless slot.nil?
    slot.hidden = false
    slot.save!
  end
  redirect_to contest_friendly_encounters_url
end

def create
  con1 = Contestant.find(params[:encounter][:con1].to_i)
  raise "Diese Aktion ist nicht erlaubt" if not current_user.has_role_for? con1
  con2 = params[:encounter][:con2]
  con2 = con2 == "" ? nil : Contestant.find(con2.to_i)

  if con2 == con1
    flash[:error] = t("messages.team_cant_play_against_self")
  end

  if con2.nil?
    enc = @contest.friendly_encounters.to_ary.find{|enc| enc.contestants.include?(con1) and enc.open_for.nil? and enc.open?}
  else
    enc = @contest.friendly_encounters.to_ary.find{|enc| enc.contestants.include?(con1) and enc.open_for == con2 and (enc.open? or enc.rejected? or enc.ready?)}
  end

  if not enc.nil?
    flash[:error] = t("messages.such_request_already_exists")
  end

  con2 = :all if con2.nil?

  if not flash[:error]
    con1.open_friendly_encounter_request :to => con2
  end

  redirect_to contest_friendly_encounters_url 
end

  def all
    @encounters = @contest.friendly_encounters
  end

  def destroy
    enc = FriendlyEncounter.find(params[:id])
    con = enc.contestants.first
    raise t("messages.action_now_allowed") if not current_user.has_role_for? con
    
    enc.destroy
    redirect_to contest_friendly_encounters_url 
  end

  def reject
    enc = FriendlyEncounter.find(params[:id])
    raise t("messages.action_not_allowed") if not enc.rejectable_by?(current_user)

    enc.reject
    redirect_to contest_friendly_encounters_url
  end

  def accept
    enc = FriendlyEncounter.find(params[:id])
    con = Contestant.find(params[:contestant])
    raise t("messages.action_not_allowed") if not enc.acceptable_by?(current_user)

    if not enc.open?
      flash[:error] = t("messages.game_not_open_any_longer")
    end

    if enc.contestants.include? con
      flash[:error] = t("messages.team_already_participating")
    end

    if not flash[:error]
      enc.add_contestant(con)
    end

    redirect_to contest_friendly_encounters_url
  end

  def play
    enc = FriendlyEncounter.find(params[:id])
    raise t("messages.action_not_allowed") if not enc.startable_by?(current_user)

    if enc.played?
      flash[:error] = t("messages.game_already_played")
    end

    if enc.running?
      flash[:error] = t("messages.game_already_running")
    end

    if not flash[:error] and not enc.ready?
      flash[:error] = t("messages.game_not_ready_yet")
    end

    if not flash[:error] and not enc.playable?
      flash[:error] = t("messages.no_more_friendly_matches_today")
    end

    if not flash[:error]
      enc.play! 
    end

    redirect_to contest_friendly_encounters_url
  end
end
