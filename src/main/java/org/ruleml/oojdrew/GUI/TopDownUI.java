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
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.ruleml.oojdrew.TopDown.TopDownApp;
import org.ruleml.oojdrew.parsing.InputFormat;

public class TopDownUI {

	private JFrame frmOoJdrew;
	private final ButtonGroup typeDefinitionButtonGroup = new ButtonGroup();
	private final ButtonGroup knowledgeBaseButtonGroup = new ButtonGroup();
	private JTable variableBindingsTable;
	private final ButtonGroup queryButtonGroup = new ButtonGroup();
	private TopDownApp controller;
	private UndoRedoTextArea typeDefinitionTextArea;
	private UndoRedoTextArea knowledgeBaseTextArea;
	private UndoRedoTextArea queryTextArea;
	private JCheckBoxMenuItem chckbxmntmValidateRuleml;
	private JMenuItem mntmShowDebugConsole;
	private JPanel typeDefinitonTab;
	private JPanel knowledgeBaseTab;
	private JSplitPane queryTab;
	private JTabbedPane tabbedPane;
	private JRadioButton typeDefinitionFormatRDFS;
	private JButton btnNextSolution;
	private JRadioButton knowledgeBaseInputFormatRuleML;
	private JRadioButton queryFormatRuleML;
	private JCheckBox typeQueryCheckbox;
	private JTree solutionTree;
	private UndoRedoTextArea solutionTextArea;
	private JScrollPane solutionTreeScrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TopDownUI window = new TopDownUI();
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
	public TopDownUI() {
		initialize();
        updateUI();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {		
		frmOoJdrew = new JFrame();
		frmOoJdrew.setTitle("OO jDREW");
		frmOoJdrew.setBounds(100, 100, 700, 650);
		frmOoJdrew.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmOoJdrew.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
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
		
		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		chckbxmntmValidateRuleml = new JCheckBoxMenuItem("Validate RuleML");
		chckbxmntmValidateRuleml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.applySettingsFromUI();
			}
		});
		mnOptions.add(chckbxmntmValidateRuleml);
		
		mntmShowDebugConsole = new JMenuItem("Show debug console");
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
		
		typeDefinitionTextArea = new UndoRedoTextArea(new String());
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
		
		knowledgeBaseTextArea = new UndoRedoTextArea(new String());

		knowledgeBaseScrollPane.setViewportView(knowledgeBaseTextArea);
		knowledgeBaseTab.setLayout(gl_knowledgeBaseTab);
		
		queryTab = new JSplitPane();
		queryTab.setOrientation(JSplitPane.VERTICAL_SPLIT);
		tabbedPane.addTab("Query", null, queryTab, null);
		
		JPanel queryTopPanel = new JPanel();
		queryTopPanel.setPreferredSize(new Dimension(10, 150));
		queryTopPanel.setBorder(null);
		queryTab.setLeftComponent(queryTopPanel);
		
		JScrollPane queryScrollPane = new JScrollPane();
		
		btnNextSolution = new JButton("Next solution");
		btnNextSolution.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.nextSolution();
			}
		});
		btnNextSolution.setEnabled(false);
		
		JButton btnIssueQuery = new JButton("Issue query");
		btnIssueQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.issueQuery();
			}
		});
		
		JLabel queryLabel = new JLabel("Query:");
		
		JLabel queryInputFormatLabel = new JLabel("Input format:");
		
		queryFormatRuleML = new JRadioButton("RuleML");
		queryButtonGroup.add(queryFormatRuleML);
		
		JRadioButton queryFormatPOSL = new JRadioButton("POSL");
		queryFormatPOSL.setSelected(true);
		queryButtonGroup.add(queryFormatPOSL);
		
		typeQueryCheckbox = new JCheckBox("Type query");
		typeQueryCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(getTypeQueryCheckboxSelected())
				{
					getSolutionTreeScrollPane().setViewportView(solutionTextArea);
				}
				else
				{
					getSolutionTreeScrollPane().setViewportView(solutionTree);
				}
			}
		});
		GroupLayout gl_queryTopPanel = new GroupLayout(queryTopPanel);
		gl_queryTopPanel.setHorizontalGroup(
			gl_queryTopPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_queryTopPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_queryTopPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(queryLabel)
						.addComponent(queryInputFormatLabel))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_queryTopPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_queryTopPanel.createSequentialGroup()
							.addComponent(queryFormatRuleML)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(queryFormatPOSL)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(typeQueryCheckbox)
							.addPreferredGap(ComponentPlacement.RELATED, 129, Short.MAX_VALUE)
							.addComponent(btnIssueQuery)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNextSolution))
						.addComponent(queryScrollPane, GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_queryTopPanel.setVerticalGroup(
			gl_queryTopPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_queryTopPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_queryTopPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(queryScrollPane)
						.addComponent(queryLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_queryTopPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnNextSolution)
						.addComponent(btnIssueQuery)
						.addComponent(queryInputFormatLabel)
						.addComponent(queryFormatRuleML)
						.addComponent(queryFormatPOSL)
						.addComponent(typeQueryCheckbox))
					.addContainerGap())
		);
		
		queryTextArea = new UndoRedoTextArea(new String());
		queryScrollPane.setViewportView(queryTextArea);
		queryTopPanel.setLayout(gl_queryTopPanel);
		
		MySplitPane queryBottomPanel = new MySplitPane();
		queryBottomPanel.setResizeWeight(0.5);
		queryTab.setRightComponent(queryBottomPanel);
		
		JPanel queryLeftPanel = new JPanel();
		queryLeftPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		queryBottomPanel.setLeftComponent(queryLeftPanel);
		queryLeftPanel.setLayout(new BorderLayout(5, 5));
		
		JLabel lblSolution = new JLabel("Solution:");
		queryLeftPanel.add(lblSolution, BorderLayout.NORTH);
		
		solutionTreeScrollPane = new JScrollPane();
		queryLeftPanel.add(solutionTreeScrollPane, BorderLayout.CENTER);
		
		solutionTree = new JTree();
		solutionTree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("unknown") {
			}
		));		
		solutionTextArea = new UndoRedoTextArea(new String());
		solutionTextArea.setEditable(false);
		solutionTreeScrollPane.setViewportView(solutionTree);
		
		JPanel queryRightPanel = new JPanel();
		queryRightPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		queryBottomPanel.setRightComponent(queryRightPanel);
		queryRightPanel.setLayout(new BorderLayout(5, 5));
		
		JLabel lblVariableBindings = new JLabel("Variable bindings:");
		queryRightPanel.add(lblVariableBindings, BorderLayout.NORTH);
		
		JScrollPane variableBindingsScrollPane = new JScrollPane();
		queryRightPanel.add(variableBindingsScrollPane, BorderLayout.CENTER);
		
		variableBindingsTable = new JTable();
		variableBindingsScrollPane.setViewportView(variableBindingsTable);
		
		tabbedPane.setSelectedIndex(1);
	}

	public boolean getFrameVisible() {
		return frmOoJdrew.isVisible();
	}
	
	public void setFrameVisible(boolean visible) {
		frmOoJdrew.setVisible(visible);
	}
	
	public void setController(TopDownApp newController)
	{
		controller = newController;
		controller.syncUIWithSettings();
	}

	public void updateUI() {
		knowledgeBaseTextArea.updateUI();
		queryTextArea.updateUI();
		typeDefinitionTextArea.updateUI();
		solutionTextArea.updateUI();
		
		SwingUtilities.updateComponentTreeUI(getFrmOoJdrew());
		getFrmOoJdrew().pack();
	}
	
	public JFrame getFrmOoJdrew() {
		return frmOoJdrew;
	}
	
	public boolean getChckbxmntmValidateRulemlSelected() {
		return chckbxmntmValidateRuleml.isSelected();
	}
	
	public void setChckbxmntmValidateRulemlSelected(boolean selected) {
		chckbxmntmValidateRuleml.setSelected(selected);
	}
	
	private EditingTab currentEditingTab()
	{
		switch(getTabbedPaneSelectedIndex())
		{
		case 0:
			return EditingTab.EditingTabTypeDefinition;
			
		case 1:
			return EditingTab.EditingTabKnowledgeBase;
			
		case 2:
			return EditingTab.EditingTabQuery;
			
		default:
			throw new RuntimeException("Unknown tab selected.");
		}
	}
	
	private void clearCurrentEditingTab()
	{
		switch(currentEditingTab())
		{
		case EditingTabTypeDefinition:
			setTypeDefinitionTextAreaText("");
			break;
			
		case EditingTabKnowledgeBase:
			setKnowledgeBaseTextAreaText("");
			break;
			
		case EditingTabQuery:
			setQueryTextAreaText("");
			break;
		}
	}
	
	public void appendToCurrentEditingTab(String content)
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		switch(currentEditingTab())
		{
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
			stringBuilder.append(getQueryTextAreaText());
			stringBuilder.append(content);
			setQueryTextAreaText(stringBuilder.toString());
			break;
		}		
	}
	
	public void setTextForCurrentEditingTab(String content)
	{
		clearCurrentEditingTab();
		appendToCurrentEditingTab(content);
	}
	
	public String getTextForCurrentEditingTab()
	{
		String text = "";
		switch(currentEditingTab())
		{
		case EditingTabTypeDefinition:
			text = getTypeDefinitionTextAreaText();
			break;
			
		case EditingTabKnowledgeBase:
			text = getKnowledgeBaseTextAreaText();
			break;
			
		case EditingTabQuery:
			text = getQueryTextAreaText();
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
	
	public String getQueryTextAreaText() {
		return queryTextArea.getText();
	}
	
	private void setQueryTextAreaText(String text_2) {
		queryTextArea.setText(text_2);
	}
	
	public void setSolutionTextAreaText(String text)
	{
		solutionTextArea.setText(text);
	}
	
	private int getTabbedPaneSelectedIndex() {
		return tabbedPane.getSelectedIndex();
	}
	
	public InputFormat getTypeInformationInputFormat()
	{
		if(getTypeDefinitionFormatRDFSSelected())
		{
			return InputFormat.InputFormatRFDS;
		}
		
		return InputFormat.InputFormatPOSL;
	}
	
	private boolean getTypeDefinitionFormatRDFSSelected() {
		return typeDefinitionFormatRDFS.isSelected();
	}
	
	public void setBtnNextSolutionEnabled(boolean enabled) {
		btnNextSolution.setEnabled(enabled);
	}
	
	private boolean getKnowledgeBaseInputFormatRuleMLSelected() {
		return knowledgeBaseInputFormatRuleML.isSelected();
	}
	
	public InputFormat getKnowledgeBaseInputFormat()
	{
		if(getKnowledgeBaseInputFormatRuleMLSelected())
		{
			return InputFormat.InputFormatRuleML;
		}
		
		return InputFormat.InputFormatPOSL;
	}
	
	private boolean getQueryFormatRuleMLSelected() {
		return queryFormatRuleML.isSelected();
	}
	
	public InputFormat getQueryInputFormat()
	{
		if(getQueryFormatRuleMLSelected())
		{
			return InputFormat.InputFormatRuleML;
		}
		
		return InputFormat.InputFormatPOSL;
	}
	
	public boolean getTypeQueryCheckboxSelected() {
		return typeQueryCheckbox.isSelected();
	}
	
	public TreeModel getSolutionTreeModel() {
		return solutionTree.getModel();
	}
	
	public void setSolutionTreeModel(TreeModel model) {
		solutionTree.setModel(model);
		solutionTree.updateUI();
	}
	
	public TableModel getVariableBindingsTableModel() {
		return variableBindingsTable.getModel();
	}
	
	public void setVariableBindingsTableModel(TableModel model_1) {
		variableBindingsTable.setModel(model_1);
		variableBindingsTable.updateUI();
	}
	
	private JScrollPane getSolutionTreeScrollPane() {
		return solutionTreeScrollPane;
	}
}
