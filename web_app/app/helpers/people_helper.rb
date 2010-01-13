module PeopleHelper
  def manageable_roles
    if current_user.has_role? :administrator
      roles = %w{pupil tutor teacher}
    elsif current_user.has_role? :tutor
      roles = %w{pupil teacher}
    else
      roles = %w{pupil}
    end
    roles.collect { |role| [Role.translate(role, :for => Contestant), role] }
  end
end
