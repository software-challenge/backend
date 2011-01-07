class ReplaceAllowRegsWithPhaseForContests < ActiveRecord::Migration
  def self.up
    remove_column :contests, :allow_team_reg
    remove_column :contests, :allow_school_reg
    add_column :contests, :phase, :string, :default => "initialization"
    Contest.all.each do |contest|
      contest.phase = "contest"
    end
  end

  def self.down
    remove_column :contests, :phase
    add_column :contests, :allow_team_reg, :boolean, :default => false
    add_column :contests, :allow_school_reg, :boolean, :default => false
  end
end
