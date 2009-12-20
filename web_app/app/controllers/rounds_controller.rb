class RoundsController < ApplicationController
  def show
    @round = Round.find(params[:id])
    send_file @round.replay.path, :disposition => 'inline'
  end
end
