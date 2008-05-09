//package jdrew.oo.SOjDA;

import javax.swing.JOptionPane;
import javax.swing.tree.*;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

import jdrew.oo.td.*;
import jdrew.oo.td.BackwardReasoner.GoalList;
import jdrew.oo.util.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

import java.util.*;
import java.io.*;


public class OOjDREWAPI {
	
	public BackwardReasoner br;
//	The solution iterator contains all the solutions
    public Iterator solit;
    Iterator parsedClauses;
    DefiniteClause dc;
    RuleMLParser rp;
    POSLParser pp;
    RDFSParser rr;
    SubsumesParser sp;
    
    /**
     * Creates an instance of the TDEngine class that can then be used to resolve queries.
     * 
     * This instance will be initialized without a taxonomy.
     *  
     * The mode argument can specify both the format of the knowledgebase (RuleML91, RuleML88, POSL),
     * as well as the method of resolving the knowledge base (String, local file, web source).
     * The kbstr argument is either the resolved knowledgebase in string form, or a resource indicator (local file or web source) which can be used to resolve the knowledgebase.     
     * @param  mode the creation mode of the knowledge base, one of any constants found in the package jdrew.oo.globals
     * @param  kbstr either the knowledge base, or a resource indicator pointing to the location of the knowledge base 
     * @return     instance of class TDEngine
     */

    public OOjDREWAPI(int mode, String kbstr){
    	this.initializeBR();
    	this.makeEngine(mode, kbstr);
    }
    
    /**
     * Creates an instance of the TDEngine class that can then be used to resolve queries. 
     * The mode argument can specify both the format of the knowledgebase (RuleML91, RuleML88, POSL),
     * as well as the method of resolving the knowledge base (String, local file, web source).
     * The kbstr argument is either the resolved knowledgebase in string form, or a resource indicator (local file or web source) which can be used to resolve the knowledgebase.     
     * The typestr argument is either the resolved taxonomy in String form, or a resource indicator (local file or web source) which can be used to resolve the taxonomy.
     * @param  mode the creation mode of the knowledge base, one of any constants found in the package jdrew.oo.globals
     * @param  kbstr either the knowledge base, or a resource indicator pointing to the location of the knowledge base 
     * @return     instance of class TDEngine
     */
    public OOjDREWAPI(int mode, String typestr, String kbstr){

    	 this.initializeBR();	
    	 try{
         this.setupTaxonomy_RDF(typestr);
    	 }
    	 catch(Exception e){e.printStackTrace();}

		this.makeEngine(mode, kbstr);
    }
    
    
    /**
     * Creates an instance of the TDEngine class that can then be used to resolve queries. 
     * When using this constructor, the most current version of RuleML is used as the default mode.
     * As well, the knowledgebase is assumed to already be in string format.
     * The kbstr argument is either the resolved knowledgebase in string form, or a resource indicator (local file or web source) which can be used to resolve the knowledgebase.     
     * @param  kbstr the knowledge base as a RuleML/XML String 
     * @return     instance of class TDEngine
     */
    public OOjDREWAPI(String kbstr){
    	
    	this.initializeBR();
    	this.makeEngine(Globals.CURRENTRULEML, kbstr);
    }
    
    /**
     * Creates an instance of the TDEngine class that can then be used to resolve queries. 
     * When using this constructor, the most current version of RuleML is used as the default format for the knowledgebase,
     * and the taxonomy is assumed to be given in RDFS/XML 
     * The kbstr argument is either the resolved knowledgebase in string form, or a resource indicator (local file or web source) which can be used to resolve the knowledgebase.     
     * The typestr argument is either the resolved taxonomy in String form, or a resource indicator (local file or web source) which can be used to resolve the taxonomy.
     * @param  typestr the taxonomy (type system) as a RDFS/XML String  
     * @param  kbstr the knowledge base as a RuleML/XML String 
     * @return     instance of class TDEngine
     */
    public OOjDREWAPI(String typestr, String kbstr){

		   this.initializeBR();
		   
		try{
		   this.setupTaxonomy_RDF(typestr);
		   
		}
		
		catch(Exception e){e.printStackTrace();}

		   this.makeEngine(Globals.CURRENTRULEML, kbstr);
	}
	
	
		
	
    /*
     * Constructor for using Top Down OO jDREW without a taxonomy 
     *  with the knowledge base provided as a file
     *  @returns TDEngine
     */
    public OOjDREWAPI(int mode, File kb){
    	
    
    String kbstr = this.fileToString(kb);
    
    System.out.println(kbstr);
    
    
    this.initializeBR();
    
    this.makeEngine(mode, kbstr);

    }

    /*
     * Constructor for using Top Down OO jDREW with a taxonomy 
     *  knowledge base provided as a file
     *  taxonomy provided as a String
     */
    public OOjDREWAPI(int mode, String typestr, File kb){

    	this.initializeBR();
    	
    	try{
        this.setupTaxonomy_RDF(typestr);
    	}
    	catch(Exception e){e.printStackTrace();}
    	
    	
    	this.makeEngine(mode, this.fileToString(kb));
	
    }
    
    /*
     * Constructor for using Top Down OO jDREW with a taxonomy 
     *  knowledge base provided as a file
     *  taxonomy provided as a String
     */
    public OOjDREWAPI(int mode, File types, File kb){
    	this.initializeBR();
    	try{
    		this.setupTaxonomy_RDF(types);
    	}
    	catch(Exception e){e.printStackTrace();}
    	System.out.println("No problem with taxonomy");
    	
	    
    this.makeEngine(mode, kb);

    }
	
    /**
     * Creates an instance of the TDEngine class that can then be used to resolve queries. 
     * When using this constructor, the most current version of RuleML is used as the default format for the knowledgebase,
     * and the taxonomy is assumed to be given in RDFS/XML 
     * The kbstr argument is either the resolved knowledgebase in string form, or a resource indicator (local file or web source) which can be used to resolve the knowledgebase.     
     * The typestr argument is either the resolved taxonomy in String form, or a resource indicator (local file or web source) which can be used to resolve the taxonomy.
     * @param  mode a code which specifies how knowledge base and taxonomy are to be resolved, codes can be found in the package jdrew.oo.globals
     * @param  typestr the taxonomy (type system) as a File, with the format specified by mode   
     * @param  kbstr the knowledge base as a RuleML/XML String 
     * @return     instance of class TDEngine
     */
    public OOjDREWAPI(int mode, File types, String kbstr){
    	
    	this.initializeBR();
    	try{
    	this.setupTaxonomy_RDF(types);
    	}
    	catch(Exception e){}
    	
    	System.out.println("No problem with taxonomy");
    	
    	this.makeEngine(mode, kbstr);
    	
    	
    }
    
    private void initializeBR(){
		try{
		
		   //create a BR	
		this.br = new BackwardReasoner();
		
		//The solution iterator contains all the solutions
	    this.solit = null;
	    
	    //dc is the Definitive clause that contians the query
	    this.dc = null;
	    
	    //Reset the Internal Symbol Table for predicates and types
	    SymbolTable.reset();
	    
	    //reset the type information
	    Types.reset();
		}
	    catch(Exception e){
	    	
	    System.out.println("Problem initializing Backward Reasoner: " + e.getMessage());

	    	}
	    }
    
	public void setupKnowledgebase_RuleML(int mode, String kbstr){
		
		 SymbolTable.reset();
		   //create a RuleML parser         
	    this.rp = new RuleMLParser();
	    //parse the knowledge base in RuleML   
	    try{
	    
	    	
	    File kb = new File("Output/RuleMLPopulatedOntology.ruleml")	;
	//    File kb = new File("rxmlInput.xml")	;	
	    this.rp.parseFile(2, kb);
    	
	    	
	//   this.rp.parseRuleMLString(mode,kbstr);
	   
	    }
	    catch(Exception e){
	    	System.out.println("Error Parsing knowledge base");
	    	e.printStackTrace();
	    	
	    }
	    
	    //load the clauses from the parser into the backward reasoner 
		this.br.loadClauses(rp.iterator());
		
		parsedClauses = rp.iterator();
		
		//DEBUGGING loop to print out all the clauses currently in the system
	    int temp = 0;
	    while (parsedClauses.hasNext()) {
	    	
	    	Object x = parsedClauses.next();
	    	temp++;
	    	
	    	//DefiniteClause d = (DefiniteClause) parsedClauses.next();
	    	
	    //	System.out.println("Loaded clause: " + d.toRuleMLString(2));
	    }	
	    System.out.println("Parsed clauses: " + temp);
	    //DEBUG 
		
		
	}

	public void setupKnowledgebase_RuleML(int mode, File kbfile){
		
		SymbolTable.reset();
		   //create a RuleML parser         
	    this.rp = new RuleMLParser();
	    //parse the knowledge base in RuleML   
	    try{
	
	 //   File kb = new File("Output/RuleMLPopulatedOntology.ruleml")	;
	//    File kb = new File("rxmlInput.xml")	;	
	    this.rp.parseFile(2, kbfile);

	//   this.rp.parseRuleMLString(mode,kbstr);
	   
	    }
	    catch(Exception e){
	    	System.out.println("Error Parsing knowledge base");
	    	e.printStackTrace();	
	    }
	    
	    //load the clauses from the parser into the backward reasoner 
		this.br.loadClauses(rp.iterator());
		parsedClauses = rp.iterator();
		//DEBUGGING loop to print out all the clauses currently in the system
	    while (parsedClauses.hasNext()) {
	    	DefiniteClause d = (DefiniteClause) parsedClauses.next();
	    	//System.out.println("Loaded clause: " + d.toRuleMLString(2));
	    }	
	    //DEBUG 	
	}
	
	public void setupTaxonomy_POSL(String typestr){	
		Types.reset();
		this.sp = new SubsumesParser(typestr);
		try {
			sp.parseSubsumes();
		} catch (Exception ex) {
		
			if(ex.getMessage() == null){
    			System.out.println("Invalid POSL Format");
			}
			else{	
				System.out.println(ex.getMessage());
			}
		}
		
	}


	public void setupTaxonomy_RDF (String typestr)throws Exception{
		    //parse types
		   RDFSParser.parseRDFSString(typestr);   
	}
	
	public void setupTaxonomy_RDF(File types) throws Exception{

		   RDFSParser.parseRDFSFile(types);   			   
	}

	public void setupKnowledgebase_POSL(int mode, String kbstr){
		
		SymbolTable.reset();
		   //create a POSL parser         
	    this.pp = new POSLParser();
	    //parse the knowledge base in POSL   
	    try{
	    this.pp.parseDefiniteClauses(kbstr);
	    
	    }
	    catch(Exception e){
	    	System.out.println("Error Parsing knowledge base");
	    	e.printStackTrace();
	    	
	    }
	    
	    //load the clauses from the parser into the backward reasoner 
		this.br.loadClauses(pp.iterator());
	
		
		//DEBUG Loop to print out all the clauses in the system
	    Iterator it = pp.iterator();
	    
	    while (it.hasNext()) {
	    //	DefiniteClause d = (DefiniteClause) it.next();
	    // 	System.out.println("Loaded clause: " + d.toRuleMLString(2));
	    }	
	    //DEBUG 
	}
	
	public void makeEngine(int mode, String kbstr){
		try{ 	   	   
			 if(mode == Globals.POSL){ 
				 this.setupKnowledgebase_POSL(mode,kbstr);
			 }
			 else if((mode >= Globals.RULEML88) && (mode <= Globals.CURRENTRULEML)){
				 this.setupKnowledgebase_RuleML(mode,kbstr);
			 }
			 else{
				 System.out.println("Error setting up knowledge base");   
			}
			   
			//Create a backward reasoner from the clauses
			br = new BackwardReasoner(br.clauses, br.oids);
	
			}
		catch(Exception e){
			System.out.println(e.toString());
		 //out.flush();
		}
	}
	
		public void makeEngine(int mode, File kb){

		try{ 	   	   
			   
			  if(mode == Globals.POSL){ 
				  this.setupKnowledgebase_POSL(mode,fileToString(kb));
			  }			   
			  
			  else if((mode >= Globals.RULEML88) && (mode <= Globals.CURRENTRULEML)){
			  	this.setupKnowledgebase_RuleML(mode,kb); 
			  }
			  else{
				System.out.println("Error setting up knowledge base");   
			  }
			 //Create a backward reasoner from the clauses
			 br = new BackwardReasoner(br.clauses, br.oids);

			}
			catch(Exception e){
				System.out.println(e.toString());
				//out.flush();
			}
		
	}
	
	
	public DefaultTreeModel getDefaultTreeModel(){
		
		DefaultMutableTreeNode root = br.toTree();
        root.setAllowsChildren(true);
        DefaultTreeModel dtm = new DefaultTreeModel(root);
		return dtm;
	}
	
	public String fileToString(File kb) {
		String kbstr = "";

		try {

			FileReader inFile = new FileReader(kb);
			BufferedReader in = new BufferedReader(inFile);
			String read = "";

			System.out.println("Reading file");
			// while((read = in.readLine()) != null)
			// {
			// kbstr = kbstr + read + '\n';
			//                   
			// }
			// in.close();

		} catch (Exception e) {
			// System.out.println(e.toString());
			return null;
		}

		return kbstr;
	}
	
	public String appendQuery(String qstr) throws Exception{
		
	      	
    		Builder bl = new Builder();
   			StringReader sr = new StringReader(qstr);
  			Document doc = bl.build(sr);

  			Element root = doc.getRootElement();

   			Element atom = root.getFirstChildElement("Atom");

   			Element atom2 = new Element(atom);
            
			Element query = new Element("Query");
			Document queryDoc = new Document(query);

    		Element implies = new Element("Implies");

    		Attribute a1 = new Attribute("mapClosure", "universal");
    		implies.addAttribute(a1);
    		query.appendChild(implies);
    
    		implies.appendChild(atom2);
    
    		Element top = new Element("Atom");
           
    		Element rel = new Element("Rel");
    
    		rel.insertChild("$top",0);     
    	           
    		top.appendChild(rel);
    
    		implies.appendChild(top);
                   
			String d = queryDoc.toXML();
			
			String s1 = d.substring(23);
	
			System.out.println("S1: " + s1);

   			return s1;

	} 
	
	public Document issueQuery(int mode, File query){
		Document answer = null;
		
		try{	
			String queryString = this.fileToString(query);
			answer = this.issueQuery(mode, queryString);
		
		}catch(Exception e){
			System.out.println("Error issuing query");
			e.printStackTrace();
		}
		
		return answer;
	}
	
	private String openWebSource(String url) throws Exception{
		
		String contents = "";
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);
		method.setFollowRedirects( true );
		
		int statusCode = client.executeMethod(method);
		if( statusCode != -1 ) {
		 		contents = method.getResponseBodyAsString();
		 		method.releaseConnection();}
		
		return contents;
	}

	private Document issueQuery_POSL(String query) throws Exception{
		
		String result = new String();
		this.pp = new POSLParser();
		
		System.out.println("parse query string");
		
		this.dc = pp.parseQueryString(query);
		
		//System.out.println("DC" + dc.toRuleMLString(2));
		//System.out.println("create iterative DFSI");
		solit = br.iterativeDepthFirstSolutionIterator(dc);

		Vector data = new Vector();
		  int varSize;
  		  System.out.println("POSL Query");
		  
		  if (!solit.hasNext()) {
	           javax.swing.tree.DefaultMutableTreeNode root = new
	                  DefaultMutableTreeNode("No Solutions");
	            javax.swing.tree.DefaultTreeModel dtm = new DefaultTreeModel(root);


	        } else {
	            BackwardReasoner.GoalList gl = (BackwardReasoner.GoalList) solit.
	                                           next();
	            System.out.println(gl.toString());
	            Hashtable varbind = gl.varBindings;
	            javax.swing.tree.DefaultMutableTreeNode root = br.toTree();
	            root.setAllowsChildren(true);

	           javax.swing.tree.DefaultTreeModel dtm = new DefaultTreeModel(root);

	 
	            int i = 0;
	            Object[][] rowdata = new Object[varbind.size()][2];
	            Enumeration e = varbind.keys();
	            while (e.hasMoreElements()) {
	                Object k = e.nextElement();
	                Object val = varbind.get(k);
	                String ks = (String) k;
	                rowdata[i][0] = ks;
	                rowdata[i][1] = val;
	                System.out.println("val: " + val);
	                result = result + val;
	                i++;         
	            	}
	            }
		    Builder bl = new Builder();
		    StringReader sr = new StringReader("<RuleML>" + result + "</RuleML>");
		    Document resultDoc = bl.build(sr);
						
			return resultDoc;

	        }
	
	public Document issueQuery(int mode, String query) throws Exception{
		
		
		String result = new String();
		String combinedResults = new String();
		Vector data = new Vector();
		
		int queryMode = mode / 10;

//		System.out.println("This is the mode code modded to remove the query basis: " + mode%Globals.QUERYBASIS);
//		System.out.println("This is the mode code modded to remove the query basis: " + mode%Globals.TAXONOMYSOURCEBASIS);
//		System.out.println("This is the mode code modded to remove the query basis: " + mode%Globals.KBSOURCEBASIS);
		
		if(mode%10 == Globals.POSL){
			return this.issueQuery_POSL(query);
		}
		else{
		
			if(queryMode == 1){query = this.appendQuery(query);}
				return this.issueQuery_RuleML91(query);
		}
			
	}
	
	public Iterator getSolutionIterator(DefiniteClause dc){
		Iterator solit = br.iterativeDepthFirstSolutionIterator(dc);
		return solit;
	}
		
	private Document issueQuery_RuleML91(String query) throws Exception{
		
		String result = new String();
		String combinedResults = new String();
		Vector data = new Vector();
		
		// must initialize rp in order to actually use it if the kb was parsed in POSL
		this.rp = new RuleMLParser();
		
		System.out.println("Query In:" + query );

		try{

			System.out.println("Before Parse Query");
			this.dc = rp.parseRuleMLQuery(query);	    
			System.out.println("After Parse Query");
			System.out.println("Get Solution Iterator");	    
	    	this.solit = this.getSolutionIterator(dc);

	    	int varSize;
	    	//System.out.println("While Solution Iterator Has Solutions");	        
	        while(solit.hasNext()) {
	            //System.out.println("Get next goal list.");
	            BackwardReasoner.GoalList gl = (BackwardReasoner.GoalList) solit.next();
	            //System.out.println("Make hash table.");
	            Hashtable varbind = gl.varBindings;
	            //javax.swing.tree.DefaultMutableTreeNode root = br.toTree();
	            //root.setAllowsChildren(true);
	            //javax.swing.tree.DefaultTreeModel dtm = new DefaultTreeModel(root);
				
	            int i = 0;
	            Object[][] rowdata = new Object[varbind.size()][2];
	            varSize = varbind.size();
	            
	            Enumeration e = varbind.keys();
	            
	            while (e.hasMoreElements()) {
	                Object k = e.nextElement();
	                Object val = varbind.get(k);
	                
	                String ks = (String) k;
	                String vals = (String) val;
	                rowdata[i][0] = ks;
	                rowdata[i][1] = vals;
         
	                // System.out.println("Before Check for Slot: Vals:" + vals +"   "+ ks);
	                //System.out.println("Enter if statement.");            
	                if(vals.contains("<slot>") && !vals.contains("<resl") &&!vals.contains("<Plex>")){
	                	Builder bl = new Builder();
	            	    StringReader sr = new StringReader(vals);
	            	    //System.out.println("Build inDoc.");
	                    Document inDoc = bl.build(sr);
	                    //System.out.println("Done Build inDoc.");
	                    //System.out.println("Get root of inDoc.");
	                    Element root = inDoc.getRootElement();
	                    //System.out.println("Done get root of inDoc.");
	                    //This is the section of code that is giving me problems with Plexes         	    
	            	    Element ind = root.getFirstChildElement("Ind");
	            	    ind.detach();
	            	    ind = root.getFirstChildElement("Ind");
	            	    ind.detach();
	            	    Document resultDoc = new Document(ind);
	            	    vals = resultDoc.toXML();
	            	    String s1 = vals.substring(23);
	            	    vals = s1;
	        			
	            	    //System.out.println("SLOTS!!\n" + vals.substring(23));
	                	
	                }
	                //System.out.println("Exit if statement.");
	                //System.out.println("After Check For Slot");
	                //System.out.println("KS: " + ks + "\nVals: " +vals);

	                if(vals.contains("<slot>") && vals.contains("<resl") && ks.contains("$Anon")){
	
	                }
	                else{
	                	result = result + "\n" + "    <Equal>" + "\n      <Var>" + ks.trim() + "</Var>" +"\n      " + vals.trim()+"\n    </Equal>";
	               //}
	               }
	                i++;
	            }//while
	            
	            combinedResults = combinedResults + "\n<Answer>\n<Rulebase>" + result + "\n</Rulebase>\n</Answer>";
	            result = "";
	            data.addElement(rowdata);
	        }//end solit hasNext                 
	    }
	    catch(Exception e){
	    	 System.out.println(e.toString());
			 //out.flush();	 
	    }
	    //System.out.println("Combined Results: " + combinedResults);
	    
	    Builder bl = new Builder();
	    StringReader sr = new StringReader("<RuleML>" + combinedResults + "</RuleML>");
	    Document resultDoc = bl.build(sr);
	
		return resultDoc;
	}

	public Document issueQuery(String query) throws Exception{
			
			String result = new String();
			String combinedResults = new String();
			Vector data = new Vector();
			
			// must initialize rp in order to actually use it if the kb was parsed in POSL
			this.rp = new RuleMLParser();
			
			System.out.println("Query In:" + query );

			String query1 = "<Query><Implies mapClosure=\"universal\"><And><Atom><Rel>test</Rel><Var>M</Var></Atom><Atom><Rel>test</Rel><Ind>x</Ind></Atom></And><Atom><Rel>top$</Rel></Atom></Implies></Query>";
			String query2 ="<Query><Implies mapClosure=\"universal\"><Atom><Rel>studiesIn</Rel><slot><Ind>university</Ind><Ind>UNB</Ind></slot><slot><Ind>student</Ind><Var>y</Var></slot></Atom><Atom><Rel>top$</Rel></Atom></Implies></Query>";
			String query3 ="<Query><Implies mapClosure=\"universal\"><Atom><Rel>studiesIn</Rel><slot><Ind>university</Ind><Var>?School</Var></slot> <resl><Var>OtherFacts</Var></resl></Atom><Atom><Rel>top$</Rel></Atom></Implies></Query>";
			String query4 ="<Query><Implies mapClosure=\"universal\"><And><Atom><Rel>studiesIn</Rel><slot><Ind>university</Ind><Var>?School</Var></slot><resl/><Var>OtherFacts</Var></resl></Atom><Atom><Rel>school</Rel><slot><Ind>name</Ind><Var>?School</Var></slot><resl><Var>?Y</Var></resl></Atom></And><Atom><Rel>top$</Rel></Atom></Implies></Query>";

			try{
				System.out.println("Before Parse Query");
				this.dc = rp.parseRuleMLQuery(query);	    
				System.out.println("After Parse Query");
				Iterator solit = this.getSolutionIterator(dc);
				int varSize;
		        
		        while(solit.hasNext()) {
	
		            BackwardReasoner.GoalList gl = (BackwardReasoner.GoalList) solit.next();    
		            Hashtable varbind = gl.varBindings;
		            //javax.swing.tree.DefaultMutableTreeNode root = br.toTree();
		            //root.setAllowsChildren(true);
		            //javax.swing.tree.DefaultTreeModel dtm = new DefaultTreeModel(root);
		            int i = 0;
		            Object[][] rowdata = new Object[varbind.size()][2];
		            varSize = varbind.size();
		            
		            Enumeration e = varbind.keys();
		            
		            while (e.hasMoreElements()) {
		                Object k = e.nextElement();
		                Object val = varbind.get(k);
		                
		                String ks = (String) k;
		                String vals = (String) val;
		                rowdata[i][0] = ks;
		                rowdata[i][1] = val;

		                System.out.println("Before Check for Slot");
		                if(vals.contains("<slot>") && !vals.contains("<resl")){
		                	Builder bl = new Builder();
		            	    StringReader sr = new StringReader(vals);
		            	    Document inDoc = bl.build(sr);
		            	    Element root = inDoc.getRootElement();
		            	    Element ind = root.getFirstChildElement("Ind");
		            	    ind.detach();
		            	    ind = root.getFirstChildElement("Ind");
		            	    ind.detach();
		            	    Document resultDoc = new Document(ind);
		            	    vals = resultDoc.toXML();
		            	    String s1 = vals.substring(23);
		            	    vals = s1;
		            	    //System.out.println("SLOTS!!\n" + vals.substring(23));
		                }
		                System.out.println("After Check For Slot");
		                //System.out.println("KS: " + ks + "\nVals: " +vals);
		                result = result + "\n" + "    <Equal>" + "\n      <Var>" + ks.trim() + "</Var>" +"\n      " + vals.trim()+"\n    </Equal>";
		                //}
		                i++;
		            }
		            //System.out.println("Result: " +  result);
		            combinedResults = combinedResults + "\n<Answer>\n<Rulebase>" + result + "\n</Rulebase>\n</Answer>";
		            result = "";
		            data.addElement(rowdata);
		        }                 
		    }
		    catch(Exception e){
		    	 System.out.println(e.toString());
				 //out.flush();	 
		    }
		    //System.out.println("Combined Results: " + combinedResults);
		    
		    Builder bl = new Builder();
		    StringReader sr = new StringReader("<RuleML>" + combinedResults + "</RuleML>");
		    Document resultDoc = bl.build(sr);
		    return resultDoc;
		}

}



