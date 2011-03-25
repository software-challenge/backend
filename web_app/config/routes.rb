ActionController::Routing::Routes.draw do |map|
  map.with_options :path_names => { :new => "neu", :edit => "bearbeiten" } do |tmap|

    tmap.resources :contests,
      :as => "wettbewerb",
      :member => {
      :refresh_matchdays => :post,
      :reset_matchdays => :post,
      :reaggregate => :post,
      :update_schedule => :post,
      :trial_contest => :get,
      :create_trial_contest => :post,
      :destroy_trial_contest => :post,
      :register_for_trial_contest => :post,
      :set_allow_trial_registration => :post,
      :phases => :get,
      :move_phase => :post
    } do |c|

      c.resources :news_posts,
       :member => {
       :publish => :post,
       :unpublish => :post
      }

      c.resources :survey_results, :member => { :show_response => :get } 

      c.resources :survey_tokens do  |st|
        st.resources :surveys, :controller => "surveyor"
      end
      c.resources :whitelist_entries, :as => "whitelist", :member => { 
        :reset => :delete,
        :ajax_delete => :post
      }
      c.resources :fake_test_suites, :as => "plagiat-test-auftrag", :member => {
        :restart => :post,
        :reset_results => :post,
        :refresh_index => :get
      }do |fts|
        fts.resources :fake_tests, :as =>"plagiat-tests", :member => {
          :restart => :post,
          :reset_results => :post,
          :refresh_show => :get,
          :refresh_index => :get
        }
     end
      c.edit_schedule '/spielplan', :controller => "contests", :action => "edit_schedule"
      c.new_team '/schools/new_team', :controller => 'schools', :action => 'new_team'
      c.resources :schools, :member => {
        :get_teams => :get
      } do |school|
        school.resources :preliminary_contestants, :as => "teams" do |prelim_contestant|
        end
      end
      c.resources :preliminary_contestants do |prelim_contestants|

      end
      c.resources :friendly_encounters, :as => "friendly_encounters", :member => {
        :play => :post,
        :reject => :post,
        :accept => :post,
        :status => :get,
        :hide => :post,
        :unhide => :post
      }
      c.all_friendly_encounters '/all_friendly_encounters', :controller => 'friendly_encounters', :action => 'all'
      c.resources :custom_matches, :as => "custom_matches", :member => {
       :play => :post
      } do |cm|
        cm.resources :rounds
      end
      c.resources :matchdays,
        :as => "spieltage",
        :collection => {
        :move => :post,
      },
        :member => {
        :play => :post,
        :get_progress => :get,
        :reaggregate => :post,
        :reset => :post,
        :publish => :post,
        :disqualifications => :get,
        :get_standings => :get
      } do |md|
        md.resources :matches, :member => {:reset => :post, :set_review => :post, :play => :post} do |m|
          m.resources :rounds, :member => {
            :send_server_log => :post,
            :show_replay => :get,
            :disqualify => :get,
            :requalify => :get,
            :reset => :post
          }
        end
        md.standings '/rangliste', :controller => 'matchdays', :action => 'standings'
      end

      c.contestants '/meine-teams', :controller => 'contestants', :action => 'my', :name_prefix => 'my_contest_'
      c.resources :contestants, :as => "teams", :member => {
        :set_and_get_overall_member_count => :get,
        :hide => :get,
        :unhide => :get,
        :add_person => :post
      } do |contestant|
        contestant.resources :clients, :as => "computerspieler", :new => {
          :uploadify => :post
        }, :member => {
          :client_details => :get,
          :browse => :post,
          :select_main => :post,
          :select => :post,
          :test => :get,
          :hide => :post,
          :get_comments => :get,
          :create_comment => :post,
          :delete_comment => :get,
          :get_logs => :get,
          :send_log => :post
        } do |client|
          client.status '/status', :controller => "clients", :action => "status"
        end
        contestant.people '/mitglieder', :controller => "people", :action => "people_for_contestant", :conditions => { :method => :get }
        contestant.person '/mitglieder/einladen', :controller => "people", :action => "invite", :name_prefix => "invite_contest_contestant_", :conditions => { :method => :get }
        contestant.resources :people, :as => "mitglieder", :except => [:index], :member => {
          :remove => :post,
        } do |person|
          person.resource :email_event, :as => "mailer", :member => {
            :update => :put
          }
        end

        contestant.matches '/matches', :controller => "matches", :action => "index_for_contestant"
      end

      c.standings '/rangliste', :controller => 'contests', :action => 'standings'
      c.results '/ergebnisse', :controller => 'contests', :action => 'results'
      c.finale '/finale', :controller => 'finales', :action => 'index'
      c.resource :finale, :as => "finale", :member => {
         :lineup => :get,
         :ranking => :get,
         :match_results => :get,
         :play => :post,
         :prepare => :post,
         :prepare_day => :post,
         :delete_matchday => :post,
         :get_finale => :get,
         :get_matchday => :get,
         :play_all => :post,
         :switch_contestants => :get,
         :send_archive => :post,
         :publish => :post,
         :hide => :post,
         :delete => :post,
         :publish_lineup => :post,
         :hide_lineup => :post
      }
      c.resources :people, :as => "personen", :member => {
        :hide => :get,
        :unhide => :get,
        :validate_code => :get
      } do |person|
        person.resource :email_event, :as => "mailer", :member => {
          :update => :put
        }
      end 

      c.register '/register', :controller => 'main', :action => 'register', :conditions => { :method => :get }
      c.map '/register', :controller => 'main', :action => 'do_register', :conditions => { :method => :post }
      c.administration '/administration', :controller => 'main', :action => 'administration'
      c.debug '/debug', :controller => 'main', :action => 'debug', :conditions => { :method => :get }
      c.clear_jobs '/clear_jobs', :controller => 'main', :action => 'clear_jobs', :conditions => { :method => :post }
      c.login '/login', :controller => 'main', :action => 'login', :conditions => { :method => :get }
      c.map '/login', :controller => 'main', :action => 'do_login', :conditions => { :method => :post }
      c.new_password '/new_password', :controller => 'main', :action => 'new_password'
      c.logout '/logout', :controller => 'main', :action => 'logout', :conditions => { :method => :post }
      c.contests '/contests', :controller => 'contests', :action => 'index', :conditions => {:method => :get}
      c.map '/contests', :controller => 'contests', :action => 'create', :conditions => {:method => :post }
      c.new '/neu', :controller => 'contests', :action => 'new'
      
      # TODO: In rails 3 replaye with math '/forum' => redirect("...")
      map.connect '/forum', :controller => 'application', :action => 'forum'
      map.connect '/faq', :controller => 'application', :action => 'faq'

      #map.connect ':controller/:action/:id'
      #map.connect ':subdomain/:action', :controller => 'contests'
    end
  end

  #map.contest ':id/:action', :controller => 'contests'
  # The priority is based upon order of creation: first created -> highest priority.

  # Sample of regular route:
  #   map.connect 'products/:id', :controller => 'catalog', :action => 'view'
  # Keep in mind you can assign values other than :controller and :action

  # Sample of named route:
  #   map.purchase 'products/:id/purchase', :controller => 'catalog', :action => 'purchase'
  # This route can be invoked with purchase_url(:id => product.id)

  # Sample resource route (maps HTTP verbs to controller actions automatically):
  #   map.resources :products

  # Sample resource route with options:
  #   map.resources :products, :member => { :short => :get, :toggle => :post }, :collection => { :sold => :get }

  # Sample resource route with sub-resources:
  #   map.resources :products, :has_many => [ :comments, :sales ], :has_one => :seller
  
  # Sample resource route with more complex sub-resources
  #   map.resources :products do |products|
  #     products.resources :comments
  #     products.resources :sales, :collection => { :recent => :get }
  #   end

  # Sample resource route within a namespace:
  #   map.namespace :admin do |admin|
  #     # Directs /admin/products/* to Admin::ProductsController (app/controllers/admin/products_controller.rb)
  #     admin.resources :products
  #   end

  # You can have the root of your site routed with map.root -- just remember to delete public/index.html.
  # map.root :controller => "welcome"

  # See how all your routes lay out with "rake routes"

  #map.with_options :controller => 'main' do |opt|
    #opt.debug '/debug', :action => 'debug', :conditions => { :method => :get }
    #opt.clear_jobs '/clear_jobs', :action => 'clear_jobs', :conditions => { :method => :post }
    #opt.login '/login', :action => 'login', :conditions => { :method => :get }
    #opt.map '/login', :action => 'do_login', :conditions => { :method => :post }
    #opt.new_password '/new_password', :action => 'new_password'
    #opt.logout '/logout', :action => 'logout', :conditions => { :method => :post }
    #opt.administration '/administration', :action => 'administration'
  #end

  # Install the default routes as the lowest priority.
  # Note: These default routes make all actions in every controller accessible via GET requests. You should
  # consider removing the them or commenting them out if you're using named routes and resources.
 
  map.connect '/voranmeldung', :controller => 'contests', :contest_id => "voranmeldung"
  map.connect '/aktuell', :controller => 'contests', :contest_id => "aktuell"
  map.connect 'wettbewerb/:subdomain/:action', :controller => 'contests'
  map.connect ':controller/:action/:id'
  map.connect ':controller/:action/:id.:format'

  map.root :controller => 'main'

end 
