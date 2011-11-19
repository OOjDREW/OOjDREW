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

package org.ruleml.oojdrew.tests;

import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.ruleml.oojdrew.Configuration;
import org.ruleml.oojdrew.parsing.ParseException;
import org.ruleml.oojdrew.parsing.RuleMLParser;
import org.ruleml.oojdrew.parsing.RuleMLParser.RuleMLFormat;

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
			rmlParser.parseFile(RuleMLFormat.RuleML91, ruleMLFile);
		} catch (Exception e1) {
			thrown = true;
		}
		
		// Parsing in compatibility mode should throw no exception
		assertEquals(shouldThrow, thrown);
	}
}