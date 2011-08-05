class ChecksumCheck < FakeCheck

  def perform
    gfrag!("Identität",(clients.first.get_hash == clients.second.get_hash),"Sind die Checksummen identisch?")     
    gfrag!("1. Whitelist",client_whitelisted?(clients.first),"Ist der erste Client auf der Whitelist?")
    gfrag!("2. Whitelist",client_whitelisted?(clients.second),"Ist der zweite Client auf der Whitelist?")
  end

 protected
  def client_whitelisted?(client)
    contest.whitelist and contest.whitelist.file_whitelisted?(client.file.path)
  end

end
