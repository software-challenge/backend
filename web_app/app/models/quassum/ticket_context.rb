class Quassum::TicketContext < ActiveRecord::Base

  belongs_to :context, :polymorphic => :true
  validates_presence_of :context, :ticket_id

  def ticket
    Quassum::Ticket.find(ticket_id)
  end

  def ticket=(ticket)
    ticket_id = ticket.id
  end

  def ticket?
    !!ticket_id
  end 

end
