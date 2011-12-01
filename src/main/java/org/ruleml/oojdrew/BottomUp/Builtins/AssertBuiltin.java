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

package org.ruleml.oojdrew.BottomUp.Builtins;

import java.util.Vector;

import org.ruleml.oojdrew.BottomUp.ForwardReasoner;
import org.ruleml.oojdrew.util.DefiniteClause;
import org.ruleml.oojdrew.util.SymbolTable;
import org.ruleml.oojdrew.util.Term;

/**
 * <p>The class implements a "builtin" for asserting a new clause into the
 * running knowledge base. While this would not normally be considered a
 * built-in relation it is implement as such to provide for easy
 * implementation. </p>
 *
 * <p>This built-in extends the jdrew.oo.bu.builtins.BUBuiltin Class, instead
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
public class AssertBuiltin extends BUBuiltin {
    private ForwardReasoner fr;
    private int symbol = SymbolTable.internSymbol("assert");

    /**
     * Constructs a new object to implement the assert built-in relation. This
     * constructor must be passed a reference to the ForwardReasoner object
     * that the built-in will be registered in.
     *
     * @param fr ForwardReasoner - The forward reasoner engine that will
     * register this built-in relationship.
     */
    public AssertBuiltin(ForwardReasoner fr) {
        super();
        this.fr = fr;
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
     * @param dc DefiniteClause The clause that contains the call to the assert
     * built-in relation.
     *
     * @param term int An index into the atoms (array) of the clause (0 is the
     * head, i = 1..n is the ith atom of the body of the clause) to the atom
     * that is the call to the assert built-in relation.
     *
     * @return DefiniteClause A clause that will successfully unify with the
     * call to the built-in if it should succeed. null or a fact that will not unify
     * (null is preferred as it is more efficient) should be returned if the
     * call to built-in relation should not succeed.
     */
    public Vector buildResult(DefiniteClause dc, int term) {
        Term t = dc.atoms[term];
        Vector natoms = new Vector();
        for (int i = 0; i < t.subTerms.length; i++) {
            Term t2 = t.subTerms[i].deepCopy();
            t2.setAtom(true);
            natoms.add(t2);
        }
        Vector vnames = new Vector();
        for (int i = 0; i < dc.variableNames.length; i++) {
            vnames.add(dc.variableNames[i]);
        }
        DefiniteClause newdc = new DefiniteClause(natoms, vnames);
        // Build a DefiniteClause object to represent the new clause to be
        // asserted

        Vector v = new Vector();
        v.add(newdc);
        fr.loadClauses(v.iterator());
        // load the new clause into the running knowledge base

        Vector v2 = new Vector();
        Term t2 = t.deepCopy();
        v = new Vector();
        v.add(t2);
        v2.add(new DefiniteClause(v, vnames));
        return v2;
        //create and return a Fact that will unify with the call to the assert
        //built-in realtion
    }
}
