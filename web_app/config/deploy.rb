set :application, "swchallenge"
set :repository,  "http://samoa.informatik.uni-kiel.de:84/svn/teaching/ss09/swchal/common/web_app"

set :scm, :subversion
# Or: `accurev`, `bzr`, `cvs`, `darcs`, `git`, `mercurial`, `perforce`, `subversion` or `none`

role :web, "134.245.253.5"                          # Your HTTP server, Apache/etc
role :app, "134.245.253.5"                          # This may be the same as your `Web` server
role :db,  "134.245.253.5", :primary => true # This is where Rails migrations will run
# role :db,  "your slave db-server here"

# If you are using Passenger mod_rails uncomment this:
# if you're still using the script/reapear helper you will need
# these http://github.com/rails/irs_process_scripts

namespace :deploy do
  task :start {}
  task :stop {}
  task :restart, :roles => :app, :except => { :no_release => true } do
    run "#{try_sudo} touch #{File.join(current_path,'tmp','restart.txt')}"
  end
end
