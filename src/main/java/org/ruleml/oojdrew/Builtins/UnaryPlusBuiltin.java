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
 * Implements a Unary Minus built-in relation.
 *
 * The call format is unaryplus(?result, +input).
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
public class UnaryPlusBuiltin implements Builtin {
    private int symbol = SymbolTable.internSymbol("unaryPlus");

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
        Term roid= new Term(SymbolTable.internSymbol("$jdrew-unaryplus-" + p2s),
                            SymbolTable.IOID, Types.ITHING);

        if (p2.getType() == Types.IINTEGER) {
            long i;
            try {
                i = Long.parseLong(p2s);
            } catch (Exception e) {
                return null;
            }
            //i = i*-1;
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
            //d = d*-1.0;
            String results = "" + d;

        
        //        if (p2.getType() == Types.IINTEGER || p2.getType() == Types.IFLOAT) {
//            double d;
//            try {
//                d = Double.parseDouble(p2s);
//            } catch (Exception e) {
//                return null;
//            }
//
//            String results = "" + (long) (d*-1);
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
