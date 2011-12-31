package org.ruleml.oojdrew.tests;

import java.io.File;

import junit.framework.TestCase;

import org.ruleml.oojdrew.COjDA;
import org.ruleml.oojdrew.SyntaxFormat;
import org.ruleml.oojdrew.parsing.RuleMLFormat;
import org.ruleml.oojdrew.util.Util;

public class COjDATest extends TestCase {
    
    private COjDA api;
    
    public static void main(String[] args) {
        try {
            COjDATest test = new COjDATest("RuleML 1.0 parsing via API (StudyCourse)");
            test.testRuleML100ParsingStudyCourse();

            System.out.println("All tests passed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public COjDATest(String testName) {
        super(testName);
        
        api = COjDA.getCOjDA();
    }
    
    public void testRuleML100ParsingStudyCourse() {
        api.configureAPI(RuleMLFormat.RuleML100);
        
        String testCaseDirectory = "src/test/java/org/ruleml/oojdrew/tests/RuleMLTestCases/";
        
        // Knowledge base and corresponding syntax format
        File rmlKnowledgeBase = new File(testCaseDirectory + "studycourse.ruleml.xml");
        SyntaxFormat kbSyntax = SyntaxFormat.RULEML;
        
        // Query and corresponding syntax format
        File rmlQuery = new File(testCaseDirectory + "studycourse.rulemlquery.xml");
        SyntaxFormat querySyntax = SyntaxFormat.RULEML;
        
        // Expected query answer
        File rmlQueryAnswer = new File(testCaseDirectory + "studycourse.rulemlqueryanswer.xml");
        
        genericCOjDATestTest(rmlKnowledgeBase, kbSyntax, rmlQuery, querySyntax, rmlQueryAnswer);
    }
    
    public void genericCOjDATestTest(File knowledgeBase, SyntaxFormat kbSyntax, File query, SyntaxFormat querySyntax, File queryAnswer) {
        boolean testPassed = false;
        try {
            api.initializeKnowledgeBase(kbSyntax, knowledgeBase); 
            String currentResult = api.issueKnowledgebaseQuery(querySyntax, query);
            String expectedResult = Util.readFile(queryAnswer);
            
            // Remove all leading and trailing whitespaces and carriage returns
            String regex = "(?m)((^\\s+)|(\\s+$)|(\\r))";
            expectedResult = expectedResult.replaceAll(regex, "");
            currentResult = currentResult.replaceAll(regex, "");
            testPassed = currentResult.equals(expectedResult);
        } catch (Exception e) {
            testPassed = false;
            e.printStackTrace();
        }

        assertEquals(testPassed, true);
    }
}
