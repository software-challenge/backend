class MovingPreliminaryContestantsToSeason < ActiveRecord::Migration
  def self.up
    begin 
    unless School.column_names.include? "season_id" 
      add_column :schools, :season_id, :integer
    end
    Contest.transaction do 
        s = Season.new 
        s.definition = SeasonDefinition.first
        s.game_definition = GameDefinition.all.last
        s.save!
        s.initialize_definition
        School.all.each do |school|
          school.season_id = s.id
          school.valid?
          school.save!
        end
      end
    end
  end

  def self.down
    raise ActiveRecord::IrreversibleMigration
    remove_column :schools, :season_id
  end
end
