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
 *  Satisfied iff the first argument is the xsd:dateTime representation consisting of the year the second argument, 
 *  month the third argument, day the fourth argument, hours the fifth argument, minutes the sixth argument, seconds the seventh argument,
 *   and timezone the eighth argument.
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.88</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * @author Nicolas Neu
 */
public class DateTimeBuiltin implements Builtin {
	private int symbol = SymbolTable.internSymbol("dateTime");

	public DefiniteClause buildResult(Term t) {
		if (t.getSymbol() != symbol) {
			return null;
		}

		if (t.subTerms.length == 9) {
			Term p2 = t.subTerms[2].deepCopy();
			Term p3 = t.subTerms[3].deepCopy();
			Term p4 = t.subTerms[4].deepCopy();
			Term p5 = t.subTerms[5].deepCopy();
			Term p6 = t.subTerms[6].deepCopy();
			Term p7 = t.subTerms[7].deepCopy();
			Term p8 = t.subTerms[8].deepCopy();

			if (p2.getSymbol() < 0 || p3.getSymbol() < 0 || p4.getSymbol() < 0 || p5.getSymbol() < 0 || p6.getSymbol() < 0|| p7.getSymbol() < 0|| p8.getSymbol() < 0) {
				return null;
			}

			if (p2.getType() != Types.IINTEGER ||  p3.getType() != Types.IINTEGER || p4.getType() != Types.IINTEGER || p5.getType() != Types.IINTEGER || p6.getType() != Types.IINTEGER || p7.getType() != Types.IINTEGER || p8.getType() != Types.ISTRING ){
				return null;
			}


			String year = p2.getSymbolString();
			String month = p3.getSymbolString();
			String day = p4.getSymbolString();
			String hours = p5.getSymbolString();
			String minutes = p5.getSymbolString();
			String seconds  = p5.getSymbolString();
			String timezone = p5.getSymbolString();


			//negative year
			if(Integer.parseInt(year) < 0){
				return null;
			}

			//invalid month
			if(Integer.parseInt(month)<0 || Integer.parseInt(month)>12){
				return null;
			}

			//invalid day
			if(Integer.parseInt(day)<0 || Integer.parseInt(day)>31){
				return null;
			}

			//invalid hours
			if(Integer.parseInt(hours) < 0 || Integer.parseInt(hours)>=24){
				return null;
			}

			//invalid minutes
			if(Integer.parseInt(minutes)<0 || Integer.parseInt(minutes)>=60){
				return null;
			}

			//invalid seconds
			if(Integer.parseInt(seconds)<0 || Integer.parseInt(seconds)>=60){
				return null;
			}

			//check for proper syntax of timezone
			if(!timezone.matches("[+-][0-1][0-4](:)[0-5][0-9]")){
				return null;
			}

            int h;
            if(timezone.charAt(0)=='+'){
            	h = Integer.parseInt(timezone.split(":")[0].substring(1));
            } else {
            	h = -Integer.parseInt(timezone.split(":")[0]);
            }			int m = Integer.parseInt(timezone.split(":")[1]);

			if(h<-12 || h>14 || !(m!=30 || m!=45 || m!=0)){
				return null;
			}



			String results = year + "-" + month + "-" + day +"- "+ hours + "-" + minutes + "-" + seconds + timezone;

			Term r1 = new Term(SymbolTable.internSymbol(results),
					SymbolTable.INOROLE, Types.ISTRING);
			Term roid = new Term(SymbolTable.internSymbol("$jdrew-date-"
					+ year + "-" + month + "-" + day + "-" + hours  + "-" + minutes + "-" + seconds + timezone),
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
