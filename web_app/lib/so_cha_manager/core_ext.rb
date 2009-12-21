
# Available with Ruby 1.9
# http://blog.jayfields.com/2006/09/ruby-instanceexec-aka-instanceeval.html
class Object
  module InstanceExecHelper; end
  include InstanceExecHelper
  def instance_exec(*args, &block)
    begin
      old_critical, Thread.critical = Thread.critical, true
      n = 0
      n += 1 while respond_to?(mname="__instance_exec#{n}")
      InstanceExecHelper.module_eval{ define_method(mname, &block) }
    ensure
      Thread.critical = old_critical
    end
    begin
      ret = send(mname, *args)
    ensure
      InstanceExecHelper.module_eval{ remove_method(mname) } rescue nil
    end
    ret
  end
end

class Logger
  def log_formatted_exception(exception)
    fatal(
      "\n\n#{exception.class} (#{exception.message}):\n    " +
        exception.backtrace.join("\n    ") + "\n\n"
    )
  end
end

# Only available in Ruby 1.9
# File lib/tmpdir.rb, line 92
def Dir.mktmpdir(prefix_suffix=nil, tmpdir=nil)
  case prefix_suffix
  when nil
    prefix = "d"
    suffix = ""
  when String
    prefix = prefix_suffix
    suffix = ""
  when Array
    prefix = prefix_suffix[0]
    suffix = prefix_suffix[1]
  else
    raise ArgumentError, "unexpected prefix_suffix: #{prefix_suffix.inspect}"
  end
  tmpdir ||= Dir.tmpdir
  t = Time.now.strftime("%Y%m%d")
  n = nil
  begin
    path = "#{tmpdir}/#{prefix}#{t}-#{$$}-#{rand(0x100000000).to_s(36)}"
    path << "-#{n}" if n
    path << suffix
    Dir.mkdir(path, 0700)
  rescue Errno::EEXIST
    n ||= 0
    n += 1
    retry
  end

  if block_given?
    begin
      yield path
    ensure
      FileUtils.remove_entry_secure path
    end
  else
    path
  end
end

# easily create singleton-methods
class ::Object
  def define_singleton_method name, &body
    singleton_class = class << self; self; end
    singleton_class.send(:define_method, name, &body)
  end
end