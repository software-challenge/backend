if RAILS_ENV == "production"
  HoptoadNotifier.configure do |config|
    config.api_key = '82d7a11e9e31e66c43fd75477f62299a'
  end
end