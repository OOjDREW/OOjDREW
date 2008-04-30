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
 * Implements a Substring built in relation.
 *
 * Satisfied iff the first argument is equal to the substring of 
 * optional length the fourth argument starting at character offset
 * the third argument in the string the second argument.
 *
 * If the first argument is a variable then it will be bound to
 * the substring of optional length the fourth argument starting at
 * character offset the third argument in the string the second argument.
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
public class SubstringBuiltin implements Builtin {
    private int symbol = SymbolTable.internSymbol("substring");

    public DefiniteClause buildResult(Term t) {
        if (t.getSymbol() != symbol) {
            return null;
        }

        if (t.subTerms.length == 4) {
            Term p2 = t.subTerms[2].deepCopy();
            Term p3 = t.subTerms[3].deepCopy();

            if (p2.getSymbol() < 0 || p3.getSymbol() < 0) {
                return null;
            }

            if (p2.getType() != Types.ISTRING || p3.getType() != Types.IINTEGER) {
                return null;
            }

            String p2s = p2.getSymbolString();
            String p3s = p3.getSymbolString();
            int p3i;
            try {
                p3i = Integer.parseInt(p3s);
            } catch (Exception e) {
                return null;
            }

            String results = p2s.substring(p3i);

            Term r1 = new Term(SymbolTable.internSymbol(results),
                               SymbolTable.INOROLE, Types.ISTRING);
            Term roid = new Term(SymbolTable.internSymbol("$jdrew-substring-"
                                                          + p2s + "-" + p3s),
                                 SymbolTable.IOID, Types.ITHING);
            Vector v = new Vector();
            v.add(roid);
            v.add(r1);
            v.add(p2);
            v.add(p3);

            Term atm = new Term(symbol, SymbolTable.INOROLE, Types.IOBJECT, v);
            atm.setAtom(true);
            Vector v2 = new Vector();
            v2.add(atm);
            return new DefiniteClause(v2, new Vector());
        } else if (t.subTerms.length == 5) {
            Term p2 = t.subTerms[2].deepCopy();
            Term p3 = t.subTerms[3].deepCopy();
            Term p4 = t.subTerms[4].deepCopy();
            if (p2.getSymbol() < 0 || p3.getSymbol() < 0 || p4.getSymbol() < 0) {
                return null;
            }

            if (p2.getType() != Types.ISTRING || p3.getType() != Types.IINTEGER ||
                p4.getType() != Types.IINTEGER) {
                return null;
            }

            String p2s = p2.getSymbolString();
            String p3s = p3.getSymbolString();
            String p4s = p4.getSymbolString();

            int p3i;
            int p4i;
            try {
                p3i = Integer.parseInt(p3s);
                p4i = Integer.parseInt(p4s);
            } catch (Exception e) {
                return null;
            }

            String results = p2s.substring(p3i, p4i);

            Term r1 = new Term(SymbolTable.internSymbol(results),
                               SymbolTable.INOROLE, Types.ISTRING);
            Term roid = new Term(SymbolTable.internSymbol("$jdrew-substring-" + p2s + "-" + p3s + "-" + p4s),
                                 SymbolTable.IOID, Types.ITHING);
            Vector v = new Vector();
            v.add(roid);
            v.add(r1);
            v.add(p2);
            v.add(p3);
            v.add(p4);

            Term atm = new Term(symbol, SymbolTable.INOROLE, Types.IOBJECT, v);
            atm.setAtom(true);
            Vector v2 = new Vector();
            v2.add(atm);
            return new DefiniteClause(v2, new Vector());
        } else {
            return null;
        }
    }

    public int getSymbol() {
        return symbol;
    }


}
