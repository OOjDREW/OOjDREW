// OO jDREW Version 0.93
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package jdrew.oo.util;

import java.io.*;
import java.util.*;

import nu.xom.*;
import org.apache.log4j.*;

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
    public static final int RULEML88 = 1;
    public static final int RULEML91 = 2;
    

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
     * Parses a file containing a knowledge base that is in the indicated
     * format. Currently only RuleML 0.88 + rests and RULEML 0.91 is supported;
     * If additional backed parsers are created then additional formats 
     * will be added.
     *
     * NOTE: It may be a good idea to add format autodetection based upon the
     * XSD and/or DTD that is referenced by the document.
     *
     * @param format int The integer code for the backend parser - currently
     * only RULEML88 and RULEML91 is accepted.
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
     *
     * @throws IOException An IOException is thrown if there is an error
     * reading the file from disk.
     */
          
    public void parseFile(int format, String filename) throws ParseException,
    ParsingException, ValidityException, IOException {

    	if (format != RULEML88 && format != RULEML91) {
    		throw new ParseException("Only RuleML 0.88 and RuleML 0.91 is currently supported.");
    	}

    	if(format == RULEML88){

    		RuleML88Parser rm88 = new RuleML88Parser(clauses);
    		Builder bl = new Builder();
    		File file = new File(filename);
    		Document doc = bl.build(file);
    		rm88.parseRuleMLDocument(doc);

    	}

    	if(format == RULEML91){

    		RuleML91Parser rm91 = new RuleML91Parser(clauses);
    		Builder bl = new Builder();
    		File file = new File(filename);
    		Document doc = bl.build(file);
    		rm91.parseRuleMLDocument(doc);

    	}

    }

    public void parseFile(int format, File kb) throws ParseException, ParsingException, ValidityException, IOException {

    	if (format != RULEML88 && format != RULEML91) {
    		throw new ParseException("Only RuleML 0.88 and RuleML 0.91 is currently supported.");
    	}

    	if(format == RULEML88){
    		System.out.println("88 chosen (Remove this later)");
    		RuleML88Parser rm88 = new RuleML88Parser(clauses);
    		Builder bl = new Builder();
   
    		Document doc = bl.build(kb);
    		rm88.parseRuleMLDocument(doc);

    		}
    	if(format == RULEML91){
    		System.out.println("91 chosen (Remove this later)");
    		RuleML91Parser rm91 = new RuleML91Parser(clauses);
    		Builder bl = new Builder();
   
    		Document doc = bl.build(kb);
    		rm91.parseRuleMLDocument(doc);
    	}
    }
    
    /**
     * Parses a string containing a knowledge base that is in the indicated
     * format. Currently only RuleML 0.88 + rests and RuleML 0.91 are supported;
     * If additional backed parsers are created then additional formats will 
     * be added.
     *
     * NOTE: It may be a good idea to add format autodetection based upon the
     * XSD and/or DTD that is referenced by the document.
     *
     * @param format int The integer code for the backend parser - currently
     * only RULEML88 and RULEML91 is accepted.
     *
     * @param contents String The string containing the knowledge base to be
     * parsed.
     *
     * @throws ParseException A ParseException is thrown if there is an error
     * in the document that causes parsing to fail.
     *
     * @throws ParsingException A ParsingException is thrown if there is an
     * error in parsing the document at an XML level.
     *
     * @throws ValidityException A ValidityException is thrown if the XML
     * document is not well formed or does not conform to the DTD specified.
     *
     * @throws IOException An IOException is thrown if there is an error
     * reading the file from disk.
     */
    
    public void parseRuleMLString(int format, String contents) throws
            ParseException, ParsingException, ValidityException, IOException {
        
        if (format != RULEML88 && format != RULEML91) {
            throw new ParseException("Only RuleML 0.88 and 0.91 is currently supported.");
        }
		
		if(format == RULEML88){
	
        	RuleML88Parser rm88 = new RuleML88Parser(clauses);
       		Builder bl = new Builder();

       		StringReader sr = new StringReader(contents);
      	 	Document doc = bl.build(sr);
        
       		rm88.parseRuleMLDocument(doc);
    	}
    	
    	if(format == RULEML91){
	
        	RuleML91Parser rm91 = new RuleML91Parser(clauses);
        	Builder bl = new Builder();
	 
       		StringReader sr = new StringReader(contents);
     	    Document doc = bl.build(sr);
        
     	    rm91.parseRuleMLDocument(doc);
    	}


    }
    //XOm element
    
       public DefiniteClause parseRuleMLQuery(String contents) throws
            ParseException, ParsingException, ValidityException, IOException {
    	   
        	RuleMLQueryParser rmq = new RuleMLQueryParser(clauses);
        	Builder bl = new Builder();
	 
       		StringReader sr = new StringReader(contents);
     	    Document doc = bl.build(sr);
        
     	    rmq.parseRuleMLDocument(doc);
     	    //make new parser
     	    //get the one out     	    
     	    Iterator it = clauses.iterator();
     	    
     	        DefiniteClause dc = null;   	
       
       	   while (it.hasNext()) {
        	
        	
       	 	dc = (DefiniteClause) it.next();
        	//System.out.println("Loaded clause: " + dc.toPOSLString());
        	
           }
          //System.out.println(dc.toPOSLString());
          
     	    return dc;
     	       
     	    
    	}
   
}


/**
 * This class implements the back-end parser for the RuleML 0.91
 * format.
 *
 * <p>Title: OO jDREW</p>
 *
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.91</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * @author Ben Craig
 * @version 0.93
 */

class RuleML91Parser {

    private Hashtable skolemMap;

    private Vector clauses;
                  
    private Vector variableNames;

	private static final String RULEBASE = "Rulebase";
    private static final String ASSERT = "Assert";
    private static final String AND = "And";
    private static final String ATOM = "Atom";
    private static final String MAPCLOSURE = "mapClosure";
    private static final String UNIVERSAL = "universal";
    private static final String IMPLIES = "Implies";
    private static final String TYPE = "type";
    private static final String PLEX = "Plex";
    private static final String CTERM = "Expr";
    private static final String IND = "Ind";
    private static final String VAR = "Var";
    private static final String RESL = "resl";
    private static final String SLOT = "slot";
    private static final String NAF = "Naf";
    private static final String REPO = "repo";
    private static final String CTOR = "Fun";
    private static final String REL = "Rel";
    private static final String CLOSURE = "closure";
    private static final String OID = "oid";
    private static final String SKOLEM = "Skolem";
    private static final String NEG = "Neg";
        
	private static final String EXPR = "Expr";
	private static final String DATA = "Data";
	
	private static final String OP = "op";
	
	//used for data
	private static final String XSITYPE = "type";

	
    /**
     * This is used for generating unique anonymous variable ids
     */
    private static int anonid = 1;

    /**
     * This is used to indicate if the document has an inner close attribute.
     */
    private boolean hasiclose = false;

    Logger logger = Logger.getLogger("jdrew.oo.util.RuleMLParser");

    /**
     * Constructs the back-end parser.
     *
     * @param clauses Vector The vector to use as a buffer - this is generally
     * passed by the RuleMLParser front-end.
     */
    public RuleML91Parser(Vector clauses) {
        this.clauses = clauses;
    }

    /**
     * This method is used to parse a RuleML 0.91 document that is stored in
     * a XOM tree.
     *
     * @param doc Document The XOM Document object that represents the RuleML
     * document to be parsed.
     *
     * @throws ParseException A ParseException is thrown if there is an error
     * parseing.
     */
            
    public void parseRuleMLDocument(Document doc) throws ParseException {
        this.skolemMap = new Hashtable();
        Element root = doc.getRootElement();
        if (!root.getLocalName().equals(ASSERT)) {
            throw new ParseException(
                    "Root element of RuleML 0.91 Document should be Assert.");
        }

        Element and = root.getFirstChildElement(RULEBASE);
        if (and == null) {
            throw new ParseException(
                    "Root element must contain Rulebase element.");
        }

        if (and.getAttribute(MAPCLOSURE) != null) {
            hasiclose = true;
            if (!and.getAttributeValue(MAPCLOSURE).equals(UNIVERSAL)) {
                throw new ParseException(
                        "Only universal inner closures are currently supported.");
            }

        } else {
            logger.info("Document root has not innerclose attribute. Indiviual clauses must have closure attributes.");
        }
	
        Elements els = and.getChildElements();
        for (int i = 0; i < els.size(); i++) {
            Element el = els.get(i);
            if (el.getLocalName().equals(ATOM)) {
                clauses.add(parseFact(el));
            } else if (el.getLocalName().equals(NEG)){
                clauses.addAll(parseNegFact(el));
            }
            else if (el.getLocalName().equals(IMPLIES)) {
                clauses.addAll(parseImplies(el));
            }
        }
    }

    /**
     * This method is used to parse an Assertion in the RuleML Document.
     *
     * @param ass Element The XOM Element objec that represents the assertion.
     *
     * @return Term A term object that represents the assertion in a way that
     * can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * assertion.
     */
          
    private Term parseAssert(Element ass) throws ParseException {
        Elements els = ass.getChildElements();
        if (els.size() != 1) {
            throw new ParseException(
                    "An Assert element can only contain one child.");
        }
		
        Element el = els.get(0);
		
        if (el.getLocalName().equals(ATOM)) {
            DefiniteClause dc = parseFact(el, false);
            Vector v = new Vector();
            for (int i = 0; i < dc.atoms.length; i++) {
                v.add(dc.atoms[i]);
            }
            Term t = new Term(SymbolTable.IASSERT, SymbolTable.INOROLE,
                              Types.IOBJECT, v);
            t.setAtom(true);
            return t;
        } else if (el.getLocalName().equals(IMPLIES)) {
            Vector v2 = parseImplies(el, false);
            DefiniteClause dc = (DefiniteClause)v2.get(0);
            Vector v = new Vector();
            for (int i = 0; i < dc.atoms.length; i++) {
                v.add(dc.atoms[i]);
            }
            Term t = new Term(SymbolTable.IASSERT, SymbolTable.INOROLE,
                              Types.IOBJECT, v);
            t.setAtom(true);
            return t;
        } else {
            throw new ParseException(
                    "Assert element can only contain Atom (fact) or Implies elements.");
        }
    }
    
    /**
     * This method is used to parse a Negative Fact in the RuleML Document.
     *
     * @param neg Element The XOM Element object that represents the neg fact.
     *
     * @return Vector a vector object that represents the negFact in a way that
     * can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * negFact.
     */
    private Vector parseNegFact(Element neg) throws ParseException {
        this.variableNames = new Vector();
        this.varClasses = new Hashtable();

        Elements atoms = neg.getChildElements("Atom");
        if(atoms.size() != 1){
            logger.error("OO jDREW only supports classical negation over single atoms.");
            throw new ParseException("OO jDREW only supports classical negation over single atoms.");
        }
		
        Element atom = atoms.get(0);

        if (!hasiclose)
        {
            String closure = atom.getAttributeValue(CLOSURE);
            if (closure == null) {
                logger.error("No closure on clause.");
                throw new ParseException("No closure on clause.");
            }

            if (!closure.equals(UNIVERSAL)) {
                logger.error("Only universal closures are currently supported.");
                throw new ParseException("Only universal inner closures are currently supported.");
            }
        }
		
        Term atm = parseAtom(atom, true, true);

        Hashtable types = this.buildTypeTable();
        this.fixVarTypes(atm, types);

        Vector atms = new Vector();
        atms.add(atm);

        DefiniteClause dc = new DefiniteClause(atms, variableNames);

        Vector v = new Vector();
        v.add(dc);

        // Add code to generate consistency check

        //This should be redone to create the consistency check better

        String atmstr = dc.toPOSLString();

        String clause = "$inconsistent() :-";
        clause += atmstr.substring(0, atmstr.length() - 1) + ",";
        clause += "\"" + atmstr.substring(atmstr.indexOf("-") + 1);

        System.err.println(clause);

        POSLParser pp = new POSLParser();
        try{
            v.add(pp.parseDefiniteClause(clause));
        } catch(Exception e){
            throw new ParseException("Error creating consistency check.");
        }

        return v;
    }

    /**
     * This method is used to parse a fact element. The use of this method is
     * deprecated; new code should use
     * parseFact(Element atom, boolean newVarnames); this version is provided
     * for backwards compatability only.
     *
     * @param atom Element The XOM Element object that represents the fact to be
     * parsed.
     *
     * @return DefiniteClause A DefiniteClause data structure that represents
     * the fact in a way that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * fact.
     */
    private DefiniteClause parseFact(Element atom) throws ParseException {
        return parseFact(atom, true);
    }

    /**
     * This method is used to parse a fact element, creating a new variable
     * name list if indicated. Typically a new variable list is wanted; but in
     * certain cases (such as parsing an inner clause in an assert) the same
     * variable list must be used.
     *
     * @param atom Element The XOM Element objec that represents the fact to be
     * parsed.
     *
     * @param newVarnames boolean If true a new variable names list is created
     * and used; otherwise the current variable name list is used.
     *
     * @return DefiniteClause A DefiniteClause data structure that represents
     * the fact in a way that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * fact.
     */
    private DefiniteClause parseFact(Element atom, boolean newVarnames) throws
            ParseException {
        if (newVarnames) {
            this.variableNames = new Vector();
            this.varClasses = new Hashtable();
        }
		
        if (!hasiclose) {
            //No inner close - should have
            String closure = atom.getAttributeValue(CLOSURE);
            if (closure == null) {
                logger.error("No closure on clause.");
                throw new ParseException("No closure on clause.");
            }

            if (!closure.equals(UNIVERSAL)) {
                logger.error("Only universal closures are currently supported.");
                throw new ParseException(
                        "Only universal inner closures are currently supported.");
            }
        }

        Term atm = parseAtom(atom, true, false);

        Hashtable types = this.buildTypeTable();
        this.fixVarTypes(atm, types);

        Vector atoms = new Vector();
        atoms.add(atm);

        DefiniteClause dc = new DefiniteClause(atoms, variableNames);
        return dc;
    }

    /**
     * This method is used to parse a implies (implication) element. The use of
     * this method is deprecated; new code should use
     * parseImplies(Element atom, boolean newVarnames); this version is provided
     * for backwards compatability only.
     *
     * @param implies Element The XOM Element objec that represents the
     * implication to be parsed.
     *
     * @return DefiniteClause A DefiniteClause data structure that represents
     * the implication in a way that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * implication.
     */
    private Vector parseImplies(Element implies) throws ParseException {
        return parseImplies(implies, true);
    }

    /**
     * This method is used to parse a implication element, creating a new
     * variable name list if indicated. Typically a new variable list is wanted;
     * but in certain cases (such as parsing an inner clause in an assert) the
     * same variable list must be used.
     *
     * @param implies Element The XOM Element objec that represents the
     * implication to be parsed.
     *
     * @param newVarnames boolean If true a new variable names list is created
     * and used; otherwise the current variable name list is used.
     *
     * @return DefiniteClause A DefiniteClause data structure that represents
     * the implication in a way that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * implication.
     */
    private Vector parseImplies(Element implies, boolean newVarnames) throws
            ParseException {
        if (newVarnames) {
            this.variableNames = new Vector();
            this.varClasses = new Hashtable();
        }

        Vector newclauses = new Vector();

        if (!hasiclose) {
            //No inner close - should have
            String closure = implies.getAttributeValue(CLOSURE);
            if (closure == null) {
                logger.error("No closure on clause.");
                throw new ParseException("No closure on clause.");
            }

            if (!closure.equals(UNIVERSAL)) {
                logger.error("Only universal closures are currently supported.");
                throw new ParseException(
                        "Only universal inner closures are currently supported.");
            }
        }
		//implies need 2 child elements
        Elements els = implies.getChildElements();
        if (els.size() != 2) {
            throw new ParseException(
                    "Implies element should have 2 child elements.");
        }

        Vector atoms = new Vector();

        Element head = els.get(1);
        if (head.getLocalName().equals(ATOM)) {
            atoms.add(parseAtom(head, true, false));
        } else if (head.getLocalName().equals(NEG)){
            Elements headatms = head.getChildElements(ATOM);
            if(headatms.size() != 1)
                throw new ParseException("Neg should have one ATOM element");

            Term atom = parseAtom(headatms.get(0), true, true);
            atoms.add(atom);

            String atomstr = atom.toPOSLString(true);

            String clause = "$Sinconsistent() :-";
            clause += atomstr + ", \"" + atomstr.substring(6) + ".";

            System.err.println(clause);

            POSLParser pp = new POSLParser();
            try{
                DefiniteClause dc2 = pp.parseDefiniteClause(clause);
                if (dc2 != null)
                    newclauses.add(dc2);
            } catch(Exception e){
                throw new ParseException("Error creating inconsistency check rule.");
            }
        } else {
            throw new ParseException(
                    "Second element of Implies should always be an Atom or Neg element.");
        }

        Element body = els.get(0);
        if (body.getLocalName().equals(ATOM)) {
            atoms.add(parseAtom(body, false, false));
        } else if (body.getLocalName().equals(NAF)) {
            atoms.add(parseNaf(body));
        } else if (body.getLocalName().equals(ASSERT)) {
            atoms.add(parseAssert(body));
        } else if (body.getLocalName().equals(NEG)) {
            atoms.add(parseAtom(body, false, true));
        }
        else if (body.getLocalName().equals(AND)) {
            els = body.getChildElements();
            for (int i = 0; i < els.size(); i++) {
                Element el = els.get(i);
                if (el.getLocalName().equals(ATOM)) {
                    atoms.add(parseAtom(el, false, false));
                } else if (el.getLocalName().equals(NAF)) {
                    atoms.add(parseNaf(el));
                } else if (el.getLocalName().equals(ASSERT)) {
                    atoms.add(parseAssert(el));
                } else if (el.getLocalName().equals(NEG)){
                    atoms.add(parseAtom(el, false, true));
                } else {
                    throw new ParseException(
                            "Implies And element should only contain Atom and Naf elements.");
                }
            }
        } else {
            throw new ParseException(
                    "First element of Implies should be an Atom, Naf or And element.");
        }

        logger.debug("Building Types");
        Hashtable types = this.buildTypeTable();
        logger.debug("Built Types");
        Iterator it = atoms.iterator();
        int i = 0;
        while(it.hasNext()){
            Term t = (Term)it.next();
            this.fixVarTypes(t, types);
            logger.debug("Fixed atom : " + i++);

        }

        DefiniteClause dc = new DefiniteClause(atoms, variableNames);
        newclauses.add(0, dc);
        return newclauses;
    }

    /**
     * This method is used to parse and <oid> element.
     *
     * @param oid Element The XOM element that represents the oid to be parsed.
     *
     * @return Term Returns the data structure that represents the oid in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the oid.
     */
       
    private Term parseOid(Element oid) throws ParseException {
        Element el = oid.getChildElements().get(0);
        Term t;
        if (el.getLocalName().equals(IND)) {
            t = parseInd(el);
        } else if(el.getLocalName().equals(DATA)) {
            t = parseData(el);
        } else if(el.getLocalName().equals(SKOLEM)) {
            t = parseVar(el);
        } else if (el.getLocalName().equals(VAR)) {
            t = parseVar(el);
        } else if (el.getLocalName().equals(CTERM)) {
            t = parseCTerm(el);
        } else {
            throw new ParseException("oid can only contain Ind, Data, Var or Cterm.");
        }
        t.role = SymbolTable.IOID;
        return t;
    }

    /**
     * Method to parse an individual constant (Ind)
     *
     * @param ind Element The XOM element that represents the Ind to be parsed.
     *
     * @return Term Returns the data structure that represents the Ind in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the Ind.
     */
     
    private Term parseInd(Element ind) throws ParseException {
        String symbol = ind.getValue().trim();
        int sym = SymbolTable.internSymbol(symbol);
        Attribute type = ind.getAttribute(TYPE);
        
        
        
        int typeid = Types.IOBJECT;
        if (type != null) {
            typeid = Types.typeID(type.getValue().trim());
            if (typeid == -1) {
                throw new ParseException("Type " + type.getValue().trim() +
                                         " is not defined.");
            }
        }

        return new Term(sym, SymbolTable.INOROLE, typeid);
    }
   
    /**
     * Method to parse an Data (Data)
     *
     * @param ind Element The XOM element that represents the Data to be parsed.
     *
     * @return Term Returns the data structure that represents the Data in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the Data.
     */
     
     //this is where you change stuff about the name space
     //uri anchoring and such, test to see if it supports any at all yet
     
     //Also INOROLE may be changed later not sure yet when creating a Data
     //Term Data has no role so it will probally not change
     
    private Term parseData(Element data) throws ParseException {
        String symbol = data.getValue().trim();
        int sym = SymbolTable.internSymbol(symbol);
        
      //Attribute type = data.getAttribute(XSITYPE,"http://www.w3.org/2001/XMLSchema-instance");
        Attribute type = data.getAttribute(TYPE);
      
		//if(type != null)       
     	 //System.out.println("PRINTING: " + type.getValue()); 

        int typeid = Types.IOBJECT;
        if (type != null) {
            typeid = Types.typeID(type.getValue().trim());
            if (typeid == -1) {
                throw new ParseException("Type " + type.getValue().trim() +
                                         " is not defined.");
            }
        }

        Term t1 = new Term(sym, SymbolTable.INOROLE, typeid);
    	t1.setData(true);
    
    	return t1;
    }
    
    /**
     * Method to parse a Var (Variable)
     *
     * @param var Element The XOM element that represents the Var
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the Var in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the Var.
     */
     
    private Term parseVar(Element var) throws ParseException {
        String symbol = var.getValue().trim();
        if (symbol.equals("")) {
            symbol = "$ANON" + anonid++;
        }

        int sym = this.internVariable(symbol);
        Attribute type = var.getAttribute(TYPE);
        int typeid = Types.IOBJECT;
        if (type != null) {
            typeid = Types.typeID(type.getValue().trim());
            if (typeid == -1) {
                throw new ParseException("Type " + type.getValue().trim() +
                                         " is not defined.");
            }
        }

        Integer symI = new Integer(sym);
        Integer typeI = new Integer(typeid);

        logger.debug("Parsing variable: symbol = " + symI + " type = " + typeI);

        Vector v;
        if(this.varClasses.containsKey(symI)){
            v = (Vector)varClasses.get(symI);
        }else{
            v = new Vector();
            varClasses.put(symI, v);
        }

        v.add(typeI);

        logger.debug("Added Type Information");

        return new Term(sym, SymbolTable.INOROLE, typeid);
    }

    /**
     * Method to parse a plex
     *
     * @param plex Element The XOM element that represents the plex
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the plex in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the plex.
     */
        
    private Term parsePlex(Element plex) throws ParseException {
        Elements els = plex.getChildElements();
        Vector subterms = new Vector();
        for (int i = 0; i < els.size(); i++) {
            Element el = els.get(i);
            if (el.getLocalName().equals(PLEX)) {
                subterms.add(parsePlex(el));
            } else if (el.getLocalName().equals(CTERM)) {
                subterms.add(parseCTerm(el));
            } else if (el.getLocalName().equals(IND)) {
                subterms.add(parseInd(el));
            } else if (el.getLocalName().equals(DATA)) {
                subterms.add(parseData(el));
            } else if (el.getLocalName().equals(SKOLEM)) {
                subterms.add(parseSkolem(el));
            } else if (el.getLocalName().equals(VAR)) {
                subterms.add(parseVar(el));
            } else if (el.getLocalName().equals(SLOT)) {
                subterms.add(parseSlot(el));
            } else if (el.getLocalName().equals(RESL)) {
                subterms.add(parseResl(el));
            } else if (el.getLocalName().equals(REPO)) {
                subterms.add(parseRepo(el));
            } else {
                throw new ParseException(
                        "Plex should only contain Plex, Cterm, Ind, Data, Var and slot, repo, resl.");
            }

        }

        Term t = new Term(SymbolTable.IPLEX, SymbolTable.INOROLE, Types.IOBJECT,
                          subterms);
        return t;
    }

    /**
     * Method to parse a Expr (Expresion)
     *
     * @param cterm Element The XOM element that represents the Expr
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the Expr in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the Expr.
     */
          
    private Term parseCTerm(Element cterm) throws ParseException {
        
        Elements els = cterm.getChildElements();
        Element op = els.get(0);
        
        boolean foundOp = false;
        
        if (op.getLocalName().equals(OP)) {
			foundOp = true;
        }
		
		Element ctor = null;
		if(foundOp){
			
			Elements ctorTag = op.getChildElements();
			ctor = ctorTag.get(0);
        }
        
        if(!foundOp){
        	ctor = els.get(0);
        }
                
        if (!ctor.getLocalName().equals(CTOR)) {
            throw new ParseException(
                    "First child of op in an Expr must be a Fun element.");
        }

        int symbol = SymbolTable.internSymbol(ctor.getValue().trim());

        int typeid = Types.IOBJECT;
        Attribute type = cterm.getAttribute(TYPE);
        if (type != null) {
            typeid = Types.typeID(type.getValue().trim());
            if (typeid == -1) {
                throw new ParseException("Type " + type.getValue().trim() +
                                         " is not defined.");
            }
        }

        Vector subterms = new Vector();
        for (int i = 1; i < els.size(); i++) {
            Element el = els.get(i);
            if (el.getLocalName().equals(PLEX)) {
                subterms.add(parsePlex(el));
            } else if (el.getLocalName().equals(CTERM)) {
                subterms.add(parseCTerm(el));
            } else if (el.getLocalName().equals(IND)) {
                subterms.add(parseInd(el));
            } else if (el.getLocalName().equals(DATA)) {
                subterms.add(parseData(el));
            } else if (el.getLocalName().equals(SKOLEM)) {
                subterms.add(parseSkolem(el));
            } else if (el.getLocalName().equals(VAR)) {
                subterms.add(parseVar(el));
            } else if (el.getLocalName().equals(SLOT)) {
                subterms.add(parseSlot(el));
            } else if (el.getLocalName().equals(RESL)) {
                subterms.add(parseResl(el));
            } else if (el.getLocalName().equals(REPO)) {
                subterms.add(parseRepo(el));
            } else {
                throw new ParseException(
                        "Expr should only contain Plex, Expr, Ind, Data, Var and slot, repo, resl.");
            }

        }

        Term t = new Term(symbol, SymbolTable.INOROLE, typeid, subterms);
        return t;
    }

    /**
     * Method to parse an Atom
     *
     * @param Atom Element The XOM element that represents the Atom
     * to be parsed.
     *
     * @param head boolean This tells the engine if the atom is a head of a rule.
     *
     * @param neg boolean This tells the engine if the atom is a negative atom (Neg)
     *
     * @return Term Returns the data structure that represents the Atom in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the Atom.
     */
     
    private Term parseAtom(Element atom, boolean head, boolean neg) throws ParseException {
        
        Elements els = atom.getChildElements();
        boolean foundoid = false;
		
		//checking for op tag before the rel
        Element op = els.get(0);
        
        boolean optionalOp = false;
        
        if (op.getLocalName().equals(OP)) {
        	   optionalOp = true;
        }   
        
        Element rel = null;
        
        if(optionalOp){   
        	Elements relTag = op.getChildElements();
        	rel = relTag.get(0);
        
        }
         
        if(!optionalOp){
        	rel = els.get(0);
        	
        } 
                
        if (!rel.getLocalName().equals(REL)) {
            throw new ParseException(
                    "First child of op in an atom must be a Rel element.");
        }
		
        String relname = rel.getValue().trim();
        if(neg)
            relname = "$neg-" + relname;

        int symbol = SymbolTable.internSymbol(relname);

        Vector subterms = new Vector();
        for (int i = 1; i < els.size(); i++) {
            Element el = els.get(i);
            if (el.getLocalName().equals(PLEX)) {
                subterms.add(parsePlex(el));
            } else if (el.getLocalName().equals(CTERM)) {
                subterms.add(parseCTerm(el));
            } else if (el.getLocalName().equals(IND)) {
                subterms.add(parseInd(el));
            } else if (el.getLocalName().equals(DATA)) {
                subterms.add(parseData(el));
            } else if (el.getLocalName().equals(SKOLEM)){
                subterms.add(parseSkolem(el));
            } else if (el.getLocalName().equals(VAR)) {
                subterms.add(parseVar(el));
            } else if (el.getLocalName().equals(SLOT)) {
                subterms.add(parseSlot(el));
            } else if (el.getLocalName().equals(RESL)) {
                subterms.add(parseResl(el));
            } else if (el.getLocalName().equals(REPO)) {
                subterms.add(parseRepo(el));
            } else if (el.getLocalName().equals(OID)) {
                if (foundoid) {
                    throw new ParseException(
                            "Atom should only contain one oid element.");
                }
                subterms.add(parseOid(el));
                foundoid = true;
            } else {
                throw new ParseException(
                        "Atom should only contain Plex, Cterm, Ind, Data, Var, slot, repo, resl and oid.");
            }
         	
        }
		//if foundoid is false
		//no idea what this is doing
        if (!foundoid) {
            if (head) {
                String symname = "$gensym" + SymbolTable.genid++;
                int symid = SymbolTable.internSymbol(symname);
                Term t2 = new Term(symid, SymbolTable.IOID, Types.IOBJECT);
                subterms.add(t2);
            } else {
                String varname = "$ANON" + anonid++;
                int symid = this.internVariable(varname);
                Vector types = new Vector();
                types.add(new Integer(Types.IOBJECT));
                this.varClasses.put(new Integer(symid), types);
                Term t2 = new Term(symid, SymbolTable.IOID, Types.IOBJECT);
                subterms.add(t2);
            }
        }

		

        Term t = new Term(symbol, SymbolTable.INOROLE, Types.IOBJECT, subterms);
        t.setAtom(true);
        return t;
    }

    /**
     * Method to parse a Slot
     *
     * @param slot Element The XOM element that represents the slot
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the slot in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the slot.
     */
    
    private Term parseSlot(Element slot) throws ParseException {
        Elements els = slot.getChildElements();
        if (els.size() != 2) {
            throw new ParseException("Slot must have two child elements.");
        }
        
        Element name = els.get(0);
		boolean dataSlot = false;
		if(name.getLocalName().equals(DATA)){
			dataSlot = true;
		}
       
        if (!name.getLocalName().equals(IND) && !name.getLocalName().equals(DATA)) {
            throw new ParseException("Only Ind and Data slot names are supported.");
        }    
		//Getting the Role from the symbol Table, it will assign one if it
		//doesnt already exist
        int role = SymbolTable.internRole(name.getValue().trim());
       
        Element value = els.get(1);
        
        Term t;
		//Figuring out the type of term it should be
        if (value.getLocalName().equals(PLEX)) {
            t = parsePlex(value);
        } else if (value.getLocalName().equals(CTERM)) {
            t = parseCTerm(value);
        } else if (value.getLocalName().equals(IND)) {
            t = parseInd(value);
        } else if (value.getLocalName().equals(DATA)) {
            t = parseData(value);
        } else if (value.getLocalName().equals(SKOLEM)){
            t = parseSkolem(value);
        } else if (value.getLocalName().equals(VAR)) {
            t = parseVar(value);
        } else {
            throw new ParseException(
                    "Slot value should be either Plex, Expr, Ind, Data or Var.");
        }
		
        t.setRole(role);
        
        if(dataSlot){
        	t.setDataSlot(true);
        }
        
        return t;
    }
   
    /**
     * Method to parse a resl (Rested Slot)
     *
     * @param resl Element The XOM element that represents the resl
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the resl in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the resl.
     */
          
    private Term parseResl(Element resl) throws ParseException {
        Elements els = resl.getChildElements();
        if (els.size() > 1) {
            throw new ParseException(
                    "resl element should only contain child element.");
        }

        Element el = els.get(0);
        Term t;
        if (el.getLocalName().equals(VAR)) {
            t = parseVar(el);
        } else if (el.getLocalName().equals(PLEX)) {
            t = parsePlex(el);
        } else {
            throw new ParseException(
                    "resl element should only contain Var or Plex as a child element.");
        }

        t.setRole(SymbolTable.IREST);
        return t;
    }

    /**
     * Method to parse a repo (Rested Positional Slot)
     *
     * @param repo Element The XOM element that represents the repo
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the repo in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the repo.
     */ 
     
    private Term parseRepo(Element repo) throws ParseException {
        Elements els = repo.getChildElements();
        if (els.size() > 1) {
            throw new ParseException(
                    "repo element should only contain child element.");
        }

        Element el = els.get(0);
        Term t;
        if (el.getLocalName().equals(VAR)) {
            t = parseVar(el);
        } else if (el.getLocalName().equals(PLEX)) {
            t = parsePlex(el);
        } else {
            throw new ParseException(
                    "repo element should only contain Var or Plex as a child element.");
        }

        t.setRole(SymbolTable.IPREST);
        return t;
    }

    /**
     * Method to parse a naf (Negation as Failure)
     *
     * @param naf Element The XOM element that represents the naf atom
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the naf atom in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the naf.
     */ 

    private Term parseNaf(Element naf) throws ParseException {
        Elements els = naf.getChildElements();
        if (els.size() != 1) {
            throw new ParseException(
                    "Naf elements should only contain one child.");
        }

        Element el = els.get(0);
        Vector subterms = new Vector();
        if (el.getLocalName().equals(ATOM)) {
            subterms.add(parseAtom(el, false, false));
        } else if (el.getLocalName().equals(AND)) {
            els = el.getChildElements();
            for (int i = 0; i < els.size(); i++) {
                Element el2 = els.get(i);
                if (!el2.getLocalName().equals(ATOM)) {
                    throw new ParseException(
                            "And child of Naf element should only contain Atom elements.");
                }

                subterms.add(parseAtom(el2, false, false));
            }
        } else {
            throw new ParseException(
                    "Naf element should only contain Atom or And child element.");
        }

        Term t = new Term(SymbolTable.INAF, SymbolTable.INOROLE, Types.IOBJECT,
                          subterms);

        t.setAtom(true);
        return t;
    }

    /**
     * Method to parse a sko(Skolem Constant)
     *
     * @param sko Element The XOM element that represents the sko
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the sko in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the sko.
     */ 
     
    private Term parseSkolem(Element sko) throws ParseException{
        String skoname = sko.getValue().trim();

        if(skoname.equals("")){
            return new Term(SymbolTable.internSymbol("$gensym" + SymbolTable.genid++),
                            SymbolTable.INOROLE, Types.ITHING);
        }else{
            if(this.skolemMap.containsKey(skoname)){
                String sym = (String)skolemMap.get(skoname);
                return new Term(SymbolTable.internSymbol(sym),
                                  SymbolTable.INOROLE, Types.ITHING);
            }else{
                String sym = "$gensym" + (SymbolTable.genid++) + "$" + skoname;
                skolemMap.put(skoname, sym);
                return new Term(SymbolTable.internSymbol(sym),
                                SymbolTable.INOROLE, Types.ITHING);
            }
        }
    }

    /**
     * Initialize a variable for use. Stores the variable name and assigns a
     * integer identifier if it is unused in this clause; returns the existing
     * identifier if it has already been used in this clause.
     *
     * Variables are Negative and Ind are positive
     *
     * @param varName String The variable name.
     *
     * @return int The integer identifier for this variable
     */
        
    private int internVariable(String varName) {
        int idx;

        idx = variableNames.indexOf(varName);
        if (idx == -1) {
            idx = variableNames.size();
            variableNames.add(varName);
        }

        return -(idx + 1);
    }

    /**
     * A vector to hold the class information for the variables in the current
     * clause. This is used for normalizing the types given to a variable if
     * more than one type is given to a variable.
     *
     * For example: in the following clause p(?x: type1, ?x:type2). the types
     * are not the same on all occurances of the variable ?x - therefore the
     * types will be normalized and receive a type that is the intersection of
     * type1 and type2.
     */
    private Hashtable varClasses;

    /**
     * A method that will go through a term and fix all variable types to be
     * consistant.
     *
     * @param ct Term The term to normalize the types in.
     *
     * @param types Hashtable A hash table containing the normalized types for
     * each variable in the clause.
     */
 
    private void fixVarTypes(Term ct, Hashtable types) {
       // logger.debug("Fixing term: " + ct.toPOSLString(true));
        for (int i = 0; i < ct.subTerms.length; i++) {
            if (ct.subTerms[i].isCTerm()) {
                fixVarTypes(ct.subTerms[i], types);
            } else if (ct.subTerms[i].getSymbol() < 0) {
                Integer sym = new Integer(ct.subTerms[i].getSymbol());
                //logger.debug("Fixing symbol = " + sym);
                Integer type = (Integer)types.get(sym);
                //logger.debug("Type = " + type);
                ct.subTerms[i].type = type.intValue();
            }
        }
    }

    /**
     * A method to find the normalized types for each variable in the clause.
     *
     * @return Hashtable A hashtable containing the normalized type for each
     * variable in the current clause.
     *
     * @throws ParseException A ParseException is thrown a variable does not
     * have a normalized form (type that is an intersection of all given types);
     * since Nothing inherits from all types this should never occur.
     */

    private Hashtable buildTypeTable() throws ParseException {
        Hashtable ht = new Hashtable();
        Enumeration e = varClasses.keys();

        while (e.hasMoreElements()) {
            Object key = e.nextElement();
            Vector value = (Vector) varClasses.get(key);
            int[] types = new int[value.size()];
            for (int i = 0; i < types.length; i++) {
                types[i] = ((Integer) value.get(i)).intValue();
            }

            int type = Types.greatestLowerBound(types);
            ht.put(key, new Integer(type));
        }
        return ht;
    }

}

/**
 * This class implements the back-end parser for the RuleML 0.88 (+ rests)
 * format.
 * <p>Title: OO jDREW</p>
 *
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.88</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * @author Marcel A. Ball
 * @version 0.93
 */
 
class RuleML88Parser {

    private Hashtable skolemMap;

    private Vector clauses;
                  
    private Vector variableNames;

    private static final String ASSERT = "Assert";
    private static final String AND = "And";
    private static final String ATOM = "Atom";
    private static final String MAPCLOSURE = "mapClosure";
    private static final String UNIVERSAL = "universal";
    private static final String IMPLIES = "Implies";
    private static final String TYPE = "type";
    private static final String PLEX = "Plex";
    private static final String CTERM = "Cterm";
    private static final String IND = "Ind";
    private static final String VAR = "Var";
    private static final String RESL = "resl";
    private static final String SLOT = "slot";
    private static final String NAF = "Naf";
    private static final String REPO = "repo";
    private static final String CTOR = "Ctor";
    private static final String REL = "Rel";
    private static final String CLOSURE = "closure";
    private static final String OID = "oid";
    private static final String SKOLEM = "Skolem";
    private static final String NEG = "Neg";

	private static final String DATA = "Data";
	
	//used for data
	private static final String XSITYPE = "type";

	
    /**
     * This is used for generating unique anonymous variable ids
     */
    private static int anonid = 1;

    /**
     * This is used to indicate if the document has an inner close attribute.
     */
    private boolean hasiclose = false;

    Logger logger = Logger.getLogger("jdrew.oo.util.RuleMLParser");

    /**
     * Constructs the back-end parser.
     *
     * @param clauses Vector The vector to use as a buffer - this is generally
     * passed by the RuleMLParser front-end.
     */
    public RuleML88Parser(Vector clauses) {
        this.clauses = clauses;
    }

    /**
     * This method is used to parse a RuleML 0.88 document that is stored in
     * a XOM tree.
     *
     * @param doc Document The XOM Document object that represents the RuleML
     * document to be parsed.
     *
     * @throws ParseException A ParseException is thrown if there is an error
     * parseing.
     */
       
    public void parseRuleMLDocument(Document doc) throws ParseException {
        this.skolemMap = new Hashtable();
        Element root = doc.getRootElement();
        if (!root.getLocalName().equals(ASSERT)) {
            throw new ParseException(
                    "Root element of RuleML 0.88 Document should be Assert.");
        }

        Element and = root.getFirstChildElement(AND);
        if (and == null) {
            throw new ParseException(
                    "Root element must contain a single And element.");
        }

        if (and.getAttribute(MAPCLOSURE) != null) {
            hasiclose = true;
            if (!and.getAttributeValue(MAPCLOSURE).equals(UNIVERSAL)) {
                throw new ParseException(
                        "Only universal inner closures are currently supported.");
            }

        } else {
            logger.info("Document root has not innerclose attribute. Indiviual clauses must have closure attributes.");
        }

		//using methods defined later on to parse atoms, negfacts and implies
	
        Elements els = and.getChildElements();
        for (int i = 0; i < els.size(); i++) {
            Element el = els.get(i);
            if (el.getLocalName().equals(ATOM)) {
                clauses.add(parseFact(el));
            } else if (el.getLocalName().equals(NEG)){
                clauses.addAll(parseNegFact(el));
            }
            else if (el.getLocalName().equals(IMPLIES)) {
                clauses.addAll(parseImplies(el));
            }
        }
    }

    /**
     * This method is used to parse and Assertion in the RuleML Document.
     *
     * @param ass Element The XOM Element objec that represents the assertion.
     *
     * @return Term A term object that represents the assertion in a way that
     * can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * assertion.
     */
        
    private Term parseAssert(Element ass) throws ParseException {
        Elements els = ass.getChildElements();
        if (els.size() != 1) {
            throw new ParseException(
                    "An Assert element can only contain one child.");
        }
		
        Element el = els.get(0);
		
        if (el.getLocalName().equals(ATOM)) {
            DefiniteClause dc = parseFact(el, false);
            Vector v = new Vector();
            for (int i = 0; i < dc.atoms.length; i++) {
                v.add(dc.atoms[i]);
            }
            Term t = new Term(SymbolTable.IASSERT, SymbolTable.INOROLE,
                              Types.IOBJECT, v);
            t.setAtom(true);
            return t;
        } else if (el.getLocalName().equals(IMPLIES)) {
            Vector v2 = parseImplies(el, false);
            DefiniteClause dc = (DefiniteClause)v2.get(0);
            Vector v = new Vector();
            for (int i = 0; i < dc.atoms.length; i++) {
                v.add(dc.atoms[i]);
            }
            Term t = new Term(SymbolTable.IASSERT, SymbolTable.INOROLE,
                              Types.IOBJECT, v);
            t.setAtom(true);
            return t;
        } else {
            throw new ParseException(
                    "Assert element can only contain Atom (fact) or Implies elements.");
        }
    }

    /**
     * This method is used to parse a Negative Fact in the RuleML Document.
     *
     * @param neg Element The XOM Element object that represents the neg fact.
     *
     * @return Vector a vector object that represents the negFact in a way that
     * can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * negFact.
     */

    private Vector parseNegFact(Element neg) throws ParseException {
        this.variableNames = new Vector();
        this.varClasses = new Hashtable();

        Elements atoms = neg.getChildElements("Atom");
        if(atoms.size() != 1){
            logger.error("OO jDREW only supports classical negation over single atoms.");
            throw new ParseException("OO jDREW only supports classical negation over single atoms.");
        }
		//getting the child
        Element atom = atoms.get(0);

		//checking clousre
        if (!hasiclose)
        {
            String closure = atom.getAttributeValue(CLOSURE);
            if (closure == null) {
                logger.error("No closure on clause.");
                throw new ParseException("No closure on clause.");
            }

            if (!closure.equals(UNIVERSAL)) {
                logger.error("Only universal closures are currently supported.");
                throw new ParseException("Only universal inner closures are currently supported.");
            }
        }
		
        Term atm = parseAtom(atom, true, true);

        Hashtable types = this.buildTypeTable();
        this.fixVarTypes(atm, types);

        Vector atms = new Vector();
        atms.add(atm);



        DefiniteClause dc = new DefiniteClause(atms, variableNames);

        Vector v = new Vector();
        v.add(dc);

        // Add code to generate consistency check

        //This should be redone to create the consistency check better

        String atmstr = dc.toPOSLString();

        String clause = "$inconsistent() :-";
        clause += atmstr.substring(0, atmstr.length() - 1) + ",";
        clause += "\"" + atmstr.substring(atmstr.indexOf("-") + 1);

        System.err.println(clause);

        POSLParser pp = new POSLParser();
        try{
            v.add(pp.parseDefiniteClause(clause));
        } catch(Exception e){
            throw new ParseException("Error creating consistency check.");
        }

        return v;
    }

    /**
     * This method is used to parse a fact element. The use of this method is
     * deprecated; new code should use
     * parseFact(Element atom, boolean newVarnames); this version is provided
     * for backwards compatability only.
     *
     * @param atom Element The XOM Element object that represents the fact to be
     * parsed.
     *
     * @return DefiniteClause A DefiniteClause data structure that represents
     * the fact in a way that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * fact.
     */
    private DefiniteClause parseFact(Element atom) throws ParseException {
        return parseFact(atom, true);
    }

    /**
     * This method is used to parse a fact element, creating a new variable
     * name list if indicated. Typically a new variable list is wanted; but in
     * certain cases (such as parsing an inner clause in an assert) the same
     * variable list must be used.
     *
     * @param atom Element The XOM Element objec that represents the fact to be
     * parsed.
     *
     * @param newVarnames boolean If true a new variable names list is created
     * and used; otherwise the current variable name list is used.
     *
     * @return DefiniteClause A DefiniteClause data structure that represents
     * the fact in a way that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * fact.
     */
    private DefiniteClause parseFact(Element atom, boolean newVarnames) throws
            ParseException {
        if (newVarnames) {
            this.variableNames = new Vector();
            this.varClasses = new Hashtable();
        }
		
        if (!hasiclose) {
            //No inner close - should have
            String closure = atom.getAttributeValue(CLOSURE);
            if (closure == null) {
                logger.error("No closure on clause.");
                throw new ParseException("No closure on clause.");
            }

            if (!closure.equals(UNIVERSAL)) {
                logger.error("Only universal closures are currently supported.");
                throw new ParseException(
                        "Only universal inner closures are currently supported.");
            }
        }

        Term atm = parseAtom(atom, true, false);

        Hashtable types = this.buildTypeTable();
        this.fixVarTypes(atm, types);

        Vector atoms = new Vector();
        atoms.add(atm);



        DefiniteClause dc = new DefiniteClause(atoms, variableNames);
        return dc;
    }

    /**
     * This method is used to parse a implies (implication) element. The use of
     * this method is deprecated; new code should use
     * parseImplies(Element atom, boolean newVarnames); this version is provided
     * for backwards compatability only.
     *
     * @param implies Element The XOM Element objec that represents the
     * implication to be parsed.
     *
     * @return DefiniteClause A DefiniteClause data structure that represents
     * the implication in a way that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * implication.
     */
    private Vector parseImplies(Element implies) throws ParseException {
        return parseImplies(implies, true);
    }

    /**
     * This method is used to parse a implication element, creating a new
     * variable name list if indicated. Typically a new variable list is wanted;
     * but in certain cases (such as parsing an inner clause in an assert) the
     * same variable list must be used.
     *
     * @param implies Element The XOM Element objec that represents the
     * implication to be parsed.
     *
     * @param newVarnames boolean If true a new variable names list is created
     * and used; otherwise the current variable name list is used.
     *
     * @return DefiniteClause A DefiniteClause data structure that represents
     * the implication in a way that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * implication.
     */
    private Vector parseImplies(Element implies, boolean newVarnames) throws
            ParseException {
        if (newVarnames) {
            this.variableNames = new Vector();
            this.varClasses = new Hashtable();
        }

        Vector newclauses = new Vector();

        if (!hasiclose) {
            //No inner close - should have
            String closure = implies.getAttributeValue(CLOSURE);
            if (closure == null) {
                logger.error("No closure on clause.");
                throw new ParseException("No closure on clause.");
            }

            if (!closure.equals(UNIVERSAL)) {
                logger.error("Only universal closures are currently supported.");
                throw new ParseException(
                        "Only universal inner closures are currently supported.");
            }
        }
		//implies need 2 child elements
        Elements els = implies.getChildElements();
        if (els.size() != 2) {
            throw new ParseException(
                    "Implies element should have 2 child elements.");
        }

        Vector atoms = new Vector();

        Element head = els.get(1);
        if (head.getLocalName().equals(ATOM)) {
            atoms.add(parseAtom(head, true, false));
        } else if (head.getLocalName().equals(NEG)){
            Elements headatms = head.getChildElements(ATOM);
            if(headatms.size() != 1)
                throw new ParseException("Neg should have one ATOM element");

            Term atom = parseAtom(headatms.get(0), true, true);
            atoms.add(atom);

            String atomstr = atom.toPOSLString(true);

            String clause = "$Sinconsistent() :-";
            clause += atomstr + ", \"" + atomstr.substring(6) + ".";

            System.err.println(clause);

            POSLParser pp = new POSLParser();
            try{
                DefiniteClause dc2 = pp.parseDefiniteClause(clause);
                if (dc2 != null)
                    newclauses.add(dc2);
            } catch(Exception e){
                throw new ParseException("Error creating inconsistency check rule.");
            }
        } else {
            throw new ParseException(
                    "Second element of Implies should always be an Atom or Neg element.");
        }

        Element body = els.get(0);
        if (body.getLocalName().equals(ATOM)) {
            atoms.add(parseAtom(body, false, false));
        } else if (body.getLocalName().equals(NAF)) {
            atoms.add(parseNaf(body));
        } else if (body.getLocalName().equals(ASSERT)) {
            atoms.add(parseAssert(body));
        } else if (body.getLocalName().equals(NEG)) {
            atoms.add(parseAtom(body, false, true));
        }
        else if (body.getLocalName().equals(AND)) {
            els = body.getChildElements();
            for (int i = 0; i < els.size(); i++) {
                Element el = els.get(i);
                if (el.getLocalName().equals(ATOM)) {
                    atoms.add(parseAtom(el, false, false));
                } else if (el.getLocalName().equals(NAF)) {
                    atoms.add(parseNaf(el));
                } else if (el.getLocalName().equals(ASSERT)) {
                    atoms.add(parseAssert(el));
                } else if (el.getLocalName().equals(NEG)){
                    atoms.add(parseAtom(el, false, true));
                } else {
                    throw new ParseException(
                            "Implies And element should only contain Atom and Naf elements.");
                }
            }
        } else {
            throw new ParseException(
                    "First element of Implies should be an Atom, Naf or And element.");
        }

        logger.debug("Building Types");
        Hashtable types = this.buildTypeTable();
        logger.debug("Built Types");
        Iterator it = atoms.iterator();
        int i = 0;
        while(it.hasNext()){
            Term t = (Term)it.next();
            this.fixVarTypes(t, types);
            logger.debug("Fixed atom : " + i++);

        }

        DefiniteClause dc = new DefiniteClause(atoms, variableNames);
        newclauses.add(0, dc);
        return newclauses;
    }

    /**
     * This method is used to parse and <oid> element.
     *
     * @param oid Element The XOM element that represents the oid to be parsed.
     *
     * @return Term Returns the data structure that represents the oid in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the oid.
     */
       
    private Term parseOid(Element oid) throws ParseException {
        Element el = oid.getChildElements().get(0);
        Term t;
        if (el.getLocalName().equals(IND)) {
            t = parseInd(el);
        } else if(el.getLocalName().equals(DATA)) {
            t = parseData(el);
        } else if(el.getLocalName().equals(SKOLEM)) {
            t = parseVar(el);
        } else if (el.getLocalName().equals(VAR)) {
            t = parseVar(el);
        } else if (el.getLocalName().equals(CTERM)) {
            t = parseCTerm(el);
        } else {
            throw new ParseException("oid can only contain Ind, Data, Var or Cterm.");
        }
        t.role = SymbolTable.IOID;
        return t;
    }

    /**
     * Method to parse an individual constant (Ind)
     *
     * @param ind Element The XOM element that represents the Ind to be parsed.
     *
     * @return Term Returns the data structure that represents the Ind in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the Ind.
     */
     
    private Term parseInd(Element ind) throws ParseException {
        String symbol = ind.getValue().trim();
        int sym = SymbolTable.internSymbol(symbol);
        Attribute type = ind.getAttribute(TYPE);
      
        int typeid = Types.IOBJECT;
        if (type != null) {
            typeid = Types.typeID(type.getValue().trim());
            if (typeid == -1) {
                throw new ParseException("Type " + type.getValue().trim() +
                                         " is not defined.");
            }
        }

        return new Term(sym, SymbolTable.INOROLE, typeid);
    }
   
    /**
     * Method to parse an Data (Data)
     *
     * @param ind Element The XOM element that represents the Data to be parsed.
     *
     * @return Term Returns the data structure that represents the Data in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the Data.
     */
     
     //this is where you change stuff about the name space
     //uri anchoring and such, test to see if it supports any at all yet
     
     //Also INOROLE may be changed later not sure yet when creating a Data
     //Term Data has no role so it will probally not change
     
    private Term parseData(Element data) throws ParseException {
        String symbol = data.getValue().trim();
        int sym = SymbolTable.internSymbol(symbol);
        
      //Attribute type = data.getAttribute(XSITYPE,"http://www.w3.org/2001/XMLSchema-instance");
        Attribute type = data.getAttribute(TYPE);
      
		//if(type != null)       
     	// System.out.println("PRINTING: " + type.getValue()); 

        int typeid = Types.IOBJECT;
        if (type != null) {
            typeid = Types.typeID(type.getValue().trim());
            if (typeid == -1) {
                throw new ParseException("Type " + type.getValue().trim() +
                                         " is not defined.");
            }
        }

        Term t1 = new Term(sym, SymbolTable.INOROLE, typeid);
    	t1.setData(true);
    	
    	return t1;
    }
    

    /**
     * Method to parse a Var (Variable)
     *
     * @param var Element The XOM element that represents the Var
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the Var in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the Var.
     */
     
    private Term parseVar(Element var) throws ParseException {
        String symbol = var.getValue().trim();
        if (symbol.equals("")) {
            symbol = "$ANON" + anonid++;
        }

        int sym = this.internVariable(symbol);
        Attribute type = var.getAttribute(TYPE);
        int typeid = Types.IOBJECT;
        if (type != null) {
            typeid = Types.typeID(type.getValue().trim());
            if (typeid == -1) {
                throw new ParseException("Type " + type.getValue().trim() +
                                         " is not defined.");
            }
        }

        Integer symI = new Integer(sym);
        Integer typeI = new Integer(typeid);

        logger.debug("Parsing variable: symbol = " + symI + " type = " + typeI);

        Vector v;
        if(this.varClasses.containsKey(symI)){
            v = (Vector)varClasses.get(symI);
        }else{
            v = new Vector();
            varClasses.put(symI, v);
        }

        v.add(typeI);

        logger.debug("Added Type Information");

        return new Term(sym, SymbolTable.INOROLE, typeid);
    }

    /**
     * Method to parse a plex
     *
     * @param plex Element The XOM element that represents the plex
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the plex in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the plex.
     */
    
    private Term parsePlex(Element plex) throws ParseException {
        Elements els = plex.getChildElements();
        Vector subterms = new Vector();
        for (int i = 0; i < els.size(); i++) {
            Element el = els.get(i);
            if (el.getLocalName().equals(PLEX)) {
                subterms.add(parsePlex(el));
            } else if (el.getLocalName().equals(CTERM)) {
                subterms.add(parseCTerm(el));
            } else if (el.getLocalName().equals(IND)) {
                subterms.add(parseInd(el));
            } else if (el.getLocalName().equals(DATA)) {
                subterms.add(parseData(el));
            } else if (el.getLocalName().equals(SKOLEM)) {
                subterms.add(parseSkolem(el));
            } else if (el.getLocalName().equals(VAR)) {
                subterms.add(parseVar(el));
            } else if (el.getLocalName().equals(SLOT)) {
                subterms.add(parseSlot(el));
            } else if (el.getLocalName().equals(RESL)) {
                subterms.add(parseResl(el));
            } else if (el.getLocalName().equals(REPO)) {
                subterms.add(parseRepo(el));
            } else {
                throw new ParseException(
                        "Plex should only contain Plex, Cterm, Ind, Data, Var and slot, repo, resl.");
            }

        }

        Term t = new Term(SymbolTable.IPLEX, SymbolTable.INOROLE, Types.IOBJECT,
                          subterms);
        return t;
    }

    /**
     * Method to parse a cterm (Complex Term)
     *
     * @param cterm Element The XOM element that represents the cterm
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the cterm in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the cterm.
     */
     
    private Term parseCTerm(Element cterm) throws ParseException {
        Elements els = cterm.getChildElements();

        Element ctor = els.get(0);
        if (!ctor.getLocalName().equals(CTOR)) {
            throw new ParseException(
                    "First child of Cterm must be a Ctor element.");
        }

        int symbol = SymbolTable.internSymbol(ctor.getValue().trim());

        int typeid = Types.IOBJECT;
        Attribute type = cterm.getAttribute(TYPE);
        if (type != null) {
            typeid = Types.typeID(type.getValue().trim());
            if (typeid == -1) {
                throw new ParseException("Type " + type.getValue().trim() +
                                         " is not defined.");
            }
        }

        Vector subterms = new Vector();
        for (int i = 1; i < els.size(); i++) {
            Element el = els.get(i);
            if (el.getLocalName().equals(PLEX)) {
                subterms.add(parsePlex(el));
            } else if (el.getLocalName().equals(CTERM)) {
                subterms.add(parseCTerm(el));
            } else if (el.getLocalName().equals(IND)) {
                subterms.add(parseInd(el));
            } else if (el.getLocalName().equals(DATA)) {
                subterms.add(parseData(el));
            } else if (el.getLocalName().equals(SKOLEM)) {
                subterms.add(parseSkolem(el));
            } else if (el.getLocalName().equals(VAR)) {
                subterms.add(parseVar(el));
            } else if (el.getLocalName().equals(SLOT)) {
                subterms.add(parseSlot(el));
            } else if (el.getLocalName().equals(RESL)) {
                subterms.add(parseResl(el));
            } else if (el.getLocalName().equals(REPO)) {
                subterms.add(parseRepo(el));
            } else {
                throw new ParseException(
                        "Cterm should only contain Plex, Cterm, Ind, Data, Var and slot, repo, resl.");
            }

        }

        Term t = new Term(symbol, SymbolTable.INOROLE, typeid, subterms);
        return t;
    }

    /**
     * Method to parse an Atom
     *
     * @param Atom Element The XOM element that represents the Atom
     * to be parsed.
     *
     * @param head boolean This tells the engine if the atom is a head of a rule.
     *
     * @param neg boolean This tells the engine if the atom is a negative atom (Neg)
     *
     * @return Term Returns the data structure that represents the Atom in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the Atom.
     */
   
    private Term parseAtom(Element atom, boolean head, boolean neg) throws ParseException {
        Elements els = atom.getChildElements();
        boolean foundoid = false;
		
        Element rel = els.get(0);
        if (!rel.getLocalName().equals(REL)) {
            throw new ParseException(
                    "First child of Atom must be a Rel element.");
        }
		
        String relname = rel.getValue().trim();
        if(neg)
            relname = "$neg-" + relname;

        int symbol = SymbolTable.internSymbol(relname);

        Vector subterms = new Vector();
        for (int i = 1; i < els.size(); i++) {
            Element el = els.get(i);
            if (el.getLocalName().equals(PLEX)) {
                subterms.add(parsePlex(el));
            } else if (el.getLocalName().equals(CTERM)) {
                subterms.add(parseCTerm(el));
            } else if (el.getLocalName().equals(IND)) {
                subterms.add(parseInd(el));
            } else if (el.getLocalName().equals(DATA)) {
                subterms.add(parseData(el));
            } else if (el.getLocalName().equals(SKOLEM)){
                subterms.add(parseSkolem(el));
            } else if (el.getLocalName().equals(VAR)) {
                subterms.add(parseVar(el));
            } else if (el.getLocalName().equals(SLOT)) {
                subterms.add(parseSlot(el));
            } else if (el.getLocalName().equals(RESL)) {
                subterms.add(parseResl(el));
            } else if (el.getLocalName().equals(REPO)) {
                subterms.add(parseRepo(el));
            } else if (el.getLocalName().equals(OID)) {
                if (foundoid) {
                    throw new ParseException(
                            "Atom should only contain one oid element.");
                }
                subterms.add(parseOid(el));
                foundoid = true;
            } else {
                throw new ParseException(
                        "Atom should only contain Plex, Cterm, Ind, Data, Var, slot, repo, resl and oid.");
            }
         	
        }
		//if foundoid is false
		//no idea what this is doing
        if (!foundoid) {
            if (head) {
                String symname = "$gensym" + SymbolTable.genid++;
                int symid = SymbolTable.internSymbol(symname);
                Term t2 = new Term(symid, SymbolTable.IOID, Types.IOBJECT);
                subterms.add(t2);
            } else {
                String varname = "$ANON" + anonid++;
                int symid = this.internVariable(varname);
                Vector types = new Vector();
                types.add(new Integer(Types.IOBJECT));
                this.varClasses.put(new Integer(symid), types);
                Term t2 = new Term(symid, SymbolTable.IOID, Types.IOBJECT);
                subterms.add(t2);
            }
        }

		

        Term t = new Term(symbol, SymbolTable.INOROLE, Types.IOBJECT, subterms);
        t.setAtom(true);
        return t;
    }

    /**
     * Method to parse a Slot
     *
     * @param slot Element The XOM element that represents the slot
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the slot in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the slot.

     */
     
    private Term parseSlot(Element slot) throws ParseException {
        Elements els = slot.getChildElements();
        if (els.size() != 2) {
            throw new ParseException("Slot must have two child elements.");
        }
        
        Element name = els.get(0);
		boolean dataSlot = false;
		if(name.getLocalName().equals(DATA)){
			dataSlot = true;
		}
       
        if (!name.getLocalName().equals(IND) && !name.getLocalName().equals(DATA)) {
            throw new ParseException("Only Ind and Data slot names are supported.");
        }    
		//Getting the Role from the symbol Table, it will assign one if it
		//doesnt already exist
        int role = SymbolTable.internRole(name.getValue().trim());
       
        Element value = els.get(1);
        
        Term t;
		//Figuring out the type of term it should be
        if (value.getLocalName().equals(PLEX)) {
            t = parsePlex(value);
        } else if (value.getLocalName().equals(CTERM)) {
            t = parseCTerm(value);
        } else if (value.getLocalName().equals(IND)) {
            t = parseInd(value);
        } else if (value.getLocalName().equals(DATA)) {
            t = parseData(value);
        } else if (value.getLocalName().equals(SKOLEM)){
            t = parseSkolem(value);
        } else if (value.getLocalName().equals(VAR)) {
            t = parseVar(value);
        } else {
            throw new ParseException(
                    "Slot value should be either Plex, Cterm, Ind, Data or Var.");
        }
		
        t.setRole(role);
        
        if(dataSlot){
        	t.setDataSlot(true);
        }
        
        return t;
    }

    /**
     * Method to parse a resl (Rested Slot)
     *
     * @param resl Element The XOM element that represents the resl
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the resl in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the resl.
     */    
     
    private Term parseResl(Element resl) throws ParseException {
        Elements els = resl.getChildElements();
        if (els.size() > 1) {
            throw new ParseException(
                    "resl element should only contain child element.");
        }

        Element el = els.get(0);
        Term t;
        if (el.getLocalName().equals(VAR)) {
            t = parseVar(el);
        } else if (el.getLocalName().equals(PLEX)) {
            t = parsePlex(el);
        } else {
            throw new ParseException(
                    "resl element should only contain Var or Plex as a child element.");
        }

        t.setRole(SymbolTable.IREST);
        return t;
    }

    /**
     * Method to parse a repo (Rested Positional Slot)
     *
     * @param repo Element The XOM element that represents the repo
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the repo in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the repo.
     */   
     
    private Term parseRepo(Element repo) throws ParseException {
        Elements els = repo.getChildElements();
        if (els.size() > 1) {
            throw new ParseException(
                    "repo element should only contain child element.");
        }

        Element el = els.get(0);
        Term t;
        if (el.getLocalName().equals(VAR)) {
            t = parseVar(el);
        } else if (el.getLocalName().equals(PLEX)) {
            t = parsePlex(el);
        } else {
            throw new ParseException(
                    "repo element should only contain Var or Plex as a child element.");
        }

        t.setRole(SymbolTable.IPREST);
        return t;
    }

    /**
     * Method to parse a naf (Negation as Failure)
     *
     * @param naf Element The XOM element that represents the naf atom
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the naf atom in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the naf.
     */ 
  
    private Term parseNaf(Element naf) throws ParseException {
        Elements els = naf.getChildElements();
        if (els.size() != 1) {
            throw new ParseException(
                    "Naf elements should only contain one child.");
        }

        Element el = els.get(0);
        Vector subterms = new Vector();
        if (el.getLocalName().equals(ATOM)) {
            subterms.add(parseAtom(el, false, false));
        } else if (el.getLocalName().equals(AND)) {
            els = el.getChildElements();
            for (int i = 0; i < els.size(); i++) {
                Element el2 = els.get(i);
                if (!el2.getLocalName().equals(ATOM)) {
                    throw new ParseException(
                            "And child of Naf element should only contain Atom elements.");
                }

                subterms.add(parseAtom(el2, false, false));
            }
        } else {
            throw new ParseException(
                    "Naf element should only contain Atom or And child element.");
        }

        Term t = new Term(SymbolTable.INAF, SymbolTable.INOROLE, Types.IOBJECT,
                          subterms);
        t.setAtom(true);
        return t;
    }

	/**
     * Method to parse a sko(Skolem Constant)
     *
     * @param sko Element The XOM element that represents the sko
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the sko in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the sko.
     */ 

    private Term parseSkolem(Element sko) throws ParseException{
        String skoname = sko.getValue().trim();

        if(skoname.equals("")){
            return new Term(SymbolTable.internSymbol("$gensym" + SymbolTable.genid++),
                            SymbolTable.INOROLE, Types.ITHING);
        }else{
            if(this.skolemMap.containsKey(skoname)){
                String sym = (String)skolemMap.get(skoname);
                return new Term(SymbolTable.internSymbol(sym),
                                  SymbolTable.INOROLE, Types.ITHING);
            }else{
                String sym = "$gensym" + (SymbolTable.genid++) + "$" + skoname;
                skolemMap.put(skoname, sym);
                return new Term(SymbolTable.internSymbol(sym),
                                SymbolTable.INOROLE, Types.ITHING);
            }
        }
    }

    /**
     * Initialize a variable for use. Stores the variable name and assigns a
     * integer identifier if it is unused in this clause; returns the existing
     * identifier if it has already been used in this clause.
     *
     * Variables are Negative and Ind are positive
     *
     * @param varName String The variable name.
     *
     * @return int The integer identifier for this variable
     */

    private int internVariable(String varName) {
        int idx;

        idx = variableNames.indexOf(varName);
        if (idx == -1) {
            idx = variableNames.size();
            variableNames.add(varName);
        }

        return -(idx + 1);
    }



    /**
     * A vector to hold the class information for the variables in the current
     * clause. This is used for normalizing the types given to a variable if
     * more than one type is given to a variable.
     *
     * For example: in the following clause p(?x: type1, ?x:type2). the types
     * are not the same on all occurances of the variable ?x - therefore the
     * types will be normalized and receive a type that is the intersection of
     * type1 and type2.
     */
    private Hashtable varClasses;

    /**
     * A method that will go through a term and fix all variable types to be
     * consistant.
     *
     * @param ct Term The term to normalize the types in.
     *
     * @param types Hashtable A hash table containing the normalized types for
     * each variable in the clause.
     */
         
    private void fixVarTypes(Term ct, Hashtable types) {
        //logger.debug("Fixing term: " + ct.toPOSLString(true));
        for (int i = 0; i < ct.subTerms.length; i++) {
            if (ct.subTerms[i].isCTerm()) {
                fixVarTypes(ct.subTerms[i], types);
            } else if (ct.subTerms[i].getSymbol() < 0) {
                Integer sym = new Integer(ct.subTerms[i].getSymbol());
               // logger.debug("Fixing symbol = " + sym);
                
                Integer type = (Integer)types.get(sym);
                //logger.debug("Type = " + type);
                
                ct.subTerms[i].type = type.intValue();
            }
        }
    }

    /**
     * A method to find the normalized types for each variable in the clause.
     *
     * @return Hashtable A hashtable containing the normalized type for each
     * variable in the current clause.
     *
     * @throws ParseException A ParseException is thrown a variable does not
     * have a normalized form (type that is an intersection of all given types);
     * since Nothing inherits from all types this should never occur.
     */
     
    private Hashtable buildTypeTable() throws ParseException {
        Hashtable ht = new Hashtable();
        Enumeration e = varClasses.keys();

        while (e.hasMoreElements()) {
            Object key = e.nextElement();
            Vector value = (Vector) varClasses.get(key);
            int[] types = new int[value.size()];
            for (int i = 0; i < types.length; i++) {
                types[i] = ((Integer) value.get(i)).intValue();
            }

            int type = Types.greatestLowerBound(types);
            ht.put(key, new Integer(type));
        }
        return ht;
    }

}

class RuleMLQueryParser {

    private Hashtable skolemMap;

    private Vector clauses;
                  
    private Vector variableNames;

	private static final String RULEBASE = "Rulebase";
    private static final String ASSERT = "Query";
    private static final String AND = "And";
    private static final String ATOM = "Atom";
    private static final String MAPCLOSURE = "mapClosure";
    private static final String UNIVERSAL = "universal";
    private static final String IMPLIES = "Implies";
    private static final String TYPE = "type";
    private static final String PLEX = "Plex";
    private static final String CTERM = "Expr";
    private static final String IND = "Ind";
    private static final String VAR = "Var";
    private static final String RESL = "resl";
    private static final String SLOT = "slot";
    private static final String NAF = "Naf";
    private static final String REPO = "repo";
    private static final String CTOR = "Fun";
    private static final String REL = "Rel";
    private static final String CLOSURE = "closure";
    private static final String OID = "oid";
    private static final String SKOLEM = "Skolem";
    private static final String NEG = "Neg";
        
	private static final String EXPR = "Expr";
	private static final String DATA = "Data";
	
	private static final String OP = "op";
	
	//used for data
	private static final String XSITYPE = "type";

	
    /**
     * This is used for generating unique anonymous variable ids
     */
    private static int anonid = 1;

    /**
     * This is used to indicate if the document has an inner close attribute.
     */
    private boolean hasiclose = false;

    Logger logger = Logger.getLogger("jdrew.oo.util.RuleMLParser");

    /**
     * Constructs the back-end parser.
     *
     * @param clauses Vector The vector to use as a buffer - this is generally
     * passed by the RuleMLParser front-end.
     */
    public RuleMLQueryParser(Vector clauses) {
        this.clauses = clauses;
    }

    /**
     * This method is used to parse a RuleML 0.91 document that is stored in
     * a XOM tree.
     *
     * @param doc Document The XOM Document object that represents the RuleML
     * document to be parsed.
     *
     * @throws ParseException A ParseException is thrown if there is an error
     * parseing.
     */
            
    public void parseRuleMLDocument(Document doc) throws ParseException {
        this.skolemMap = new Hashtable();
        Element root = doc.getRootElement();
        if (!root.getLocalName().equals(ASSERT)) {
            throw new ParseException(
                    "Root element of a Query should be Query.");
        }
		
		/*
        Element and = root.getFirstChildElement(RULEBASE);
        
        if (and == null) {
            throw new ParseException(
                    "Root element must contain Rulebase element.");
        }

        if (and.getAttribute(MAPCLOSURE) != null) {
            hasiclose = true;
            if (!and.getAttributeValue(MAPCLOSURE).equals(UNIVERSAL)) {
                throw new ParseException(
                        "Only universal inner closures are currently supported.");
            }

        } else {
            logger.info("Document root has not innerclose attribute. Indiviual clauses must have closure attributes.");
        }
		*/
		hasiclose = true;
		
		Elements els = root.getChildElements();
        //Elements els = and.getChildElements();
        for (int i = 0; i < els.size(); i++) {
            Element el = els.get(i);
            if (el.getLocalName().equals(ATOM)) {
                clauses.add(parseFact(el));
            } else if (el.getLocalName().equals(NEG)){
                clauses.addAll(parseNegFact(el));
            }
            else if (el.getLocalName().equals(IMPLIES)) {
                clauses.addAll(parseImplies(el));
            }
        }
    }

    /**
     * This method is used to parse an Assertion in the RuleML Document.
     *
     * @param ass Element The XOM Element objec that represents the assertion.
     *
     * @return Term A term object that represents the assertion in a way that
     * can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * assertion.
     */
          
    private Term parseAssert(Element ass) throws ParseException {
        Elements els = ass.getChildElements();
        if (els.size() != 1) {
            throw new ParseException(
                    "An Assert element can only contain one child.");
        }
		
        Element el = els.get(0);
		
        if (el.getLocalName().equals(ATOM)) {
            DefiniteClause dc = parseFact(el, false);
            Vector v = new Vector();
            for (int i = 0; i < dc.atoms.length; i++) {
                v.add(dc.atoms[i]);
            }
            Term t = new Term(SymbolTable.IASSERT, SymbolTable.INOROLE,
                              Types.IOBJECT, v);
            t.setAtom(true);
            return t;
        } else if (el.getLocalName().equals(IMPLIES)) {
            Vector v2 = parseImplies(el, false);
            DefiniteClause dc = (DefiniteClause)v2.get(0);
            Vector v = new Vector();
            for (int i = 0; i < dc.atoms.length; i++) {
                v.add(dc.atoms[i]);
            }
            Term t = new Term(SymbolTable.IASSERT, SymbolTable.INOROLE,
                              Types.IOBJECT, v);
            t.setAtom(true);
            return t;
        } else {
            throw new ParseException(
                    "Assert element can only contain Atom (fact) or Implies elements.");
        }
    }
    
    /**
     * This method is used to parse a Negative Fact in the RuleML Document.
     *
     * @param neg Element The XOM Element object that represents the neg fact.
     *
     * @return Vector a vector object that represents the negFact in a way that
     * can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * negFact.
     */
    private Vector parseNegFact(Element neg) throws ParseException {
        this.variableNames = new Vector();
        this.varClasses = new Hashtable();

        Elements atoms = neg.getChildElements("Atom");
        if(atoms.size() != 1){
            logger.error("OO jDREW only supports classical negation over single atoms.");
            throw new ParseException("OO jDREW only supports classical negation over single atoms.");
        }
		
        Element atom = atoms.get(0);

        if (!hasiclose)
        {
            String closure = atom.getAttributeValue(CLOSURE);
            if (closure == null) {
                logger.error("No closure on clause.");
                throw new ParseException("No closure on clause.");
            }

            if (!closure.equals(UNIVERSAL)) {
                logger.error("Only universal closures are currently supported.");
                throw new ParseException("Only universal inner closures are currently supported.");
            }
        }
		
        Term atm = parseAtom(atom, true, true);

        Hashtable types = this.buildTypeTable();
        this.fixVarTypes(atm, types);

        Vector atms = new Vector();
        atms.add(atm);

        DefiniteClause dc = new DefiniteClause(atms, variableNames);

        Vector v = new Vector();
        v.add(dc);

        // Add code to generate consistency check

        //This should be redone to create the consistency check better

        String atmstr = dc.toPOSLString();

        String clause = "$inconsistent() :-";
        clause += atmstr.substring(0, atmstr.length() - 1) + ",";
        clause += "\"" + atmstr.substring(atmstr.indexOf("-") + 1);

        System.err.println(clause);

        POSLParser pp = new POSLParser();
        try{
            v.add(pp.parseDefiniteClause(clause));
        } catch(Exception e){
            throw new ParseException("Error creating consistency check.");
        }

        return v;
    }

    /**
     * This method is used to parse a fact element. The use of this method is
     * deprecated; new code should use
     * parseFact(Element atom, boolean newVarnames); this version is provided
     * for backwards compatability only.
     *
     * @param atom Element The XOM Element object that represents the fact to be
     * parsed.
     *
     * @return DefiniteClause A DefiniteClause data structure that represents
     * the fact in a way that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * fact.
     */
    private DefiniteClause parseFact(Element atom) throws ParseException {
        return parseFact(atom, true);
    }

    /**
     * This method is used to parse a fact element, creating a new variable
     * name list if indicated. Typically a new variable list is wanted; but in
     * certain cases (such as parsing an inner clause in an assert) the same
     * variable list must be used.
     *
     * @param atom Element The XOM Element objec that represents the fact to be
     * parsed.
     *
     * @param newVarnames boolean If true a new variable names list is created
     * and used; otherwise the current variable name list is used.
     *
     * @return DefiniteClause A DefiniteClause data structure that represents
     * the fact in a way that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * fact.
     */
    private DefiniteClause parseFact(Element atom, boolean newVarnames) throws
            ParseException {
        if (newVarnames) {
            this.variableNames = new Vector();
            this.varClasses = new Hashtable();
        }
		
        if (!hasiclose) {
            //No inner close - should have
            String closure = atom.getAttributeValue(CLOSURE);
            if (closure == null) {
                logger.error("No closure on clause.");
                throw new ParseException("No closure on clause.");
            }

            if (!closure.equals(UNIVERSAL)) {
                logger.error("Only universal closures are currently supported.");
                throw new ParseException(
                        "Only universal inner closures are currently supported.");
            }
        }

        Term atm = parseAtom(atom, true, false);

        Hashtable types = this.buildTypeTable();
        this.fixVarTypes(atm, types);

        Vector atoms = new Vector();
        atoms.add(atm);

        DefiniteClause dc = new DefiniteClause(atoms, variableNames);
        return dc;
    }

    /**
     * This method is used to parse a implies (implication) element. The use of
     * this method is deprecated; new code should use
     * parseImplies(Element atom, boolean newVarnames); this version is provided
     * for backwards compatability only.
     *
     * @param implies Element The XOM Element objec that represents the
     * implication to be parsed.
     *
     * @return DefiniteClause A DefiniteClause data structure that represents
     * the implication in a way that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * implication.
     */
    private Vector parseImplies(Element implies) throws ParseException {
        return parseImplies(implies, true);
    }

    /**
     * This method is used to parse a implication element, creating a new
     * variable name list if indicated. Typically a new variable list is wanted;
     * but in certain cases (such as parsing an inner clause in an assert) the
     * same variable list must be used.
     *
     * @param implies Element The XOM Element objec that represents the
     * implication to be parsed.
     *
     * @param newVarnames boolean If true a new variable names list is created
     * and used; otherwise the current variable name list is used.
     *
     * @return DefiniteClause A DefiniteClause data structure that represents
     * the implication in a way that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * implication.
     */
    private Vector parseImplies(Element implies, boolean newVarnames) throws
            ParseException {
        if (newVarnames) {
            this.variableNames = new Vector();
            this.varClasses = new Hashtable();
        }

        Vector newclauses = new Vector();

        if (!hasiclose) {
            //No inner close - should have
            String closure = implies.getAttributeValue(CLOSURE);
            if (closure == null) {
                logger.error("No closure on clause.");
                throw new ParseException("No closure on clause.");
            }

            if (!closure.equals(UNIVERSAL)) {
                logger.error("Only universal closures are currently supported.");
                throw new ParseException(
                        "Only universal inner closures are currently supported.");
            }
        }
		//implies need 2 child elements
        Elements els = implies.getChildElements();
        if (els.size() != 2) {
            throw new ParseException(
                    "Implies element should have 2 child elements.");
        }

        Vector atoms = new Vector();

        Element head = els.get(1);
        if (head.getLocalName().equals(ATOM)) {
            atoms.add(parseAtom(head, true, false));
        } else if (head.getLocalName().equals(NEG)){
            Elements headatms = head.getChildElements(ATOM);
            if(headatms.size() != 1)
                throw new ParseException("Neg should have one ATOM element");

            Term atom = parseAtom(headatms.get(0), true, true);
            atoms.add(atom);

            String atomstr = atom.toPOSLString(true);

            String clause = "$Sinconsistent() :-";
            clause += atomstr + ", \"" + atomstr.substring(6) + ".";

            System.err.println(clause);

            POSLParser pp = new POSLParser();
            try{
                DefiniteClause dc2 = pp.parseDefiniteClause(clause);
                if (dc2 != null)
                    newclauses.add(dc2);
            } catch(Exception e){
                throw new ParseException("Error creating inconsistency check rule.");
            }
        } else {
            throw new ParseException(
                    "Second element of Implies should always be an Atom or Neg element.");
        }

        Element body = els.get(0);
        if (body.getLocalName().equals(ATOM)) {
            atoms.add(parseAtom(body, false, false));
        } else if (body.getLocalName().equals(NAF)) {
            atoms.add(parseNaf(body));
        } else if (body.getLocalName().equals(ASSERT)) {
            atoms.add(parseAssert(body));
        } else if (body.getLocalName().equals(NEG)) {
            atoms.add(parseAtom(body, false, true));
        }
        else if (body.getLocalName().equals(AND)) {
            els = body.getChildElements();
            for (int i = 0; i < els.size(); i++) {
                Element el = els.get(i);
                if (el.getLocalName().equals(ATOM)) {
                    atoms.add(parseAtom(el, false, false));
                } else if (el.getLocalName().equals(NAF)) {
                    atoms.add(parseNaf(el));
                } else if (el.getLocalName().equals(ASSERT)) {
                    atoms.add(parseAssert(el));
                } else if (el.getLocalName().equals(NEG)){
                    atoms.add(parseAtom(el, false, true));
                } else {
                    throw new ParseException(
                            "Implies And element should only contain Atom and Naf elements.");
                }
            }
        } else {
            throw new ParseException(
                    "First element of Implies should be an Atom, Naf or And element.");
        }

        logger.debug("Building Types");
        Hashtable types = this.buildTypeTable();
        logger.debug("Built Types");
        Iterator it = atoms.iterator();
        int i = 0;
        while(it.hasNext()){
            Term t = (Term)it.next();
            this.fixVarTypes(t, types);
            logger.debug("Fixed atom : " + i++);

        }

        DefiniteClause dc = new DefiniteClause(atoms, variableNames);
        newclauses.add(0, dc);
        return newclauses;
    }

    /**
     * This method is used to parse and <oid> element.
     *
     * @param oid Element The XOM element that represents the oid to be parsed.
     *
     * @return Term Returns the data structure that represents the oid in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the oid.
     */
       
    private Term parseOid(Element oid) throws ParseException {
        Element el = oid.getChildElements().get(0);
        Term t;
        if (el.getLocalName().equals(IND)) {
            t = parseInd(el);
        } else if(el.getLocalName().equals(DATA)) {
            t = parseData(el);
        } else if(el.getLocalName().equals(SKOLEM)) {
            t = parseVar(el);
        } else if (el.getLocalName().equals(VAR)) {
            t = parseVar(el);
        } else if (el.getLocalName().equals(CTERM)) {
            t = parseCTerm(el);
        } else {
            throw new ParseException("oid can only contain Ind, Data, Var or Cterm.");
        }
        t.role = SymbolTable.IOID;
        return t;
    }

    /**
     * Method to parse an individual constant (Ind)
     *
     * @param ind Element The XOM element that represents the Ind to be parsed.
     *
     * @return Term Returns the data structure that represents the Ind in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the Ind.
     */
     
    private Term parseInd(Element ind) throws ParseException {
        String symbol = ind.getValue().trim();
        int sym = SymbolTable.internSymbol(symbol);
        Attribute type = ind.getAttribute(TYPE);
      
        int typeid = Types.IOBJECT;
        if (type != null) {
            typeid = Types.typeID(type.getValue().trim());
            if (typeid == -1) {
                throw new ParseException("Type " + type.getValue().trim() +
                                         " is not defined.");
            }
        }

        return new Term(sym, SymbolTable.INOROLE, typeid);
    }
   
    /**
     * Method to parse an Data (Data)
     *
     * @param ind Element The XOM element that represents the Data to be parsed.
     *
     * @return Term Returns the data structure that represents the Data in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the Data.
     */
     
     //this is where you change stuff about the name space
     //uri anchoring and such, test to see if it supports any at all yet
     
     //Also INOROLE may be changed later not sure yet when creating a Data
     //Term Data has no role so it will probally not change
     
    private Term parseData(Element data) throws ParseException {
        String symbol = data.getValue().trim();
        int sym = SymbolTable.internSymbol(symbol);
        
      //Attribute type = data.getAttribute(XSITYPE,"http://www.w3.org/2001/XMLSchema-instance");
        Attribute type = data.getAttribute(TYPE);
      
		//if(type != null)       
     	 //System.out.println("PRINTING: " + type.getValue()); 

        int typeid = Types.IOBJECT;
        if (type != null) {
            typeid = Types.typeID(type.getValue().trim());
            if (typeid == -1) {
                throw new ParseException("Type " + type.getValue().trim() +
                                         " is not defined.");
            }
        }

        Term t1 = new Term(sym, SymbolTable.INOROLE, typeid);
    	t1.setData(true);
    
    	return t1;
    }
    
    /**
     * Method to parse a Var (Variable)
     *
     * @param var Element The XOM element that represents the Var
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the Var in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the Var.
     */
     
    private Term parseVar(Element var) throws ParseException {
        String symbol = var.getValue().trim();
        if (symbol.equals("")) {
            symbol = "$ANON" + anonid++;
        }

        int sym = this.internVariable(symbol);
        Attribute type = var.getAttribute(TYPE);
        int typeid = Types.IOBJECT;
        if (type != null) {
            typeid = Types.typeID(type.getValue().trim());
            if (typeid == -1) {
                throw new ParseException("Type " + type.getValue().trim() +
                                         " is not defined.");
            }
        }

        Integer symI = new Integer(sym);
        Integer typeI = new Integer(typeid);

        logger.debug("Parsing variable: symbol = " + symI + " type = " + typeI);

        Vector v;
        if(this.varClasses.containsKey(symI)){
            v = (Vector)varClasses.get(symI);
        }else{
            v = new Vector();
            varClasses.put(symI, v);
        }

        v.add(typeI);

        logger.debug("Added Type Information");

        return new Term(sym, SymbolTable.INOROLE, typeid);
    }

    /**
     * Method to parse a plex
     *
     * @param plex Element The XOM element that represents the plex
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the plex in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the plex.
     */
        
    private Term parsePlex(Element plex) throws ParseException {
        Elements els = plex.getChildElements();
        Vector subterms = new Vector();
        for (int i = 0; i < els.size(); i++) {
            Element el = els.get(i);
            if (el.getLocalName().equals(PLEX)) {
                subterms.add(parsePlex(el));
            } else if (el.getLocalName().equals(CTERM)) {
                subterms.add(parseCTerm(el));
            } else if (el.getLocalName().equals(IND)) {
                subterms.add(parseInd(el));
            } else if (el.getLocalName().equals(DATA)) {
                subterms.add(parseData(el));
            } else if (el.getLocalName().equals(SKOLEM)) {
                subterms.add(parseSkolem(el));
            } else if (el.getLocalName().equals(VAR)) {
                subterms.add(parseVar(el));
            } else if (el.getLocalName().equals(SLOT)) {
                subterms.add(parseSlot(el));
            } else if (el.getLocalName().equals(RESL)) {
                subterms.add(parseResl(el));
            } else if (el.getLocalName().equals(REPO)) {
                subterms.add(parseRepo(el));
            } else {
                throw new ParseException(
                        "Plex should only contain Plex, Cterm, Ind, Data, Var and slot, repo, resl.");
            }

        }

        Term t = new Term(SymbolTable.IPLEX, SymbolTable.INOROLE, Types.IOBJECT,
                          subterms);
        return t;
    }

    /**
     * Method to parse a Expr (Expresion)
     *
     * @param cterm Element The XOM element that represents the Expr
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the Expr in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the Expr.
     */
          
    private Term parseCTerm(Element cterm) throws ParseException {
        
        Elements els = cterm.getChildElements();
        Element op = els.get(0);
        
        boolean foundOp = false;
        
        if (op.getLocalName().equals(OP)) {
			foundOp = true;
        }
		
		Element ctor = null;
		if(foundOp){
			
			Elements ctorTag = op.getChildElements();
			ctor = ctorTag.get(0);
        }
        
        if(!foundOp){
        	ctor = els.get(0);
        }
                
        if (!ctor.getLocalName().equals(CTOR)) {
            throw new ParseException(
                    "First child of op in an Expr must be a Fun element.");
        }

        int symbol = SymbolTable.internSymbol(ctor.getValue().trim());

        int typeid = Types.IOBJECT;
        Attribute type = cterm.getAttribute(TYPE);
        if (type != null) {
            typeid = Types.typeID(type.getValue().trim());
            if (typeid == -1) {
                throw new ParseException("Type " + type.getValue().trim() +
                                         " is not defined.");
            }
        }

        Vector subterms = new Vector();
        for (int i = 1; i < els.size(); i++) {
            Element el = els.get(i);
            if (el.getLocalName().equals(PLEX)) {
                subterms.add(parsePlex(el));
            } else if (el.getLocalName().equals(CTERM)) {
                subterms.add(parseCTerm(el));
            } else if (el.getLocalName().equals(IND)) {
                subterms.add(parseInd(el));
            } else if (el.getLocalName().equals(DATA)) {
                subterms.add(parseData(el));
            } else if (el.getLocalName().equals(SKOLEM)) {
                subterms.add(parseSkolem(el));
            } else if (el.getLocalName().equals(VAR)) {
                subterms.add(parseVar(el));
            } else if (el.getLocalName().equals(SLOT)) {
                subterms.add(parseSlot(el));
            } else if (el.getLocalName().equals(RESL)) {
                subterms.add(parseResl(el));
            } else if (el.getLocalName().equals(REPO)) {
                subterms.add(parseRepo(el));
            } else {
                throw new ParseException(
                        "Expr should only contain Plex, Expr, Ind, Data, Var and slot, repo, resl.");
            }

        }

        Term t = new Term(symbol, SymbolTable.INOROLE, typeid, subterms);
        return t;
    }

    /**
     * Method to parse an Atom
     *
     * @param Atom Element The XOM element that represents the Atom
     * to be parsed.
     *
     * @param head boolean This tells the engine if the atom is a head of a rule.
     *
     * @param neg boolean This tells the engine if the atom is a negative atom (Neg)
     *
     * @return Term Returns the data structure that represents the Atom in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the Atom.
     */
     
    private Term parseAtom(Element atom, boolean head, boolean neg) throws ParseException {
        
        Elements els = atom.getChildElements();
        boolean foundoid = false;
		
		//checking for op tag before the rel
        Element op = els.get(0);
        
        boolean optionalOp = false;
        
        if (op.getLocalName().equals(OP)) {
        	   optionalOp = true;
        }   
        
        Element rel = null;
        
        if(optionalOp){   
        	Elements relTag = op.getChildElements();
        	rel = relTag.get(0);
        
        }
         
        if(!optionalOp){
        	rel = els.get(0);
        	
        } 
                
        if (!rel.getLocalName().equals(REL)) {
            throw new ParseException(
                    "First child of op in an atom must be a Rel element.");
        }
		
        String relname = rel.getValue().trim();
        if(neg)
            relname = "$neg-" + relname;

        int symbol = SymbolTable.internSymbol(relname);

        Vector subterms = new Vector();
        for (int i = 1; i < els.size(); i++) {
            Element el = els.get(i);
            if (el.getLocalName().equals(PLEX)) {
                subterms.add(parsePlex(el));
            } else if (el.getLocalName().equals(CTERM)) {
                subterms.add(parseCTerm(el));
            } else if (el.getLocalName().equals(IND)) {
                subterms.add(parseInd(el));
            } else if (el.getLocalName().equals(DATA)) {
                subterms.add(parseData(el));
            } else if (el.getLocalName().equals(SKOLEM)){
                subterms.add(parseSkolem(el));
            } else if (el.getLocalName().equals(VAR)) {
                subterms.add(parseVar(el));
            } else if (el.getLocalName().equals(SLOT)) {
                subterms.add(parseSlot(el));
            } else if (el.getLocalName().equals(RESL)) {
                subterms.add(parseResl(el));
            } else if (el.getLocalName().equals(REPO)) {
                subterms.add(parseRepo(el));
            } else if (el.getLocalName().equals(OID)) {
                if (foundoid) {
                    throw new ParseException(
                            "Atom should only contain one oid element.");
                }
                subterms.add(parseOid(el));
                foundoid = true;
            } else {
                throw new ParseException(
                        "Atom should only contain Plex, Cterm, Ind, Data, Var, slot, repo, resl and oid.");
            }
         	
        }
		//if foundoid is false
		//no idea what this is doing
        if (!foundoid) {
            if (head) {
                String symname = "$gensym" + SymbolTable.genid++;
                int symid = SymbolTable.internSymbol(symname);
                Term t2 = new Term(symid, SymbolTable.IOID, Types.IOBJECT);
                subterms.add(t2);
            } else {
                String varname = "$ANON" + anonid++;
                int symid = this.internVariable(varname);
                Vector types = new Vector();
                types.add(new Integer(Types.IOBJECT));
                this.varClasses.put(new Integer(symid), types);
                Term t2 = new Term(symid, SymbolTable.IOID, Types.IOBJECT);
                subterms.add(t2);
            }
        }

		

        Term t = new Term(symbol, SymbolTable.INOROLE, Types.IOBJECT, subterms);
        t.setAtom(true);
        return t;
    }

    /**
     * Method to parse a Slot
     *
     * @param slot Element The XOM element that represents the slot
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the slot in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the slot.
     */
    
    private Term parseSlot(Element slot) throws ParseException {
        Elements els = slot.getChildElements();
        if (els.size() != 2) {
            throw new ParseException("Slot must have two child elements.");
        }
        
        Element name = els.get(0);
		boolean dataSlot = false;
		if(name.getLocalName().equals(DATA)){
			dataSlot = true;
		}
       
        if (!name.getLocalName().equals(IND) && !name.getLocalName().equals(DATA)) {
            throw new ParseException("Only Ind and Data slot names are supported.");
        }    
		//Getting the Role from the symbol Table, it will assign one if it
		//doesnt already exist
        int role = SymbolTable.internRole(name.getValue().trim());
       
        Element value = els.get(1);
        
        Term t;
		//Figuring out the type of term it should be
        if (value.getLocalName().equals(PLEX)) {
            t = parsePlex(value);
        } else if (value.getLocalName().equals(CTERM)) {
            t = parseCTerm(value);
        } else if (value.getLocalName().equals(IND)) {
            t = parseInd(value);
        } else if (value.getLocalName().equals(DATA)) {
            t = parseData(value);
        } else if (value.getLocalName().equals(SKOLEM)){
            t = parseSkolem(value);
        } else if (value.getLocalName().equals(VAR)) {
            t = parseVar(value);
        } else {
            throw new ParseException(
                    "Slot value should be either Plex, Expr, Ind, Data or Var.");
        }
		
        t.setRole(role);
        
        if(dataSlot){
        	t.setDataSlot(true);
        }
        
        return t;
    }
   
    /**
     * Method to parse a resl (Rested Slot)
     *
     * @param resl Element The XOM element that represents the resl
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the resl in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the resl.
     */
          
    private Term parseResl(Element resl) throws ParseException {
        Elements els = resl.getChildElements();
        if (els.size() > 1) {
            throw new ParseException(
                    "resl element should only contain child element.");
        }

        Element el = els.get(0);
        Term t;
        if (el.getLocalName().equals(VAR)) {
            t = parseVar(el);
        } else if (el.getLocalName().equals(PLEX)) {
            t = parsePlex(el);
        } else {
            throw new ParseException(
                    "resl element should only contain Var or Plex as a child element.");
        }

        t.setRole(SymbolTable.IREST);
        return t;
    }

    /**
     * Method to parse a repo (Rested Positional Slot)
     *
     * @param repo Element The XOM element that represents the repo
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the repo in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the repo.
     */ 
     
    private Term parseRepo(Element repo) throws ParseException {
        Elements els = repo.getChildElements();
        if (els.size() > 1) {
            throw new ParseException(
                    "repo element should only contain child element.");
        }

        Element el = els.get(0);
        Term t;
        if (el.getLocalName().equals(VAR)) {
            t = parseVar(el);
        } else if (el.getLocalName().equals(PLEX)) {
            t = parsePlex(el);
        } else {
            throw new ParseException(
                    "repo element should only contain Var or Plex as a child element.");
        }

        t.setRole(SymbolTable.IPREST);
        return t;
    }

    /**
     * Method to parse a naf (Negation as Failure)
     *
     * @param naf Element The XOM element that represents the naf atom
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the naf atom in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the naf.
     */ 

    private Term parseNaf(Element naf) throws ParseException {
        Elements els = naf.getChildElements();
        if (els.size() != 1) {
            throw new ParseException(
                    "Naf elements should only contain one child.");
        }

        Element el = els.get(0);
        Vector subterms = new Vector();
        if (el.getLocalName().equals(ATOM)) {
            subterms.add(parseAtom(el, false, false));
        } else if (el.getLocalName().equals(AND)) {
            els = el.getChildElements();
            for (int i = 0; i < els.size(); i++) {
                Element el2 = els.get(i);
                if (!el2.getLocalName().equals(ATOM)) {
                    throw new ParseException(
                            "And child of Naf element should only contain Atom elements.");
                }

                subterms.add(parseAtom(el2, false, false));
            }
        } else {
            throw new ParseException(
                    "Naf element should only contain Atom or And child element.");
        }

        Term t = new Term(SymbolTable.INAF, SymbolTable.INOROLE, Types.IOBJECT,
                          subterms);

        t.setAtom(true);
        return t;
    }

    /**
     * Method to parse a sko(Skolem Constant)
     *
     * @param sko Element The XOM element that represents the sko
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the sko in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the sko.
     */ 
     
    private Term parseSkolem(Element sko) throws ParseException{
        String skoname = sko.getValue().trim();

        if(skoname.equals("")){
            return new Term(SymbolTable.internSymbol("$gensym" + SymbolTable.genid++),
                            SymbolTable.INOROLE, Types.ITHING);
        }else{
            if(this.skolemMap.containsKey(skoname)){
                String sym = (String)skolemMap.get(skoname);
                return new Term(SymbolTable.internSymbol(sym),
                                  SymbolTable.INOROLE, Types.ITHING);
            }else{
                String sym = "$gensym" + (SymbolTable.genid++) + "$" + skoname;
                skolemMap.put(skoname, sym);
                return new Term(SymbolTable.internSymbol(sym),
                                SymbolTable.INOROLE, Types.ITHING);
            }
        }
    }

    /**
     * Initialize a variable for use. Stores the variable name and assigns a
     * integer identifier if it is unused in this clause; returns the existing
     * identifier if it has already been used in this clause.
     *
     * Variables are Negative and Ind are positive
     *
     * @param varName String The variable name.
     *
     * @return int The integer identifier for this variable
     */
        
    private int internVariable(String varName) {
        int idx;

        idx = variableNames.indexOf(varName);
        if (idx == -1) {
            idx = variableNames.size();
            variableNames.add(varName);
        }

        return -(idx + 1);
    }

    /**
     * A vector to hold the class information for the variables in the current
     * clause. This is used for normalizing the types given to a variable if
     * more than one type is given to a variable.
     *
     * For example: in the following clause p(?x: type1, ?x:type2). the types
     * are not the same on all occurances of the variable ?x - therefore the
     * types will be normalized and receive a type that is the intersection of
     * type1 and type2.
     */
    private Hashtable varClasses;

    /**
     * A method that will go through a term and fix all variable types to be
     * consistant.
     *
     * @param ct Term The term to normalize the types in.
     *
     * @param types Hashtable A hash table containing the normalized types for
     * each variable in the clause.
     */
 
    private void fixVarTypes(Term ct, Hashtable types) {
      //  logger.debug("Fixing term: " + ct.toPOSLString(true));
        for (int i = 0; i < ct.subTerms.length; i++) {
            if (ct.subTerms[i].isCTerm()) {
                fixVarTypes(ct.subTerms[i], types);
            } else if (ct.subTerms[i].getSymbol() < 0) {
                Integer sym = new Integer(ct.subTerms[i].getSymbol());
               // logger.debug("Fixing symbol = " + sym);
                Integer type = (Integer)types.get(sym);
                //logger.debug("Type = " + type);
                ct.subTerms[i].type = type.intValue();
            }
        }
    }

    /**
     * A method to find the normalized types for each variable in the clause.
     *
     * @return Hashtable A hashtable containing the normalized type for each
     * variable in the current clause.
     *
     * @throws ParseException A ParseException is thrown a variable does not
     * have a normalized form (type that is an intersection of all given types);
     * since Nothing inherits from all types this should never occur.
     */

    private Hashtable buildTypeTable() throws ParseException {
        Hashtable ht = new Hashtable();
        Enumeration e = varClasses.keys();

        while (e.hasMoreElements()) {
            Object key = e.nextElement();
            Vector value = (Vector) varClasses.get(key);
            int[] types = new int[value.size()];
            for (int i = 0; i < types.length; i++) {
                types[i] = ((Integer) value.get(i)).intValue();
            }

            int type = Types.greatestLowerBound(types);
            ht.put(key, new Integer(type));
        }
        return ht;
    }

}