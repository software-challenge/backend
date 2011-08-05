QUASSUM = {}
QUASSUM[:cache] = Rails.cache
QUASSUM[:domain] = "sunlight.informatik.uni-kiel.de"
QUASSUM[:project_slug] = "ein-projekt"

# default user for getting infos from the quassum:
QUASSUM[:user] = {:username => "sven@koschnicke.de", :token => "db1e123fa15bc06bfb85bcf70e99cfd7", :password => "cd960b526"}

# setup settings for faye server
QUASSUM[:faye] = {:url => "http://localhost:9292/faye"}



