// OO jDREW Version 0.89
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package org.ruleml.oojdrew.TopDown.Builtins;

import org.ruleml.oojdrew.TopDown.BackwardReasoner;
import org.ruleml.oojdrew.util.DefiniteClause;

public class TDBuiltin {
      
        /**
         * A reference to the object that implements the generic built-in
         * relation. This object must implement the org.ruleml.oojdrew.builtins.Builtin
         * Interface correctly.
         */
    
    org.ruleml.oojdrew.Builtins.Builtin builtin;
   
    /**
     * Constructs a new empty Top-down built-in. This is provided only for
     * compatability reasons.
     */
    
    public TDBuiltin() {
        builtin = null;
    }

    /**
     * Constructs a new Top-Down built-in. The user should pass an instance of
     * the Class that implements the generic built-in relation.
     *
     * @param b Builtin A generic built-in implementation class, this must
     * implement the org.ruleml.oojdrew.builtins.Builtin Interface.
     */
     
    public TDBuiltin(org.ruleml.oojdrew.Builtins.Builtin b) {
        builtin = b;
    }

    /**
     * Gets the integer symbol code for the built-in relation symbol. This
     * method simply calls the getSymbol() method of the generic built-in
     * implementation that is referenced by this TDBuiltin object.
     *
     * @return int The integer code for the built-in relation symbol
     */
    public int getSymbol() {
        return builtin.getSymbol();
    }

    /**
     * Builds the resulting fact from the call to this built-in if it exists.
     * This method extracts the appropriate Term object that represents the call
     * to the built-in relation and passes to the generic built-in's
     * builtResult() method.
     *
     * @param gl GoalList the goal list that contains the call to the
     * built-in
     *
     * @param term int The index into the atoms of the clause for the call to
     * the built-in relation represented by this TDBuiltin object.
     *
     * @return DefiniteClause a DC containing a fact that will unify with the call
     * to the built-in if it should succeed. null or a fact that will not unify
     * (null is preferred as it is more efficient) should be returned if the
     * call to built-in relation should not succeed.
     */
     
    public DefiniteClause buildResult(BackwardReasoner.GoalList gl, int term) {
        //System.out.println("In TDBuiltin...");
        return builtin.buildResult(gl.getAtom(term));
    }

}
