// OO jDREW Version 0.93
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package org.ruleml.oojdrew.parsing;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Vector;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.ruleml.oojdrew.util.DefiniteClause;

/**
 * A class for parsing RuleML. This is broken into two section. The
 * RuleMLParser class which is the public interface that users access; and the
 * RuleML88Parser class; which implements the parsing of the RuleML 0.88 +
 * rests syntax that is currently supported.
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
public class RuleMLParser {

    /**
     * A buffer that stores clauses that have already been parsed.
     */
    private Vector clauses;

    /**
     * This is used to indicate what back-end parser to use. Currently only
     * RuleML 0.88 (+ rests) and RuleML 0.91 is supported; so only the RULEML88 = 1
     * value isdefined as well as RULEML91 = 2. As new backend parsers are 
     * added then extra defines can be added.
     */
    public static enum RuleMLVersion
    {
    	RuleML88,
    	RuleML91,
    	RuleML100
    }

    /**
     * Constructs a new parser object.
     */
    public RuleMLParser() {
        clauses = new Vector();
    }

    /**
     * Gets an iterator over all clauses that are stored in the internal clause
     * buffer. This method does not automatically clear items from the buffer.
     *
     * @return Iterator An iterator over all clauses in the buffer.
     */
    public Iterator iterator() {
        return clauses.iterator();
    }

    /**
     * Clears the internal buffer; and forces a garbage collection cycle. This
     * allows the easy reuse of a parser object.
     */
    public void clear() {
        clauses = null;
        System.gc();
        clauses = new Vector();
    }

    /**
     * @see jdrew.oo.parsing.RuleMLParser#parseDocument 
     */
    public void parseFile(RuleMLVersion version, String filename) throws ParseException,
    ParsingException, ValidityException, IOException 
    {
		File file = new File(filename);
		parseFile(version, file);
    }

    /**
     * @see jdrew.oo.parsing.RuleMLParser#parseDocument 
     */
    public void parseFile(RuleMLVersion version, File file) throws ParseException, ParsingException, ValidityException, IOException 
    {
		Builder bl = new Builder();
		Document doc = bl.build(file);
		parseDocument(version, doc);
    }
    
    /**
     * @see jdrew.oo.parsing.RuleMLParser#parseDocument 
     */
    public void parseRuleMLString(RuleMLVersion version, String contents) throws
            ParseException, ParsingException, ValidityException, IOException {
   		Builder bl = new Builder();
   		StringReader sr = new StringReader(contents);
  	 	Document doc = bl.build(sr);
  	 	parseDocument(version, doc);
    }
    
    /**
     * Parses a document containing a knowledge base that is in the indicated
     * format.
     * If additional backed parsers are created then additional formats 
     * will be added.
     *
     * NOTE: It may be a good idea to add format auto detection based upon the
     * XSD and/or DTD that is referenced by the document.
     *
     * @param version RuleMLVersion The RuleML version for the backend parser
     *
     * @param kb String The filename (including the full path) to the
     * file to be parsed.
     *
     * @throws ParseException A ParseException is thrown if there is an error
     * in the document that causes parsing to fail.
     *
     * @throws ParsingException A ParsingException is thrown if there is an
     * error in parsing the document at an XML level.
     *
     * @throws ValidityException A ValidityException is thrown if the XML
     * document is not well formed or does not conform to the DTD specified.
     */
	public void parseDocument(RuleMLVersion version, Document doc)
			throws ParseException, ParsingException, ValidityException
	{
		RuleMLDocumentParser parser = null;

		switch (version)
		{
		case RuleML88:
		case RuleML91:
		case RuleML100:
			parser = new RuleMLDocumentParser(clauses);
		}

		if (parser == null)
		{
			throw new ParseException(
					"Selected RuleML version is not supported!");
		}

		parser.parseRuleMLDocument(doc);
	}
        
   public DefiniteClause parseRuleMLQuery(String contents) throws
        ParseException, ParsingException, ValidityException, IOException {

		parseRuleMLString(RuleMLVersion.RuleML91, contents);
		
		Iterator it = clauses.iterator();

		DefiniteClause dc = null;
		while (it.hasNext())
		{
			dc = (DefiniteClause) it.next();
			// System.out.println("Loaded clause: " + dc.toPOSLString());
		}
		// System.out.println(dc.toPOSLString());

		return dc;
	}
}
