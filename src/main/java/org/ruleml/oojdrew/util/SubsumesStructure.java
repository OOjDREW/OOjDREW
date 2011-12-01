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
