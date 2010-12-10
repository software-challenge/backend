class FakeTestsController < ApplicationController
  
  access_control do
    allow :administrator
  end

  def create
   @fake_test = FakeTest.new
   @fake_test.clients << Client.find_by_id(params[:ft][:client_id])
   @fake_test.clients << Client.find_by_id(params[:ft][:opponent_id])
   if params[:ft][:checksum_check] == "1"
     @fake_test.checks << ChecksumCheck.new
   end
   if params[:ft][:diff_check] == "1"
     @fake_test.checks << DiffCheck.new
   end
   @fake_test.name = params[:ft][:name]
   @fake_test.description = params[:ft][:description]
   if @fake_test.save
     @fake_test.perform_delayed!
     flash[:notice] = 'Plagiat-Test wurde erfolgreich erstellt'
   else 
     flash[:error] = 'Plagiat-Test konnte nicht erstellt werden, es trat ein Fehler auf!'
     render :action => 'new'
   end
   redirect_to params[:return_to]
   end

   def index
   end

   def new
     @fake_test = FakeTest.new
   end

   def restart
     @fake_test = FakeTest.find_by_id(params[:id])
     @fake_test.restart!
     redirect_to :action => "index"
   end

   def show
     @fake_test = FakeTest.find_by_id(params[:id])
   end

   def destroy
     @fake_test = FakeTest.find_by_id(params[:id])
     @fake_test.destroy
     redirect_to :action => "index"
   end
end
