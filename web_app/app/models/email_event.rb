class EmailEvent < ActiveRecord::Base

  belongs_to :person

  named_scope :rcvs_on_matchday_played, :conditions => {:rcv_on_matchday_played => true}
  named_scope :rcvs_client_matchday_warning, :conditions => {:rcv_client_matchday_warning => true}

  attr_reader :email

  def email
    person ? person.email : ""
  end

end

