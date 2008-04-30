// OO jDREW Version 0.89
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package jdrew.oo.td.builtins;

import java.util.*;
import jdrew.oo.td.*;
import jdrew.oo.util.*;

/**
 * <p>The class implements a "builtin" for asserting a new clause into the
 * running knowledge base. While this would not normally be considered a
 * built-in relation it is implement as such to provide for easy
 * implementation. </p>
 *
 * <p>This built-in extends the jdrew.oo.td.builtins.TDBuiltin Class, instead
 * of implementing the jdrew.oo.builtins.Builtin Interface as it requires access
 * to data structures that are not available to it when implementing a regular
 * built-in and also it does not work across both bottom-up and top-down, but is
 * instead specific to bottom-up.</p>
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
public class AssertBuiltin extends TDBuiltin {
    private BackwardReasoner br;
    private int symbol = SymbolTable.internSymbol("assert");

    /**
     * Constructs a new object to implement the assert built-in relation. This
     * constructor must be passed a reference to the backward reasoner object
     * that the built-in will be registered in.
     *
     * @param br BackwardReasoner - The backward reasoner engine that will
     * register this built-in relationship.
     */
    public AssertBuiltin(BackwardReasoner br) {
        super();
        this.br = br;
    }

    /**
     * Access the symbol code for the assert built-in relation.
     *
     * @return int The symbol code for the assert built-in relation.
     */
    public int getSymbol() {
        return symbol;
    }

    /**
     * This method performs the actions necessary to implement the assert
     * built-in relation. This will assert the clause that is the parameter to
     * the assert into the running knowledge base and cause the goal to succeed.
     *
     * @param gl GoalList goal list that contains the call to the assert
     * built-in relation.
     *
     * @param term int An index into the atoms (array) of the clause (0 is the
     * head, i = 1..n is the ith atom of the body of the clause) to the atom
     * that is the call to the assert built-in relation.
     *
     * @return DefiniteClause A clause that will successfully unify with the call
     * to the built-in if it should succeed. null or a fact that will not unify
     * (null is preferred as it is more efficient) should be returned if the
     * call to built-in relation should not succeed.
     */
    public DefiniteClause buildResult(BackwardReasoner.GoalList gl, int term) {
        Term t = gl.getAtom(term);
        Vector natoms = new Vector();
        for (int i = 0; i < t.subTerms.length; i++) {
            Term t2 = t.subTerms[i].deepCopy();
            t2.setAtom(true);
            natoms.add(t2);
        }
        String[] variableNames = gl.getVariableNames();
        Vector vnames = new Vector();
        for (int i = 0; i < variableNames.length; i++) {
            vnames.add(variableNames[i]);
        }
        DefiniteClause newdc = new DefiniteClause(natoms, vnames);
        Vector v = new Vector();
        v.add(newdc);
        br.loadClauses(v.iterator());

        Term t2 = t.deepCopy();
        v = new Vector();
        v.add(t2);
        return new DefiniteClause(v, vnames);
    }

}
