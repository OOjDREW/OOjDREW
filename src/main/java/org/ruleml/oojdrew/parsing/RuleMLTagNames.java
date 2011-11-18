package org.ruleml.oojdrew.parsing;

import org.ruleml.oojdrew.parsing.RuleMLParser.RuleMLVersion;

public class RuleMLTagNames {
	
	public final String RULEML = "RuleML";
	
	public final String RULEBASE = "Rulebase";
    public final String ASSERT = "Assert";
    public final String AND = "And";
    public final String ATOM = "Atom";
    public final String UNIVERSAL = "universal";
    public final String IMPLIES = "Implies";
    public final String TYPE = "type";
    public final String PLEX = "Plex";
    public final String IND = "Ind";
    public final String VAR = "Var";
    public final String RESL = "resl";
    public final String SLOT = "slot";
    public final String NAF = "Naf";
    public final String REPO = "repo";
    public final String REL = "Rel";
    public final String CLOSURE = "closure";
    public final String OID = "oid";
    public final String SKOLEM = "Skolem";
    public final String NEG = "Neg";
    
	public final String DATA = "Data";
    
	public final String OP;
	
	public final String QUERY = "Query";
	
	public final String EXPR; 
	public final String FUN; 
    public final String MAPCLOSURE;
    
    public final String PREMISE100 = "if";
    public final String CONCLUSION100 = "then";
    
    public final String PREMISE = "body";
    public final String CONCLUSION = "head";
	
	public RuleMLTagNames(RuleMLVersion ruleMLversion)
	{
		if (ruleMLversion == RuleMLVersion.RuleML91 || ruleMLversion == RuleMLVersion.RuleML100)
		{
			EXPR = "Expr";
			FUN = "Fun";
			MAPCLOSURE  = "mapClosure";
			OP = "op";
		}
		else // RuleML 0.88
		{
			EXPR = "Cterm";
			FUN = "Ctor";
			MAPCLOSURE = "innerclose";
			OP = "opr";
		}
	}
}
