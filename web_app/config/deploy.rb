set :application, "swchallenge"

set :user, "scadmin"
set :runner, "scadmin"
set :admin_runner, "scadmin"

set(:scm_user) do
  Capistrano::CLI.ui.ask "SCM User:"
end

set(:scm_password) do
  Capistrano::CLI.password_prompt "SCM Password (#{scm_user}): "
end

set(:repository) do
	"--username #{scm_user} --password #{scm_password} --no-auth-cache http://samoa.informatik.uni-kiel.de:84/svn/teaching/ss09/swchal/common/web_app"
end

set :scm, :subversion
# Or: `accurev`, `bzr`, `cvs`, `darcs`, `git`, `mercurial`, `perforce`, `subversion` or `none`

set :scm_auth_cache, true

set :deploy_to, "/home/scadmin/www/apps/#{application}"

role :web, "134.245.253.5"                          # Your HTTP server, Apache/etc
role :app, "134.245.253.5"                          # This may be the same as your `Web` server
role :db,  "134.245.253.5", :primary => true        # This is where Rails migrations will run
# role :db,  "your slave db-server here"

require 'cap_recipes/tasks/passenger'
require 'cap_recipes/tasks/rails'
require 'cap_recipes/tasks/delayed_job'

after 'deploy:restart', 'daemons:restart'

namespace :deploy do
  task :start do

  end

  task :stop do

  end
  
  task :restart, :roles => :app, :except => { :no_release => true } do
    run "#{try_sudo} touch #{File.join(current_path,'tmp','restart.txt')}"
  end
end

namespace :daemons do
  task :start do
    run "#{File.join(current_path,'script','daemons')} start"
  end

  task :restart => [ :stop, :start ] do
    # nothing here
  end
  
  task :stop do
    run "#{File.join(current_path,'script','daemons')} stop"
  end
end
