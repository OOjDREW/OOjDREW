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

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Vector;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.ruleml.oojdrew.Configuration;
import org.ruleml.oojdrew.util.DefiniteClause;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

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
public class RuleMLParser implements PreferenceChangeListener {

    /**
     * A buffer that stores clauses that have already been parsed.
     */
    private Vector<DefiniteClause> clauses;
    private Configuration config;
    private boolean validateRuleML;

    /**
     * This is used to indicate what back-end parser to use. Currently only
     * RuleML 0.88 (+ rests) and RuleML 0.91 is supported; so only the RULEML88 = 1
     * value isdefined as well as RULEML91 = 2. As new backend parsers are 
     * added then extra defines can be added.
     */
    public static enum RuleMLFormat
    {
    	RuleML88,
    	RuleML91,
    	RuleML100,
    	RuleMLQuery
    }

    /**
     * Constructs a new parser object.
     */
    public RuleMLParser(Configuration config) {
        clauses = new Vector<DefiniteClause>();
        this.config = config;
        config.addPreferenceChangeListener(this);
        preferenceChange(null);
    }

    /**
     * Gets an iterator over all clauses that are stored in the internal clause
     * buffer. This method does not automatically clear items from the buffer.
     *
     * @return Iterator An iterator over all clauses in the buffer.
     */
    public Iterator<DefiniteClause> iterator() {
        return clauses.iterator();
    }

    /**
     * Clears the internal buffer; and forces a garbage collection cycle. This
     * allows the easy reuse of a parser object.
     */
    public void clear() {
        clauses = new Vector<DefiniteClause>();
        System.gc();
    }

    /**
     * @see jdrew.oo.parsing.RuleMLParser#parseDocument 
     */
    public void parseFile(RuleMLFormat format, String filename) throws ParseException,
    ParsingException, ValidityException, IOException 
    {
		File file = new File(filename);
		parseFile(format, file);
    }

    /**
     * @see jdrew.oo.parsing.RuleMLParser#parseDocument 
     */
    public void parseFile(RuleMLFormat format, File file) throws ParseException, ParsingException, ValidityException, IOException 
    {
		Builder bl = new Builder();
		Document doc = bl.build(file);
		parseDocument(format, doc);
    }
    
    /**
     * @see jdrew.oo.parsing.RuleMLParser#parseDocument 
     */
    public void parseRuleMLString(RuleMLFormat format, String contents) throws
            ParseException, ParsingException, ValidityException, IOException {
    	
    	if (validateRuleML) {
        	XMLReader xmlReader;
        	try {
        		xmlReader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser"); 
        		xmlReader.setFeature("http://apache.org/xml/features/validation/schema", true);
    		} catch (SAXException e) {
    			throw new ParseException("Unable to create XML validator");
    		}
        	
       		Builder bl = new Builder(xmlReader, true);
       		StringReader sr = new StringReader(contents);
       		Document doc;
       		try {
       			doc = bl.build(sr);	
       	  	 	parseDocument(format, doc);
			} catch (Exception e) {
				throw new ParseException(
						"Document does not validate against the specified XML schema definition(s)!");
			}
    	} else {
	   		Builder bl = new Builder();
	   		StringReader sr = new StringReader(contents);
	  	 	Document doc = bl.build(sr);
	  	 	parseDocument(format, doc);
    	}
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
     * @param format RuleMLVersion The RuleML version for the backend parser
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
	public void parseDocument(RuleMLFormat format, Document doc)
			throws ParseException, ParsingException, ValidityException
	{
		RuleMLDocumentParser parser = new RuleMLDocumentParser(format, clauses);

		parser.parseRuleMLDocument(doc);
	}
        
	/**
	 * Parses a RuleML query
	 * @param contents
	 * @return
	 * @throws ParseException
	 * @throws ParsingException
	 * @throws ValidityException
	 * @throws IOException
	 */
   public DefiniteClause parseRuleMLQuery(String contents) throws
        ParseException, ParsingException, ValidityException, IOException {

		parseRuleMLString(RuleMLFormat.RuleMLQuery, contents);
		
		return (DefiniteClause) clauses.lastElement();
	}

	public void preferenceChange(PreferenceChangeEvent evt) {
		validateRuleML = config.getValidateRuleMLEnabled();
	}
}
