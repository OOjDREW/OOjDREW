/*
 * Copyright (C) 1999-2007 <a href="mailto:adrian.paschke@gmx.de">Adrian Paschke</a>
 * 
 * This servlet is part of the Rule Based Service Level Agreement (RBSLA)
 * framework, available at 
 * <a href=" http://ibis.in.tum.de/staff/paschke/rbsla/index.htm">RBSLA</a>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import javax.swing.*;
import javax.swing.tree.*;
import javax.servlet.*; 
import javax.servlet.http.*;
import java.io.*;
import java.net.URL;
import nu.xom.*;

import jdrew.oo.td.*;
import jdrew.oo.util.*;
import java.util.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;

import com.oreilly.servlet.HttpMessage;

public class LiaisonChairRuleML2008 extends HttpServlet {

  public final static String FS = System.getProperty("file.separator");
  
  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
  }

 /////////////////////////
 
 
  public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    System.out.println("Liaison Chair Servlet RuleML-2008");
    out.println("Liaison Chair Servlet RuleML-2008");
    
    Calendar cal = new GregorianCalendar();
	
	int year = cal.get(Calendar.YEAR);           
    int month = cal.get(Calendar.MONTH) + 1;           
    int day = cal.get(Calendar.DAY_OF_MONTH);

	String date;	
 	
 	if(month == 10 || month == 11 || month == 12)	 
 		date = "" + year + month + day;
 	else
 		date = "" + year + "0" + month + day;
 	
 	date = "date(" + date + ":integer).";
 	System.out.println("Liaison Chair Servlet Console update:");
 	System.out.println(date);
  }
 
 
 /////////////////////////

  public void doPost (HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    response.setContentType("text/html; charset=UTF-8"); 	
    PrintWriter out = response.getWriter();
   
    try
    {	   	
    	System.out.println("Liaison Chair Servlet");
    	System.out.println(response.toString());
    	
    	BufferedReader brd = request.getReader();
    	
    	String input = "";
    	String message = "";
    	
    	while(!input.equals("</RuleML>")){    
     
      	input = brd.readLine();
      	
      	message = message + input;	
      	}
           	
    System.out.println("Received Message: "  + message);
	
//	BackwardReasoner br = new BackwardReasoner();
 //   Iterator solit =null;
 //   DefiniteClause dc = null;
 //   SymbolTable.reset();
               
    POSLParser pp = new POSLParser();
   // String contents = "c(a).\nc(b).\nc(c).";
   
    Date t1 = new GregorianCalendar().getTime();
	System.out.println(t1.getHours() + ":" + t1.getMinutes()); 
	//append time to contents
	
	System.out.println("day: " + t1.getDay());
	System.out.println("day: " + t1.getYear());
	System.out.println("day: " + t1.getMonth());
	
	//time
	String time = "time(" + t1.getHours ()+ ":integer).";
	System.out.println(time);	
   	
	String url = "http://www.jdrew.org/oojdrew/ruleSetsRuleML-2008/liaisonChairRuleML-2008.posl";
	
	//String url = "http://www.jdrew.org/oojdrew/test.posl";
    String contents = "";
   
   	//day of the week
    int day = t1.getDay();
    boolean weekday = true;
    
    if(day == 0 || day == 6){
    	weekday = false;
    }
    
    String dayOfWeek;
    
    if(weekday){
    	dayOfWeek = "day(weekday).";	
    }
    else{
    	dayOfWeek = "day(weekend).";
    }
    //full date 
    Calendar cal = new GregorianCalendar();
	
	int year = cal.get(Calendar.YEAR);           
    int month = cal.get(Calendar.MONTH) + 1;           
    int day2 = cal.get(Calendar.DAY_OF_MONTH);

	String date;	
 	
	String day3 = "" + day2;
	
	if(day2 == 1 || day2 == 2 || day2 == 3 ||
	   day2 == 4 || day2 == 5 || day2 == 6 ||
	   day2 == 7 || day2 == 8 || day2 == 9 ){
	
		day3 = "0" + day2; 
		}
	
	
 	if(month == 10 || month == 11 || month == 12)	 
 		date = "" + year + month + day3;
 	else
 		date = "" + year + "0" + month + day3;
 	
 	date = "date(" + date + ":integer).";
 	
 	System.out.println(date);
    
    
    String url2 = "http://www.jdrew.org/oojdrew/ruleSetsRuleML-2008/publicityChairOntologyRuleML-2008.rdf";
    HttpClient client2 = new HttpClient();
    GetMethod method2 = new GetMethod( url2 );
    method2.setFollowRedirects( true );
	String typestr = "";  
    // Execute the GET method
    int statusCode2 = client2.executeMethod( method2 );
    if( statusCode2 != -1 ) {
      typestr = method2.getResponseBodyAsString();
    }
   		System.out.println("Types: " + typestr);
        Types.reset();
        RDFSParser.parseRDFSString(typestr);
             
   try {
    HttpClient client = new HttpClient();
    GetMethod method = new GetMethod( url );
    method.setFollowRedirects( true );

    // Execute the GET method
    int statusCode = client.executeMethod( method );
    if( statusCode != -1 ) {
      contents = method.getResponseBodyAsString();
    }
   }
   catch( Exception e ) {
    e.printStackTrace();
   }
    
    contents = contents + "\n" + time;
    contents = contents + "\n" + dayOfWeek;
    contents = contents + "\n" + date;
    
    BackwardReasoner br = new BackwardReasoner();
    Iterator solit =null;
    DefiniteClause dc = null;
    SymbolTable.reset();
    
    pp.parseDefiniteClauses(contents);
   
	br.loadClauses(pp.iterator());

    Iterator it = pp.iterator();
    while (it.hasNext()) {
    	DefiniteClause d = (DefiniteClause) it.next();
    	System.out.println("Loaded clause: " + d.toPOSLString());
    }	
      
	br = new BackwardReasoner(br.clauses, br.oids);
	 
	MessageParser m = new MessageParser(message);
	Element atom = null;
	
	try{
	
		 atom = m.parseForContent();
	
	}
	catch(Exception e){
		
		 System.out.println("Invalid Message");
		 //out.flush();
		 
	}
	  
    QueryBuilder q = new QueryBuilder(atom);
    String query = q.generateDoc();
    System.out.println("ABOUT TO INPUT THIS QUERY:" + query);
    RuleMLParser qp = new RuleMLParser();

	try{
	
    dc = qp.parseRuleMLQuery(query);
    
    }
    
    catch(Exception e){
    	 System.out.println("Invalid Query");
		 //out.flush();
    }
    
    //solit = br.iterativeDepthFirstSolutionIterator(dc);
    
 	solit = br.iterativeDepthFirstSolutionIterator(dc);

    int varSize = 0;
            
    while(solit.hasNext()) {

    	Vector data = new Vector();
    	
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
            
            i++;
        }
        
		data.addElement(rowdata);
		String[] messages = new String[data.size()];
		MessageGenerator g = new MessageGenerator(data,varSize,"ruleml2008_LiaisonChair",m.getId(),m.getProtocol(), m.getRel());
		messages = g.Messages2();
    
        String appender = "";
        
        URL sender = new URL("http://10.1.3.4:9997");
        HttpMessage msg = new HttpMessage(sender);   
     	Properties props = new Properties();
                
       for(int i1 = 0; i1 < data.size();i1++){		
            System.out.println(i1 + ")" + messages[i1].toString());
            props.put("text", messages[i1].toString());
       		InputStream in = msg.sendGetMessage(props);
       }
	   System.out.println("NEXT MESSAGE");
    }                 

    MessageGenerator g = new MessageGenerator(null,varSize,"ruleml2008_LiaisonChair",m.getId(),m.getProtocol(), m.getRel());
    
    URL sender = new URL("http://10.1.3.4:9997");
    HttpMessage msg = new HttpMessage(sender);   
 	Properties props = new Properties();
    
    String finalMessage = g.finalMessage(query);
    
    System.out.println(finalMessage);
    
    props.put("text", finalMessage);
		InputStream in = msg.sendGetMessage(props); 
    
    System.out.println("Stop_Communication");     
  
     
    }
    catch (Exception e)
    {		
		System.out.println("ERROR has occured : " + e.toString());
	
    }
    out.close();
  }
  
  // Get parameters from the request URL.
  String getRequestParam(HttpServletRequest request, String param)
  {
	  if (request != null) 
    { 
	    String paramVal = request.getParameter(param); 
		  return paramVal;
	  }
	  return null;
  }
  
}
