class FakeTest < ActiveRecord::Migration
  def self.up
    create_table :fake_tests do |t|
      t.integer :job_id
      t.string :name
      t.text :description
      t.timestamps
    end
    create_table :clients_fake_tests, :id => false do |t|
      t.references :fake_test
      t.references :client
      t.timestamps
    end
    create_table :fake_checks do |t|
      t.integer :fake_test_id
      t.string :type, :default => "FakeCheck"
      t.timestamps
    end
    create_table :check_result_fragments do |t|
      t.integer :fake_check_id
      t.string :name
      t.string :value
      t.string :description
    end 
  end

  def self.down
    drop_table :fake_tests
    drop_table :clients_fake_tests
    drop_table :check_result_fragments
    drop_table :fake_checks
  end
end
