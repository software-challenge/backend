SeasonDefinition.create "Software Challenge 2012" do
  
  #game_definition :HaseUndIgel
  subdomain "2012" 

  phase "warmup" do
    contest "north" do
      subdomain :nrth
       
      select_contestants do 
        contestants.select{|c| 
          begin 
            (%{Schleswig-Holstein Mecklenburg-Vorpommern Hamburg Bremen Brandenburg  Niedersachsen Berlin Sachsen-Anhalt Nordrhein-Westfalen}).include? c.school.state 
          rescue 
            false 
          end
        }
      end
      #import_contestants_from_contest "2010" do
      #  best 10
      #  exclude_by_id 22
      #end
       
      #match_all do 
      #  contestants_with :ranking, 'advanced'
      #  contestants_with :location, 'Kiel'
      #end
      
      #select_contestants do  
      #  contestants.select{|c| c.name.length < 50}
      #end

    end
    contest "south" do
      subdomain :sth

      select_contestants do 
        contestants.select{|c| 
          begin 
            (%{Sachsen Thüringen Hessen Rheinland-Pfalz Saarland Bayern Baden-Württemberg}).include? c.school.state 
          rescue 
            false 
          end
        }
      end
    end
  end
   
  phase "Champions-League" do
    contest "champions" do
      has_finale!

      import_contestants_from_contest "2012_nrth" do
        best 3
      end
      import_contestants_from_contest "2012_sth" do
        best 3
      end

      select_contestants do 
        contestants
      end

      #contestants_with :name, "Testschaf"
      #contestants_with :name, "Gym E'Hagen"
    end
  end
end
