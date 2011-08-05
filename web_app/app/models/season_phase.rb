class SeasonPhase < ActiveRecord::Base
  belongs_to :season
  acts_as_list :scope => :season_id

  has_many :friendly_encounters, :through => :contests
  has_many :contests, :dependent => :destroy
  
  def build_contests
    configuration.contests.each do |contest_configuration|
      contests << contest_configuration.build_contest(self)
    end
    save
  end

  def load_contestants
    configuration.contests.each do |contest_configuration|
      contest = Contest.find_by_subdomain contest_configuration.full_subdomain
      unless higher_item
        contest.contestants += contest_configuration.choose_contestants(season.contestants)
      else
        contest.contestants += contest_configuration.choose_contestants
      end
      contest.save
    end
  end

  def finished?
    contests.all?{|c| c.regular_phase_finished?}
  end

  def current_phase?
    season.current_phase == self
  end

  def configuration
    season.definition.find_phase identifier
  end
  
  # TODO: include to game definition!
  def name
    identifier
  end

  def status
    if finished? or (season.current_phase and season.current_phase.position > position)
      "past" 
    elsif current_phase? 
      "current"
    else
      "future" 
    end
  end
end

