package finals;

import java.awt.Panel;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ImportHandler extends DefaultHandler{

	MainFrame main;
	Ranking currentRanking;
	Contestant currentContestant;
	FinalsConfiguration currentConfig;
	Match currentMatch;
	Round currentRound;	
	String currentReplay;
	Contestant currentWinner;
	String currentText;
	LinkedList<Match> currentStepsMatches;
	boolean waitingForText = false;
	int currentRank;
	LinkedList<Round> currentRounds;
	int order;
	
	
	 public void startElement(String uri, String localName,
		        String qName, Attributes  attributes)
		        throws SAXException {
		 
		        if(qName == "ranking"){ 
		        	currentRanking = new Ranking(new LinkedList<Contestant>());
		        }
		        
		        if(qName == "settings"){
		        	currentConfig = new  FinalsConfiguration();
		        }
		        
		        if(qName == "rank"){
		        	currentRank = Integer.parseInt(attributes.getValue("position"));
		        }
		        if(qName == "winner"){
		        	waitingForText = true;
		        }
		        
		        if(qName == "contestant"){
		        	currentContestant = new Contestant();
		        	currentContestant.rank = currentRank;
		        	currentContestant.name = attributes.getValue("name");
		        	currentContestant.home = attributes.getValue("location");
		        	
		        }
		        
		        if(qName == "finalStep"){
		        	currentStepsMatches = new LinkedList<Match>();
		        	order = Integer.parseInt(attributes.getValue("order"));
		        	if (order == 3) {
						order = 4;
					}else if (order == 4) {
						order = 3;
					}
		        	
		        }
		        
		        if(qName == "rounds"){
		        	currentRounds = new LinkedList<Round>();
		        }
		        
		        if(qName == "round"){
		        	currentRound = new Round();
		        }
		        
		        if(qName == "replay"){
		        	waitingForText = true;
		        }
		        
		        if(qName == "match"){
		        	currentMatch = new Match();
		        }
		     }
		 
		     public void endElement(String uri, String localName,
		          String qName)
		          throws SAXException {
		    	 //System.out.println(qName);
		    	 
		    	 if(qName == "replay"){
		    		 currentRound.filename = currentText;
		    	 }
		    	 
		    	 if(qName == "round"){
		    		 currentRounds.add(currentRound); 
		    	 }
		    	 
		    	 if(qName == "rounds"){
		    		 currentMatch.rounds = currentRounds;
		    	 }
		    	 
		    	 if(qName == "winner"){
		    		 currentRound.winner = new Contestant(currentText, "");
		    	 }
		    	 
		    	 if(qName == "ranking"){
		    		 currentRanking.add(currentContestant, currentRank);
		    	 }
		    	 
		    	 if(qName == "first"){
		    		 currentMatch.first = currentContestant;
		    	 }
		    	
		    	 if(qName == "second"){
		    		 currentMatch.second = currentContestant;
		    	 }
		    	 
		    	 if(qName == "match"){
		    		 currentStepsMatches.add(currentMatch);
		    		 }
		    	 
		    	 if(qName == "finalStep"){
		    		 main.addFinalsStep(new Final_Step(main.pan, main.contestPanel, currentStepsMatches,main.steps,false,order), order);
		    	 }
		    	 
		    	 if (qName == "gameUID") {
					currentConfig.setServerStartupCommand(currentText);
				 }
		    	 
		    	 if(qName == "gameName"){
		    		 currentConfig.setSpielname(currentText);
		    	 }
		     }
		     
		     public void characters (char ch[], int start, int length)
		     {
		    	if(waitingForText){
		    	 currentText = new String(ch, start, length);
		    	 waitingForText = false;
		    	}
		    	 
		     }
		 
		      }
