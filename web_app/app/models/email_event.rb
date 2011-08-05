class EmailEvent < ActiveRecord::Base

  belongs_to :person

  named_scope :rcvs_on_matchday_played, :conditions => {:rcv_on_matchday_played => true}
  named_scope :rcvs_client_matchday_warning, :conditions => {:rcv_client_matchday_warning => true}
  named_scope :rcvs_contest_progress_info, :conditions => {:rcv_contest_progress_info => true}
  named_scope :rcvs_on_matchday_published, :conditions => {:rcv_on_matchday_published => true}
  named_scope :rcvs_survey_token_notification, :conditions => {:rcv_survey_token_notification => true}
  named_scope :rcvs_quassum_notification, :condition => {:rcv_quassum_notification => true}

  attr_reader :email

  def email
    person ? person.email : ""
  end

end

