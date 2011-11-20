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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

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
import org.ruleml.oojdrew.TopDown.Builtins.AssertBuiltin;
import org.ruleml.oojdrew.TopDown.Builtins.RegisterBuiltin;
import org.ruleml.oojdrew.TopDown.Builtins.TDBuiltin;
import org.ruleml.oojdrew.parsing.POSLParser;
import org.ruleml.oojdrew.util.DefiniteClause;
import org.ruleml.oojdrew.util.EngineException;
import org.ruleml.oojdrew.util.SymbolTable;
import org.ruleml.oojdrew.util.Term;

/**
 * Based upon the backward reasoner from the original jDREW by Bruce Spencer.
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

public class BackwardReasoner {

    /**
     *
     */
    private static final int REPORT_EVERY_N_INFERENCES = 5000;

    /**
     *
     */
    public Hashtable clauses;

    public Hashtable oids;

    public int count = 0;
    
    /**
     *
     */
    private Hashtable builtins;

    public Hashtable getBuiltins(){
        return builtins;
    }

    /**
     *
     */
    private Stack choicePoints;
    /**
     *
     */
    private int nExtensions;
    /**
     *
     */
    private int nInferences;
    /**
     *
     */
    private GoalList top;

    /**
     *
     */
    //Logger logger = Logger.getLogger("jdrew.oo.td.BackwardReasoner");

    /**
     *
     */
    public BackwardReasoner() {
        this.clauses = new Hashtable();
        this.oids = new Hashtable();
        oids.put(-1, new Vector());
        choicePoints = new Stack();
        nExtensions = 0;
        nInferences = 0;
        builtins = new Hashtable();
        registerBuiltins();
    }

    /**
     *
     * @param clauses Hashtable
     */
    public BackwardReasoner(Hashtable clauses, Hashtable oids) {
        this.clauses = clauses;
        this.oids = oids;
        choicePoints = new Stack();
        nExtensions = 0;
        nInferences = 0;
        builtins = new Hashtable();
        registerBuiltins();
    }

    /**
     *
     */
    public void registerBuiltins() {
        this.registerBuiltin(new AssertBuiltin(this));
        this.registerBuiltin(new RegisterBuiltin(this, "registerBuiltin"));
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
     *
     * @param handler Builtin
     */
    public void registerBuiltin(Builtin handler) {
        registerBuiltin(new TDBuiltin(handler));
    }

    /**
     *
     * @param handler TDBuiltin
     */
    public void registerBuiltin(TDBuiltin handler) {
        Integer sym = handler.getSymbol();
        this.builtins.put(sym, handler);
    }

    /**
     *
     * @param it
     */
         
     
    public void loadClauses(Iterator it) {
        while (it.hasNext()) {
            DefiniteClause dc = (DefiniteClause) it.next();
            //logger.debug("Loaded clause: " + dc.toPOSLString());
            Integer sym = dc.atoms[0].getSymbol();
            if(!dc.atoms[0].subTerms[0].isExpr()){
                int ioid = dc.atoms[0].subTerms[0].getSymbol();
                if(ioid < 0) ioid = -1;
                //logger.debug("Loading oid: " + ioid);

                Integer oid = ioid;

                if (oids.containsKey(oid)) {
                    //if(ioid != -1)
                        //logger.warn("Duplicate OID: " + SymbolTable.symbol(oid.intValue()));
                    Vector v = (Vector) oids.get(oid);
                    v.add(dc);
                } else {
                    Vector v = new Vector();
                    v.add(dc);
                    oids.put(oid, v);
                }
            }
            if (clauses.containsKey(sym)) {
                Vector v = (Vector) clauses.get(sym);
                v.add(dc);
            } else {
                Vector v = new Vector();
                v.add(dc);
                clauses.put(sym, v);
            }
        }
    }

    /**
     *
     * @return
     */
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("Size " + nExtensions + "\n");
        toString(1, top, b);
        return b.toString();
    }

    /**
     *
     * @param indent int
     * @param gl GoalList
     * @param b StringBuffer
     */
     
     
     
    private void toString(int indent, GoalList gl, StringBuffer b) {
        for (int i = 0; i < indent; i++) {
            b.append(" ");
        }
        b.append(gl.toString());
        b.append("\n");
        for (int i = 1; i < gl.atomCount; i++) {
            if (gl.memberGoals[i].state == Goal.HAS_SUBGOALLIST_STATE) {
                toString(indent + 2, gl.memberGoals[i].subGoalList, b);
            }
        }
    }

    /**
     * Methods to generate a DefaulteMultibleTreeNode representation of the
     * solution tree. This can be displayed in a JTree
     * @return DefaultMutableTreeNode
     */
    public DefaultMutableTreeNode toTree() {
        return toTree(top);
    }

    /**
     *
     * @param gl GoalList
     * @return DefaultMutableTreeNode
     */
    private DefaultMutableTreeNode toTree(GoalList gl) {
    	//System.out.println(gl.toString());
        DefaultMutableTreeNode tn = new DefaultMutableTreeNode();
        
        tn.setUserObject(gl.toString());
        
        for (int i = 1; i < gl.atomCount; i++) {
            if (gl.memberGoals[i].state == Goal.HAS_SUBGOALLIST_STATE) {
                tn.add(toTree(gl.memberGoals[i].subGoalList));
            }
        }
        return tn;
    }

    /**
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
     
     
    public class DepthFirstSolutionIterator implements Iterator {

		//Constructor takes a Definite Clause for a query

        DepthFirstSolutionIterator(DefiniteClause queryClause) {
            foundNext = false;
            failed = false;
            enteringFirstTime = true;
            top = new GoalList(queryClause);
            top.init();
        }
		
		//Constructor Takes a goal list.

        DepthFirstSolutionIterator(GoalList goalList) {
            foundNext = false;
            failed = false;
            enteringFirstTime = true;
            top = goalList;
            top.init();
        }

        boolean foundNext;
        boolean failed;
        boolean enteringFirstTime;
		//Check to see if there is a next goal
        public boolean hasNext() {
            if (foundNext) {
                return true;
            } else if (failed) {
                foundNext = false;
                return false;
            } else {
                if (!enteringFirstTime) {
                    if (!chronologicalBacktrack()) {
                        failed = true;
                    }
                }
                boolean succeeded = false;
                while (!failed && !succeeded) {
                    Goal g = firstOpenGoal();
                    //if (g != null) {
                    //logger.debug("New Goal is " + g + " in state " + g.stateToString());
                    //}
                    if (g == null) {
                        succeeded = true;
                    } else if (g.hasMoreChoices()) {
                        if (g == null) {
                            System.err.println("GEE IS NULL");
                        }
                        if (choicePoints == null) {
                            System.err.println("CHOICEPOINTS IS NULL");
                        }
                        choicePoints.push(g);
                        g.nextChoice(Goal.PROPAGATE_WHEN_SOLVED);
                    } else {
                        g.refreshChoices();
                        if (!chronologicalBacktrack()) {
                            failed = true;
                        }
                    }
                }
                foundNext = succeeded;
                return foundNext;
            }
        }

        /**
         *
         * @return Object
         */
         
         //Top is a goal list
         
        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            foundNext = false;
            enteringFirstTime = false;
            return top;
        }

        /**
         *
         * @throws UnsupportedOperationException
         */
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException
                    ("DepthFirstSolutionIterator does not allow remove");
        }

        /**
         *
         * @return boolean
         */
        private boolean chronologicalBacktrack() {
            //go back to most recent choicepoint that has something to try
            //unbind any variables that were done since that choice

            boolean foundSomethingToRetry = false;
            while (!choicePoints.empty() && !foundSomethingToRetry) {
                Goal g = (Goal) choicePoints.pop();
                //logger.debug("BTing - Removing " + g.subGoalList +
                //                     " from " + g);

                g.undoChoice();
                if (g.hasMoreChoices()) {
                    foundSomethingToRetry = true;
                    // logger.debug("BTing - found something to retry: " + g);

                } else {
                    g.refreshChoices();
                }
            }
            return foundSomethingToRetry;
        }

    }


    /**
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
     
     
     
    public class IterativeDepthFirstSolutionIterator implements Iterator {

        IterativeDepthFirstSolutionIterator(GoalList goalList,
                                            int max, int by) {
            foundNext = false;
            failed = false;
            enteringFirstTime = true;
            this.max = max;
            this.by = by;
            bumpedIntoLimit = false;
            top = goalList;
            top.init();
        }

        IterativeDepthFirstSolutionIterator(DefiniteClause queryClause,
                                            int max, int by) {

            foundNext = false;
            failed = false;
            enteringFirstTime = true;
            this.max = max;
            this.by = by;
            bumpedIntoLimit = false;
            top = new GoalList(queryClause);
            top.init();
        }

        private boolean foundNext;
        private boolean failed;
        private boolean enteringFirstTime;
        private int max;
        private int by;
        private boolean bumpedIntoLimit;

        public boolean hasNext() {
            if (foundNext) {
                return true;
            } else if (failed && !bumpedIntoLimit) {
                foundNext = false;
                return false;
            } else {
                if (!enteringFirstTime) {
                    if (!iterativeDepthChronologicalBacktrack()) {
                        failed = true;
                    }
                }
                boolean succeeded = false;
                while (!(failed && !bumpedIntoLimit) && !succeeded) {
                    //failure in this search method means failure without
                    //having hit the depth limit, hence "!failed" is
                    //replaced by "!(failed && !bumpedIntoLimit)"
                    Goal g = firstOpenGoal();
                    ///if (g != null) {
                    //   logger.debug("New Goal is " + g + " in state " + g.stateToString());
                    //}
                    if (g == null) {
                        if (max - by < nExtensions && nExtensions <= max) {
                            succeeded = true;
                        } else {
                            if (!iterativeDepthChronologicalBacktrack()) {
                                failed = true;
                            }
                        }
                    } else if (g.hasMoreChoices()) {
                        int nextChoiceSize = g.nextChoiceSize();
                        choicePoints.push(g);
                        g.nextChoice(Goal.PROPAGATE_WHEN_SOLVED);
                        //logger.debug("NExtensions = " + nExtensions + "\nnextChoiceSize = " +
                        //            nextChoiceSize + "\nmax = " + max);

                        if (nExtensions + nextChoiceSize > max) {
                            //uses the number of new subgoals as an admissible heuristic
                            //in an a* search
                            bumpedIntoLimit = true;
                            if (!iterativeDepthChronologicalBacktrack()) {
                                failed = true;
                            }
                        }

                    } else {
                        g.refreshChoices();
                        if (!iterativeDepthChronologicalBacktrack()) {
                            failed = true;
                        }
                    }
                }
                foundNext = succeeded;
                return foundNext;
            }
        }

        /**
         *
         * @return Object
         */
        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            foundNext = false;
            enteringFirstTime = false;
            return top;
        }

        /**
         *
         * @throws UnsupportedOperationException
         */
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException
                    ("DepthFirstSolutionIterator does not allow remove");
        }

        /**
         *
         * @return boolean
         */
        private boolean iterativeDepthChronologicalBacktrack() {
            //go back to most recent choicepoint that has something to try
            //unbind any variables that were done since that choice

            boolean foundSomethingToRetry = false;
            while (!choicePoints.empty() && !foundSomethingToRetry) {
                Goal g = (Goal) choicePoints.pop();
                // logger.debug("BTing - Removing " + g.subGoalList + " from " + g);

                g.undoChoice();
                if (g.hasMoreChoices()) {
                    foundSomethingToRetry = true;
                    //logger.debug("BTing - found something to retry: " + g);

                } else {
                    g.refreshChoices();
                }
            }
            if (!foundSomethingToRetry) {
                if (bumpedIntoLimit) {
                    max += by;
                    //logger.debug("Increasing the limit from " + (max - by) + " to " + max);

                    bumpedIntoLimit = false;
                    foundSomethingToRetry = true;
                }
            }
            return foundSomethingToRetry;
        }

    }


    /**
     *
     * @param queryClause
     * @return
     */
    public Iterator depthFirstSolutionIterator(DefiniteClause queryClause) {
        return new DepthFirstSolutionIterator(queryClause);
    }
    /**
     *
     * @param goalList
     * @return
     */
    public Iterator depthFirstSolutionIterator(GoalList goalList) {
        return new DepthFirstSolutionIterator(goalList);
    }

    /**
     *
     * @param queryClause
     * @param max
     * @param by
     * @return
     */
    public Iterator iterativeDepthFirstSolutionIterator
            (DefiniteClause queryClause, int max, int by) {
        return new IterativeDepthFirstSolutionIterator(queryClause, max, by);
    }

    /**
     *
     * @param queryClause - the queryClause contains the internal representation of the query to be executed
     * @return  Iterator the Iterator contains all the solutions to the query
     */
    public Iterator iterativeDepthFirstSolutionIterator(DefiniteClause queryClause) {
        return new IterativeDepthFirstSolutionIterator(queryClause, 1, 1);
    }
    
    /**
     *
     * @param goalList
     * @param max
     * @param by
     * @return
     */
    public Iterator iterativeDepthFirstSolutionIterator
            (GoalList goalList, int max, int by) {
        return new IterativeDepthFirstSolutionIterator(goalList, max, by);
    }

    /**
     *
     * @param goalList
     * @return
     */
    public Iterator iterativeDepthFirstSolutionIterator
            (GoalList goalList) {
        return new IterativeDepthFirstSolutionIterator(goalList, 1, 1);
    }

    /**
     *
     * @return
     */
    public Goal firstOpenGoal() {
        //This is a shortcut to finding the first open goal
        // we will develop a better strategy involving an openGoals iterator
        return top.firstOpenGoal();
    }

    /**
     * Goal - contains an atom (atomic formula) to be solved. Each Goal object
     * belongs to exactly one GoalList.  Also each Goal can have at most one
     * SubGoalList attached to it at a given time. The SubGoalList is attached by
     * calling attachSubGoalList, and this GoalList forms the children of the Goal
     * in the GoalTree.  The possible GoalLists relevant for this Goal are created
     * by first constructing the SubGoalListIterator and then calling next() on
     * this Iterator to generate successively the SubGoalLists.  By relevant, we
     * mean that the Goal is unifiable with the head atom of a Definite Clause in
     * the BackwardReasoner's input clauses.
     *
     * <p> A Goal may be partially or fully solved.  A Goal with an attached
     * SubGoalList is fully solved if that SubGoalList is empty, or if all of the
     * Goals in that SubGoalList are solved. In this case we also say that the
     * SubGoalList is solved.  Solved is synonymous with fully solved.  A Goal or
     * its attached SubGoalList is partially solved if not fully solved and not
     * failed (see below).
     *
     * <p> A Goal may also become failed, which means that there are no
     * SubGoalLists available for it, or all SubGoalLists available for it are
     * failed GoalLists.  A failed GoalList contains at least one failed Goal. A
     * Goal informs the GoalList of which it is a member that is a failed Goal by
     * calling failed(this) on the GoalList. Recall that a Goal knows this only
     * after all of its SubGoalLists have been found to be failed GoalLists, and
     * since this is under programmer control, it is up to the programmer to make
     * the call to failed.  Depending on the client program, this method may or
     * may not be used as it is sets variables that are provided only for the
     * user's convenience of record keeping, and do not initiate any other
     * activity in the BackwardReasoner.
     *
     * <p>Title: OO jDREW</p>
     * <p>Description: A deductive reasoning engine for Object-Oriented Knowledge Representation in OO RuleML</p>
     * <p>Copyright: Copyright (c) 2003</p>
     * <p>Company: National Research Council of Canada</p>
     * @author Marcel Ball
     * @version 0.83
     */
    public class Goal {
        public static final int NO_AUTO_PROPAGATE = 0;
        public static final int PROPAGATE_WHEN_SOLVED = 1;
        public static final int PROPAGATE_EAGERLY = 2;

        public static final int INITIAL_STATE = 0;
        public static final int HAS_SUBGOALLIST_STATE = 1;
        public static final int HAS_NO_SUBGOALLIST_STATE = 2;
        public static final int HAS_BUILTIN_SOLUTION_STATE = 3;
        public static final int HAS_NO_BUILTIN_SOLUTION_STATE = 4;
        public static final int HAS_NAF_SOLUTION_STATE = 5;
        public static final int HAS_NO_NAF_SOLUTION_STATE = 6;
        public static final int HAS_UNCONSUMED_NAF_SOLUTION_STATE = 7;

        public static final int PROVE_BY_SUBGOALLIST = 0;
        public static final int PROVE_BY_BUILTIN = 1;
        public static final int PROVE_BY_NAF = 2;

        int state;
        int proveByType;

        /**
         *
         * @return String
         */
        String stateToString() {
            switch (state) {
            case INITIAL_STATE: {
                return "INITIAL_STATE";
            }
            case HAS_SUBGOALLIST_STATE: {
                return "HAS_SUBGOALLIST_STATE";
            }
            case HAS_NO_SUBGOALLIST_STATE: {
                return "HAS_NO_SUBGOALLIST_STATE";
            }
            case HAS_BUILTIN_SOLUTION_STATE: {
                return "HAS_BUILTIN_SOLUTION_STATE";
            }
            case HAS_NO_BUILTIN_SOLUTION_STATE: {
                return "HAS_NO_BUILTIN_SOLUTION_STATE";
            }
            case HAS_NAF_SOLUTION_STATE: {
                return "HAS_NAF_SOLUTION_STATE";
            }
            case HAS_NO_NAF_SOLUTION_STATE: {
                return "HAS_NO_NAF_SOLUTION_STATE";
            }
            case HAS_UNCONSUMED_NAF_SOLUTION_STATE: {
                return "HAS_UNCONSUMED_NAF_SOLUTION_STATE";
            }
            }
            return "No string for this state " + state;
        }

        /**
         *
         * @param goalList GoalList
         * @param literalIndex int
         */
        Goal(GoalList goalList, int literalIndex) {
            this.goalList = goalList;
            this.literalIndex = literalIndex;
            solved = false;
            subGoalList = null;
            state = INITIAL_STATE;
        }

        /**
         *
         * @return boolean
         */
        boolean nafGoal() {
            return goalList.atoms[symbolIndex].getSymbol() == SymbolTable.INAF;
        }

        SubGoalListIterator sglit;
        GoalList goalList;
        int literalIndex;
        int symbolIndex;
        int propagateMode;
        boolean solved;
        GoalList subGoalList;

        /**
         *
         * @return boolean
         */
        boolean hasMoreChoices() {
            //logger.debug("Entering hasMoreChoices()");
            if (proveByType == PROVE_BY_SUBGOALLIST) {
                return hasMoreSubGoalLists();
            } else if (proveByType == PROVE_BY_NAF) {
                if (state == HAS_NO_NAF_SOLUTION_STATE ||
                    state == HAS_NAF_SOLUTION_STATE) {
                    return false;
                } else if (state == INITIAL_STATE) {
                   // logger.debug("Proving by NAF:");
                    Term nafAtom = this.goalList.atoms[this.symbolIndex];
                    String goalString = "";

                    Term[] nafAtomterms = nafAtom.getSubTerms();
                    for (int i = 0; i < nafAtomterms.length; i++) {
                        goalString +=
                                nafAtomterms[i].toPOSLString(this.goalList.
                                variableNames);
                        if ((i + 1) < nafAtomterms.length) {
                            goalString += ",";
                        }
                    }
                    goalString += ".";

                    //logger.debug("Naf goal string: " + goalString);

                    BackwardReasoner nafTree = null;
                    DefiniteClause nafQueryClause = null;
                    try {
                        POSLParser dcfp = new POSLParser();
                        try {
                            nafQueryClause = dcfp.parseQueryString(goalString);
                        } catch (antlr.RecognitionException ex1) {
                           // logger.error("Cannot parse naf clause: " +
                            //             goalString);
                           // logger.error(
                            //        "This should never happen as this is a generated clause string.");
                            return false;
                        } catch (antlr.TokenStreamException ex1) {
                           // logger.error("IO Exception parsing naf clause.");
                            return false;
                        }

                        //logger.debug("New BackwardReasoner for naf clause: " + goalString);

                        nafTree = new BackwardReasoner(clauses, oids);

                    } catch (RuntimeException e) {
                        //logger.error("Runtime exception creating naf clause.");
                        return false;
                    }

                    Iterator nafSolver = nafTree.depthFirstSolutionIterator(
                            nafQueryClause);

                    if (nafSolver.hasNext()) {
                        //logger.debug("NO NAF solution");
                        state = HAS_NO_NAF_SOLUTION_STATE;
                    } else {
                        //logger.debug("NAF soltuion");
                        state = HAS_UNCONSUMED_NAF_SOLUTION_STATE;
                    }
                }
                return state == HAS_UNCONSUMED_NAF_SOLUTION_STATE;
            }
            // else if (provedByType == PROVED_BY_BUILTIN){
            else {
                return false; //Can't happen unless proveByType is missing
            }
        }

        /**
         *
         * @param propagateMode int
         */
        void nextChoice(int propagateMode) {
            //logger.debug("Trying next choice for " + this);
            if (!hasMoreChoices()) {
                throw new EngineException("Attempted next choice for " +
                                          this +", but no more choices exist");
            } else if (proveByType == PROVE_BY_SUBGOALLIST) {
                attachNextSubGoalList(propagateMode);
            } else if (proveByType == PROVE_BY_NAF) {
                solved = true;
                state = HAS_NAF_SOLUTION_STATE;
                goalList.notifySolved(this);
            }
            // else if (provedByType == PROVED_BY_BUILTIN){
            nExtensions++;
            nInferences++;
            if (BackwardReasoner.REPORT_EVERY_N_INFERENCES > 0) {
                if (nInferences % BackwardReasoner.REPORT_EVERY_N_INFERENCES ==
                    0) {
                    //logger.info("Performed " + nInferences +
                     //           " inferences");
                }
            }
        }

        /**
         *
         * @return int
         */
        int nextChoiceSize() {
            if (!hasMoreChoices()) {
                throw new EngineException("Attempted next choice for " +
                                          this +", but no more choices exist");
            } else if (proveByType == PROVE_BY_SUBGOALLIST) {
                return sglit.nextGoalListSize() - 1;
            } else if (proveByType == PROVE_BY_NAF){
                return 0;
            } else {
                return 1;
            }
        }

            /**
         *
         */
        void undoChoice() {
            //logger.debug("Undoing choice for " + this);
            if (proveByType == PROVE_BY_SUBGOALLIST) {
                removeSubGoalList();
            } else if (proveByType == PROVE_BY_NAF) {
                removeNAFSolve();
                state = HAS_NO_NAF_SOLUTION_STATE;
            }
            //else if (proveByType == PROVED_BY_BUILTIN)
            nExtensions--;
        }

        /**
         *
         */
        void refreshChoices() {
            //logger.debug("refreshing choices for " + this);
            if (proveByType == PROVE_BY_SUBGOALLIST) {
                state = INITIAL_STATE;
            } else if (proveByType == PROVE_BY_NAF) {
                state = INITIAL_STATE;
                //else if (proveByType == PROVED_BY_BUILTIN)
            }
        }

        /**
         *
         */
        void removeNAFSolve() {
            if (solved) {
                //should be solved.  If not we should not be here
                goalList.notifyUnsolved(this);
                solved = false;
            }
        }

        /**
         *
         * @return boolean
         */
        boolean hasMoreSubGoalLists() {
            if (state == INITIAL_STATE) {
                state = HAS_NO_SUBGOALLIST_STATE;
                sglit = new SubGoalListIterator(this,
                                                SubGoalListIterator.
                                                APPLY_TO_GOALLIST);
                //if (true) {
                //    SubGoalListIterator sglit_1 = new SubGoalListIterator(this,
                //            SubGoalListIterator.APPLY_TO_GOALLIST);
                //logger.debug("Choices for Goal " + this +" are ");
                //    int count = 0;
                //    while (sglit_1.hasNext()) {
                //        count++;
                //logger.debug(count + " " + sglit_1.next());
                //    }
                //logger.debug("Total of " + count + " choices");
                //}
            }
            return sglit.hasNext();
        }

        /**
         *
         * @param propagateMode int
         */
        void attachNextSubGoalList(int propagateMode) {
            //have already checked that a new subgoalist exists
            //first undo any bindings that might already have been propagated
            if (state == HAS_SUBGOALLIST_STATE) {
                removeSubGoalList();
            }
            GoalList subGoalList = (GoalList) sglit.next();
            //logger.debug("Adding " + subGoalList + " to goal " + this);

            this.subGoalList = subGoalList;
            this.propagateMode = propagateMode;
            subGoalList.setParent(this);
            state = HAS_SUBGOALLIST_STATE;
            if (subGoalList.solved()) {
                solved = true;
                subGoalList.propagateBindingsToParent();
                goalList.notifySolved(this);
            }
            if (propagateMode == PROPAGATE_EAGERLY) {
                subGoalList.propagateBindingsToParent();
            }
        }

        /**
         *
         */
        void removeSubGoalList() {
            //logger.debug("Removing " + this.subGoalList + " from goal " + this);
            if (solved) {
                goalList.notifyUnsolved(this);
                solved = false;
            }
            state = HAS_NO_SUBGOALLIST_STATE;
            subGoalList = null;
        }

        /**
         *
         */
        public void setSymbolIndex() {
            goalList.setSymbolIndex();
        }

        /**
         *
         * @return String
         */
        public String toString() {
            StringBuffer b = new StringBuffer();
            setSymbolIndex();
            
            b.append(this.goalList.atoms[this.symbolIndex].toPOSLString(this.
                    goalList.variableNames));
            
            //System.out.println("Test Line 1003: ");
            //System.out.println(this.goalList.atoms[this.symbolIndex].toPOSLString(this.
             //       goalList.variableNames));
            
            
            return b.toString();
        }

    } //Goal


    /**
     * GoalList - contains a list of Goals and is attached to a specific Goal
     * which is called its parent. If and when a Goal in the list becomes solved,
     * or partially solved, the effect is to bind variables in the atomic formula.
     * These bindings are immediately applied to the sibling Goals in this
     * SubGoalList. They may also be propogated to the parent Goal, by calling
     * subGoalListSolved() on the parent when the parent is attached to this
     * SubGoalList.  By default, this call must be done by the programmmer, but
     * the default behaviour can be set to PROPAGATE_FULLY_SOLVED, which changes
     * it so that subGoalSolved is called on the parent when the SubGoalList is
     * fully solved. The default behaviour can also be set to PROPAGATE_EAGERLY
     * which will propagate eagerly: subGoalSolved is called automatically
     * whenever a GoalList becomes more fully solved by the solution of one more
     * of its member Goals.
     *
     * <p> Because a Goal may be attached to a failed SubGoalList, but other
     * SubGoalLists are still available for it, it is important to be able undo
     * the effects of this failed SubGoalList on the Goal, and consequently
     * effects that have may been propagated throughout the BackwardReasoner. Thus each Goal
     * and each GoalList has a backup facility that makes it possible to retrieve
     * a previous version, replacing the old values the variable bindings. In the
     * case of the GoalList, the backup facility also retrieves a records of what
     * member Goals have been solved. The backup facility is used through the
     * createBackup() and restoreBackup() routines.  Thus backups are done
     * manually, not automatically, and are based on a stack; N calls to
     * createBackup() followed by M calls to restoreBackup(), when N<=M, will
     * restore to the point when N-M call to createBackup() was made. If M>N, an
     * exception is thrown. (We may consider automatic backups through the tree
     * but the problem is that backups need to be synchronized so that the tree is
     * brought back to a consistent overall state. This is also the reason why
     * propagation from GoalLists to Goals is not automatic by default.  One can
     * keep the effects of changes to the Clause tree local in a GoalList until
     * that GoalList is fully solved, and only then propagate it.  This removes
     * the need to undo the results of propagating partially solved GoalLists
     * through the tree.)
     *
     * <p>Title: OO jDREW</p>
     * <p>Description: A deductive reasoning engine for Object-Oriented Knowledge Representation in OO RuleML</p>
     * <p>Copyright: Copyright (c) 2003</p>
     * <p>Company: National Research Council of Canada</p>
     * @author Marcel Ball
     * @version 0.83
     */
    public class GoalList {
        public Hashtable varBindings;

        /**
         *
         * @param dc DefiniteClause
         */
        GoalList(DefiniteClause dc) {
            atoms = dc.atoms;
            varCount = dc.variableNames.length;
            hasVariableNames = true;
            variableNames = dc.variableNames;
            varBindings = new Hashtable();
        }

        /**
         *
         */
        void init() {
            countAtoms();
            memberGoals = new Goal[atomCount];
            memberGoals[0] = null;
            for (int i = 1; i < atomCount; i++) {
                memberGoals[i] = new Goal(this, i);
            }
            setSymbolIndex();
            for (int i = 1; i < atomCount; i++) {
                if (memberGoals[i].nafGoal()) {
                    memberGoals[i].proveByType = Goal.PROVE_BY_NAF;
                } else {
                    memberGoals[i].proveByType = Goal.PROVE_BY_SUBGOALLIST;
                    //else -- add in builtins
                }
            }
        }

        /**
         *
         */
        void countAtoms() {
            atomCount = atoms.length;
        }

        /**
         *
         */
        void setSymbolIndex() {
            int symbolIndex = 0;
            for (int i = 1; i < atomCount; i++) {
                //symbolIndex += symbols[symbolIndex][1];
                symbolIndex++;
                memberGoals[i].symbolIndex = symbolIndex;
            }
        }

        public int varCount;
        public String[] variableNames;
        public boolean hasVariableNames;
        public int atomCount = 0;
        //int[][] symbols;
        public Term[] atoms;
        public Goal parent;
        public Goal[] memberGoals;
        boolean hasParent = false;
        boolean solved = false;

        /**
         *
         * @param goal Goal
         */
        void setParent(Goal goal) {
            parent = goal;
            hasParent = true;
        }

        /**
         *
         * @param g Goal
         */
        void notifySolved(Goal g) {
            if (solved()) {
                if (hasParent) {
                    parent.solved = true;
                    if (parent.propagateMode == Goal.PROPAGATE_WHEN_SOLVED) {
                        propagateBindingsToParent();
                    }
                    parent.goalList.notifySolved(parent);
                }
            }
        }

        /**
         *
         * @return boolean
         */
        boolean solved() {
            int i = 1;
            while (i < atomCount) {
                if (!memberGoals[i].solved) {
                    return false;
                } else {
                    i++;
                }
            }
            return true;
        }

        /**
         *
         * @param g Goal
         */
        void notifyUnsolved(Goal g) {
            // logger.debug("Unsolving goal " + g);

            if (g.propagateMode == Goal.PROPAGATE_WHEN_SOLVED) {
                if (g.proveByType == Goal.PROVE_BY_SUBGOALLIST) {
                    g.goalList.restoreBackup();
                    // logger.debug("Restoring unsolved goal to " + g);
                }
            }
            if (solved()) {
                if (hasParent) {
                    parent.goalList.notifyUnsolved(parent);
                }
            }
            g.solved = false;
        }

        /**
         *
         */
        public void propagateBindingsToParent() {
            if (!hasParent) {
                //logger.warn(this +" has no parent.");
                //Should we throw an exception here?
            } else {
                //logger.debug("Moving variable bindings of " + this +" to " + parent);


                Unifier u = new Unifier(parent, this, Unifier.DCTREE_MODE);
                if (u.unified) {
                    parent.goalList.createBackup();
                    u.applyToGoal();
                    //this.setSymbolIndex();
                    //logger.debug("Variable bindings moved to " + parent);
                } else {
                    throw new EngineException("Could not propagate bindings");
                }
            }
        }

        /**
         *  Used to display the Proof Tree in the GUI
         * 
         * @return String
         */
        public String toString() {
            StringBuffer b = new StringBuffer();
            //need this
            b.append(this.atoms[0].toPOSLString(this.variableNames)); //top
           
            //Recontructing posl in the goal list 
            
            //System.out.println("Test Line 1214 BR:");
            //System.out.println(this.atoms[0].toPOSLString(this.variableNames));
            
            if (this.atoms.length > 1) {
                 //and this
            	b.append(":-");
                for (int i = 1; i < this.atoms.length; i++) {
                    b.append(this.atoms[i].toPOSLString(this.variableNames));
                    if (i + 1 < this.atoms.length) {
                        b.append(",");
                    }
                }
            }
            b.append(".");
            return b.toString();
        }

        public String toStringAll() {
            StringBuffer b = new StringBuffer();
            //need this
           // b.append(this.atoms[0].toPOSLString(this.variableNames)); //top
           
            //Recontructing posl in the goal list 
            
            //System.out.println("Test Line 1214 BR:");
            //System.out.println(this.atoms[0].toPOSLString(this.variableNames));
            
            if (this.atoms.length > 1) {
                 //and this
            	//b.append(":-");
                for (int i = 1; i < this.atoms.length; i++) {
                    b.append(this.atoms[i].toPOSLStringAll(this.variableNames,false,true));
                   
                    if (i + 1 < this.atoms.length) {
                        b.append(",");
                    }
                }
            }
            b.append(".");
            return b.toString();
        }
        
        /**
         *
         * @return Goal
         */
        public Goal firstOpenGoal() {
            for (int i = 1; i < atomCount; i++) {
                Goal g = memberGoals[i];
                if (g.proveByType == Goal.PROVE_BY_SUBGOALLIST) {
                    if (g.state != Goal.HAS_SUBGOALLIST_STATE &&
                        !g.solved) {
                        return g;
                    } else if (!g.solved) {
                        return g.subGoalList.firstOpenGoal();
                    }
                } else if (g.proveByType == Goal.PROVE_BY_NAF) {
                    if (g.state == Goal.INITIAL_STATE) {
                        return g;
                    }
                }
                //else if (g.proveByType == Goal.PROVE_BY_BUILTIN)
                //if(g.state == INITIAL) return g;
            }
            return null; //means no open goals
        }

        /**
         *
         * @return Goal
         */
        public Goal head() {
            return new Goal(this, 0);
        }

        /**
         *
         * @param idx int
         * @return Term
         */
        public Term getAtom(int idx) {
            if (idx < 0 || idx >= this.atoms.length) {
                return null;
            } else {
                return this.atoms[idx].deepCopy();
            }
        }

        private Stack s = new Stack();

        /**
         *
         */
        public void createBackup() {
            //s.push(new StackFrame(symbols, varCount, hasVariableNames, variableNames));
            s.push(new StackFrame(atoms, varCount, hasVariableNames,
                                  variableNames));
        }

        /**
         *
         */
        public void restoreBackup() {
            if (s.empty()) {
                //logger.error("Popped empty stack");
            } else {
                StackFrame sf = (StackFrame) s.pop();
                //symbols = sf.symbols;
                atoms = sf.atoms;
                varCount = sf.varCount;
                hasVariableNames = sf.hasVariableNames;
                variableNames = sf.variableNames;
                //setSymbolIndex();
            }
        }

        /**
         *
         */
        public void purgeBackup() {
            if (s.empty()) {
                throw new RuntimeException("Popped empty stack");
            }
            StackFrame sf = (StackFrame) s.pop();
        }

        /**
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
        class StackFrame {
            //int[][] symbols;
            Term[] atoms;
            int varCount;
            boolean hasVariableNames;
            String[] variableNames;

            /**
             *
             * @param terms Term[]
             * @param side int
             * @return Term[]
             */
            private Term[] cloneTermArray(Term[] terms, int side) {
                Term[] nterms = new Term[terms.length];
                for (int i = 0; i < nterms.length; i++) {
                    nterms[i] = terms[i].deepCopy(side);

                }
                return nterms;
            }

            /**
             *
             * @param atoms Term[]
             * @param varCount int
             * @param hasVariableNames boolean
             * @param variableNames String[]
             */
            StackFrame(Term[] atoms, int varCount, boolean hasVariableNames,
                       String[] variableNames) {
                this.atoms = cloneTermArray(atoms, 0);

                //System.arraycopy(symbols, 0, this.symbols, 0, symbols.length);
                this.varCount = varCount;
                this.hasVariableNames = hasVariableNames;
                if (hasVariableNames) {
                    this.variableNames = new String[varCount];
                    System.arraycopy(variableNames, 0, this.variableNames, 0,
                                     varCount);
                }
            }
        }


        /**
         *
         * @return String[]
         */
        public String[] getVariableNames() {
            return this.variableNames;
        }
    } //BackwardReasoner.GoalList


    /**
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
    public class SubGoalListIterator implements Iterator {
        public static final int APPLY_TO_GOALLIST = 0;
        public static final int APPLY_TO_GOAL_AND_GOALLIST = 1;
        public static final int APPLY_TO_GOAL = 2;

        /**
         *
         * @param goal Goal
         * @param mode int
         */
        SubGoalListIterator(Goal goal, int mode) {
            this.goal = goal;
            //this.uit = discTree.unifiableIterator(goal.goalList.symbols, goal.literalIndex);

            this.uit = this.getUnifiableIterator(goal.goalList,
                                                 goal.literalIndex);

            this.mode = mode;
            if (mode == APPLY_TO_GOALLIST) {
            } else if (mode == APPLY_TO_GOAL_AND_GOALLIST) {
            } else if (mode == APPLY_TO_GOAL) {
            } else {
                throw new EngineException("SubGoalListIterator mode not valid");
            }
        }

        private int mode;
        //public DiscTree.UnifiableIterator uit;
        public Iterator uit;
        private GoalList nextGoalList;
        private Goal goal;
        private boolean foundNext = false;

        /**
         *
         * @param gl GoalList
         * @param term int
         * @return Iterator
         */
        private Iterator getUnifiableIterator(GoalList gl, int term) {
            Term t = gl.getAtom(term);
            Integer sym = t.getSymbol();
            if (builtins.containsKey(sym)) {
               // logger.debug("Using builtin for " +
                //             SymbolTable.symbol(sym.intValue()));
                TDBuiltin b = (TDBuiltin) builtins.get(sym);
                Vector v = new Vector();
                DefiniteClause dc = b.buildResult(gl, term);
                if (dc != null) {
                    v.add(dc);
                }
                return v.iterator();
            }

            if(!t.subTerms[0].isExpr() && t.subTerms[0].getSymbol() >= 0){
                //logger.debug("Retrieving by oid");
                Integer oid = t.subTerms[0].getSymbol();
                if(oids.containsKey(oid)){
                    //logger.debug("Found oid: " + oid);
                    Vector v = (Vector)oids.get(oid);
                    Vector v2 = (Vector)oids.get(-1);
                    Vector v3 = new Vector();
                    v3.addAll(v);
                    v3.addAll(v2);
                    return v3.iterator();
                }
                else{
                   // logger.debug("Did not find oid: " + oid);
                    Vector v = (Vector)oids.get(-1);
                    return v.iterator();
                }
            }
            else{
               // logger.debug("Finding by symbol");
                if (clauses.containsKey(sym)) {
                    Vector v = (Vector) clauses.get(sym);
                    return v.iterator();
                } else {
                    Vector v = new Vector();
                    return v.iterator();
                }
            }
        }

        /**
         *
         * @return boolean
         */
        public boolean hasNextOld() {      
        	
            if (foundNext) {
                return true;
            } else if (!uit.hasNext()) {
                return false;
            } else {
                nextGoalList = new GoalList((DefiniteClause) uit.next());
                nextGoalList.init();

                Unifier u = new Unifier(goal, nextGoalList,
                                        Unifier.DCTREE_MODE, true);
                try {
                    if (u.unified) {
                        if (mode == APPLY_TO_GOALLIST
                            || mode == APPLY_TO_GOAL_AND_GOALLIST) {
                            u.applyToGoalList();
                            //nextGoalList.setSymbolIndex();
                        }
                        if (mode == APPLY_TO_GOAL_AND_GOALLIST
                            || mode == APPLY_TO_GOAL) {
                            goal.goalList.createBackup();
                            u.applyToGoal();
                            goal.setSymbolIndex();

                        }
                        foundNext = true;
                        return true;
                    } else {
                    	count++;
                    	System.out.println(count);
                        return hasNextOld();
                    }
                } catch (Exception e) {
                    //logger.error(e.getMessage());
                	System.out.println("stack results: ");
                    e.printStackTrace();
                    return false;
                }
            }
        }

        //has next 2
        public boolean hasNext()
        {
           
           if(foundNext) return true;
           
           if(!uit.hasNext()) return false;
           
           while(uit.hasNext())
           {
               nextGoalList = new GoalList( (DefiniteClause) uit.next());
               nextGoalList.init();

               Unifier u = new Unifier(goal, nextGoalList, Unifier.DCTREE_MODE,
        true);

               try
               {
                   if(u.unified) {
                       if(mode == APPLY_TO_GOALLIST || mode ==
        APPLY_TO_GOAL_AND_GOALLIST)
                       {
                           u.applyToGoalList();
                       }
                       if (mode == APPLY_TO_GOAL_AND_GOALLIST || mode ==
        APPLY_TO_GOAL)
                       {
                           goal.goalList.createBackup();
                           u.applyToGoal();
                           goal.setSymbolIndex();
                       }
                       foundNext = true;
                       return true;
                   }

               }
               catch( Exception e ){
            	   return false;   
               }
          }
           return false;
        }
        /**
         *
         * @return Object
         */
        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            foundNext = false;
            //logger.debug("SubGoalListIterator returns next as " + nextGoalList);
            return nextGoalList;
        }

        /**
         *
         * @return int
         */
        public int nextGoalListSize() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return nextGoalList.atomCount;
        }

        /**
         * @throws UnsupportedOperationException
         */
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException
                    ("SubGoalListIterator does not allow remove");
        }
    }
} //BackwardReasoner
