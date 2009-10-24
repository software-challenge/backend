require 'rubygems'
require 'rake'
require 'rake/testtask'

desc "Default Task"
task :default => [ :test ]

# Run the unit tests
Rake::TestTask.new :test do |t|
  t.libs << "test"
  t.pattern = 'test/*_test.rb'
  t.ruby_opts << '-rubygems'
  t.verbose = false
end