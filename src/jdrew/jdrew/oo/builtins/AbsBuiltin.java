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
 * Implements a abs (absolute value) built-in relation.
 *
 * The call format is abs(?result, +input).
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
public class AbsBuiltin implements Builtin {
    private int symbol = SymbolTable.internSymbol("abs");

    public DefiniteClause buildResult(Term t) {
        if (t.getSymbol() != symbol) {
            return null;
        }

        if (t.subTerms.length != 3) {
            return null;
        }

        Term p2 = t.subTerms[2].deepCopy();

        if (p2.getSymbol() < 0) {
            return null;
        }

        String p2s = p2.getSymbolString();
        Term r1;
        Term roid= new Term(SymbolTable.internSymbol("$jdrew-abs-" + p2s),
                            SymbolTable.IOID, Types.ITHING);

        if (p2.getType() == Types.IINTEGER) {
            long i;
            try {
                i = Long.parseLong(p2s);
            } catch (Exception e) {
                return null;
            }
            i = Math.abs(i);
            String results = "" + i;
            r1 = new Term(SymbolTable.internSymbol(results),
                          SymbolTable.INOROLE, Types.IINTEGER);
        } else if (p2.getType() == Types.IFLOAT) {
            double d;
            try {
                d = Double.parseDouble(p2s);
            } catch (Exception e) {
                return null;
            }
            d = Math.abs(d);
            String results = "" + d;
            r1 = new Term(SymbolTable.internSymbol(results),
                          SymbolTable.INOROLE, Types.IFLOAT);
        } else {
            return null;
        }

        Vector v = new Vector();
        v.add(roid);
        v.add(r1);
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
