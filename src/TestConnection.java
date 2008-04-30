// OO jDREW Version 0.89
// Copyright (c) 2005 Marcel Ball
// This is the right one.
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

/*
 * TestConnection.java
 *
 * Just a test class to connect to the PROVA server
 *
 * Created on May 1, 2007, 2:57 PM
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

public class TestConnection{
	
	
	 public static void main(String[] args) {
	 	try
		{
	 	Socket socket;
		InputStreamReader in_stream;
		OutputStream out_stream;
		BufferedReader in_reader;
		PrintWriter out_writer;
	
	 	
	   	socket = new Socket("10.1.23.67", 8888);
		out_stream = socket.getOutputStream();
		out_writer = new PrintWriter(out_stream);
		in_stream = new InputStreamReader(socket.getInputStream());
		in_reader = new BufferedReader(in_stream);
		
		System.out.println("connected");
		
	InputStreamReader isr = new InputStreamReader( System.in );


    BufferedReader stdin = new BufferedReader( isr );

    String input = stdin.readLine();
	System.out.println(input);
		
		out_writer.println(input);
		out_writer.flush();
		
		System.out.println(in_reader.readLine());
		
		}
		
		
		catch (UnknownHostException exp)
		{
			exp.getMessage();
			return;
		}
		catch (IOException exp)
		{
			exp.getMessage();
			return;
		}
	 
	 	
	 
	 
	 }
	 
	 
}