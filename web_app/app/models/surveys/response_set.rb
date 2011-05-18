module Surveyor
  module Models
    module ResponseSetMethods
      def self.included(base)
        # Associations
        base.send :belongs_to, :survey
        base.send :belongs_to, :user, :class_name => 'Person'
        base.send :has_many, :responses, :dependent => :destroy
        base.send :accepts_nested_attributes_for, :responses, :allow_destroy => true
        
        @@validations_already_included ||= nil
        unless @@validations_already_included
          # Validations
          base.send :validates_presence_of, :survey_id
          base.send :validates_associated, :responses
          base.send :validates_uniqueness_of, :access_code
          
          @@validations_already_included = true
        end

        # Attributes
        base.send :attr_protected, :completed_at
        
        # Class methods
        base.instance_eval do
          def reject_or_destroy_blanks(hash_of_hashes)
            puts "INC: #{hash_of_hashes.inspect}"
            result = {}
            (hash_of_hashes || {}).each_pair do |k, hash|
              if has_blank_value?(hash)
                result.merge!({k => hash.merge("_destroy" => "true")}) if hash.has_key?("id")
              else
                result.merge!({k => hash})
              end
            end
            result
          end
          def has_blank_value?(hash)
            return hash["answer_id"].all?{|v| v.blank?} if hash["answer_id"].is_a?(Array) and hash["answer_id"].all?{|s| s.is_a? String}  
            hash["answer_id"].blank? or hash.all?{|k,v| v.is_a?(Array) ? v.all?{|x| x.to_s.blank?} : v.to_s.blank?}
          end
       end
          
      # Instance methods
      def initialize(*args)
        super(*args)
        default_args
      end

      def default_args
        self.started_at ||= Time.now
        self.access_code = Surveyor::Common.make_tiny_code
      end

      def access_code=(val)
        while ResponseSet.find_by_access_code(val)
          val = Surveyor::Common.make_tiny_code
        end
        super
      end

      # Updating the responses without getting duplicates is not quite easy. 
      # But in all cases tested this should work.
      def update_responses(hash_of_responses)
        updates = []
        new_responses = {}
        hash_of_responses.each do |k,v|
          if v['answer_id'].is_a? Array
            v['answer_id'].delete("")
            v['answer_id'] = v['answer_id'].first
          end
          resp = responses.select{|r| r.question_id.to_s == v['question_id']}
          resp = resp.select{|r| r.answer_id.to_s == v['answer_id']} if resp.first and resp.first.question.pick != "one"
          resp = resp.first if resp
          if resp
            v['id'] = resp.id 
            updates << v
          else
            if new_responses[v['answer_id']] 
              new_responses[v['answer_id']].merge! v
            else 
              new_responses[v['answer_id']] = v
            end
          end
        end
        update_attributes :responses_attributes => updates + new_responses.values
      end


      def to_csv(access_code = false, print_header = true)
        qcols = Question.content_columns.map(&:name) - %w(created_at updated_at)
        acols = Answer.content_columns.map(&:name) - %w(created_at updated_at)
        rcols = Response.content_columns.map(&:name)
        require 'fastercsv'
        FCSV(result = "") do |csv|
          csv << (access_code ? ["response set access code"] : []) + qcols.map{|qcol| "question.#{qcol}"} + acols.map{|acol| "answer.#{acol}"} + rcols.map{|rcol| "response.#{rcol}"} if print_header
          responses.each do |response|
            csv << (access_code ? [self.access_code] : []) + qcols.map{|qcol| response.question.send(qcol)} + acols.map{|acol| response.answer.send(acol)} + rcols.map{|rcol| response.send(rcol)}
          end
        end
        result
      end
      def complete!
        self.completed_at = Time.now
      end

      def correct?
        responses.all?(&:correct?)
      end
      def correctness_hash
        { :questions => survey.sections_with_questions.map(&:questions).flatten.compact.size,
          :responses => responses.compact.size,
          :correct => responses.find_all(&:correct?).compact.size
        }
      end
      def mandatory_questions_complete?
        progress_hash[:triggered_mandatory] == progress_hash[:triggered_mandatory_completed]
      end
      def progress_hash
        qs = survey.sections_with_questions.map(&:questions).flatten
        ds = dependencies(qs.map(&:id))
        triggered = qs - ds.select{|d| !d.is_met?(self)}.map(&:question)
        { :questions => qs.compact.size,
          :triggered => triggered.compact.size,
          :triggered_mandatory => triggered.select{|q| q.mandatory?}.compact.size,
          :triggered_mandatory_completed => triggered.select{|q| q.mandatory? and is_answered?(q)}.compact.size
        }
      end
      def is_answered?(question)
        %w(label image).include?(question.display_type) or !is_unanswered?(question)
      end
      def is_unanswered?(question)
        self.responses.detect{|r| r.question_id == question.id}.nil?
      end

      # Returns the number of response groups (count of group responses enterted) for this question group
      def count_group_responses(questions)
        questions.map{|q| responses.select{|r| (r.question_id.to_i == q.id.to_i) && !r.response_group.nil?}.group_by(&:response_group).size }.max
      end

      def unanswered_dependencies
        dependencies.select{|d| d.is_met?(self) and self.is_unanswered?(d.question)}.map(&:question)
      end

      def all_dependencies(question_ids = nil)
        arr = dependencies(question_ids).partition{|d| d.is_met?(self) }
        {:show => arr[0].map{|d| d.question_group_id.nil? ? "q_#{d.question_id}" : "qg_#{d.question_group_id}"}, :hide => arr[1].map{|d| d.question_group_id.nil? ? "q_#{d.question_id}" : "qg_#{d.question_group_id}"}}
      end

      # Check existence of responses to questions from a given survey_section
      def no_responses_for_section?(section)
        !responses.any?{|r| r.survey_section_id == section.id}
      end

      protected

      def dependencies(question_ids = nil)
        Dependency.all(:include => :dependency_conditions, :conditions => {:dependency_conditions => {:question_id => question_ids || responses.map(&:question_id)}})
      end

      end
    end
  end
end

class ResponseSet < ActiveRecord::Base
  include Surveyor::Models::ResponseSetMethods
  
  alias :person :user
  attr_accessor :person
  has_one :survey_token
  validates_presence_of :survey_token 

  def complete?
   !!completed_at
  end
end

