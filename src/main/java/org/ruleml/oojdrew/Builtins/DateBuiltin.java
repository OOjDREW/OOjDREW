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
 *
 * <p>Title: OO jDREW</p>
 *
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.88</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * @author Nicolas Neu
 */
public class DateBuiltin implements Builtin {
    private int symbol = SymbolTable.internSymbol("date");

    public DefiniteClause buildResult(Term t) {
        if (t.getSymbol() != symbol) {
            return null;
        }

        if (t.subTerms.length == 6) {
            Term p2 = t.subTerms[2].deepCopy();
            Term p3 = t.subTerms[3].deepCopy();
            Term p4 = t.subTerms[4].deepCopy();
            Term p5 = t.subTerms[5].deepCopy();

            if (p2.getSymbol() < 0 || p3.getSymbol() < 0 || p4.getSymbol() < 0 || p5.getSymbol() < 0) {
                return null;
            }

            if (p2.getType() != Types.IINTEGER ||  p3.getType() != Types.IINTEGER || p4.getType() != Types.IINTEGER || p5.getType() != Types.ISTRING ){
                return null;
            }
            

            String p2s = p2.getSymbolString();
            String p3s = p3.getSymbolString();
            String p4s = p4.getSymbolString();
            String p5s = p5.getSymbolString();
            
            //negative year
            if(Integer.parseInt(p2s) < 0){
            	return null;
            }
            
            //invalid month
            if(Integer.parseInt(p3s)<0 || Integer.parseInt(p3s)>12){
            	return null;
            }
            
            //invalid day
            if(Integer.parseInt(p4s)<0 || Integer.parseInt(p4s)>31){
            	return null;
            }
            
            //check for proper syntax of timezone
            if(!p5s.matches("[+-][0-1][0-4](:)[0-5][0-9]")){
            	return null;
            }
            
            int h;
            if(p5s.charAt(0)=='+'){
            	h = Integer.parseInt(p5s.split(":")[0].substring(1));
            } else {
            	h = -Integer.parseInt(p5s.split(":")[0]);
            }            int m = Integer.parseInt(p5s.split(":")[1]);
            
            if(h<-12 || h>14 || !(m!=30 || m!=45 || m!=0)){
            	return null;
            }
    
        
        
        String results = p2s + "-" + p3s + "-" + p4s  + p5s;

            Term r1 = new Term(SymbolTable.internSymbol(results),
                               SymbolTable.INOROLE, Types.ISTRING);
            Term roid = new Term(SymbolTable.internSymbol("$jdrew-date-"
                                                          + p2s + "-" + p3s + "-" + p4s + "-" + p5s),
                                 SymbolTable.IOID, Types.ITHING);
            Vector v = new Vector();
            v.add(roid);
            v.add(r1);
            v.add(p2);
            v.add(p3);
            v.add(p4);
            v.add(p5);

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
        return this.symbol;
    }
}
