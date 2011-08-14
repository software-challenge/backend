class PersonSweeper < ActionController::Caching::Sweeper
  observe Person

  def after_save(person)
    expire_cache(person)
  end

  def expire_cache(person)
    # a dirty fix so that sweeping works from the model side
    @controller ||= ActionController::Base.new

    # use the current day in month to let the fragments expire at least everyday
    expire_fragment "_person_#{person.id}_#{Time.now.strftime("%d")}"
  end
end
