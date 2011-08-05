class AddingGameContextToMatch < ActiveRecord::Migration
  def self.up
    add_column :matches, :context_id, :integer
    add_column :matches, :context_type, :string
    Contest.transaction do 
      Match.all.select{|m| m.set_type == "Matchday"}.each{|e| e.context = e.set.contest; e.save}
      Match.all.select{|m| m.set_type == "Client"}.each{|e| e.context = e.set.contestant.contests.first; e.save}
      Match.all.select{|m| m.set_type == "FriendlyEncounter"}.each{|e| puts e.set.class.to_s; e.context = e.set.context; e.save}
      Match.all.select{|m| m.set_type == "Contest"}.each{|e| e.context = e.set; e.save}
    end
  end

  def self.down
    remove_column :matches, :context_id
    remove_column :matches, :context_type
  end
end
