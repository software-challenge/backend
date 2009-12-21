ActionController::Routing::Routes.draw do |map|
  map.with_options :path_names => { :new => "neu", :edit => "bearbeiten" } do |map|
    map.resources :people, :as => "personen"

    map.resources :contestants, :as => "teilnehmer" do |contestant|
      contestant.resources :clients, :as => "computerspieler", :member => {
        :browse => :post,
        :select_main => :post,
        :select => :post
      }
    end

    map.administration '/administration', :controller => 'administration', :action => 'index'

    map.resources :contests,
      :as => "wettbewerbe",
      :member => {
        :refresh_matchdays => :post,
        :reset_matchdays => :post,
    } do |c|
      c.resources :matchdays, 
        :as => "spieltage",
        :member => {
          :play => :post,
          :reaggregate => :post,
          :reset => :post
      } do |md|
        md.resources :matches do |m|
          m.resources :rounds
        end
      end
      c.resources :contestants, :as => "teilnehmer" do |contestant|
        contestant.resources :clients, :as => "computerspieler"
      end
    end

    map.with_options :controller => 'main' do |opt|
      opt.login '/debug', :action => 'debug'
      opt.login '/login', :action => 'login', :conditions => { :method => :get }
      opt.map '/login', :action => 'do_login', :conditions => { :method => :post }
      opt.logout '/logout', :action => 'logout', :conditions => { :method => :post }
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

    # Install the default routes as the lowest priority.
    # Note: These default routes make all actions in every controller accessible via GET requests. You should
    # consider removing the them or commenting them out if you're using named routes and resources.
    map.connect ':controller/:action/:id'
    map.connect ':controller/:action/:id.:format'

    map.root :controller => 'main', :action => 'index'
  end
end
