module PeopleHelper
  def manageable_roles
    if current_user.administrator?
      %w{pupil tutor teacher}.collect do |role|
        [Role.translate(role, :for => Contestant), role]
      end
    else
      %w{pupil}.collect do |role|
        [Role.translate(role, :for => Contestant), role]
      end
    end
  end
end
