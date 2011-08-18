class NewsPostsController < ApplicationController
  before_filter :fetch_news_post, :except => [:new, :feed, :create, :index]
  before_filter :fetch_context

  access_control do 
    allow :administrator

    actions :show, :index, :feed do
      allow all
    end
  end

  def show
    if @news_post and (@news_post.published? or administrator?)
      respond_to do |format|
        format.html
        format.js { render :partial => 'news_post' }
      end
    else
      flash[:error] = "News-Beitrag konnte nicht gefunden werden"
      redirect_to root_url
    end
  end

  def new
    @news_post = NewsPost.new()
  end

  def edit
  end

  def index
    @news_posts = (administrator? ? @context.news_posts.sort_by_update : @context.news_posts.published.sort_by_update)
  end

  def create
    @news_post = NewsPost.create(:title => params[:news_post][:title], :text => params[:news_post][:text], :person => @current_user, :context => @context)
    if @news_post
      flash[:notice] = "News wurden erfolgreich erstellt!"
      redirect_to [@context, @news_post]
    else
      flash[:error] = "Beim Erstellen trat ein Fehler auf!"
      render :action => 'new'
    end
  end

  def update
    @news_post.update_attributes(params[:news_post])
    if @news_post.save
      @news_post.translate!
      flash[:notice] = "News wurden erfolgreich bearbeitet."
      redirect_to [@context, @news_post]
    else
      flash[:error] = "Beim Bearbeiten trat ein Fehler auf!"
      render :action => 'edit'
    end
  end

  def destroy
    @news_post.destroy
    redirect_to [@¢ontext, :news]
  end
  
  def publish
    @news_post.publish!
    redirect_to :action => 'show'
  end

  def unpublish
     @news_post.unpublish!
    redirect_to :action => 'show'
  end

  def feed
    respond_to do |format|
      format.rss { render :layout => false} 
    end
  end

  protected

  def fetch_news_post
   @news_post = NewsPost.find_by_id(params[:id]) 
  end

  def fetch_context
    if @news_post
      @context = @news_post.context
    else
      @context = (@contest ? @contest : @season)
    end
  end
end
