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

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * This class implements a TypeDefFrame used by the Translator.
 *
 * <p>Title: OO jDREW</p>
 *
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.88</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * @author not attributable
 * @version 0.89
 */
public class TypeDefFrame extends JFrame {
    JButton jbParseTypes = new JButton();
    JScrollPane jScrollPane1 = new JScrollPane();
    static JTextArea typetext = new JTextArea();
     /**
      * This is the constructor for the TypeDefFrame.
      */   
    public TypeDefFrame() {
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
        ExitActionTDF exitAction = new ExitActionTDF("Exit");
        OpenActionTDF openAction = new OpenActionTDF("Open File");
    
        //adding the connection action and exit action to the menu
        fileMenu.add(openAction);
        //fileMenu.addSeparator();            
        //fileMenu.add(exitAction);
                
        //making a new menu bar and adding the file menu to it                
        JMenuBar sysMenu = new JMenuBar();
        sysMenu.add(fileMenu);
        setJMenuBar(sysMenu);
    	
    	
        getContentPane().setLayout(null);
        jbParseTypes.setBounds(new Rectangle(475, 452, 141, 24));
        jbParseTypes.setText("Parse Types");
        jbParseTypes.addMouseListener(new
                                      TypeDefFrame_jbParseTypes_mouseAdapter(this));
        typetext.setText("");
        jScrollPane1.setBounds(new Rectangle(5, 5, 620, 435));
        jScrollPane1.getViewport().add(typetext);
        this.getContentPane().add(jScrollPane1, null);
        this.getContentPane().add(jbParseTypes, null);

        this.setTitle("Type Definition");
        this.setSize(640, 540);
    }
    
    /**
     * This is the main method that is called when the Translator is ran.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TypeDefFrame typedefframe = new TypeDefFrame();
    }
   
    /**
     * This method prompts the user to select a file and then,
     * places its contents in the typedef text area.
     */
    public static void openFile(){
    
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

                        
                        typetext.setText(contents);
 
                                
                } catch (Exception e) {
                        System.out.println(e.toString());
                }              
        
  }//openFile
    
   /**
    * This method parses the Types.
    */
    public void jbParseTypes_mouseClicked(MouseEvent e) {
        String typetext = this.typetext.getText().trim();
        org.ruleml.oojdrew.util.Types.reset();
        try{
            org.ruleml.oojdrew.parsing.RDFSParser.parseRDFSString(typetext);
        }catch (Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);
        }
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
class TypeDefFrame_jbParseTypes_mouseAdapter extends MouseAdapter {
    private TypeDefFrame adaptee;
    TypeDefFrame_jbParseTypes_mouseAdapter(TypeDefFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.jbParseTypes_mouseClicked(e);
    }
}

 /**
  * This class implements a ExitActionTDF which is used by the Menu bar in the
  * TypeDefFrame.
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
        class ExitActionTDF extends AbstractAction
        {
                /**
                 * This is the contructor for a ExitAction.
                 * It calls the contructor for a AbstractAction.
                 * @param String name - the name for the action
                 */
                
                ExitActionTDF(String name)
                {
                        super(name);
                }

                /**
                 * This method is called when a ExitAction is performed
                 * When an exitAction is performed it will exit the program.
                 * @param event - the event that occured
                 */

                public void actionPerformed(ActionEvent event)
                {
                        System.exit(0);
                        
                }
        }//ExitAction
 
 /**
  * This class implements a OpenActionTDF which is used by the Menu bar in the
  * TypeDefFrame.
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
        class OpenActionTDF extends AbstractAction
        {
                /**
                 * This is the contructor for a OpenAction.
                 * It calls the contructor for a AbstractAction.
                 * @param String name - the name for the action
                 */
                
                OpenActionTDF(String name)
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
                        TypeDefFrame.openFile();
                }
        }//Open Action
