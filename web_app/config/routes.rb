ActionController::Routing::Routes.draw do |map|
  map.with_options :path_names => { :new => "neu", :edit => "bearbeiten" } do |tmap|

    tmap.resources :people, :as => "personen"

    tmap.resources :contests,
      :as => "wettbewerbe",
      :path_prefix => '/administration',
      :name_prefix => 'admin_'
    tmap.resource :contest,
      :as => "wettbewerb",
      :member => {
      :refresh_matchdays => :post,
      :reset_matchdays => :post,
    } do |c|
      c.edit_schedule '/spielplan/bearbeiten', :controller => "contests", :action => "edit_schedule"
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
        :publish => :post
      } do |md|
        md.resources :matches, :member => {:reset => :post} do |m|
          m.resources :rounds
        end
        md.standings '/rangliste', :controller => 'matchdays', :action => 'standings'
      end

      c.contestants '/meine-teams', :controller => 'contestants', :action => 'my', :name_prefix => 'my_contest_'
      c.resources :contestants, :as => "teams" do |contestant|
        contestant.resources :clients, :as => "computerspieler", :new => {
          :uploadify => :post
        }, :member => {
          :client_details => :get,
          :browse => :post,
          :select_main => :post,
          :select => :post,
          :test => :post,
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
        contestant.person '/mitglieder/einladen', :controller => "people", :action => "invite",
          :name_prefix => "invite_contest_contestant_", :conditions => { :method => :get }
        contestant.resources :people, :as => "mitglieder", :except => [:index]

        contestant.matches '/matches', :controller => "matches", :action => "index_for_contestant"
      end

      c.standings '/rangliste', :controller => 'contests', :action => 'standings'
      c.results '/ergebnisse', :controller => 'contests', :action => 'results'
    end
  end

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

  map.with_options :controller => 'main' do |opt|
    opt.debug '/debug', :action => 'debug', :conditions => { :method => :get }
    opt.clear_jobs '/clear_jobs', :action => 'clear_jobs', :conditions => { :method => :post }
    opt.login '/login', :action => 'login', :conditions => { :method => :get }
    opt.map '/login', :action => 'do_login', :conditions => { :method => :post }
    opt.logout '/logout', :action => 'logout', :conditions => { :method => :post }
    opt.administration '/administration', :action => 'administration'
  end

  # Install the default routes as the lowest priority.
  # Note: These default routes make all actions in every controller accessible via GET requests. You should
  # consider removing the them or commenting them out if you're using named routes and resources.
  map.connect ':controller/:action/:id'
  map.connect ':controller/:action/:id.:format'

  map.root :controller => 'main'

end 
