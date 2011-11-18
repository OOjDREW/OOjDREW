package org.ruleml.oojdrew.parsing;

/**
 * Class used to handle exceptions when parsing a POSL type query
 */
public class POSLTypeQueryException extends Exception {
	
	/**
	 * Id of the Exception
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor for a RuleMLTypeQueryExcetion
	 * 
	 * @param message - error message
	 */
	POSLTypeQueryException(String message){
		super(message);
	}
}
	