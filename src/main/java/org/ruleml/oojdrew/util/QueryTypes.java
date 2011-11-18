// OO jDREW Version 0.93
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package org.ruleml.oojdrew.util;

import java.util.Iterator;
import java.util.Vector;

/**
 *
 * This Class will implement all the methods required to Query the type information(Taxonomy)
 * It makes use of the methods defined in the Types class and is used as an API for
 * querying types.
 * 
 * Possible Queries include: 
 * 
 * To note subsumesPlus implements the transitive closure over the type system,
 * while subsumes is only direct subClasses or superClasses.
 * 
 * subsumes(superClass, subClass) -> Returns true if the superClass
 * 									 is direct superClass of the subClass, false
 * 									 otherwise.
 * 
 * subsumesPlus(superClass, subClass) -> Similar to subsumes except it is not 
 * 										 only direct subClasses it is any subClass of
 * 										 the superClass in the type graph.
 * 
 * subsumes(superClass, ?subClass) -> This will return all the direct subClasses of the
 *                                    superClass.
 *                                    
 * subsumesPlus(superClass, ?subClass) -> This will return all the subClasses of the
 * 										  superClass not just direct subClasses.
 * 
 * subsumes(?superClass,subClass) -> This will return all direct superClasses of the
 * 									 given subClass.
 * 
 * subsumesPlus(?superClass,subClass) -> This will return all superClasses of the given
 * 										 subClasses
 * 
 * subsumes(?superClass,?subClass) -> This will return all direct super Class and sub Class
 *                                    relations.
 * 
 * subsuemsPlus(?superClass,?subClass) -> This will return all super Class and sub Class
 * 									      relations, not only direct relations.
 *
 * lub(Class1,Class2,Class3,etc) -> returns the Least Upper Bound of a set of classes
 * 
 * glb(Class1,Class2,Class3,etc) -> returns the Greatest Upper Bound of a set of classes
 * 
 * <p>Title: OO jDREW</p>
 *
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.91</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * @author Ben Craig
 * @version 0.93
 */

public class QueryTypes {
	
	private Vector typeVector;
	
	public QueryTypes(){
		
		this.typeVector = Types.types;
	}
	
	/**
	 * This method will determine if a class is a subClass of another class during Querying
	 *
	 * This method uses transitive closure and not direct subClass.
	 *
	 * @param String - subClass this is the class that we are going to test if it is a sub class of the super class
	 * @param String - superClass this is the class that is going to be tested if it is a super class of the sub class
	 * @return boolean - true if the subClass is a sub class of the super class, false otherwise
	 */
	public boolean isSubClass(String subClass, String superClass) throws EngineException{
		
		return Types.isSubClass(superClass,subClass);
	}
	
	/**
	 * This method will determine is a class is a super class of another class during Querying
	 *
	 * This method uses transitive closure and not direct super Class.
	 *
	 *@param String - superClass this is the class that is going to be tested if it is a super class of the sub class
	 *@param String - subClass this is the class that is going to be tested if it a sub class of the super class
	 *@return boolean - true if the superClass is a super class of the sub class, false otherwise
	 */
	public boolean isSuperClass(String superClass, String subClass) throws EngineException {
		
		return Types.isSuperClass(superClass,subClass);
	}
	
	
	/**
	 * This method will determine if a class is a subClass of another class during Querying
	 *
	 * This method uses transitive closure and not direct subClass.
	 *
	 * @param String - subClass this is the class that we are going to test if it is a sub class of the super class
	 * @param String - superClass this is the class that is going to be tested if it is a super class of the sub class
	 * @return boolean - true if the subClass is a sub class of the super class, false otherwise
	 */
	public boolean isDirectSubClass(String subClass, String superClass) throws EngineException{
	
		return Types.edgeExist(superClass,subClass);
	}
	
	/**
	 * This method will determine is a class is a super class of another class during Querying
	 *
	 * @param String - superClass this is the class that is going to be tested if it is a super class of the sub class
	 * @param String - subClass this is the class that is going to be tested if it a sub class of the super class
	 * @return boolean - true if the superClass is a super class of the sub class, false otherwise
	 */
	public boolean isDirectSuperClass(String superClass, String subClass) throws EngineException {
		
		return Types.edgeExist(superClass,subClass);
	}

	/**
	 * This method will determine the greaterLowerBound of an Array of classes(Strings)
	 * @param String[] classes - this array contains all the classes that the user wants to test the greater lower bound for
	 * @return String - The class that is the greatest lower bound of the array of given classes
	 */
	public String greatestLowerBound(String[] classes) throws EngineException{
		
		return Types.greatestLowerBound(classes);
	}

	/**
	 * This method will determine the least upper bound of an array of classes
	 * @param String[] classes - this array contains all of the classes that the user wants to test the least upper bound for 
	 * @return String - the class that is the least upper bound of the array of given classes
	 */
	public String leastUpperBound(String[] classes) throws EngineException{
		
		return Types.leastUpperBound(classes);

	}	

	/**
	 * This method will find all the direct sub classes for a given class
	 *  
	 * @param String classString - this String contains the class name that all the sub classes will be found for
	 * @return String[] - all the strings in an array that are subclasses of the given class
	 */	
	public String[] getDirectSubClasses(String superClass){
		
		Iterator typeIt = typeVector.iterator();	
		Vector subClasses = new Vector();
		
		while(typeIt.hasNext()){
			Object b = typeIt.next();
			String compare = b.toString();
			if(Types.edgeExist(superClass,compare)){
					subClasses.addElement(compare);
			}	
		}
		
		int size = subClasses.size();
		
		String[] subClassesStr = new String[size];
		
		Iterator subIt = subClasses.iterator();
		
		int i = 0;
		while(subIt.hasNext()){
			String str = subIt.next().toString();
			subClassesStr[i] = str;
			i++;
		}
	
		return subClassesStr;	
	}
	
	/**
	 * This method will find all the direct super Classes for a given class
	 *  
	 * @param String classString - this String contains the class name that all the sub classes will be found for
	 * @return String[] - all the strings in an array that are subclasses of the given class
	 */	
	public String[] getDirectSuperClasses(String subClass){
		
		Iterator typeIt = typeVector.iterator();	
		Vector superClasses = new Vector();
		
		while(typeIt.hasNext()){
			Object b = typeIt.next();
			String compare = b.toString();
			if(Types.edgeExist(compare,subClass)){
				if(!compare.equals("Thing")){
					superClasses.addElement(compare);
				}
			}	
		}
		
		int size = superClasses.size();
		
		String[] superClassesStr = new String[size];
		
		Iterator subIt = superClasses.iterator();
		
		int i = 0;
		while(subIt.hasNext()){
			String str = subIt.next().toString();
			superClassesStr[i] = str;
			i++;
		}
		
		return superClassesStr;	
	}
	
	/**
	 * This method will find all the subClasses for a given class
	 * 
	 * This method uses transitive closure and not direct subClass.
	 * 
	 * @param String classString - this String contains the class name that all the sub classes will be found for
	 * @return String[] - all the strings in an array that are subclasses of the given class
	 */		
	public String[] findAllSubClasses(String classString) throws EngineException{
	
		Iterator typeIt = typeVector.iterator();	
		Vector subClasses = new Vector();
		
		while(typeIt.hasNext()){
			Object b = typeIt.next();
			String compare = b.toString();
			if(Types.isSubClass(compare,classString)){
				if(!compare.equals("Thing")){
					if(!compare.equals(classString)){
					subClasses.addElement(compare);
					}
				}
			}
		}
		
		int size = subClasses.size();
		
		String[] subClassesStr = new String[size];
		
		Iterator subIt = subClasses.iterator();
		
		int i = 0;
		while(subIt.hasNext()){
			String str = subIt.next().toString();
			subClassesStr[i] = str;
			i++;
		}
		
		return subClassesStr;	
	}

	/**
	 * This method will find all the super classes for a given class
	 * 
	 * This method uses transitive closure and not direct superClass.
	 * 
	 * @param String classString - this String contains the class name that all the super classes will be found for
	 * @return String[] - all the strings in an array that are super classes of the given class
	 */	
	public String[] findAllSuperClasses(String classString) throws EngineException{
		
		Iterator typeIt = typeVector.iterator();	
		Vector superClasses = new Vector();
		
		while(typeIt.hasNext()){
			Object b = typeIt.next();
			String compare = b.toString();
			if(Types.isSuperClass(compare,classString)){
				if(!compare.equals("Thing")){
					if(!compare.equals(classString)){
					superClasses.addElement(compare);
					}
				}
			}
		}
		
		int size = superClasses.size();
		
		String[] superClassesStr = new String[size];
		
		Iterator superIt = superClasses.iterator();
		
		int i = 0;
		while(superIt.hasNext()){
			String str = superIt.next().toString();
			superClassesStr[i] = str;
			i++;
		}
		
		return superClassesStr;	
	}

	/**
	 * This method will find all the super classes relations in the current type system.
	 * 
	 * This method uses transitive closure and not direct superClass.
	 * 
	 * @return Vector - the vector contains a set strings in the order of 
	 * superClass, subClass pairs it should be a 
	 * subsumes Object Vector(may be implemented later)
	 */
	
	public Vector findAllSuperClassesOfEverything() throws EngineException{
		Vector allSup = new Vector();
		Iterator typeIt = typeVector.iterator();
			while(typeIt.hasNext()){
				Object b = typeIt.next();
				
				String classStr = b.toString();
				
					if(!classStr.equals("Thing") &&
						!classStr.equals("Numeric") &&
						!classStr.equals("Integer") &&
						!classStr.equals("Real") &&
						!classStr.equals("String"))	{

						String[] allSupers = this.findAllSuperClasses(classStr);				
								
						for(int i = 0; i < allSupers.length;i++){
							allSup.add(allSupers[i]);
							allSup.add(classStr);
						}
					}	
			}
		
		return allSup;
	}
	
	/**
	 * This method will find all the sub classes relations in the current type system.
	 * 
	 * This method uses transitive closure and not direct sub Class.
	 * 
	 * @return Vector - the vector contains a set strings in the order of 
	 * superClass, subClass pairs it should be a 
	 * subsumes Object Vector(may be implemented later)
	 */
     public Vector findAllSubClassesOfEverything() throws EngineException{
		Vector allSubs = new Vector();
		Iterator typeIt = typeVector.iterator();
			while(typeIt.hasNext()){
				Object b = typeIt.next();
				String classStr = b.toString();
				
				//System.out.println("Class String: " + classStr);
				//System.out.println("All Subs of: " + classStr);
				if(!classStr.equals("Thing") &&
					   !classStr.equals("Numeric") &&
					   !classStr.equals("Integer") &&
					   !classStr.equals("Real") &&
					   !classStr.equals("String")){
						
						String[] allSubClasses = this.findAllSubClasses(classStr);				
										   					
					for(int i = 0; i < allSubClasses.length;i++){
							allSubs.add(classStr);
							allSubs.add(allSubClasses[i]);
					}
				}
			}	
		return allSubs;
	}
		
 	/**
 	 * This method will find all the direct super class relations in the current type system.
     *
 	 * @return Vector - the vector contains a set strings in the order of 
 	 * superClass, subClass pairs it should be a 
 	 * subsumes Object Vector(may be implemented later)
 	 */
	public Vector findAllDirectSuperClassesOfEverything() throws EngineException{
			Vector allSup = new Vector();
			Iterator typeIt = typeVector.iterator();
				while(typeIt.hasNext()){
					Object b = typeIt.next();
					
					String classStr = b.toString();

						if(!classStr.equals("Thing") &&
							!classStr.equals("Numeric") &&
							!classStr.equals("Integer") &&
							!classStr.equals("Real") &&
							!classStr.equals("String"))	{

							String[] allSupers = this.getDirectSuperClasses(classStr);				
									
							for(int i = 0; i < allSupers.length;i++){
								allSup.add(allSupers[i]);
								allSup.add(classStr);
							}
						}	
				}
			
			return allSup;
		}
	
 	/**
 	 * This method will find all the super classes in the current type system.
     *
 	 * @return Vector - the vector contains a set strings in the order of 
 	 * superClass, subClass pairs it should be a 
 	 * subsumes Object Vector(may be implemented later)
 	 */
	public Vector findAllDirectSubClassesOfEverything() throws EngineException{
			Vector allSubs = new Vector();
			Iterator typeIt = typeVector.iterator();
				while(typeIt.hasNext()){
					Object b = typeIt.next();
					String classStr = b.toString();

					if(!classStr.equals("Thing") &&
						   !classStr.equals("Numeric") &&
						   !classStr.equals("Integer") &&
						   !classStr.equals("Real") &&
						   !classStr.equals("String")){
							
							String[] allSubClasses = this.getDirectSubClasses(classStr);				
											   					
						for(int i = 0; i < allSubClasses.length;i++){
								allSubs.add(classStr);
								allSubs.add(allSubClasses[i]);
						}
					}
				}	
			return allSubs;
		}
}