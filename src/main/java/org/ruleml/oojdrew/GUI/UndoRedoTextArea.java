package org.ruleml.oojdrew.GUI;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

public class UndoRedoTextArea extends JTextArea implements UndoableEditListener, FocusListener, KeyListener
{
    private UndoManager undoManager;
    private JPopupMenu contextMenu;
   
    public UndoRedoTextArea(String emptyText)
    {
        super(emptyText);
        
        undoManager = new UndoManager();
        getDocument().addUndoableEditListener(this);
        
        contextMenu = new JPopupMenu();
        contextMenu.add(new SelectAction(this));
    	contextMenu.add(new CutAction(this));
        contextMenu.add(new CopyAction(this));
        contextMenu.add(new PasteAction(this));
        contextMenu.addSeparator();
        contextMenu.add(new UndoAction(this, undoManager));
        contextMenu.add(new RedoAction(this, undoManager));
       
        addKeyListener(this);
        addFocusListener(this);
        
        addMouseListener(new MouseAdapter()
		{
            public void mouseClicked(MouseEvent e)
            {
                requestFocus();
                processMouseClick(e);
            }
		});
        
        addMouseListener(new MouseAdapter()
        {

        });
    }
    

    public void undoableEditHappened(UndoableEditEvent e)
    {
        undoManager.addEdit(e.getEdit());
    }
    
    public void keyPressed(KeyEvent e) 
    {
    	int keyCode = e.getKeyCode();
    	
    	// Check if CTRL is pressed
    	if (e.isControlDown())
    	{
            try
            {
            	 // Do undo action using Ctrl + Y
		        if(keyCode == KeyEvent.VK_Z)
		        { 
		        	undoManager.undo();
		        }
		        // Do redo action using Ctrl + Y
		        else if(keyCode == KeyEvent.VK_Y)
		        {                
		        	undoManager.redo();
		        }
	        }
            catch(Exception ex)
            {
            	// Do nothing
            }
    	}
    }
    
    protected void processMouseClick(MouseEvent e)
    {
        // Only interested in the right button
        if(SwingUtilities.isRightMouseButton(e))
        {    
            // Display the menu
            Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), this);
            contextMenu.show(this, pt.x, pt.y);
        }
    }
    
    public void focusGained(FocusEvent fe)
    {
    }

    public void focusLost(FocusEvent fe)
    {
    }

    public void keyReleased(KeyEvent e) 
    {
    }
    
    public void keyTyped(KeyEvent e)
    {
    }
    
    class SelectAction extends AbstractAction
    { 
        JTextArea textArea; 
     
        public SelectAction(JTextArea textArea)
        { 
            super("Select all"); 
            this.textArea = textArea; 
        } 
     
        public void actionPerformed(ActionEvent e)
        { 
        	textArea.selectAll(); 
        } 
     
        public boolean isEnabled()
        { 
            return textArea.isEditable() && textArea.isEnabled();
        }
    }
      
    class CopyAction extends AbstractAction
    { 
        JTextArea textArea; 
     
        public CopyAction(JTextArea textArea){ 
            super("Copy"); 
            this.textArea = textArea; 
        } 
     
        public void actionPerformed(ActionEvent e){ 
        	textArea.copy(); 
        } 
     
        public boolean isEnabled()
        { 
            return textArea.isEditable() && textArea.isEnabled();
        } 
    }
    
    class CutAction extends AbstractAction
    { 
        JTextArea textArea; 
     
        public CutAction(JTextArea textArea)
        { 
            super("Cut"); 
            this.textArea = textArea; 
        } 
     
        public void actionPerformed(ActionEvent e)
        { 
        	textArea.cut(); 
        } 
     
        public boolean isEnabled(){ 
            return textArea.isEditable() && textArea.isEnabled();
        } 
    }
    
    class PasteAction extends AbstractAction
    { 
        JTextArea textArea; 
     
        public PasteAction(JTextArea textArea)
        { 
            super("Paste"); 
            this.textArea = textArea; 
        } 
     
        public void actionPerformed(ActionEvent e)
        { 
        	textArea.paste(); 
        } 
     
        public boolean isEnabled()
        { 
            if (textArea.isEditable() && textArea.isEnabled())
            { 
                Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this); 
                return contents.isDataFlavorSupported(DataFlavor.stringFlavor); 
            }
            else
            {
                return false;
            }
        } 
    }
    
    class UndoAction extends AbstractAction
    { 
        JTextArea textArea; 
        UndoManager undoManager;
     
        public UndoAction(JTextArea textArea, UndoManager undoManager)
        { 
            super("Undo"); 
            this.textArea = textArea;
            this.undoManager = undoManager;
        } 
     
        public void actionPerformed(ActionEvent e)
        { 
        	undoManager.undo(); 
        }
     
        public boolean isEnabled()
        { 
            if (textArea.isEditable() && textArea.isEnabled())
            { 
                return true;
            }
            else
            {
                return false;
            }
        } 
    }
    
    class RedoAction extends AbstractAction
    { 
        JTextArea textArea; 
        UndoManager undoManager;
     
        public RedoAction(JTextArea textArea, UndoManager undoManager)
        { 
            super("Redo"); 
            this.textArea = textArea;
            this.undoManager = undoManager;
        } 
     
        public void actionPerformed(ActionEvent e)
        { 
        	undoManager.redo(); 
        }
     
        public boolean isEnabled()
        { 
            if (textArea.isEditable() && textArea.isEnabled())
            { 
                return true;
            }
            else
            {
                return false;
            }
        } 
    }
}

