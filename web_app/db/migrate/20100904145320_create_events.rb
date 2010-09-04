class CreateEvents < ActiveRecord::Migration
  def self.up
    create_table :events do |t|
      t.integer :contest_id
      t.integer :param_int_1
      t.integer :param_int_2
      t.integer :param_int_3
      t.string  :param_string_1
      t.string  :param_string_2
      t.boolean :param_bool_1
      t.boolean :param_bool_2
      t.datetime :param_time_1
      t.string :type, :default => "Event"
      t.timestamps
    end
  end

  def self.down
    drop_table :events
  end
end
