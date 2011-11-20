// OO jDREW - An Object Oriented extension of the Java Deductive Reasoning Engine for the Web
// Copyright (C) 2005 Marcel Ball
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

import java.util.Vector;

import org.apache.log4j.Logger;

import ptolemy.graph.DirectedAcyclicGraph;

/**
 * This class represents the types that are defined within OO jDREW's built-in
 * term typing system.
 *
 * While a set of unary predicates can be used to represent type sorts using
 * a built-in system is considerably more efficient and allows for the easier
 * creation of generalized built-in relations.
 *
 * <p>Title: OO jDREW</p>
 *
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.88</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * @author Marcel A. Ball
 * @version 0.89
 */
public class Types {
	
		public static void main(String[] args){
			
			System.out.println("hi");
			Types.reset();
		}
	
        /**
         * The integer code for the base type of the type system (Thing) - this
         * value should not be used; ITHING should be used instead. This is kept
         * for compatability purposes.
         */
        public static final int IOBJECT = 0;

        /**
         * The integer code for the base type of the type system (Thing).
         */
        public static final int ITHING = 0;

        /**
         * The type name for the base type of the type system (Thing).
         */
        public static final String OBJECT = "Thing";

        /**
         * The integer code for the bottom of the type lattice (Nothing). This
         * type inherits from all other types in the system.
         */
        public static final int INOTHING = -1;

        /**
         * The name for the bottom of the type lattice (Nothing).
         */
        public static final String NOTHING = "Nothing";

        /**
         * The name for the base type for all numeric types in the type lattice.
         */
        public static final String NUMERIC = "Numeric";

        /**
         * The name for the integer type in the type lattice.
         */
        public static final String INTEGER = "Integer";

        /**
         * The name for the floating point number type in the type lattice.
         */
        public static final String FLOAT = "Real";

        /**
         * The name for the string type in the type lattice.
         */
        public static final String STRING = "String";

        /**
         * The integer code for the integer type.
         */
        public static int IINTEGER;

        /**
         * The integer code for the floating point number type.
         */
        public static int IFLOAT;

        /**
         * The integer code for the numeric base type.
         */
        public static int INUMERIC;

        /**
         * The integer code for the String base type.
         */
        public static int ISTRING;

        /**
         * A vector that contains all of the type names. The index of a type
         * in this vector is the integer code that is used for that type.
         */
        public static Vector types = new Vector();
        
        /**
         * A vector that contains all types yet to be created. The index of a type
         * in this vector is the integer code that is used for that type.
         */
        public static Vector tempTypes = new Vector();
        

        /**
         * A dag that represents the types and their inheritance pathways.
         * This is used to efficiently determine the type relationships.
         */
        public static DirectedAcyclicGraph dag = new DirectedAcyclicGraph();
		
				
        static Logger log = Logger.getLogger("jdrew.oo.util.Types");

    /**
     * The static initialization of the base types in the type system.
     */
    static {
        types.add(OBJECT);
        dag.addNodeWeight(0);
        
        
        INUMERIC = createType(NUMERIC, new String[] {OBJECT});
        IINTEGER = createType(INTEGER, new String[] {NUMERIC});
        IFLOAT = createType(FLOAT, new String[] {NUMERIC});
        ISTRING = createType(STRING, new String[] {OBJECT});
    }	

    /**
     * A method to reset the type system. Any clause related data structres that
     * were created before a call to reset() cannot be used after the reset as
     * the integer codes may no longer be valid.
     */
    public static void reset() {
        dag = new DirectedAcyclicGraph();
        types = new Vector();
        types.add(OBJECT);
        dag.addNodeWeight(0);
        INUMERIC = createType(NUMERIC, new String[] {OBJECT});
        IINTEGER = createType(INTEGER, new String[] {NUMERIC});
        IFLOAT = createType(FLOAT, new String[] {NUMERIC});
        ISTRING = createType(STRING, new String[] {OBJECT});
    }

    /**
     * Greg's Working on this
     *
     * @param name String The name of the new type to be defined.
     *
     * @param parents String[] An array containing the name of all direct
     * superclasses of the type.
     *
     * @return int The integer code for the type, this is what is stored in a
     * Term objects type instance variable.
     */
    public static int createDAGNode(String name) {
    	
                if (types.contains(name)) {
            log.warn("Type " + name +
                     " already exists, cannot create type definition.");
            throw new EngineException("Type " + name +
                    " already exists, cannot create type definition.");
        }

      

        int id = types.size();
        types.add(name);

        Integer iID = id;
        dag.addNodeWeight(iID);

        return id;
    }

    /**
     * Define a new type in the type system. The user must specify the name of
     * the type and the names of all direct superclasses.
     *
     * @param name String The name of the new type to be defined.
     *
     * @param parents String[] An array containing the name of all direct
     * superclasses of the type.
     *
     * @return int The integer code for the type, this is what is stored in a
     * Term objects type instance variable.
     */
    public static int createType(String name, String[] parents) {
        Integer[] parentids;

        if (types.contains(name)) {
            log.warn("Type " + name +
                     " already exists, cannot create type definition.");
            throw new EngineException("Type " + name +
                    " already exists, cannot create type definition.");
        }

        if (parents.length > 0) {
            parentids = new Integer[parents.length];

            for (int i = 0; i < parents.length; i++) {
                int id = types.indexOf(parents[i]);
                if (id == -1) {
                    log.warn("Type " + parents[i] +
                             " not defined, cannot create sub-type.");
                    throw new EngineException("Type " + parents[i] +
                            " not defined, cannot create sub-type.");
                }
                parentids[i] = id;
            }
        } else {
            parentids = new Integer[1];
            parentids[0] = IOBJECT;
        }

        int id = types.size();
        types.add(name);

        Integer iID = id;
        dag.addNodeWeight(iID);

        for (int i = 0; i < parentids.length; i++) {
            dag.addEdge(iID, parentids[i]);
        }

        return id;
    }

    
    
    
    
    
    /**
     * Store uncreated types temporarily
     * 
     * 
     * 
     */
    
    public static void storeTempTypes(String name, String[] parents){
	
    	Object[] obj = new Object[2];
	
    	obj[0] = name;
    	obj[1] = parents;
	
    	tempTypes.add(obj);
	
}    
    
    
/**
 * Retrieves and creates uncreated types temporarily
 * 
 * 
 * 
 */

    public static void makeTypes(){

    	Vector temp = new Vector();
    	while(!tempTypes.isEmpty()){	
    			Object obj[] = (Object[])tempTypes.remove(0);

    			String name = obj[0].toString();

    			Types.createDAGNode(name);
    			System.out.println(name);
    			temp.add(obj);
    	}

    	while(!temp.isEmpty()){
	
    		Object obj[] = (Object[])temp.remove(0);
    		String name = obj[0].toString();	
    		String[] parents = (String[])obj[1];
    		Types.createDAGEdges(name, parents);
	}

}       
    
    /**
     * Define a new type in the type system. The user must specify the name of
     * the type and the names of all direct superclasses.
     *
     * @param name String The name of the new type to be defined.
     *
     * @param parents String[] An array containing the name of all direct
     * superclasses of the type.
     *
     * @return int The integer code for the type, this is what is stored in a
     * Term objects type instance variable.
     */
    public static int createDAGEdges(String name, String[] parents) {
        Integer[] parentids;

        if (types.contains(name)) {
            log.warn("Type " + name +
                     " already exists, creating DAG edges.");
            }

        if (parents.length > 0) {
            parentids = new Integer[parents.length];

            for (int i = 0; i < parents.length; i++) {
                int id = types.indexOf(parents[i]);
                if (id == -1) {
                    log.warn("Type " + parents[i] +
                             " not defined, cannot create sub-type.");
                    throw new EngineException("Type " + parents[i] +
                            " not defined, cannot create sub-type.");
                }
                parentids[i] = id;
            }
        } else {
            parentids = new Integer[1];
            parentids[0] = IOBJECT;
        }

        Integer iID = types.indexOf(name);
        //dag.addNodeWeight(iID);

        for (int i = 0; i < parentids.length; i++) {
            dag.addEdge(iID, parentids[i]);
        }

        return 1;
    }
    
    
    /**
     * Get the type name for a specified type identification number.
     *
     * @param id int The integer typy identification number of the type.
     *
     * @return String The name of the type defined by that
     */
    public static String typeName(int id) {
        if (id < -1 || id >= types.size()) {
            throw new EngineException("No type has been defined with code " +
                                      id + ".");
        } else if (id == -1) {
            return "NOTHING";
        } else {
            return (String) types.get(id);
        }
    }

    /**
     * Get the type identification integer associated with a type name.
     *
     * @param name String The name of the type to get the identification number
     * for.
     *
     * @return int The identification integer associated with the type.
     */
    public static int typeID(String name) {

        if (name.equals(NOTHING)) {
            return -1;
        }
        if (types.contains(name)) {
            return types.indexOf(name);
        } else {
            throw new EngineException("There is no type " + name + " defined.");
        }
    }

    /**
     * Check to see if a type is defined for the identification integer passed.
     *
     * @param id int The type identification integer.
     *
     * @return boolean true if there is a type with an identification integer
     * equal to id, false otherwise.
     */
    public static boolean isTypeDefined(int id) {
        if (id < -1 || id >= types.size()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Check to see if there is a type with a specified name already defined.
     *
     * @param name String The type name to check.
     *
     * @return boolean true if there is a type defined with with a name equal to
     * the passed name, false otherwise.
     */
    public static boolean isTypeDefined(String name) {
        if (name.equals(NOTHING)) {
            return true;
        } else {
            return types.contains(name);
        }
    }

    /**
     * This method is a synonyom for the
     * isSubClass(int subClass, int superClass) method.
     *
     * @param subClass int The integer identification number of the sub-type to
     * test.
     *
     * @param superClass int The integer identification number of the super-type
     * to test.
     *
     * @return boolean true if the type identified by subClass is a sub-type of
     * the type identified by superClass, for if subClass and superClass are the
     * same types, false otherwise.
     */
    public static boolean isa(int subClass, int superClass) {
        return isSubClass(subClass, superClass);

    }

    /**
     * Check to see if the type identified by subClass is is a subclass of the
     * type identified by superClass. (Transitive Closure)
     *
     * @param subClass int The integer identification number of the sub-type to
     * test.
     *
     * @param superClass int The integer identification number of the super-type
     * to test.
     *
     * @return boolean true if the type identified by subClass is a sub-type of
     * the type identified by superClass, for if subClass and superClass are the
     * same types, false otherwise.
     */
    public static boolean isSubClass(int subClass, int superClass) {

        int glb = greatestLowerBound(superClass, subClass);
        if (glb == subClass) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check to see if type subClass is a sub-type of type superClass. (Transitive Closure)
     *
     * @param subClass String The sub-type to check.
     *
     * @param superClass String The super-type to check.
     *
     * @return boolean true if subClass is a sub-type of superClass, or if they
     * are the same type, false otherwise.
     */
    public static boolean isSubClass(String subClass, String superClass) {
        int idsub = typeID(subClass);
        int idsuper = typeID(superClass);

        return isSubClass(idsub, idsuper);
    }

    /**
     * Check to see if one type is the super-type of another. (Transitive Closure)
     *
     * @param superClass int The integer identification of the super-type.
     *
     * @param subClass int The integer identification of the sub-type
     *
     * @return boolean true if the type identified by superClass is a super-type
     * of the type identified by subClass, or if they are the same type, false
     * otherwise.
     */
    public static boolean isSuperClass(int superClass, int subClass) {
        return isSubClass(subClass, superClass);
    }

    /**
     * Check to see if one type is the super-type of another type. (Transitive Closure)
     *
     * @param superClass String The super-type to check.
     *
     * @param subClass String The sub-type to check.
     *
     * @return boolean true if superClass is a super-type of subClass, of if
     * they are the same type, false otherwise.
     */
    public static boolean isSuperClass(String superClass, String subClass) {
        int idsub = typeID(subClass);
        int idsuper = typeID(superClass);

        return isSubClass(idsub, idsuper);
    }

    /**
     * Find the greatest lower bound of two types. Identified by integer codes.
     *
     * @param class1 int The integer code for type 1.
     *
     * @param class2 int The integer code for type 2.
     *
     * @return int The integer code of the greatest lower bound of the two types.
     * If this is negative one (-1) then they have no real greatest lower bound,
     * only the artificial lower bound of 'Nothing'.
     */
    public static int greatestLowerBound(int class1, int class2) {
        if (class1 == -1 || class2 == -1) {
            return -1;
        }
        Integer glb = ((Integer) dag.greatestLowerBound(class1,
        		class2));
        if (glb == null) {
            return -1;
        } else {
            return glb.intValue();
        }
    }

    /**
     * Find the greatest lower bound of typed types.
     *
     * @param class1 String The first type.
     *
     * @param class2 String The second type.
     *
     * @return String The name of the type that is the greatest lower bound of
     * the two types. If this is "Nothing" then they have no real greatest lower
     * bound, just the artificial lower bound of 'Nothing'.
     */
    public static String greatestLowerBound(String class1, String class2) {
        int classid1 = typeID(class1);
        int classid2 = typeID(class2);
        int glb = greatestLowerBound(classid1, classid2);

        if (glb == -1) {
            return NOTHING;
        } else {
            return Types.typeName(glb);
        }
    }

    /**
     * Find the greatest lower bound of a list of types, identified by integer
     * codes.
     *
     * @param classes int[] An array containing the integer codes of the types
     * to find the greatest lower bound for.
     *
     * @return int The integer code for the type that is the greatest lower
     * bound of the passed types. If this is negative one (-1) then the passed
     * types have no real lower bound, just the artificial lower bound
     * 'Nothing'.
     */
    public static int greatestLowerBound(int[] classes) {
        Object[] classesobj = new Object[classes.length];
        for (int i = 0; i < classes.length; i++) {
            if (classes[i] == -1) {
                return -1;
            }
            classesobj[i] = classes[i];
        }
        Integer glb = (Integer) dag.greatestLowerBound(classesobj);

        if (glb == null) {
            return -1;
        } else {
            return glb.intValue();
        }
    }

    /**
     * Find the greater lower bound for a group of types.
     *
     * @param classes String[] An array containing the types to find the
     * greatest lower bound of.
     *
     * @return String The name of the type that is the greatest lower bound of
     * the types that are passed. If this is "Nothing" then the passed types
     * have no real lower bound, only the artificial lower bound of 'Nothing'.
     */
    public static String greatestLowerBound(String[] classes) {
        Object[] classesobj = new Object[classes.length];
        for (int i = 0; i < classes.length; i++) {
            int type = Types.typeID(classes[i]);
            if (type == -1) {
                return NOTHING;
            }
            classesobj[i] = type;
        }
        Integer glb = (Integer) dag.greatestLowerBound(classesobj);
        
        if (glb == null) {
            return NOTHING;
        } else {
            return Types.typeName(glb.intValue());
        }
    }

    /**
     * This method is used to test for a direct sub class or a direct super class
     * 
     * @param superClass - the super class to be tested for
     * @param subClass - the sub class to be tested for
     * @return boolean - true if a edge exist from the superClass to the subClass
     */
    
	public static boolean edgeExist(String superClass, String subClass){
		
		int classid1 = typeID(superClass);
		int classid2 = typeID(subClass);
		
		Object b = classid1;
		Object c = classid2;
		
		boolean hasEdge =  dag.edgeExists(c,b);
					 
		return hasEdge;
		
	}

    /**
     * Find the greatest lower bound of two types. Identified by integer codes.
     *
     * @param class1 int The integer code for type 1.
     *
     * @param class2 int The integer code for type 2.
     *
     * @return int The integer code of the greatest lower bound of the two types.
     * If this is negative one (-1) then they have no real greatest lower bound,
     * only the artificial lower bound of 'Nothing'.
     */
    public static int leastUpperBound(int class1, int class2) {
        if (class1 == -1 || class2 == -1) {
            return -1;
        }
        Integer lub = ((Integer) dag.leastUpperBound(class1,
        		class2));
        if (lub == null) {
            return -1;
        } else {
            return lub.intValue();
        }
    }

    /**
     * Find the greatest lower bound of type types.
     *
     * @param class1 String The first type.
     *
     * @param class2 String The second type.
     *
     * @return String The name of the type that is the greatest lower bound of
     * the two types. If this is "Nothing" then they have no real greatest lower
     * bound, just the artificial lower bound of 'Nothing'.
     */
    public static String leastUpperBound(String class1, String class2) {
        int classid1 = typeID(class1);
        int classid2 = typeID(class2);
        int lub = leastUpperBound(classid1, classid2);

        if (lub == -1) {
            return NOTHING;
        } else {
            return Types.typeName(lub);
        }
    }

    /**
     * Find the greatest lower bound of a list of types, identified by integer
     * codes.
     *
     * @param classes int[] An array containing the integer codes of the types
     * to find the greatest lower bound for.
     *
     * @return int The integer code for the type that is the greatest lower
     * bound of the passed types. If this is negative one (-1) then the passed
     * types have no real lower bound, just the artificial lower bound
     * 'Nothing'.
     */
    public static int leastUpperBound(int[] classes) {
        Object[] classesobj = new Object[classes.length];
        for (int i = 0; i < classes.length; i++) {
            if (classes[i] == -1) {
                return -1;
            }
            classesobj[i] = classes[i];
        }
        Integer lub = (Integer) dag.leastUpperBound(classesobj);

        if (lub == null) {
            return -1;
        } else {
            return lub.intValue();
        }
    }

    /**
     * Find the lower bound of a group of type.
     *
     * @param classes String[] An array containing the types to find the
     * greatest lower bound of.
     *
     * @return String The name of the type that is the greatest lower bound of
     * the types that are passed. If this is "Nothing" then the passed types
     * have no real lower bound, only the artificial lower bound of 'Nothing'.
     */
    public static String leastUpperBound(String[] classes) {
        Object[] classesobj = new Object[classes.length];
        for (int i = 0; i < classes.length; i++) {
            int type = Types.typeID(classes[i]);
            if (type == -1) {
                return NOTHING;
            }
            classesobj[i] = type;
        }
        Integer lub = (Integer) dag.leastUpperBound(classesobj);

        if (lub == null) {
            return NOTHING;
        } else {
            return Types.typeName(lub.intValue());
        }
    }

}
