class MergeTutorAndTeacherAttribute < ActiveRecord::Migration
  def self.up
    add_column :memberships, :role, :string
    execute "UPDATE memberships SET role = 'tutor' WHERE tutor = '1'"
    execute "UPDATE memberships SET role = 'teacher' WHERE teacher = '1'"
    execute "UPDATE memberships SET role = 'pupil' WHERE role IS NULL"
    remove_column :memberships, :teacher
    remove_column :memberships, :tutor
  end

  def self.down
    raise ActiveRecord::MigrationIrreversible
  end
end
