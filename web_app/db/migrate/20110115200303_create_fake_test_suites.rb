class CreateFakeTestSuites < ActiveRecord::Migration
  def self.up
    create_table :fake_test_suites do |t|
      t.string :name
      t.text :description
      t.integer :contest_id
      t.timestamps
    end
    remove_column :fake_tests, :name
    remove_column :fake_tests, :description
    remove_column :fake_tests, :contest_id
    add_column :fake_tests, :fake_test_suite_id, :integer
  end

  def self.down
    drop_table :fake_tests_suites
    add_column :fake_tests, :name, :string
    add_column :fake_tests, :description, :text
    add_column :fake_tests, :contest_id, :integer
    remove_column :fake_tests, :fake_test_suite_id
  end
end
