class ChecksumCheck < FakeCheck
  def perform
    fragments << CheckResultFragment.new(:name => "Identität", :value => 
    fake_test.clients.first.get_hash == fake_test.clients.second.get_hash ? "true" : "false", :description  => "Sind die Checksummen identisch?")      
  end
end
