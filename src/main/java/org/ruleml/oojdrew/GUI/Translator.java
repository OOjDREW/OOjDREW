// OO jDREW - An Object Oriented extension of the Java Deductive Reasoning Engine for the Web
// Copyright (C) 2005 Marcel Ball
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

package org.ruleml.oojdrew.GUI;
import java.io.FileReader;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.ruleml.oojdrew.Config;
import org.ruleml.oojdrew.Configuration;
import org.ruleml.oojdrew.parsing.POSLParser;
import org.ruleml.oojdrew.parsing.RuleMLParser;
import org.ruleml.oojdrew.parsing.RuleMLParser.RuleMLFormat;
import org.ruleml.oojdrew.parsing.SubsumesParser;
import org.ruleml.oojdrew.util.DefiniteClause;
import org.ruleml.oojdrew.util.Types;

/**
 * This class implements a Translater that is used to translate RuleML 0.88 to 
 * RuleML 0.91 or POSL, and Vice Versa.
 *
 * <p>Title: OO jDREW</p>
 *
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.88</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * @author Marcel Ball
 * @version 0.89
 */
public class Translator extends JFrame {
        
     /**
      * This keeps track of the current Parser being used
      */
       
    public static RuleMLFormat ruleMLversion = RuleMLFormat.RuleML88;
    
    private RuleMLParser rmlParser;
    
     /**
      * This is the constructor for the Translator.
      */   
    public Translator(RuleMLParser rmlParser) {
    	this.rmlParser = rmlParser;
    	
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
   
    /** 
     * This method is called from within the constructor to
     * initialize the form.
     */
    private void jbInit() throws Exception {
            
        JMenu fileMenu = new JMenu("File");
        //creating a ExitAction and a ConnAction both are defined later
        ExitActionT exitAction = new ExitActionT("Exit");
        OpenActionT openAction = new OpenActionT("Open File");
        WriteActionT writeAction = new WriteActionT("Write File");
        
        
        //adding the connection action and exit action to the menu
        fileMenu.add(openAction);
        fileMenu.addSeparator();  
        fileMenu.add(writeAction);
        fileMenu.addSeparator();            
        fileMenu.add(exitAction);
                
        //making a new menu bar and adding the file menu to it                
        JMenuBar sysMenu = new JMenuBar();
        sysMenu.add(fileMenu);
        setJMenuBar(sysMenu);

        getContentPane().setLayout(null);
        jbToPosl.setBounds(new Rectangle(50, 338, 90, 23));
        jbToPosl.setText("To POSL");
        jbToPosl.addMouseListener(new Translator_jbToPosl_mouseAdapter(this));
        jbPosl91To1.setBounds(new Rectangle(155, 338, 180, 23));
        jbPosl91To1.setText("Upgrade POSL 0.91 to 1.0");
        jbPosl91To1.addMouseListener(new Translator_jbPOSL91TO1_mouseAdapter(this));
        jbToRML.setBounds(new Rectangle(350, 338, 120, 23));
        jbToRML.setText("To RuleML 0.88");
        jbToRML.addMouseListener(new Translator_jbToRML_mouseAdapter(this));
        jbToRML91.setBounds(new Rectangle(485, 338, 125, 23));
        jbToRML91.addMouseListener(new Translator_jbToRML91_mouseAdapter(this));
        jbToRML91.setText("To RuleML 0.91");
        this.addWindowListener(new Translator_windowAdapter(this));
        jScrollPane2.setBounds(new Rectangle(5, 370, 610, 300));
        jLabel1.setBounds(new Rectangle(5, 350, 66, 15));
        jScrollPane1.setBounds(new Rectangle(5, 28, 610, 300));
        jLabel2.setBounds(new Rectangle(5, 5, 84, 15));
       // jbTypes.setBounds(new Rectangle(530, 337, 115, 23));
       // jbTypes.setText("Types");
       // jbTypes.addMouseListener(new Translator_jbTypes_mouseAdapter(this));
        rmltext.setLineWrap(false);
        posltext.setLineWrap(false);
        posltext.setWrapStyleWord(true);
        jScrollPane1.getViewport().add(rmltext);
        rmltext.setText("");
        rmltext.setWrapStyleWord(true);

        jLabel1.setText("POSL");
        jScrollPane2.getViewport().add(posltext);
        jLabel2.setText("RuleML");
        this.getContentPane().add(jLabel1, null);
        this.getContentPane().add(jLabel2, null);
        this.getContentPane().add(jScrollPane1, null);
        this.getContentPane().add(jScrollPane2, null);
       // this.getContentPane().add(jbTypes);
        this.getContentPane().add(jbToPosl, null);
        this.getContentPane().add(jbPosl91To1, null);
        this.getContentPane().add(jbToRML, null);
        this.getContentPane().add(jbToRML91, null);

        this.setSize(630,730);
        this.setTitle("RuleML <-> POSL Converter");
        this.setResizable(false);
    }
   
    /**
     * This is the main method that is called when the Translator is ran.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Configuration config = new Config();
        RuleMLParser rmlParser = new RuleMLParser(config);
    	
    	
        Translator translator = new Translator(rmlParser);
        BasicConfigurator.configure();
        Logger root = Logger.getRootLogger();
        root.setLevel(Level.DEBUG);
                translator.setSize(700,750);
        translator.show();
        org.ruleml.oojdrew.Config.PRINTGENOIDS = false;
        org.ruleml.oojdrew.Config.PRINTGENSYMS = false;
        org.ruleml.oojdrew.Config.PRINTVARID = false;
        org.ruleml.oojdrew.Config.PRINTANONVARNAMES = false;
    }

    /**
     * This method first prompts what the user wants to write(Posl/RuleML) then
     * the user selects where they want to save the file and name the file.  
     * It then writes the contents after running the forward reasoner.
     */
   public static void writeFile(){
        
        JFrame f1 = new JFrame();
        
         Object[] possibleValues = {"RuleML", "POSL"};
         Object selectedValue = JOptionPane.showInputDialog(null,

            "Select one", "Type of File",

            JOptionPane.INFORMATION_MESSAGE, null,

            possibleValues, possibleValues[0]);
    
    System.out.println(selectedValue);
    
    
    if(selectedValue != null){

        Frame parent = new Frame();
        
        FileDialog fd = new FileDialog(parent, "Please choose a file:",
                   FileDialog.SAVE);
        fd.show();
        String inputValue = fd.getFile();
        String fileName = fd.getDirectory() + inputValue;
                
        FileOutputStream out;
    	PrintStream print;

            if(inputValue != null){
    
            try
               {
                       //false = new
                       //true = append
                      
                out = new FileOutputStream(fileName,false);
                print = new PrintStream(out);
                        
                        if(selectedValue.equals("RuleML")){
                                print.println(rmltext.getText());        
                        }
                        if(selectedValue.equals("POSL")){
                                print.println(posltext.getText());        
                        }

                        print.close();
              }                            
  
               catch (IOException e)
                {
                System.err.println("error with file");
                }
         }
        }
        }//write file
    
    /**
     * This method prompts the user to select a file and then,
     * places its contents in the selected text area(POSL, RuleML).
     */
    public static void openFile(){
        
         JFrame f1 = new JFrame();
        
         Object[] possibleValues = {"RuleML", "POSL"};
         Object selectedValue = JOptionPane.showInputDialog(null,

            "Select one", "Type of File",

            JOptionPane.INFORMATION_MESSAGE, null,

            possibleValues, possibleValues[0]);
            
    if(selectedValue != null){
    
    
    System.out.println(selectedValue);

        Frame parent = new Frame();
        
        FileDialog fd = new FileDialog(parent, "Please choose a file:",
                   FileDialog.LOAD);
        fd.show();

        String selectedItem = fd.getFile();        
        String fileName = fd.getDirectory() + fd.getFile();
        
                try {

                        FileReader inFile = new FileReader(fileName);
                        BufferedReader in = new BufferedReader(inFile);
                        String read ="";
                        String contents="";
                        
                        while((read = in.readLine()) != null)
                        {
                                contents = contents + read + '\n';
                        }
                        in.close();

                                if(selectedValue.equals("RuleML")){
                                rmltext.setText(contents);
                                posltext.setText("");        
                                }
                                if(selectedValue.equals("POSL")){
                                posltext.setText(contents);
                                rmltext.setText("");        
                                }                                
                                
                } catch (Exception e) {
                        posltext.setText(e.toString());
                        System.out.println(e.toString());
                }              
        }//selected value != null
  }//openFile

    //TypeDefFrame tdf = new TypeDefFrame();

    JScrollPane jScrollPane1 = new JScrollPane();
    static JTextArea rmltext = new JTextArea();
    JScrollPane jScrollPane2 = new JScrollPane();
    static JTextArea posltext = new JTextArea();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JButton jbToPosl = new JButton();
    JButton jbPosl91To1 = new JButton();
    JButton jbToRML = new JButton();
    JButton jbToRML91 = new JButton();
    //JButton jbTypes = new JButton();
    
    /**
     * This method is called when the user closing the window.
     */
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    /**
     * This method Translates POSL to RULEML
     */
    public void jbToRML_mouseClicked(MouseEvent e) {
            
        ruleMLversion = RuleMLFormat.RuleML88;
            
        String posltext = this.posltext.getText().trim();
        
        //////Work around to avoid type definitions in translator
        
        String patternStr = ":[ ]*([a-zA-Z_]+)";

        Pattern p = Pattern.compile(patternStr);
        Matcher m = p.matcher(posltext);      
        
        ArrayList<String> terms = new ArrayList<String>();
        
        while(m.find()) {
            for(int i=0; i< m.groupCount(); i++) {
                    String matchedWithColon = m.group(i);
                    
                    String matched = matchedWithColon.substring(1);
                    matched = matched.trim();
                                        
                    if(!terms.contains(matched) &&
                       !matched.equals("String") &&
                       !matched.equals("Thing") &&
                       !matched.equals("Nothing") &&
                       !matched.equals("Numeric") &&
                       !matched.equals("Integer") &&
                       !matched.equals("Real")){
                    	
                    		terms.add(matched);
                    }
                                 
            }
        }
        
        String tempOntology = "subsumes(A,B).";
        
        for(int i = 0; i < terms.size(); i++){
        	String nextLine = "subsumes(A," + terms.get(i) + ").";
        	tempOntology += "\n" + nextLine;
        }

        org.ruleml.oojdrew.util.Types.reset();
		Types.reset();
		SubsumesParser sp = new SubsumesParser(tempOntology);
		try {
			sp.parseSubsumes();
		} catch (Exception e1) {
 			JOptionPane.showMessageDialog(this, "Please make sure there are no spaces after a colon.\ni.e fact(number:[no space here]Integer) ", "Error",
                    JOptionPane.ERROR_MESSAGE);
		}
				
	    //////Work around to avoid type definitions in translator
		
        POSLParser pp = new POSLParser();
        try{
            pp.parseDefiniteClauses(posltext);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);
        }

        System.out.println("Done Parsing");

        Iterator it = pp.iterator();
        Element as = new Element("Assert");
        Element and = new Element("And");
        Attribute a = new Attribute("mapClosure", "universal");
        and.addAttribute(a);
        as.appendChild(and);

        while(it.hasNext()){
            DefiniteClause dc = (DefiniteClause)it.next();
            and.appendChild(dc.toRuleML(ruleMLversion));
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        nu.xom.Serializer sl = new nu.xom.Serializer(os);
        sl.setIndent(3);
        sl.setLineSeparator("\n");
        try {
        	Document doc = new Document(as);
            sl.write(doc);
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                                              JOptionPane.ERROR_MESSAGE);
        }

        this.rmltext.setText(os.toString());
    }

    public void jbToRML91_mouseClicked(MouseEvent e) {
            
    	ruleMLversion =  RuleMLFormat.RuleML91;
            
        String posltext = this.posltext.getText().trim();
        
        
        //////Work around to avoid type definitions in translator
        
        String patternStr = ":[ ]*([a-zA-Z_]+)";

        Pattern p = Pattern.compile(patternStr);
        Matcher m = p.matcher(posltext);      
        
        ArrayList<String> terms = new ArrayList<String>();
        
        while(m.find()) {
            for(int i=0; i< m.groupCount(); i++) {
                    String matchedWithColon = m.group(i);
                    
                    String matched = matchedWithColon.substring(1);
                    matched = matched.trim();
                                        
                    if(!terms.contains(matched) &&
                       !matched.equals("String") &&
                       !matched.equals("Thing") &&
                       !matched.equals("Nothing") &&
                       !matched.equals("Numeric") &&
                       !matched.equals("Integer") &&
                       !matched.equals("Real")){
                    	
                    		terms.add(matched);
                    }
                                 
            }
        }
        
        String tempOntology = "subsumes(A,B).";
        
        for(int i = 0; i < terms.size(); i++){
        	String nextLine = "subsumes(A," + terms.get(i) + ").";
        	tempOntology += "\n" + nextLine;
        }

        org.ruleml.oojdrew.util.Types.reset();
		Types.reset();
		SubsumesParser sp = new SubsumesParser(tempOntology);
		try {
			sp.parseSubsumes();
		} catch (Exception e1) {
 			JOptionPane.showMessageDialog(this, "Please make sure there are no spaces after a colon.\ni.e fact(number:[no space here]Integer) ", "Error",
                    JOptionPane.ERROR_MESSAGE);
		}
				
	    //////Work around to avoid type definitions in translator
        
        
        
        POSLParser pp = new POSLParser();
        try{
            pp.parseDefiniteClauses(posltext);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);
        }

        System.out.println("Done Parsing");

        Iterator it = pp.iterator();
        Element as = new Element("Assert");

        Element and = new Element("Rulebase");
        Attribute a = new Attribute("mapClosure", "universal");
        and.addAttribute(a);
        as.appendChild(and);

        while(it.hasNext()){
            DefiniteClause dc = (DefiniteClause)it.next();
            and.appendChild(dc.toRuleML(ruleMLversion));
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        nu.xom.Serializer sl = new nu.xom.Serializer(os);
        sl.setIndent(3);
        sl.setLineSeparator("\n");
        try {
        	Document doc = new Document(as);
            sl.write(doc);
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                                              JOptionPane.ERROR_MESSAGE);
        }

        this.rmltext.setText(os.toString());
    }

    private static void confirmationDialog(Component parent, String text) {
        JOptionPane pane = new JOptionPane(text, JOptionPane.INFORMATION_MESSAGE);
      JDialog dialog = pane.createDialog(parent, "Information");
      // Make dialog modal
      dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
      dialog.setVisible(true);
      return;
   }

    /**
     * This method upgrades POSL 0.91 to POSL 1.0.
     */
    public void jbPOSL91TO1_mouseClicked(MouseEvent e) {
	// TODO: implement!
	try
	{
	String posl91Text = this.posltext.getText();
	String posl1Text = new String();

	StringTokenizer st = new StringTokenizer(posl91Text, ":");
	int substitutions = 0;
	while(st.hasMoreTokens())
	{
		String upToToken = st.nextToken();
		posl1Text += upToToken;
		if(st.hasMoreTokens())
		{
			substitutions++;
			posl1Text += "^^";
		}
	}
	// Create modal popup to tell user conversion completed successfully.
	confirmationDialog((JButton)e.getSource(), "POSL 0.91 was converted to POSL 1.0 without errors.\n" + substitutions + " substitution(s) were made.");
	this.posltext.setText(posl1Text);
	}
	catch(Exception exception)
	{
		// Create modal popup displaying the error message
		confirmationDialog((JButton)e.getSource(), "An error occurred while upgrading POSL text field, the text was not changed.\nError: " + exception.getMessage());
	}
    }

    /**
     * This method Translates RULEML to POSL.
     */
    public void jbToPosl_mouseClicked(MouseEvent e) {
        String rmltext = this.rmltext.getText();
        ///work around to remove type dependency
        StringTokenizer st = new StringTokenizer(rmltext,"\n");
        String typeDoc = "<Top>\n";
        while(st.hasMoreTokens()){
        	String nextLine = st.nextToken().trim() + "\n";
        	
        	if(nextLine.contains("type=")){
     
        		typeDoc += nextLine;
        	}
        }
        typeDoc += "</Top>";
                
        Builder bl = new Builder();
   		StringReader sr = new StringReader(typeDoc);
   		Document doc = null;
   		
   		try {
			doc = bl.build(sr);
		} catch (ValidityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParsingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
		
		Element root = doc.getRootElement();
        
		Elements children = root.getChildElements();
		
		ArrayList<String> terms = new ArrayList<String>();
		
		for(int i = 0; i < children.size(); i++){
			
			Element nextChild = children.get(i);
			String matched = nextChild.getAttribute(0).getValue();
			
            if(!terms.contains(matched) &&
                    !matched.equals("String") &&
                    !matched.equals("Thing") &&
                    !matched.equals("Nothing") &&
                    !matched.equals("Numeric") &&
                    !matched.equals("Integer") &&
                    !matched.equals("Real")){
                 	
                 		terms.add(matched);
                 }
		}
		
		String tempOntology = "subsumes(A,B).";
	        
	    for(int i = 0; i < terms.size(); i++){
	    	String nextLine = "subsumes(A," + terms.get(i) + ").";
	    	tempOntology += "\n" + nextLine;
	    }
	    org.ruleml.oojdrew.util.Types.reset();
	    Types.reset();
		SubsumesParser sp = new SubsumesParser(tempOntology);
			
		try {
			sp.parseSubsumes();
		} catch (Exception e1) {
	 		JOptionPane.showMessageDialog(this, "Please make sure there are no spaces after a colon.\ni.e fact(number:[no space here]Integer) ", "Error",
	                   JOptionPane.ERROR_MESSAGE);
		}
		///work around to remove type dependency
		try
		{
			rmlParser.parseRuleMLString(RuleMLFormat.RuleML91, rmltext);
		} catch (Exception ex)
		{

			try
			{
				rmlParser.parseRuleMLString(RuleMLFormat.RuleML88, rmltext);
			} catch (Exception ex2)
			{
				JOptionPane.showMessageDialog(this, ex2.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
        }

        StringBuffer sb = new StringBuffer();
        Iterator it = rmlParser.iterator();
        while(it.hasNext()){
            DefiniteClause dc = (DefiniteClause)it.next();
            sb.append(dc.toPOSLString());
            sb.append("\n");
        }

        this.posltext.setText(sb.toString());
    }
	
	/**
	 * This method is called when the user clicks on the button to define type 
	 * information.
	 */
    public void jbTypes_mouseClicked(MouseEvent e) {
        
       // tdf.show();
    }
}
 /**
  * This class implements a ExitActionT which is used by the Menu bar in the
  * Translator.
  *
  * <p>Title: OO jDREW</p>
  *
  * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
  * 0.88</p>
  *
  * <p>Copyright: Copyright (c) 2005</p>
  *
  * @author Ben Craig
  * @version 0.89
  */
        class ExitActionT extends AbstractAction
        {
                /**
                 * This is the contructor for a ExitAction.
                 *It calls the contructor for a AbstractAction.
                 *@param String name - the name for the action
                 */
                
                ExitActionT(String name)
                {
                        super(name);
                }

                /**
                 * This method is called when a ExitAction is performed
                 *When an exitAction is performed it will exit the program.
                 *@param event - the event that occured
                 */

                public void actionPerformed(ActionEvent event)
                {
                        System.exit(0);
                }
        }//ExitAction
        
 /**
  * This class implements a OpenActionT which is used by the Menu bar in the
  * Translator.
  *
  * <p>Title: OO jDREW</p>
  *
  * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
  * 0.88</p>
  *
  * <p>Copyright: Copyright (c) 2005</p>
  *
  * @author Ben Craig
  * @version 0.89
  */       
        class OpenActionT extends AbstractAction
        {
                /**
                 * This is the contructor for a OpenAction.
                 * It calls the contructor for a AbstractAction.
                 * @param String name - the name for the action
                 */
                
                OpenActionT(String name)
                {
                        super(name);
                }

                /**
                 * This method is called when a OpenAction is performed
                 * When an openAction is performed it will open a file.
                 * @param event - the event that occured
                 */

                public void actionPerformed(ActionEvent event)
                {
                        Translator.openFile();
                }
        }//Open Action
 /**
  * This class implements a WriteActionT which is used by the Menu bar in the
  * Translator.
  *
  * <p>Title: OO jDREW</p>
  *
  * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
  * 0.88</p>
  *
  * <p>Copyright: Copyright (c) 2005</p>
  *
  * @author Ben Craig
  * @version 0.89
  */ 
        class WriteActionT extends AbstractAction
        {
                /**
                 * This is the contructor for a WriteAction.
                 * It calls the contructor for a AbstractAction.
                 * @param String name - the name for the action
                 */
                
                WriteActionT(String name)
                {
                        super(name);
                }

                /**
                 * This method is called when a WriteAction is performed
                 * When an writeAction is performed it will write the text
                 * out to a file.
                 * @param event - the event that occured
                 */

                public void actionPerformed(ActionEvent event)
                {
                        Translator.writeFile();
                }
        }//Open Action
 
 /**
  * This class implements a MouseAdapter
  *
  * <p>Title: OO jDREW</p>
  *
  * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
  * 0.88</p>
  *
  * <p>Copyright: Copyright (c) 2005</p>
  *
  * @author Ben Craig
  * @version 0.89
  */ 
class Translator_jbTypes_mouseAdapter extends MouseAdapter {
    private Translator adaptee;
    Translator_jbTypes_mouseAdapter(Translator adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.jbTypes_mouseClicked(e);
    }
}
 /**
  * This class implements a MouseAdapter
  *
  * <p>Title: OO jDREW</p>
  *
  * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
  * 0.88</p>
  *
  * <p>Copyright: Copyright (c) 2005</p>
  *
  * @author Ben Craig
  * @version 0.89
  */ 
class Translator_jbToPosl_mouseAdapter extends MouseAdapter {
    private Translator adaptee;
    Translator_jbToPosl_mouseAdapter(Translator adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.jbToPosl_mouseClicked(e);
    }
}
/**
 * This class implements a MouseAdapter
 *
 * <p>Title: OO jDREW</p>
 *
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.88</p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * @author Daniel Latimer
 * @version
 */
class Translator_jbPOSL91TO1_mouseAdapter extends MouseAdapter {
    private Translator adaptee;
    Translator_jbPOSL91TO1_mouseAdapter(Translator adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.jbPOSL91TO1_mouseClicked(e);
    }
}
 /**
  * This class implements a MouseAdapter
  *
  * <p>Title: OO jDREW</p>
  *
  * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
  * 0.88</p>
  *
  * <p>Copyright: Copyright (c) 2005</p>
  *
  * @author Ben Craig
  * @version 0.89
  */ 
class Translator_jbToRML_mouseAdapter extends MouseAdapter {
    private Translator adaptee;
    Translator_jbToRML_mouseAdapter(Translator adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.jbToRML_mouseClicked(e);
    }
}
 /**
  * This class implements a MouseAdapter
  *
  * <p>Title: OO jDREW</p>
  *
  * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
  * 0.88</p>
  *
  * <p>Copyright: Copyright (c) 2005</p>
  *
  * @author Ben Craig
  * @version 0.89
  */ 
class Translator_jbToRML91_mouseAdapter extends MouseAdapter {
    private Translator adaptee;
    Translator_jbToRML91_mouseAdapter(Translator adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.jbToRML91_mouseClicked(e);
    }
}
 /**
  * This class implements a WindowAdapter
  *
  * <p>Title: OO jDREW</p>
  *
  * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
  * 0.88</p>
  *
  * <p>Copyright: Copyright (c) 2005</p>
  *
  * @author Ben Craig
  * @version 0.89
  */ 
class Translator_windowAdapter extends WindowAdapter {
    private Translator adaptee;
    Translator_windowAdapter(Translator adaptee) {
        this.adaptee = adaptee;
    }

    public void windowClosing(WindowEvent e) {
        adaptee.windowClosing(e);
    }
}
