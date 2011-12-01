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
