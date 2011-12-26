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

package org.ruleml.oojdrew.parsing;

public class RuleMLTagNames {

    // "Root" tags
    public final String RULEML = "RuleML";
    public final String RULEBASE = "Rulebase";
    public final String ASSERT = "Assert";
    public final String QUERY = "Query";

    // Supported tags
    public final String AND = "And";
    public final String ATOM = "Atom";
    public final String IMPLIES = "Implies";
    public final String NAF = "Naf";
    public final String REL = "Rel";
    public final String NEG = "Neg";
    public final String IND = "Ind";
    public final String VAR = "Var";
    public final String PLEX = "Plex";
    public final String SKOLEM = "Skolem";
    public final String DATA = "Data";

    public final String OID = "oid";
    public final String TYPE = "type";
    public final String SLOT = "slot";
    public final String RESL = "resl";
    public final String REPO = "repo";

    // Supported attribute values
    public final String UNIVERSAL = "universal";

    // Tags which differ between RuleML versions
    public final String PREMISE;
    public final String CONCLUSION;
    public final String EXPR;
    public final String FUN;
    public final String MAPCLOSURE;
    public final String OP;

    // Unsupported tags
    public final String EQUAL = "Equal";

    // Skipped tags (only children will be parsed)
    public final String REIFY = "Reify";
    public final String RIGHT = "right";
    public final String LEFT = "left";
    public final String ACT = "act";
    public final String ARG = "arg";
    public final String FORMULA = "formula";
    public final String DECLARE = "declare";
    public final String STRONG = "strong";
    public final String WEAK = "weak";
    public final String TORSO = "torso";
    public final String DEGREE = "degree";

    public RuleMLTagNames(final RuleMLFormat rmlFormat) {
        // Initialize RuleML premise and conclusion tags
        if (rmlFormat == RuleMLFormat.RuleML100) {
            // RuleML version 1.0
            PREMISE = "if";
            CONCLUSION = "then";
        } else {
            // RuleML versions < 1.0
            PREMISE = "body";
            CONCLUSION = "head";
        }

        // Initialize further tags
        switch (rmlFormat) {
        case RuleML88:
            EXPR = "Cterm";
            FUN = "Ctor";
            MAPCLOSURE = "innerclose";
            OP = "opr";
            break;
        default: // RuleML 0.91 (+Query) and 1.0
            EXPR = "Expr";
            FUN = "Fun";
            MAPCLOSURE = "mapClosure";
            OP = "op";
        }
    }
}
