require 'sandbox'

class MatchdaysController < ApplicationController
  before_filter :fetch_contest

  def index
    @matchdays = @contest.matchdays

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @matchdays }
    end
  end

  def show
    @matchday = @contest.matchdays.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @matchday }
    end
  end

  def update
    @matchday = Matchday.find(params[:id])

    respond_to do |format|
      if @matchday.update_attributes(params[:matchday])
        flash[:notice] = 'Matchday was successfully updated.'
        format.html { redirect_to(@matchday) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @matchday.errors, :status => :unprocessable_entity }
      end
    end
  end

  def destroy
    @matchday = @contest.matchdays.find(params[:id])
    @matchday.destroy

    respond_to do |format|
      format.html { redirect_to(matchdays_url) }
      format.xml  { head :ok }
    end
  end

  def play
    @matchday = @contest.matchdays.find(params[:id])

    Matchday.transaction do
      # TODO: start a deferred job
      @matchday.played = true
      @matchday.save!
      update_scoretable
    end

    redirect_to contest_matchday_url(@contest, @matchday)
  end

  protected

  def get_file_as_string(filename)
    data = ''
    f = File.open(filename, "r")
    f.each_line do |line|
      data += line
    end
    return data
  end

  def update_scoretable
    sandbox = Sandbox.new("sum_all(elements)")
    mod = Module.new do
      define_method :assert_size do |rows|
        if rows.empty?
          true
        else
          default_size = rows.first.size
          rows.each do |row|
            raise "row sizes didn't match" unless row.size == default_size
          end
        end
      end

      define_method :sum_all do |rows|
        if rows.empty?
          []
        else
          assert_size rows
          width = rows.first.size
          result = []
          width.times do |i|
            result << rows.inject(0) { |sum,x| sum + x[i] }
          end
          result
        end
      end

    end
    sandbox.extend mod

    begin
      result = sandbox.invoke(:locals => {:elements => [[1,0,0],[2,3,0],[3,0,0],[4,2,0]]})
      flash[:notice] = "Result: #{result.to_sentence}"
    rescue => e
      flash[:error] = e.message
    end
  end

  def fetch_contest
    @contest = Contest.find(params[:contest_id])
  end
end
