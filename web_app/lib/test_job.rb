class TestJob < Struct.new(:match_id, :activateClient)
  def perform
    match = ClientMatch.find(match_id)
    match.perform(activateClient)
  end
end
