package org.ruleml.oojdrew.parsing;

import nu.xom.Document;

public interface IRuleMLParser {
	
	/***
	 * Runs RuleML parser
	 * 
	 * @param doc Document to parse
	 * @throws ParseException
	 */
	public void parseRuleMLDocument(Document doc) throws ParseException;
}
