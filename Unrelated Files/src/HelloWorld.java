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

/** Simple servlet used to test server.
 *  <P>
 *  Taken from Core Servlets and JavaServer Pages 2nd Edition
 *  from Prentice Hall and Sun Microsystems Press,
 *  http://www.coreservlets.com/.
 *  &copy; 2003 Marty Hall; may be freely used or adapted.
 */

public class HelloWorld {

	public static void main(String args[]){
		
		Vector input = new Vector();
		
		Object[][] ob = new Object[2][2];
		
		ob[0][0] = "?XTEst";
		ob[0][1] = "<Expr>" + "\n" +
				   "   <Fun>Hi</Fun>" + "\n" +
				   "   <Ind>bob</Ind>"+ "\n" +
				   "</Expr>";
				
		ob[1][0] = "?Y";
		ob[1][1] = "<Ind>LAWL</Ind>";
		
		input.add(ob);
		
		Object[][] ob2 = new Object[2][2];
		
		ob2[0][0] = "?X";
		ob2[0][1] = "<Expr>" + "\n" +
				   "   <Fun>Hi</Fun>" + "\n" +
				   "   <Ind>bob</Ind>"+ "\n" +
				   "</Expr>";
				
		ob2[1][0] = "?Y";
		ob2[1][1] = "<Ind>LAWL</Ind>";
		
		
		input.add(ob2);
		
		int varSize = 2;
		
		MessageGenerator m = new MessageGenerator(input, varSize, "senderIn", "idIn", "protocolIn", "rel");
		String[] mess = m.Messages2();
		
		for(int i = 0; i < mess.length; i++)
			System.out.println(mess[i]);
		
		
	}



}