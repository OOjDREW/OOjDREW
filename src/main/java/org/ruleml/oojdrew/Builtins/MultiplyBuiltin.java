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

package org.ruleml.oojdrew.Builtins;

import java.util.Vector;

import org.ruleml.oojdrew.util.DefiniteClause;
import org.ruleml.oojdrew.util.SymbolTable;
import org.ruleml.oojdrew.util.Term;
import org.ruleml.oojdrew.util.Types;

/**
 * Implements a Multiply built-in relation.
 *
 * The call format is multiply(?result, input1, input2, input3, (can be infinite amount of paramters)).
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
public class MultiplyBuiltin implements Builtin {
    private int symbol = SymbolTable.internSymbol("multiply");

    public DefiniteClause buildResult(Term t) {
        if (t.getSymbol() != symbol) {
            return null;
        }

        Term r1;

        boolean allint = true;

        double sum = 0;

        Vector v = new Vector();
        String oid = "$jdrew-mul-";
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
                //System.out.print(d);
            } catch (Exception e) {
                return null;
            }
            if (i == 2) {
                sum = d;
                oid += d;
            } else {
                sum *= d;
                oid += "x" + d;
            }

            v.add(ti);
        }

        //System.out.println("Sum: " + sum);

        if (allint) {
            String results = "" + (long) sum;
            r1 = new Term(SymbolTable.internSymbol(results),
                          SymbolTable.INOROLE, Types.IINTEGER);
        } else {
            String results = "" + sum;
            r1 = new Term(SymbolTable.internSymbol(results),
                          SymbolTable.INOROLE, Types.IFLOAT);
        }

        Term roid = new Term(SymbolTable.internSymbol(oid), SymbolTable.IOID,
                             Types.ITHING);

        v.add(0, r1);
        v.add(0, roid);

        Term atm = new Term(symbol, SymbolTable.INOROLE, Types.IOBJECT, v);
        atm.setAtom(true);
        Vector v2 = new Vector();
        v2.add(atm);
        return new DefiniteClause(v2, new Vector());
    }

    public int getSymbol() {
        return this.symbol;
    }
}
