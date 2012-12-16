package org.ruleml.oojdrew.tests;

import org.ruleml.oojdrew.util.Util;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TypeInfereceTest extends TestCase {
	
	
	public static void testTypeInference(){
				
	    Assert.assertEquals(2, Util.getTypeForInference("1"));
	    Assert.assertEquals(2, Util.getTypeForInference("1234"));
	    Assert.assertEquals(2, Util.getTypeForInference("-5"));
	    
	    Assert.assertEquals(3, Util.getTypeForInference("1.0"));
	    Assert.assertEquals(3, Util.getTypeForInference("1.234235"));
	    Assert.assertEquals(3, Util.getTypeForInference("0.5"));
	    
	    Assert.assertEquals(4, Util.getTypeForInference("Hello World"));
	    Assert.assertEquals(4, Util.getTypeForInference("twentythree"));
	}
	

}
