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
 * Implements a divide (division) built-in relation.
 *
 * The call format is abs(?result, dividend, divisor).
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
public class DivideBuiltin implements Builtin {
    private int sym = SymbolTable.internSymbol("divide");

    public DefiniteClause buildResult(Term t) {
        if (t.getSymbol() != sym) {
            return null;
        }

        if (t.subTerms.length != 4) {
            return null;
        }

        Term p2 = t.subTerms[2].deepCopy();
        Term p3 = t.subTerms[3].deepCopy();

        if (p2.getSymbol() < 0 || p3.getSymbol() < 0) {
            return null;
        }

        String p2s = p2.getSymbolString();
        String p3s = p3.getSymbolString();
        Term r1;

        if (p2.getType() == Types.IINTEGER && p3.getType() == Types.IINTEGER) {
            long p2i;
            long p3i;
            try {
                p2i = Long.parseLong(p2s);
                p3i = Long.parseLong(p3s);
            } catch (Exception e) {
                return null;
            }
            String results = "" + ((double) (p2i) / p3i);
            r1 = new Term(SymbolTable.internSymbol(results),
                          SymbolTable.INOROLE, Types.IFLOAT);
        } else if ((p2.getType() == Types.IFLOAT &&
                    p3.getType() == Types.IINTEGER) ||
                   (p2.getType() == Types.IINTEGER &&
                    p3.getType() == Types.IFLOAT) ||
                   (p2.getType() == Types.IFLOAT &&
                    p3.getType() == Types.IFLOAT)) {
            double p2d;
            double p3d;
            try {
                p2d = Double.parseDouble(p2s);
                p3d = Double.parseDouble(p3s);
            } catch (Exception e) {
                return null;
            }
            String results = "" + (p2d / p3d);
            r1 = new Term(SymbolTable.internSymbol(results),
                          SymbolTable.INOROLE, Types.IFLOAT);
        } else {
            return null;
        }

        Term roid = new Term(SymbolTable.internSymbol("$jdrew-divide-"
                                                      + p2s + "/" + p3s),
                             SymbolTable.IOID, Types.ITHING);

        Vector v = new Vector();
        v.add(roid);
        v.add(r1);
        v.add(p2);
        v.add(p3);

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
