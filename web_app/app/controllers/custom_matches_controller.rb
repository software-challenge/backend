class CustomMatchesController < ApplicationController

  access_control do
    allow :administrator
  end

  def create
    con1 = Contestant.find(params[:match][:contestant1])
    con2 = Contestant.find(params[:match][:contestant2])
    unless con1 == con2
      mm = @contest.custom_matches.create
      mm.clients = [con1.current_client, con2.current_client] 
    else  
      flash[:error] = "Eine Schule kann nicht gegen sich selbst antreten"
    end

    redirect_to hash_for_contest_custom_matches_url
  end

  def play
    mm = CustomMatch.find(params[:id])
    mm.perform_delayed!
    redirect_to hash_for_contest_custom_matches_url
  end

  def show
    mm = CustomMatch.find(params[:id])
    redirect_to hash_for_contest_custom_matches_url
  end

  def destroy
    mm = CustomMatch.find(params[:id])
    mm.destroy
    redirect_to contest_custom_matches_url
  end
end
