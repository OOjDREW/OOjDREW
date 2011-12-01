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

package org.ruleml.oojdrew.BottomUp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.ruleml.oojdrew.BottomUp.Builtins.AssertBuiltin;
import org.ruleml.oojdrew.BottomUp.Builtins.BUBuiltin;
import org.ruleml.oojdrew.Builtins.AbsBuiltin;
import org.ruleml.oojdrew.Builtins.AddBuiltin;
import org.ruleml.oojdrew.Builtins.Builtin;
import org.ruleml.oojdrew.Builtins.CeilingBuiltin;
import org.ruleml.oojdrew.Builtins.ContainsBuiltin;
import org.ruleml.oojdrew.Builtins.ContainsIgnoreCaseBuiltin;
import org.ruleml.oojdrew.Builtins.CosBuiltin;
import org.ruleml.oojdrew.Builtins.DivideBuiltin;
import org.ruleml.oojdrew.Builtins.EndsWithBuiltin;
import org.ruleml.oojdrew.Builtins.EqualBuiltin;
import org.ruleml.oojdrew.Builtins.FloorBuiltin;
import org.ruleml.oojdrew.Builtins.GreaterThanBuiltin;
import org.ruleml.oojdrew.Builtins.GreaterThanOrEqualBuiltin;
import org.ruleml.oojdrew.Builtins.IntegerDivideBuiltin;
import org.ruleml.oojdrew.Builtins.LessThanBuiltin;
import org.ruleml.oojdrew.Builtins.LessThanOrEqualBuiltin;
import org.ruleml.oojdrew.Builtins.ModBuiltin;
import org.ruleml.oojdrew.Builtins.MultiplyBuiltin;
import org.ruleml.oojdrew.Builtins.NotEqualBuiltin;
import org.ruleml.oojdrew.Builtins.PowBuiltin;
import org.ruleml.oojdrew.Builtins.RoundBuiltin;
import org.ruleml.oojdrew.Builtins.SinBuiltin;
import org.ruleml.oojdrew.Builtins.StartsWithBuiltin;
import org.ruleml.oojdrew.Builtins.StringConcatBuiltin;
import org.ruleml.oojdrew.Builtins.StringEqualIgnoreCaseBuiltin;
import org.ruleml.oojdrew.Builtins.StringLengthBuiltin;
import org.ruleml.oojdrew.Builtins.StringLowerCaseBuiltin;
import org.ruleml.oojdrew.Builtins.StringUpperCaseBuiltin;
import org.ruleml.oojdrew.Builtins.SubstringBuiltin;
import org.ruleml.oojdrew.Builtins.SubtractBuiltin;
import org.ruleml.oojdrew.Builtins.TanBuiltin;
import org.ruleml.oojdrew.parsing.RuleMLParser.RuleMLFormat;
import org.ruleml.oojdrew.util.DefiniteClause;
import org.ruleml.oojdrew.util.Term;

import ptolemy.graph.DirectedAcyclicGraph;
import ptolemy.graph.Edge;
import ptolemy.graph.Node;

/**
 * This class implements the forward reasoner (bottom-up) modules of OO jDREW;
 * The unifier for this module is implemented by the jdrew.oo.bu.Unifier class
 * and a subsumption checking system is implemented by the
 * jdrew.oo.bu.Subsumption Class.
 *
 * A forward reasoner works by processing "new" facts; As each new fact is
 * processed unificiation with all previously exisiting rules is attempted;
 * if the unification is successful one of two things happens: if the resolvent
 * is a fact then it is added to the end of the new facts list; if the resolvent
 * is a rule then it is processed (attempting unification with all processed
 * facts) and is then added to the list of rules.
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
public class ForwardReasoner {
	
	public static enum RuleDescriptionLanguage
	{
		POSL,
		RuleML
	}

    /**
     * This Hashtable stores all known facts that have already been processed.
     * The facts are indexed by the predicate name of the head (only) atom of
     * the facts.
     *
     * For each predicate that has been used there is a key->value pair in the
     * hash table; where the key is the integer code for the predicate symbol
     * (in the form of an Integer object) and the value is a Vector containing
     * the DefiniteClause objects for all processed facts with that predicate
     * symbol.
     */
    private Hashtable oldFacts;

    /**
     * This Hashtable stores all known rules in the knowledge base (both those
     * provided and those that have been derived). The rules are indexed by the
     * predicate name of the first atom in the body of the rule.
     *
     * For each predicate that has been used there is a key->value pair in the
     * hash table; where the key is the integer code for the predicate symbol
     * (in the form of an Integer object) and the value is a Vector containing
     * the DefiniteClause objects for all processed facts with that predicate
     * symbol.
     */
    private Hashtable rules;

    /**
     * This Vector contains all new facts that have not yet been processed.
     */
    private Vector newFacts;

    /**
     * This Hashtable contains references to all registered built-ins (the
     * default built-ins are registered with the engine at the time of object
     * creation).
     *
     * The built-ins are stored as a key->value pair in the hash table; where
     * the integer code for the built-in predicate name (as an Integer object)
     * is the key and the object that implements the built-in (this will be
     * either a sub-class of BUBuiltin or a Class that implements the
     * jdrew.oo.builtins.Builtin interface and is wrapped in a BUBuiltin by
     * the system) is the value.
     *
     * User created built-ins can be registered with calls to
     * registerBuiltin(jdrew.oo.builtins.Builtin) [in the case of a generic
     * built-in] or registerBuiltin(jdrew.oo.bu.builtins.BUBuiltin) [in the
     * case of a BU specific built-in that requires access to the engine
     * data structures].
     */
    private Hashtable builtins;
	
	/**
	 * This number is set by the user(Set to zero to ignore it all together).
	 * To determine how many times they want to iterate over all the clauses,
	 * this will prevent infinite loops and will display to the user what 
	 * has already been derived so far.
	 */
	private int loopCounter;
	
    //Logger logger = Logger.getLogger("jdrew.oo.bu.ForwardReasoner");

	/**
	 * This vector will contain all nodes in the precedence graph.
	 * Its needed to see when we add a new node if the node already exists or
	 * not.
	 */
	private Vector nodes = new Vector();

	/**
	 * This vector will contain all the edges in the precedence graph.
	 * We need the edges so we can test which edges are in cycles later on.
	 */
	private Vector edges = new Vector();
	
	/**
	 * This vector will contain a message for each edge that is negative in a cycle.
	 * This is used to report to the user why stratification failed.
	 */
	private Vector message = new Vector();

	/**
	 * This boolean refers to negative as being true  
	 */
    private static final int negative = -1;
	
	/**
	 * This boolean refers to positive as being false
	 */
	private static final int positive = 1;
	
	/**
	 *  This Directed Graph contains the rules based on a directed graph
	 */
	private DirectedAcyclicGraph dg = new DirectedAcyclicGraph();
	
	private Vector stringsPOSL = new Vector();
	private Vector stringsRULEML = new Vector();
	private boolean flip = false;
	
    /**
     * This method constructs a new ForwardReasoner object (implementation of
     * a bottom-up reasoning engine); creating the required buffers for
     * knowledge base storage (oldFacts and rules Hashtable's and newFacts
     * Vector) and registers the system provided built-in relations with the
     * engine by calling the registerBuiltins();
     */
    public ForwardReasoner() {
        super();
        oldFacts = new Hashtable();
        rules = new Hashtable();
        builtins = new Hashtable();

        newFacts = new Vector();
        registerBuiltins();
    }

    /**
     * Allows user code to access the newFacts vector. Users may want to use
     * this in their code to allow the display of information in the knowledge
     * base.
     *
     * @return Vector A reference to the newFacts Vector for this bottom-up
     * reasoning engine.
     */
    public Vector getNewFacts() {
        return newFacts;
    }

    /**
     * Allows user code to access the oldFacts hash table. Users may want to
     * use this in their code to allow the display of information in the
     * knowledge base.
     *
     * @return Hashtable A reference to the oldFacts Hashtable for this
     * bottom-up reasoning engine.
     */
    public Hashtable getOldFacts() {
        return oldFacts;
    }

    /**
     * Allows user code to access the rules hash table. Users may want to use
     * this in their code to allow the display of information in the knowledge
     * base.
     *
     * @return Hashtable A reference to the oldFacts Hashtable for this
     * bottom-up reasoning engine.
     */
    public Hashtable getRules() {
        return rules;
    }

    /**
     * This method registers all of the standard built-in relations that are
     * included with the OO jDREW system with the created reasoning engine.
     */
    private void registerBuiltins() { 
        this.registerBuiltin(new AssertBuiltin(this));
 
        this.registerBuiltin(new AbsBuiltin());
        
        this.registerBuiltin(new AddBuiltin());
        this.registerBuiltin(new CeilingBuiltin());
        this.registerBuiltin(new ContainsBuiltin());
        this.registerBuiltin(new ContainsIgnoreCaseBuiltin());
        this.registerBuiltin(new CosBuiltin());
        this.registerBuiltin(new DivideBuiltin());
        this.registerBuiltin(new EndsWithBuiltin());
        this.registerBuiltin(new EqualBuiltin());
        this.registerBuiltin(new FloorBuiltin());
        this.registerBuiltin(new GreaterThanBuiltin());
        this.registerBuiltin(new GreaterThanOrEqualBuiltin());
        this.registerBuiltin(new IntegerDivideBuiltin());
        this.registerBuiltin(new LessThanBuiltin());
        this.registerBuiltin(new LessThanOrEqualBuiltin());
        this.registerBuiltin(new ModBuiltin());
        this.registerBuiltin(new MultiplyBuiltin());
        this.registerBuiltin(new NotEqualBuiltin());
        this.registerBuiltin(new PowBuiltin());
        this.registerBuiltin(new RoundBuiltin());
        this.registerBuiltin(new SinBuiltin());
        this.registerBuiltin(new StartsWithBuiltin());
        this.registerBuiltin(new StringConcatBuiltin());
        this.registerBuiltin(new StringEqualIgnoreCaseBuiltin());
        this.registerBuiltin(new StringLengthBuiltin());
        this.registerBuiltin(new StringLowerCaseBuiltin());
        this.registerBuiltin(new StringUpperCaseBuiltin());
        this.registerBuiltin(new SubstringBuiltin());
        this.registerBuiltin(new SubtractBuiltin());
        this.registerBuiltin(new TanBuiltin());
        
        
    }

    /**
     * This method is used to register a new user created built-in with the
     * reasoning engine. This version of the method is for those built-ins that
     * implement the jdrew.oo.builtins.Builtin interface (these can be used
     * with both the bottom-up and the top-down engine).
     *
     * If the built-in that is being created requires access to the data
     * structures of the reasoning engine it should instead extend the
     * jdrew.oo.bu.builtins.BUBuiltin class and be registered with the
     * registerBuiltin(BUBuiltin) method.
     *
     * @param b Builtin An instance of the class that implements the built-in
     * relation; this should implement the jdrew.oo.builtins.Builtin interface.
     */
    
    public void registerBuiltin(Builtin b) {
        registerBuiltin(new BUBuiltin(b));
    }

    /**
     * This method is used to register a new user created built-in with the
     * reasoning engine. This version of the method is for those built-ins that
     * extend the jdrew.oo.bu.builtins.BUBuiltin class (those specific to the
     * bottom-up engine).
     *
     * @param b BUBuiltin An instance of the class that implements the built-in
     * relation; this should be a sub-class of the
     * jdrew.oo.bu.builtins.BUBuiltin class.
     */
    public void registerBuiltin(BUBuiltin b) {
        Integer sym = b.getSymbol();
        builtins.put(sym, b);
    }

    /**
     * This method will return a string that will contain the new facts and old facts
     * in RuleML or POSL form
     */
    public String printClauses(RuleDescriptionLanguage type, RuleMLFormat version) {
    	    	
    	String out = "";
    	
    	int i = 1;
    	Iterator it = newFacts.iterator();
    	//needs to excute multiple times
    	    	
    	if(!flip){
        	flip = true;
		}
           	while (it.hasNext()) {
            	DefiniteClause dc = (DefiniteClause) it.next();
            	            	
            	stringsPOSL.addElement(dc.toPOSLString());
            	stringsRULEML.addElement(dc.toRuleMLString(version));
            	            	            	            	
            	i++;
        	}
			//return(out);

		if(flip){
			
			if (type == RuleDescriptionLanguage.POSL)
			{
				// System.out.println("\n%Old Facts: ");
				out = out + "%Old Facts: \n";
				Iterator iter2 = stringsPOSL.iterator();
				while (iter2.hasNext())
				{
					String out2 = (String) iter2.next();
					// System.out.println(out2);
					out = out + out2 + "\n";
				}
			}
		
			if (type == RuleDescriptionLanguage.RuleML)
			{
				out = out + "%Old Facts: \n";
				Iterator iter2 = stringsRULEML.iterator();
				while (iter2.hasNext())
				{
					String out2 = (String) iter2.next();
					// System.out.println(out2);
					out = out + out2 + "\n";
				}
			}
		
			
		}
	
        //System.out.println("\n%New Facts ");
        out = out + "\n%New Facts: \n";
        Enumeration keys = oldFacts.keys();
        i = 1;
        
        while (keys.hasMoreElements()) {
        	
            Integer key = (Integer) keys.nextElement();
            Vector v = (Vector) oldFacts.get(key);
            it = v.iterator();
            
            if(type == RuleDescriptionLanguage.POSL){
      
            	while (it.hasNext()) {
              	  DefiniteClause dc = (DefiniteClause) it.next();
                
              		Iterator iter = stringsPOSL.iterator();
             	    boolean print = true;
                    while(iter.hasNext()){
              		String test = (String)iter.next();
                		if(test.equals(dc.toPOSLString())){
                			print = false;
                		}
                }
    			
    				if(print){
    					out = out + dc.toPOSLString() + "\n";   
               			//System.out.println(dc.toPOSLString());
                	}
           		}
        	
        	}
        
        	if(type == RuleDescriptionLanguage.RuleML){
        		
        		while (it.hasNext()) {
              	  DefiniteClause dc = (DefiniteClause) it.next();
                
              		Iterator iter = stringsRULEML.iterator();
             	    boolean print = true;
                    while(iter.hasNext()){
              		String test = (String)iter.next();
                		if(test.equals(dc.toRuleMLString(version))){
                			print = false;
                		}
                }
    			
    				if(print){
    					out = out + dc.toRuleMLString(version) + "\n";   
                	}
           		}
         		
        	     		
        	}
        }

    	return out;
    }

    /**
     * This method is used to load clauses into the reasoning engine. This will
     * process each clause as it is loaded - placing new facts into the new
     * facts list and processing new rules against all exisiting processed
     * facts.
     *
     * @param it Iterator An iterator containing the new clauses to be loaded;
     * this should only iterate over DefiniteClause objects. These iterators
     * can be created by calling the iterator() method of parsers - such as
     * RuleMLParser or POSLParser.
     */
    public void loadClauses(Iterator it) {
        while (it.hasNext()) {
            DefiniteClause dc = (DefiniteClause) it.next();                    
            process(dc);
        }
    }

    /**
     * This method runs the main forward reasoner; causing the engine to find
     * all possible conclusions from the knowledge base that was loaded into
     * the engine.
     *
     * Bellow is the main process of the system.
     *
     * As long as there is a new fact in the newFacts list remove the first
     * fact from the list.
     *
     * Check to see if this fact is subsumed by another already processed fact
     * (oldFacts); if it is subsumed, discard the fact and move to the next
     * fact in the list.
     *
     * Add the fact to the old fact table.
     *
     * Find all rules that may be possible unification matches with the newly
     * selected fact. Attempt unification with each possible rule; if the
     * unification succeeds then build the resolvent and call the
     * process(DefiniteClause) method passing the newly created resolvent.
     */
    public void runForwardReasoner() {
		//If the user defined counter reaches this number then we 
		//stop running the reasoner.
        int counter = 0;
        //If the user supplies 0 as the counter then it will just continue to
        //process normally with out a counter
        //But if they define a counter it will stop after that many iterations
        while (newFacts.size() > 0) {
        
            DefiniteClause dc = (DefiniteClause) newFacts.remove(0);
						
            //logger.debug("Processing " + dc.toPOSLString());

            if (this.isSubsumed(dc)) {
                // If this new fact is subsumed by an old fact ignore and go to next new fact
                continue;

            }
                        
            Integer sym = dc.atoms[0].getSymbol();

            if (oldFacts.containsKey(sym)) { // add new fact to oldFact "list"
                Vector v = (Vector) oldFacts.get(sym);
                v.add(dc);
            } else {
                Vector v = new Vector();
                v.add(dc);
                oldFacts.put(sym, v);
            } // end adding new fact to oldFact "List"

            ArrayList al = new ArrayList();

            if (rules.containsKey(sym)) {
                // Rules with the same relation symbol - possible unifications
                Vector v = (Vector) rules.get(sym);
                Iterator it = v.iterator();
                while (it.hasNext()) {
                    // go though all possible unifications, and try to unify
                    DefiniteClause rule = (DefiniteClause) it.next();
                                                       
                    //logger.debug("Unifying with rule " + rule.toPOSLString());
                                       
                    Unifier u = new Unifier(dc, rule);
                    
                    if (u.unifies()) {
                        // new fact unifies with a rule - process the resolvent
                        DefiniteClause r = u.resolvent();
                        //logger.debug("Unified - resolvent: " + r.toPOSLString());
                        al.add(r);
                    }
                }

                it = al.iterator();
                while(it.hasNext()){
                        process((DefiniteClause)it.next());
            	}
        	}
    	    //incrementing the iteration counter
    	   	counter++;
        	if(counter == loopCounter){
        		break;
        	}
    	}
	}
    /**
     * This method is used to determine if a newly selected fact is subsumed
     * by another fact that has already been processed by the system.
     *
     * @param fact DefiniteClause This is the newly selected fact to perform
     * the subsumption check on.
     *
     * @return boolean Returns true if the fact is subsumed by another
     * processed fact; false otherwise. If this returns true (is subsumed) then
     * the input fact should be discarded as there is another already processed
     * fact which is more general than the newly selected fact.
     */
    private boolean isSubsumed(DefiniteClause fact) {
        Subsumption s = new Subsumption(fact);
        Integer sym = fact.atoms[0].getSymbol();
        if (oldFacts.containsKey(sym)) {
            Vector v = (Vector) oldFacts.get(sym);
            Iterator it = v.iterator();
            while (it.hasNext()) {
                DefiniteClause dc = (DefiniteClause) it.next();
                if (s.subsumedBy(dc)) {
                    //logger.info(fact.toPOSLString() + " is subsumed by " +
                     //           dc.toPOSLString());
                    return true;
                }

            }
        }

        return false;
    }

    /**
     * This method is used to an iterator over all clauses that may unify with
     * the specified atom in the given clause.
     *
     * The first step in the process is to retrieve the atom that is to be
     * considered for unification and access the predicate symbol.
     *
     * Once the predicate symbol is retrieved the system checks to see if there
     * is a built-in relation registered for that predicate symbol; if there is
     * the the clause and atom index are passed to the built-in implementation
     * for it to generate the appropriate response.
     *
     * If there is no built-in registered for that predicate the system will
     * retrieve any processed facts with that predicate symbol; these will be
     * returned (as an iterator) to check if they unify with the clause.
     *
     * @param dc2 DefiniteClause The clause to find possible unifying facts for.
     *
     * @param term int The index to the atom to search on (0 is the head, 1 to
     * n-1 are the atoms of the body); typically this is always a 1.
     *
     * @return Iterator An iterator over all clauses that may unify with the
     * specified atom of the passed clause.
     */
    private Iterator getUnifiableIterator(DefiniteClause dc2, int term) {
        Term t = dc2.atoms[term];        
        Integer sym = t.getSymbol();
        if (builtins.containsKey(sym)) {
            BUBuiltin b = (BUBuiltin) builtins.get(sym);
            Vector v = b.buildResult(dc2, term);
            return v.iterator();
        } else if (oldFacts.containsKey(sym)) {
            Vector ofs = (Vector) oldFacts.get(sym);
            return ofs.iterator();
        } else {
            // Not a built-in and no facts to consider for unification
            Vector v = new Vector();
            return v.iterator();
        }
    }

    /**
     * This method is used to process new clauses - either when loading them
     * or when a new rule is generated in a resolution step.
     *
     * If the new clause is a fact it is simply added to the new facts list.
     *
     * If the new clause is a rule then it is processed against all previously
     * used facts (oldFacts). This processing is done in two stages: first all
     * oldFacts that can possibly unify with the new rule are selected from the
     * oldFacts table; then unification is attempted with each of those facts
     * and if successful the resolvent is added to the newResults list. Once
     * all of the old facts have been tried then the system will iterate through
     * the newResults list and process each of the new resolvents by calling
     * the process(DefiniteClause) method recursively.
     *
     * @param dc DefiniteClause This should be the new clause to process; this
     * can either be a clause as it is loaded; or a newly generated resolvent in
     * the process(DefiniteClause) method or the runForwardReasoner() method.
     */
    private void process(DefiniteClause dc) {

        if (dc.atoms.length == 1) {
        	
        	//check to see at this point if dc still contains data
        	
            newFacts.add(dc);
        } else {
            //logger.debug("Processing " + dc.toPOSLString());

            Vector newResults = new Vector();

            Integer sym = dc.atoms[1].getSymbol();
            Iterator ofsit = getUnifiableIterator(dc, 1);
           
            while (ofsit.hasNext()) {
                DefiniteClause oldfact = (DefiniteClause) ofsit.next();  	
           		
                Unifier u = new Unifier(oldfact, dc);
                //logger.debug("Unifying new rule " + dc.toPOSLString() +
                //             " with old fact " + oldfact.toPOSLString());
                if (u.unifies()) {
                    DefiniteClause nr = u.resolvent();
                    newResults.add(nr);
                   // logger.debug("Unified - resolvent: " + nr.toPOSLString());
                }
            }

            Iterator nrit = newResults.iterator();
            while (nrit.hasNext()) {
                DefiniteClause dc2 = (DefiniteClause) nrit.next();
                process(dc2);
            }

            if (rules.containsKey(sym)) {
                Vector v = (Vector) rules.get(sym);
                v.add(dc);
            } else {
                Vector v = new Vector();
                v.add(dc);
                rules.put(sym, v);
            }
        }
    }
		
	/**
	 * This method builds a precedence graph based on the given rules.
	 *
	 * For each rule we must find the head of the rule and then check
	 * if it exists or not.  If it exists we use that node as the sink for
	 * each body term.  If it doesn't exists we make a new node object with
	 * the term object and it is used for the sink of each body term.  
	 * We test if a term exists or not by unifying the head of the rule 
	 * with all the other terms that we seen already.  If we create a node
	 * we have to store it in the node vector, that is used to compare if a
	 * node exists or not.
	 *
	 * Then for each body clause of a rule we check if the term already 
	 * exists or not. If it doesn't we create a new node with the term object
	 * for the source. If it does exist we just use that node as the source.
	 * If we create a node we have to store it in the node vector, that is used
	 * to compare if a node exists or not.
	 *
	 * After we get a source and a sink node we can then create a edge.  
	 * The edges are what define the graph.  For every head of a rule and 
	 * each body clause there will be an edge created.
	 *
	 * See the source for a more detailed comment.
	 */
		
	public void buildPrecedenceGraph(){

		Enumeration e = rules.elements();
		
		//this while loop loops through each rule
		while(e.hasMoreElements()){	
			//we are getting the rule from the iterator
			Vector rule = (Vector) e.nextElement();
			Iterator it = rule.iterator();
			
			//we are now going through each term in the rule			
			while(it.hasNext()){
								
				DefiniteClause dc = (DefiniteClause) it.next();
		  		Term head = dc.atoms[0];
		  		Node sink = null; //this will be the sink for each edge in this rule
		  		boolean headExists = false; //check to see if the head already exists
		  		
		  		//check to see if the head exist
		  		Iterator nodeIterator = nodes.iterator();
		  		//looping through all previous terms
		  		while(nodeIterator.hasNext()){
		  			Node n1 = (Node)nodeIterator.next();
		  			Term compare = (Term)n1.getWeight();
		  				//if the head already exists want to use that node as
		  				//the sink
		  				//need to make a unifier to test to see if 2 terms are equal
		  			Unifier u = new Unifier();
		  			if(u.unify(head,compare)){
		  				sink = n1;
		  				headExists = true;
		  				break;
		  			}	
		  		}	  		
		  		//making a new node since it didnt exist
		  		if(!headExists){
		  			sink = new Node(head);	
		  			dg.addNode(sink);//adding the new node to the graph
		  			nodes.addElement(sink);//adding the new node to the vector
		  		}	
		  		//going through each body clause	
				for(int j = 1; j < dc.atoms.length; j++){

			    	Term body = dc.atoms[j];
			        boolean hasNegEdge = false;
			        boolean bodyExists = false;
			        //if it does the edges will be made differntly
			        boolean containsANestedNaf = false;
			        Node source = null;
			        //if the atom is a naf atom
			        //System.out.println("body symbol: " + body.getSymbolString());
					if(body.getSymbolString().equals("naf")){
					//	System.out.println("negative edge found");
						hasNegEdge = true;
						//removing the naf part of the atom
						Term[] nafAtoms = dc.atoms[j].getSubTerms();
						body= nafAtoms[0];
						//checking to see if there was a nested naf					
						if(nafAtoms.length != 1){
							containsANestedNaf = true;
							break;//the break prevents crashes
							//May not need to deal with this at all
							//we can process the for loop here
							//for multiple nested nafs
						}							
					}
					//only excute this code if there isnt a nested naf
					if(!containsANestedNaf){
					
						Iterator nodeIterator2 = nodes.iterator();
									
						while(nodeIterator2.hasNext()){
							Node n2 = (Node)nodeIterator2.next();
							Term compare2 = (Term)n2.getWeight();
							Unifier u2 = new Unifier();
							if(u2.unify(body,compare2)){
								source = n2;
								bodyExists = true;
								break;
							}	
						}
					
						if(!bodyExists){
							source = new Node(body);
							dg.addNode(source);
							nodes.addElement(source);
						}
						
					} 
					//creating the edge with the source, sink and if it
					//is negative clause or not
					Edge e1 = null;
					Object b = -1;
					Object c = 1;
					if(hasNegEdge){
					e1 = new Edge(source,sink,b);
					}
					if(!hasNegEdge){
					e1 = new Edge(source,sink,c);	
					}
					dg.addEdge(e1);
					edges.addElement(e1);
				}//each body clause	
			}//each term
		}//each rule	
		
		//****JUST USED for testing purposes to print out the graph*****
		//System.out.println("Graph built The edges are: \n");
		Iterator test = edges.iterator();
		while(test.hasNext()){
			Edge edgeTest = (Edge)test.next();
			//System.out.println("Edge");
			Node n1 = (Node)edgeTest.source();
			Node n2 = (Node)edgeTest.sink();
			Term t1 = (Term)n1.getWeight();
			Term t2 = (Term)n2.getWeight();
			//System.out.println("Source: " +  t1.toPOSLString(true));
			//System.out.println("Sink:   " +  t2.toPOSLString(true));
			//System.out.println("Is the edge negative: " + (Integer)edgeTest.getWeight() + "\n");
		}
		//****Just used for testing purposes to print out the graph*****
	}
	/**
	 * This method is used to see if there is a negative edge in a cycle
	 * in the precedence graph.  What it does is check every edge in the
	 * predence graph and test if it is within a cycle and if it is then
	 * we test if the edge is negative.  If we find a negative edge in a cycle
	 * its all we need to know that a knowledge base is not stratfiable.
	 *
	 * We loop through each edge and check if its source and sink are nodes in
	 * cycles, which ptolemy can detect.  So we loop through all possible
	 * combinations of cycle nodes and see if the edge exists or not
	 *
	 * It also populates a string vector containing the reasons why
	 * stratification fails.
	 *
	 * @return boolean - true if a negative edge exists, false otherwise
	 */
	 
	public boolean detectNegativeCycle(){
		boolean neg = false;
		//getting the nodes in cycles
		Collection col = dg.cycleNodeCollection();
		Vector cycleNodes = new Vector();
		cycleNodes.addAll(col);
		//System.out.println("Number of nodes in cycles: " + cycleNodes.size());
		//System.out.println("The Nodes are \n");
		
		//if there is are no cycles then we know the knowledge base is stratifiable
	//	if(cycleNodes.size() == 0){
		//	return false;
	//	}
		//****TEST****
		for(int p = 0; p < cycleNodes.size(); p++){
			Node tNode = (Node)cycleNodes.elementAt(p);
			Term tTerm = (Term)tNode.getWeight();
			//System.out.println(tTerm.toPOSLString(true));
		}
		//System.out.println();
		//****TEST****
		//must iterator through the edges to see if a cycle
		//contains a negative edge
		Iterator edgeIterator = edges.iterator();
		//going through each edge in the graph to see if its in a cycle
		//if it is then we check if its negative
		while(edgeIterator.hasNext()){
			
			Edge edge = (Edge)edgeIterator.next();
			//if the source and the sink are in the cycleNodes
			//then we know the edge is in the cylce
			Term source	= (Term)((Node)edge.source()).getWeight();
			Term sink = (Term)((Node)edge.sink()).getWeight();
			int numberOfNodes = cycleNodes.size();
			
			for(int i =0; i < numberOfNodes; i++){

				Node n1 = (Node)cycleNodes.elementAt(i);
				Term t1 = (Term)n1.getWeight();
				
				for(int j = 0; j < numberOfNodes;j++){
					
					Node n2 = (Node)cycleNodes.elementAt(j);
					Term t2 = (Term)n2.getWeight();
					Unifier u = new Unifier();
									
					if(u.unify(source,t1) && (u.unify(sink,t2))){
						
						String ugly = "" + edge.getWeight();
						int b = Integer.parseInt(ugly);
												
						if(b == negative){
							
						//System.out.println("Negative edge found in cycle");
						Node n11 = (Node)edge.source();
						Node n22 = (Node)edge.sink();
						Term t11 = (Term)n11.getWeight();
						Term t22 = (Term)n22.getWeight();
						//System.out.println("Source: " +  t11.toPOSLString(true));
						//System.out.println("Sink:   " +  t22.toPOSLString(true));
						//System.out.println("Is the edge negative: " + (Integer)edge.getWeight() + "\n");
						//adding the reasons why stratification fail to a vector
						
						String msg = " The rule with a head atom '" + t22.toPOSLString(true) +
						"'\n cannot have a Naf in a body with an atom containing relation '" + t11.getSymbolString() +"'";
						message.addElement(msg);
						
						//return true;
						neg = true;
						}
					}	
				}
				
			}		
		}
		return neg;	
		//return false;
	}
	/**
	 * This method is used to see if a Knowledge base is statfiable or not.
	 * It first checks if the rules contain a naf or not.  If no rule contains
	 * a naf then its stratfiable. If there is a naf we have to create a precedence
	 * graph and test if there is a negative cycle or not.
	 *
	 * @return boolean - true if the knowledge base is stratfiable, false otherwise
	 */
	public boolean isStratifiable(){
		//checks to see if the rules contain a naf
		if(!this.rulesContainsNaf()){
			return true;
		}	
		//build the precedent graph
		this.buildPrecedenceGraph();
		//if we detect a negative cycle that means that its not stratfiable
		//if we do not detect a negative cylce that means it is stratfiable
		return !this.detectNegativeCycle();
	}

	/**
	 * This method is used to see if the body of any rule contains a naf
	 * to see whether or not we need to check for stratification.  It goes
	 * through each rule to see if a term contains a naf or not.  If no rules
	 * contain a naf then we know its stratifiable and there is no point in
	 * building a precedence graph and checking for negative cycles.
	 *
	 * @return boolean - true if a rule contians naf, false otherwise
	 */

	public boolean rulesContainsNaf(){

    	Enumeration e = rules.elements();
    	
		while(e.hasMoreElements()){
			
			Vector rule = (Vector) e.nextElement();
			Iterator it = rule.iterator();
						
			while(it.hasNext()){
				
				DefiniteClause dc = (DefiniteClause) it.next();
		  	
				for(int j = 0; j < dc.atoms.length; j++){
				    	
			    	Term t1 = dc.atoms[j];
			        				
					if(t1.getSymbolString().equals("naf")){
						return true;
					}
				}
			
			}					
		}
		return false;
	}


	/**
     * This method is used to set the number of times when
     * the forward reasoner should stop running.
     * 
     * @param String - the number of times a rule should loop
     * 
     */
	public void setLoopCounter(String number){
		
		int num = Integer.parseInt(number);
		loopCounter = num;
		
	}
	/**
     * This method is used to set the number of times when
     * the forward reasoner should stop running.
     * 
     * @param int - the number of times a rule should loop
     * 
     */
	public void setLoopCounter(int number){
		
		loopCounter = number;
		
	}
	/**
     * This method is used to get the number of times when
     * the forward reasoner should stop running.
     *
     * @return int - the number of times a rule should loop
     * 
     */
	public int getLoopCounter(){
		
		return loopCounter;
		
	}
	/**
     * This method returns a vector of strings containing information
     * about stratification.
     *
     * @return vector - a vector of strings containing why stratification fails
     * 
     */
	public Vector getMessage(){
		return message;
	}

}
