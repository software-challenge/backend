class FakeTestSuitesController < ApplicationController
  before_filter :fetch_fake_test_suite
  before_filter :fetch_fake_test_suites

  def fetch_fake_test_suite
    @fake_test_suite = FakeTestSuite.find_by_id(params[:id])
  end

  def fetch_fake_test_suites
    @fake_test_suites = @contest.fake_test_suites
  end

  def destroy
    if @fake_test_suite.destroy
      flash[:notice] = "Die Test-Suite wurde erfolgreich entfernt!"
      redirect_to :action => :index
    else
      flash[:error] = "Beim entfernen der Test-Suite trat ein Fehler auf!"
      redirect_to :back || contest_fake_test_suites_url(@contest)
    end
  end

  def new
    @fake_test_suite = FakeTestSuite.new
    fetch_available_checks
  end

  def show
    redirect_to contest_fake_test_suite_fake_tests_url(@contest, @fake_test_suite)
  end

  def refresh_index
    respond_to do |format|
      format.html { redirect_to :action => "index" }
      format.js  
    end
  end
 
  def create
    @test_suite = FakeTestSuite.new
    @test_suite.contest = @contest
    @test_suite.name = params[:ft][:name]
    @test_suite.description = params[:ft][:description]
    fetch_available_checks
    begin
     params[:ft][:opponent_ids].each do |opponent_id|
       @fake_test = FakeTest.new
       @fake_test.clients << Client.find_by_id(params[:ft][:client_id])
       @fake_test.clients << Client.find_by_id(opponent_id)
       @available_checks.each do |check|
          if params[:ft][:checks][check.name.underscore] == "1"
            @fake_test.checks << check.new
          else
          end
       end
       @fake_test.save!
       @test_suite.fake_tests << @fake_test
     end
    @test_suite.save!
    @test_suite.perform!
    flash[:notice] = 'Plagiat-Test wurde erfolgreich erstellt'
    redirect_to params[:return_to]
   rescue
     flash[:error] = 'Plagiat-Test konnte nicht erstellt werden, es trat ein Fehler auf!'
     render :action => 'new'
   end
  end
  
  def restart
    begin 
      @fake_test_suite.reset_results!
      @fake_test_suite.perform!
      respond_to do |format|
       format.html { redirect_to :action => 'index' }
       format.js
      end
    rescue
      flash[:error] = "Beim Durchführen oder Zurücksetzen ist ein Fehler aufgetreten!"
      redirect_to :action => :index
    end
  end

  def reset_results
    begin 
      @fake_test_suite.reset_results!
    rescue
      flash[:error] = "Beim Zurücksetzen der Ergebnisse ist ein Fehler aufgetreten!"
      redirect_to :action => :index
    end
  end

   def fetch_available_checks
     @available_checks = FakeTest.available_checks_for_contest(@contest)
   end

   def index
   end
end
