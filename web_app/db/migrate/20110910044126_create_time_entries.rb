class CreateTimeEntries < ActiveRecord::Migration
  def self.up
    ContestantReportEvent.destroy_all
    create_table :time_entries do |t|
      t.integer :minutes, :default => 0, :null => false
      t.string :title
      t.text :description
      t.integer :person_id
      t.string :context_type
      t.integer :context_id
      t.timestamps
    end
    Contestant.all.select{|c| c.report}.each do |c|
      TimeEntry.create(:title => "Protokoll bis #{Time.now.strftime("%d.%m%Y")}", :description => c.report, :context => c, :person => c.people.tutors_and_helpers.first)
    end
    remove_column :contestants, :report
  end

  def self.down
    drop_table :time_entries
    add_column :contestants, :report, :text
  end
end
