class Quassum::TicketAttachment < ActiveRecord::Base
  
  validates_presence_of :ticket_id
  
  has_attached_file :file, :path =>"#{RAILS_ROOT}/public/system/tickets/attachments/:id/:basename.:extension" 
  validates_attachment_presence :file
  validates_attachment_size :file, :less_than => 10.megabytes

  after_save :save_ticket

  def ticket
    Quassum::Ticket.find(ticket_id)
  end

  def save_ticket
    ticket.save
  end

end
