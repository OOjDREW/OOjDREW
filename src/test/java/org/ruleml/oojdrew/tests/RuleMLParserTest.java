package org.ruleml.oojdrew.tests;

import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.ruleml.oojdrew.Configuration;
import org.ruleml.oojdrew.parsing.ParseException;
import org.ruleml.oojdrew.parsing.RuleMLParser;
import org.ruleml.oojdrew.parsing.RuleMLParser.RuleMLVersion;

import junit.framework.TestCase;

public class RuleMLParserTest extends TestCase {
	private Configuration config;
	
	public RuleMLParserTest(String testName)
	{
		super(testName);
		this.config = new MockConfiguration();
	}

	public void testRuleML91CompatibilityParsing()
	{
		config.setRuleMLCompatibilityModeEnabled(true);
		genericRuleMLParsingTest("src/test/java/org/ruleml/oojdrew/tests/RuleMLTestCases/discount_091_compat.ruleml.xml", false);
		
		config.setRuleMLCompatibilityModeEnabled(false);
		genericRuleMLParsingTest("src/test/java/org/ruleml/oojdrew/tests/RuleMLTestCases/discount_091_compat.ruleml.xml", true);
	}
	
	public void testRuleML91Parsing()
	{
		config.setRuleMLCompatibilityModeEnabled(false);
		genericRuleMLParsingTest("src/test/java/org/ruleml/oojdrew/tests/RuleMLTestCases/discount-0.91.ruleml.xml", false);
	}
	
	public void testRuleML100Parsing()
	{
		config.setRuleMLCompatibilityModeEnabled(false);
		genericRuleMLParsingTest("src/test/java/org/ruleml/oojdrew/tests/RuleMLTestCases/discount-1.0.ruleml.xml", false);
	}
	
	public void genericRuleMLParsingTest(String ruleMLFile, boolean shouldThrow)
	{
		boolean thrown = false;

		RuleMLParser rmlParser = new RuleMLParser(config);
		
		try {
			rmlParser.parseFile(RuleMLVersion.RuleML91, ruleMLFile);
		} catch (Exception e1) {
			thrown = true;
		}
		
		// Parsing in compatibility mode should throw no exception
		assertEquals(shouldThrow, thrown);
	}
}
