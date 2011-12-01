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

package org.ruleml.oojdrew.TopDown.Builtins;

import java.util.Hashtable;
import java.util.Vector;

import org.ruleml.oojdrew.TopDown.BackwardReasoner;
import org.ruleml.oojdrew.util.DefiniteClause;
import org.ruleml.oojdrew.util.SymbolTable;
import org.ruleml.oojdrew.util.Term;
import org.ruleml.oojdrew.util.Types;

/**
 * <p>The class implements a "builtin" for registering a new clause into the
 * running knowledge base. While this would not normally be considered a
 * built-in relation it is implement as such to provide for easy
 * implementation. </p>
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
public class RegisterBuiltin extends TDBuiltin {
    private BackwardReasoner br;
    private int sym;
   
    /**
     * Constructs a new object to implement the register built-in relation. This
     * constructor must be passed a reference to the backward reasoner object
     * that the built-in will be registered in.
     *
     * @param br BackwardReasoner - The backward reasoner engine that will
     * register this built-in relationship.
     *
     * @param symbol String - the symbol string
     */
        public RegisterBuiltin(BackwardReasoner br, String symbol){
        this.br = br;
        sym = SymbolTable.internSymbol(symbol);
    }
    /**
     * Access the symbol code for the assert built-in relation.
     *
     * @return int The symbol code for the assert built-in relation.
     */
    public int getSymbol(){
        return sym;
    }
   
    /**
     * This method performs the actions necessary to implement the register
     * built-in relation. This will assert the clause that is the parameter to
     * the assert into the running knowledge base and cause the goal to succeed.
     *
     * @param gl GoalList goal list that contains the call to the assert
     * built-in relation.
     *
     * @param term int An index into the atoms (array) of the clause (0 is the
     * head, i = 1..n is the ith atom of the body of the clause) to the atom
     * that is the call to the assert built-in relation.
     *
     * @return DefiniteClause A clause that will successfully unify with the call
     * to the built-in if it should succeed. null or a fact that will not unify
     * (null is preferred as it is more efficient) should be returned if the
     * call to built-in relation should not succeed.
     */
    public DefiniteClause buildResult(BackwardReasoner.GoalList gl, int term) {
        Term t = gl.getAtom(term);

        if(t.getSymbol() != sym)
            return null;

        if(t.subTerms.length != 2)
            return null;

        Term p1 = t.subTerms[1].deepCopy();

        if(p1.getSymbol() < 0 || p1.isExpr())
            return null;

        String cname = p1.getSymbolString();

        try{
            Class c = Class.forName(cname);
            Object b = c.newInstance();
            if(b instanceof org.ruleml.oojdrew.Builtins.Builtin){ // handle generic Builtin subclass
                org.ruleml.oojdrew.Builtins.Builtin bb = (org.ruleml.oojdrew.Builtins.Builtin) b;
                Hashtable bins = br.getBuiltins();
                Integer sym = bb.getSymbol();
                if(bins.containsKey(sym)){
                    Object o = bins.get(sym);
                    if(!bb.getClass().equals(o.getClass()))
                        return null; //something else is already registered - fail
                }
                else
                    br.registerBuiltin(bb); //register built-in
            }
            else if(b instanceof org.ruleml.oojdrew.TopDown.Builtins.TDBuiltin){ //handle TDBuiltin subclass
                org.ruleml.oojdrew.TopDown.Builtins.TDBuiltin bb = (org.ruleml.oojdrew.TopDown.Builtins.TDBuiltin) b;
                Hashtable bins = br.getBuiltins();
                Integer sym = bb.getSymbol();
                if(bins.containsKey(sym)){
                    Object o = bins.get(sym);
                    if(!bb.getClass().equals(o.getClass()))
                        return null; //something else is already registered - fail
                }
                else
                    br.registerBuiltin(bb); //register built-in
            }
            else
                return null;
        }catch(Exception e){
            return null;
        }

        Term roid = new Term(SymbolTable.internSymbol("$jdrew-rb-" + cname),
                             SymbolTable.IOID, Types.ITHING);
        Vector v = new Vector();
        v.add(roid);
        v.add(p1);

        Term atom = new Term(sym, SymbolTable.INOROLE, Types.ITHING, v);
        Vector v2 = new Vector();
        v2.add(atom);

        return new DefiniteClause(v2, new Vector());
    }

}
