require 'zip/zip'

class Client < ActiveRecord::Base

  POTENTIAL_FILE_EXTENSIONS = %w{.jar .exe .py .rb}

  belongs_to :contestant
  belongs_to :author, :class_name => "Person"
  
  has_many :file_entries, :class_name => "ClientFileEntry", :dependent => :destroy
  has_one :test_match, :class_name => "ClientMatch", :as => :set, :dependent => :destroy

  has_attached_file :file

  validates_presence_of :file
  validates_attachment_presence :file

  validates_presence_of :author
  validates_presence_of :contestant

  belongs_to :main_file_entry, :class_name => "ClientFileEntry"

  delegate :contest, :to => :contestant
  delegate :game_definition, :to => :contest

  def test_results
    result = test_match.slot_for(self).cause_distribution
    regular_results = result["REGULAR"].to_i
    all_results = result.inject(0){ |sum,x| sum + x[1].to_i }
    [regular_results, all_results]
  end

  def build_index!    
    Client.transaction do
      file_entries.destroy_all

      Zip::ZipFile.foreach(file.path) do |e|
        file_name = e.name.to_s

        file_entries.create!(:file_type => e.ftype.to_s,
          :file_name => file_name,
          :file_size => e.size,
          :level => calculate_level(file_name))
      end
    end

    guess_main_file!
  end

  def current?
    contestant.current_client == self
  end

  def java?
    # FIXME: implement selection
    true
  end

  def status
    if test_match and test_match.played?
      all_tests_passed? ? "ok" : "broken"
    elsif test_match and test_match.running?
      "testing"
    elsif main_file_entry
      "testable"
    else
      "uploaded"
    end
  end

  %w{ok broken testing testable}.each do |k|
    define_method "#{k}?" do
      status == k
    end
  end

  def tested?
    ok? or broken?
  end

  def all_tests_passed?
    if test_match and test_match.played?
      test_results[0] == test_results[1]
    end
  end

  def already_used?
    # FIXME: check for running games
    false
  end

  def test_delayed!
    raise "client was already tested" if tested?
    raise "client is currently tested" if testing?
    raise "no test_contestant available" unless contest.test_contestant
    
    Match.transaction do
      test_match.destroy if test_match
      self.test_match = nil
      match = self.create_test_match
      match.clients = [self, contest.test_contestant.current_client]
      match.perform_delayed!
    end
  end

  def after_save
    if main_file_entry_id_changed?
      test_match.destroy if test_match
    end
  end

  protected

  def guess_main_file!
    regex = POTENTIAL_FILE_EXTENSIONS.collect do |ext|
      Regexp.escape ext
    end.join("|").gsub("\\", "\\\\\\")

    potential_matches = file_entries.all(
      :conditions => ["client_file_entries.file_name REGEXP '((#{regex})$)' AND level <= 2"],
      :order => "level ASC, file_name ASC",
      :limit => 10)

    return if potential_matches.empty?

    result = potential_matches.first

    catch :found do
      potential_matches.each do |match|
        %w{simpleclient client myclient main start run}.each do |name|
          if /\/(#{name})\.[a-zA-Z]+\Z/ =~ match.file_name
            result = match
            throw :found
          end
        end
      end
    end

    self.main_file_entry = result
    save!
  end

  def calculate_level(filename)
    n = 0
    filename.scan(/\/\b/) { n += 1 }
    n
  end
end
