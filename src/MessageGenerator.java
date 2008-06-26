// OO jDREW Version 0.89
// Copyright (c) 2005 Marcel Ball
// This is the right one.
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

/*
 * MessageGenerator.java
 *
 * This class will create messages to be sent back to the Prova engine.
 *
 * Created on May 1, 2007, 2:57 PM
 */
 
import java.io.*;
import java.util.*;
import jdrew.oo.util.*;

import nu.xom.*;

public class MessageGenerator{
	//Data contains the bindings from the query
	private Vector data;
	//Size is the number of variables in the query
	private int size;
	//Id, protocol, sender, and relation name are the parameters of the message that was sent
	private String id;
 	private String prot;
 	private String sender;
	private String relationName;
	
	/**
	 * MessageGenerator Contructor
	 *
	 * @param Vector input - the bindings of the query
	 * @param int varSize - the number of variables in the query
	 * @param String senderIn - the sender of the message
	 * @param String idIn - the id of the message
	 * @param String protocolIn - the protocol of the message
	 * @param String rel - the relation of the query
	 */
	MessageGenerator(Vector input, int varSize, String senderIn, String idIn, String protocolIn, String rel){
		size = varSize;
		data = input;
		id = idIn;	
		prot = protocolIn;
		sender = senderIn;
		relationName = rel;
	}

 /**
  * This method will generate the messages to be sent to the prova engine
  *
  * @return String[] - the messages to be sent back to the prova engine (could be multiple)
  */

	String[] Messages(){
		System.out.println("new testing stuff");
		System.out.println("varSize: " + size);
		
		String[] messages = new String[data.size()];
		
		Iterator it = data.iterator();
		
		Object[][] vars;
		int j = 0;
		
		while(it.hasNext()){
			
			
			
			Element root = new Element("RuleML");
			
		    root.setNamespaceURI("http://www.ruleml.org/0.91/xsd");
			root.addNamespaceDeclaration("xsi","http://www.w3.org/2001/XMLSchema-instance");
			
			Attribute a3 = new Attribute("xsi:SchemaLocation", 
										 "http://www.w3.org/2001/XMLSchema-instance","http://www.ruleml.org/0.91/xsd http://ibis.in.tum.de/research/ReactionRuleML/0.2/rr.xsd");
			
			root.addAttribute(a3);
												
			Document doc = new Document(root);
								
		    Element message = new Element("Message");
			
			Attribute a1 = new Attribute("mode", "outbound");
			Attribute a2 = new Attribute("directive", "answer");				
			
			message.addAttribute(a1);
			message.addAttribute(a2);
			
			root.appendChild(message);
			
			Element oid = new Element("oid");
        	message.appendChild(oid);
			
			Element ind = new Element("Ind");
			oid.appendChild(ind);
			
			ind.insertChild(id,0);
			
			Element protocol = new Element("protocol");
			message.appendChild(protocol);
			
			Element ind2 = new Element("Ind");
			protocol.appendChild(ind2);
			
			ind2.insertChild(prot,0);
			
			Element send = new Element("sender");
			message.appendChild(send);
			
			Element ind3 = new Element("Ind");
			send.appendChild(ind3);
			
			ind3.insertChild(sender,0);
			
			
			Element content = new Element("content");
			message.appendChild(content);
			
			Element atom = new Element("Atom");
			content.appendChild(atom);
			
			Element rel = new Element("Rel");
			atom.appendChild(rel);
			rel.insertChild(relationName,0);
			
			vars = (Object[][])it.next();
			
			String[] rev = new String[size];
			
			//check for type here
			//send back types
			//because my types come back as String:Type, i need to make the type distinction here
			//as well as the returns of plexs and CTerms
			
			//test([item1,item2,item3]).

			//test(cterm[item1,item2,item3]).

			//test(12:Real).
			
			for(int i = 0; i < size; i++){
						
				Object k   = vars[i][0];
				Object val = vars[i][1];
				System.out.println(i);
				System.out.println("k: " + k.toString() + " val: " + val.toString());
				
				rev[i] = val.toString();

				//test
				//Element e = new Element("Ind");
			    //e.insertChild(val.toString(),0);
				//atom.appendChild(e);
			
			}
						
			//for(int i = size-1; i >= 0; i--){
			//i will already have the element here
			//so i need a way to parse the xml here and return an element to append
			//I think its possible to do with the RuleML parser
					
			//Look at the Message parser to loop through 
			
			for(int i = 0; i < size; i++){
				
				Builder bl = new Builder();
				
				StringReader sr = new StringReader(rev[i]);
				Element newDocRoot = null;
				
				try{

					System.out.println("Building Doc -->	");
					System.out.println(rev[i]);
					Document doc2 = bl.build(sr);
					System.out.println(doc2.toXML());
					
					Element docRoot = doc2.getRootElement();
					//loop through them
					newDocRoot = new Element(docRoot);
	
				}
				catch(Exception e){
					System.out.println(e.toString());
					
				}
					
				
				//Element e = new Element("Ind");
				//e.insertChild(rev[i],0);
				atom.appendChild(newDocRoot);
				
			}
			
			String d = doc.toXML();
			
			System.out.println("Debug");
			System.out.println(d.toString());
			System.out.println("//Debug");
			
			
			String p1 = d.substring(0,241);
			String p2 = d.substring(251);
			
			d = p1 + " " + p2;
			
			messages[j] = d;
			j++;
			
		}
		
		return messages;
	}

	String[] Messages2(){
		
		String[] messages = new String[data.size()];
		
		Iterator it = data.iterator();
		
		Object[][] vars;
		
		int j = 0;
		
		while(it.hasNext()){
			
			
			String messageTest = "";
			
			messageTest = "<RuleML xmlns=\"http://www.ruleml.org/0.91/xsd\"" + "\n" +
					"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + "\n" +
					"xsi:SchemaLocation=\"http://www.ruleml.org/0.91/xsd " + "\n" +
					"http://ibis.in.tum.de/research/ReactionRuleML/0.2/rr.xsd\">";
			
			messageTest = messageTest + "\n" + "\n" +
			"<Message mode=\"outbound\" directive=\"answer\">";
			

			messageTest = messageTest + "\n" +
			"\t <oid>" + "\n" +
			"\t\t <Ind>" + id + "</Ind>" + "\n" +
			"\t </oid>";
			
			messageTest = messageTest + "\n" +
			"\t<protocol>" + "\n" +
			"\t\t<Ind>" + "esb" + "</Ind>" + "\n" +
			"\t</protocol>";
			
			messageTest = messageTest + "\n" +
			"\t<sender>" + "\n" +
			"\t\t<Ind>" + sender + "</Ind>" + "\n" +
			"\t</sender>";
			
			messageTest = messageTest + "\n" +
			"\t<content>";
			
			messageTest = messageTest + "\n" +
			"\t\t<Rulebase>";
			
			vars = (Object[][])it.next();

			for(int i = 0; i < size; i++){
				
				Object k   = vars[i][0];
				Object val = vars[i][1];
				
				String var = k.toString();
				
				messageTest = messageTest + "\n" + "\t \t \t<Equal>" + "\n";
				
				messageTest = messageTest + "\t\t\t\t <Var>";
				
				String variableName = var.substring(1,var.length());
				
				messageTest = messageTest + variableName + "</Var>" + "\n";
				
				String binding = val.toString();
				
				StringTokenizer st = new StringTokenizer(binding,"\n");
				System.out.println("COUNT:" + st.countTokens());
				
				while(st.hasMoreTokens()){
					
					messageTest = messageTest + "\t\t\t" + "         " + st.nextToken() + "\n";
				}
				
				//messageTest = messageTest +  binding + "\n";
				
				messageTest = messageTest + "\t\t\t</Equal>";
				
			}
			
			messageTest = messageTest + "\n" +
			"\t\t</Rulebase>";				
			
			messageTest = messageTest + "\n" +
			"\t</content>";
			
			messageTest = messageTest + "\n" + "</Message>";
			
			messageTest = messageTest + "\n" + "</RuleML>";
		
			Builder bTest = new Builder();
			StringReader srTest = new StringReader(messageTest);
			//System.out.println(messageTest);
			
			try{

				//Document docTest = bTest.build(srTest);
				//messages[j] = docTest.toXML();
				messages[j] = messageTest;
			}catch(Exception e){
				System.out.println(e.toString());
				
			}	
			
			
			j++;
			
		}//while	
		
		
		
		return messages;
	}
	
	String finalMessage(String query){
		
		String finalMessage = "";
		
		finalMessage = "<RuleML xmlns=\"http://www.ruleml.org/0.91/xsd\"" + "\n" +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + "\n" +
				"xsi:SchemaLocation=\"http://www.ruleml.org/0.91/xsd " + "\n" +
				"http://ibis.in.tum.de/research/ReactionRuleML/0.2/rr.xsd\">";
		
		finalMessage = finalMessage + "\n" + "\n" +
		"<Message mode=\"outbound\" directive=\"no_further_answers\">";
		

		finalMessage = finalMessage + "\n" +
		"\t <oid>" + "\n" +
		"\t\t <Ind>" + id + "</Ind>" + "\n" +
		"\t </oid>";
		
		finalMessage = finalMessage + "\n" +
		"\t<protocol>" + "\n" +
		"\t\t<Ind>" + "esb" + "</Ind>" + "\n" +
		"\t</protocol>";
		
		finalMessage = finalMessage + "\n" +
		"\t<sender>" + "\n" +
		"\t\t<Ind>" + sender + "</Ind>" + "\n" +
		"\t</sender>";
		
		finalMessage = finalMessage + "\n" +
		"\t<content>";
		
		finalMessage = finalMessage + "\n" + "\t" + "<Atom>";
		
		finalMessage = finalMessage + "\n" + "\t" + "\t"+ "<Rel>end</Rel>";
		
		finalMessage = finalMessage + "\n" + "\t" + "\t" + "\t"+ "<Ind>messages</Ind>";
		
		finalMessage = finalMessage + "\n" + "\t" + "</Atom>";
		
		
		
		
		finalMessage = finalMessage + "\n" +
		"\t</content>";
		
		finalMessage = finalMessage + "\n" + "</Message>";
		
		finalMessage = finalMessage + "\n" + "</RuleML>";
		
		return finalMessage;
	}
	
	
	String[] TestMessages(){

		String[] messages = new String[data.size()];
		
		Iterator it = data.iterator();
		
		Object[][] vars;
		int j = 0;
		while(it.hasNext()){
			
			String mes;
			
			mes = "Message mode=outbound directive=answer \n"+
			"Oid: " + id + "\n" +
			"Protocol: " + prot + "\n"+
		    "Sender: " + sender + "\n"+
		    "Content: " +"\n" +
		    "Relation: " + relationName + "\n";
		    		
			vars = (Object[][])it.next();
			
			for(int i = 0; i < size; i++){
				
				Object k   = vars[i][0];
				Object val = vars[i][1];
				
				mes = mes + "Variable: " + k.toString() + " Binding: " + val.toString() + "\n";
				
				
			}
			    	    
			messages[j] = mes;
			j++;
			
		}
		
		return messages;
	}
	
	
	
	
	
	
	
}
