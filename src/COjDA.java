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
import jdrew.oo.util.SubsumesException;
import jdrew.oo.util.TaxonomyQueryAPI;

/**
 * This class implements the Complete OO jDREW API (COjDA)
 * 
 * @author craigb
 *
 */

public class COjDA {

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
    
    
    /**
     * This COjDA constructor requires only a Knowledge base to be constructed.
     * 
     * @param profile_KB - The format the KB is in.  1 For POSL, 2 for RuleML91.
     * @param KB - This File contains the KB to be parsed.
     * 
     * @throws RecognitionException
     * @throws TokenStreamException
     * @throws IOException
     * @throws ValidityException
     * @throws ParseException
     * @throws ParsingException
     */
	COjDA(int profile_KB, File KB) throws RecognitionException, TokenStreamException, IOException, ValidityException, ParseException, ParsingException{
		
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
	
	/**
	 * This COjDA constructor requires only a Knowledge base to be constructed.
	 * 
	 * @param profile_KB - The format the KB is in.  1 For POSL, 2 for RuleML91.
	 * @param KB - This String contains the KB to be parsed.
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 * @throws ValidityException
	 * @throws ParseException
	 * @throws ParsingException
	 * @throws IOException
	 */
	COjDA(int profile_KB, String KB) throws RecognitionException, TokenStreamException, ValidityException, ParseException, ParsingException, IOException{

		if(profile_KB == POSL){

			pp.parseDefiniteClauses(KB);
			intialize_engine(pp.iterator());
			
		}else if(profile_KB == RULEML91){

            rp.parseRuleMLString(RuleMLParser.RULEML91,KB);
            intialize_engine(rp.iterator());
		}
		
	}
	
	/**
	 * This COjDA constructor requires a Knowledge base and Taxonomy to be constructed.
	 * 
	 * @param profile_KB - The format the KB is in.  1 For POSL, 2 for RuleML91.
	 * @param profile_Taxonomy - The format the Taxonomy is in.  1 For POSL, 3 for RDFS.
	 * @param KB - This String contains the KB to be parsed.
	 * @param taxonomy -This String contains the Taxonomy to be parsed.
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 * @throws ValidityException
	 * @throws ParseException
	 * @throws ParsingException
	 * @throws IOException
	 * @throws SubsumesException
	 */
	COjDA(int profile_KB, int profile_Taxonomy, String KB, String taxonomy) throws RecognitionException, TokenStreamException, ValidityException, ParseException, ParsingException, IOException, SubsumesException{
		
		noTaxonomy = false;

		if(profile_Taxonomy == POSL){
			taxonomyAPI = new TaxonomyQueryAPI(TaxonomyQueryAPI.POSL, taxonomy);
		}else if(profile_Taxonomy == RDFS){
			taxonomyAPI = new TaxonomyQueryAPI(TaxonomyQueryAPI.RDFS, taxonomy);
		}
		
		if(profile_KB == POSL){

			pp.parseDefiniteClauses(KB);
			intialize_engine(pp.iterator());
			
		}else if(profile_KB == RULEML91){

            rp.parseRuleMLString(RuleMLParser.RULEML91,KB);
            intialize_engine(rp.iterator());
		}
		
	}
	
	/**
	 * This COjDA constructor requires a Knowledge base and Taxonomy to be constructed.
	 * 
	 * @param profile_KB - The format the KB is in.  1 For POSL, 2 for RuleML91.
	 * @param profile_Taxonomy - The format the Taxonomy is in.  1 For POSL, 3 for RDFS..
	 * @param KB - This String contains the KB to be parsed.
	 * @param taxonomy -This File contains the Taxonomy to be parsed.
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 * @throws ValidityException
	 * @throws ParseException
	 * @throws ParsingException
	 * @throws IOException
	 * @throws SubsumesException
	 */
	COjDA(int profile_KB, int profile_Taxonomy, String KB, File taxonomy) throws RecognitionException, TokenStreamException, ValidityException, ParseException, ParsingException, IOException, SubsumesException{
		noTaxonomy = false;
		
		if(profile_Taxonomy == POSL){
			taxonomyAPI = new TaxonomyQueryAPI(TaxonomyQueryAPI.POSL, taxonomy);
		}else if(profile_Taxonomy == RDFS){
			taxonomyAPI = new TaxonomyQueryAPI(TaxonomyQueryAPI.RDFS, taxonomy);
		}
		
		if(profile_KB == POSL){
			pp.parseDefiniteClauses(KB);
			intialize_engine(pp.iterator());
			
		}else if(profile_KB == RULEML91){
            rp.parseRuleMLString(RuleMLParser.RULEML91,KB);
            intialize_engine(rp.iterator());
		}

	}
	/**
	 * This COjDA constructor requires a Knowledge base and Taxonomy to be constructed.
	 * 
	 * @param profile_KB - The format the KB is in.  1 For POSL, 2 for RuleML91.
	 * @param profile_Taxonomy - The format the Taxonomy is in.  1 For POSL, 3 for RDFS.
	 * @param KB - This File contains the KB to be parsed.
	 * @param taxonomy -This String contains the Taxonomy to be parsed.
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 * @throws IOException
	 * @throws ValidityException
	 * @throws ParseException
	 * @throws ParsingException
	 * @throws SubsumesException
	 */
	COjDA(int profile_KB, int profile_Taxonomy, File KB, String taxonomy) throws RecognitionException, TokenStreamException, IOException, ValidityException, ParseException, ParsingException, SubsumesException{
		noTaxonomy = false;
		
		if(profile_Taxonomy == POSL){
			taxonomyAPI = new TaxonomyQueryAPI(TaxonomyQueryAPI.POSL, taxonomy);
		}else if(profile_Taxonomy == RDFS){
			taxonomyAPI = new TaxonomyQueryAPI(TaxonomyQueryAPI.RDFS, taxonomy);
		}
		
		if(profile_KB == POSL){
			pp.parseDefiniteClauses(fileToString(KB));
			intialize_engine(pp.iterator());
		}else if(profile_KB == RULEML91){
            rp.parseRuleMLString(RuleMLParser.RULEML91,fileToString(KB));
            intialize_engine(rp.iterator());
		}

	}
	
	/**
	 * This COjDA constructor requires a Knowledge base and Taxonomy to be constructed.
	 * 
	 * @param profile_KB - The format the KB is in.  1 For POSL, 2 for RuleML91.
	 * @param profile_Taxonomy - The format the Taxonomy is in.  1 For POSL, 3 for RDFS..
	 * @param KB - This File contains the KB to be parsed.
	 * @param taxonomy - This File contains the Taxonomy to be parsed.
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 * @throws IOException
	 * @throws ValidityException
	 * @throws ParseException
	 * @throws ParsingException
	 * @throws SubsumesException
	 */
	COjDA(int profile_KB, int profile_Taxonomy, File KB, File taxonomy) throws RecognitionException, TokenStreamException, IOException, ValidityException, ParseException, ParsingException, SubsumesException{
		noTaxonomy = false;
		
		if(profile_Taxonomy == POSL){
			taxonomyAPI = new TaxonomyQueryAPI(TaxonomyQueryAPI.POSL, taxonomy);
		}else if(profile_Taxonomy == RDFS){
			taxonomyAPI = new TaxonomyQueryAPI(TaxonomyQueryAPI.RDFS, taxonomy);
		}
		
		if(profile_KB == POSL){
			pp.parseDefiniteClauses(fileToString(KB));
			intialize_engine(pp.iterator());
		}else if(profile_KB == RULEML91){
            rp.parseRuleMLString(RuleMLParser.RULEML91,fileToString(KB));
            intialize_engine(rp.iterator());
		}

	}
		
	/**
	 * This method will intialize the OO jDREW engine.
	 * @param clauses - The facts to intialize OO jDREW.
	 */
	private void intialize_engine(Iterator clauses){
		br = new BackwardReasoner();
		br.loadClauses(clauses);
		br = new BackwardReasoner(br.clauses, br.oids);
	}
	
	/**
	 * This method Converts a file to a String.
	 * 
	 * @param the File to be converted.
	 * @return the contents of the file as a string.
	 * @throws IOException
	 */
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
	
	/**
	 * This method will issue a Query on the KB using a POSL Query
	 *
	 * @param query - a POSL query as a String.
	 * @return returns the RuleML answer expression as a String.
	 * 
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 */
	public String issueKBQuery_POSL(String query) throws RecognitionException, TokenStreamException{
		
		dc = pp.parseQueryString(query);
		Iterator solit = br.iterativeDepthFirstSolutionIterator(dc);
		ArrayList<BindingPair> solutionPairs = generateBindingObjects(solit);		
		return generateRuleMLAnswerExpression(solutionPairs);
	}
	
	/**
	 * This method will issue a Query on the KB using a POSL Query
	 *
	 * @param query - a POSL query stored in a File.
	 * @return returns the RuleML answer expression as a String.
	 * 
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 * @throws IOException
	 */
	public String issueKBQuery_POSL(File query) throws RecognitionException, TokenStreamException, IOException{
		return issueKBQuery_POSL(fileToString(query));
	}
	
	/**
	 * This method will issue a Query on the KB using a RuleML Query
	 *
	 * @param query - a RuleML query as a String.
	 * @return returns the RuleML answer expression as a String.
	 * 
	 * @throws ValidityException
	 * @throws ParseException
	 * @throws ParsingException
	 * @throws IOException
	 */
	public String issueKBQuery_RuleML(String query) throws ValidityException, ParseException, ParsingException, IOException{
	
		dc = rp.parseRuleMLQuery(query);
		Iterator solit = br.iterativeDepthFirstSolutionIterator(dc);
		ArrayList<BindingPair> solutionPairs = generateBindingObjects(solit);		
		return generateRuleMLAnswerExpression(solutionPairs);
	}
	
	/**
	 * This method will issue a Query on the KB using a RuleML Query
	 *
	 * @param query - a RuleML query stored in a File.
	 * @return returns the RuleML answer expression as a String.
	 * 
	 * @throws ValidityException
	 * @throws ParseException
	 * @throws ParsingException
	 * @throws IOException
	 */
	public String issueKBQuery_RuleML(File query) throws ValidityException, ParseException, ParsingException, IOException{
		return issueKBQuery_RuleML(fileToString(query));
	}
	
	/**
	 * This method will issue a POSL query on the Taxonomy.
	 * 
	 * @param query - a POSL query as a String.
	 * @return returns the RuleML answer expression as a String.
	 * 
	 * @throws ValidityException
	 * @throws ParseException
	 * @throws ParsingException
	 * @throws IOException
	 * @throws Exception
	 */
	public String issueTaxonomyQuery_POSL(String query) throws ValidityException, ParseException, ParsingException, IOException, Exception{
		
		if(noTaxonomy)
			return "Must initialize a taxonomy before executing taxonomy queries";
		
		return taxonomyAPI.executeQueryPOSL(query);
		
	}
	
	/**
	 * This method will issue a POSL query on the Taxonomy.
	 * 
	 * @param query - a POSL query stored in a File.
	 * @return returns the RuleML answer expression as a String.
	 * 
	 * @throws ValidityException
	 * @throws ParseException
	 * @throws ParsingException
	 * @throws IOException
	 * @throws Exception
	 */
	public String issueTaxonomyQuery_POSL(File query) throws ValidityException, ParseException, ParsingException, IOException, Exception{
		return issueTaxonomyQuery_POSL(fileToString(query));
	}
	
	/**
	 * This method will issue a RuleML query on the Taxonomy.
	 * 
	 * @param query - a RuleML query as a String.
	 * @return returns the RuleML answer expression as a String.
	 * 
	 * @throws ValidityException
	 * @throws ParseException
	 * @throws ParsingException
	 * @throws IOException
	 * @throws Exception
	 */
	public String issusTaxonomyQuery_RuleML(String query) throws ValidityException, ParseException, ParsingException, IOException, Exception{
		
		if(noTaxonomy)
			return "Must initialize a taxonomy before executing taxonomy queries";
		
		return taxonomyAPI.executeQueryRuleML(query);	
	}
	
	/**
	 * This method will issue a RuleML query on the Taxonomy.
	 * 
	 * @param query - a RuleML query stored in a File.
	 * @return returns the RuleML answer expression as a String.
	 * 
	 * @throws ValidityException
	 * @throws ParseException
	 * @throws ParsingException
	 * @throws IOException
	 * @throws Exception
	 */
	public String issueTaxonomyQuery_RuleML(File query) throws ValidityException, ParseException, ParsingException, IOException, Exception{
		return issusTaxonomyQuery_RuleML(fileToString(query));
	}
	
	/**
	 * This method will generate the Binding Pairs.
	 * 
	 * @param solutions - solutions to the queries.
	 * @return - returns an array list of all the binding pairs.
	 */
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
		            
		            StringTokenizer st = new StringTokenizer(ks,":");
		            if(st.countTokens() == 2){
		            	String var = st.nextToken().trim();
		            	String type = st.nextToken().trim();
		            	ks = "<Var type=\"" + type+ "\">" + var + "</Var>";
		            }else{		            
		            	ks = "<Var>"+ ks +"</Var>";
		            }
		            BindingPair bp = new BindingPair(ks,val);
		            pairs.add(bp);		            
		        }
		 }
		
		return pairs;
	}
	
	/**
	 * This method will generate the RuleML answer expression as the solution to a query
	 * 
	 * @param solutionPairs - all the solutions to the query.
	 * @return RuleML answer expression based on the solutions given.
	 */
	private String generateRuleMLAnswerExpression(ArrayList<BindingPair> solutionPairs){
		
		String answer = "<RuleML>\n\t<Answer>\n";
		
		for(int i = 0; i < solutionPairs.size(); i++){
			
			if(i % varSize == 0)
				answer = answer + "\t\t<Rulebase>\n";

				
				answer = answer + "\t\t\t<Equal>\n";
				
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
