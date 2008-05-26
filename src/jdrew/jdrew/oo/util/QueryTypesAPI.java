package jdrew.oo.util;

public class QueryTypesAPI {

	
	public QueryTypesAPI(){
		
	}
	
	public String executeQuery(String RuleMLTypeQuery) throws RuleMLTypeQueryExcetion {
		
		String answer = "<RuleML>\n\t<Rulebase>";
		
		TypeQueryParserRuleML qp = new TypeQueryParserRuleML(RuleMLTypeQuery);
				
		
		
		
		
		answer += "\n\t</Rulebase>\n</RuleML>";
		
		return answer;
	}
	
	
	
	
}
