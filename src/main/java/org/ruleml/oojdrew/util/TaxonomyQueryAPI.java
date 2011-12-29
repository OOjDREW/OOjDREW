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

package org.ruleml.oojdrew.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.ruleml.oojdrew.SyntaxFormat;
import org.ruleml.oojdrew.parsing.POSLTypeQueryException;
import org.ruleml.oojdrew.parsing.ParseException;
import org.ruleml.oojdrew.parsing.RDFSParser;
import org.ruleml.oojdrew.parsing.RuleMLTypeQueryException;
import org.ruleml.oojdrew.parsing.SubsumesException;
import org.ruleml.oojdrew.parsing.SubsumesParser;
import org.ruleml.oojdrew.parsing.TypeQueryParserPOSL;
import org.ruleml.oojdrew.parsing.TypeQueryParserRuleML;

public class TaxonomyQueryAPI {

	private QueryTypes typeQuery;
	
    /**
     * Setup a new taxonomy
     * 
     * @param syntaxFormat
     *            The syntax format of the taxonomy (RuleML or POSL)
     * @param taxonomy
     *            The taxonomy document
     * 
     * @throws ValidityException
     * @throws ParseException
     * @throws ParsingException
     * @throws IOException
     * @throws SubsumesException
     */
    public void initializeTaxonomy(SyntaxFormat syntaxFormat, String taxonomy)
            throws ValidityException, ParseException, ParsingException, IOException,
            SubsumesException {
        Types.reset();
        
        if (syntaxFormat == SyntaxFormat.RDFS) {
            RDFSParser.parseRDFSString(taxonomy);
        } else if (syntaxFormat == SyntaxFormat.POSL) {
            SubsumesParser sp = new SubsumesParser(taxonomy);
            sp.parseSubsumes();
        }

        typeQuery = new QueryTypes();
    }
    
    /**
     * Issue a query on the taxonomy by using either a POSL or a RuleML query.
     * 
     * @see TaxonomyQueryAPI#executeQueryRuleML(String)
     * @see TaxonomyQueryAPI#executeQueryPOSL(String)
     */
    public String executeQuery(SyntaxFormat syntaxFormat, String taxonomyQuery) throws ValidityException,
            POSLTypeQueryException, ParseException, ParsingException, IOException, Exception {
        String result;
        if (syntaxFormat == SyntaxFormat.POSL) {
            result = executeQueryPOSL(taxonomyQuery);
        } else {
            result = executeQueryRuleML(taxonomyQuery);
        }
        return result;
    }

	/**
	 * This method will issue a Query on the KB using a RuleML Query.
	 *
	 * @param RuleMLTypeQuery - a RuleML query as a String.
	 * @return returns the RuleML answer expression as a String.
	 * 
	 * @throws RuleMLTypeQueryException
	 * @throws ValidityException
	 * @throws ParseException
	 * @throws ParsingException
	 * @throws IOException
	 */
    private String executeQueryRuleML(String RuleMLTypeQuery) throws RuleMLTypeQueryException, ValidityException, ParseException, ParsingException, IOException {
		
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
					answer = answer + "\t\t\t<Equal>\n";
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
					answer = answer + "\t\t\t<Equal>\n";
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
					answer = answer + "\t\t\t<Equal>\n";
					answer = answer + "\t\t\t\t<Var>" + term1VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + vit1.next().toString() + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t\t<Equal>\n";
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
					answer = answer + "\t\t\t<Equal>\n";
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
					answer = answer + "\t\t\t<Equal>\n";
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
					answer = answer + "\t\t\t<Equal>\n";
					answer = answer + "\t\t\t\t<Var>" + term1VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + vit1.next().toString() + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t\t<Equal>\n";
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
				  answer = answer + "\t\t\t<Equal>\n";
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
				  answer = answer + "\t\t\t<Equal>\n";
				  answer = answer + "\t\t\t\t<Var>" + resultVar + "</Var>\n";
				  answer = answer + "\t\t\t\t<Ind>" + greatestLowerBound + "</Ind>\n";
				  answer = answer + "\t\t\t</Equal>\n";
				  answer = answer + "\t\t</Rulebase>";

			}
			
		}

		answer += "\n\t</Answer>\n</RuleML>";
		
		return answer;
	}
	
	/**
	 * This method will issue a Query on the KB using a POSL Query.
	 *
	 * @param poslTypeQuery - a POSL query as a String.
	 * @return returns the RuleML answer expression as a String.
	 * 
	 * @throws ValidityException
	 * @throws POSLTypeQueryException
	 * @throws ParseException
	 * @throws ParsingException
	 * @throws IOException
	 * @throws Exception
	 */
	private String executeQueryPOSL(String poslTypeQuery) throws ValidityException, POSLTypeQueryException, ParseException, ParsingException, IOException, Exception{
		
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
					answer = answer + "\t\t\t<Equal>\n";
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
					answer = answer + "\t\t\t<Equal>\n";
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
					answer = answer + "\t\t\t<Equal>\n";
					answer = answer + "\t\t\t\t<Var>" + term1VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + vit1.next().toString() + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t\t<Equal>\n";
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
					answer = answer + "\t\t\t<Equal>\n";
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
					answer = answer + "\t\t\t<Equal>\n";
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
					answer = answer + "\t\t\t<Equal>\n";
					answer = answer + "\t\t\t\t<Var>" + term1VarName + "</Var>\n";
					answer = answer + "\t\t\t\t<Ind>" + vit1.next().toString() + "</Ind>\n";
					answer = answer + "\t\t\t</Equal>\n";
					answer = answer + "\t\t\t<Equal>\n";
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
				  answer = answer + "\t\t\t<Equal>\n";
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
				  answer = answer + "\t\t\t<Equal>\n";
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
