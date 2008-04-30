
import javax.swing.tree.*;

import jdrew.oo.td.*;
import jdrew.oo.util.*;

import java.io.InputStream;
import java.util.*;

public class Template  {
	
	public static void main(String args[]) throws Exception{
	
	try{

    //create a BR	
	BackwardReasoner br = new BackwardReasoner();
	
	//The solution iterator contains all the solutions
    Iterator solit = null;
    
    //dc is the Definitive clause that contians the query
    DefiniteClause dc = null;
    
    //Reset the Internal Symbol Table for predicates and types
    SymbolTable.reset();
    
    //create a posl parser         
    POSLParser pp = new POSLParser();
    
    //Store your KB here in POSL
    String kbstr = "test(a). \n test(b).";
   
    //store types here   
    String typestr = "";  
   
    //reset the type information
    Types.reset();
    
    //parse types in RDF
    RDFSParser.parseRDFSString(typestr);
    
    //parse the knowledge base in POSL          
    pp.parseDefiniteClauses(kbstr);

    //load the clauses from the parser into the backward reasoner 
	br.loadClauses(pp.iterator());
	
	//DEBUG Loop to print out all the clauses in the system
    Iterator it = pp.iterator();
    
    while (it.hasNext()) {
    	DefiniteClause d = (DefiniteClause) it.next();
    	//System.out.println("Loaded clause: " + d.toPOSLString());
    }	
    //DEBUG 
    
    //Create a backward reasoner from the clauses
	br = new BackwardReasoner(br.clauses, br.oids);
	 
	//query string
    String query = "test(?x)";
    
    //parse the query
    dc = pp.parseQueryString(query);
    
    //execute the query
 	solit = br.iterativeDepthFirstSolutionIterator(dc);
   
 	//display the results
 	Vector data = new Vector();
    int varSize = 0;
        
        while(solit.hasNext()) {

            BackwardReasoner.GoalList gl = (BackwardReasoner.GoalList) solit.
                                           next();
            
            Hashtable varbind = gl.varBindings;
            javax.swing.tree.DefaultMutableTreeNode root = br.toTree();
            root.setAllowsChildren(true);

            javax.swing.tree.DefaultTreeModel dtm = new DefaultTreeModel(root);
			
            int i = 0;
            Object[][] rowdata = new Object[varbind.size()][2];
            varSize = varbind.size();
            
            Enumeration e = varbind.keys();
            while (e.hasMoreElements()) {
                Object k = e.nextElement();
                Object val = varbind.get(k);
                String ks = (String) k;
                rowdata[i][0] = ks;
                rowdata[i][1] = val;
                
                System.out.println("Variable: " + ks + " Value: " + val);
                
                i++;
            }
            
			data.addElement(rowdata);
        }                 
      
    }

    catch(Exception e){
    	 System.out.println(e.toString());
    }
    
        
	}
}
