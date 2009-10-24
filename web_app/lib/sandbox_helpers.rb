require 'sandbox'

module SoftwareChallenge
  module ScriptHelpers
    module Aggregate
      def assert_size(rows)
        if rows.empty?
          true
        else
          default_size = rows.first.size
          rows.each do |row|
            raise "row sizes didn't match" unless row.size == default_size
          end
        end
      end

      def sum_all(rows)
        if rows.empty?
          []
        else
          assert_size rows
          width = rows.first.size
          result = []
          width.times do |i|
            result << rows.inject(0) { |sum,x| sum + x[i] }
          end
          result
        end
      end
    end
  end
end