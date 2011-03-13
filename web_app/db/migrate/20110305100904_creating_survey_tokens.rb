class CreatingSurveyTokens < ActiveRecord::Migration
  def self.up
    create_table :survey_tokens do |t|
      t.integer :survey_id
      t.integer :token_owner_id
      t.string :token_owner_type
      t.integer :response_set_id
      t.timestamp :valid_from
      t.timestamp :valid_until
      t.boolean :allow_teacher, :default => true
      t.boolean :allow_tutor, :default => false
      t.boolean :allow_pupil, :default => false
      t.timestamps
    end

    add_column :preliminary_contestants, :person_id, :integer
  end

  def self.down
    drop_table :survey_tokens
    remove_column :preliminary_contestants, :person_id
  end
end
