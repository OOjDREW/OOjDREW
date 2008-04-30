//package jdrew;

import javax.swing.tree.*;

import jdrew.oo.td.*;
import jdrew.oo.util.*;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

public class TopDownAllSolutions  {
    
	final static int RULEML88 = 0;
	final static int RULEML91 = 1;
	final static int POSL = 2;
	final static int RDF = 3;
	
    public static void main(String args[]) throws Exception{ 
    
    try{
    
    /**
     * Configuration Variables
     */
    //set the type of KB you are going to use RULEML88, RULEML91, POSL are supported
    int kbType = POSL;
    //set the type of query
    int queryType = POSL;
    //set how you will input your typeDefinition either RDF or POSL is supported
    int typeDefinition = RDF;

    //Store your Knowledge base file, type file and answer file here
    String kbFile = "P:\\testKB.posl";
	String typeFile = "P:\\types.rdf";
    String ansFile = "p:\\out.ans";
    
    //set to true if you want to append to the current ansFile and false to have a new
    boolean append = false;
	
    //query string in POSL
    String query = "a(?x).";
    /**
     * End of Configuration Variables
     */

    //create a BR    
    BackwardReasoner br = new BackwardReasoner();
    
    //The solution iterator contains all the solutions
    Iterator solit = null;
    
    //dc is the Definitive clause that contains the query 
    DefiniteClause dc = null;
    
    //Reset the Internal Symbol Table for predicates and types
    SymbolTable.reset();

    /*
     * Type Parsing
     */
    String contents = "";
    FileReader inFileType = new FileReader(typeFile);
    BufferedReader inType = new BufferedReader(inFileType);
    String read = "";
            
     while((read = inType.readLine()) != null)
     {
      contents = contents + read + '\n';
     }
    //store types here   
    String typestr = contents;  
    Types.reset();
    
    if(typeDefinition == RDF){
    	RDFSParser.parseRDFSString(typestr);
    }
    if(typeDefinition == POSL){
    	SubsumesParser sp = new SubsumesParser(typestr);
    	sp.parseSubsumes();
    }
 
   /*
    * KB Parsing
    */
    contents="";
    read = "";
    FileReader inFile = new FileReader(kbFile);
    BufferedReader in = new BufferedReader(inFile);
      
     while((read = in.readLine()) != null)
     {
      contents = contents + read + '\n';
     }
    in.close();                                               
    String kbstr = contents;
    
    if(kbType == POSL){
    	//create a posl parser         
        POSLParser pp = new POSLParser(); 
        //parse the knowledge base in POSL          
        pp.parseDefiniteClauses(kbstr);
        br.loadClauses(pp.iterator());
        
        //DEBUG Loop to print out all the clauses in the system
        Iterator it = pp.iterator();
        
        while (it.hasNext()) {
            DefiniteClause d = (DefiniteClause) it.next();
            //System.out.println("Loaded clause: " + d.toPOSLString());
        }    
        //DEBUG 
    }
    if(kbType == RULEML88){
    	RuleMLParser rmp = new RuleMLParser();
    	rmp.parseRuleMLString(RuleMLParser.RULEML88, kbstr);
    	br.loadClauses(rmp.iterator());
    }
    
    if(kbType == RULEML91){
    	RuleMLParser rmp = new RuleMLParser();
    	rmp.parseRuleMLString(RuleMLParser.RULEML91, kbstr);
    	br.loadClauses(rmp.iterator());
    }
    //Create a backward reasoner from the clauses
    br = new BackwardReasoner(br.clauses , br.oids);
    
    //Query parsing    
    if(queryType == POSL){
        POSLParser pp = new POSLParser();
        dc = pp.parseQueryString(query);
    }
    
    if(queryType == RULEML91){
    	RuleMLParser qp = new RuleMLParser();  
    	dc = qp.parseRuleMLQuery(query);
    }
    
    if(queryType == RULEML88){
    	RuleMLParser qp = new RuleMLParser();  
    	dc = qp.parseRuleMLQuery(query);
    }
   
    //execute the query
     solit = br.iterativeDepthFirstSolutionIterator(dc);
     String writeText ="";

    //get the results
    while(solit.hasNext()) {
            BackwardReasoner.GoalList gl = (BackwardReasoner.GoalList) solit.next();
            String goalText = gl.toStringAll();
            writeText = writeText + goalText + "\n";
            //System.out.println(gl.toStringAll());
     }
    
     FileOutputStream out;
     PrintStream print;       
     out = new FileOutputStream(ansFile,append);
     print = new PrintStream(out);                    
	 print.println(writeText);                               
	 print.close();
    
    }
    catch(Exception e){
        System.out.println(e.toString());
    }
    
        
   }
}

