class Role < ActiveRecord::Base
  acts_as_authorization_role

  def to_s
    if authorizable.is_a? Contestant
      case name.to_s
      when "teacher"
        "LehrerIn"
      when "pupil"
        "SchülerIn"
      when "Tutor"
        "TutorIn"
      else
        "Unbekannte Rolle"
      end
    else
      super
    end
  end
end