ActionMailer::Base.delivery_method = :smtp
ActionMailer::Base.raise_delivery_errors = true
ActionMailer::Base.smtp_settings = {
  :address  => "smtp.gfxpro.eu",
  :port  => 587,
  :user_name  => "software-challenge@gfxpro.eu",
  :password  => "4rtjxw40x",
  :authentication  => :plain
}
ActionMailer::Base.default_url_options[:host] = "www.software-challenge.de"
