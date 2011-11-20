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

import junit.framework.TestCase;

import org.ruleml.oojdrew.Configuration;
import org.ruleml.oojdrew.parsing.RuleMLParser;
import org.ruleml.oojdrew.parsing.RuleMLParser.RuleMLFormat;

public class RuleMLParserTest extends TestCase {
	private Configuration config;
	
	public static void main(String[] args)
	{
		try
		{
			RuleMLParserTest test = new RuleMLParserTest("RuleML 0.91 Compatibility Parsing (discount)");
			test.testRuleML91CompatibilityParsing();
			
			test = new RuleMLParserTest("RuleML 0.91 Parsing (discount)");
			test.testRuleML91Parsing();
			
			test = new RuleMLParserTest("RuleML 1.0 Parsing (discount)");
			test.testRuleML100Parsing();
			
			System.out.println("All tests passed.");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public RuleMLParserTest(String testName)
	{
		super(testName);
		this.config = new MockConfiguration();
	}

	public void testRuleML91CompatibilityParsing()
	{
		config.setValidateRuleMLEnabled(true);
		genericRuleMLParsingTest("src/test/java/org/ruleml/oojdrew/tests/RuleMLTestCases/discount_091_compat.ruleml.xml", false);
	}
	
	public void testRuleML91Parsing()
	{
		config.setValidateRuleMLEnabled(false);
		genericRuleMLParsingTest("src/test/java/org/ruleml/oojdrew/tests/RuleMLTestCases/discount-0.91.ruleml.xml", false);
	}
	
	public void testRuleML100Parsing()
	{
		config.setValidateRuleMLEnabled(false);
		genericRuleMLParsingTest("src/test/java/org/ruleml/oojdrew/tests/RuleMLTestCases/discount-1.0.ruleml.xml", false);
	}
	
	public void genericRuleMLParsingTest(String ruleMLFile, boolean shouldThrow)
	{
		boolean thrown = false;

		RuleMLParser rmlParser = new RuleMLParser(config);
		
		try {
			rmlParser.parseFile(RuleMLFormat.RuleML91, ruleMLFile);
		} catch (Exception e1) {
			e1.printStackTrace();
			thrown = true;
		}
		
		// Parsing in compatibility mode should throw no exception
		assertEquals(shouldThrow, thrown);
	}
}
