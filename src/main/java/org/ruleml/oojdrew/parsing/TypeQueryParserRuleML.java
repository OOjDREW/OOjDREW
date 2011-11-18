/**
* Class used to Parse a Type query in RuleML format
* <p>Title: OO jDREW</p>
*
* <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
* 0.91</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* @author Ben Craig
* @version 0.96
*/
package org.ruleml.oojdrew.parsing;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.ruleml.oojdrew.util.LUBGLBStructure;
import org.ruleml.oojdrew.util.SubsumesStructure;
	
public class TypeQueryParserRuleML {

	private String queryContents;
	private boolean validPredicate;

	//defining the valid predicates for type querying
	public final static String LUB =  "LUB";
	public final static String GLB =  "GLB";
	public final static String SUBSUMES = "Subsumes";
	public final static String SUBSUMESPLUS = "SubsumesPlus";
	
	//valid RuleML tags
	private final static String REL = "Rel";
	private final static String VAR = "Var";
	
	//predicate used for querying
	private String predicate = "";
	

	/**
	 * Constructor for a TypeQueryParserRuleML
	 * 
	 * @param contents - the query to be parsed
	 */
	public TypeQueryParserRuleML(String contents){
	
		queryContents = contents;
	
	}
	
	public TypeQueryParserRuleML(){
	
	}
	
	/**
	 * Access Method to get the predicate name
	 * 
	 * @return the predicate name
	 */
	public String getPredicate(){
		return predicate;
	}
	
	/**
	 * Access method to see if a predicate is valid or not
	 * 
	 * @return true if the predicate is valid false otherwise
	 */
	public boolean getValidPredicate(){
		return validPredicate;
	}
	
	/**
	 * This method will parse the Query and determine if the predicate is valid or not
	 * 	
	 * @return Elements - the elements of the predicate that need to be further parsed
	 * @throws RuleMLTypeQueryException
	 * @throws ParseException
	 * @throws ParsingException
	 * @throws ValidityException
	 * @throws IOException
	 */
	public Elements parseForPredicate()  throws RuleMLTypeQueryException, ParseException, ParsingException, ValidityException, IOException{
					
	    Builder bl = new Builder();
   		StringReader sr = new StringReader(queryContents);
  	 	Document doc = bl.build(sr);
		
  	 	//get predicate element
  	 	Element root = doc.getRootElement();
     	
  	 	predicate = root.getLocalName();
  	 	
  	 	if(predicate.equalsIgnoreCase(LUB)  ||
  	 			predicate.equalsIgnoreCase(GLB) ||
  	 			predicate.equalsIgnoreCase(SUBSUMES) ||
  	 			predicate.equalsIgnoreCase(SUBSUMESPLUS)){
  	 	
  	 	   validPredicate = true;
  	 		
  	 	} else{
  	 		
  	 		validPredicate = false;
  	 		throw new RuleMLTypeQueryException("Only LUB, GLB, Subsumes, and SubsumesPlus are valid predcates");
  	 	}

  	 	//get all the roots the predicate
  	 	return root.getChildElements();
 	 	
	}
	
	/**
	 * This method will determine the structure of the subsumes Query
	 * 
	 * @param elements - the elements that need to be parsed into a subsumes structure
	 * @return subsumesStructure - structure used to query a subsumes or subsumesPlus predicate
	 * @throws RuleMLTypeQueryException
	 */
	public SubsumesStructure parseElementsSubsumesAndSubsumesPlus(Elements elements) throws RuleMLTypeQueryException{
		
		if(elements.size() != 2){
			throw new RuleMLTypeQueryException("Subsumes and Subsumes plus must have 2 arguments");
		}
		
		Element superClass = elements.get(0); 
		Element subClass = elements.get(1);
		
		String superName = superClass.getValue();
		String subName = subClass.getValue();
		boolean superVar = false;
		boolean subVar = false;
		
		if(superClass.getLocalName().equalsIgnoreCase(REL)){
			superVar = false;
		}else if(superClass.getLocalName().equalsIgnoreCase(VAR)){
			superVar = true;
		}else{
			throw new RuleMLTypeQueryException("Only Rel and Var tags can be used inside of Subsumes or SubsumesPlus");
		}
		
		if(subClass.getLocalName().equalsIgnoreCase(REL)){
			subVar = false;
		}else if(subClass.getLocalName().equalsIgnoreCase(VAR)){
			subVar = true;
		}else{
			throw new RuleMLTypeQueryException("Only Rel and Var tags can be used inside of Subsumes or SubsumesPlus");
		}

		if(superVar && subVar && superName.equalsIgnoreCase(subName)){
			throw new RuleMLTypeQueryException("Cannot have duplicate variable names in subsumes");
		}
		
		return new SubsumesStructure(superName, subName,  superVar, subVar);
	}
	
	/**
	 * This method will determine the structure of the LUB or GLB Query
	 * 
	 * @param elements - the elements that need to be parsed into a LUB or GLB structure
	 * @return LUBGLBStructure - structure used to query a LUB or GLB predicate
	 * @throws RuleMLTypeQueryException
	 */
	public LUBGLBStructure parseElementsGLBandLUB(Elements elements) throws RuleMLTypeQueryException{
		
		Element firstTerm = elements.get(0);
		
		
		ArrayList<String> terms = new ArrayList<String>();
		boolean resultVarUsed = false;
		String resultVarName = firstTerm.getValue(); 
					 
		if(firstTerm.getLocalName().equalsIgnoreCase(VAR)){
			resultVarUsed = true;
		}else if(!firstTerm.getLocalName().equalsIgnoreCase(REL)){
			throw new RuleMLTypeQueryException("Only Rel and Var tags can be used as the first term inside of a GLB or LUB");
		}
				
		int index = 0;
		//if the first term in the LUB / GLB function is a VAR we need the index to be set to 1
		if(resultVarUsed){
			index = 1;
		}else {
			index = 0;
		}

		//loop through children , add them to array list, skip first child if needed
		
		for(int i = index; i < elements.size(); i++){
			Element e = elements.get(i);
			if(!e.getLocalName().equalsIgnoreCase(REL)){
				throw new RuleMLTypeQueryException("Only Rel tags can be used for the second terms and on inside of a GLB or LUB");
			}
			System.out.println(e.getValue());
			terms.add(e.getValue());
		}

		return new LUBGLBStructure(terms, resultVarUsed, resultVarName);
	}
}
	