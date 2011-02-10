class FakeTestSuite < ActiveRecord::Base
  belongs_to :contest 
  has_many :fake_tests, :dependent => :destroy

  def perform!
    fake_tests.each do |ft|
      ft.perform_delayed!
    end
  end
 
  def handle_event!
    if done?
      contest.events << FakeTestEvent.create(:fake_test_suite => self, :contest => contest) 
      contest.save!
    end
  end

  def started?
    fake_tests.inject(false){|w,ft| w|= ft.started?}
  end

  def done?
    fake_tests.inject(true){|w,ft| w &= ft.done?}
  end

  def running?
    fake_tests.inject(true){|w,ft| w &= ft.running?}
  end

  def reset_results!
    fake_tests.each{|ft| ft.reset_results!}
  end

  def primary_client 
    fake_tests.first.clients.first
  end

  def state
    if done?
      return 'finished'
    elsif running?
      return 'idle'
    elsif started?
      return 'error'
    else 
      return 'ready'
    end
  end
end
