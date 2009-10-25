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

      def greater_equal_less(my_score, other_score, greater, equal, less)
        raise "my_score was nil" unless my_score
        raise "other_score was nil" unless other_score
        
        if my_score > other_score
          greater
        elsif my_score < other_score
          less
        else
          equal
        end
      end

      def aggregate(data, method = '')
        assert_size data

        transposed = data.transpose
        result = []
        i = 0
        
        method.downcase.each_char do |c|
          fragment = transposed[i]
          case c
          when 's'
            result << fragment.inject(0) { |sum,x| sum + x }
          when 'a'
            sum = fragment.inject(0) { |sum,x| sum + x }
            result << (sum / fragment.size)
          else
            raise "unknown aggregation type: #{c}"
          end
          i = i.next
        end

        result
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