	// OO jDREW Version 0.89
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more
// details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package jdrew.oo.gui;

import java.util.*;
import java.awt.*;

import java.io.*;

import javax.swing.*;
//import com.borland.jbcl.layout.XYLayout;
//import com.borland.jbcl.layout.*;
import java.awt.event.*;

import jdrew.oo.Config;
import jdrew.oo.util.*;
import java.util.*;
import nu.xom.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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
       
    public static int currentParser =  RuleMLParser.RULEML88;
     /**
      * This is the constructor for the Translator.
      */   
    public Translator() {
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
        jbToPosl.setBounds(new Rectangle(50, 338, 115, 23));
        jbToPosl.setText("To POSL");
        jbToPosl.addMouseListener(new Translator_jbToPosl_mouseAdapter(this));
        jbToRML.setBounds(new Rectangle(200, 338, 120, 23));
        jbToRML.setText("To RuleML 0.88");
        jbToRML.addMouseListener(new Translator_jbToRML_mouseAdapter(this));
        jbToRML91.setBounds(new Rectangle(350, 338, 150, 23));
        jbToRML91.addMouseListener(new Translator_jbToRML91_mouseAdapter(this));
        jbToRML91.setText("To RuleML 0.91");
        this.addWindowListener(new Translator_windowAdapter(this));
        jScrollPane2.setBounds(new Rectangle(5, 370, 610, 300));
        jLabel1.setBounds(new Rectangle(5, 350, 66, 15));
        jScrollPane1.setBounds(new Rectangle(5, 28, 610, 300));
        jLabel2.setBounds(new Rectangle(5, 5, 84, 15));
        jbTypes.setBounds(new Rectangle(530, 337, 115, 23));
        jbTypes.setText("Types");
        jbTypes.addMouseListener(new Translator_jbTypes_mouseAdapter(this));
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
        this.getContentPane().add(jbTypes);
        this.getContentPane().add(jbToPosl, null);
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
        
        Translator translator = new Translator();
        BasicConfigurator.configure();
        Logger root = Logger.getRootLogger();
        root.setLevel(Level.DEBUG);
                translator.setSize(700,750);
        translator.show();
        jdrew.oo.Config.PRINTGENOIDS = false;
        jdrew.oo.Config.PRINTGENSYMS = false;
        jdrew.oo.Config.PRINTVARID = false;
        jdrew.oo.Config.PRINTANONVARNAMES = false;
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

    TypeDefFrame tdf = new TypeDefFrame();

    JScrollPane jScrollPane1 = new JScrollPane();
    static JTextArea rmltext = new JTextArea();
    JScrollPane jScrollPane2 = new JScrollPane();
    static JTextArea posltext = new JTextArea();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JButton jbToPosl = new JButton();
    JButton jbToRML = new JButton();
    JButton jbToRML91 = new JButton();
    JButton jbTypes = new JButton();
    
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
            
            currentParser =  RuleMLParser.RULEML88;
            
        String posltext = this.posltext.getText().trim();
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
            and.appendChild(dc.toRuleML(currentParser));
        }

        java.io.StringWriter sw = new java.io.StringWriter();
        nu.xom.Serializer sl = new nu.xom.Serializer(sw);
        sl.setIndent(3);
        sl.setLineSeparator("\n");
        try {
            sl.write(as);
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                                              JOptionPane.ERROR_MESSAGE);
        }

        this.rmltext.setText(sw.getBuffer().toString());
    }

        public void jbToRML91_mouseClicked(MouseEvent e) {
            
    currentParser =  RuleMLParser.RULEML91;
            
        String posltext = this.posltext.getText().trim();
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
            and.appendChild(dc.toRuleML(currentParser));
        }

        java.io.StringWriter sw = new java.io.StringWriter();
        nu.xom.Serializer sl = new nu.xom.Serializer(sw);
        sl.setIndent(3);
        sl.setLineSeparator("\n");
        try {
            sl.write(as);
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                                              JOptionPane.ERROR_MESSAGE);
        }

        this.rmltext.setText(sw.getBuffer().toString());
    }

    /**
     * This method Translates RULEML to POSL.
     */
    public void jbToPosl_mouseClicked(MouseEvent e) {
        String rmltext = this.rmltext.getText();
      
        RuleMLParser rmp = new RuleMLParser();
        try{
            rmp.parseRuleMLString(RuleMLParser.RULEML91, rmltext);
        }catch(Exception ex){
                
                try{
                rmp.parseRuleMLString(RuleMLParser.RULEML88, rmltext);
                }
                catch(Exception ex2){
                        JOptionPane.showMessageDialog(this, ex2.getMessage(), "Error",
                                         JOptionPane.ERROR_MESSAGE);
                }
        }

        StringBuffer sb = new StringBuffer();
        Iterator it = rmp.iterator();
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
        
        tdf.show();
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
