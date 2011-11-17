// OO jDREW Version 0.89
// Copyright (c) 2005 Marcel Ball
// This is the right one.
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

/*
 * Server.java
 * 
 * This method will open a socket connection and wait for a connection, the socket will
 * recieve a message and then parse it.  Then the server will answer the query and send
 * a return message to the socket.
 * 
 * Created on March 3, 2005, 2:57 PM
 */

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

public class Server{
	
 public static void main(String[] args) {
  	
  	try
    {
    		
 	int serverport = 8080;
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
         
       //load up knowledge base
       BackwardReasoner br = new BackwardReasoner();
       Iterator solit;
         
       SymbolTable.reset();
             
       POSLParser pp = new POSLParser();
       String kbstr = "c(a,b,c).\nc(b,c,d).\nc(c,d,e).";
       System.out.println(kbstr);
         
            try {
              pp.parseDefiniteClauses(kbstr);
            } catch (Exception ex) {
					System.out.println(ex.getMessage());
            }

        br.loadClauses(pp.iterator());
          	
    	System.out.println("===test====");
    		Iterator it = pp.iterator();
    		while (it.hasNext()) {
            	DefiniteClause d = (DefiniteClause) it.next();
            	System.out.println("Loaded clause: " + d.toPOSLString());
    		}
    	
    	System.out.println("===test====");
    	boolean hasMore = true;
    		       
       //execute query

      br = new BackwardReasoner(br.clauses, br.oids);
       
      String input1 = "";
      Element atom = null;
      DefiniteClause dc = null;
      String incMessage = "";
      
     // while(input1.indexOf("</Message>") != -1){    
      while(!input1.equals("</Message>")){    
     //while ((input1 = S1Input.readLine()) != null) { 
      	input1 = S1Input.readLine();
      	System.out.println("recieved: " + input1);
      	incMessage = incMessage + input1 + "\n";
      }
       
      System.out.println(incMessage);
      
      StringTokenizer st = new StringTokenizer(incMessage, "\n");
      int count = 0;
      while(count != 6){
      	System.out.println(count + " " +  st.nextToken());
      	count++;
      }
      
      String xmlMessage = "";
      System.out.println("===XML===");
      while(st.hasMoreTokens()){
      	xmlMessage = xmlMessage + st.nextToken();
      }
      System.out.println(xmlMessage);
       try{        
     // input1 = "<Message mode='outbound' directive='request'><oid> <Ind>id</Ind></oid><protocol> <Ind>esb</Ind></protocol><sender> <Ind>User</Ind></sender><content><Atom> <Rel>rides</Rel> <Var>thing</Var> <Var>person</Var> </Atom></content></Message>";
      m = new MessageParser(xmlMessage);
      atom = m.parseForContent();
      
      System.out.println("===CONTENT TEST ===");
      System.out.println("sender: " + m.getSender() + " id: " + m.getId() + " prot: " + m.getProtocol());
      
      
      QueryBuilder q = new QueryBuilder(atom);
      String query = q.generateDoc();
		//parsing the query
      RuleMLParser qp = new RuleMLParser();
     
       try{
       	dc = qp.parseRuleMLQuery(query);
       } 
        catch (Exception ex) {
 			System.out.println(ex.getMessage());
       }
      System.out.println("======" + dc.toPOSLString() + "======");
      
       }
       catch (Exception ex) {
       	      System.out.println(ex.getMessage());
       }
       
       
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
					
			Socket socket;
			InputStreamReader in_stream;
			OutputStream out_stream;
			BufferedReader in_reader;
			PrintWriter out_writer;
	
	   		//socket = new Socket("10.1.23.67", 9999);
			//out_stream = socket.getOutputStream();
			//out_writer = new PrintWriter(out_stream);
			//in_stream = new InputStreamReader(socket.getInputStream());
			//in_reader = new BufferedReader(in_stream);


            for(int i = 0; i < data.size();i++){
            		
            		//out_writer.println(messages[i].toString());
					//out_writer.flush();
            	//outS1.println(messages[i].toString());
            	//outS1.flush();
            	System.out.println("sending: " + messages[i].toString());
            }
            if (!solit.hasNext()) {
            hasMore = false;
        	}
 	 s1.close();
	// }//while	
	  	
    }
   catch(NullPointerException exp){
   		exp.getMessage();
  	 	return;
   }  
   catch(UnknownHostException exp){	   
   		exp.getMessage();
 		return;
   }
   catch (IOException exp){
   		exp.getMessage();
   		return;
   }  	
 	
 }
 
}