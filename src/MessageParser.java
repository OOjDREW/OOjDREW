// OO jDREW Version 0.89
// Copyright (c) 2005 Marcel Ball
// This is the right one.
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

/*
 * MessageParser.java
 *
 * This class will parse a RuleML message and generate a Query from the message.
 *
 * Created on May 1, 2007, 2:57 PM
 */

import java.io.*;
import java.util.*;
import nu.xom.*;
import jdrew.oo.util.*;

public class MessageParser{
	
	//The message
	private String input;
	
	//The following 4 variables are needed for the Messsage Generator
	
	//The message id
 	private String id;
 	//The message protocol
 	private String protocol;
 	//The sender of the message
 	private String sender;
 	//The relation of the message
 	private String relation;
 	
 	/**
 	 * Method to access the relation
 	 * @return String - the relation contained in the message
 	 */
 	 	
 	public String getRel(){
 		return relation;
 	}
 	
 	/**
 	 * Method to access the id
 	 * @return String - the id contained in the message
 	 */
 	
 	public String getId(){
 		return id;
 	}
 
 	/**
 	 * Method to access the protocol
 	 * @return String - the protocol contained in the message
 	 */
 	
 	public String getProtocol(){
 		return protocol;
 	}
 	
 	/**
 	 * Method to access the sender
 	 * @return String - the sender contained in the message
 	 */
 	
 	public String getSender(){
 		return sender;
 	}
 	
 	/**
 	 * Constructor for a message Parser
 	 * @param message - the message to be parsed
 	 */
 	
 	MessageParser(String message){
 		
 		input = message;
 		
 	}
 	
 	/**
 	 * This method sets the relation, id, protocol, and sender of the message
 	 * 
 	 * @returns XOM.Element - The xom element contains the Query Atom
 	 */
 	
	Element parseForContent() throws
            ParseException, ParsingException, ValidityException, IOException {
		
			String query = null;
			
			Element atom =null;
		
		    Builder bl = new Builder();
       		StringReader sr = new StringReader(input);
      	 	Document doc = bl.build(sr);
      	 	
      	 	System.out.println(doc.toXML());
      	 	
      	 	Element root = doc.getRootElement();
      	 	     	 	
      	 	Elements roots = root.getChildElements();

        	Element root2 = roots.get(0);     	
      	 	      	 	    	 	
      	 	Elements els = root2.getChildElements();
        
        	for (int i = 0; i < els.size(); i++) {
        		
        		Element el = els.get(i);
        		System.out.println(el.toString());	
        	    	    		
        		if (el.getLocalName().equals("oid")) {
        			Elements id2s = el.getChildElements();
        			Element id2 = id2s.get(0);
        			id = id2.getValue();
        		}
        		
        		if (el.getLocalName().equals("protocol")) {
        			Elements protocol2s = el.getChildElements();
        			Element protocol2 = protocol2s.get(0);
        			protocol = protocol2.getValue();
        		}
        		
        		if (el.getLocalName().equals("sender")) {
        			Elements sender2s = el.getChildElements();
        			Element sender2 = sender2s.get(0);
        			sender = sender2.getValue();        			
        		}
        		
        		if (el.getLocalName().equals("content")) {
        			 
        			 Elements atoms = el.getChildElements();
        			 atom = atoms.get(0);
  	
        			 Elements rels = atom.getChildElements();
        			 Element rel = rels.get(0);

        			 relation = rel.getValue();      			

        		}
        		
        	}
	
		return atom;
			
	}
 
}