// OO jDREW Version 0.93
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package jdrew.oo.parsing;

import jdrew.oo.parsing.generated.POSLLexer;
import jdrew.oo.util.DefiniteClause;
import jdrew.oo.util.parsing.*;

import java.util.*;
import java.io.*;
import antlr.*;
/**
 * <p>Title: OO jDREW</p>
 *
 * This class serves as a wrapper for the parser that is generated from the
 * POSLParser-Java.g ANTLR grammar. The classes for the ANTLR generated parser
 * are part of the jdrew.oo.util.parsing package.
 *
 * The methods in this class take the appropriate reader (StringReader,
 * FileReader), then a lexcographical analyzer is created on that reader; once
 * this is done a jdrew.oo.util.parsing.POSLParser object is created on the
 * lexer and the appropriate parsing routine is invoked.
 *
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.88</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * @author Marcel Ball
 * @version 0.89
 */
public class POSLParser {
    Vector clauses = new Vector();

    public POSLParser() {
    }

    /**
     * Returns an interator over the clauses that are stored in the parsers
     * internal buffer; the buffer is not automatically cleared after a call
     * to iterator(). To clear the buffer a call to reset() must be invoked.
     * @return Iterator
     */
    public Iterator iterator() {
        return clauses.iterator();
    }

    /**
     * Clears the parsers internal clause buffer.
     */
    public void reset() {
        clauses = new Vector();
    }

    /**
     * This method is used to parse a single definite clause, stored in a
     * String. The DefiniteClause object that represents the parsed clause is
     * returned directly to the user and not added to the internal clause
     * buffer.
     *
     * @param clause String A string containing the clause to be parsed. If the
     * string contains multiple clauses only the first clause will be parsed.
     *
     * @return DefiniteClause The DefiniteClause object that represents the
     * parsed clause.
     *
     * @throws RecognitionException
     * @throws TokenStreamException
     */
    public DefiniteClause parseDefiniteClause(String clause) throws RecognitionException, TokenStreamException{
        StringReader sr = new StringReader(clause);
        POSLLexer lex = new POSLLexer(sr);
        jdrew.oo.parsing.generated.POSLParser pp = new jdrew.oo.parsing.generated.POSLParser(lex);
        return pp.clause(true);
    }

    /**
     * This method is used to parse a query - stored in a String. The
     * DefiniteClause object that represents the parsed query is returned
     * directly to the user and not added to the internal clause buffer.
     *
     * @param query String A string containing the query to be parsed. If the
     * string contains multiple queries only the first query will be parsed.
     *
     * @return DefiniteClause The DefiniteClause object that represents the
     * parsed query.
     *
     * @throws RecognitionException
     * @throws TokenStreamException
     */
    public DefiniteClause parseQueryString(String query) throws RecognitionException, TokenStreamException{
        return parseDefiniteClause("$top():-" + query + ".");
    }
	//used for parsing a Type Query String
 	public DefiniteClause parseQueryStringT(String query) throws RecognitionException, TokenStreamException{
        return parseDefiniteClause(query + ".");
    }

    /**
     * This method is used to parse a group of clauses, stored in a String. The
     * parsed clauses (DefiniteClause objects) are added to the internal clause
     * buffer and not returned directly to the caller. The an iterator over the
     * clauses can be retrieved by invoking the iterator() method.
     *
     * @param clauses String A string containing one or more clauses.
     *
     * @throws RecognitionException
     * @throws TokenStreamException
     */
    public void parseDefiniteClauses(String clauses) throws RecognitionException, TokenStreamException{
        StringReader sr = new StringReader(clauses);
        parseReader(sr);
    }

    /**
     * This method is used to parse a group of clauses stored in a local file.
     * The full path to the filename should be used. A FileReader object is
     * created using the filename as the path; then the reader is passed to
     * the parseReader(Reader) method.
     *
     * @param filename String A string containing the complete path to the file.
     *
     * @throws FileNotFoundException
     * @throws RecognitionException
     * @throws TokenStreamException
     */
    public void parseDCFile(String filename) throws FileNotFoundException, RecognitionException, TokenStreamException{
        FileReader fr = new FileReader(filename);
        parseReader(fr);
    }

    /**
     * This method is used to parse a group of clauses read from an InputStream
     * (keyboard, socket, etc.). A InputStreamReader is created using the
     * InputStream and passed to the parseReader(Reader) method. Clauses are
     * parsed until a End of File (EOF) character is reached (Typically CTRL-D
     * on Unix-like systems and CTRL-Z on Windows).
     *
     * @param is InputStream The input stream to read clauses from.
     *
     * @throws RecognitionException
     * @throws TokenStreamException
     */
    public void parseInputStream(InputStream is) throws RecognitionException, TokenStreamException {
        InputStreamReader isr = new InputStreamReader(is);
        parseReader(isr);
    }

    /**
     * This method is used by parseInputStream(), parseDCFile() and
     * parseDefiniteClauses() to perform the parsing, using the reader that is
     * created in each of those methods.
     *
     * This method creates a POSLLexer (Lexcographical Analyzer) object on the
     * Reader. Once this is done a jdrew.oo.util.parsing.POSLParser object
     * created on the lexer and the rulebase(Vector) method is invoked to parse
     * the rulebase that is read from the reader.
     *
     * @param r Reader The reader to read the input from.
     *
     * @throws RecognitionException
     * @throws TokenStreamException
     */
    private void parseReader(Reader r) throws RecognitionException, TokenStreamException {
        POSLLexer lex = new POSLLexer(r);
        jdrew.oo.parsing.generated.POSLParser pp = new jdrew.oo.parsing.generated.POSLParser(lex);
        pp.rulebase(this.clauses);
    }
}
