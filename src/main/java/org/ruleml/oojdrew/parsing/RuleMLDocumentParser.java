package org.ruleml.oojdrew.parsing;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.ruleml.oojdrew.Config;
import org.ruleml.oojdrew.parsing.RuleMLParser.RuleMLVersion;
import org.ruleml.oojdrew.util.DefiniteClause;
import org.ruleml.oojdrew.util.SymbolTable;
import org.ruleml.oojdrew.util.Term;
import org.ruleml.oojdrew.util.Types;

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

public class RuleMLDocumentParser implements IRuleMLParser, PreferenceChangeListener {

    private Hashtable skolemMap;

    private Vector clauses;
                  
    private Vector variableNames;
    
    /**
     * This is used for generating unique anonymous variable ids
     */
    private static int anonid = 1;

    /**
     * This is used to indicate if the document has an inner close attribute.
     */
    private boolean hasMapClosure = false;
    
    /**
     * RuleML tag names
     */
    private RuleMLTagNames tagNames = null;
    
    private RuleMLVersion ruleMLversion;

    Logger logger = Logger.getLogger("jdrew.oo.util.RuleMLParser");

    Config config;
    boolean compatibilityMode;

    /**
     * Constructs the back-end parser.
     *
     * @param clauses Vector The vector to use as a buffer - this is generally
     * passed by the RuleMLParser front-end.
     */
    public RuleMLDocumentParser(Vector clauses) {
        this.clauses = clauses;
        
        // Set default RuleML version
        this.ruleMLversion = RuleMLVersion.RuleML91;
        this.tagNames = new RuleMLTagNames(ruleMLversion);
        
        this.config = new Config();
        readConfig();
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
        Element firstChild = null;
        
        String rootName = root.getLocalName();
		if (rootName.equals(tagNames.RULEML))
		{
			root = getFirstChildElement(root, tagNames.ASSERT);
			if (root == null)
			{
	            throw new ParseException(
	                    "Assert has to be the first child of the RuleML element!");
			}
			firstChild = getFirstChildElement(root, tagNames.RULEBASE);
			if (firstChild == null)
			{
				firstChild = root;
			}
		} 
		else if (rootName.equals(tagNames.ASSERT))
		{
			firstChild = getFirstChildElement(root, tagNames.RULEBASE);
			if (firstChild == null)
			{
				// If no Rulebase element exists, it has to be RuleML 0.88
				ruleMLversion = RuleMLVersion.RuleML88;
				tagNames = new RuleMLTagNames(ruleMLversion);
				
				firstChild = getFirstChildElement(root, tagNames.AND);
			}
		}
		
        if (firstChild == null) 
        {
            throw new ParseException(
                    "RuleML or Assert element must contain an Rulebase or an And element!");
        }

        if (firstChild.getAttribute(tagNames.MAPCLOSURE) != null) {
            hasMapClosure = true;
            if (!firstChild.getAttributeValue(tagNames.MAPCLOSURE).equals(tagNames.UNIVERSAL)) {
                throw new ParseException(
                        "Only universal inner closures are currently supported.");
            }

        } else {
            logger.info("Document root has not innerclose attribute. Indiviual clauses must have closure attributes.");
        }
	
        Elements els = firstChild.getChildElements();
        for (int i = 0; i < els.size(); i++) {
            Element el = els.get(i);
            if (el.getLocalName().equals(tagNames.ATOM)) {
                clauses.add(parseFact(el));
            } else if (el.getLocalName().equals(tagNames.NEG)){
                clauses.addAll(parseNegFact(el));
            }
            else if (el.getLocalName().equals(tagNames.IMPLIES)) {
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
		
        if (el.getLocalName().equals(tagNames.ATOM)) {
            DefiniteClause dc = parseFact(el, false);
            Vector v = new Vector();
            for (int i = 0; i < dc.atoms.length; i++) {
                v.add(dc.atoms[i]);
            }
            Term t = new Term(SymbolTable.IASSERT, SymbolTable.INOROLE,
                              Types.IOBJECT, v);
            t.setAtom(true);
            return t;
        } else if (el.getLocalName().equals(tagNames.IMPLIES)) {
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

        if (!hasMapClosure)
        {
            String closure = atom.getAttributeValue(tagNames.CLOSURE);
            if (closure == null) {
                logger.error("No closure on clause.");
                throw new ParseException("No closure on clause.");
            }

            if (!closure.equals(tagNames.UNIVERSAL)) {
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
		
        if (!hasMapClosure) {
            //No inner close - should have
            String closure = atom.getAttributeValue(tagNames.CLOSURE);
            if (closure == null) {
                logger.error("No closure on clause.");
                throw new ParseException("No closure on clause.");
            }

            if (!closure.equals(tagNames.UNIVERSAL)) {
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

        if (!hasMapClosure) {
            //No inner close - should have
            String closure = implies.getAttributeValue(tagNames.CLOSURE);
            if (closure == null) {
                logger.error("No closure on clause.");
                throw new ParseException("No closure on clause.");
            }

            if (!closure.equals(tagNames.UNIVERSAL)) {
                logger.error("Only universal closures are currently supported.");
                throw new ParseException(
                        "Only universal inner closures are currently supported.");
            }
        }
		// Implies must have two children (excluding OID elements)
        Elements children = implies.getChildElements();
        if (getElementCount(children) != 2) {
            throw new ParseException(
                    "Implies element must contain a premise and a conclusion element.");
        }
        
        int currentIndex = getFirstChildElementIndex(children, 0);
        Element firstChild = children.get(currentIndex);
        String firstChildName = firstChild.getLocalName();
        
        currentIndex = getFirstChildElementIndex(children, currentIndex + 1);
        Element secondChild = children.get(currentIndex);
        
        Element premise;
        Element conclusion;
        
        // Check if implies starts with premise element
		if (firstChildName.equals(tagNames.PREMISE100) || firstChildName.equals(tagNames.PREMISE))
		{
			premise = firstChild.getChildElements().get(0);
			conclusion = secondChild.getChildElements().get(0);
		}
		// If implies starts with conclusion element
		else if (firstChildName.equals(tagNames.CONCLUSION100) || firstChildName.equals(tagNames.CONCLUSION))
		{
			premise = secondChild.getChildElements().get(0);
			conclusion = firstChild.getChildElements().get(0);
		}
		// No premise or conclusion tag available
		else if (compatibilityMode)
		{
			// Use backwards compatibility 
	        premise = firstChild;
	        conclusion = secondChild;
		}
		else
		{
			// Use default order
	        premise = secondChild;
	        conclusion = firstChild;
		}

        Vector atoms = new Vector();
        
        if (conclusion.getLocalName().equals(tagNames.ATOM)) {
            atoms.add(parseAtom(conclusion, true, false));
        } else if (conclusion.getLocalName().equals(tagNames.NEG)){
            Elements headatms = conclusion.getChildElements(tagNames.ATOM);
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

        if (premise.getLocalName().equals(tagNames.ATOM)) {
            atoms.add(parseAtom(premise, false, false));
        } else if (premise.getLocalName().equals(tagNames.NAF)) {
            atoms.add(parseNaf(premise));
        } else if (premise.getLocalName().equals(tagNames.ASSERT)) {
            atoms.add(parseAssert(premise));
        } else if (premise.getLocalName().equals(tagNames.NEG)) {
            atoms.add(parseAtom(premise, false, true));
        }
        else if (premise.getLocalName().equals(tagNames.AND)) {
            children = premise.getChildElements();
            for (int i = 0; i < children.size(); i++) {
                Element el = children.get(i);
                if (el.getLocalName().equals(tagNames.ATOM)) {
                    atoms.add(parseAtom(el, false, false));
                } else if (el.getLocalName().equals(tagNames.NAF)) {
                    atoms.add(parseNaf(el));
                } else if (el.getLocalName().equals(tagNames.ASSERT)) {
                    atoms.add(parseAssert(el));
                } else if (el.getLocalName().equals(tagNames.NEG)){
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
        if (el.getLocalName().equals(tagNames.IND)) {
            t = parseInd(el);
        } else if(el.getLocalName().equals(tagNames.DATA)) {
            t = parseData(el);
        } else if(el.getLocalName().equals(tagNames.SKOLEM)) {
            t = parseVar(el);
        } else if (el.getLocalName().equals(tagNames.VAR)) {
            t = parseVar(el);
        } else if (el.getLocalName().equals(tagNames.EXPR)) {
            t = parseExpression(el);
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
        Attribute type = ind.getAttribute(tagNames.TYPE);
        
        
        
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
        Attribute type = data.getAttribute(tagNames.TYPE);
      
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
        Attribute type = var.getAttribute(tagNames.TYPE);
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
            if (el.getLocalName().equals(tagNames.PLEX)) {
                subterms.add(parsePlex(el));
            } else if (el.getLocalName().equals(tagNames.EXPR)) {
                subterms.add(parseExpression(el));
            } else if (el.getLocalName().equals(tagNames.IND)) {
                subterms.add(parseInd(el));
            } else if (el.getLocalName().equals(tagNames.DATA)) {
                subterms.add(parseData(el));
            } else if (el.getLocalName().equals(tagNames.SKOLEM)) {
                subterms.add(parseSkolem(el));
            } else if (el.getLocalName().equals(tagNames.VAR)) {
                subterms.add(parseVar(el));
            } else if (el.getLocalName().equals(tagNames.SLOT)) {
                subterms.add(parseSlot(el));
            } else if (el.getLocalName().equals(tagNames.RESL)) {
                subterms.add(parseResl(el));
            } else if (el.getLocalName().equals(tagNames.REPO)) {
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
     * @param expr Element The XOM element that represents the Expr
     * to be parsed.
     *
     * @return Term Returns the data structure that represents the Expr in a way
     * that can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is an error parsing the Expr.
     */
          
    private Term parseExpression(Element expr) throws ParseException {
        
        Elements els = expr.getChildElements();
        Element op = els.get(0);
        
        boolean foundOp = false;
        
        if (op.getLocalName().equals(tagNames.OP)) {
			foundOp = true;
        }
		
		Element fun = null;
		if(foundOp){
			
			Elements ctorTag = op.getChildElements();
			fun = ctorTag.get(0);
        }
        
        if(!foundOp){
        	fun = els.get(0);
        }
                
        if (!fun.getLocalName().equals(tagNames.FUN)) {
            throw new ParseException(
                    "First child of op in an Expr must be a Fun element.");
        }

        int symbol = SymbolTable.internSymbol(fun.getValue().trim());

        int typeid = Types.IOBJECT;
        Attribute type = expr.getAttribute(tagNames.TYPE);
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
            if (el.getLocalName().equals(tagNames.PLEX)) {
                subterms.add(parsePlex(el));
            } else if (el.getLocalName().equals(tagNames.EXPR)) {
                subterms.add(parseExpression(el));
            } else if (el.getLocalName().equals(tagNames.IND)) {
                subterms.add(parseInd(el));
            } else if (el.getLocalName().equals(tagNames.DATA)) {
                subterms.add(parseData(el));
            } else if (el.getLocalName().equals(tagNames.SKOLEM)) {
                subterms.add(parseSkolem(el));
            } else if (el.getLocalName().equals(tagNames.VAR)) {
                subterms.add(parseVar(el));
            } else if (el.getLocalName().equals(tagNames.SLOT)) {
                subterms.add(parseSlot(el));
            } else if (el.getLocalName().equals(tagNames.RESL)) {
                subterms.add(parseResl(el));
            } else if (el.getLocalName().equals(tagNames.REPO)) {
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
        
        boolean foundoid = false;
               
        Elements children = atom.getChildElements();
        
		//checking for op tag before the rel
        Element op = getFirstChildElement(atom, tagNames.OP);
        
        Element rel = null;
        
        if(op != null){   
        	Elements relTag = op.getChildElements();
        	rel = relTag.get(0);
        }
        else {
        	rel = getFirstChildElement(atom, tagNames.REL);
        }
                
        if (rel == null) {
            throw new ParseException(
                    "First child of op in an atom must be a Rel element.");
        }
		
        String relname = rel.getValue().trim();
        if(neg) {
            relname = "$neg-" + relname;
        }

        int symbol = SymbolTable.internSymbol(relname);

        Vector subterms = new Vector();
        int startIndex = getFirstChildElementIndex(children, 0) + 1;
        for (int i = startIndex; i < children.size(); i++) {
            Element el = children.get(i);
            if (el.getLocalName().equals(tagNames.PLEX)) {
                subterms.add(parsePlex(el));
            } else if (el.getLocalName().equals(tagNames.EXPR)) {
                subterms.add(parseExpression(el));
            } else if (el.getLocalName().equals(tagNames.IND)) {
                subterms.add(parseInd(el));
            } else if (el.getLocalName().equals(tagNames.DATA)) {
                subterms.add(parseData(el));
            } else if (el.getLocalName().equals(tagNames.SKOLEM)){
                subterms.add(parseSkolem(el));
            } else if (el.getLocalName().equals(tagNames.VAR)) {
                subterms.add(parseVar(el));
            } else if (el.getLocalName().equals(tagNames.SLOT)) {
                subterms.add(parseSlot(el));
            } else if (el.getLocalName().equals(tagNames.RESL)) {
                subterms.add(parseResl(el));
            } else if (el.getLocalName().equals(tagNames.REPO)) {
                subterms.add(parseRepo(el));
            } else if (el.getLocalName().equals(tagNames.OID)) {
                if (foundoid) {
                    throw new ParseException(
                            "Atom should only contain one oid element.");
                }
                subterms.add(parseOid(el));
                foundoid = true;
            } else {
                throw new ParseException(
                        "Atom should only contain Plex, Expr, Ind, Data, Var, slot, repo, resl and oid.");
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
		if(name.getLocalName().equals(tagNames.DATA)){
			dataSlot = true;
		}
       
        if (!name.getLocalName().equals(tagNames.IND) && !name.getLocalName().equals(tagNames.DATA)) {
            throw new ParseException("Only Ind and Data slot names are supported.");
        }    
		//Getting the Role from the symbol Table, it will assign one if it
		//doesnt already exist
        int role = SymbolTable.internRole(name.getValue().trim());
       
        Element value = els.get(1);
        
        Term t;
		//Figuring out the type of term it should be
        if (value.getLocalName().equals(tagNames.PLEX)) {
            t = parsePlex(value);	
        } else if (value.getLocalName().equals(tagNames.EXPR)) {
            t = parseExpression(value);
        } else if (value.getLocalName().equals(tagNames.IND)) {
            t = parseInd(value);
        } else if (value.getLocalName().equals(tagNames.DATA)) {
            t = parseData(value);
        } else if (value.getLocalName().equals(tagNames.SKOLEM)){
            t = parseSkolem(value);
        } else if (value.getLocalName().equals(tagNames.VAR)) {
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
        if (el.getLocalName().equals(tagNames.VAR)) {
            t = parseVar(el);
        } else if (el.getLocalName().equals(tagNames.PLEX)) {
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
        if (el.getLocalName().equals(tagNames.VAR)) {
            t = parseVar(el);
        } else if (el.getLocalName().equals(tagNames.PLEX)) {
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
        if (el.getLocalName().equals(tagNames.ATOM)) {
            subterms.add(parseAtom(el, false, false));
        } else if (el.getLocalName().equals(tagNames.AND)) {
            els = el.getChildElements();
            for (int i = 0; i < els.size(); i++) {
                Element el2 = els.get(i);
                if (!el2.getLocalName().equals(tagNames.ATOM)) {
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
    
    /**
     * Get the first element with the given name
     * @param elements Element to search for the child
     * @param childName Name of the element to look for
     * @return First element which is labeled with childName 
     */
    private Element getFirstChildElement(Element element, String childName)
    {
    	Elements children = element.getChildElements();
    	for (int i = 0; i < children.size(); i++)
    	{
    		Element child = children.get(i);
    		if (child.getLocalName().equals(childName))
    		{
    			return child;
    		}
    	}
    	return null;
    }
   
    /**
     * Get the first element which is not labeled with OID
     * @param elements Elements to search for child
     * @param startIndex Start index to start search at
     * @return Index of the first element which is not labeled with OID 
     */
    private int getFirstChildElementIndex(Elements elements, int startIndex)
    {
    	for (int i = startIndex; i < elements.size(); i++)
    	{
    		Element child = elements.get(i);
    		if (!child.getLocalName().equals(tagNames.OID))
    		{
    			return i;
    		}
    	}
    	return -1;
    }
    
    /**
     * Gets the count of elements which do not have OID as element name
     * @param elements Elements to count
     * @return Amount of elements which do not have OID as element name
     */
    private int getElementCount(Elements elements)
    {
    	int count = 0;
    	for (int i = 0; i < elements.size(); i++)
    	{
    		Element child = elements.get(i);
    		if (!child.getLocalName().equals(tagNames.OID))
    		{
    			count++;
    		}
    	}
    	return count;
    }



	@Override
	public void preferenceChange(PreferenceChangeEvent arg0) {
		readConfig();
	}
	
	public void readConfig()
	{
		this.compatibilityMode = config.getRuleMLCompatibilityModeEnabled();
	}
}