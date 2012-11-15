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
 * Implements a Sub String Before built-in relation.
 *
 * The call format is substringbefore(?result, input1,input2).
 *
 * <p>Title: OO jDREW</p>
 *
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.88</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 */
public class SubstringAfterBuiltin implements Builtin {
    private int symbol = SymbolTable.internSymbol("substringafter");

    public DefiniteClause buildResult(Term t) {
        if (t.getSymbol() != symbol) {
            return null;
        }

        
        if (t.subTerms.length != 4) {
            return null;
        }
        
        Vector v = new Vector();

        Term p2 = t.subTerms[2].deepCopy();
        Term p3 = t.subTerms[3].deepCopy();

        if (p2.getSymbol() < 0||p3.getSymbol()<0) {
            return null;
        }
        
        if (p2.getType() != Types.ISTRING || p3.getType() != Types.ISTRING) {
            return null;
        }
        

        String p2s = p2.getSymbolString();
        String p3s = p3.getSymbolString();
        v.add(p2);
        v.add(p3);
        String results = p2s.contains(p3s) ? p2s.substring( p2s.indexOf(p3s)+p3s.length(),p2s.length()):p2s.substring(p2s.length()) ;
        
        if(results.equals("")) {
        	return null;
        }
        
        Term tr = new Term(SymbolTable.internSymbol(results),
                SymbolTable.INOROLE, Types.ISTRING);

        Term roid = new Term(SymbolTable.internSymbol("$jdrew-substringafter-" + p3s),
              SymbolTable.IOID, Types.ITHING);
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
