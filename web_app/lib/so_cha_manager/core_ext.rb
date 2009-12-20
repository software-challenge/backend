
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