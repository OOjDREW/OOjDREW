/**
* Class used to handle the data associated with a Subsumes or SubsumesPlus query
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

public class SubsumesStructure{
	
	private String superName;
	private String subName;
	private boolean superIsVar;
	private boolean subIsVar;
	
	/**
	 * Constructor for a SusbsumesStructure
	 * 
	 * @param superName - name of the super class
	 * @param subName - name of the sub class
	 * @param superIsVar - whether or not the super class is a variable or not
	 * @param subIsVar - whether or not the sub class is a variable or not
	 */
	public SubsumesStructure(String superName, String subName, boolean superIsVar, boolean subIsVar){
		
		this.superName = superName;
		this.subName = subName;
		this.superIsVar = superIsVar;
		this.subIsVar = subIsVar;
	}
	
	/**
	 * Access Method for superName
	 *  
	 * @return name of the super class
	 */
	public String getSuperName(){
		return superName;
	}
	
	/**
	 * Access Method for subName
	 *  
	 * @return name of the sub class
	 */
	public String getSubName(){
		return subName;
	}
	
	/**
	 * Access Method for superIsVar
	 *  
	 * @return true if the super class is a variable false otherwise
	 */
	public boolean getSuperVar(){
		return superIsVar;
	}
	
	/**
	 * Access Method for subIsVar
	 *  
	 * @return true if the sub class is a variable false otherwise
	 */
	public boolean getSubVar(){
		return subIsVar;
	}
}
