class NewsPostsController < ApplicationController

  access_control do 
    allow :administrator

    actions :show do
      allow all
    end
  end

  def show
    @news_post = NewsPost.find_by_id(params[:id])
    if @news_post and (@news_post.published? or administrator?)
      respond_to do |format|
        format.html
        format.js { render :partial => 'news_post' }
      end
    else
      flash[:error] = "News-Beitrag konnte nicht gefunden werden"
      redirect_to contest_url(@contest)
    end
  end

  def new
    @news_post = NewsPost.new()
  end

  def edit
    @news_post = NewsPost.find_by_id(params[:id])
  end

  def create
    @news_post = NewsPost.create(:title => params[:news_post][:title], :text => params[:news_post][:text], :person => @current_user)
    if @news_post
      flash[:notice] = "News wurden erfolgreich erstellt!"
      redirect_to contest_news_post_url(@contest,@news_post)
    else
      flash[:error] = "Beim Erstellen trat ein Fehler auf!"
      render :action => 'new'
    end
  end

  def update
    @news_post = NewsPost.find_by_id(params[:id])
    @news_post.update_attributes(params[:news_post])
    if @news_post.save
      @news_post.translate!
      flash[:notice] = "News wurden erfolgreich bearbeitet."
      redirect_to contest_news_post_url(@contest,@news_post)
    else
      flash[:error] = "Beim Bearbeiten trat ein Fehler auf!"
      render :action => 'edit'
    end
  end

  def destroy
    @news_post = NewsPost.find_by_id(params[:id])
    @news_post.destroy
    redirect_to contest_url(@contest)
  end
  
  def publish
    @news_post = NewsPost.find_by_id(params[:id])
    @news_post.publish!
    redirect_to :action => 'show'
  end

  def unpublish
    @news_post = NewsPost.find_by_id(params[:id])
    @news_post.unpublish!
    redirect_to :action => 'show'
  end
end
