package jdrew.oo.util;

import java.util.*;

/**
*
* This Class will implement the Subsumes Parser.  This allows user to use the POSL 
* syntax subsumes(superClass, subClass) to implement a user defined taxonomy.
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

public class SubsumesParser {
	
	String parseString;
	/** A Vector of the subsumes Objects in the system after parsing. */
	Vector subsumeObjects = new Vector();
	
	/** A Vector of Strings that contain all the classes in the type system after parsing. */
	Vector allClasses = new Vector();
	
	/** A Vector of RDFObjects in the type system after parsing. */
	Vector RDFObjects = new Vector();

	/** 
	 * This is the constructor for the subsumes parser.  All it needs is a string to be
	 * parsed.
	 *   
	 * @param String - parseString this is the POSL representation of the type system to be
	 * parsed
	 */
	public SubsumesParser(String parseString){
		
		this.parseString = parseString;
	}
	
	/**
	 * This method will parse the given String when the SubsumesParser was created. 
	 *
	 * @throws SubException - a Exception that could occur depending on invalid formatting
	 */
	
	public void parseSubsumes() throws SubException{
		this.buildSubsumeObjects();
		this.buildRDFObjects();
		this.buildTypes();
	}
	
	/**
	 * This method will parse through all the given subsumes relations and create
	 * objects for them.  The objects contain a 2 strings one for the super class and one 
	 * for the sub class.
	 * 
	 * @throws SubException a Exception that could occur depending on invalid formatting
	 */
	public void buildSubsumeObjects() throws SubException{
		
		 Vector rdfsObjects = new Vector();
	 	 //Parsing the POSL String given
		 POSLParser pp = new POSLParser();
		 try{
		 pp.parseDefiniteClauses(parseString);
		 }catch(Exception e){
			 throw new SubException("Incorrect POSL syntax");
		 }
		 //Obtaining the Parsed subsume objects	 
		 Iterator it = pp.iterator();
		 
		 //Iterating through all of the clauses to make sure they are of valid syntax
		 //Once they have been validated then a subsumes Object is created to 
		 //store the parameters of the subsumes objects
		 while (it.hasNext()) {
		    DefiniteClause dc = (DefiniteClause) it.next();
		    
		    Term[] atoms = dc.atoms;
	    	if(atoms.length != 1){
	    		throw new SubException("Types cannot contain a rule.");
	       	}
	    	Term t1 = atoms[0];
	    	String predicate = t1.getSymbolString();
	    	if(!predicate.equals("subsumes")){
	    		throw new SubException("Only subsumes facts can be in type definition.");	
	    	}
	    	Term[] terms = t1.getSubTerms();
	    	
    		if(terms.length != 3){
    			throw new SubException("Subsumes facts can only have 2 arguments.");
    		}
    		String[] varnames = dc.variableNames;
    		String term1 = terms[1].toPOSLString(varnames,true);
    		String term2 = terms[2].toPOSLString(varnames,true);
    		//System.out.println(term1 + " " + term2);
    	    boolean t1Var = false;
    	    boolean t2Var = false;
    		
    	    String term1Sub = term1.substring(0,1);
    	    String term2Sub = term2.substring(0,1);
    	    
    	    if(term1Sub.equals("?")){
    	    	throw new SubException("Subsumes facts cannot contain variables in type definitions");
    	    	
    	    }
    	    if(term2Sub.equals("?")){
    	    	throw new SubException("Subsumes facts cannot contain variables in type definitions");
    	    	
    	    }
    	    
    	    SubsumesObject so = new SubsumesObject(term1,term2);
    	    
    	    subsumeObjects.add(so);	 
		 }
		 
	}
	/**
	 * Iterating through all of the subsume objects to create RDF Objects
	 * RDF objects contain the the parent class and all of its sub classes.
	 */
	public void buildRDFObjects(){
		
		Iterator it = subsumeObjects.iterator();
		String superClass;
		String subClass;
		String compare;
		boolean firstPass = true;
		boolean superDuplicate = false;
		boolean subDuplicate = false;
		
		//Looping through the subsume Objects to determine all of the classes in the
		//Type System duplicates must be removed.
		
		while(it.hasNext()){
			superDuplicate = false;
			subDuplicate = false;
			
			Object b = it.next();
			SubsumesObject so = (SubsumesObject) b;
			
			superClass = so.getSuperClass();
			subClass = so.getSubClass() ;

			if(firstPass){
				//System.out.println("First pass");
				firstPass = false;
				allClasses.add(superClass);
			}
			int classSize = allClasses.size();
			
			Iterator allClassIt = allClasses.iterator();
			//checking for duplicates
			while(allClassIt.hasNext()){
				
				Object comp = allClassIt.next();
				compare = comp.toString();
				
				if(superClass.equals(compare)){
					//System.out.println("Found duplicate super");
					superDuplicate = true;
				}
				
				if(subClass.equals(compare)){
					//System.out.println("Found duplicate sub");
					subDuplicate = true;
				}
			}	
			if(!superDuplicate){
				allClasses.add(superClass);
			}
			if(!subDuplicate){
				allClasses.add(subClass);			
			}
		}
						
		
		Iterator secondPass = allClasses.iterator();
		//make all RDF objects for each class
		//This iteration will create all of the RDF Objects needed
		//We need to find all of the subClasses of every class collected in the
		//allClasses Vector.
		while(secondPass.hasNext()){

			Object sup = secondPass.next();
			String mainClass = sup.toString();
			
			Iterator subObjects = subsumeObjects.iterator();
			//find all parents of each rdf object
			Vector allParents = new Vector();
			while(subObjects.hasNext()){	
				Object subObject = subObjects.next();
				SubsumesObject so = (SubsumesObject)subObject;
				String superC = so.getSuperClass();
				String subC   = so.getSubClass();
				
				if(mainClass.equals(subC)){
					allParents.add(superC);
				}
			
			}
			int size = allParents.size();
			String[] parents = new String[size];
			
			for(int i = 0 ; i < size; i++){
				Object parOb = allParents.elementAt(i);
				parents[i] = parOb.toString();
			}

			RDFObject ro = new RDFObject(mainClass,parents);			
			RDFObjects.add(ro);
		}	
	}
	/**
	 * This method will create the RDFS Types.  Once the RDF Objects have been created
	 * all that is left to do is define them in the Type Graph.
	 */
	public void buildTypes(){
		
		Iterator it = RDFObjects.iterator();
		
		while(it.hasNext()){
			
			Object ob = it.next();
			RDFObject rdfsOb = (RDFObject)ob;
			
			String name = rdfsOb.getName();
			String[] parents = rdfsOb.getParents();

			Types.createType(name, parents);
			
		}
	}
	
}//SubsumesParser Class

/**
 *  This class is used to create a subsumes Object, that contains a pair of strings.
 * 
 *  @author craigb
 */
class SubsumesObject{
	
	String superClass;
	String subClass;
	
	/**
	 * This is the constructor for a subsumes object
	 * 
	 * @param superClass the super class of the object
	 * @param subClass the sub class of the object
	 */
	SubsumesObject(String superClass, String subClass){
		
		this.superClass = superClass;
		this.subClass = subClass;		
	}
	
	/**
	 * Method to return the super class of the object
	 * @return String the super class
	 */
	String getSuperClass(){
		return superClass;
	}
	
	/**
	 * Method to return the sub class of the object
	 * @return String the sub class
	 */
	String getSubClass(){
		return subClass;
	}
}

/**
 * This class is used to construct an RDFObject
 *
 * @author craigb
 *
 */

class RDFObject{
	
	String name;
	String[] parents;
	
	/**
	 * Constructor for an RDFObject
	 * 
	 * @param name the name of the sub class
	 * @param parents of the sub class
	 */
	RDFObject(String name, String[] parents){
		
		this.name = name;
		this.parents = parents;
		
	}
	
	/**
	 * Get the name of the type to be created
	 * 
	 * @return String the name of the type to be created
	 */
	String getName(){
		return name;
	}
	
	/**
	 * This method will return the array of hte parents
	 * 
	 * @return String[] the parents
	 */
	String[] getParents(){
		return parents;
	}
	
}