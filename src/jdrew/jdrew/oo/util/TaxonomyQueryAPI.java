package jdrew.oo.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

public class TaxonomyQueryAPI {

	private QueryTypes typeQuery;
	
	public TaxonomyQueryAPI(File typeFile) throws ValidityException, ParsingException, IOException{
		Types.reset();
		RDFSParser.parseRDFSFile(typeFile);
		typeQuery = new QueryTypes();
	}

	public TaxonomyQueryAPI(String typeFile) throws ValidityException, ParsingException, IOException, ParseException{
		Types.reset();
		RDFSParser.parseRDFSString(typeFile);
		typeQuery = new QueryTypes();
	}
	
	public String executeQueryRuleML(String RuleMLTypeQuery) throws RuleMLTypeQueryExcetion, ValidityException, ParseException, ParsingException, IOException {
		
		String answer = "<RuleML>\n\t<Answer>\n";
		
		TypeQueryParserRuleML rmlTParser = new TypeQueryParserRuleML(RuleMLTypeQuery);
		Elements elements = rmlTParser.parseForPredicate();
		String predicate = rmlTParser.getPredicate();
		
		SubsumesStructure subPlus = null;
		SubsumesStructure sub = null;
		LUBGLBStructure lub = null;
		LUBGLBStructure glb = null;
		
		if(predicate.equalsIgnoreCase(TypeQueryParserRuleML.SUBSUMESPLUS)){
			
			subPlus = rmlTParser.parseElementsSubsumesAndSubsumesPlus(elements);
			
			//rel rel
			if(!subPlus.getSuperVar() && !subPlus.getSubVar()){
				answer = answer + "\t\t<Ind>";
				answer = answer + typeQuery.isSuperClass(subPlus.getSuperName(),subPlus.getSubName());
				answer = answer + "</Ind>";
				
			//var rel get all super classes
			}else if(subPlus.getSuperVar() && !subPlus.getSubVar()){

				String term1VarName = subPlus.getSuperName();
				String[] superClasses = typeQuery.findAllSuperClasses(subPlus.getSubName());
							
				for(int i = 0; i < superClasses.length; i++){
					answer = answer + "\t\t<Rulebase>\n";
					answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
					answer = answer + "\t\t\t\t<Var>" + term1VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + superClasses[i] + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t</Rulebase>";
					if(i != superClasses.length -1)
						answer = answer + "\n";					
				}
							
			//rel var get all sub classes
        		}else if(!subPlus.getSuperVar() && subPlus.getSubVar()){

				String term2VarName = subPlus.getSubName();
				String[] subClasses = typeQuery.findAllSubClasses(subPlus.getSuperName());
				
				for(int i = 0; i < subClasses.length; i++){
					answer = answer + "\t\t<Rulebase>\n";
					answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
					answer = answer + "\t\t\t\t<Var>" + term2VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + subClasses[i] + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t</Rulebase>";
					if(i != subClasses.length -1)
						answer = answer + "\n";	
				}
			//var var get all relations
			}else if(subPlus.getSuperVar() && subPlus.getSubVar()){

				String term2VarName = subPlus.getSubName();
				String term1VarName = subPlus.getSuperName();

    			Iterator vit1 = typeQuery.findAllSuperClassesOfEverything().iterator();
    			
    			while(vit1.hasNext()){

					answer = answer + "\t\t<Rulebase>\n";
					answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
					answer = answer + "\t\t\t\t<Var>" + term1VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + vit1.next().toString() + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
					answer = answer + "\t\t\t\t<Var>" + term2VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + vit1.next().toString() + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t</Rulebase>";
					
					if(vit1.hasNext())
						answer = answer + "\n";	

    			}

			}	
		}else if(predicate.equalsIgnoreCase(TypeQueryParserRuleML.SUBSUMES)){
			
			sub = rmlTParser.parseElementsSubsumesAndSubsumesPlus(elements);
			
			//rel rel
			if(!sub.getSuperVar() && !sub.getSubVar()){
				answer = answer + "\t\t<Ind>";
				answer = answer + typeQuery.isDirectSuperClass(sub.getSuperName(),sub.getSubName());
				answer = answer + "</Ind>";
				
			//var rel get all super classes
			}else if(sub.getSuperVar() && !sub.getSubVar()){

				String term1VarName = sub.getSuperName();
				String[] superClasses = typeQuery.getDirectSuperClasses(sub.getSubName());
							
				for(int i = 0; i < superClasses.length; i++){
					answer = answer + "\t\t<Rulebase>\n";
					answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
					answer = answer + "\t\t\t\t<Var>" + term1VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + superClasses[i] + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t</Rulebase>";
					if(i != superClasses.length -1)
						answer = answer + "\n";					
				}
							
			//rel var get all sub classes
        		}else if(!sub.getSuperVar() && sub.getSubVar()){

				String term2VarName = sub.getSubName();
				String[] subClasses = typeQuery.getDirectSubClasses(sub.getSuperName());
				
				for(int i = 0; i < subClasses.length; i++){
					answer = answer + "\t\t<Rulebase>\n";
					answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
					answer = answer + "\t\t\t\t<Var>" + term2VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + subClasses[i] + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t</Rulebase>";
					if(i != subClasses.length -1)
						answer = answer + "\n";	
				}
			//var var get all relations
			}else if(sub.getSuperVar() && sub.getSubVar()){

				String term2VarName = sub.getSubName();
				String term1VarName = sub.getSuperName();

    			Iterator vit1 = typeQuery.findAllDirectSuperClassesOfEverything().iterator();
    			
    			while(vit1.hasNext()){

					answer = answer + "\t\t<Rulebase>\n";
					answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
					answer = answer + "\t\t\t\t<Var>" + term1VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + vit1.next().toString() + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
					answer = answer + "\t\t\t\t<Var>" + term2VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + vit1.next().toString() + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t</Rulebase>";
					
					if(vit1.hasNext())
						answer = answer + "\n";	
    			}
			}	
			
		}else if(predicate.equalsIgnoreCase(TypeQueryParserRuleML.LUB)){
			
			lub = rmlTParser.parseElementsGLBandLUB(elements);
			
			if(lub.getResultVar()){
				
				  ArrayList<String> terms = lub.getTerms();
                  
                  String[] lubArray = new String[terms.size()];

                  for(int i = 0; i < terms.size();i++)
                          lubArray[i] = terms.get(i);

                  String leastUpperBound = typeQuery.leastUpperBound(lubArray);
                  String resultVar = lub.getResultVarName();
				  answer = answer + "\t\t<Rulebase>\n";
				  answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
				  answer = answer + "\t\t\t\t<Var>" + resultVar + "</Var>\n";
				  answer = answer + "\t\t\t\t<Ind>" + leastUpperBound + "</Ind>\n";
				  answer = answer + "\t\t\t</Equal>\n";
				  answer = answer + "\t\t</Rulebase>";
                  
                  
			}
			
		}else if(predicate.equalsIgnoreCase(TypeQueryParserRuleML.GLB)){
			
			glb = rmlTParser.parseElementsGLBandLUB(elements);
			
			if(glb.getResultVar()){
				
				  ArrayList<String> terms = glb.getTerms();
                  
                  String[] glbArray = new String[terms.size()];

                  for(int i = 0; i < terms.size();i++)
                	  glbArray[i] = terms.get(i);

                  String greatestLowerBound = typeQuery.greatestLowerBound(glbArray);
                  String resultVar =  glb.getResultVarName();
				  answer = answer + "\t\t<Rulebase>\n";
				  answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
				  answer = answer + "\t\t\t\t<Var>" + resultVar + "</Var>\n";
				  answer = answer + "\t\t\t\t<Ind>" + greatestLowerBound + "</Ind>\n";
				  answer = answer + "\t\t\t</Equal>\n";
				  answer = answer + "\t\t</Rulebase>";

			}
			
		}

		answer += "\n\t</Answer>\n</RuleML>";
		
		return answer;
	}
	
	public String executeQueryPOSL(String poslTypeQuery) throws ValidityException, POSLTypeQueryExcetion, ParseException, ParsingException, IOException, Exception{
		
		TypeQueryParserPOSL poslTParser = new TypeQueryParserPOSL(poslTypeQuery);
		Term[] queryTerms = poslTParser.parseForPredicate();
		String predicate = poslTParser.getPredicate();
		String answer = "<RuleML>\n\t<Answer>\n";
		
		SubsumesStructure subPlus = null;
		SubsumesStructure sub = null;
		LUBGLBStructure lub = null;
		LUBGLBStructure glb = null;
		
		if(predicate.equalsIgnoreCase(TypeQueryParserRuleML.SUBSUMESPLUS)){
			
			subPlus = poslTParser.parseElementsSubsumesAndSubsumesPlus(queryTerms);
			
			//rel rel
			if(!subPlus.getSuperVar() && !subPlus.getSubVar()){
				answer = answer + "\t\t<Ind>";
				answer = answer + typeQuery.isSuperClass(subPlus.getSuperName(),subPlus.getSubName());
				answer = answer + "</Ind>";
				
			//var rel get all super classes
			}else if(subPlus.getSuperVar() && !subPlus.getSubVar()){

				String term1VarName = subPlus.getSuperName();
				String[] superClasses = typeQuery.findAllSuperClasses(subPlus.getSubName());
							
				for(int i = 0; i < superClasses.length; i++){
					answer = answer + "\t\t<Rulebase>\n";
					answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
					answer = answer + "\t\t\t\t<Var>" + term1VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + superClasses[i] + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t</Rulebase>";
					if(i != superClasses.length -1)
						answer = answer + "\n";					
				}
							
			//rel var get all sub classes
        		}else if(!subPlus.getSuperVar() && subPlus.getSubVar()){

				String term2VarName = subPlus.getSubName();
				String[] subClasses = typeQuery.findAllSubClasses(subPlus.getSuperName());
				
				for(int i = 0; i < subClasses.length; i++){
					answer = answer + "\t\t<Rulebase>\n";
					answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
					answer = answer + "\t\t\t\t<Var>" + term2VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + subClasses[i] + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t</Rulebase>";
					if(i != subClasses.length -1)
						answer = answer + "\n";	
				}
			//var var get all relations
			}else if(subPlus.getSuperVar() && subPlus.getSubVar()){

				String term2VarName = subPlus.getSubName();
				String term1VarName = subPlus.getSuperName();

    			Iterator vit1 = typeQuery.findAllSuperClassesOfEverything().iterator();
    			
    			while(vit1.hasNext()){

					answer = answer + "\t\t<Rulebase>\n";
					answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
					answer = answer + "\t\t\t\t<Var>" + term1VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + vit1.next().toString() + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
					answer = answer + "\t\t\t\t<Var>" + term2VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + vit1.next().toString() + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t</Rulebase>";
					
					if(vit1.hasNext())
						answer = answer + "\n";	

    			}

			}	
		}else if(predicate.equalsIgnoreCase(TypeQueryParserRuleML.SUBSUMES)){
			
			sub = poslTParser.parseElementsSubsumesAndSubsumesPlus(queryTerms);
			
			//rel rel
			if(!sub.getSuperVar() && !sub.getSubVar()){
				answer = answer + "\t\t<Ind>";
				answer = answer + typeQuery.isDirectSuperClass(sub.getSuperName(),sub.getSubName());
				answer = answer + "</Ind>";
				
			//var rel get all super classes
			}else if(sub.getSuperVar() && !sub.getSubVar()){

				String term1VarName = sub.getSuperName();
				String[] superClasses = typeQuery.getDirectSuperClasses(sub.getSubName());
							
				for(int i = 0; i < superClasses.length; i++){
					answer = answer + "\t\t<Rulebase>\n";
					answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
					answer = answer + "\t\t\t\t<Var>" + term1VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + superClasses[i] + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t</Rulebase>";
					if(i != superClasses.length -1)
						answer = answer + "\n";					
				}
							
			//rel var get all sub classes
        		}else if(!sub.getSuperVar() && sub.getSubVar()){

				String term2VarName = sub.getSubName();
				String[] subClasses = typeQuery.getDirectSubClasses(sub.getSuperName());
				
				for(int i = 0; i < subClasses.length; i++){
					answer = answer + "\t\t<Rulebase>\n";
					answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
					answer = answer + "\t\t\t\t<Var>" + term2VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + subClasses[i] + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t</Rulebase>";
					if(i != subClasses.length -1)
						answer = answer + "\n";	
				}
			//var var get all relations
			}else if(sub.getSuperVar() && sub.getSubVar()){

				String term2VarName = sub.getSubName();
				String term1VarName = sub.getSuperName();

    			Iterator vit1 = typeQuery.findAllDirectSuperClassesOfEverything().iterator();
    			
    			while(vit1.hasNext()){

					answer = answer + "\t\t<Rulebase>\n";
					answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
					answer = answer + "\t\t\t\t<Var>" + term1VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + vit1.next().toString() + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
					answer = answer + "\t\t\t\t<Var>" + term2VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + vit1.next().toString() + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t</Rulebase>";
					
					if(vit1.hasNext())
						answer = answer + "\n";	
    			}
			}	
			
		}else if(predicate.equalsIgnoreCase(TypeQueryParserRuleML.LUB)){
			
			lub = poslTParser.parseElementsGLBandLUB(queryTerms);
			
			if(lub.getResultVar()){
				
				  ArrayList<String> terms = lub.getTerms();
                  
                  String[] lubArray = new String[terms.size()];

                  for(int i = 0; i < terms.size();i++)
                          lubArray[i] = terms.get(i);

                  String leastUpperBound = typeQuery.leastUpperBound(lubArray);
                  String resultVar = lub.getResultVarName();
				  answer = answer + "\t\t<Rulebase>\n";
				  answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
				  answer = answer + "\t\t\t\t<Var>" + resultVar + "</Var>\n";
				  answer = answer + "\t\t\t\t<Ind>" + leastUpperBound + "</Ind>\n";
				  answer = answer + "\t\t\t</Equal>\n";
				  answer = answer + "\t\t</Rulebase>";
                  
                  
			}
			
		}else if(predicate.equalsIgnoreCase(TypeQueryParserRuleML.GLB)){
			
			glb = poslTParser.parseElementsGLBandLUB(queryTerms);
			
			if(glb.getResultVar()){
				
				  ArrayList<String> terms = glb.getTerms();
                  
                  String[] glbArray = new String[terms.size()];

                  for(int i = 0; i < terms.size();i++)
                	  glbArray[i] = terms.get(i);

                  String greatestLowerBound = typeQuery.greatestLowerBound(glbArray);
                  String resultVar =  glb.getResultVarName();
				  answer = answer + "\t\t<Rulebase>\n";
				  answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
				  answer = answer + "\t\t\t\t<Var>" + resultVar + "</Var>\n";
				  answer = answer + "\t\t\t\t<Ind>" + greatestLowerBound + "</Ind>\n";
				  answer = answer + "\t\t\t</Equal>\n";
				  answer = answer + "\t\t</Rulebase>";

			}
			
		}

		answer += "\n\t</Answer>\n</RuleML>";
		
		return answer;
		
	}
		
}
