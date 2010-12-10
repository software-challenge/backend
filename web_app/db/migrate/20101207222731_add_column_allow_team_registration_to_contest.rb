class AddColumnAllowTeamRegistrationToContest < ActiveRecord::Migration
  def self.up
    add_column :contests, :allow_team_reg, :boolean
  end

  def self.down
    remove_column :contests, :allow_team_reg
  end
end
