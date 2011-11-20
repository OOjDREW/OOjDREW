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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.ruleml.oojdrew.parsing.RuleMLParser.RuleMLFormat;
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

public class RuleMLDocumentParser{

    private Hashtable<String, String> skolemMap;

    private Vector<DefiniteClause> clauses;
                  
    private Vector<String> variableNames;
    
    /**
     * This is used for generating unique anonymous variable ids
     */
    private int anonid = 1;
    
    /**
     * RuleML tag names
     */
    private RuleMLTagNames tagNames = null;
    
    private RuleMLFormat rulemlFormat;

    private Logger logger = Logger.getLogger("jdrew.oo.util.RuleMLParser");

    /**
     * A vector to hold the class information for the variables in the current
     * clause. This is used for normalizing the types given to a variable if
     * more than one type is given to a variable.
     *
     * For example: in the following clause p(?x: type1, ?x:type2). the types
     * are not the same on all occurrences of the variable ?x - therefore the
     * types will be normalized and receive a type that is the intersection of
     * type1 and type2.
     */
    private Hashtable<Integer, Vector<Integer>> varClasses;

    /**
     * Constructs the back-end parser.
     *
     * @param clauses Vector The vector to use as a buffer - this is generally
     * passed by the RuleMLParser front-end.
     */
    public RuleMLDocumentParser(RuleMLFormat format, Vector<DefiniteClause> clauses) {
        this.clauses = clauses;
        
        // Set default RuleML version
        this.rulemlFormat = format;
        this.tagNames = new RuleMLTagNames(format);
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
        this.skolemMap = new Hashtable<String, String>();
               
        Element root = doc.getRootElement();        
        Element firstChild = null;
        
        String rootName = root.getLocalName();
        // If RuleML is root element
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
		// If Assert is root element
		else if (rootName.equals(tagNames.ASSERT))
		{
		    firstChild = getFirstChildElement(root, tagNames.RULEBASE);
			if (firstChild == null)
			{
				// If no Rulebase element exists, it has to be RuleML 0.88
				if (rulemlFormat != RuleMLFormat.RuleML88)
				{
					rulemlFormat = RuleMLFormat.RuleML88;
					tagNames = new RuleMLTagNames(rulemlFormat);
				}
				
				firstChild = getFirstChildElement(root, tagNames.AND);
			}
		}
		// Otherwise use Query as root attribute
		else if (rulemlFormat.equals(RuleMLFormat.RuleMLQuery) && rootName.equals(tagNames.QUERY))
		{
			// Use query element as first child
			firstChild = root;
		}
		
        if (firstChild == null) {
        	// Note: first child only can get null if root is no query element
            throw new ParseException(
                    "RuleML or Assert element must contain an Rulebase or an And element!");
        }
	
        Elements children = firstChild.getChildElements();
        for (int i = 0; i < children.size(); i++) {
            Element child = skipRoleTag(children.get(i));
            String childName = child.getLocalName();
            if (childName.equals(tagNames.ATOM)) {
            	resetVariables();
                clauses.add(parseFact(child));
            } else if (childName.equals(tagNames.NEG)){
                clauses.addAll(parseNegFact(child));
            } else if (childName.equals(tagNames.IMPLIES)) {
            	resetVariables();
                clauses.addAll(parseImplies(child));
            }
        }
    }

    /**
     * This method is used to parse an Assertion in the RuleML Document.
     *
     * @param ass Element The XOM Element object that represents the assertion.
     *
     * @return Term A term object that represents the assertion in a way that
     * can be used by the reasoning engine.
     *
     * @throws ParseException Thrown if there is a serious error parsing the
     * assertion.
     */
    private Term parseAssert(Element ass) throws ParseException {
        Elements children = ass.getChildElements();
        
        Element firstChild = skipRoleTag(children.get(0));
		
        if (firstChild.getLocalName().equals(tagNames.ATOM)) {
            DefiniteClause dc = parseFact(firstChild);
            Vector<Term> v = new Vector<Term>();
            for (int i = 0; i < dc.atoms.length; i++) {
                v.add(dc.atoms[i]);
            }
            Term t = new Term(SymbolTable.IASSERT, SymbolTable.INOROLE,
                              Types.IOBJECT, v);
            t.setAtom(true);
            return t;
        } else if (firstChild.getLocalName().equals(tagNames.IMPLIES)) {
            Vector<DefiniteClause> v2 = parseImplies(firstChild);
            DefiniteClause dc = (DefiniteClause)v2.get(0);
            Vector<Term> v = new Vector<Term>();
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
    private Vector<DefiniteClause> parseNegFact(Element neg) throws ParseException {
        resetVariables();

        Elements atoms = neg.getChildElements("Atom");
        if(atoms.size() != 1){
            logger.error("OO jDREW only supports classical negation over single atoms.");
            throw new ParseException("OO jDREW only supports classical negation over single atoms.");
        }
		
        Element firstAtom = skipRoleTag(atoms.get(0));
	
        Term atm = parseAtom(firstAtom, true, true);

        Hashtable<Integer, Integer> types = this.buildTypeTable();
        this.fixVarTypes(atm, types);

        Vector<Term> atms = new Vector<Term>();
        atms.add(atm);

        DefiniteClause dc = new DefiniteClause(atms, variableNames);

        Vector<DefiniteClause> v = new Vector<DefiniteClause>();
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
    private DefiniteClause parseFact(Element atom) throws ParseException {
        Term atm = parseAtom(atom, true, false);

        Hashtable<Integer, Integer> types = this.buildTypeTable();
        this.fixVarTypes(atm, types);

        Vector<Term> atoms = new Vector<Term>();
        atoms.add(atm);

        DefiniteClause dc = new DefiniteClause(atoms, variableNames);
        return dc;
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
    private Vector<DefiniteClause> parseImplies(Element implies) throws
    	ParseException {

        Vector<DefiniteClause> newclauses = new Vector<DefiniteClause>();

		// Implies must have two children (excluding OID elements)
        Elements children = implies.getChildElements();
        
        // Set first and second child and skip OID element if exists.
        Element firstChild = skipRoleTag(children.get(0));
        Element secondChild = skipRoleTag(children.get(1));
        if (firstChild.getLocalName().equals(tagNames.OID))
        {
        	firstChild = skipRoleTag(children.get(1));
        	secondChild = skipRoleTag(children.get(2));
        }
        
        String firstChildName = firstChild.getLocalName();
        Element premise, conclusion;
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
		else
		{
			// Use default order
	        premise = firstChild;
	        conclusion = secondChild;
		}
		
		premise = skipRoleTag(premise);
		conclusion = skipRoleTag(conclusion);

        Vector<Term> subterms = new Vector<Term>();
        if (conclusion.getLocalName().equals(tagNames.ATOM)) {
            subterms.add(parseAtom(conclusion, true, false));
        } else if (conclusion.getLocalName().equals(tagNames.NEG)){
            Elements headatms = conclusion.getChildElements(tagNames.ATOM);
            if(headatms.size() != 1)
                throw new ParseException("Neg should have one ATOM element");

            Term atom = parseAtom(headatms.get(0), true, true);
            subterms.add(atom);

            String atomstr = atom.toPOSLString(true);

            String clause = "$Sinconsistent() :-";
            clause += atomstr + ", \"" + atomstr.substring(6) + ".";

            System.err.println(clause);

            POSLParser pp = new POSLParser();
            try{
                DefiniteClause dc2 = pp.parseDefiniteClause(clause);
                if (dc2 != null)
                    newclauses.add(dc2);
            } catch(Exception e) {
                throw new ParseException("Error creating inconsistency check rule.");
            }
        } else {
            throw new ParseException(
                    "Second element of Implies should always be an Atom or Neg element.");
        }
        
        String premiseName = premise.getLocalName();
        
        if (premiseName.equals(tagNames.ATOM)) {
            subterms.add(parseAtom(premise, false, false));
        } else if (premiseName.equals(tagNames.NAF)) {
            subterms.add(parseNaf(premise));
        } else if (premiseName.equals(tagNames.ASSERT)) {
            subterms.add(parseAssert(premise));
        } else if (premiseName.equals(tagNames.NEG)) {
            subterms.add(parseAtom(premise, false, true));
        } else if (premiseName.equals(tagNames.AND)) {
            children = premise.getChildElements();
            for (int i = 0; i < children.size(); i++) {
                Element el = skipRoleTag(children.get(i));
                if (el.getLocalName().equals(tagNames.ATOM)) {
                    subterms.add(parseAtom(el, false, false));
                } else if (el.getLocalName().equals(tagNames.NAF)) {
                    subterms.add(parseNaf(el));
                } else if (el.getLocalName().equals(tagNames.ASSERT)) {
                    subterms.add(parseAssert(el));
                } else if (el.getLocalName().equals(tagNames.NEG)){
                    subterms.add(parseAtom(el, false, true));
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
        Hashtable<Integer, Integer> types = this.buildTypeTable();
        logger.debug("Built Types");
        Iterator<Term> it = subterms.iterator();
        int i = 0;
        while(it.hasNext()){
            Term t = (Term)it.next();
            this.fixVarTypes(t, types);
            logger.debug("Fixed atom : " + i++);
        }

        DefiniteClause dc = new DefiniteClause(subterms, variableNames);
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
        Element element = oid.getChildElements().get(0);
        Term term = parseDefaultElement(element);
        term.role = SymbolTable.IOID;

        return term;
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
		return parseSimpleElement(ind);
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
    	Term term = parseSimpleElement(data);
    	term.setData(true);
    	
    	return term;
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
        String symbolName = var.getValue().trim();
        
        if (symbolName.isEmpty()) {
            symbolName = "$ANON" + anonid++;
        }

        int sym = this.internVariable(symbolName);
        int typeid = parseTypeAttribute(var);

        logger.debug("Parsing variable: symbol = " + sym + " type = " + typeid);

        Vector<Integer> v;
        
        if(this.varClasses.containsKey(sym)){
            v = varClasses.get(sym);
        } else {
            v = new Vector<Integer>();
            varClasses.put(sym, v);
        }

        v.add(typeid);

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
        Vector<Term> subterms = parseDefaultElements(plex);

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
    	Elements children = expr.getChildElements();
        Element op = children.get(0);
        
        boolean foundOp = false;
        
        if (op.getLocalName().equals(tagNames.OP)) {
			foundOp = true;
        }
		
		Element fun = null;
		if(foundOp) {
			Elements funTag = op.getChildElements();
			fun = funTag.get(0);
        }
        
        if(!foundOp){
        	fun = children.get(0);
        }
                
        if (!fun.getLocalName().equals(tagNames.FUN)) {
            throw new ParseException(
                    "First child of op in an Expr must be a Fun element.");
        }

        int symbol = SymbolTable.internSymbol(fun.getValue().trim());
        int typeid = parseTypeAttribute(expr);

        Vector<Term> subterms = parseDefaultElements(expr);
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

        Vector<Term> subterms = new Vector<Term>();
        int startIndex = getFirstChildElementIndex(children, 0) + 1;
        for (int i = startIndex; i < children.size(); i++) {
            Element element = children.get(i);
            Term term = null;
            if (element.getLocalName().equals(tagNames.OID)) {
                if (foundoid) {
                    throw new ParseException(
                            "Atom should only contain one oid element.");
                }
                term = parseOid(element);
                foundoid = true;
            } else {
            	term = parseDefaultElement(element);
            }
            subterms.add(term);
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
                Vector<Integer> types = new Vector<Integer>();
                types.add(Types.IOBJECT);
                this.varClasses.put(symid, types);
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
        Elements children = slot.getChildElements();
        
        Element firstChildName = children.get(0);
		boolean dataSlot = false;
		if(firstChildName.getLocalName().equals(tagNames.DATA)){
			dataSlot = true;
		}
       
        if (!firstChildName.getLocalName().equals(tagNames.IND) && !firstChildName.getLocalName().equals(tagNames.DATA)) {
            throw new ParseException("Only Ind and Data slot names are supported.");
        }    
		//Getting the Role from the symbol Table, it will assign one if it
		//doesnt already exist
        int role = SymbolTable.internRole(firstChildName.getValue().trim());
       
        Element element = children.get(1);
        Term term = parseDefaultElement(element);
        
        term.setRole(role);
        
        if(dataSlot){
        	term.setDataSlot(true);
        }
        
        return term;
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
    	Elements children = resl.getChildElements();
    	Element firstChild = skipRoleTag(children.get(0));
    	Term t = parseDefaultElement(firstChild);
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
    	Term t = parseResl(repo);
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
        Elements children = naf.getChildElements();
        Element el = skipRoleTag(children.get(0));
        
        Vector<Term> subterms = new Vector<Term>();
        if (el.getLocalName().equals(tagNames.ATOM)) {
            subterms.add(parseAtom(el, false, false));
        } else if (el.getLocalName().equals(tagNames.AND)) {
            children = el.getChildElements();
            for (int i = 0; i < children.size(); i++) {
                Element el2 = children.get(i);
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

        if(skoname.isEmpty()){
            return new Term(SymbolTable.internSymbol("$gensym" +
            				SymbolTable.genid++), SymbolTable.INOROLE,
            				Types.ITHING);
        }else{
            if(this.skolemMap.containsKey(skoname)){
                String sym = skolemMap.get(skoname);
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
     * A method that will go through a term and fix all variable types to be
     * consistant.
     *
     * @param complexTerm Term The term to normalize the types in.
     *
     * @param types Hashtable A hash table containing the normalized types for
     * each variable in the clause.
     */
    private void fixVarTypes(Term complexTerm, Hashtable<Integer, Integer> types) {
       // logger.debug("Fixing term: " + complexTerm.toPOSLString(true));
        for (int i = 0; i < complexTerm.subTerms.length; i++) {
            if (complexTerm.subTerms[i].isExpr()) {
                fixVarTypes(complexTerm.subTerms[i], types);
            } else if (complexTerm.subTerms[i].getSymbol() < 0) {
                Integer sym = complexTerm.subTerms[i].getSymbol();
                //logger.debug("Fixing symbol = " + sym);
                Integer type = (Integer)types.get(sym);
                //logger.debug("Type = " + type);
                complexTerm.subTerms[i].type = type.intValue();
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
    private Hashtable<Integer, Integer> buildTypeTable() throws ParseException {
        Hashtable<Integer, Integer> ht = new Hashtable<Integer, Integer>();
        Enumeration<Integer> e = varClasses.keys();

        while (e.hasMoreElements()) {
            int key = e.nextElement();
            Vector<Integer> value = varClasses.get(key);
            int[] types = new int[value.size()];
            for (int i = 0; i < types.length; i++) {
                types[i] = ((Integer) value.get(i)).intValue();
            }

            int type = Types.greatestLowerBound(types);
            ht.put(key, type);
        }
        return ht;
    }
    
    /**
     * Parses the given element and adds parsed data to subterms
     * 
     * Known elements are: Plex, Expr, Ind, Data, Var, slot, repo, resl
     * 
     * @param el Element to parse
     * @return Parsed element as Term
     * @throws ParseException Thrown if this method is not able to handle
     * element. 
     */
    private Term parseDefaultElement(Element el) throws ParseException
    {
    	String elementName = el.getLocalName();
    	Term result = null;
    	
        if (elementName.equals(tagNames.PLEX)) {
        	result = parsePlex(el);
        } else if (elementName.equals(tagNames.EXPR)) {
        	result = parseExpression(el);
        } else if (elementName.equals(tagNames.IND)) {
        	result = parseInd(el);
        } else if (elementName.equals(tagNames.DATA)) {
        	result = parseData(el);
        } else if (elementName.equals(tagNames.SKOLEM)) {
        	result = parseSkolem(el);
        } else if (elementName.equals(tagNames.VAR)) {
        	result = parseVar(el);
        } else if (elementName.equals(tagNames.SLOT)) {
        	result =  parseSlot(el);
        } else if (elementName.equals(tagNames.RESL)) {
        	result = parseResl(el);
        } else if (elementName.equals(tagNames.REPO)) {
        	result = parseRepo(el);
		} else {
			throw new ParseException(String.format(
					"Element (%s) not supported!", elementName));
        }
        
        return result;
    }
    
    /**
     * Get the inner element with role tag skipped
     * 
     * (e.g. <arg><Atom>...</Atom></arg> returns
     *       Atom element)
     * 
     * @param element Element for which role tags should be skipped
     * @return Element with role tags skipped
     */
    private Element skipRoleTag(Element element)
    {
    	Element result = element;
    	String elementName = element.getLocalName();
    	
    	boolean hasRoleTag = false;
    	if (elementName.equals(tagNames.ARG)) {
    		hasRoleTag = true;
    		logger.warn("arg element skipped.");
    	} else if (elementName.equals(tagNames.FORMULA)) {
    		hasRoleTag = true;
    		logger.warn("formula element skipped.");
    	} else if (elementName.equals(tagNames.ACT)) {
    		hasRoleTag = true;
    		logger.warn("act element skipped.");
    	} else if (elementName.equals(tagNames.DECLARE)) {
    		hasRoleTag = true;
    		logger.warn("declare element skipped.");
    	} else if (elementName.equals(tagNames.STRONG)) {
    		hasRoleTag = true;
    		logger.warn("strong element skipped.");
    	} else if (elementName.equals(tagNames.WEAK)) {
    		hasRoleTag = true;
    		logger.warn("weak element skipped.");
    	} else if (elementName.equals(tagNames.TORSO)) {
    		hasRoleTag = true;
    		logger.warn("torso element skipped.");
    	}
    	
    	if (hasRoleTag)
    	{
    		result = element.getChildElements().get(0);
    	}
    	
    	return result;
    }
   
   /**
     * Parses all children of the given element with the parseDefaultElement
     * method and returns all sub-terms created from those elements. 
     * 
     * @param element The element whose children should be parsed.
     * @return A collection of Term objects generated from the children.
     * @throws ParseException Thrown if the parseDefaultElement method is unable
     * to handle one of element's children.
     */
    private Vector<Term> parseDefaultElements(Element element) throws ParseException
    {
    	Elements children = element.getChildElements();
    	Vector<Term> subterms = new Vector<Term>();
   	
        for (int i = 0; i < children.size(); i++) {
            Element child = children.get(i);
            Term term = parseDefaultElement(child);
            subterms.add(term);
        }
        
        return subterms;
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
	 * 
	 * @param elements
	 *            Elements to search for child
	 * @param startIndex
	 *            Start index to start search at
	 * @return Index of the first element which is not labeled with OID. If not
	 *         exist, returns -1.
	 */
    private int getFirstChildElementIndex(Elements elements, int startIndex)
    {
    	for (int i = startIndex; i < elements.size(); i++)
    	{
    		Element child = skipRoleTag(elements.get(i));
    		
    		if (!child.getLocalName().equals(tagNames.OID))
    		{
    			return i;
    		}
    	}
    	return -1;
    }
    
    /**
     * Parse the type attribute of <Expr>, <Ind>, <Data> and <Var> elements.
     * 
     * @param element The element whose type attribute sould be parsed.
     * @return A unique numeric identifier generated by the type system.
     * @throws ParseException Thrown if the type specified in the type
     * attribute is invalid.
     */
    private int parseTypeAttribute(Element element) throws ParseException {
		Attribute type = element.getAttribute(tagNames.TYPE);
        
        int typeid = Types.IOBJECT;
        
        if (type != null) {
            typeid = Types.typeID(type.getValue().trim());
            if (typeid == -1) {
                throw new ParseException("Type " + type.getValue().trim() +
                                         " is not defined.");
            }
        }
        
		return typeid;
	}
    
    /**
     * Parse a simple element that just contains plain data (like <Ind> and
     * <Data>).
     * 
     * @param element The element to parse
     * 
     * @return A Term data structure that represents the data element in a way
     * that can be understood by the reasoning engine.
     * 
     * @throws ParseException Thrown if the type specified in the optional type
     * attribute is invalid.
     */
    private Term parseSimpleElement(Element element) throws ParseException
    {
    	String symbol = element.getValue().trim();
        int sym = SymbolTable.internSymbol(symbol);
        int typeid = parseTypeAttribute(element);

        return new Term(sym, SymbolTable.INOROLE, typeid);
    }
    
	/**
	 * Reset the internal variable name and variable type lookup tables.
	 */
	private void resetVariables() {
		this.variableNames = new Vector<String>();
		this.varClasses = new Hashtable<Integer, Vector<Integer>>();
	}
}