module SurveyResultsHelper
  def display_response(r_set,question)
    sets = r_set.responses.select{|r| r.question == question}
	  	if sets.size == 0
  			return "-"
  		elsif sets.size >= 1
  			return (sets.first.string_value || sets.first.text_value || show_answer(sets.first))
  		else
  		  txt = ""
        sets.each do |set|
          txt << show_answer(set) + "<br/>"
        end
        return txt
		  end
  end
  
  def show_answer(set)
    if  set.answer.text == "Datetime"
      set.datetime_value.strftime("%d.%m.%Y, %H:%M")
    elsif set.float_value
      set.float_value.to_s
    elsif set.integer_value 
      set.integer_value.to_s
    else
      set.answer.text
    end
  end
end
