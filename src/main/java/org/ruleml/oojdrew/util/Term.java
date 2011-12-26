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

package org.ruleml.oojdrew.util;

import java.io.ByteArrayOutputStream;
import java.util.Vector;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.ruleml.oojdrew.Config;
import org.ruleml.oojdrew.parsing.RuleMLFormat;
import org.ruleml.oojdrew.parsing.RuleMLTagNames;

/**
 * An object that represents a logic term (Ind, Var, Cterm Plex, Atom). For a
 * simple term (ind or variable) the subTerms array is set to null, for
 * non-simple terms (atoms, complex terms, plexs) the subTerms array contains
 * the parameters for that term.
 * 
 * <p>
 * Title: OO jDREW
 * </p>
 * 
 * <p>
 * Description: A deductive reasoning engine for Object-Oriented Knowledge in OO
 * RuleML
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * @author Marcel Ball
 * @version 0.89
 */
public class Term implements Comparable {

    /**
     * This is a regular expression to test against to see if a symbol needs to
     * be quoted when producing POSL format output.
     */
    public static final String regex = "^(-)?[a-zA-Z0-9\\$][a-zA-Z0-9_\\$\\.]*$";

    /**
     * The symbol code to be used for this term, for a variable this is a
     * negative number - and the variable name is stored in the variableNames
     * array of the DefiniteClause object, for a ind this is a positive number
     * and the symbol text is stored in the SymbolTable.
     */

    public int symbol;

    /**
     * The role name code to be used for this term, for a positional argument
     * this is equal to the INOROLE member of the SymbolTable. For slotted
     * arguments the name of the role is stored in the SymbolTable.
     */
    public int role;

    /**
     * The type code to be used for this term, the types for the engine are
     * defined by an RDFS file, and are parsed by the RDFSParser object into the
     * Types system.
     */
    public int type;

    /**
     * The symbol code for the URI for this term; The actual URI is stored in
     * the SymbolTable.
     */
    public int href = -1;

    /**
     * subTerms Term[] An array containing the terms that will be the parameters
     * of this atom, cterm or plex that is being created.
     */
    public Term[] subTerms = null;

    /**
     * This is used to tell if the Term is an atom or not.
     */
    public boolean atom = false;

    /**
     * -1 is assigned as the rest if this is a simple term or a complex term
     * with no slotted rest parameter.
     */
    public int rest = -1;

    /**
     * -1 is assigned as the positional rest if this is a simple term or a
     * complex term with no positional slotted rest parameter.
     */
    public int prest = -1;

    /**
     * Used by the unification process to check what clause a term belongs to
     * when doing unification. This is not used elsewhere.
     */
    int side;

    /**
     * This is used to distinguish between Data and Ind
     */
    private boolean isData = false;

    /**
     * This is used to distinguish between Data and Ind, when using slots
     */
    private boolean dataSlot = false;

    /**
     * Creates a new simple term (variable or ind).
     * 
     * @param symbol
     *            The symbol code to be used for this term, for a variable this
     *            is a negative number - and the variable name is stored in the
     *            variableNames array of the DefiniteClause object, for a ind
     *            this is a positive number and the symbol text is stored in the
     *            SymbolTable.
     * 
     * @param role
     *            The role name code to be used for this term, for a positional
     *            argument this is equal to the INOROLE member of the
     *            SymbolTable. For slotted arguments the name of the role is
     *            stored in the SymbolTable.
     * 
     * @param type
     *            The type code to be used for this term, the types for the
     *            engine are defined by an RDFS file, and are parsed by the
     *            RDFSParser object into the Types system.
     */
    public Term(int symbol, int role, int type) {
        this.symbol = symbol;
        this.role = role;
        this.type = type;
    }

    /**
     * Create a new simple term with a URI reference or label (must be ind)
     * 
     * @param symbol
     *            The symbol code to be used for this term, for a variable this
     *            is a negative number - and the variable name is stored in the
     *            variableNames array of the DefiniteClause object, for a ind
     *            this is a positive number and the symbol text is stored in the
     *            SymbolTable.
     * 
     * @param role
     *            The role name code to be used for this term, for a positional
     *            argument this is equal to the INOROLE member of the
     *            SymbolTable. For a slotted parameter the role name is stored
     *            in the SymbolTable.
     * 
     * @param type
     *            The type code to be used for this term, the types for the
     *            engine are defined by an RDFS file, and are parsed by the
     *            RDFSParser object into the Types system.
     * 
     * @param href
     *            The symbol code for the URI for this term; The actual URI is
     *            stored in the SymbolTable.
     */
    public Term(int symbol, int role, int type, int href) {
        this.symbol = symbol;
        this.role = role;
        this.type = type;
        this.href = href;
    }

    /**
     * Creates a new complex term (atom, expr, plex).
     * 
     * @param symbol
     *            The symbol code to be used for the relation or constructor of
     *            this term, this is always a positive number and the symbol
     *            text is stored in the SymbolTable.
     * 
     * @param role
     *            The role name code to be used for this term, for a positional
     *            argument this is equal to the INOROLE member of the
     *            SymbolTable. For a slotted parameter the role name is stored
     *            in the SymbolTable.
     * 
     * @param type
     *            The type code to be used for this term, the types for the
     *            engine are defined by an RDFS file, and are parsed by the
     *            RDFSParser object into the Types system.
     * 
     * @param subTerms
     *            An array containing the terms that will be the parameters of
     *            this atom, expr or plex that is being created.
     */
    public Term(int symbol, int role, int type, Term[] subTerms) {
        this(symbol, role, type);
        Vector st = this.sort(subTerms);
        this.subTerms = new Term[st.size()];
        for (int i = 0; i < st.size(); i++) {
            this.subTerms[i] = (Term) st.get(i);
            if (this.subTerms[i].getRole() == SymbolTable.IREST) {
                rest = i;
            }
            if (this.subTerms[i].getRole() == SymbolTable.IPREST) {
                prest = i;
            }
        }
    }

    /**
     * Creates a new complex term (atom, expr, plex).
     * 
     * @param symbol
     *            The symbol code to be used for the relation or constructor of
     *            this term, this is always a positive number and the symbol
     *            text is stored in the SymbolTable.
     * 
     * @param role
     *            The role name code to be used for this term, for a positional
     *            argument this is equal to the INOROLE member of the
     *            SymbolTable. For a slotted parameter the role name is stored
     *            in the SymbolTable.
     * 
     * @param type
     *            The type code to be used for this term, the types for the
     *            engine are defined by an RDFS file, and are parsed by the
     *            RDFSParser object into the Types system.
     * 
     * @param subTerms
     *            Vector value An array containing the terms that will be the
     *            parameters of this atom, expr or plex that is being created.
     */
    public Term(int symbol, int role, int type, Vector subTerms) {
        this(symbol, role, type);
        Vector st = this.sort(subTerms);
        this.subTerms = new Term[st.size()];
        for (int i = 0; i < st.size(); i++) {
            this.subTerms[i] = (Term) st.get(i);
            if (this.subTerms[i].getRole() == SymbolTable.IREST) {
                rest = i;
            }
            if (this.subTerms[i].getRole() == SymbolTable.IPREST) {
                prest = i;
            }
        }
    }

    /**
     * A method to get the symbol for a role within a complex term. If the r
     * value is "" this will return the first positional argument. Also if there
     * is more than one value of a role name this will return the first one.
     * 
     * @param r
     *            String A string containing the name of the role to retrieve
     *            the symbol for.
     * 
     * @return A string containing the symbol associated with the passed role.
     *         If this is a variable it will be returned as ?Varx, were x is the
     *         variable id, for for complex term this will contain the
     *         constructor symbol. Values are not enclosed within " even if this
     *         would be required for POSL Syntax.
     * 
     */
    public String getSymbolForRole(String r) {
        int role = SymbolTable.internRole(r);
        for (int i = 0; i < this.subTerms.length; i++) {
            if (subTerms[i].getRole() == role) {
                return subTerms[i].getSymbolString();
            }
        }
        return null;
    }

    /**
     * Gets the symbol code for this term. If it is a complex term this gives
     * the symbol code for the predicate/constructor. If this value is a
     * negative number then this term is a variable.
     * 
     * @return The symbol code for this term.
     */
    public int getSymbol() {
        return symbol;
    }

    /**
     * Sets the symbol code for this term.
     * 
     * @param symbol
     *            The value to set symbol to.
     */
    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }

    /**
     * Gets the symbol for this term. If it is a complex term this gives the
     * symbol for the predicate/constructor. If it is a variable then the symbol
     * is prefixed with a ?.
     * 
     * @return The symbol for this term.
     */
    public String getSymbolString() {
        if (symbol > 0) {
            return SymbolTable.symbol(symbol);
        } else {
            return "?Var" + (-(symbol + 1));
        }
    }

    /**
     * Gets the role name code for this term. If this is a rest paramater it
     * will be equal to the IREST member of the associated SymbolTable. If this
     * is a positional argument it will be equal to the INOROLE member of the
     * associated SymbolTable.
     * 
     * @return The role code for this term.
     */
    public int getRole() {
        return role;
    }

    /**
     * Sets the role code for this term.
     * 
     * @param role
     *            The value to set role to.
     */
    public void setRole(int role) {
        this.role = role;
    }

    /**
     * Gets the role name for this term. If this is a positional argument this
     * will be an empty string, for a rest parameter it will be '$REST',
     * otherwise it is the name of the role.
     * 
     * @return The role name for this term.
     */
    public String getRoleString() {
        return SymbolTable.role(role);
    }

    /**
     * Gets the type code for this term. For an untyped term this will be equal
     * to the IOBJECT member of the TypeHierarchy associated with the clause.
     * 
     * @return The type code for this term.
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the type code for this term.
     * 
     * @param type
     *            The value to set type to.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Gets the value of the side member variable - this is used by the
     * unification process to keep track of terms.
     * 
     * @return The current value of the side member.
     */
    public int getSide() {
        return side;
    }

    /**
     * Sets the value of the side member variable.
     * 
     * @param side
     *            The value to set side to.
     */
    public void setSide(int side) {
        this.side = side;
    }

    /**
     * Gets the index of the slotted rest parameter.
     * 
     * @return The index of the slotted rest parameter within this term, or -1
     *         if this is a simple term or a complex term with no slotted rest
     *         parameter.
     */
    public int getRest() {
        return rest;
    }

    /**
     * Gets the index of the positional rest parameter.
     * 
     * @return The index of the position rest parameter within this term, or -1
     *         if this is a simple term or a complex term with no positional
     *         rest parameter.
     */
    public int getPosRest() {
        return prest;
    }

    /**
     * Gets an array of the arguments for this term.
     * 
     * @return A Term[] array of the parameters for this term. Returns null if
     *         this is a simple term (variable or ind).
     */
    public Term[] getSubTerms() {
        return subTerms;
    }

    /**
     * Tests if a term is a complex term.
     * 
     * @return True if this term is a complex term (atom, expr, tup) false
     *         otherwise.
     */
    public boolean isExpr() {
        return (subTerms != null);
    }

    /**
     * Test if this term is an atom.
     * 
     * @return boolean value - true if this term is an atom, false otherwise.
     *         This is used only by the output routines, for other things Expr
     *         and Atoms can be treated the same.
     */
    public boolean isAtom() {
        return (subTerms != null && atom);
    }

    /**
     * Sets the boolean atom flag to the passed value, if the atom member
     * variable is set to true and it is not a complex term (subTerms array is
     * set to null) isAtom() will still return false.
     * 
     * @param atom
     *            The value that the atom member should be set to.
     */
    public void setAtom(boolean atom) {
        this.atom = atom;
    }

    /**
     * Sets the isData flag to the passed value, if isData variable is set to
     * true then it is a data element if not it is a Ind
     * 
     * @param data
     *            The value that the isData member should be set to.
     */
    public void setData(boolean data) {
        isData = data;
    }

    /**
     * Gets the isData vaule
     * 
     * @return The value of the isData member
     */
    public boolean getData() {
        return isData;
    }

    /**
     * Sets the isDataSlot flag to the passed value, if isDataSlot variable is
     * set to true then it is a data element if not it is a Ind inside the slot.
     * 
     * @param data
     *            The value that the isDataSlot member should be set to.
     */
    public void setDataSlot(boolean data) {
        dataSlot = data;
    }

    /**
     * Gets the isDataSlot value
     * 
     * @return The value that the isDataSlot member
     */
    public boolean getDataSlot() {
        return dataSlot;
    }

    /**
     * Returns a string representation of this term. The format of this string
     * is determined by the value of the PRPRINT static variable in the
     * org.ruleml.oojdrew.Config class. If this is true, this will produce a
     * string in POSL syntax, otherwise it will produce a string in OO RuleML
     * XML syntax.
     * 
     * This method is used when you do not have access to the variable names;
     * variables are output as ?Varx - where x is the variable id.
     * 
     * @param rmlFormat
     *            RuleMLFormat, to determine which RuleML version to use.
     * 
     * @return The String representation of this term.
     */
    public String toString(RuleMLFormat rmlFormat) {
        if (Config.PRPRINT) {
            return this.toPOSLString(true);
        } else {

            return this.toRuleMLString(rmlFormat);
        }
    }

    /**
     * Returns a string representation of this term. The format of this string
     * is determined by the value of the PRPRINT static variable in the
     * org.ruleml.oojdrew.Config class. If this is true, this will produce a
     * string in POSL syntax, otherwise it will produce a string in OO RuleML
     * XML syntax.
     * 
     * @param rmlFormat
     *            RuleMLFormat, to determine which RuleML version to use.
     * 
     * @param varNames
     *            String array containing the variable names
     * 
     * @return The String representation of this term.
     */
    public String toString(String[] varNames, RuleMLFormat rmlFormat) {
        if (Config.PRPRINT) {
            return this.toPOSLString(varNames, true);
        } else {

            return this.toRuleMLString(varNames, rmlFormat);

        }
    }

    /**
     * Returns a string representation of this term in POSL syntax. This method
     * is used in cases where you do not have access to the variable names
     * associated with the clause - variable names will be output as ?Varx where
     * x is the variable id.
     * 
     * @return The POSL syntax representation of this term.
     */

    public String toPOSLString(boolean head) {
        String s = "";

        if (this.role != SymbolTable.INOROLE && this.role != SymbolTable.IREST
                && this.role != SymbolTable.IPREST) {
            String rs = SymbolTable.role(this.role);
            if (!rs.matches(regex)) {
                rs = "\"" + rs + "\"";
            }
            s += rs + "->";
        }

        if (this.symbol < 0) {
            s += "?Var" + (-(symbol + 1));
        } else {
            if (this.symbol != SymbolTable.INAF
                    && this.symbol != SymbolTable.IPLEX
                    && this.symbol != SymbolTable.IASSERT) {
                String sym = SymbolTable.symbol(this.symbol);

                if (sym.startsWith("$gensym")
                        && !org.ruleml.oojdrew.Config.PRINTGENSYMS) {
                    int idx = sym.indexOf("$", 7);
                    if (idx > -1) {
                        String skoname = sym.substring(idx + 1);
                        if (!sym.matches(regex)) {
                            skoname = "\"" + skoname + "\"";
                        }
                        sym = "_" + skoname;
                    } else
                        sym = "_";
                } else if (!sym.matches(regex)) {
                    sym = "\"" + sym + "\"";
                }
                s += sym;
            }
        }

        if (this.isAtom() && this.symbol == SymbolTable.INAF) {
            s += "naf(";
            for (int i = 0; i < this.subTerms.length; i++) {
                s += this.subTerms[i].toPOSLString(head);
                if ((i + 1) < this.subTerms.length) {
                    s += ",";
                }
            }
            s += ")";
        } else if (this.isAtom() && this.symbol == SymbolTable.IASSERT) {
            s += "assert( ";
            s += this.subTerms[0].toPOSLString(true);
            if (this.subTerms.length > 1) {
                s += " :- ";
                for (int i = 1; i < this.subTerms.length; i++) {
                    s += this.subTerms[i].toPOSLString(false);
                    if (i + 1 < this.subTerms.length) {
                        s += ", ";
                    }
                }
            }
            s += ". )";
        } else if (this.isExpr()) {
            String sb;
            String eb;
            if (this.isAtom()) {
                sb = "(";
                eb = ")";
            } else {
                sb = "[";
                eb = "]";
            }

            s += sb;
            for (int i = 0; i < this.subTerms.length; i++) {
                if (this.subTerms[i].role == SymbolTable.IREST) {
                    s += " !"; // handle printout if term is a slotted rest term
                } else if (this.subTerms[i].role == SymbolTable.IPREST) {
                    s += " |"; // handle printout if term is a positional rest
                               // term
                }

                if (this.subTerms[i].role == SymbolTable.IOID
                        && !org.ruleml.oojdrew.Config.PRINTGENOIDS && head) {
                    if (this.subTerms[i].getSymbolString()
                            .startsWith("$gensym"))
                        continue;
                }
                // get rid of blank oid here
                s += this.subTerms[i].toPOSLString(head);
                if (this.subTerms[i].role == SymbolTable.IOID) {
                    s += "^ ";
                }

                if ((i + 1) < this.subTerms.length) {

                    if (this.subTerms[i].role != SymbolTable.IOID
                            && this.subTerms[i + 1].role == SymbolTable.INOROLE) {
                        s += ", ";
                    } else if (this.subTerms[i + 1].role == SymbolTable.IREST) {
                    } else if (this.subTerms[i + 1].role == SymbolTable.IPREST) {
                    } else if (this.subTerms[i].role != SymbolTable.IOID) {
                        s += "; ";
                    }
                }
            }
            s += eb;
        }

        if (this.type != Types.IOBJECT) {
            String ts = Types.typeName(this.type);
            if (!ts.matches(regex)) {
                ts = "\"" + ts + "\"";
            }
            s += " : " + ts;
        }
        return s;
    }

    /**
     * Returns a string representation of this term in POSL syntax.
     * 
     * @param varNames
     *            A string array containing the variable names to use when
     *            outputting the string. These are stored in the DefiniteClause
     *            that the term is part of.
     * 
     * @return The POSL syntax representation of the term.
     */
    public String toPOSLString(String[] varNames) {
        return toPOSLString(varNames, false, true);
    }

    public String toPOSLString(String[] varNames, boolean head) {
        return toPOSLString(varNames, false, head);
    }

    /**
     * Returns a string representation of this term in POSL syntax, with the
     * option of omitting the role name. This is used by the mechanism for
     * saving variable bindings.
     * 
     * @param varNames
     *            A string array containing the variable names to use when
     *            outputting the string. These are stored in the DefiniteClause
     *            that the term is part of.
     * 
     * @param skiprole
     *            If true then the role name is omitted from the string output,
     *            otherwise the role name is included.
     * 
     * @param head
     *            If true then the atom is the head of a rule.
     * 
     * @return String The POSL syntax representation of the term.
     */
    public String toPOSLString(String[] varNames, boolean skiprole, boolean head) {
        String s = "";

        if (!skiprole) {
            if (this.role != SymbolTable.INOROLE
                    && this.role != SymbolTable.IREST
                    && this.role != SymbolTable.IPREST
                    && this.role != SymbolTable.IOID) {
                String rs = SymbolTable.role(this.role);
                if (!rs.matches(regex)) {
                    rs = "\"" + rs + "\"";
                }
                s += rs + "->";
            }
        }

        if (this.symbol < 0) {
            if (varNames[-(symbol + 1)].startsWith("$ANON")
                    && !org.ruleml.oojdrew.Config.PRINTANONVARNAMES) {
                s += "?";
            } else {
                s += "?" + varNames[-(symbol + 1)];
                if (org.ruleml.oojdrew.Config.PRINTVARID) {
                    s += "_" + (-(symbol + 1));
                }
            }
        } else {
            if (this.symbol != SymbolTable.INAF
                    && this.symbol != SymbolTable.IPLEX
                    && this.symbol != SymbolTable.IASSERT) {
                String sym = SymbolTable.symbol(this.symbol);

                if (sym.startsWith("$gensym")
                        && !org.ruleml.oojdrew.Config.PRINTGENSYMS) {
                    int idx = sym.indexOf("$", 7);
                    if (idx > -1) {
                        String skoname = sym.substring(idx + 1);
                        if (!sym.matches(regex)) {
                            skoname = "\"" + skoname + "\"";
                        }
                        sym = "_" + skoname;
                    } else
                        sym = "_";
                } else if (!sym.matches(regex)) {
                    sym = "\"" + sym + "\"";
                }

                s += sym;
            }
        }

        if (this.isAtom() && this.symbol == SymbolTable.INAF) {
            s += "naf( ";
            for (int i = 0; i < this.subTerms.length; i++) {
                s += this.subTerms[i].toPOSLString(varNames, head);
                if ((i + 1) < this.subTerms.length) {
                    s += ",";
                }
            }
            s += " )";
        } else if (this.isAtom() && this.symbol == SymbolTable.IASSERT) {
            s += "assert( ";
            s += this.subTerms[0].toPOSLString(varNames, true);
            if (this.subTerms.length > 1) {
                s += " :- ";
                for (int i = 1; i < this.subTerms.length; i++) {
                    s += this.subTerms[i].toPOSLString(varNames, false);
                    if (i + 1 < this.subTerms.length) {
                        s += ", ";
                    }
                }
            }
            s += ". )";
        } else if (this.isExpr()) {
            String sb;
            String eb;
            if (this.isAtom()) {
                sb = "(";
                eb = ")";
            } else {
                sb = "[";
                eb = "]";
            }

            s += sb;
            for (int i = 0; i < this.subTerms.length; i++) {
                if (this.subTerms[i].role == SymbolTable.IREST) {
                    s += "!"; // handle printout if first term is a slotted rest
                              // term
                } else if (this.subTerms[i].role == SymbolTable.IPREST) {
                    s += "|"; // handle printout if first term is a positional
                              // rest term
                }

                if (this.subTerms[i].role == SymbolTable.IOID
                        && !org.ruleml.oojdrew.Config.PRINTGENOIDS) {
                    if (this.subTerms[i].symbol > 0
                            && this.subTerms[i].getSymbolString().startsWith(
                                    "$gensym") && head)
                        continue;
                    if (this.subTerms[i].symbol < 0
                            && varNames[-(this.subTerms[i].symbol + 1)]
                                    .startsWith("$ANON") && !head)
                        continue;
                }

                s += this.subTerms[i].toPOSLString(varNames, head);
                if (this.subTerms[i].role == SymbolTable.IOID) {
                    s += "^ ";
                }

                if ((i + 1) < this.subTerms.length) {
                    if (this.subTerms[i].role != SymbolTable.IOID
                            && this.subTerms[i + 1].role == SymbolTable.INOROLE) {
                        s += ", ";
                    } else if (this.subTerms[i + 1].role == SymbolTable.IREST) {
                    } else if (this.subTerms[i + 1].role == SymbolTable.IPREST) {
                    } else if (this.subTerms[i].role != SymbolTable.IOID) {
                        s += "; ";
                    }
                }
            }
            s += eb;
        }

        if (this.type != Types.IOBJECT) {
            String ts = Types.typeName(this.type);
            if (!ts.matches(regex)) {
                ts = "\"" + ts + "\"";
            }
            s += " : " + ts;
        }
        return s;
    }

    // String solution to print instantiated facts
    public String toPOSLStringAll(String[] varNames, boolean skiprole,
            boolean head) {

        String s = "";

        if (!skiprole) {
            if (this.role != SymbolTable.INOROLE
                    && this.role != SymbolTable.IREST
                    && this.role != SymbolTable.IPREST
                    && this.role != SymbolTable.IOID) {
                String rs = SymbolTable.role(this.role);
                if (!rs.matches(regex)) {
                    rs = "\"" + rs + "\"";
                }
                s += rs + "->";
            }
        }

        if (this.symbol < 0) {
            if (varNames[-(symbol + 1)].startsWith("$ANON")
                    && !org.ruleml.oojdrew.Config.PRINTANONVARNAMES) {
                s += "?";
            } else {
                s += "?" + varNames[-(symbol + 1)];
                if (org.ruleml.oojdrew.Config.PRINTVARID) {
                    s += "" + (-(symbol + 1));
                }
            }
        } else {
            if (this.symbol != SymbolTable.INAF
                    && this.symbol != SymbolTable.IPLEX
                    && this.symbol != SymbolTable.IASSERT) {
                String sym = SymbolTable.symbol(this.symbol);

                if (sym.startsWith("$gensym")
                        && !org.ruleml.oojdrew.Config.PRINTGENSYMS) {
                    int idx = sym.indexOf("$", 7);
                    if (idx > -1) {
                        String skoname = sym.substring(idx + 1);
                        if (!sym.matches(regex)) {
                            skoname = "\"" + skoname + "\"";
                        }
                        sym = "" + skoname;
                    } else
                        sym = "";
                } else if (!sym.matches(regex)) {
                    sym = "\"" + sym + "\"";
                }

                s += sym;
            }
        }

        if (this.isAtom() && this.symbol == SymbolTable.INAF) {
            s += "naf( ";
            for (int i = 0; i < this.subTerms.length; i++) {
                s += this.subTerms[i].toPOSLString(varNames, head);
                if ((i + 1) < this.subTerms.length) {
                    s += ",";
                }
            }
            s += " )";
        } else if (this.isAtom() && this.symbol == SymbolTable.IASSERT) {
            s += "assert( ";
            s += this.subTerms[0].toPOSLString(varNames, true);
            if (this.subTerms.length > 1) {
                s += " :- ";
                for (int i = 1; i < this.subTerms.length; i++) {
                    s += this.subTerms[i].toPOSLString(varNames, false);
                    if (i + 1 < this.subTerms.length) {
                        s += ", ";
                    }
                }
            }
            s += ". )";
        } else if (this.isExpr()) {
            String sb;
            String eb;
            if (this.isAtom()) {
                sb = "(";
                eb = ")";
            } else {
                sb = "[";
                eb = "]";
            }

            s += sb;
            for (int i = 0; i < this.subTerms.length; i++) {
                if (this.subTerms[i].role == SymbolTable.IREST) {
                    s += "!"; // handle printout if first term is a slotted rest
                              // term
                } else if (this.subTerms[i].role == SymbolTable.IPREST) {
                    s += "|"; // handle printout if first term is a positional
                              // rest term
                }

                if (this.subTerms[i].role == SymbolTable.IOID
                        && !org.ruleml.oojdrew.Config.PRINTGENOIDS) {
                    if (this.subTerms[i].symbol > 0
                            && this.subTerms[i].getSymbolString().startsWith(
                                    "$gensym") && head)
                        continue;
                    if (this.subTerms[i].symbol < 0
                            && varNames[-(this.subTerms[i].symbol + 1)]
                                    .startsWith("$ANON") && !head)
                        continue;
                }

                // get rid of blank oid here

                String s2 = "";

                s2 = this.subTerms[i].toPOSLString(varNames, head);

                if (s2.compareTo("_") != 0) {

                    s += s2;
                }

                if (this.subTerms[i].role == SymbolTable.IOID) {
                    // s += "^ ";
                }

                if ((i + 1) < this.subTerms.length) {
                    if (this.subTerms[i].role != SymbolTable.IOID
                            && this.subTerms[i + 1].role == SymbolTable.INOROLE) {
                        s += ", ";
                    } else if (this.subTerms[i + 1].role == SymbolTable.IREST) {
                    } else if (this.subTerms[i + 1].role == SymbolTable.IPREST) {
                    } else if (this.subTerms[i].role != SymbolTable.IOID) {
                        s += "; ";
                    }
                }
            }
            s += eb;
        }

        if (this.type != Types.IOBJECT) {
            String ts = Types.typeName(this.type);
            if (!ts.matches(regex)) {
                ts = "\"" + ts + "\"";
            }
            s += " : " + ts;
        }
        return s;
    }

    /**
     * Produces an OO RuleML XML syntax representation of this term, stored in a
     * string.
     * 
     * This version is for the case where you do not have access to the variable
     * names for the term.
     * 
     * @param rmlFormat
     *            RuleMLFormat, to determine which RuleML version to use.
     * 
     * @return The OO RuleML syntax representation of this, stored as a
     *         "pretty printed" string.
     */
    public String toRuleMLString(RuleMLFormat rmlFormat) {

        Element rml = this.toRuleML(true, rmlFormat);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        nu.xom.Serializer sl = new nu.xom.Serializer(os);
        sl.setIndent(3);
        sl.setLineSeparator("\n");
        try {
            Document doc = new Document(rml);
            sl.write(doc);
        } catch (java.io.IOException e) {
            System.err.println(e.getMessage());
        }
        return os.toString();
    }

    /**
     * Produces an OO RuleML XML syntax representation of this term, stored in a
     * string.
     * 
     * @param varNames
     *            A string array containing the variable names to use when
     *            outputting - these are stored in the DefiniteClause object
     *            associated with the term.
     * 
     * @param rmlFormat
     *            RuleMLFormat, to determine which RuleML version to use.
     * 
     * @return A string containing the OO RuleML XML representation of this
     *         term.
     */
    public String toRuleMLString(String[] varNames, RuleMLFormat version) {

        Element rml = this.toRuleML(varNames, true, version);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        nu.xom.Serializer sl = new nu.xom.Serializer(os);
        sl.setIndent(3);
        sl.setLineSeparator("\n");
        try {
            Document doc = new Document(rml);
            sl.write(doc);
        } catch (java.io.IOException e) {
            System.err.println(e.getMessage());
        }
        return os.toString();
    }

    /**
     * Produces an OO RuleML XML syntax representation of this term, stored in a
     * nu.xom.Element object.
     * 
     * This version is for when you do not have access to the variable names.
     * 
     * @see toRuleML(String[] varNames, boolean head, RuleMLFormat rmlFormat)
     */
    public Element toRuleML(boolean head, RuleMLFormat rmlFormat) {
        return toRuleML(null, head, rmlFormat);
    }

    /**
     * Produces an OO RuleML XML syntax representation of this term, stored in a
     * nu.xom.Element object.
     * 
     * @param varNames
     *            The variable names associated with the term; these are stored
     *            in the DefiniteClause object associated with the term.
     * 
     * @param rmlFormat
     *            RuleMLFormat, to determine which RuleML version to use.
     * 
     * @return The OO RuleML syntax representation of this, as an Element value.
     */
    public Element toRuleML(String[] varNames, boolean head, RuleMLFormat rmlFormat) {

        RuleMLTagNames rmlTagNames = new RuleMLTagNames(rmlFormat);

        // Printing the Clauses in RuleML Format
        Element el = null;
        boolean dst = false;
        if (this.isExpr()) {
            if (this.isAtom() && this.symbol == SymbolTable.INAF) {
                el = new Element(rmlTagNames.NAF);
            } else if (this.isAtom() && this.symbol == SymbolTable.IASSERT) {
                el = new Element(rmlTagNames.ASSERT);
                if (this.subTerms.length == 1) {
                    el.insertChild(subTerms[0].toRuleML(varNames, true, rmlFormat), 0);
                } else {
                    Element implies = new Element(rmlTagNames.IMPLIES);
                    if (this.subTerms.length > 2) {
                        Element and = new Element(rmlTagNames.AND);
                        for (int i = 1; i < this.subTerms.length; i++) {
                            and.appendChild(this.subTerms[i].toRuleML(varNames, false, rmlFormat));
                        }
                        implies.appendChild(and);
                    } else {
                        implies.appendChild(this.subTerms[1].toRuleML(varNames, false, rmlFormat));
                    }
                    implies.appendChild(this.subTerms[0].toRuleML(varNames, true, rmlFormat));
                    el.appendChild(implies);
                }
                dst = true;
            } else if (this.isAtom()) {
                el = new Element(rmlTagNames.ATOM);

                Element rel = new Element(rmlTagNames.REL);
                el.insertChild(rel, el.getChildCount());
                rel.insertChild(SymbolTable.symbol(this.symbol), 0);
            } else if (this.symbol == SymbolTable.IPLEX) {
                el = new Element(rmlTagNames.PLEX);
            } else {
                el = new Element(rmlTagNames.EXPR);

                Element fun = new Element(rmlTagNames.FUN);
                el.insertChild(fun, el.getChildCount());
                fun.insertChild(SymbolTable.symbol(this.symbol), 0);
            }

            Element subElement;
            for (int i = 0; i < this.subTerms.length && !dst; i++) {
                subElement = this.subTerms[i].toRuleML(varNames, head, rmlFormat);
                if (subElement != null) {
                    el.insertChild(subElement, el.getChildCount());
                }
            }
        } else {
            if (this.role == SymbolTable.IOID
                    && !org.ruleml.oojdrew.Config.PRINTGENOIDS) {
                if (this.symbol > 0
                        && this.getSymbolString().startsWith("$gensym") && head)
                    return null;
                else if (varNames != null && this.symbol < 0
                        && varNames[-(this.symbol + 1)].startsWith("$ANON")
                        && !head)
                    return null;
            }

            if (this.symbol < 0) {
                el = new Element(rmlTagNames.VAR);
                if (varNames != null
                        && (!varNames[-(symbol + 1)].startsWith("$ANON") || org.ruleml.oojdrew.Config.PRINTANONVARNAMES)) {
                    String varName = varNames[-(symbol + 1)];
                    if (org.ruleml.oojdrew.Config.PRINTVARID) {
                        varName += -(symbol + 1);
                    }
                    el.insertChild(varName, 0);
                } else {
                    el.insertChild(rmlTagNames.VAR + (-(symbol + 1)), 0);
                }
            } else {
                String sym = SymbolTable.symbol(this.symbol);
                if (sym.startsWith("$gensym")
                        && !org.ruleml.oojdrew.Config.PRINTGENSYMS) {
                    el = new Element(rmlTagNames.SKOLEM);
                    int idx = sym.indexOf("$", 7);
                    if (idx > -1) {
                        String skoname = sym.substring(idx + 1);
                        el.appendChild(skoname);
                    }
                } else {
                    if (isData) {
                        el = new Element(rmlTagNames.DATA);
                        el.insertChild(sym, 0);
                    }

                    if (!isData) {
                        el = new Element(rmlTagNames.IND);
                        el.insertChild(sym, 0);
                    }
                }
            }
        }

        if (this.type != Types.IOBJECT) {
            Attribute typeAttribute = new Attribute(rmlTagNames.TYPE, Types.typeName(type));
            el.addAttribute(typeAttribute);
        }

        if (this.role == SymbolTable.IREST) {
            Element resl = new Element(rmlTagNames.RESL);
            resl.insertChild(el, 0);
            return resl;
        } else if (this.role == SymbolTable.IPREST) {
            Element repo = new Element(rmlTagNames.REPO);
            repo.insertChild(el, 0);
            return repo;
        } else if (this.role == SymbolTable.IOID) {
            Element oid = new Element(rmlTagNames.OID);
            oid.insertChild(el, 0);
            return oid;
        } else if (this.role == SymbolTable.INOROLE) {
            return el;
        } else {
            Element slot = new Element(rmlTagNames.SLOT);
            Element child = null;

            // If isData is true then change it here
            if (dataSlot) {
                child = new Element(rmlTagNames.DATA);
            } else {
                child = new Element(rmlTagNames.IND);
            }
            child.appendChild(SymbolTable.role(this.role));

            slot.appendChild(child);
            slot.appendChild(el);
            return slot;
        }
    }

    /**
     * Make an identical deep (recursive) copy of this term. This is used when
     * unifying to avoid making changes to the original data structures that
     * represent clauses.
     * 
     * @return A full copy of this term.
     */
    public Term deepCopy() {
        // False indicates not to change the original side values
        return deepCopy(0, false);
    }

    /**
     * Produces a deep (recursive) copy of this term, with the side member set
     * to the specified value. This is used when Unifying to avoid making
     * changes to the original data structures that represent clauses.
     * 
     * @param pside
     *            The side value that should be stored in the copy.
     * 
     * @return A full copy of this term.
     */
    public Term deepCopy(int pside) {
        // True indicates to change the original side values to pside
        return deepCopy(pside, true);
    }

    /**
     * @see deepCopy()
     * @see deepCopy(int pside)
     */
    private Term deepCopy(int newSideValue, boolean useNewSide) {
        Term term;
        if (this.subTerms != null) {
            Term[] sterms = new Term[this.subTerms.length];
            for (int i = 0; i < this.subTerms.length; i++) {
                sterms[i] = this.subTerms[i].deepCopy(newSideValue, useNewSide);
            }
            term = new Term(this.symbol, this.role, this.type, sterms);
            term.atom = this.atom;
        } else {
            term = new Term(this.symbol, this.role, this.type);
        }

        term.side = useNewSide ? newSideValue : this.side;

        if (isData) {
            term.setData(true);
        }
        return term;
    }

    /**
     * Sorts a Vector of term objects by the role code.
     * 
     * @param toSort
     *            A Vector containing only Term objects - to be sorted by the
     *            role name code.
     * 
     * @return Vector A Vector containing the sorted terms.
     */
    public static Vector sort(Vector toSort) {
        Vector sorted = new Vector();

        for (int i = 0; i < toSort.size(); i++) {
            Term c = (Term) toSort.get(i);

            int j = 0;
            while (j < sorted.size()) {
                Term chk = (Term) sorted.get(j);
                if (c.getRole() >= chk.getRole()) {
                    j++;
                } else {
                    break;
                }
            }
            sorted.add(j, c);
        }
        return sorted;
    }

    /**
     * Sorts an array of org.ruleml.oojdrew.util.Term values by the role code.
     * 
     * @param toSort
     *            An array of Term objects to be sorted by the role name code.
     * 
     * @return A Vector containing the sorted terms.
     */
    public static Vector sort(Term[] toSort) {
        Vector ts = new Vector();
        for (int i = 0; i < toSort.length; i++) {
            ts.add(toSort[i]);
        }
        return sort(ts);
    }

    /**
     * Compares one term to another object. If this object is a Term object, it
     * will compare first by the symbol code, then the role name code, the
     * number of parameters, and finally by the actual parameters themselves. If
     * the other object is not a Term, then this always returns 1.
     * 
     * @param toCompare
     *            The object to compare this term to.
     * 
     * @return The result of the comparison 1 if the other term is "less than",
     *         0 if they are equal, or -1 if the other term is "greater than".
     */
    public int compareTo(Object toCompare) {
        if (this.getClass() != toCompare.getClass()) {
            return 1;
        }

        Term o = (Term) toCompare;
        if (this.symbol < o.symbol) {
            return 1;
        } else if (this.symbol > o.symbol) {
            return -1;
        } else {
            if (this.role < o.role) {
                return 1;
            } else if (this.role > o.role) {
                return -1;
            } else {
                if (this.subTerms == null) {
                    return 0;
                }
                if (this.subTerms.length < o.subTerms.length) {
                    return 1;
                } else if (this.subTerms.length > o.subTerms.length) {
                    return -1;
                } else {
                    for (int i = 0; i < this.subTerms.length; i++) {
                        int cmp = this.subTerms[i].compareTo(o.subTerms[i]);
                        if (cmp != 0) {
                            return cmp;
                        }
                    }
                    return 0;
                }
            }
        }
    }
}
