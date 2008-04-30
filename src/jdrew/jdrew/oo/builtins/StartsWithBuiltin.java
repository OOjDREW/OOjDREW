// OO jDREW Version 0.89
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package jdrew.oo.builtins;

import java.util.*;

import jdrew.oo.util.*;

/**
 * Implements a Starts with built-in relation.
 *
 * Calling format startsWith(?input1, ?input2).
 *
 * Satisfied iff the first argument starts with the second argument. 
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
public class StartsWithBuiltin implements Builtin {
    private int symbol = SymbolTable.internSymbol("startsWith");

    public DefiniteClause buildResult(Term t) {
        if (t.getSymbol() != symbol) {
            return null;
        }

        if (t.subTerms.length != 3) {
            return null;
        }

        Term p1 = t.subTerms[1].deepCopy();
        Term p2 = t.subTerms[2].deepCopy();

        if (p1.getSymbol() < 0 || p1.getSymbol() < 0) {
            return null;
        }

        if (p1.getType() != Types.ISTRING || p2.getType() != Types.ISTRING) {
            return null;
        }

        String p1s = p1.getSymbolString();
        String p2s = p2.getSymbolString();

        if (!p1s.startsWith(p2s)) {
            return null;
        }

        Term roid = new Term(SymbolTable.internSymbol("$jdrew-startswith-"
                                                      + p1s + "-" + p2s),
                             SymbolTable.IOID, Types.ITHING);

        Vector v = new Vector();
        v.add(roid);
        v.add(p1);
        v.add(p2);

        Term atm = new Term(symbol, SymbolTable.INOROLE, Types.IOBJECT, v);
        atm.setAtom(true);
        Vector v2 = new Vector();
        v2.add(atm);
        return new DefiniteClause(v2, new Vector());
    }

    public int getSymbol() {
        return symbol;
    }


}
