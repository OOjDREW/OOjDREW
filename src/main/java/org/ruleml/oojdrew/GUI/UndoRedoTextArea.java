package org.ruleml.oojdrew.GUI;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

public class UndoRedoTextArea extends JTextArea implements UndoableEditListener, FocusListener, KeyListener
{
    private UndoManager undoManager;
   
    public UndoRedoTextArea(String emptyText)
    {
        super(emptyText);
        
        undoManager = new UndoManager();
        
        getDocument().addUndoableEditListener(this);

        addKeyListener(this);
        addFocusListener(this);
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
}

