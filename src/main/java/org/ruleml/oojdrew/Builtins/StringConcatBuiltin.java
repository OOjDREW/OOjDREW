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
