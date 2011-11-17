// OO jDREW Version 0.93
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package jdrew.oo.parsing;

/**
 * An exception that is thrown if there is an error parsing input correctly.
 * This is used in the RDFSParser, RuleMLParser and POSLParser classes to
 * indicate some error with parsing (usually malformed input of some sort).
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
public class ParseException extends Exception {
    public ParseException() {
        super();
    }

    public ParseException(String message) {
        super(message);
    }
    
}
