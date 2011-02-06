class FakeTestsController < ApplicationController
  access_control do
    allow :administrator
  end

  before_filter :fetch_fake_test
  before_filter :fetch_fake_tests
  
  def create
    @test_suite = FakeTestSuite.new
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
       @test_suite << @fake_test
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
   
   def refresh_index 
   end

   def refresh_show
   end

   def index
     session[:ft_states] = @fake_tests.collect{|ft| ft.state} 
   end

   def new
     fetch_available_checks
     @fake_test = FakeTest.new
   end

   def restart
     @fake_test.restart!
     @available_checks = FakeTest.available_checks_for_contest(@contest)
     redirect_to :action => "index"
     # Maybe use this ajax stuff to get this a bit more dynamic!
     #respond_to do |format|
     #  format.html { redirect_to :action => "index" }
     #  format.js { render :action => "index" } 
     #end
   end

   def show
   end

   def destroy
     @fake_test_id = @fake_test.id 
     if @fake_test.destroy 
       flash[:notice] = "Plagiat-Test erfolgreich entfernt."
     else 
       flash[:error] = "Plagit-Test konnte nicht entfernt werdne!"
     end
     respond_to do |format|
       format.html { redirect_to :action => "index" }
       format.js
     end
   end

   def fetch_fake_test
     @fake_test = FakeTest.find_by_id(params[:id])
   end

   def fetch_fake_tests
     @fake_tests = @fake_test_suite.fake_tests
   end

   def fetch_available_checks
     @available_checks = FakeTest.available_checks_for_contest(@contest)
   end

end
