/**
* Class used to handle the data associated with a GLB or LUB query
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

package org.ruleml.oojdrew.util;

import java.util.ArrayList;

 public class LUBGLBStructure{
	
	//all the names of the objects to compute a GLB or LUB on
	private ArrayList<String> terms;
	//need to know if a result variable is used or not
	private boolean resultVarUsed;
	//Name of the result variable if one is sued
	private String resultVarName; 		
	
	/**
	 * Constructor for a LUBGLBStructure
	 * 
	 * @param terms - The different classes to compute a LUB or GLB with
	 * @param resultVarUsed - Whether or not a result variable is used or not in the query
	 * @param resultVarName - Name of the result variable for the query
	 */
	public LUBGLBStructure(ArrayList<String> terms, boolean resultVarUsed,String resultVarName){
	
		this.terms = terms;
		this.resultVarUsed = resultVarUsed;
		this.resultVarName = resultVarName;
	}
	
	/**
	 * Access Method for terms
	 *  
	 * @return ArrayList<String> of classes for the LUB or GLB Query
	 */
	public ArrayList<String> getTerms(){
		return terms;
	}
	
	/**
	 * Access Method Result Variable
	 * 
	 * @return true if a result variable is used false otherwise
	 */
	public boolean getResultVar(){
		return resultVarUsed;
	}
	/**
	 * Access Method for the name of the result variable
	 * 
	 * @return String the name of the result variable
	 */
	public String getResultVarName(){
		return resultVarName;
	}
	
}
