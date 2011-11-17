package jdrew.oo.parsing;

/**
 * Class used to handle exceptions when parsing a RuleML type query
 */
public class RuleMLTypeQueryException extends Exception {
	
	/**
	 * Id of the Exception
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for a RuleMLTypeQueryExcetion
	 * 
	 * @param message - error message
	 */
	RuleMLTypeQueryException(String message){
		super(message);
	}
}