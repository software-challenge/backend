class WhitelistEntry < ActiveRecord::Base
  validates_presence_of :filename
  validates_presence_of :checksum

  belongs_to :whitelist, :dependent => :destroy
  has_one :contest, :through => :whitelist 
end
