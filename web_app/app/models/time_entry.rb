class TimeEntry < ActiveRecord::Base
  belongs_to :person
  belongs_to :context, :polymorphic => true

  validates_presence_of :person
  validates_presence_of :context
  validates_presence_of :title

  def time=(input)
    if input.is_a? Integer
      self.minutes = input
    elsif input.is_a?(String) and input.include?(":")
      s = input.split(":")
      self.minutes = begin (s[0].to_i * 60)+s[1].to_i rescue 0 end
    elsif input.is_a?(String)
      self.minutes = begin input.to_i rescue 0 end
    end
  end

  def minutes_for_input
    hs = 0;
    ms= 0;
    if minutes >= 60
      hs = minutes / 60
      ms = minutes - hs*60
    else minutes > 0
      ms = minutes
    end
    hs
    "#{hs < 100 ? "0"*(2-hs.to_s.length) : ""}#{hs}:#{"0"*(2-ms.to_s.length)}#{ms}"
  end
end
