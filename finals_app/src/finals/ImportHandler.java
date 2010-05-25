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
	boolean playerOpen = false;
	boolean winnersOpen = false;
	boolean losersOpen = false;
	boolean rankOpen = false;
	boolean rankingOpen = false;
	
	
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
		        	rankOpen = true;
		        }
		        if(qName == "winner"){
		        	waitingForText = true;
		        }
		        
		        if(qName == "winners"){
		    		 currentMatch.winners = new LinkedList<Contestant>();
		    		 winnersOpen = true;
		    	 }
		        
		        if(qName == "losers"){
		        	currentMatch.losers = new LinkedList<Contestant>();
		        	losersOpen = true;
		        }
		        
		        if(qName == "players"){
		        	playerOpen = true;
		        }
		        
		        if(qName == "contestant" && (playerOpen || rankOpen)){
		        	currentContestant = new Contestant();
		        	currentContestant.rank = currentRank;
		        	currentContestant.name = attributes.getValue("name");
		        	currentContestant.home = attributes.getValue("location");
		        	
		        }
		        
		        if(qName == "contestant" && winnersOpen){
		        	currentMatch.winners.add(new Contestant(attributes.getValue("name"),attributes.getValue("location")));
		        }
		        
		        if(qName == "contestant" && losersOpen){
		        	currentMatch.losers.add(new Contestant(attributes.getValue("name"),attributes.getValue("location")));
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
		        
		        if(qName == "gameUID"){
		        	waitingForText = true;
		        }
		        
		        if(qName == "gameName"){
		        	waitingForText = true;
		        }
		        
		        if(qName == "contestant" && rankOpen){
		        	currentRanking.add(new Contestant(attributes.getValue("name"),attributes.getValue("location")),currentRank);
		        }
		     }
		 
		     public void endElement(String uri, String localName,
		          String qName)
		          throws SAXException {
		    	 
			    if(qName == "winners"){
			    	winnersOpen = false;
			    }
			        
			    if(qName == "losers"){
			       	losersOpen = false;
			     }
		    	 
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
		    	 
		    	 
		    	 
		    	 if(qName == "rank"){
		    		 //currentRanking.add(currentContestant, currentRank);
		    		 rankOpen = false;
		    	 }
		    	 
		    	 if(qName == "ranking"){
		    		 main.ranking = currentRanking;
		    	 }
		    	 
		    	 if(qName == "contestant" && currentMatch != null && playerOpen){
		    		 if(currentMatch.first == null){
		    			 currentMatch.first = currentContestant;
		    		 }else{
		    			 currentMatch.second = currentContestant;
		    		 }
		    	 }
		    	 
		    	 if(qName == "match"){
		    		 currentStepsMatches.add(currentMatch);
		    		 currentMatch = null;
		    		 }
		    	 
		    	 if(qName == "finalStep"){
		    		 if(order == 1){
		    			 Match temp = currentStepsMatches.get(1);
			    		 currentStepsMatches.set(1, currentStepsMatches.get(3));
			    		 currentStepsMatches.set(3, temp); 
			    		 temp = currentStepsMatches.get(3);
			    		 currentStepsMatches.set(3, currentStepsMatches.get(2));
			    		 currentStepsMatches.set(2, temp);
		    		 }
		    		 main.addFinalsStep(new Final_Step(main,currentStepsMatches,main.steps,false,order), order);
		    	 }
		    	 
		    	 if (qName == "gameUID") {
					//currentConfig.setServerStartupCommand(currentText);
				 }
		    	 
		    	 if(qName == "gameName"){
		    		 currentConfig.setSpielname(currentText);
		    	 }
		    	 
		    	 if(qName == "players"){
		    		 playerOpen = false;
		    	 }
		    	 
		    	 if(qName == "settings"){
		    		 main.config = currentConfig;
		    	 }
		     }
		     
		     public void characters (char ch[], int start, int length)
		     {
		    	if(waitingForText){
		    	 currentText = new String(ch, start, length);
		    	 waitingForText = false;
		    	}
		    	 
		     }
		     
		     public void endDocument() throws SAXException{
		    	 if(main.config == null || main.steps == null) throw new SAXException("");
		     }
		      }
