module SurveyResultsHelper
  def display_response(r_set,question, args = {})
    sets = r_set.responses.select{|r| r.question == question}
	  	if sets.size == 0
  			return "-"
  		elsif sets.size >= 1
  			return sets.map{|s| show_answer(s)}.join(", ")
  		else
  		  txt = ""
        sets.each do |set|
          txt << show_answer(set) + (args[:separator] || "<br/>")
        end
        return txt
		  end
  end
  
  def show_answer(set)
    case set.answer.response_class.downcase
      when "datetime"
        set.datetime_value.strftime("%d.%m.%Y, %H:%M")
      when "float"
        set.float_value.to_s
      when "integer"
        set.integer_value.to_s
      when "string"
        set.string_value
      when "answer"
        set.answer.text
      else
        ""
    end
  end
end
