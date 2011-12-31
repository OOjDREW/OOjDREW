// OO jDREW - An Object Oriented extension of the Java Deductive Reasoning Engine for the Web
// Copyright (C) 2008 Ben Craig
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

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.ruleml.oojdrew.SyntaxFormat;
import org.ruleml.oojdrew.BottomUp.BottomUpApp;

public class BottomUpUI implements UI {

	private final JFrame frmOoJdrew = new JFrame();
	private final DefaultReasonerMenu menuBar = new DefaultReasonerMenu();
	private final ButtonGroup typeDefinitionButtonGroup = new ButtonGroup();
	private final ButtonGroup knowledgeBaseButtonGroup = new ButtonGroup();
	private final ButtonGroup outputFormatButtonGroup = new ButtonGroup();
	private UndoRedoTextArea typeDefinitionTextArea;
	private UndoRedoTextArea knowledgeBaseTextArea;
    private UndoRedoTextArea outputTextArea;
    private JPanel typeDefinitonTab;
    private JPanel knowledgeBaseTab;
    private JTabbedPane tabbedPane;
    private JRadioButton typeDefinitionFormatRDFS;
    private JRadioButton knowledgeBaseInputFormatRuleML;
    private JRadioButton outputFormatRuleML;
    private JCheckBox chkBoxPrintRules;
    private JCheckBox chkBoxSeparateFacts;
    private JFormattedTextField tfInputLoopCounter;
    
    // UI controller class
    private BottomUpApp controller;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BottomUpUI window = new BottomUpUI();
					window.frmOoJdrew.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public BottomUpUI() {
		initialize();
        updateUI();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {		
		frmOoJdrew.setTitle("OO jDREW");
        frmOoJdrew.setBounds(100, 100, 661, 700);
		frmOoJdrew.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 
		frmOoJdrew.setJMenuBar(menuBar);
		
	    JMenu chckbxmntmTestForStratification = new JMenu("Test Knowledgebase for Stratification");
		chckbxmntmTestForStratification.setToolTipText("Checks if the knowledgebase is stratifiable");
		chckbxmntmTestForStratification.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.checkStratificiation();
            }
        });
		menuBar.addRunMenu(chckbxmntmTestForStratification);
		
		frmOoJdrew.getContentPane().setLayout(new BorderLayout(0, 0));
			
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frmOoJdrew.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		typeDefinitonTab = new JPanel();
		tabbedPane.addTab("Type definition", null, typeDefinitonTab, null);
		
		JButton btnLoadTypeInformation = new JButton("Load type information");
		btnLoadTypeInformation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.parseTypeInformation();
			}
		});
		
		JLabel typeDefinitionFormatLabel = new JLabel("Input format:");
		
		typeDefinitionFormatRDFS = new JRadioButton("RDFS");
		typeDefinitionButtonGroup.add(typeDefinitionFormatRDFS);
		
		JRadioButton typeDefinitionFormatPOSL = new JRadioButton("POSL");
		typeDefinitionFormatPOSL.setSelected(true);
		typeDefinitionButtonGroup.add(typeDefinitionFormatPOSL);
		
		JScrollPane typeDefinitionScrollPane = new JScrollPane();
		GroupLayout gl_typeDefinitonTab = new GroupLayout(typeDefinitonTab);
		gl_typeDefinitonTab.setHorizontalGroup(
			gl_typeDefinitonTab.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_typeDefinitonTab.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_typeDefinitonTab.createParallelGroup(Alignment.LEADING)
						.addComponent(typeDefinitionScrollPane, GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
						.addGroup(gl_typeDefinitonTab.createSequentialGroup()
							.addComponent(typeDefinitionFormatLabel)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(typeDefinitionFormatRDFS)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(typeDefinitionFormatPOSL)
							.addPreferredGap(ComponentPlacement.RELATED, 231, Short.MAX_VALUE)
							.addComponent(btnLoadTypeInformation)))
					.addContainerGap())
		);
		gl_typeDefinitonTab.setVerticalGroup(
			gl_typeDefinitonTab.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_typeDefinitonTab.createSequentialGroup()
					.addContainerGap()
					.addComponent(typeDefinitionScrollPane, GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_typeDefinitonTab.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnLoadTypeInformation)
						.addComponent(typeDefinitionFormatLabel)
						.addComponent(typeDefinitionFormatRDFS)
						.addComponent(typeDefinitionFormatPOSL))
					.addContainerGap())
		);
		
		typeDefinitionTextArea = new UndoRedoTextArea("");
		typeDefinitionScrollPane.setViewportView(typeDefinitionTextArea);
		typeDefinitonTab.setLayout(gl_typeDefinitonTab);
		
		knowledgeBaseTab = new JPanel();
		tabbedPane.addTab("Knowledge base", null, knowledgeBaseTab, null);
		
		JButton btnParseKnowledgeBase = new JButton("Parse knowledge base");
		btnParseKnowledgeBase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.parseKnowledgeBase();
			}
		});
		
		JLabel knowledgeBaseInputFormatLabel = new JLabel("Input format:");
		
		knowledgeBaseInputFormatRuleML = new JRadioButton("RuleML");
		knowledgeBaseButtonGroup.add(knowledgeBaseInputFormatRuleML);
		
		JRadioButton knowledgeBaseInputFormatPOSL = new JRadioButton("POSL");
		knowledgeBaseInputFormatPOSL.setSelected(true);
		knowledgeBaseButtonGroup.add(knowledgeBaseInputFormatPOSL);
		
		JScrollPane knowledgeBaseScrollPane = new JScrollPane();
		GroupLayout gl_knowledgeBaseTab = new GroupLayout(knowledgeBaseTab);
		gl_knowledgeBaseTab.setHorizontalGroup(
			gl_knowledgeBaseTab.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_knowledgeBaseTab.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_knowledgeBaseTab.createParallelGroup(Alignment.LEADING)
						.addComponent(knowledgeBaseScrollPane, GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
						.addGroup(gl_knowledgeBaseTab.createSequentialGroup()
							.addComponent(knowledgeBaseInputFormatLabel)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(knowledgeBaseInputFormatRuleML)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(knowledgeBaseInputFormatPOSL)
							.addPreferredGap(ComponentPlacement.RELATED, 221, Short.MAX_VALUE)
							.addComponent(btnParseKnowledgeBase)))
					.addContainerGap())
		);
		gl_knowledgeBaseTab.setVerticalGroup(
			gl_knowledgeBaseTab.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_knowledgeBaseTab.createSequentialGroup()
					.addContainerGap()
					.addComponent(knowledgeBaseScrollPane, GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_knowledgeBaseTab.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnParseKnowledgeBase)
						.addComponent(knowledgeBaseInputFormatLabel)
						.addComponent(knowledgeBaseInputFormatRuleML)
						.addComponent(knowledgeBaseInputFormatPOSL))
					.addContainerGap())
		);
		
		knowledgeBaseTextArea = new UndoRedoTextArea("");

		knowledgeBaseScrollPane.setViewportView(knowledgeBaseTextArea);
		knowledgeBaseTab.setLayout(gl_knowledgeBaseTab);
		
		JPanel outputTab = new JPanel();
		tabbedPane.addTab("Output", null, outputTab, null);

        JScrollPane outputScrollPane = new JScrollPane();
        JLabel outputConfigurationLabel = new JLabel("Output:");

        outputFormatRuleML = new JRadioButton("RuleML");
        JRadioButton outputFormatPOSL = new JRadioButton("POSL");
        outputFormatPOSL.setSelected(true);

        outputFormatButtonGroup.add(outputFormatRuleML);
        outputFormatButtonGroup.add(outputFormatPOSL);
		
		chkBoxPrintRules = new JCheckBox("Print Rules");
		
		chkBoxSeparateFacts = new JCheckBox("Separate Facts");
		
		JButton btnRunForwardReasoner = new JButton("Run Forward Reasoner");
		btnRunForwardReasoner.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent arg0) {
		        controller.runForwardReasoner();
		    }
		});
		
		JLabel lblbMaxLoopCount = new JLabel("Max Loop Count");
		tfInputLoopCounter = new JFormattedTextField(0);
		tfInputLoopCounter.setColumns(10);
		
		GroupLayout gl_outputTab = new GroupLayout(outputTab);
		gl_outputTab.setHorizontalGroup(
		    gl_outputTab.createParallelGroup(Alignment.LEADING)
		        .addGroup(gl_outputTab.createSequentialGroup()
		            .addContainerGap()
		            .addGroup(gl_outputTab.createParallelGroup(Alignment.LEADING)
		                .addComponent(outputScrollPane, GroupLayout.DEFAULT_SIZE, 703, Short.MAX_VALUE)
		                .addGroup(gl_outputTab.createSequentialGroup()
		                    .addComponent(outputConfigurationLabel)
		                    .addPreferredGap(ComponentPlacement.UNRELATED)
		                    .addComponent(outputFormatRuleML)
		                    .addPreferredGap(ComponentPlacement.RELATED)
		                    .addComponent(outputFormatPOSL)
		                    .addGap(10)
		                    .addComponent(chkBoxPrintRules)
		                    .addPreferredGap(ComponentPlacement.UNRELATED)
		                    .addComponent(chkBoxSeparateFacts)
		                    .addPreferredGap(ComponentPlacement.UNRELATED)
		                    .addComponent(lblbMaxLoopCount)
		                    .addPreferredGap(ComponentPlacement.RELATED)
		                    .addComponent(tfInputLoopCounter, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
		                    .addGap(84)
		                    .addComponent(btnRunForwardReasoner)))
		            .addContainerGap())
		);
		gl_outputTab.setVerticalGroup(
		    gl_outputTab.createParallelGroup(Alignment.TRAILING)
		        .addGroup(gl_outputTab.createSequentialGroup()
		            .addContainerGap()
		            .addComponent(outputScrollPane, GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addGroup(gl_outputTab.createParallelGroup(Alignment.BASELINE)
		                .addComponent(btnRunForwardReasoner)
		                .addComponent(outputConfigurationLabel)
		                .addComponent(outputFormatRuleML)
		                .addComponent(outputFormatPOSL)
		                .addComponent(lblbMaxLoopCount)
		                .addComponent(tfInputLoopCounter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		                .addComponent(chkBoxSeparateFacts)
		                .addComponent(chkBoxPrintRules))
		            .addContainerGap())
		);
		outputTab.setLayout(gl_outputTab);
		outputTextArea = new UndoRedoTextArea("");
		outputTextArea.setEditable(false);

        outputScrollPane.setViewportView(outputTextArea);
		
		tabbedPane.setSelectedIndex(1);
	}

    public boolean getFrameVisible() {
        return frmOoJdrew.isVisible();
    }

    public void setFrameVisible(boolean visible) {
        frmOoJdrew.setVisible(visible);
    }

    public void setController(AbstractUIApp controller) {
        menuBar.setController(controller);
        this.controller = (BottomUpApp) controller;
        this.controller.syncUIWithSettings();
    }

    public void updateUI() {
        knowledgeBaseTextArea.updateUI();
        typeDefinitionTextArea.updateUI();
        outputTextArea.updateUI();

        SwingUtilities.updateComponentTreeUI(getFrmOoJdrew());
        getFrmOoJdrew().pack();
    }

    public JFrame getFrmOoJdrew() {
        return frmOoJdrew;
    }

    public boolean getChckbxmntmValidateRulemlSelected() {
        return menuBar.getChckbxmntmValidateRulemlSelected();
    }

    public void setChckbxmntmValidateRulemlSelected(boolean selected) {
        menuBar.setChckbxmntmValidateRulemlSelected(selected);
    }

    private EditingTab currentEditingTab() {
        switch (getTabbedPaneSelectedIndex()) {
        case 0:
            return EditingTab.EditingTabTypeDefinition;

        case 1:
            return EditingTab.EditingTabKnowledgeBase;

        case 2:
            return EditingTab.EditingTabOutput;

        default:
            throw new RuntimeException("Unknown tab selected.");
        }
    }

    private void clearCurrentEditingTab() {
        switch (currentEditingTab()) {
        case EditingTabTypeDefinition:
            setTypeDefinitionTextAreaText("");
            break;

        case EditingTabKnowledgeBase:
            setKnowledgeBaseTextAreaText("");
            break;

        case EditingTabOutput:
            setOutputTextAreaText("");
            break;
        }
    }

    public void appendToCurrentEditingTab(String content) {
        StringBuilder stringBuilder = new StringBuilder();

        switch (currentEditingTab()) {
        case EditingTabTypeDefinition:
            stringBuilder.append(getTypeDefinitionTextAreaText());
            stringBuilder.append(content);
            setTypeDefinitionTextAreaText(stringBuilder.toString());
            break;

        case EditingTabKnowledgeBase:
            stringBuilder.append(getKnowledgeBaseTextAreaText());
            stringBuilder.append(content);
            setKnowledgeBaseTextAreaText(stringBuilder.toString());
            break;

        case EditingTabQuery:
            stringBuilder.append(getOutputTextAreaText());
            stringBuilder.append(content);
            setOutputTextAreaText(stringBuilder.toString());
            break;
        }
    }

    public void setTextForCurrentEditingTab(String content) {
        clearCurrentEditingTab();
        appendToCurrentEditingTab(content);
    }

    public String getTextForCurrentEditingTab() {
        String text = "";
        switch (currentEditingTab()) {
        case EditingTabTypeDefinition:
            text = getTypeDefinitionTextAreaText();
            break;

        case EditingTabKnowledgeBase:
            text = getKnowledgeBaseTextAreaText();
            break;

        case EditingTabQuery:
            text = getOutputTextAreaText();
            break;
        }
        return text;
    }

    public String getTypeDefinitionTextAreaText() {
        return typeDefinitionTextArea.getText();
    }

    private void setTypeDefinitionTextAreaText(String text) {
        typeDefinitionTextArea.setText(text);
    }

    public String getKnowledgeBaseTextAreaText() {
        return knowledgeBaseTextArea.getText();
    }

    private void setKnowledgeBaseTextAreaText(String text_1) {
        knowledgeBaseTextArea.setText(text_1);
    }

    public String getOutputTextAreaText() {
        return outputTextArea.getText();
    }

    public void setOutputTextAreaText(String text) {
        outputTextArea.setText(text);
    }

    private int getTabbedPaneSelectedIndex() {
        return tabbedPane.getSelectedIndex();
    }

    public SyntaxFormat getTypeInformationInputFormat() {
        if (typeDefinitionFormatRDFS.isSelected()) {
            return SyntaxFormat.RDFS;
        }

        return SyntaxFormat.POSL;
    }

    public SyntaxFormat getKnowledgeBaseInputFormat() {
        if (knowledgeBaseInputFormatRuleML.isSelected()) {
            return SyntaxFormat.RULEML;
        }

        return SyntaxFormat.POSL;
    }

    public SyntaxFormat getOutputFormat() {
        if (outputFormatRuleML.isSelected()) {
            return SyntaxFormat.RULEML;
        }

        return SyntaxFormat.POSL;
    }

    public boolean getSeparateFactsEnabled() {
        return chkBoxSeparateFacts.isSelected();
    }

    public String getInputLoopCount() {
        return tfInputLoopCounter.getValue().toString();
    }

    public boolean getPrintRulesEnabled() {
        return chkBoxPrintRules.isSelected();
    }
}
