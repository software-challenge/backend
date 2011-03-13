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

ActiveRecord::Schema.define(:version => 20110311174155) do

  create_table "answers", :force => true do |t|
    t.integer  "question_id"
    t.text     "text"
    t.text     "short_text"
    t.text     "help_text"
    t.integer  "weight"
    t.string   "response_class"
    t.string   "reference_identifier"
    t.string   "data_export_identifier"
    t.string   "common_namespace"
    t.string   "common_identifier"
    t.integer  "display_order"
    t.boolean  "is_exclusive"
    t.boolean  "hide_label"
    t.integer  "display_length"
    t.string   "custom_class"
    t.string   "custom_renderer"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "default_value"
  end

  create_table "check_result_fragments", :force => true do |t|
    t.integer "fake_check_id"
    t.string  "name"
    t.string  "value"
    t.string  "description"
  end

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
    t.boolean  "hidden",             :default => false, :null => false
    t.string   "parameters"
  end

  create_table "clients_fake_tests", :id => false, :force => true do |t|
    t.integer  "fake_test_id"
    t.integer  "client_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "comments", :force => true do |t|
    t.text     "text"
    t.integer  "client_id"
    t.integer  "person_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "contestants", :force => true do |t|
    t.string   "name"
    t.integer  "contest_id"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "score_id"
    t.integer  "current_client_id"
    t.boolean  "tester",                       :default => false,  :null => false
    t.boolean  "hidden",                       :default => false,  :null => false
    t.string   "location",                     :default => "",     :null => false
    t.integer  "overall_member_count",         :default => 0,      :null => false
    t.boolean  "disqualified",                 :default => false
    t.string   "ranking",                      :default => "none", :null => false
    t.integer  "school_id"
    t.boolean  "participate_at_trial_contest", :default => false,  :null => false
  end

  create_table "contests", :force => true do |t|
    t.string   "name"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "game_definition"
    t.string   "subdomain"
    t.boolean  "play_automatically",       :default => false,            :null => false
    t.integer  "trial_contest_id"
    t.string   "phase",                    :default => "initialization"
    t.boolean  "allow_trial_registration", :default => false,            :null => false
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

  create_table "dependencies", :force => true do |t|
    t.integer  "question_id"
    t.integer  "question_group_id"
    t.string   "rule"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "dependency_conditions", :force => true do |t|
    t.integer  "dependency_id"
    t.string   "rule_key"
    t.integer  "question_id"
    t.string   "operator"
    t.integer  "answer_id"
    t.datetime "datetime_value"
    t.integer  "integer_value"
    t.float    "float_value"
    t.string   "unit"
    t.text     "text_value"
    t.string   "string_value"
    t.string   "response_other"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "email_events", :force => true do |t|
    t.string   "person_id"
    t.boolean  "rcv_on_matchday_played",      :default => false, :null => false
    t.boolean  "rcv_client_matchday_warning", :default => true,  :null => false
    t.datetime "created_at"
    t.datetime "updated_at"
    t.boolean  "rcv_contest_progress_info",   :default => true,  :null => false
  end

  create_table "events", :force => true do |t|
    t.integer  "contest_id"
    t.integer  "param_int_1"
    t.integer  "param_int_2"
    t.integer  "param_int_3"
    t.string   "param_string_1"
    t.string   "param_string_2"
    t.boolean  "param_bool_1"
    t.boolean  "param_bool_2"
    t.datetime "param_time_1"
    t.string   "type",           :default => "Event"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "fake_checks", :force => true do |t|
    t.integer  "fake_test_id"
    t.string   "type",         :default => "FakeCheck"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.datetime "finished_at"
  end

  create_table "fake_test_suites", :force => true do |t|
    t.string   "name"
    t.text     "description"
    t.integer  "contest_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "fake_tests", :force => true do |t|
    t.integer  "job_id"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "fake_test_suite_id"
    t.datetime "started_at"
  end

  create_table "finales", :force => true do |t|
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "contest_id"
    t.integer  "job_id"
    t.boolean  "published",  :default => false
  end

  create_table "friendly_encounter_slots", :force => true do |t|
    t.integer  "friendly_encounter_id"
    t.integer  "client_id"
    t.integer  "contestant_id"
    t.integer  "score_id"
    t.boolean  "hidden"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "friendly_encounters", :force => true do |t|
    t.integer  "contest_id"
    t.datetime "played_at"
    t.integer  "job_id"
    t.string   "type",        :default => "FriendlyEncounter"
    t.integer  "open_for_id"
    t.boolean  "rejected",    :default => false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "login_tokens", :force => true do |t|
    t.integer  "person_id"
    t.string   "code"
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
    t.boolean  "public",     :default => false,      :null => false
    t.string   "type",       :default => "Matchday"
    t.string   "name",       :default => ""
    t.integer  "finale_id"
    t.boolean  "trial",      :default => false,      :null => false
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

  create_table "news_posts", :force => true do |t|
    t.string   "title"
    t.text     "text"
    t.text     "html"
    t.datetime "published_at"
    t.integer  "person_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "people", :force => true do |t|
    t.string   "email"
    t.string   "password_hash"
    t.string   "password_salt"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "first_name",           :default => "",    :null => false
    t.string   "last_name",            :default => "",    :null => false
    t.boolean  "show_email_to_others", :default => false, :null => false
    t.boolean  "hidden",               :default => false, :null => false
    t.datetime "last_seen"
    t.boolean  "logged_in",            :default => false, :null => false
    t.integer  "phone_number"
    t.string   "validation_code"
  end

  create_table "people_roles", :id => false, :force => true do |t|
    t.integer  "person_id"
    t.integer  "role_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "preliminary_contestants", :force => true do |t|
    t.integer  "school_id"
    t.string   "name"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "participation_probability", :null => false
    t.integer  "person_id"
  end

  create_table "question_groups", :force => true do |t|
    t.text     "text"
    t.text     "help_text"
    t.string   "reference_identifier"
    t.string   "data_export_identifier"
    t.string   "common_namespace"
    t.string   "common_identifier"
    t.string   "display_type"
    t.string   "custom_class"
    t.string   "custom_renderer"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "questions", :force => true do |t|
    t.integer  "survey_section_id"
    t.integer  "question_group_id"
    t.text     "text"
    t.text     "short_text"
    t.text     "help_text"
    t.string   "pick"
    t.string   "reference_identifier"
    t.string   "data_export_identifier"
    t.string   "common_namespace"
    t.string   "common_identifier"
    t.integer  "display_order"
    t.string   "display_type"
    t.boolean  "is_mandatory"
    t.integer  "display_width"
    t.string   "custom_class"
    t.string   "custom_renderer"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "correct_answer_id"
  end

  create_table "response_sets", :force => true do |t|
    t.integer  "user_id"
    t.integer  "survey_id"
    t.string   "access_code"
    t.datetime "started_at"
    t.datetime "completed_at"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "response_sets", ["access_code"], :name => "response_sets_ac_idx", :unique => true

  create_table "responses", :force => true do |t|
    t.integer  "response_set_id"
    t.integer  "question_id"
    t.integer  "answer_id"
    t.datetime "datetime_value"
    t.integer  "integer_value"
    t.float    "float_value"
    t.string   "unit"
    t.text     "text_value"
    t.string   "string_value"
    t.string   "response_other"
    t.string   "response_group"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "survey_section_id"
  end

  add_index "responses", ["survey_section_id"], :name => "index_responses_on_survey_section_id"

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
    t.boolean  "qualification_changed", :default => false, :null => false
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

  create_table "scheduled_jobs", :force => true do |t|
    t.string   "name",                          :null => false
    t.boolean  "running",    :default => false
    t.datetime "last_check"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "schools", :force => true do |t|
    t.string   "name",             :null => false
    t.integer  "zipcode",          :null => false
    t.string   "location",         :null => false
    t.string   "state",            :null => false
    t.integer  "person_id"
    t.integer  "contest_id"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "contact_function", :null => false
  end

  create_table "score_fragments", :force => true do |t|
    t.integer "score_id"
    t.decimal "value",          :precision => 63, :scale => 10
    t.string  "fragment"
    t.decimal "adjusted_value", :precision => 63, :scale => 10
  end

  create_table "scores", :force => true do |t|
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "game_definition"
    t.string   "score_type"
    t.string   "cause"
    t.string   "error_message",   :default => ""
    t.string   "adjusted_cause"
  end

  create_table "survey_sections", :force => true do |t|
    t.integer  "survey_id"
    t.string   "title"
    t.text     "description"
    t.string   "reference_identifier"
    t.string   "data_export_identifier"
    t.string   "common_namespace"
    t.string   "common_identifier"
    t.integer  "display_order"
    t.string   "custom_class"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "survey_tokens", :force => true do |t|
    t.integer  "survey_id"
    t.integer  "token_owner_id"
    t.string   "token_owner_type"
    t.integer  "response_set_id"
    t.datetime "valid_from"
    t.datetime "valid_until"
    t.boolean  "allow_teacher",    :default => true
    t.boolean  "allow_tutor",      :default => false
    t.boolean  "allow_pupil",      :default => false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "surveys", :force => true do |t|
    t.string   "title"
    t.text     "description"
    t.string   "access_code"
    t.string   "reference_identifier"
    t.string   "data_export_identifier"
    t.string   "common_namespace"
    t.string   "common_identifier"
    t.datetime "active_at"
    t.datetime "inactive_at"
    t.string   "css_url"
    t.string   "custom_class"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "display_order"
  end

  add_index "surveys", ["access_code"], :name => "surveys_ac_idx", :unique => true

  create_table "validation_conditions", :force => true do |t|
    t.integer  "validation_id"
    t.string   "rule_key"
    t.string   "operator"
    t.integer  "question_id"
    t.integer  "answer_id"
    t.datetime "datetime_value"
    t.integer  "integer_value"
    t.float    "float_value"
    t.string   "unit"
    t.text     "text_value"
    t.string   "string_value"
    t.string   "response_other"
    t.string   "regexp"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "validations", :force => true do |t|
    t.integer  "answer_id"
    t.string   "rule"
    t.string   "message"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "whitelist_entries", :force => true do |t|
    t.integer  "whitelist_id"
    t.string   "filename"
    t.string   "checksum"
    t.string   "comment"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "whitelists", :force => true do |t|
    t.integer "contest_id"
    t.text    "description"
  end

end
