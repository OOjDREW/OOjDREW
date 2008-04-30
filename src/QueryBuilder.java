// OO jDREW Version 0.89
// Copyright (c) 2005 Marcel Ball
// This is the right one.
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

/*
 * QueryBuilder.java
 *
 * This class will Build a Query from an atom that was parsed from a message.
 * The Query will be in RuleML form and will be built using XOM
 *
 * Created on May 1, 2007, 2:57 PM
 */

import java.io.*;
import java.util.*;

import nu.xom.*;

import jdrew.oo.util.*;

public class QueryBuilder{
	
	/*
	 * This variable stores the query atom from the message
	 */
	
	private Element atom;
	
	/**
	 * The QueryBuilder Contstructor just assigns the atom instance variable to the
	 * value of the atom from the message
	 * @param XOM Element atomFromMessage - the atom contained in the message
	 */
	
	QueryBuilder(Element atomFromMessage){
		
		atom = atomFromMessage;
	}
	
	/** 
	 * This method will create the query based on the atom from the message
	 *
	 * @return String - The query in ruleml format
	 */
	
    String generateDoc(){
    	
      	Element root = new Element("Query");
		Document doc = new Document(root);
		
		Element implies = new Element("Implies");
    	root.appendChild(implies);
    	
    	//test
    	Element newA1 = new Element(atom);
    	implies.appendChild(newA1);
    	//test
    	/*
    	Element a1 = new Element("Atom");
    	implies.appendChild(a1);
    	
     	//getting atom values from the atom we parsed
    	
    	Elements els = atom.getChildElements();
    	
    	Attribute aType;
    	
      	for (int i = 0; i < els.size(); i++) {
        		Element ele = els.get(i);
        		System.out.println("Checking Elements: " + ele.toString());
        		
        		String name =  ele.getLocalName();
        		
        		String value = ele.getValue();
        		
        		aType = ele.getAttribute("type"); 
        		
        		if(aType != null){
        	
        			System.out.println("checking attirbute test 5");
        			System.out.println("TYPE ATTRIBUTE: " + aType.toString());
        		}
        		Element e = new Element(name);     		
        		
        		if(aType!=null){
        			Attribute newA = new Attribute(aType);
        			e.addAttribute(newA);
        		}
        		
        		e.insertChild(value,0);
   

        		
        		a1.appendChild(e);       		
      	}
    	*/   	
    	Element a2 = new Element("Atom");
    	implies.appendChild(a2);
    	
    	Element rel = new Element("Rel");
    	rel.insertChild("$top",0);
    	a2.appendChild(rel);    	
    	//implies.appendChild(atom);
    	String q = null;

    	String d = doc.toXML();
    	   	
    	String s1 = d.substring(23);
   	
    	return s1;
    }
	
}