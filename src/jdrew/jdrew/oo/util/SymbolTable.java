// OO jDREW Version 0.93
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package jdrew.oo.util;

import java.util.*;

/**
 * This class is responsible for managing the integer codes that are used by
 * the engine datastructers to represent roles and symbols. Integer codes are
 * used as these values are  compared frequently and integer comparisons are
 * consierably more efficent than string comparisons.
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
public class SymbolTable {

    /**
     * This vector stores all known symbols. The index of the symbol within
     * this vector is the integer code that is used to represent this symbol.
     */
    public static Vector symbols = new Vector();

    /**
     * This vector stores all known roles. The index of the role within this
     * vector is the integer code that is used to represent this role.
     */
    public static Vector roles = new Vector();

    /**
     * This is used to generate new Skolem constants; ensuring uniqueness of
     * all generated constants.
     */
    public static int genid = 100000;

    /**
     * This integer stores the integer code that is used for the system oid
     * role.
     */
    public static final int IOID = internRole("$oid$");

    /**
     * This integer stores the integer code that is used for the role of
     * positional parameters.
     */
    public static final int INOROLE = internRole("");

    /**
     * This integer stores the integer code that is used for the role of
     * positional rest parameters.
     */
    public static final int IPREST = internRole("|");

    /**
     * This integer stores the integer code that is used for the role of
     * slotted rest parameters. This is set equal to Integer.MAX_VALUE to
     * ensure that it will always be the final parameter in the normalized
     * parameter lists.
     */
    public static final int IREST = Integer.MAX_VALUE;

    /**
     * This symbol takes up the first slot in the symbol table - as we ensure
     * that the integer code 0 is never used. This is not else where.
     */
    public static final int INOSYM = internSymbol("");

    /**
     * This integer stores the code for a NAF term.
     */
    public static final int INAF = internSymbol("naf");

    /**
     * This integer stores the code for the "constructor" of a plex term.
     */
    public static final int IPLEX = internSymbol("$PLEX");

    /**
     * This integer stores the code for an assert term.
     */
    public static final int IASSERT = internSymbol("assert");

    /**
     *
     */
    public static final int IINCONSISTENT = internSymbol("$inconsistent");

    /**
     *
     */
    public static final int INEG = internSymbol("neg");

    /**
     * This method is used to reset the symbol table. Data structures that
     * were created previous to this should no longer be used.
     */
    public static void reset() {
        symbols = new Vector();
        roles = new Vector();
        internRole("$oid$");
        internRole("");
        internRole("|");
        internSymbol("");
        internSymbol("naf");
        internSymbol("$PLEX");
        internSymbol("assert");
        internSymbol("$inconsistent");
        internSymbol("neg");
    }

    /**
     * This method is used to initalize a symbol and assign it an integer code
     * if it has not been used before; or retrieve the integer code of a symbol
     * if it already has been encountered.
     *
     * @param sym String A string containing the symbol.
     *
     * @return int The integer code of the symbol.
     */
     
    public static int internSymbol(String sym) {
        if (symbols.contains(sym)) {
            return symbols.indexOf(sym);
        } else {
            int id = symbols.size();
            symbols.add(sym);
            return id;
        }
    }

    /**
     * This method is used to initalize a role and assign it an integer code if
     * it has not been used before; or retrieve the integer code of a role if
     * it already has been encountered.
     *
     * @param role String A string containing the role name.
     *
     * @return int The integer code of the role.
     */
    public static int internRole(String role) {
        if (roles.contains(role)) {
            return roles.indexOf(role);
        } else {
            int id = roles.size();
            roles.add(role);
            return id;
        }
    }

    /**
     * This method gets the string representation of a symbol for the integer
     * code. This is commonly used by output routines.
     *
     * @param idx int The integer code to get the symbol string for.
     *
     * @return String The symbol string.
     */
    public static String symbol(int idx) {
        if (idx < 0 || idx >= symbols.size()) {
            return null;
        } else {
            return (String) symbols.get(idx);
        }
    }

    /**
     * This method gets the string representation of a role for the integer
     * code. This is commonly used by output routines.
     *
     * @param idx int The integer code to get the role string for.
     *
     * @return String The role string.
     */
    public static String role(int idx) {
        if (idx < 0 || idx >= roles.size()) {
            return null;
        } else {
            return (String) roles.get(idx);
        }
    }
}
