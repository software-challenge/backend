class AddSubdomainToContest < ActiveRecord::Migration
  def self.up
    add_column :contests, :subdomain, :string
    Contest.reset_column_information

    Contest.transaction do
      Contest.all.each do |c|
         # no validation, set, save -> via update_attribute
        c.update_attribute(:subdomain, c.name.parameterize)
      end
    end
  end

  def self.down
    remove_column :contests, :subdomain
  end
end
