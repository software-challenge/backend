require 'rack/utils'

# From http://railstips.org/blog/archives/2009/07/21/uploadify-and-rails-2.3/
# and fixed by Marcel Jackwerth to support POST, as the original
# uses ::Rack::Utils.parse_query(env['QUERY_STRING']) which only works for GET
class FlashSessionCookieMiddleware
  def initialize(app, session_key = '_session_id')
    @app = app
    @session_key = session_key
  end

  def call(env)
    if env['HTTP_USER_AGENT'] =~ /^(Adobe|Shockwave) Flash/
      params = Rack::Request.new(env)

      unless params[@session_key].nil?
        env['HTTP_COOKIE'] = "#{@session_key}=#{params[@session_key]}".freeze
      end
    end

    @app.call(env)
  end
end
