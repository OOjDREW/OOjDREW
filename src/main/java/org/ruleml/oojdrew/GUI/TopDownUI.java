package org.ruleml.oojdrew.GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.ruleml.oojdrew.TopDown.TopDownApp;

public class TopDownUI {

	private JFrame frmOoJdrew;
	private final ButtonGroup typeDefinitionButtonGroup = new ButtonGroup();
	private final ButtonGroup knowledgeBaseButtonGroup = new ButtonGroup();
	private JTable variableBindingsTable;
	private final ButtonGroup queryButtonGroup = new ButtonGroup();
	private TopDownApp controller;
	private JTextArea typeDefinitionTextArea;
	private JTextArea knowledgeBaseTextArea;
	private JTextArea queryTextArea;
	private JCheckBoxMenuItem chckbxmntmValidateRuleml;
	private JCheckBoxMenuItem chckbxmntmShowDebugConsole;
	private JPanel typeDefinitonTab;
	private JPanel knowledgeBaseTab;
	private JSplitPane queryTab;

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
		mntmOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.openFile();
			}
		});
		mnFile.add(mntmOpenFile);
		
		JMenuItem mntmOpenUri = new JMenuItem("Open URI...");
		mntmOpenUri.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		mnFile.add(mntmOpenUri);
		
		mnFile.addSeparator();
		
		JMenuItem mntmExit = new JMenuItem("Exit");
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
		
		chckbxmntmShowDebugConsole = new JCheckBoxMenuItem("Show debug console");
		chckbxmntmShowDebugConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.applySettingsFromUI();
			}
		});
		mnOptions.add(chckbxmntmShowDebugConsole);
		
		JMenuItem mntmAdjustFontSize = new JMenuItem("Adjust font size...");
		mntmAdjustFontSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.showFontSizeDialog();
			}
		});
		mnOptions.add(mntmAdjustFontSize);
		frmOoJdrew.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frmOoJdrew.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		typeDefinitonTab = new JPanel();
		tabbedPane.addTab("Type definition", null, typeDefinitonTab, null);
		
		JButton btnLoadTypeInformation = new JButton("Load type information");
		
		JLabel typeDefinitionFormatLabel = new JLabel("Input format:");
		
		JRadioButton typeDefinitionFormatRDFS = new JRadioButton("RDFS");
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
		
		typeDefinitionTextArea = new JTextArea();
		typeDefinitionScrollPane.setViewportView(typeDefinitionTextArea);
		typeDefinitonTab.setLayout(gl_typeDefinitonTab);
		
		knowledgeBaseTab = new JPanel();
		tabbedPane.addTab("Knowledge base", null, knowledgeBaseTab, null);
		tabbedPane.setEnabledAt(1, true);
		
		JButton btnParseKnowledgeBase = new JButton("Parse knowledge base");
		
		JLabel knowledgeBaseInputFormatLabel = new JLabel("Input format:");
		
		JRadioButton knowledgeBaseInputFormatRuleML = new JRadioButton("RuleML");
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
		
		knowledgeBaseTextArea = new JTextArea();
		knowledgeBaseScrollPane.setViewportView(knowledgeBaseTextArea);
		knowledgeBaseTab.setLayout(gl_knowledgeBaseTab);
		
		queryTab = new JSplitPane();
		queryTab.setOrientation(JSplitPane.VERTICAL_SPLIT);
		tabbedPane.addTab("Query", null, queryTab, null);
		
		JPanel queryTopPanel = new JPanel();
		queryTopPanel.setBorder(null);
		queryTab.setLeftComponent(queryTopPanel);
		
		JScrollPane queryScrollPane = new JScrollPane();
		
		JButton btnNextSolution = new JButton("Next solution");
		
		JButton btnIssueQuery = new JButton("Issue query");
		
		JLabel queryLabel = new JLabel("Query:");
		
		JLabel queryInputFormatLabel = new JLabel("Input format:");
		
		JRadioButton queryFormatRuleML = new JRadioButton("RuleML");
		queryButtonGroup.add(queryFormatRuleML);
		
		JRadioButton queryFormatPOSL = new JRadioButton("POSL");
		queryFormatPOSL.setSelected(true);
		queryButtonGroup.add(queryFormatPOSL);
		
		JCheckBox typeQueryCheckbox = new JCheckBox("Type query");
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
		
		queryTextArea = new JTextArea();
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
		
		JScrollPane solutionTreeScrollPane = new JScrollPane();
		queryLeftPanel.add(solutionTreeScrollPane, BorderLayout.CENTER);
		
		JTree solutionTree = new JTree();
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
		SwingUtilities.updateComponentTreeUI(getFrmOoJdrew());
		getFrmOoJdrew().pack();
	}
	
	// TODO: delete these three methods
	private JTextArea getTypeDefinitionTextArea() {
		return typeDefinitionTextArea;
	}
	
	private JTextArea getKnowledgeBaseTextArea() {
		return knowledgeBaseTextArea;
	}
	
	private JTextArea getQueryTextArea() {
		return queryTextArea;
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
	
	public boolean getChckbxmntmShowDebugConsoleSelected() {
		return chckbxmntmShowDebugConsole.isSelected();
	}
	
	public void setChckbxmntmShowDebugConsoleSelected(boolean selected_1) {
		chckbxmntmShowDebugConsole.setSelected(selected_1);
	}
	
	private EditingTab currentEditingTab()
	{
		if(getTypeDefinitonTab().isEnabled())
		{
			return EditingTab.EditingTabTypeDefinition;
		}
		
		if(getKnowledgeBaseTab().isEnabled())
		{
			return EditingTab.EditingTabKnowledgeBase;
		}
		
		if(getQueryTab().isEnabled())
		{
			return EditingTab.EditingTabQuery;
		}
		
		throw new RuntimeException("Unknown tab selected.");
	}
	
	private JPanel getTypeDefinitonTab() {
		return typeDefinitonTab;
	}
	
	private JPanel getKnowledgeBaseTab() {
		return knowledgeBaseTab;
	}
	
	private JSplitPane getQueryTab() {
		return queryTab;
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
}
