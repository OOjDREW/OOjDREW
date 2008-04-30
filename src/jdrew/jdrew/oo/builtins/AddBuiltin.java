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
 * Implements a add built-in relation.
 *
 * The call format is add(?result, input1, input2, input3, (can be infinite amount of paramters)).
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
public class AddBuiltin implements Builtin {
    private int sym = SymbolTable.internSymbol("add");

    public DefiniteClause buildResult(Term t) {
    	//System.out.println("Top OF Add");
        if (t.getSymbol() != sym) {
            return null;
        }

        Term r1;

        boolean allint = true;

        double sum = 0;

        Vector v = new Vector();
        String oid = "$jdrew-add";
        for (int i = 2; i < t.subTerms.length; i++) {
            Term ti = t.subTerms[i].deepCopy();
            if (ti.getSymbol() < 0) {
                return null;
            }

            if (ti.getType() != Types.IINTEGER && ti.getType() != Types.IFLOAT) {
                return null;
            }
            if (ti.getType() == Types.IFLOAT) {
                allint = false;
            }


            double d;
            try {
                d = Double.parseDouble(ti.getSymbolString());
            } catch (Exception e) {
                return null;
            }

            oid += "+" + ti.getSymbolString();
            sum += d;
            v.add(ti);
        }

        if (allint) {
            String results = "" + (long) sum;
            r1 = new Term(SymbolTable.internSymbol(results),
                          SymbolTable.INOROLE, Types.IINTEGER);
        } else {
            String results = "" + sum;
            r1 = new Term(SymbolTable.internSymbol(results),
                          SymbolTable.INOROLE, Types.IFLOAT);
        }
        v.add(0, r1);
        v.add(0, new Term(SymbolTable.internSymbol(oid),
                          SymbolTable.IOID, Types.ITHING));


        Term atm = new Term(sym, SymbolTable.INOROLE, Types.IOBJECT, v);
        atm.setAtom(true);
        Vector v2 = new Vector();
        v2.add(atm);
        //System.out.println("In add");
        return new DefiniteClause(v2, new Vector());
    }

    public int getSymbol() {
        return this.sym;
    }
}
