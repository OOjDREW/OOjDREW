package org.ruleml.oojdrew;

public class BindingPair {

	private String variable;
	private String value;
	
	BindingPair(String variable, String value){
		this.variable = variable;
		this.value = value;
	}
	
	public String getVariable(){
		return variable;
	}
	
	public String getValue(){
		return value;
	}
	
}
