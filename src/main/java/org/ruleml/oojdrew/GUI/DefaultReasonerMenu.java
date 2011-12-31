// OO jDREW - An Object Oriented extension of the Java Deductive Reasoning Engine for the Web
// Copyright (C) 2011
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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class DefaultReasonerMenu extends JMenuBar {
    
    private JMenu mnFile;
    private JMenu mnOptions;
    private JMenu mnRun;

    // UI controller class
    private AbstractUIApp controller;
    
    public DefaultReasonerMenu() {
        super();
        
        populateMenuBar();
    }
    
    private void populateMenuBar() {
        mnFile = new JMenu("File");
        mnOptions = new JMenu("Options");
        mnRun = new JMenu("Run");
        
        populateFileMenu();
        populateOptionsMenu();
        populateRunMenu();
        
        this.add(mnFile);
        this.add(mnOptions);
        this.add(mnRun);
    }
    
    public void addToOptionMenu(Component component) {
        mnOptions.add(component);
    }
    
    public void addToRunMenu(Component component) {
        mnRun.add(component);
    }
    
    private void populateFileMenu() {
        
        JMenuItem mntmOpenFile = new JMenuItem("Open file...");
        mntmOpenFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        mntmOpenFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.openFile();
            }
        });
        mnFile.add(mntmOpenFile);
        
        JMenuItem mntmOpenUri = new JMenuItem("Open URI...");
        mntmOpenUri.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.openURI();
            }
        });
        mnFile.add(mntmOpenUri);
        
        JMenuItem mntmSaveAs = new JMenuItem("Save as...");
        mntmSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        mntmSaveAs.setMnemonic(KeyEvent.VK_S);
        mntmSaveAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.saveFileAs();
            }
        });
        mnFile.add(mntmSaveAs);
        
        mnFile.addSeparator();
        
        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
        mntmExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        mnFile.add(mntmExit);
    }
    
    private void populateOptionsMenu() {         
        JMenuItem mntmShowDebugConsole = new JMenuItem("Show debug console");
        mntmShowDebugConsole.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.showDebugConsole();
            }
        });
        mnOptions.add(mntmShowDebugConsole);
        
        JMenuItem mntmPreferences = new JMenuItem("Preferences...");
        mntmPreferences.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
        mntmPreferences.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.showPreferenceDialog();
            }
        });
        mnOptions.add(mntmPreferences);
    }
    
    private void populateRunMenu() { 
        JMenuItem mnRmlValidator = new JMenuItem("RuleML (XML) validator");
        mnRmlValidator.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
            }
        });
        mnRun.add(mnRmlValidator);
        
        JMenuItem mnRmlNormalizer = new JMenuItem("RuleML (XSLT) normalizer");
        mnRmlNormalizer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
            }
        });
        mnRun.add(mnRmlNormalizer);
    }
    
    public void setController(AbstractUIApp controller) {
        this.controller = controller;
    }
}
