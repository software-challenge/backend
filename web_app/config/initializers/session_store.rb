# Be sure to restart your server when you modify this file.

# Your secret key for verifying cookie session data integrity.
# If you change this key, all old sessions will become invalid!
# Make sure the secret is at least 30 characters and all random, 
# no regular words or you'll be exposed to dictionary attacks.
ActionController::Base.session = {
  :key         => '_web_app_session',
  :secret      => '9107a1a41f9175bd853adaa71e6860c314f2e2f499ad9909b92abb2a97177c7dea348743f8d202f047b390cc274808a3e5953e6a0c64be8a95d16cc0fc715e00'
}

# Use the database for sessions instead of the cookie-based default,
# which shouldn't be used to store highly confidential information
# (create the session table with "rake db:sessions:create")
# ActionController::Base.session_store = :active_record_store
