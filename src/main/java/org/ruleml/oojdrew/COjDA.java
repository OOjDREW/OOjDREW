// OO jDREW - An Object Oriented extension of the Java Deductive Reasoning Engine for the Web
// Copyright (C) 2011
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

package org.ruleml.oojdrew;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.log4j.Level;
import org.ruleml.oojdrew.TopDown.BackwardReasoner;
import org.ruleml.oojdrew.parsing.POSLParser;
import org.ruleml.oojdrew.parsing.ParseException;
import org.ruleml.oojdrew.parsing.RDFSParser;
import org.ruleml.oojdrew.parsing.RuleMLFormat;
import org.ruleml.oojdrew.parsing.RuleMLParser;
import org.ruleml.oojdrew.parsing.SubsumesException;
import org.ruleml.oojdrew.util.DefiniteClause;
import org.ruleml.oojdrew.util.TaxonomyQueryAPI;
import org.ruleml.oojdrew.util.Util;

import antlr.RecognitionException;
import antlr.TokenStreamException;

/**
 * This class implements the Complete OO jDREW API (COjDA)
 */
public class COjDA {

    private int varSize = 0;

    private Configuration config;

    private RuleMLParser rmlParser;
    private POSLParser poslParser;
    private TaxonomyQueryAPI taxonomyQueryAPI;

    private BackwardReasoner backwardReasoner;

    private boolean knowledgeBaseInitialized;
    private boolean taxonomyInitialized;

    public static COjDA getCOjDA() {
        // Construct dependencies
        Configuration config = new Config(COjDA.class);

        // Create the parsers
        RDFSParser rdfsParser = new RDFSParser();
        POSLParser poslParser = new POSLParser();
        RuleMLParser rmlParser = new RuleMLParser(config);
        TaxonomyQueryAPI taxonomyQueryAPI = new TaxonomyQueryAPI();

        // Create the reasoning engine
        BackwardReasoner backwardReasoner = new BackwardReasoner();

        // Create TopDownApp
        COjDA cojda = new COjDA(config, poslParser, rmlParser, taxonomyQueryAPI, backwardReasoner);

        return cojda;
    }

    private COjDA(Configuration config, POSLParser poslParser, RuleMLParser rmlParser,
            TaxonomyQueryAPI taxonomyQueryAPI, BackwardReasoner backwardReasoner) {

        this.config = config;

        // log4j is not intended for API usage
        this.config.setLogLevel(Level.OFF);

        this.poslParser = poslParser;
        this.rmlParser = rmlParser;
        this.taxonomyQueryAPI = taxonomyQueryAPI;
        this.backwardReasoner = backwardReasoner;

        knowledgeBaseInitialized = false;
        taxonomyInitialized = false;
        
        this.config.addPreferenceChangeListener(rmlParser);
    }

    /**
     * Configure the API
     * 
     * @param rmlFormat
     *            RuleML format which should be used (e.g. RuleML 1.0)
     * 
     * @param enableValidation
     *            If true, the XML validation will be enabled
     */
    public void configureAPI(RuleMLFormat rmlFormat, boolean enableValidation) {
        config.setSelectedRuleMLFormat(rmlFormat);
        config.setValidateRuleMLEnabled(enableValidation);
    }

    /**
     * Parse a given knowledge base in the given syntax format and initialize a
     * backward reasoner using the parsed knowledge base.
     * 
     * @param syntaxFormat
     *            The syntax format of the knowledge base
     * 
     * @param knowledgeBase
     *            The knowledge base which should be parsed
     * 
     * @throws ParseException
     * @throws ParsingException
     * @throws IOException
     * @throws RecognitionException
     * @throws TokenStreamException
     */
    public void initializeKnowledgeBase(SyntaxFormat syntaxFormat, String knowledgeBase)
            throws ParseException, ParsingException, IOException, RecognitionException, TokenStreamException {

        if (syntaxFormat == SyntaxFormat.RDFS) {
            throw new ParseException("RDFS cannot be used for knowledge base representation.");
        }
        
        Iterator kbIterator;
        if (syntaxFormat == SyntaxFormat.POSL) {
            poslParser.parseDefiniteClauses(knowledgeBase);
            kbIterator = poslParser.iterator();
        } else {
            rmlParser.parseRuleMLString(knowledgeBase);
            kbIterator = rmlParser.iterator();
        }
        initializeBackwardReasoner(kbIterator);
        knowledgeBaseInitialized = true;
    }

    /**
     * Parse a given knowledge base in the given syntax format and initialize a
     * backward reasoner using the parsed knowledge base.
     * 
     * @see COjDA#initializeKnowledgeBase(SyntaxFormat, String)
     * 
     */
    public void initializeKnowledgeBase(SyntaxFormat syntaxFormat, File knowledgeBase)
            throws FileNotFoundException, IOException, RecognitionException, TokenStreamException,
            ParseException, ParsingException {

        String fileContent = Util.readFile(knowledgeBase);
        initializeKnowledgeBase(syntaxFormat, fileContent);
    }

    /**
     * Initialize a taxonomy (POSL or RDFS)
     * 
     * @param syntaxFormat
     *            The syntax format of the taxonomy 
     * 
     * @param taxonomy
     *            The taxonomy which should be initialized
     * 
     * @throws ValidityException
     * @throws ParseException
     * @throws ParsingException
     * @throws IOException
     * @throws SubsumesException
     */
    public void initializeTaxonomy(SyntaxFormat syntaxFormat, String taxonomy) throws ValidityException,
            ParseException, ParsingException, IOException, SubsumesException {
        
        if (syntaxFormat == SyntaxFormat.RULEML) {
            throw new ParseException("RuleML cannot be used for taxonomy representation.");
        }
        
        taxonomyQueryAPI.initializeTaxonomy(syntaxFormat, taxonomy);
        taxonomyInitialized = true;
    }

    /**
     * Initialize a POSL or a RDFS taxonomy 
     * 
     * @see COjDA#initializeTaxonomy(SyntaxFormat, String)
     */
    public void initializeTaxonomy(SyntaxFormat syntaxFormat, File taxonomy) throws ValidityException,
            ParseException, ParsingException, IOException, SubsumesException {

        String fileContent = Util.readFile(taxonomy);
        initializeTaxonomy(syntaxFormat, fileContent);
    }

    /**
     * Issue a query on the knowledge base in either POSL or RuleML syntax
     * 
     * @param query
     *            The POSL or RuleML knowledge base query as a string.
     * 
     * @return The answer as a string formatted RuleML expression.
     * 
     * @throws RecognitionException
     * @throws TokenStreamException
     * @throws IOException
     * @throws ParsingException
     * @throws ParseException
     */
    public String issueKnowledgebaseQuery(SyntaxFormat syntaxFormat, String query)
            throws RecognitionException, TokenStreamException, ParseException, ParsingException, IOException {

        if (!knowledgeBaseInitialized) {
            throw new ParseException("No knowledge base available. Please initialize a knowledge base first.");
        }

        DefiniteClause definitiveClause;
        if (syntaxFormat == SyntaxFormat.POSL) {
            definitiveClause = poslParser.parseQueryString(query);
        } else {
            definitiveClause = rmlParser.parseRuleMLQuery(query);
        }

        Iterator solutionIterator = backwardReasoner.iterativeDepthFirstSolutionIterator(definitiveClause);
        ArrayList<BindingPair> solutionPairs = generateBindingObjects(solutionIterator);

        return generateRuleMLAnswerExpression(solutionPairs);
    }

    /**
     * Issue a query on the knowledge base in either POSL or RuleML syntax
     * 
     * @see COjDA#issueKnowledgebaseQuery(SyntaxFormat, String)
     */
    public String issueKnowledgebaseQuery(SyntaxFormat syntaxFormat, File query) throws RecognitionException,
            TokenStreamException, ParseException, ParsingException, IOException {

        String fileContent = Util.readFile(query);
        return issueKnowledgebaseQuery(syntaxFormat, fileContent);
    }

    /**
     * Issue a query on the taxonomy by using either a POSL or a RuleML query.
     * 
     * @param query
     *            The POSL or RuleML taxonomy query as a string.
     * 
     * @return The answer as a string formatted RuleML expression.
     * 
     * @throws ValidityException
     * @throws ParseException
     * @throws ParsingException
     * @throws IOException
     * @throws Exception
     */
    public String issueTaxonomyQuery(SyntaxFormat syntaxFormat, String query) throws ValidityException,
            ParseException, ParsingException, Exception {

        if (!taxonomyInitialized) {
            throw new ParseException("No taxonomy available. Please initialize a taxonomy first.");
        }

        return taxonomyQueryAPI.executeQuery(syntaxFormat, query);
    }

    /**
     * Issue a query on the taxonomy by using either a POSL or a RuleML query.
     * 
     * @see COjDA#issueTaxonomyQuery(SyntaxFormat, String)
     */
    public String issusTaxonomyQuery_RuleML(SyntaxFormat syntaxFormat, File query) throws ValidityException,
            ParseException, ParsingException, IOException, Exception {

        String fileContent = Util.readFile(query);
        return issueTaxonomyQuery(syntaxFormat, fileContent);
    }

    /**
     * This method will initialize the OO jDREW reasoning engine.
     * 
     * @param clauses
     *            The facts to initialize OO jDREW with.
     */
    private void initializeBackwardReasoner(Iterator clauses) {
        backwardReasoner = new BackwardReasoner();
        backwardReasoner.loadClauses(clauses);
        backwardReasoner = new BackwardReasoner(backwardReasoner.clauses, backwardReasoner.oids);
    }

    /**
     * This method will generate the Binding Pairs.
     * 
     * @param solutions
     *            The results of the queries.
     * 
     * @return An array list of all the binding pairs.
     */
    private ArrayList<BindingPair> generateBindingObjects(Iterator solutions) {

        ArrayList<BindingPair> pairs = new ArrayList<BindingPair>();

        while (solutions.hasNext()) {

            BackwardReasoner.GoalList gl = (BackwardReasoner.GoalList) solutions.next();
            Hashtable varbind = gl.varBindings;
            varSize = varbind.size();
            Enumeration e = varbind.keys();
            while (e.hasMoreElements()) {
                Object k = e.nextElement();
                String val = (String) varbind.get(k);
                String ks = (String) k;
                ks = ks.substring(1);

                StringTokenizer st = new StringTokenizer(ks, ":");
                if (st.countTokens() == 2) {
                    String var = st.nextToken().trim();
                    String type = st.nextToken().trim();
                    ks = "<Var type=\"" + type + "\">" + var + "</Var>";
                } else {
                    ks = "<Var>" + ks + "</Var>";
                }
                BindingPair bp = new BindingPair(ks, val);
                pairs.add(bp);
            }
        }

        return pairs;
    }

    /**
     * This method will generate the RuleML answer expression as the solution to
     * a query
     * 
     * @param solutionPairs
     *            All the solutions to the query.
     * 
     * @return RuleML answer expression based on the solutions given.
     */
    private String generateRuleMLAnswerExpression(ArrayList<BindingPair> solutionPairs) {

        String answer = "<RuleML>\n\t<Answer>\n";

        for (int i = 0; i < solutionPairs.size(); i++) {

            if (i % varSize == 0) {
                answer = answer + "\t\t<Rulebase>\n";
            }
            answer = answer + "\t\t\t<Equal>\n";

            BindingPair pair = solutionPairs.get(i);
            answer = answer + "\t\t\t\t" + pair.getVariable() + "\n";

            StringTokenizer st = new StringTokenizer(pair.getValue(), "\n");

            while (st.hasMoreTokens()) {
                answer = answer + "\t\t\t\t" + st.nextToken() + "\n";
            }
            answer = answer + "\t\t\t</Equal>";

            if (i % varSize == varSize - 1) {
                answer = answer + "\n\t\t</Rulebase>";
            }

            if (!(i == solutionPairs.size() - 1)) {
                answer = answer + "\n";
            }
        }

        answer += "\n\t</Answer>\n</RuleML>";

        return answer;
    }
}
