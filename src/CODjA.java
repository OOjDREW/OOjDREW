import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import jdrew.oo.td.BackwardReasoner;
import jdrew.oo.util.DefiniteClause;
import jdrew.oo.util.POSLParser;

import jdrew.oo.util.ParseException;
import jdrew.oo.util.RDFSParser;
import jdrew.oo.util.RuleMLParser;
import jdrew.oo.util.SubException;
import jdrew.oo.util.TaxonomyQueryAPI;


public class CODjA {

	int varSize = 0;
	BackwardReasoner br;
    DefiniteClause dc;
    RuleMLParser rp = new RuleMLParser();
    POSLParser pp = new POSLParser();
    RDFSParser rr;
    TaxonomyQueryAPI taxonomyAPI;
    
    boolean noTaxonomy = true;
    boolean noKB = true;
    
    public static final int POSL = 1;
    public static final int RULEML91 = 2;
    public static final int RDFS = 3;
    
	CODjA(int profile_KB, File KB) throws RecognitionException, TokenStreamException, IOException, ValidityException, ParseException, ParsingException{
		
		if(profile_KB == POSL){
				
			pp = new POSLParser();
			pp.parseDefiniteClauses(fileToString(KB));
			intialize_engine(pp.iterator());
			
		}else if(profile_KB == RULEML91){
			
			rp = new RuleMLParser();
            rp.parseRuleMLString(RuleMLParser.RULEML91, fileToString(KB));
            intialize_engine(rp.iterator());
		}
		
	}
	
	CODjA(int profile_KB, String KB) throws RecognitionException, TokenStreamException, ValidityException, ParseException, ParsingException, IOException{

		if(profile_KB == POSL){

			pp.parseDefiniteClauses(KB);
			intialize_engine(pp.iterator());
			
		}else if(profile_KB == RULEML91){

            rp.parseRuleMLString(RuleMLParser.RULEML91,KB);
            intialize_engine(rp.iterator());
		}
		
	}
	
	CODjA(int profile_KB, int profile_Taxonomy, String KB, String taxonomy) throws RecognitionException, TokenStreamException, ValidityException, ParseException, ParsingException, IOException, SubException{
		
		noTaxonomy = false;

		if(profile_KB == POSL){

			pp.parseDefiniteClauses(KB);
			intialize_engine(pp.iterator());
			
		}else if(profile_KB == RULEML91){

            rp.parseRuleMLString(RuleMLParser.RULEML91,KB);
            intialize_engine(rp.iterator());
		}
		
		if(profile_Taxonomy == POSL){
			taxonomyAPI = new TaxonomyQueryAPI(TaxonomyQueryAPI.POSL, taxonomy);
		}else if(profile_Taxonomy == RDFS){
			taxonomyAPI = new TaxonomyQueryAPI(TaxonomyQueryAPI.RDFS, taxonomy);
		}
		
	}
	
	CODjA(int profile_KB, int profile_Taxonomy, String KB, File taxonomy) throws RecognitionException, TokenStreamException, ValidityException, ParseException, ParsingException, IOException, SubException{
		noTaxonomy = false;

		if(profile_KB == POSL){

			pp.parseDefiniteClauses(KB);
			intialize_engine(pp.iterator());
			
		}else if(profile_KB == RULEML91){

            rp.parseRuleMLString(RuleMLParser.RULEML91,KB);
            intialize_engine(rp.iterator());
		}
		if(profile_Taxonomy == POSL){
			taxonomyAPI = new TaxonomyQueryAPI(TaxonomyQueryAPI.POSL, taxonomy);
		}else if(profile_Taxonomy == RDFS){
			taxonomyAPI = new TaxonomyQueryAPI(TaxonomyQueryAPI.RDFS, taxonomy);
		}
	}
	
	CODjA(int profile_KB, int profile_Taxonomy, File KB, String taxonomy) throws RecognitionException, TokenStreamException, IOException, ValidityException, ParseException, ParsingException, SubException{
		noTaxonomy = false;

		if(profile_KB == POSL){

			pp.parseDefiniteClauses(fileToString(KB));
			intialize_engine(pp.iterator());
			
		}else if(profile_KB == RULEML91){

            rp.parseRuleMLString(RuleMLParser.RULEML91,fileToString(KB));
            intialize_engine(rp.iterator());
		}
		if(profile_Taxonomy == POSL){
			taxonomyAPI = new TaxonomyQueryAPI(TaxonomyQueryAPI.POSL, taxonomy);
		}else if(profile_Taxonomy == RDFS){
			taxonomyAPI = new TaxonomyQueryAPI(TaxonomyQueryAPI.RDFS, taxonomy);
		}
	}
	
	CODjA(int profile_KB, int profile_Taxonomy, File KB, File taxonomy) throws RecognitionException, TokenStreamException, IOException, ValidityException, ParseException, ParsingException, SubException{
		noTaxonomy = false;

		if(profile_KB == POSL){

			pp.parseDefiniteClauses(fileToString(KB));
			intialize_engine(pp.iterator());
			
		}else if(profile_KB == RULEML91){

            rp.parseRuleMLString(RuleMLParser.RULEML91,fileToString(KB));
            intialize_engine(rp.iterator());
		}
		if(profile_Taxonomy == POSL){
			taxonomyAPI = new TaxonomyQueryAPI(TaxonomyQueryAPI.POSL, taxonomy);
		}else if(profile_Taxonomy == RDFS){
			taxonomyAPI = new TaxonomyQueryAPI(TaxonomyQueryAPI.RDFS, taxonomy);
		}
	}
		
	
	private void intialize_engine(Iterator clauses){
		br = new BackwardReasoner();
		br.loadClauses(clauses);
		br = new BackwardReasoner(br.clauses, br.oids);
	}
	
	private String fileToString(File file) throws IOException{
		
		 FileReader inFile = new FileReader(file);
         BufferedReader in = new BufferedReader(inFile);
         String read ="";
         String contents="";
         
         while((read = in.readLine()) != null)
         {
                 contents = contents + read + '\n';
         }
         in.close();
         
         return contents;			
	}
	
	public String issueKBQuery_POSL(String query) throws RecognitionException, TokenStreamException{
		
		dc = pp.parseQueryString(query);
		Iterator solit = br.iterativeDepthFirstSolutionIterator(dc);
		ArrayList<BindingPair> solutionPairs = generateBindingObjects(solit);		
		return generateRuleMLAnswerExpression(solutionPairs);
	}
	
	public String issueKBQuery_POSL(File query) throws RecognitionException, TokenStreamException, IOException{
		return issueKBQuery_POSL(fileToString(query));
	}
	
	public String issueKBQuery_RuleML(String query) throws ValidityException, ParseException, ParsingException, IOException{
	
		dc = rp.parseRuleMLQuery(query);
		Iterator solit = br.iterativeDepthFirstSolutionIterator(dc);
		ArrayList<BindingPair> solutionPairs = generateBindingObjects(solit);		
		return generateRuleMLAnswerExpression(solutionPairs);
	}
	
	public String issueKBQuery_RuleML(File query) throws ValidityException, ParseException, ParsingException, IOException{
		return issueKBQuery_RuleML(fileToString(query));
	}
		
	public String issueTaxonomyQuery_POSL(String query) throws ValidityException, ParseException, ParsingException, IOException, Exception{
		
		if(noTaxonomy)
			return "Must initialize a taxonomy before executing taxonomy queries";
		
		return taxonomyAPI.executeQueryPOSL(query);
		
	}
	
	public String issueTaxonomyQuery_POSL(File query) throws ValidityException, ParseException, ParsingException, IOException, Exception{
		return issueTaxonomyQuery_POSL(fileToString(query));
	}
		
	public String issusTaxonomyQuery_RuleML(String query) throws ValidityException, ParseException, ParsingException, IOException, Exception{
		
		if(noTaxonomy)
			return "Must initialize a taxonomy before executing taxonomy queries";
		
		return taxonomyAPI.executeQueryRuleML(query);	
	}
	
	public String issueTaxonomyQuery_RuleML(File query) throws ValidityException, ParseException, ParsingException, IOException, Exception{
		return issusTaxonomyQuery_RuleML(fileToString(query));
	}
	
	private ArrayList<BindingPair> generateBindingObjects(Iterator solutions){

		ArrayList<BindingPair> pairs = new ArrayList<BindingPair>();
		
		 while(solutions.hasNext()) {
		    	
		        BackwardReasoner.GoalList gl = (BackwardReasoner.GoalList) solutions.next();
		        Hashtable varbind = gl.varBindings;
		        varSize = varbind.size();
		        Enumeration e = varbind.keys();
		        while (e.hasMoreElements()) {
		            Object k = e.nextElement();
		            String val = (String)varbind.get(k);
		            String ks = (String) k;
		            ks = ks.substring(1);
		            ks = "<Var>"+ ks +"</Var>";
		            BindingPair bp = new BindingPair(ks,val);
		            pairs.add(bp);		            
		        }
		 }
		
		return pairs;
	}
	
	private String generateRuleMLAnswerExpression(ArrayList<BindingPair> solutionPairs){
		
		String answer = "<RuleML>\n\t<Answer>\n";
		
		for(int i = 0; i < solutionPairs.size(); i++){
			
			if(i % varSize == 0)
				answer = answer + "\t\t<Rulebase>\n";

				
				answer = answer + "\t\t\t<Equal oriented=\"yes\">\n";
				
				BindingPair pair = solutionPairs.get(i);
				answer = answer + "\t\t\t\t" + pair.getVariable() + "\n";

				StringTokenizer st = new StringTokenizer(pair.getValue(),"\n");
				
				while(st.hasMoreTokens()){
					
					answer = answer + "\t\t\t\t" + st.nextToken() + "\n";
				}
							
				
				answer = answer + "\t\t\t</Equal>";
			
			if(i % varSize == varSize-1)										
				answer = answer + "\n\t\t</Rulebase>";
		
			if(!(i == solutionPairs.size()-1)){
				answer = answer + "\n";
			}
		}
	
		answer += "\n\t</Answer>\n</RuleML>";
		
		return answer;
	}
	
	
}
