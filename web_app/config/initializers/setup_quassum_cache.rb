QUASSUM = {}
QUASSUM[:cache] = Rails.cache


#if RAILS_ENV == "development"
  QUASSUM[:domain] = "sunlight.informatik.uni-kiel.de"
  QUASSUM[:port] = 3000
  QUASSUM[:project_slug] = "ein-projekt"
#  # default user for getting infos from the quassum:
  QUASSUM[:user] = {:username => "sven@koschnicke.de", :token => "db1e123fa15bc06bfb85bcf70e99cfd7", :password => "cd960b526"}
#elsif RAILS_ENV == "production"
#  QUASSUM[:domain] = "cau.quassum.com"
#  QUASSUM[:port] = 80
#  QUASSUM[:project_slug] = "webapp"
#  QUASSUM[:user] = {:username => "software-challenge@gfxpro.eu", :token => "2589082f287306bfed43e0f047", :password => "246df7f9a" }
#end

# setup settings for faye server
QUASSUM[:faye] = {:url => "http://localhost:9292/faye"}



