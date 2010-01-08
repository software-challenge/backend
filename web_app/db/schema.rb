# This file is auto-generated from the current state of the database. Instead of editing this file, 
# please use the migrations feature of Active Record to incrementally modify your database, and
# then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your database schema. If you need
# to create the application database on another system, you should be using db:schema:load, not running
# all the migrations from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20100107150533) do

  create_table "client_file_entries", :force => true do |t|
    t.integer "client_id"
    t.string  "file_name"
    t.string  "file_type"
    t.integer "file_size"
    t.integer "level"
  end

  create_table "clients", :force => true do |t|
    t.string   "name"
    t.integer  "contestant_id"
    t.integer  "author_id"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "file_file_name"
    t.string   "file_content_type"
    t.integer  "file_file_size"
    t.integer  "main_file_entry_id"
  end

  create_table "contestants", :force => true do |t|
    t.string   "name"
    t.integer  "contest_id"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "score_id"
    t.integer  "current_client_id"
    t.boolean  "tester",            :default => false, :null => false
  end

  create_table "contests", :force => true do |t|
    t.string   "name"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "game_definition"
    t.string   "subdomain"
  end

  create_table "delayed_jobs", :force => true do |t|
    t.integer  "priority",   :default => 0
    t.integer  "attempts",   :default => 0
    t.text     "handler"
    t.string   "last_error"
    t.datetime "run_at"
    t.datetime "locked_at"
    t.datetime "failed_at"
    t.string   "locked_by"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "match_slots", :force => true do |t|
    t.integer  "match_id"
    t.integer  "position"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "score_id"
    t.integer  "matchday_slot_id"
    t.string   "type"
    t.integer  "client_id"
  end

  create_table "matchday_slots", :force => true do |t|
    t.integer  "client_id"
    t.integer  "matchday_id"
    t.integer  "contestant_id"
    t.integer  "score_id"
    t.integer  "position"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "matchdays", :force => true do |t|
    t.integer  "contest_id"
    t.date     "when"
    t.integer  "position"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.datetime "played_at"
    t.integer  "job_id"
  end

  create_table "matches", :force => true do |t|
    t.integer  "set_id"
    t.string   "set_type"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.datetime "played_at"
    t.integer  "job_id"
    t.string   "type"
  end

  create_table "memberships", :force => true do |t|
    t.integer  "person_id"
    t.integer  "contestant_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "people", :force => true do |t|
    t.string   "email"
    t.string   "password_hash"
    t.string   "password_salt"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.boolean  "blocked",              :default => false, :null => false
    t.string   "first_name",           :default => "",    :null => false
    t.string   "last_name",            :default => "",    :null => false
    t.string   "nick_name",            :default => "",    :null => false
    t.boolean  "show_email_to_others", :default => false, :null => false
  end

  create_table "people_roles", :id => false, :force => true do |t|
    t.integer  "person_id"
    t.integer  "role_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "roles", :force => true do |t|
    t.string   "name",              :limit => 40
    t.string   "authorizable_type", :limit => 40
    t.integer  "authorizable_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "round_slots", :force => true do |t|
    t.integer  "match_slot_id"
    t.integer  "round_id"
    t.integer  "score_id"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "position"
  end

  create_table "rounds", :force => true do |t|
    t.integer  "match_id"
    t.datetime "played_at"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "replay_file_name"
    t.string   "replay_content_type"
    t.integer  "replay_file_size"
  end

  create_table "score_fragments", :force => true do |t|
    t.integer "score_id"
    t.decimal "value",    :precision => 63, :scale => 10
    t.string  "fragment"
  end

  create_table "scores", :force => true do |t|
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "game_definition"
    t.string   "score_type"
    t.string   "cause"
  end

end
