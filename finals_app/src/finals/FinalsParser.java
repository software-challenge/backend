package finals;

import java.util.LinkedList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;



public class FinalsParser{
	public FinalsParser() {
		super();
	}

	Ranking currentRanking;
	Contestant currentContestant;
	
	
	 public void parse() {
		 
		  try {
		 
		     SAXParserFactory factory = SAXParserFactory.newInstance();
		     SAXParser saxParser = factory.newSAXParser();
		 
		     DefaultHandler handler = new DefaultHandler() {
		 
		 
		     public void startElement(String uri, String localName,
		        String qName, Attributes  attributes)
		        throws SAXException {
		 
		        System.out.println("Start Element :" + qName);
		 
		        if(qName == "ranking"){ 
		        	currentRanking = new Ranking(new LinkedList<Contestant>());
		        }
		        
		        if(qName == "rank"){
		        	
		        }
		 
		     }
		 
		     public void endElement(String uri, String localName,
		          String qName)
		          throws SAXException {
		 
		          System.out.println("End Element :" + qName);
		 
		     }
		 
		     public void characters(char ch[], int start, int length)
		         throws SAXException {
		 
		        }
		 
		      };
		 
		      saxParser.parse("/home/andre/testexport.xml", handler);
		 
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		  }

}
