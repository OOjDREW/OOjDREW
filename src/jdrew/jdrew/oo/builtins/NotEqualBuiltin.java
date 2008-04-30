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
 * Implements a Contains Equal built in relation.
 * 
 * Calling format notEqual(input1, input2).
 * 
 * Satisfied iff the first argument and the second argument are not the same.
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
public class NotEqualBuiltin implements Builtin {
    private int sym = SymbolTable.internSymbol("notEqual");

    public DefiniteClause buildResult(Term t) {
        if (t.getSymbol() != sym) {
            return null;
        }

        if (t.subTerms.length != 3) {
            return null;
        }

        Term p1 = t.subTerms[1].deepCopy();
        Term p2 = t.subTerms[2].deepCopy();

        if (p1.getSymbol() < 0 || p2.getSymbol() < 0) {
            return null;
        }

        String p1s = p1.getSymbolString();
        String p2s = p2.getSymbolString();

        if ((p1.getType() == Types.IFLOAT || p1.getType() == Types.IINTEGER)
            && (p2.getType() == Types.IFLOAT || p2.getType() == Types.IINTEGER)) {

            double d1;
            double d2;

            try {
                d1 = Double.parseDouble(p1s);
                d2 = Double.parseDouble(p2s);
            } catch (Exception e) {
                return null;
            }

            if (d1 == d2) {
                return null;
            }
        } else {
            if (p1.symbol == p2.symbol) {
                return null;
            }
        }

        Term roid = new Term(SymbolTable.internSymbol("$jdrew-notequal-"
                                                      + p1s + "!=" + p2s),
                             SymbolTable.IOID, Types.ITHING);

        Vector v = new Vector();
        v.add(roid);
        v.add(p1);
        v.add(p2);

        Term atm = new Term(sym, SymbolTable.INOROLE, Types.IOBJECT, v);
        atm.setAtom(true);
        Vector v2 = new Vector();
        v2.add(atm);
        return new DefiniteClause(v2, new Vector());
    }

    public int getSymbol() {
        return this.sym;
    }
}
