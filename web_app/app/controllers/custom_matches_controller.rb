class CustomMatchesController < ApplicationController

  access_control do
    allow :administrator
  end
   
  def index
    @custom_match = CustomMatch.new
  end

  def create
    con1 = Contestant.find(params[:match][:contestant1])
    con2 = Contestant.find(params[:match][:contestant2])
    rounds = params[:match][:rounds].to_i
    rounds ||= @contest.game_definition.test_rounds
    if (con1 != con2 and rounds.even? and rounds > 0)
      mm = @contest.custom_matches.create(:context => @contest)
      mm.setup_clients [con1.current_client, con2.current_client], rounds
    elsif con1 == con2 
      flash[:error] = "Eine Schule kann nicht gegen sich selbst antreten"
    elsif rounds.odd?
      flash[:error] = "Nur gerade Anzahl Runden möglich"
    elsif rounds <= 0
      flash[:error] = "Spiel mit #{rounds} Runden nicht möglich"
    end

    redirect_to contest_custom_matches_url(@contest)
  end

  def play
    mm = CustomMatch.find(params[:id])
    prio = params[:priority].to_i
    mm.perform_delayed! prio
    redirect_to contest_custom_matches_url(@contest)
  end

  def show
    @match = CustomMatch.find(params[:id])
  end

  def destroy
    mm = CustomMatch.find(params[:id])
    mm.destroy
    redirect_to contest_custom_matches_url(@contest)
  end
end
