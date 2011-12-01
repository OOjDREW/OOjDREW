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

import java.util.Hashtable;
import java.util.Vector;

import org.ruleml.oojdrew.util.DefiniteClause;
import org.ruleml.oojdrew.util.EngineException;
import org.ruleml.oojdrew.util.SymbolTable;
import org.ruleml.oojdrew.util.Term;
import org.ruleml.oojdrew.util.Types;

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
public class Unifier {

    /**
     * This variable stores references to the two clauses (facts) that are
     * being considered; the fact stored at index 0 is the one to be made
     * ground and checked for subsumption; the fact that will be stored at
     * index 1 is the one to check against.
     */
    private DefiniteClause[] clauses;

    /**
     * This variable stores a copy of the atoms of the two clauses; the same
     * indexing convention is used as for DefiniteClause[] clauses.
     */
    private Term[][] atoms;

    /**
     * This variable is used to temporarly store variable bindings; the same
     * indexing convention is used as for DefiniteClause[] clauses.
     */
    private Term[][] vars;

    /**
     *  This is use as a index for the clauses, atoms, vars variables.
     */
    private static final int FACT = 0;
    /**
     * This is use as a index for the clauses, atoms, vars variables.
     */
    private static final int RULE = 1;

    /**
     *  This variable is set to true if the 2 clauses unified or not.
     */
    private boolean unified = false;

    /**
     * This hashtable contains the variablesUsed used when creating variable bindings.
     */
    private Hashtable variableUsed;
    /**
     *  This vector contains the variable names used when creating variable binding.
     */
    private Vector variableNames;

    /**
     * This method is used to create a unifier that is
     * used to test whether or not 2 terms are equal.
     */ 
     public Unifier(){
     	vars = new Term[2][];
        vars[FACT] = new Term[100];
        vars[RULE] = new Term[100];
     }

	/**
     * This method is used to create a unifier that is
     * used to test whether a fact unifies with a rule.
     *
     * @param fact DefiniteClause fact - this is the fact that will be used 
     * to try and unify with the rule.
     *
     * @param rule DefiniteClause - this is the rule that will be used to
     * try and unify with the fact.
     */ 
    public Unifier(DefiniteClause fact, DefiniteClause rule) {
        super();
                
        if (!fact.isFact()) {
            throw new EngineException(
                    "DefiniteClause fact must be a fact clause.");
        }

        if (rule.isFact()) {
            throw new EngineException(
                    "DefiniteClause rule must be a rule clause.");
        }

        this.clauses = new DefiniteClause[2];
        this.clauses[FACT] = fact;
        this.clauses[RULE] = rule;

        this.atoms = new Term[2][];
        this.atoms[FACT] = new Term[1];
        this.atoms[RULE] = new Term[clauses[RULE].atoms.length];
        this.atoms[FACT][0] = fact.atoms[0].deepCopy(FACT);

        for (int i = 0; i < this.atoms[RULE].length; i++) {
            atoms[RULE][i] = rule.atoms[i].deepCopy(RULE);
        }

        vars = new Term[2][];
        vars[FACT] = new Term[fact.variableNames.length];
        vars[RULE] = new Term[rule.variableNames.length];

        unified = unify(atoms[FACT][0], atoms[RULE][1]);
    }

    /**
     * This method will produce the resolvent of unifing a fact with a rule.
     *
     * @return DefiniteClause the resolvent that was produced by unifying a 
     * fact and rule.
     */
    public DefiniteClause resolvent() {
        this.variableNames = new Vector();
        this.variableUsed = new Hashtable();

        Vector newatoms = new Vector();
        newatoms.add(apply(atoms[RULE][0]));
        for (int i = 2; i < atoms[RULE].length; i++) {
            newatoms.add(apply(atoms[RULE][i]));
        }

        DefiniteClause dc = new DefiniteClause(newatoms, variableNames);
        return dc;
    }

    /**
     * This method returns the value of the unified variable.
     *
     * @return boolean
     */
    public boolean unifies() {
        return unified;
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
     * @return boolean Returns true if the two terms unify; false otherwise.
     */
    public boolean unify(Term term1, Term term2) {
   
        Term t1 = deref(term1);
        Term t2 = deref(term2);  
          
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
                        if (!unify(t1.subTerms[i], t2.subTerms[j])) {
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
                Term t1restterm = new Term(SymbolTable.IPLEX, SymbolTable.IREST,
                                           Types.IOBJECT, t1restterms);
                Term t2prestterm = new Term(SymbolTable.IPLEX,
                                            SymbolTable.IPREST, Types.IOBJECT,
                                            t2prestterms);
                Term t2restterm = new Term(SymbolTable.IPLEX, SymbolTable.IREST,
                                           Types.IOBJECT, t2restterms);

                if (t1prest) {
                    if (!unify(t1.subTerms[t1.prest], t1prestterm)) {
                        return false;
                    }
                } else {
                    if (t1prestterms.size() > 0) {
                        return false; // t1 has no positional rest term, but one is required for successful unification
                    }
                }

                if (t1rest) {
                    if (!unify(t1.subTerms[t1.rest], t1restterm)) {
                        return false;
                    }
                } else {
                    if (t1restterms.size() > 0) {
                        return false; // t1 has no slotted rest term, but one is required for successful unification
                    }
                }

                if (t2prest) {
                    if (!unify(t2.subTerms[t2.prest], t2prestterm)) {
                        return false;
                    }
                } else {
                    if (t2prestterms.size() > 0) {
                        return false; // t2 has no positional rest term, but one is required for successful unification
                    }
                }

                if (t2rest) {
                    if (!unify(t2.subTerms[t2.rest], t2restterm)) {
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
                return true;
            } else {
                // t1 is an individual constant (Ind) and t2 is a complex term (Cterm, Plex, Atom)
                return false;
            }
        } else if (!t1.isExpr() && !t2.isExpr()) {
            if (t1.getSymbol() >= 0 && t2.getSymbol() >= 0) {
                // Both t1 and t2 are individual constants (Ind)
                //edit here //exact area where to make ind and data not bind together
                //but need to get this data thing working
                if (t1.getSymbol() == t2.getSymbol() &&
                    Types.isSuperClass(t2.getType(), t1.getType())  && (t1.getData() == t2.getData())) {
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
                    return true;
                } else {
                    return false; // Types are not compatible (! (type(t2) >= type(t2)) )
                }
            } else if (t1.getSymbol() < 0 && t2.getSymbol() < 0) {
                if (t1.getSide() == t2.getSide() &&
                    t1.getSymbol() == t2.getSymbol()) {
                    return true;
                    // Same variable - do nothing. prevents an infinite dereferencing loop.
                }

                // Both t1 and t2 are variables (Var)
                int type = Types.greatestLowerBound(t1.getType(), t2.getType());
                int side = t2.getSide();
                int sym = -(t2.getSymbol() + 1);
                Term t1dc = t1.deepCopy();
                t1dc.setType(type);
                this.vars[side][sym] = t1dc;
                return true;
            } else {
                throw new EngineException("Terms are not valid.");
            }
            // This should never happen - one of hte previous cases will always occur
        } else {
            throw new EngineException("Terms are not valid.");
        }
        // This should never happen - one of the previous cases will always occur
    }

    /**
     * This method applies the varibale bindings to a term.
     *
     * @param t Term the term to apply variable bindings too.
     *
     * @return Term the term with variable bindings applied to it.
     */
    private Term apply(Term t) {
        int role = t.getRole();
        if (!t.isExpr()) {
            Term dt = deref(t); // dereference Term
            Term n = dt.deepCopy(); // make working copy
            n.setRole(role);
            if (dt == t) { // Have we reached the final referenced value (has it been dereferenced further by the call to deref)
                if (n.symbol < 0) { // If dereferenced term is a variable - calculate the new variable symbol id
                    String var = n.getSide() + ":" + n.getSymbol();
                    int idx;
                    if (this.variableUsed.containsKey(var)) {
                        idx = ((Integer) (variableUsed.get(var))).intValue();
                    } else {
                        idx = this.variableNames.size();
                        this.variableNames.add(clauses[n.getSide()].
                                               variableNames[ -(n.getSymbol() +
                                1)]);
                        this.variableUsed.put(var, idx);
                    }
                    n.setSymbol( -(idx + 1)); // set new variable id
                }
                return n; // return updated term
            } else {
                return apply(n); // apply changes to dereferenced value
            }
        } else {
            Term[] subs = t.subTerms;
            Vector newsubs = new Vector();
            for (int i = 0; i < subs.length; i++) { // for each subterm
                Term sub = deref(subs[i]); // get deferenced subterm
                if ((sub.getRole() == SymbolTable.IREST ||
                     sub.getRole() == SymbolTable.IPREST)
                    && sub.isExpr() && sub.getSymbol() == SymbolTable.IPLEX) {
                    // If this is a rest term, and is bound to a PLEX of terms
                    // merge in rest term list into main body
                    Term[] restterms = sub.subTerms;
                    for (int j = 0; j < restterms.length; j++) {
                        newsubs.add(apply(restterms[j])); // apply variable binds to sub-term and add to sub-term list
                    }
                } else {
                    newsubs.add(apply(subs[i])); // apply variable bindings to sub-term and add to sub-term list
                }
            }

            Term t2 = new Term(t.getSymbol(), t.getRole(), t.getType(), newsubs); // create term for new clause
            t2.setAtom(t.isAtom());
            return t2;
        }
    }

}
