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
 * Implements a String Concatination built-in relation.
 * 
 * Satisfied iff the first argument is equal to the string resulting 
 * from the concatenation of the strings the second argument through 
 * the last argument.
 * 
 * If the first argument is a variable then it will be bound to the 
 * concatenation of the strings the second argument through the last argument.
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
public class StringConcatBuiltin implements Builtin {
    private int symbol = SymbolTable.internSymbol("stringConcat");

    public DefiniteClause buildResult(Term t) {
        if (t.getSymbol() != symbol) {
            return null;
        }

        Vector v = new Vector();
        StringBuffer sb = new StringBuffer();
        String oid = "$jdrew-strconcat";
        for (int i = 2; i < t.subTerms.length; i++) {
            Term ti = t.subTerms[i].deepCopy();
            if (ti.getSymbol() < 0) {
                return null;
            }
            if (ti.getType() != Types.ISTRING) {
                return null;
            }
            String tis = ti.getSymbolString();
            sb.append(tis);
            oid += "-" + tis;
            v.add(ti);
        }

        String results = sb.toString();
        Term tr = new Term(SymbolTable.internSymbol(results),
                           SymbolTable.INOROLE, Types.ISTRING);

        Term roid = new Term(SymbolTable.internSymbol(oid), SymbolTable.IOID,
                             Types.ITHING);
        v.add(0, tr);
        v.add(0, roid);

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
