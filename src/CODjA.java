import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.tree.DefaultTreeModel;

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
import jdrew.oo.util.SubsumesParser;
import jdrew.oo.util.TaxonomyQueryAPI;


public class CODjA {

	int varSize = 0;
	BackwardReasoner br;
    DefiniteClause dc;
    RuleMLParser rp;
    POSLParser pp;
    RDFSParser rr;
    SubsumesParser sp;
    TaxonomyQueryAPI api;
    boolean noTaxonomy = true;
    boolean noKB = true;
    
    public static final int POSL = 1;
    public static final int RULEML91 = 2;
    public static final int RDFS = 3;
    
	CODjA(int profile_KB, File KB) throws RecognitionException, TokenStreamException, IOException, ValidityException, ParseException, ParsingException{
		
		noKB = false;
		
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
		
		noKB = false;
		
		if(profile_KB == POSL){
			
			pp = new POSLParser();
			pp.parseDefiniteClauses(KB);
			intialize_engine(pp.iterator());
			
		}else if(profile_KB == RULEML91){
			
			rp = new RuleMLParser();
            rp.parseRuleMLString(RuleMLParser.RULEML91,KB);
            intialize_engine(rp.iterator());
		}
		
	}
	
	CODjA(int profile_KB, int profile_Taxonomy, String KB, String Taxonomy){
		
	}
	
	CODjA(int profile_KB, int profile_Taxonomy, String KB, File Taxonomy){
		
	}
	
	CODjA(int profile_KB, int profile_Taxonomy, File KB, String Taxonomy){
		
	}
	
	CODjA(int profile_KB, int profile_Taxonomy, File KB, File Taxonomy){
		
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
	
	public String issueKBQuery_RuleML(String query){
	
		
		
		return null;
	}
	
	public String issueTaxonomyQuery_POSL(String query) throws ValidityException, ParseException, ParsingException, IOException, Exception{
		return api.executeQueryPOSL(query);
		
	}
	
	public String issusTaxonomyQuery_RuleML(String query) throws ValidityException, ParseException, ParsingException, IOException, Exception{
		return api.executeQueryRuleML(query);
		
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
		            BindingPair bp = new BindingPair(ks,val);
		            pairs.add(bp);		            
		        }
		 }
		
		return pairs;
	}
	
	private String generateRuleMLAnswerExpression(ArrayList<BindingPair> solutionPairs){
		
		for(int i = 0; i < solutionPairs.size(); i++){
			BindingPair pair = solutionPairs.get(i);
		}
	
		
		
		
		return null;
	}
	
	
}
