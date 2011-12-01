// OO jDREW - An Object Oriented extension of the Java Deductive Reasoning Engine for the Web
// Copyright (C) 2008 Ben Craig
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

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
