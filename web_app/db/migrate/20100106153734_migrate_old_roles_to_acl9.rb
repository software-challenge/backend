class MigrateOldRolesToAcl9 < ActiveRecord::Migration
  def self.up
    Membership.all.each do |m|
      role = m[:role].to_sym
      m.person.has_role! role, m.contestant
    end

    Person.all(:conditions => { :administrator => true }).each do |p|
      p.has_role! :administrator
    end

    remove_column :memberships, :role
    remove_column :people, :administrator
  end

  def self.down
    raise ActiveRecord::MigrationIrreversible
  end
end
