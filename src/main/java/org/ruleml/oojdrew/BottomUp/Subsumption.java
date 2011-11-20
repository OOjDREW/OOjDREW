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

import java.util.Vector;

import org.ruleml.oojdrew.util.DefiniteClause;
import org.ruleml.oojdrew.util.EngineException;
import org.ruleml.oojdrew.util.SymbolTable;
import org.ruleml.oojdrew.util.Term;
import org.ruleml.oojdrew.util.Types;

/**
 * This class is used for checking if one (newly selected) fact is subsumed by
 * another fact that has already been processed.
 *
 * Subsumption checking is a two step process; first the newly selected fact is
 * ground (all variables are bound to newly created constants), once the fact
 * has been made ground then subsumption is checked by attempting to unify the
 * ground atom with other facts; if unification succeedes then the fact it was
 * unified wtih subsumes the fact that was made ground.
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
public class Subsumption {

    /**
     * This variable stores references to the two clauses (facts) that are
     * being considered; the fact stored at index 0 is the one to be made
     * ground and checked for subsumption; the fact that will be stored at
     * index 1 is the one to check against.
     */
    private DefiniteClause[] clauses = new DefiniteClause[2];

    /**
     * This variable stores a copy of the atoms of the two clauses; the same
     * indexing convention is used as for DefiniteClause[] clauses.
     */
    private Term[] atoms = new Term[2];

    /**
     * This variable is used to temporarly store variable bindings; the same
     * indexing convention is used as for DefiniteClause[] clauses.
     */
    private Term[][] vars = new Term[2][];

    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    /**
     * This is used to generate new constants for grounding the facts; it is
     * initially set to the first unused symbol code.
     */
    private int sid;

    /**
     * This creates a subsumption checker; First it initializes all data
     * structures based upon the fact that is passed (this is the one to be
     * checked if it is subsumed by another fact); then it will call the
     * ground(Term) method to ground the fact.
     *
     * @param base DefiniteClause The fact to check for subsumption; this is
     * generally a newly selected fact in the runForwardReasoner method of a
     * ForwardReasoner object.
     */
    public Subsumption(DefiniteClause base) {
        super();
        clauses[LEFT] = base;
        atoms[LEFT] = base.atoms[0].deepCopy(LEFT);
        vars[LEFT] = new Term[base.variableNames.length];
        sid = SymbolTable.symbols.size();
        ground(atoms[LEFT]);
    }

    /**
     * This method is used to ground a term; If the term is an unbound variable
     * it will generate a new symbol to bind the variable to; if it is a
     * non-simple term (atom, plex, complex term) it will recursively call the
     * ground(Term) method on each of the parameters; to ensure that the term
     * is completely ground.
     *
     * @param t Term The term to ground; this is called on the head atom of the
     * fact to be ground for subsumption checking.
     */
    private void ground(Term t) {
        if (t.symbol < 0) {
            if (t.getRole() == SymbolTable.IREST ||
                t.getRole() == SymbolTable.IPREST) {
                vars[t.getSide()][ -(t.symbol +
                        1)] = new Term(SymbolTable.IPLEX, SymbolTable.INOROLE,
                                       t.type, new Vector());
            }

            if (vars[t.getSide()][ -(t.symbol + 1)] == null &&
                t.getRole() != SymbolTable.IREST &&
                t.getRole() != SymbolTable.IPREST) {
                vars[t.getSide()][ -(t.symbol +
                        1)] = new Term(sid++, SymbolTable.INOROLE, t.type);
            }
        }

        if (t.isExpr()) {
            for (int i = 0; i < t.subTerms.length; i++) {
                ground(t.subTerms[i]);
            }
        }
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
     * This method is called to check to see if the passed fact subsumes the
     * fact that was used in the consturctor of the Subsumption object.
     *
     * This method can be called multiple times to check the newly selcted fact
     * for subsumption by more than one old processed fact.
     *
     * @param oldfact DefiniteClause The old fact to test for subsumption with.
     *
     * @return boolean returns true if the passed fact subsums the fact given to
     * the constructor; false otherwise.
     */
    public boolean subsumedBy(DefiniteClause oldfact) {
        clauses[RIGHT] = oldfact;
        atoms[RIGHT] = oldfact.atoms[0].deepCopy(RIGHT);
        vars[RIGHT] = new Term[oldfact.variableNames.length];
        boolean subsumes = unify(atoms[LEFT], atoms[RIGHT]);
        clauses[RIGHT] = null;
        atoms[RIGHT] = null;
        vars[RIGHT] = null;
        return subsumes;
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
    private boolean unify(Term term1, Term term2) {
        Term t1 = deref(term1);
        Term t2 = deref(term2);

        if (t1.isExpr() && t2.isExpr()) {
            if (t1.getSymbol() == t2.getSymbol() &&
                Types.isSuperClass(t2.getType(), t1.getType())) {
                Vector t1restterms = new Vector();
                Vector t2restterms = new Vector();
                Vector t1prestterms = new Vector();
                Vector t2prestterms = new Vector();

                boolean t1rest = (t1.rest > 0);
                boolean t2rest = (t2.rest > 0);
                boolean t1prest = (t1.prest > 0);
                boolean t2prest = (t2.prest > 0);

                if (t1rest && !t2rest) {
                    return false; // t1 has slotted rest term, t2 must have slotted rest term to be more general
                }
                if (t1prest && !t2prest) {
                    return false; // t2 has positional rest term, t2 must have slotted rest term to be more general
                }

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
                                            SymbolTable.INOROLE, Types.IOBJECT,
                                            t1prestterms);
                Term t1restterm = new Term(SymbolTable.IPLEX,
                                           SymbolTable.INOROLE, Types.IOBJECT,
                                           t1restterms);
                Term t2prestterm = new Term(SymbolTable.IPLEX,
                                            SymbolTable.INOROLE, Types.IOBJECT,
                                            t2prestterms);
                Term t2restterm = new Term(SymbolTable.IPLEX,
                                           SymbolTable.INOROLE, Types.IOBJECT,
                                           t2restterms);

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
                if (t1.getSymbol() == t2.getSymbol() &&
                    Types.isSuperClass(t2.getType(), t1.getType())) {
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
            // This should never happen - one of the previous cases will always occur
        } else {
            throw new EngineException("Terms are not valid.");
        }
        // This should never happen - one of the previous cases will always occur

    }
}
