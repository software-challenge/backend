class FakeTestEvent < Event
  validates_presence_of :param_int_1

  def fake_test_suite=(fake_test_suite)
   self.param_int_1 = fake_test_suite.id
  end

  def fake_test_suite
    FakeTestSuite.find_by_id(param_int_1)
  end
  
  def valid_event?
    !!fake_test_suite
  end

  def path
    [:contest, fake_test_suite]
  end

  def text
    "Der Test der Plagiat-Test-Aufstellung #{fake_test_suite.name.blank? ? "Nr. #{fake_test_suite.id}" : fake_test_suite.name} wurde fertiggestellt."  
  end
end
