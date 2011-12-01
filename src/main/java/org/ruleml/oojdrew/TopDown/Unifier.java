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

package org.ruleml.oojdrew.TopDown;

/**
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

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.ruleml.oojdrew.parsing.RuleMLParser.RuleMLFormat;
import org.ruleml.oojdrew.util.EngineException;
import org.ruleml.oojdrew.util.SymbolTable;
import org.ruleml.oojdrew.util.Term;
import org.ruleml.oojdrew.util.Types;

/**
 * A <code>Unifier</code> is an object that performs two separate functions: it
 * tells whether two clauses literals that occur in Goals/GoalLists clauses can
 * unify, and it builds the resulting Goals/GoalLists after the required
 * substitution, required for the unification, have been applied.
 *
 * In this case a DCTree.Goal and DCTree.GoalList are always used
 *
 * @author  Marcel A. Ball
 */
public class Unifier {

    /**
     * This is use as a index for the goalLists, atoms, vars.
     */
    static final int GOAL = 0;
   
    /**
     * This is use as a index for the goalLists, atoms, vars.
     */
    static final int SUBGOALLIST = 1;

    /**
     * This variable stores references to the two goalLists that are
     * being considered;
     */
    BackwardReasoner.GoalList[] goalLists;

    /**
     * An it for the mode.
     */
    static final int DCTREE_MODE = 0;


    
    static final int POSL = 1;
    static final int RuleML = 2;
    
    /**
     *  Switch this variable between 1 and 2,
     *  1 = POSL Bindings
     *  2 = RuleML Bindings
     */
    
    int varBindTypes = POSL;
    
    /**
     * This variable stores a copy of the atoms of the two clauses.
     */
    
    Term[][] atoms = null;
    /**
     *This variable is used to temporarly store variable bindings.
     */
   
    Term[][] vars = null;
    /**
     * This array contains the variable names.
     */
    String[] varNames;
    
    /**
     * Keeps track of the amount of Variables
     */
    int variableSize;
   
    /**
     *  Keeps track of the amount of variables.
     */
    int variableCount;

    /**
     * Hastable for the variable bindings.
     */
    Hashtable[] bindings;

    /**
     *  The subGoalList that will be used by the Unifier, value of the current subGoals
     */
    BackwardReasoner.GoalList subGoalList;

    /**
     *  The goal that will be used by the Unifier, value of the current goal.
     */
    BackwardReasoner.Goal goal;

    /**
     *  This value is set to true if unification of the Goal and the first Goal
     *  in the subGoalList is successful, false otherwise.
     *  It should always be checked to see if it is set to true before calling
     *  the applyToGoal() or applyToGoalList() methods, as they will throw a
     *  RuntimeException if unification was not successful.
     */
    public boolean unified = false;

    /**
     * This value is should be set to true if the unification is taking place when
     * travelling down the tree, and false when returning back up. This indication
     * is necessary to have proper type unification.
     */
    public boolean down = false;

    /**
     * An int for the mode
     */
    int mode;

    /**
     * This method will make a deep copy duplicate of the term.
     *
     * @param terms Term[] the term to be duplicated.
     * @param side int used by the unification process to check what clause a
     * term belongs to when doing unification.
     * @return Term[] the duplicated term.
     */
    private static Term[] cloneTermArray(Term[] terms, int side) {
        Term[] nterms = new Term[terms.length];
        for (int i = 0; i < nterms.length; i++) {
            nterms[i] = (Term) terms[i].deepCopy(side);
        }
        return nterms;
    }

    /**
     * Constructs a new Unifier for the OO-jDREW top-down module.
     *
     * @param goal <code>jDREW.oo.td.DCTree.Goal value</code> - a DCTree.Goal
     * value of the current goal
     *
     * @param subGoalList <code>jDREW.oo.td.DCTree.GoalList value</code> - a
     * DCTree.GoalList value of the current subGoals
     *
     * @param mode <code>int value</code> - an int for the mode - this should be
     * Unifier.DCTREE_MODE
     *
     * @param down <code>boolean value</code> - Tells the unifier if this is going
     * up or down the tree, use for applying type bindings correctly.
     */

    public Unifier(BackwardReasoner.Goal goal,
                   BackwardReasoner.GoalList subGoalList, int mode,
                   boolean down) {
        this.mode = mode;
        this.down = down;

        if (mode == DCTREE_MODE) {
            this.goal = goal;
            goal.setSymbolIndex();
            this.subGoalList = subGoalList;
            atoms = new Term[2][];
            atoms[GOAL] = cloneTermArray(goal.goalList.atoms, GOAL);
            atoms[SUBGOALLIST] = cloneTermArray(subGoalList.atoms, SUBGOALLIST);
            goalLists = new BackwardReasoner.GoalList[2];
            goalLists[GOAL] = goal.goalList;
            goalLists[SUBGOALLIST] = subGoalList;
            vars = new Term[2][];
            vars[GOAL] = new Term[goal.goalList.varCount];
            vars[SUBGOALLIST] = new Term[subGoalList.varCount];
            this.variableSize = goal.goalList.varCount + subGoalList.varCount;
            this.variableCount = 0;
            this.varNames = new String[variableSize];
            this.bindings = new Hashtable[2];
            this.bindings[GOAL] = new Hashtable();
            this.bindings[SUBGOALLIST] = new Hashtable();
            unified = unify(atoms[GOAL][goal.symbolIndex], atoms[SUBGOALLIST][0], varBindTypes);

        }

    }

    /**
     * Constructs a new Unifier for the OO-jDREW top-down module. This is the same
     * as calling the Unifier(DCTree.Goal goal, DCTree.GoalList subGoalList,
     * int mode, boolean down) constructor with down set to false.
     *
     * @param goal <code>jDREW.oo.td.DCTree.Goal value</code> - a DCTree.Goal
     * value of the current goal
     *
     * @param subGoalList <code>jDREW.oo.td.DCTree.GoalList value</code> - a
     * DCTree.GoalList value of the current subGoals
     *
     * @param mode <code>int value</code> - an int for the mode - this should be
     * Unifier.DCTREE_MODE
     */

    public Unifier(BackwardReasoner.Goal goal,
                   BackwardReasoner.GoalList subGoalList, int mode) {
        this.mode = mode;

        if (mode == DCTREE_MODE) {
            this.goal = goal;
            goal.setSymbolIndex();
            this.subGoalList = subGoalList;
            atoms = new Term[2][];
            atoms[GOAL] = cloneTermArray(goal.goalList.atoms, GOAL);
            atoms[SUBGOALLIST] = cloneTermArray(subGoalList.atoms, SUBGOALLIST);
            goalLists = new BackwardReasoner.GoalList[2];
            goalLists[GOAL] = goal.goalList;
            goalLists[SUBGOALLIST] = subGoalList;
            vars = new Term[2][];
            vars[GOAL] = new Term[goal.goalList.varCount];
            vars[SUBGOALLIST] = new Term[subGoalList.varCount];
            this.variableSize = goal.goalList.varCount + subGoalList.varCount;
            this.variableCount = 0;
            this.varNames = new String[variableSize];
            this.bindings = new Hashtable[2];
            this.bindings[GOAL] = new Hashtable();
            this.bindings[SUBGOALLIST] = new Hashtable();

            unified = unify(atoms[GOAL][goal.symbolIndex], atoms[SUBGOALLIST][0],varBindTypes );
        }
    }


    /**
     * This method is used to check if two terms unify with each other; and to
     * perform any variable bindings that are necessary to make the terms unfiy.
     *
     * For more details see the inline comments in the source of this method
     * and/or look at the description of the unify(Term, Term) method of the
     * jdrew.oo.bu.Unifier class.
     *
     * @param term1 Term One of the terms to attempt to unify.
     *
     * @param term2 Term The second term to attempt to unify.
     * 
     * @param varBindTypes 1 = POSL 2 = RuleML
     *
     * @return boolean Returns true if the two terms unify; false otherwise.
     */
    private boolean unify(Term term1, Term term2, int varBindType) {
        Term t1 = deref(term1);
        Term t2 = deref(term2);

        Logger logger = Logger.getLogger("jdrew.oo.td.Unifier");
        //logger.debug(t1.toPOSLString(false));
        //logger.debug(t2.toPOSLString(false));

        if (t1.isExpr() && t2.isExpr()) {
            if (t1.getSymbol() == t2.getSymbol() &&
                Types.isSuperClass(t2.getType(), t1.getType())) {
                Vector t1restterms = new Vector();
                Vector t2restterms = new Vector();
                Vector t1prestterms = new Vector();
                Vector t2prestterms = new Vector();

                boolean t1rest = (t1.rest >= 0);
                boolean t2rest = (t2.rest >= 0);
                boolean t1prest = (t1.prest >= 0);
                boolean t2prest = (t2.prest >= 0);

                int i = 0;
                int j = 0;

                while (i < t1.subTerms.length && j < t2.subTerms.length) {
                    if (t1.subTerms[i].role == SymbolTable.IREST ||
                        t1.subTerms[i].role == SymbolTable.IPREST) {
                        // This is a rest term in t1 - skip for now - this is handeled at the end of unification

                        i++;
                        continue;
                    }

                    if (t2.subTerms[j].role == SymbolTable.IREST ||
                        t2.subTerms[j].role == SymbolTable.IPREST) {
                        // This is a rest term in t2 - skip for now - this is handeled at the end of unification
                        j++;
                        continue;
                    }

                    if (t1.subTerms[i].role < t2.subTerms[j].role) {
                        // role(t1[i]) is before role(t2[j]) - go to next i or fail
                        if (t1.subTerms[i].role == SymbolTable.INOROLE &&
                            t2prest) {
                            // add to positional rest term list for t2
                            t2prestterms.add(t1.subTerms[i]);
                            i++;
                        } else if (t1.subTerms[i].role > SymbolTable.INOROLE &&
                                   t2rest) {
                            // add to slotted rest term list for t2
                            t2restterms.add(t1.subTerms[i]);
                            i++;
                        } else {
                            return false; // no appropriate rest term in t2 - unification fails
                        }
                    } else if (t1.subTerms[i].role == t2.subTerms[j].role) {
                        // role(t1[i]) is same as role(t2[j]) - unify t1[i] and t2[j]
                        if (!unify(t1.subTerms[i], t2.subTerms[j], varBindTypes )) {
                            return false;
                        }
                        i++;
                        j++;
                    } else if (t1.subTerms[i].role > t2.subTerms[j].role) {
                        // role(t1[i]) is after role(t2[j]) - go to next j
                        if (t2.subTerms[j].role == SymbolTable.INOROLE &&
                            t1prest) {
                            // add to positional rest term list for t1
                            t1prestterms.add(t2.subTerms[j]);
                            j++;
                        } else if (t2.subTerms[j].role > SymbolTable.INOROLE &&
                                   t1rest) {
                            // add to slotted rest term list for t1
                            t1restterms.add(t2.subTerms[j]);
                            j++;
                        } else {
                            return false; // no appropriate rest term in t1 - unification fails
                        }
                    }
                }

                while (i < t1.subTerms.length) {
                    if (t1.subTerms[i].role == SymbolTable.IREST ||
                        t1.subTerms[i].role == SymbolTable.IPREST) {
                        // This is a rest term in t1 - skip for now - this is handeled at the end of unification
                        i++;
                    } else if (t1.subTerms[i].role == SymbolTable.INOROLE &&
                               t2prest) {
                        t2prestterms.add(t1.subTerms[i]);
                        i++;
                    } else if (t1.subTerms[i].role > SymbolTable.INOROLE &&
                               t2rest) {
                        t2restterms.add(t1.subTerms[i]);
                        i++;
                    } else {
                        return false; // no appropriate rest term in t2 - unification fails
                    }
                }

                while (j < t2.subTerms.length) {
                    if (t2.subTerms[j].role == SymbolTable.IREST ||
                        t2.subTerms[j].role == SymbolTable.IPREST) {
                        // This is a rest term in t1 - skip for now - this is handeled at the end of unification
                        j++;
                    } else if (t2.subTerms[j].role == SymbolTable.INOROLE &&
                               t1prest) {
                        t1prestterms.add(t2.subTerms[j]);
                        j++;
                    } else if (t2.subTerms[j].role > SymbolTable.INOROLE &&
                               t1rest) {
                        t1restterms.add(t2.subTerms[j]);
                        j++;
                    } else {
                        return false; // no appropriate rest term in t2 - unification fails
                    }
                }

                // Now do unification of rest term with rest term list that was created
                Term t1prestterm = new Term(SymbolTable.IPLEX,
                                            SymbolTable.IPREST, Types.IOBJECT,
                                            t1prestterms);
                t1prestterm.setSide(t2.getSide());
                Term t1restterm = new Term(SymbolTable.IPLEX,
                                           SymbolTable.IREST, Types.IOBJECT,
                                           t1restterms);
                t1restterm.setSide(t2.getSide());
                Term t2prestterm = new Term(SymbolTable.IPLEX,
                                            SymbolTable.IPREST, Types.IOBJECT,
                                            t2prestterms);
                t2prestterm.setSide(t1.getSide());
                Term t2restterm = new Term(SymbolTable.IPLEX,
                                           SymbolTable.IREST, Types.IOBJECT,
                                           t2restterms);
                t2restterm.setSide(t1.getSide());

                if (t1prest) {
                    if (!unify(t1.subTerms[t1.prest], t1prestterm, varBindTypes)) {
                        return false;
                    }
                } else {
                    if (t1prestterms.size() > 0) {
                        return false; // t1 has no positional rest term, but one is required for successful unification
                    }
                }

                if (t1rest) {
                    if (!unify(t1.subTerms[t1.rest], t1restterm,varBindTypes)) {
                        return false;
                    }
                } else {
                    if (t1restterms.size() > 0) {
                        return false; // t1 has no slotted rest term, but one is required for successful unification
                    }
                }

                if (t2prest) {
                    if (!unify(t2.subTerms[t2.prest], t2prestterm, varBindTypes)) {
                        return false;
                    }
                } else {
                    if (t2prestterms.size() > 0) {
                        return false; // t2 has no positional rest term, but one is required for successful unification
                    }
                }

                if (t2rest) {
                    if (!unify(t2.subTerms[t2.rest], t2restterm, varBindTypes )) {
                        return false;
                    }
                } else {
                    if (t2restterms.size() > 0) {
                        return false; // t2 has no slotted rest term, but one is required for successful unification
                    }
                }

                return true; // All subterms unified correctly, symbols and types are compatible, therefore t1 and t2 unify

            }

            else {
                return false; // Symbols were different or types were not compatible (! (type(t2) >= type(t1)))
            }
        } else if (t1.isExpr() && !t2.isExpr()) {
            if (t2.getSymbol() < 0 &&
                Types.isSuperClass(t2.getType(), t1.getType())) {
                // t2 is a variable, t1 is a complex term (Cterm, Plex, Atom)
                int side = t2.getSide();
                int sym = -(t2.getSymbol() + 1);
                this.vars[side][sym] = t1;

                if (side == GOAL) {
                    String t2str = "?" + this.goal.goalList.variableNames[sym];
                    if (t2.type != Types.IOBJECT) {
                        t2str += " : " + Types.typeName(t2.type);
                    }

                    if (!t2str.startsWith("?$ANON")) {

                        if (t1.getSide() == GOAL) {
                        //	System.out.println("1");
                        	if(varBindType == POSL){
                        	
                            this.goal.goalList.varBindings.put(t2str,
                                    t1.toPOSLString(this.goal.goalList.
                                    variableNames, true));
                            }
                            
                            if(varBindType == RuleML){
                                this.goal.goalList.varBindings.put(t2str,
                                        t1.toRuleMLString(this.goal.goalList.
                                        variableNames, RuleMLFormat.RuleML91));
                            }
                            
                        } else {
                        	//System.out.println("2");
                        	if(varBindType == POSL){
                        		
                            this.goal.goalList.varBindings.put(t2str,
                                    t1.toPOSLString(this.subGoalList.
                                    variableNames, true));
                            }
                            
                            if(varBindType == RuleML){
                                this.goal.goalList.varBindings.put(t2str,
                                        t1.toRuleMLString(this.subGoalList.
                                        variableNames, RuleMLFormat.RuleML91));                           	
                            }                            
                        }
                    }
                } else {
                    String t2str = "?" + this.subGoalList.variableNames[sym];
                    if (t2.type != Types.IOBJECT) {
                        t2str += " : " + Types.typeName(t2.type);
                    }

                    if (!t2str.startsWith("?$ANON")) {

                        if (t1.getSide() == GOAL) {
                        	//System.out.println("3");
                        	if(varBindType == POSL){
                        	
                            this.subGoalList.varBindings.put(t2str,
                                    t1.toPOSLString(this.goal.goalList.
                                    variableNames, true));
                            }
                            
                            if(varBindType == RuleML){
                                this.subGoalList.varBindings.put(t2str,
                                        t1.toRuleMLString(this.goal.goalList.
                                        variableNames, RuleMLFormat.RuleML91));                           	
                            }                           
                            
                        } else {
                        //	System.out.println("4");
                        	if(varBindType == POSL){
                        	 
                            this.subGoalList.varBindings.put(t2str,
                                    t1.toPOSLString(this.subGoalList.
                                    variableNames, true));
                            
                            }                           
                            if(varBindType == RuleML){
                                this.subGoalList.varBindings.put(t2str,
                                        t1.toRuleMLString(this.subGoalList.
                                        variableNames, RuleMLFormat.RuleML91));
                            }                            
                            
                        }
                    }
                }

                return true;
            } else {
                // t2 is an individual constant (Ind) and t2 is a complex term (Cterm, Plex, Atom)
                return false;
            }
        } else if (!t1.isExpr() && t2.isExpr()) {
            if (t1.getSymbol() < 0 &&
                Types.isSuperClass(t1.getType(), t2.getType())) {
                // t1 is a variable, t2 is a complex term (Cterm, Plex, Atom)
                int side = t1.getSide();
                int sym = -(t1.getSymbol() + 1);
                this.vars[side][sym] = t2;

                if (side == GOAL) {
                    String t1str = "?" + this.goal.goalList.variableNames[sym];
                    if (t1.type != Types.IOBJECT) {
                        t1str += " : " + Types.typeName(t1.type);
                    }

                    if (!t1str.startsWith("?$ANON")) {

                        if (t2.getSide() == GOAL) {
                        	//System.out.println("5");
                        	if(varBindType == POSL){
                        		
                        			this.goal.goalList.varBindings.put(t1str,
                                    t2.toPOSLString(this.goal.goalList.
                                    variableNames, true));
                            }
                            
                            if(varBindType == RuleML){
                                this.goal.goalList.varBindings.put(t1str,
                                        t2.toRuleMLString(this.goal.goalList.
                                        variableNames, RuleMLFormat.RuleML91));
                            } 
                            
                            
                        } else {
                        	//System.out.println("6");
                        	if(varBindType == POSL){
                        		
                        		String posl = t2.toPOSLString(this.subGoalList.variableNames, true);                        		                            		  
                        		                        		
                        		if(posl.contains("->")){
                        			posl = posl.substring(posl.indexOf("->") + 2);
                        		}
                        		
                            this.goal.goalList.varBindings.put(t1str,
                            		posl);
                        	}
                        	if(varBindType == RuleML){
                        		
                        		String ruleML = t2.toRuleMLString(this.subGoalList.variableNames, RuleMLFormat.RuleML91);
                        		int firstIndex = ruleML.indexOf("</Ind>");
                        		int secondIndex = ruleML.indexOf("</slot>");
                        		
                        		if(firstIndex != -1 && secondIndex != -1)
                        			ruleML = ruleML.substring(firstIndex+6, secondIndex);
                        		
                        		//System.out.println(ruleML);
                        		
                        		this.goal.goalList.varBindings.put(t1str,
                        				ruleML);
                            } 
                        }
                    }
                } else {
                    String t1str = "?" + this.subGoalList.variableNames[sym];
                    if (t1.type != Types.IOBJECT) {
                        t1str += " : " + Types.typeName(t1.type);
                    }

                    if (!t1str.startsWith("?$ANON")) {

                        if (t2.getSide() == GOAL) {
                        	//System.out.println("7");
                        	if(varBindType == POSL){
                        	
                            this.subGoalList.varBindings.put(t1str,
                                    t2.toPOSLString(this.goal.goalList.
                                    variableNames, true));
                        	}
                        	if(varBindType == RuleML){
                                this.subGoalList.varBindings.put(t1str,
                                        t2.toRuleMLString(this.goal.goalList.
                                        variableNames, RuleMLFormat.RuleML91));                        		
                        	}
                            
                        } else {
                        	//System.out.println("8");
                        	if(varBindType == POSL){
                        		
                        		this.subGoalList.varBindings.put(t1str,
                                    t2.toPOSLString(this.subGoalList.
                                    variableNames, true));
                        	}
                        	if(varBindType == RuleML){
                        		this.subGoalList.varBindings.put(t1str,
                                        t2.toRuleMLString(this.subGoalList.
                                        variableNames, RuleMLFormat.RuleML91));                       		
                        	}
                        }
                    }
                }
                return true;
            } else {
                // t1 is an individual constant (Ind) and t2 is a complex term (Cterm, Plex, Atom)
                return false;
            }
        } else if (!t1.isExpr() && !t2.isExpr()) {
            if (t1.getSymbol() >= 0 && t2.getSymbol() >= 0) {
                // Both t1 and t2 are individual constants (Ind)
                if (t1.getSymbol() == t2.getSymbol() &&
                    Types.isSuperClass(t1.getType(),t2.getType()) && (t1.getData() == t2.getData())) {
                	//System.out.println("T1: "+ t1.getType());
                	//System.out.println("T2: " + t2.getType());
                	//System.out.println("Ind Ind unification");
                    //Both symbols are the same, and the types are compatible (type(t2) >= type(t1))
                    return true;
                } else {
                    return false;
                }
            } else if (t1.getSymbol() < 0 && t2.getSymbol() >= 0) {
                // t1 is a variable (Var) and t2 is an individual constant (Ind)
                if (Types.isSuperClass(t1.getType(), t2.getType())) {
                    int sym = -(t1.getSymbol() + 1);
                    int side = t1.getSide();
                    this.vars[side][sym] = t2;

                    if (side == GOAL) {
                        String t1str = "?" +
                                       this.goal.goalList.variableNames[sym];
                        if (t1.type != Types.IOBJECT) {
                            t1str += " : " + Types.typeName(t1.type);
                        }

                        if (!t1str.startsWith("?$ANON")) {

                            if (t2.getSide() == GOAL) {
                            	//System.out.println("9");
                            	if(varBindType == POSL){    
                            			
                            	this.goal.goalList.varBindings.put(t1str,
                                        t2.toPOSLString(this.goal.goalList.
                                        variableNames, true));
                            	}
                            	
                            	if(varBindType == RuleML){
                                	this.goal.goalList.varBindings.put(t1str,
                                            t2.toRuleMLString(this.goal.goalList.
                                            variableNames, RuleMLFormat.RuleML91));                           		
                            	}
                                
                            } else {
                            	//System.out.println("10");
                            	if(varBindType == POSL){ 
                            		
                          		                          		
                            		String posl = t2.toPOSLString(this.subGoalList.variableNames, true);                        		                            		  
                            		if(posl.contains("->")){
                            			posl = posl.substring(posl.indexOf("->") + 2);
                            		}
                            		                           		
                                this.goal.goalList.varBindings.put(t1str,
                                		posl);
                            	}
                            	if(varBindType == RuleML){ 

                            		String ruleML = t2.toRuleMLString(this.subGoalList.variableNames, RuleMLFormat.RuleML91);
                            		int firstIndex = ruleML.indexOf("</Ind>");
                            		int secondIndex = ruleML.indexOf("</slot>");
                            		
                            		if(firstIndex != -1 && secondIndex != -1)
                            			ruleML = ruleML.substring(firstIndex+6, secondIndex);
                            		
                                    this.goal.goalList.varBindings.put(t1str,
                                    		ruleML);
                                	}                           	
                            }
                        }
                    } else {
                        String t1str = "?" + this.subGoalList.variableNames[sym];
                        if (t1.type != Types.IOBJECT) {
                            t1str += " : " + Types.typeName(t1.type);
                        }

                        if (!t1str.startsWith("?$ANON")) {

                            if (t2.getSide() == GOAL) {
                            	//System.out.println("11");
                            	if(varBindType == POSL){ 
                            		
                            		this.subGoalList.varBindings.put(t1str,
                                        t2.toPOSLString(this.goal.goalList.
                                        variableNames, true));
                            	}
                            	
                            	if(varBindType == RuleML){ 
                            		this.subGoalList.varBindings.put(t1str,
                                        t2.toRuleMLString(this.goal.goalList.
                                        variableNames, RuleMLFormat.RuleML91));
                            	}
                            	
                            } else {
                            	//System.out.println("12");
                            	if(varBindType == POSL){ 
                            		
                            		this.subGoalList.varBindings.put(t1str,
                                        t2.toPOSLString(this.subGoalList.
                                        variableNames, true));
                            	}
                            	if(varBindType == RuleML){ 
                            		this.subGoalList.varBindings.put(t1str,
                                        t2.toRuleMLString(this.subGoalList.
                                        variableNames, RuleMLFormat.RuleML91));
                            	}                           	
                            }
                        }
                    }

                    return true;
                } else {
                    return false; // Types are not compatible (! (type(t1) >= type(t2)) )
                }
            } else if (t1.getSymbol() >= 0 && t2.getSymbol() < 0) {
                // t1 is an individual constant (Ind) and t2 is a variable (Var)
                if (Types.isSuperClass(t2.getType(), t1.getType())) {
                    int sym = -(t2.getSymbol() + 1);
                    int side = t2.getSide();
                    this.vars[side][sym] = t1;

                    if (side == GOAL) {
                        String t2str = "?" +
                                       this.goal.goalList.variableNames[sym];
                        if (t2.type != Types.IOBJECT) {
                            t2str += " : " + Types.typeName(t2.type);
                        }

                        if (!t2str.startsWith("?$ANON")) {

                            if (t1.getSide() == GOAL) {
                            //	System.out.println("13");
                            	if(varBindType == POSL){ 
                            		
                                this.goal.goalList.varBindings.put(t2str,
                                        t1.toPOSLString(this.goal.goalList.
                                        variableNames, true));
                            	}
                            	if(varBindType == RuleML){ 
                                	
                                    this.goal.goalList.varBindings.put(t2str,
                                            t1.toRuleMLString(this.goal.goalList.
                                            variableNames, RuleMLFormat.RuleML91));
                                	}
                            } else {
                            	//System.out.println("14");
                            	if(varBindType == POSL){ 
                            		
                            		this.goal.goalList.varBindings.put(t2str,
                                        t1.toPOSLString(this.subGoalList.
                                        variableNames, true));
                            	}
                            	
                            	if(varBindType == RuleML){ 
                            		this.goal.goalList.varBindings.put(t2str,
                                        t1.toRuleMLString(this.subGoalList.
                                        variableNames, RuleMLFormat.RuleML91));
                            	}
                            }
                        }
                    } else {
                        String t2str = "?" + this.subGoalList.variableNames[sym];
                        if (t2.type != Types.IOBJECT) {
                            t2str += " : " + Types.typeName(t2.type);
                        }

                        if (!t2str.startsWith("?$ANON")) {

                            if (t1.getSide() == GOAL) {
                            	//System.out.println("15");
                            	if(varBindType == POSL){ 
                            		
                            		this.subGoalList.varBindings.put(t2str,
                                        t1.toPOSLString(this.goal.goalList.
                                        variableNames, true));
                            	}
                            	if(varBindType == RuleML){ 
                            		this.subGoalList.varBindings.put(t2str,
                                        t1.toRuleMLString(this.goal.goalList.
                                        variableNames, RuleMLFormat.RuleML91));
                            	}
                            	
                            } else {
                            	//System.out.println("16");
                            	if(varBindType == POSL){ 
                            		
                            		this.subGoalList.varBindings.put(t2str,
                                        t1.toPOSLString(this.subGoalList.
                                        variableNames, true));
                            	}
                            	if(varBindType == RuleML){ 
                            		this.subGoalList.varBindings.put(t2str,
                                            t1.toRuleMLString(this.subGoalList.
                                            variableNames, RuleMLFormat.RuleML91));
                            	}
                            
                            }
                        }
                    }
                    return true;
                } else {
                    return false; // Types are not compatible (! (type(t2) >= type(t2)) )
                }
            } else if (t1.getSymbol() < 0 && t2.getSymbol() < 0) {
                if (t1.getSide() == t2.getSide() &&
                    t1.getSymbol() == t2.getSymbol()) {
                    return true;
                    // same variable - done nothing - prevents infinite dereference loop
                }
                // Both t1 and t2 are variables (Var)


                int type = Types.greatestLowerBound(t1.getType(), t2.getType());

                if(type == Types.INOTHING){
                    //Logger logger = Logger.getLogger("jdrew.oo.td.Unifier");
                    logger.debug("Unification failed as there is no type that satisfies the restrictions of both variable types: " + Types.typeName(t1.getType()) + " and " + Types.typeName(t2.getType()));
                    return false;
                }
                //System.out.println("hi");
                //System.out.println("======Unifier 95=========");
                Term x;
                Term y;
                
                if (down) {
                    x = t1;
                    y = t2;
                } else {
                    x = t2;
                    y = t1;
                }
                int side = y.getSide();
                int sym = -(y.getSymbol() + 1);
                Term xdc = x.deepCopy();
                xdc.setType(type);
                this.vars[side][sym] = xdc;

                if (side == GOAL) {
                    String ystr = "?" + this.goal.goalList.variableNames[sym];
                    if (y.type != Types.IOBJECT) {
                        ystr += " : " + Types.typeName(y.type);
                    }

                    if (!ystr.startsWith("?$ANON")) {
                    	
                        if (x.getSide() == GOAL) {
                        //	System.out.println("17");
                        	if(varBindType == POSL){ 
                        		
                        		this.goal.goalList.varBindings.put(ystr,
                                    x.toPOSLString(this.goal.goalList.
                                    variableNames, true));
                        	}
                        	if(varBindType == RuleML){ 
                        		this.goal.goalList.varBindings.put(ystr,
                                    x.toRuleMLString(this.goal.goalList.
                                    variableNames, RuleMLFormat.RuleML91));
                        	}                        	
                        } else {
                        //	System.out.println("18");
                        	if(varBindType == POSL){ 
                        		
                        		this.goal.goalList.varBindings.put(ystr,
                                    x.toPOSLString(this.subGoalList.
                                    variableNames, true));
                        	}
                        	if(varBindType == RuleML){ 
                        		this.goal.goalList.varBindings.put(ystr,
                                    x.toRuleMLString(this.subGoalList.
                                    variableNames, RuleMLFormat.RuleML91));
                        	}
                        }
                    }
                } else {
                    String ystr = "?" + this.subGoalList.variableNames[sym];
                    if (y.type != Types.IOBJECT) {
                        ystr += " : " + Types.typeName(y.type);
                    }

                    if (!ystr.startsWith("?$ANON")) {

                        if (x.getSide() == GOAL) {
                        	//System.out.println("19");
                        	if(varBindType == POSL){ 
                        		
                        		this.subGoalList.varBindings.put(ystr,
                                    x.toPOSLString(this.goal.goalList.
                                    variableNames, true));	
                        	}
                        	if(varBindType == RuleML){ 
                        		
                        		this.subGoalList.varBindings.put(ystr,
                                    x.toRuleMLString(this.goal.goalList.
                                    variableNames, RuleMLFormat.RuleML91));	
                        	}                       	
                        } else {
                        	//System.out.println("20");
                        	if(varBindType == POSL){ 
                        		
                        		this.subGoalList.varBindings.put(ystr,
                                    x.toPOSLString(this.subGoalList.
                                    variableNames, true));
                        	}
                        	if(varBindType == RuleML){ 
                                
                        		this.subGoalList.varBindings.put(ystr,
                                    x.toRuleMLString(this.subGoalList.
                                    variableNames, RuleMLFormat.RuleML91));
                        	}
                        }
                    }
                }
                return true;
            } else {
                throw new EngineException("Terms are not valid.");
            }
            // This should never happen - one of the previous cases will always occur
        } else {
            throw new EngineException("Terms are not valid.");
        }
        // This should never happen - one of the previous cases will always occur
    }

    /**
     * This method is used to dereference a variable; i.e. to find any variable
     * bindings that have already been made.
     *
     * @param term Term The term to dereference.
     *
     * @return Term The dereferenced term; if the initial term is not a variable
     * then this is that initial term; if the inital term is a variable, then
     * this will either be the original variable; or the term that that
     * variable was bound to if it has been bound.
     */
    private Term deref(Term term) {
        if (term.getSymbol() > 0) {
            return term;
        } else {
            int side = term.getSide();
            int sym = -(term.getSymbol() + 1);
            Term termd = vars[side][sym];
            if (termd == null) {
                return term;
            } else {
                return deref(termd);
            }
        }
    }

    /**
     *
     */
    private Hashtable varNameFound;

    /**
     *  Used to apply the variable bindings to the current goal. Should only be
     *  called if unified is set to true.
     */
    public void applyToGoal() {
        if (!unified) {
            throw new RuntimeException(
                    "Attempt to use substitution without unification.");
        }

        variableCount = 0;
        varNameFound = new Hashtable();
        varNames = new String[this.variableSize];
        if (mode == DCTREE_MODE) {
            goal.goalList.atoms = new Term[atoms[GOAL].length];
            for (int i = 0; i < goal.goalList.atoms.length; i++) {
                goal.goalList.atoms[i] = apply(atoms[GOAL][i]);

            }
            goal.goalList.variableNames = this.varNames;
            goal.goalList.varCount = this.variableCount;
            goal.setSymbolIndex();
        }
    }

    /**
     *  Used to apply variable bindings to the sub goal list. Should only be called
     *  if unified is set to true.
     */
    public void applyToGoalList() {
        if (!unified) {
            throw new RuntimeException(
                    "Attempt to use substitution without unification.");
        }

        variableCount = 0;

        varNameFound = new Hashtable();
        varNames = new String[this.variableSize];
        if (mode == DCTREE_MODE) {
            subGoalList.atoms = new Term[atoms[SUBGOALLIST].length];
            for (int i = 0; i < subGoalList.atoms.length; i++) {
                subGoalList.atoms[i] = apply(atoms[SUBGOALLIST][i]);

            }
            subGoalList.varCount = this.variableCount;
            subGoalList.variableNames = this.varNames;
        }
    }

    /**
     * This method applies the varibale bindings to a term.
     *
     * @param t Term the term to apply variable bindings too.
     *
     * @return Term the term with variable bindings applied to it.
     */
    private Term apply(Term ct) {
        int role = ct.getRole();
        int classID = ct.getType();
        if (!ct.isExpr()) {
            Term dCt = deref(ct);
            Term n = (Term) dCt.deepCopy();
            n.setRole(role);
            //n.setType(classID);
            //n.classID = classID;
            if (dCt == ct) {
                if (n.getSymbol() < 0) {
                    String t = n.getSide() + ":" + n.getSymbol();

                    int idx;
                    if (varNameFound.containsKey(t)) {
                        idx = ((Integer) varNameFound.get(t)).intValue();
                    } else {

                        idx = variableCount;
                        Integer idx2 = idx;
                        varNameFound.put(t, idx2);
                        varNames[idx] = goalLists[dCt.getSide()].variableNames[ -
                                        dCt.getSymbol() - 1];
                        variableCount++;
                    }
                    n.setSymbol( -(idx + 1));
                    //n.classID = classID;
                }
                return n;
            } else {
                Term n2 = apply(n);
                //if(n2.symbol < 0)
                //   n2.setType(classID);
                return n2;
            }
        } else if (ct.isExpr()) {
            Term ct2 = ct;
            Term[] ct2terms = ct2.getSubTerms();
            Vector terms2 = new Vector();
            //Term[] terms2 = new Term[ct2terms.length];
            for (int i = 0; i < ct2terms.length; i++) {
                Term test2 = deref(ct2terms[i]);
                if ((ct2terms[i].getRole() == SymbolTable.IPREST ||
                     ct2terms[i].getRole() == SymbolTable.IREST) &&
                    test2.isExpr() && test2.getSymbol() == SymbolTable.IPLEX) {
                    Term[] restSubTerms = test2.getSubTerms();
                    for (int j = 0; j < restSubTerms.length; j++) {
                        terms2.add(apply(restSubTerms[j]));
                    }

                    if (this.down) {
                        String t = ct2terms[i].getSide() + ":" +
                                   ct2terms[i].getSymbol();
                        int idx;
                        if (varNameFound.containsKey(t)) {
                            idx = ((Integer) varNameFound.get(t)).intValue();
                        } else {
                            idx = variableCount;
                            Integer idx2 = idx;
                            varNameFound.put(t, idx2);
                            varNames[idx] = goalLists[ct2terms[i].getSide()].
                                            variableNames[ -(ct2terms[i].
                                    getSymbol() + 1)];
                            variableCount++;
                        }

                        int symn = -(idx + 1);

                        terms2.add(new Term(symn, ct2terms[i].getRole(),
                                            Types.IOBJECT));
                    }
                } else {
                    terms2.add(apply(ct2terms[i]));
                }
            }

            Term nct = new Term(ct2.getSymbol(), ct2.getRole(), ct2.getType(),
                                terms2);
            nct.setSide(ct2.getSide());
            nct.setAtom(ct.isAtom());
            return nct;
        } else {
            throw new RuntimeException("Error creating resolvent.");
        }
    }

}
