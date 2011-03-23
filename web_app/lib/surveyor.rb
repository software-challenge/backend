require 'surveyor/common'
require 'surveyor/acts_as_response'
require 'formtastic/surveyor_builder'
#Formtastic::SemanticFormHelper.builder = Formtastic::SurveyorBuilder
Formtastic::SemanticFormBuilder.default_text_area_height = 4
Formtastic::SemanticFormBuilder.default_text_area_width = 50
Formtastic::SemanticFormBuilder.default_text_field_size = 50
Formtastic::SemanticFormBuilder.all_fields_required_by_default = false
