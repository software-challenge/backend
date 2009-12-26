module PeopleHelper
  def role_for(person, contestant)
    person.membership_for(contestant).role
  end

  def translate_role(role)
    case role
    when "teacher"
      "Lehrer"
    when "tutor"
      "Tutor"
    when "pupil"
      "Schüler"
    else
      ""
    end
  end

  def manageable_roles
    if current_user.administrator?
      [["Schüler", "pupil"], ["Lehrer", "teacher"], ["Tutor", "tutor"]]
    else
      [["Schüler", "pupil"]]
    end
  end
end
