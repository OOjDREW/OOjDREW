// OO jDREW Version 0.93
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package jdrew.oo.util;

/**
 * This exception is thrown if there is an error in the reasoning engine -
 * this should only happen if the engine is given invalid input. Parsers should
 * be written to prevent the creation of data structures that will not be valid
 * input for the reasoning engine.
 *
 * If a EngineException is thrown then it is likely that there is a bug with
 * the parser that is causing it to generate invalid data structures. It is
 * also possible that the user continues to use clause/term data structures that
 * were created before a call to the reset() method of jdrew.oo.util.Types or
 * jdrew.oo.util.SymbolTable.
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
public class EngineException extends RuntimeException {
    public EngineException() {
        super();
    }

    public EngineException(String message) {
        super(message);
    }

    public EngineException(Throwable cause) {
        super(cause);
    }

    public EngineException(String message, Throwable cause) {
        super(message, cause);
    }
}
