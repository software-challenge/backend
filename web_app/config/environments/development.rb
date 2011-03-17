# Settings specified here will take precedence over those in config/environment.rb

# In the development environment your application's code is reloaded on
# every request.  This slows down response time but is perfect for development
# since you don't have to restart the webserver when you make code changes.
config.cache_classes = false

# Log error messages when you accidentally call methods on nil.
config.whiny_nils = true

# Show full error reports and disable caching
config.action_controller.consider_all_requests_local = true
config.action_view.debug_rjs                         = true
config.action_controller.perform_caching             = false

# Don't care if the mailer can't send
ActionMailer::Base.delivery_method = :test
ActionMailer::Base.raise_delivery_errors = false

ENV['CLIENT_LOGS_FOLDER'] = RAILS_ROOT + "/clientlogs/"
ENV['SERVER_LOGS_FOLDER'] = RAILS_ROOT + "/serverlogs/"
ENV['FRIENDLY_GAMES_PER_DAY'] = "4"
ENV['MR_SMITH'] = "mr.smith@sc.de"
ENV['EMAIL_VALIDATION'] = "false"
ENV['SHOW_GRAVATAR'] = "false"


