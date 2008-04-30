import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import jdrew.oo.td.*;
import jdrew.oo.util.*;
import org.apache.log4j.*;


import java.net.*;
import nu.xom.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class HttpTest{
	
	public static void main(String args[]) throws IOException{

	Date t1 = new GregorianCalendar().getTime();
	System.out.println(t1.getDay());

  	try
    {	
	
	int serverport = 8888;
    Socket s1;
 	MessageParser m = null;
 	
 	ServerSocket listener = new ServerSocket(serverport);
    System.out.println("starting server");
    	
   // while(true){

    s1 = listener.accept();
	
	System.out.println("connected");

    
    OutputStream outputS1 = s1.getOutputStream();
    PrintWriter outS1 = new PrintWriter(outputS1);
    InputStreamReader isrS1 = new InputStreamReader(s1.getInputStream());
    BufferedReader S1Input = new BufferedReader(isrS1);
         
    outS1.print("you connected");
    outS1.flush(); 
	
	   BackwardReasoner br = new BackwardReasoner();
       Iterator solit;
         
       SymbolTable.reset();
             
       POSLParser pp = new POSLParser();
       String kbstr = "emoo(a,b,c).\nemoo(b,c,d).\nemoo(c,d,e).";
       System.out.println(kbstr);
	
	      
      pp.parseDefiniteClauses(kbstr);

	
	  br.loadClauses(pp.iterator());
          	
    	System.out.println("===test====");
    		Iterator it = pp.iterator();
    		while (it.hasNext()) {
            	DefiniteClause d = (DefiniteClause) it.next();
            	System.out.println("Loaded clause: " + d.toPOSLString());
    		}
    	
    	System.out.println("===test====");
    	boolean hasMore = true;
	
	br = new BackwardReasoner(br.clauses, br.oids);
	
	String message = "";

	BufferedReader brd = new BufferedReader(new InputStreamReader(System.in));
	
	String input = "";
	
	while(!input.equals("</RuleML>")){    
     
      	input = S1Input.readLine();
      	
      	message = message + input;	
      	}
	
	System.out.println(message);
	
	m = new MessageParser(message);
	Element atom = m.parseForContent();
	System.out.println(m.getSender());
	System.out.println(m.getProtocol());
	System.out.println(m.getId());
	System.out.println(m.getRel());
	
	QueryBuilder q = new QueryBuilder(atom);
    String query = q.generateDoc();
    
    System.out.println(query);
	
	  RuleMLParser qp = new RuleMLParser();
     
      DefiniteClause dc = null;
      
      dc = qp.parseRuleMLQuery(query);

      System.out.println("======" + dc.toPOSLString() + "======");
             
        solit = br.iterativeDepthFirstSolutionIterator(dc);
 
        System.out.println("===binding test======");
        Vector data = new Vector();
        int varSize = 0;
        
        while(solit.hasNext()) {

            BackwardReasoner.GoalList gl = (BackwardReasoner.GoalList) solit.
                                           next();
            System.out.println(gl.toString());
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
                System.out.println("ks: " + ks + " val: " + val);
                i++;
            }
            
			data.addElement(rowdata);
        }                 
            System.out.println("===binding test======");
            
            String[] messages = new String[data.size()];
            
            
            MessageGenerator g = new MessageGenerator(data,varSize,"User",m.getId(),m.getProtocol(), m.getRel());
            
            messages = g.Messages();
            
           for(int i = 0; i < data.size();i++){

            	System.out.println("sending: " + messages[i].toString());
            }
	
    }
   catch(Exception exp){
   		System.out.println("error: " + exp.toString());
  	 	return;
   }  
	
	}
}