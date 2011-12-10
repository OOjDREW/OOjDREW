package org.ruleml.oojdrew.TopDown;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import nu.xom.Elements;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.ruleml.oojdrew.Config;
import org.ruleml.oojdrew.Configuration;
import org.ruleml.oojdrew.GUI.DebugConsole;
import org.ruleml.oojdrew.GUI.PreferenceDialogUI;
import org.ruleml.oojdrew.GUI.PreferenceManager;
import org.ruleml.oojdrew.GUI.TextPaneAppender;
import org.ruleml.oojdrew.GUI.TopDownUI;
import org.ruleml.oojdrew.GUI.UISettingsController;
import org.ruleml.oojdrew.parsing.InputFormat;
import org.ruleml.oojdrew.parsing.POSLParser;
import org.ruleml.oojdrew.parsing.RDFSParser;
import org.ruleml.oojdrew.parsing.RuleMLParser;
import org.ruleml.oojdrew.parsing.RuleMLParser.RuleMLFormat;
import org.ruleml.oojdrew.parsing.SubsumesParser;
import org.ruleml.oojdrew.parsing.TypeQueryParserPOSL;
import org.ruleml.oojdrew.parsing.TypeQueryParserRuleML;
import org.ruleml.oojdrew.util.DefiniteClause;
import org.ruleml.oojdrew.util.LUBGLBStructure;
import org.ruleml.oojdrew.util.QueryTypes;
import org.ruleml.oojdrew.util.SubsumesStructure;
import org.ruleml.oojdrew.util.SymbolTable;
import org.ruleml.oojdrew.util.Term;
import org.ruleml.oojdrew.util.Types;

public class TopDownApp implements UISettingsController,
		PreferenceChangeListener {
	private Configuration config;
	private TopDownUI ui;
	private PreferenceDialogUI preferenceDialogUI;
	private DebugConsole debugConsole;
	private PreferenceManager preferenceManager;
	private Logger logger;
	private RDFSParser rdfsParser;
	private POSLParser poslParser;
	private RuleMLParser rmlParser;
	private SubsumesParser subsumesParser;
	private BackwardReasoner backwardReasoner;

	// TODO: Rewrite all code that uses the following variables
	// These variables were copied from the old UI
	private Iterator solit;
	private Iterator it;
	private boolean t1Var;
	private boolean t2Var;
	private String term1VarName;
	private String term2VarName;
	private SubsumesStructure subPlus;
	private SubsumesStructure sub;
	private LUBGLBStructure lub;
	private LUBGLBStructure glb;

	public static void main(String[] args) {
		// The look and feel must be set before any UI objects are constructed
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Create a TopDownApp using the factory method...
		TopDownApp app = TopDownApp.getTopDownApp();

		// ... and start it's event loop
		app.run();
	}

	public void run() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				logger.debug("Entering event loop");
				ui.setFrameVisible(true);
			}
		});
	}

	public static TopDownApp getTopDownApp() {
		// Construct dependencies
		Configuration config = new Config();
		PreferenceManager preferenceManager = new PreferenceManager(config);
		TopDownUI topDownUI = new TopDownUI();
		PreferenceDialogUI preferenceDialogUI = new PreferenceDialogUI();

		// Create DebugConsole and logger
		DebugConsole debugConsole = new DebugConsole();

		BasicConfigurator.configure();
		Logger root = Logger.getRootLogger();
		root.setLevel(Level.DEBUG);
		TextPaneAppender tpa = new TextPaneAppender(new PatternLayout(
				"%-5p %d [%t]:  %m%n"), "Debug");
		tpa.setTextPane(debugConsole.getTextPane());
		root.addAppender(tpa);

		// Create the parsers
		RDFSParser rdfsParser = new RDFSParser();
		POSLParser poslParser = new POSLParser();
		RuleMLParser rmlParser = new RuleMLParser(config);
		SubsumesParser subsumesParser = new SubsumesParser();

		// Create the reasoning engine
		BackwardReasoner backwardReasoner = new BackwardReasoner();

		// Create TopDownApp
		TopDownApp topDownApp = new TopDownApp(config, preferenceManager,
				topDownUI, preferenceDialogUI, debugConsole, rdfsParser,
				poslParser, rmlParser, subsumesParser, backwardReasoner);

		return topDownApp;
	}

	private TopDownApp(Configuration config, PreferenceManager fontSizeManager,
			TopDownUI ui, PreferenceDialogUI fontSizeDialogUI,
			DebugConsole debugConsole, RDFSParser rdfsParser,
			POSLParser poslParser, RuleMLParser rmlParser,
			SubsumesParser subsumesParser, BackwardReasoner backwardReasoner) {
		this.config = config;
		this.preferenceManager = fontSizeManager;
		this.ui = ui;
		this.preferenceDialogUI = fontSizeDialogUI;
		this.debugConsole = debugConsole;
		this.logger = Logger.getLogger(this.getClass());
		this.rdfsParser = rdfsParser;
		this.poslParser = poslParser;
		this.rmlParser = rmlParser;
		this.subsumesParser = subsumesParser;
		this.backwardReasoner = backwardReasoner;

		ui.setController(this);
		fontSizeDialogUI.setSettingsController(this);
		config.addPreferenceChangeListener(this);
		preferenceChange(null);
	}

	public void syncUIWithSettings() {
		preferenceDialogUI.setSpinnerTextAreaFontSizeValue(config
				.getTextAreaFontSize());
		preferenceDialogUI.setSpinnerUIFontSizeValue(config.getUIFontSize());
		preferenceDialogUI.setLinkFontSizes(config.getLinkFontSizes());
		preferenceDialogUI.setLookAndFeel(config.getSelectedLookAndFeel());
		preferenceDialogUI.setSelectedRuleMLFormat(config.getSelectedRuleMLFormat());
		
		ui.setChckbxmntmValidateRulemlSelected(config
				.getValidateRuleMLEnabled());
	}

	public void applySettingsFromUI() {
		config.setTextAreaFontSize(preferenceDialogUI
				.getSpinnerTextAreaFontSizeValue());
		config.setUIFontSize(preferenceDialogUI.getSpinnerUIFontSizeValue());
		config.setLinkFontSizes(preferenceDialogUI.getLinkFontSizes());
		config.setLookAndFeel(preferenceDialogUI.getSelectedLookAndFeel());
		config.setSelectedRuleMLFormat(preferenceDialogUI.getSelectedRuleMLFormat());
		config.setValidateRuleMLEnabled(ui
				.getChckbxmntmValidateRulemlSelected());
	}
	
	public void showDebugConsole()
	{
		debugConsole.setVisible(true);
	}
	
	public void showPreferenceDialog() {
		preferenceDialogUI.setVisible(true);
	}

	public void preferenceChange(PreferenceChangeEvent evt) {
		ui.updateUI();
		preferenceDialogUI.updateUI();
	}

	private boolean showOpenForAppendDialog() {
		return 0 == JOptionPane.showConfirmDialog(null, "Append content?",
				"Append or replace?", JOptionPane.YES_NO_OPTION);
	}

	public void openFile() {
		boolean append = showOpenForAppendDialog();

		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(ui.getFrmOoJdrew());

		if (result != JFileChooser.APPROVE_OPTION) {
			return;
		}

		String fileContents;

		try {
			File file = fileChooser.getSelectedFile();
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			StringBuilder stringBuilder = new StringBuilder();
			String lineSeparator = System.getProperty("line.separator");
			String currentLine;

			while ((currentLine = bufferedReader.readLine()) != null) {
				stringBuilder.append(currentLine);
				stringBuilder.append(lineSeparator);
			}

			fileContents = stringBuilder.toString();
			bufferedReader.close();
		} catch (IOException e) {
			defaultExceptionHandler(e);
			return;
		}

		if (append) {
			ui.appendToCurrentEditingTab(fileContents);
		} else {
			ui.setTextForCurrentEditingTab(fileContents);
		}
	}

	/**
	 * Shows open URI dialog and reads from URL to editing tab
	 */
	public void openURI() {
		boolean append = showOpenForAppendDialog();
		String uri = JOptionPane.showInputDialog("Please enter an URI");
		
		if (uri == null)
		{
			return;
		}
		
		// Set connection timeout [ms] for HTTP connection
		int connectionTimeoutMillis = 3000;
		
		StringBuffer buffer = new StringBuffer();
		try {
			// Create new HTTP connection with given timeout
			URL url = new URL(uri);
			URLConnection urlConnection = url.openConnection();	
			urlConnection.setConnectTimeout(connectionTimeoutMillis);
			
			// Open input stream for reading
			InputStream inputStream = urlConnection.getInputStream();
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream));
			
			// Read input stream to string buffer
	        String inputLine;
	        String newLine = System.getProperty("line.separator");
	        while ((inputLine = streamReader.readLine()) != null) 
	        {
	        	buffer.append(inputLine);
	        	buffer.append(newLine);
	        }
	        
	        // Close streams
	        streamReader.close();
		} catch (Exception e) {
			defaultExceptionHandler(e);
			return;
		}
		
		if (append) {
			ui.appendToCurrentEditingTab(buffer.toString());
		} else {
			ui.setTextForCurrentEditingTab(buffer.toString());
		}
	}
	
	public void saveFileAs()
	{
		JFileChooser fileChooser = new JFileChooser()
		{
			@Override
			public void approveSelection()
			{
			    File f = getSelectedFile();
			    if(f.exists() && getDialogType() == SAVE_DIALOG)
			    {
			        int result = JOptionPane.showConfirmDialog(this,"File already exists, overwrite?","Existing file",JOptionPane.YES_NO_OPTION);
			        switch(result)
			        {
			            case JOptionPane.YES_OPTION:
			                super.approveSelection();
			                return;
			            case JOptionPane.NO_OPTION:
			                return;
			        }
			    }
			    super.approveSelection();
			}
		};
		
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result = fileChooser.showSaveDialog(ui.getFrmOoJdrew());

		if (result != JFileChooser.APPROVE_OPTION) 
		{
			return;
		}

		try
		{
			File file = fileChooser.getSelectedFile();
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			String textToWrite = ui.getTextForCurrentEditingTab();

			bufferedWriter.write(textToWrite);
			bufferedWriter.close();
		} 
		catch (IOException e) 
		{
			defaultExceptionHandler(e);
			return;
		}
	}

	public void parseTypeInformation() {
		String typeInformation = ui.getTypeDefinitionTextAreaText();
		InputFormat format = ui.getTypeInformationInputFormat();

		// Reset the type system
		Types.reset();

		if (format == InputFormat.InputFormatRFDS) {
			parseRDFSTypes(typeInformation);
		} else {
			parsePOSLTypes(typeInformation);
		}

		// Type information may have changed, time to parse the knowledge base
		// again.
		parseKnowledgeBase();
	}

	private void parseRDFSTypes(String typeInformation) {
		try {
			RDFSParser.parseRDFSString(typeInformation);
		} catch (Exception e) {
			defaultExceptionHandler(e);
			return;
		}
	}

	private void parsePOSLTypes(String typeInformation) {
		try {
			subsumesParser.parseSubsumes(typeInformation);
		} catch (Exception e) {
			defaultExceptionHandler(e);
			return;
		}
	}

	public void parseKnowledgeBase() {
		SymbolTable.reset();
		ui.setBtnNextSolutionEnabled(false);

		InputFormat knowledgeBaseFormat = ui.getKnowledgeBaseInputFormat();
		String knowledgeBase = ui.getKnowledgeBaseTextAreaText();
		backwardReasoner.clearClauses();

		if (knowledgeBase.isEmpty()) {
			return;
		}

		if (knowledgeBaseFormat == InputFormat.InputFormatRuleML) {
			parseRuleMLKnowledeBase(knowledgeBase);
		} else {
			parsePOSLKnowledgeBase(knowledgeBase);
		}

	}

	private void parseRuleMLKnowledeBase(String knowledgeBase) {
		rmlParser.clear();

		try {
			rmlParser.parseRuleMLString(RuleMLFormat.RuleML100, knowledgeBase);
		} catch (Exception e) {
			defaultExceptionHandler(e);
			return;
		}

		backwardReasoner.loadClauses(rmlParser.iterator());
	}

	private void parsePOSLKnowledgeBase(String knowledgeBase) {
		poslParser.reset();

		try {
			poslParser.parseDefiniteClauses(knowledgeBase);
		} catch (Exception e) {
			defaultExceptionHandler(e);
			return;
		}

		backwardReasoner.loadClauses(poslParser.iterator());
	}

	private void defaultExceptionHandler(Exception e) {
		String msg = String.format("Unknown error occured (%s)", e.getClass().getName());
		if (e.getMessage() != null) {
			msg = e.getMessage();
		}
		else if (e.getCause() != null && e.getCause().getMessage() != null)
		{
			msg = e.getCause().getMessage();
		}
		
		JOptionPane.showMessageDialog(ui.getFrmOoJdrew(), msg,
				"Error", JOptionPane.ERROR_MESSAGE);
		logger.error(msg);
	}

	public void issueQuery() {
		String query = ui.getQueryTextAreaText();
		InputFormat format = ui.getQueryInputFormat();
		boolean typeQuery = ui.getTypeQueryCheckboxSelected();

		if (format == InputFormat.InputFormatRuleML) {
			if (typeQuery) {
				issueRuleMLTypeQuery(query);
			} else {
				issueRuleMLQuery(query);
			}
		} else {
			if (typeQuery) {
				issuePOSLTypeQuery(query);
			} else {
				issuePOSLQuery(query);
			}
		}
	}

	private void issueRuleMLQuery(String query) {
		rmlParser.clear();
		
		try {
			DefiniteClause dc = rmlParser.parseRuleMLQuery(query);
			processQuery(dc);
		} catch (Exception e) {
			defaultExceptionHandler(e);
		}
	}

	private void issuePOSLQuery(String query) {
		DefiniteClause dc;
		try {
			dc = poslParser.parseQueryString(query);
			processQuery(dc);
		} catch (Exception e) {
			defaultExceptionHandler(e);
		}
	}

	// TODO: This method was copied from the old GUI and has been modified to
	// work with the current code base. This code should be rewritten in a much
	// cleaner fashion.
	private void processQuery(DefiniteClause dc) {
		// TODO: Find a way to use the existing backwardReasoner (for the sake
		// of dependency injection)
		backwardReasoner = new BackwardReasoner(backwardReasoner.clauses,
				backwardReasoner.oids);

		solit = backwardReasoner.iterativeDepthFirstSolutionIterator(dc);
		ui.setBtnNextSolutionEnabled(true);

		if (!solit.hasNext()) {
			javax.swing.tree.DefaultMutableTreeNode root = new DefaultMutableTreeNode(
					"unknown");
			javax.swing.tree.DefaultTreeModel dtm = new DefaultTreeModel(root);

			ui.setSolutionTreeModel(dtm);
			ui.setBtnNextSolutionEnabled(false);

			ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
					new Object[][] { { null, null } }, new String[] {
							"Variable", "Binding" }));

		} else {
			BackwardReasoner.GoalList gl = (BackwardReasoner.GoalList) solit
					.next();

			Hashtable varbind = gl.varBindings;

			javax.swing.tree.DefaultMutableTreeNode root = backwardReasoner
					.toTree();

			root.setAllowsChildren(true);

			javax.swing.tree.DefaultTreeModel dtm = new DefaultTreeModel(root);

			ui.setSolutionTreeModel(dtm);

			int i = 0;
			Object[][] rowdata = new Object[varbind.size()][2];

			Enumeration e = varbind.keys();

			while (e.hasMoreElements()) {
				Object k = e.nextElement();
				Object val = varbind.get(k);
				String ks = (String) k;
				rowdata[i][0] = ks;
				rowdata[i][1] = val;
				i++;
			}
			String[] colnames = new String[] { "Variable", "Binding" };

			ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
					rowdata, colnames));
		}

		if (!solit.hasNext()) {
			ui.setBtnNextSolutionEnabled(false);
		}
	}

	// TODO: This method was copied from the old GUI and has been modified to
	// work with the current code base. This code should be rewritten in a much
	// cleaner fashion.
	
	public void nextSolution()
	{
		boolean typeQuery = ui.getTypeQueryCheckboxSelected();
		
		if(typeQuery)
		{
			nextSolutionForTypeQuery();
		}
		else
		{
			nextSolutionForQuery();			
		}
	}
	
	public void nextSolutionForQuery() {
		BackwardReasoner.GoalList gl = (BackwardReasoner.GoalList) solit.next();
		// System.out.println(gl.toString());
		Hashtable varbind = gl.varBindings;
		javax.swing.tree.DefaultMutableTreeNode root = backwardReasoner
				.toTree();
		javax.swing.tree.DefaultTreeModel dtm = new DefaultTreeModel(root);

		// logger.debug("Getting next solution: ");

		ui.setSolutionTreeModel(dtm);

		int i = 0;
		Object[][] rowdata = new Object[varbind.size()][2];
		Enumeration e = varbind.keys();
		while (e.hasMoreElements()) {
			Object k = e.nextElement();
			Object val = varbind.get(k);
			String ks = (String) k;
			rowdata[i][0] = ks;
			rowdata[i][1] = val;

			i++;
		}
		String[] colnames = new String[] { "Variable", "Binding" };

		ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
				rowdata, colnames));

		if (!solit.hasNext()) {
			ui.setBtnNextSolutionEnabled(false);
		}
	}

	// TODO: This method was copied from the old GUI and has been modified to
	// work with the current code base. This code should be rewritten in a much
	// cleaner fashion.
	public void nextSolutionForTypeQuery()
	{
		//Var Ind
		if(t1Var == true && t2Var == false){
					
			Object[][] rowdata = new Object[2][2];

       		rowdata[0][0] = "?" + term1VarName;
       	 	rowdata[0][1] = (String)it.next();
            
        	String[] colnames = new String[] {"Variable", "Binding"};

        	ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(rowdata, colnames));
		}
		//Ind Var
		if(t1Var == false && t2Var == true){
					
			Object[][] rowdata = new Object[2][2];

       		rowdata[0][0] = "?" + term2VarName;
       	 	rowdata[0][1] = (String)it.next();
            
        	String[] colnames = new String[] {"Variable", "Binding"};

        	ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(rowdata, colnames));
		}		
		//Var Var
		if(t1Var == true && t2Var == true){
					
			Object[][] rowdata = new Object[2][2];

       		rowdata[0][0] = "?" + term1VarName;
       	 	rowdata[0][1] = (String)it.next();
            
            rowdata[1][0] = "?" + term2VarName;
            rowdata[1][1] = (String)it.next();
            
        	String[] colnames = new String[] {"Variable", "Binding"};

        	ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(rowdata, colnames));
		}
		
		if(!it.hasNext())
		{
			ui.setBtnNextSolutionEnabled(false);
		}
	}
	
	// TODO: This method was copied from the old GUI and has been modified to
	// work with the current code base. This code should be rewritten in a much
	// cleaner fashion.
	private void issueRuleMLTypeQuery(String query) {
		Object[][] resetRow = new Object[2][2];
		String[] resetCol = new String[] { "Variable", "Binding" };

		ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
				resetRow, resetCol));

		ui.setBtnNextSolutionEnabled(false);

		// It is an iterator that is used to map all the solutions to bindings
		it = null;
		// Creating a QueryTypes objects
		QueryTypes typeQuery = new QueryTypes();

		if (query.equals("")) {
			return;
		}

		try {

			// need to get rid of this eventually
			t1Var = false;
			t2Var = false;
			term1VarName = "";
			term2VarName = "";

			ui.setSolutionTextAreaText("");
			TypeQueryParserRuleML rmlTParser = new TypeQueryParserRuleML(query);
			Elements elements = rmlTParser.parseForPredicate();

			String predicate = rmlTParser.getPredicate();

			if (predicate.equalsIgnoreCase(TypeQueryParserRuleML.SUBSUMESPLUS)) {

				subPlus = rmlTParser
						.parseElementsSubsumesAndSubsumesPlus(elements);

				// rel rel
				if (!subPlus.getSuperVar() && !subPlus.getSubVar()) {
					ui.setSolutionTextAreaText(""
							+ typeQuery.isSuperClass(subPlus.getSuperName(),
									subPlus.getSubName()));
					// var rel get all super classes
				} else if (subPlus.getSuperVar() && !subPlus.getSubVar()) {
					t1Var = true;
					term1VarName = subPlus.getSuperName();

					String[] superClasses = typeQuery
							.findAllSuperClasses(subPlus.getSubName());

					Object[][] rowdata = new Object[2][2];
					rowdata[0][0] = "?" + subPlus.getSuperName();
					rowdata[0][1] = superClasses[0];
					String[] colnames = new String[] { "Variable", "Binding" };
					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));

					Vector<String> nextVector = new Vector<String>();
					for (int i = 1; i < superClasses.length; i++)
						nextVector.add(superClasses[i]);

					it = nextVector.iterator();

					if (it.hasNext()) {
						ui.setBtnNextSolutionEnabled(true);
					}

					// rel var get all sub classes
				} else if (!subPlus.getSuperVar() && subPlus.getSubVar()) {
					t2Var = true;
					term2VarName = subPlus.getSubName();
					String[] subClasses = typeQuery.findAllSubClasses(subPlus
							.getSuperName());

					for (int i = 0; i < subClasses.length; i++)
						System.out.println(subClasses[i]);

					Object[][] rowdata = new Object[2][2];

					rowdata[0][0] = "?" + subPlus.getSubName();
					rowdata[0][1] = subClasses[0];

					String[] colnames = new String[] { "Variable", "Binding" };

					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));

					Vector nextVector = new Vector();
					for (int i = 1; i < subClasses.length; i++)
						nextVector.add(subClasses[i]);

					it = nextVector.iterator();

					if (it.hasNext()) {
						ui.setBtnNextSolutionEnabled(true);
					}

					// var var get all relations
				} else if (subPlus.getSuperVar() && subPlus.getSubVar()) {
					t1Var = true;
					t2Var = true;
					term2VarName = subPlus.getSubName();
					term1VarName = subPlus.getSuperName();

					if (subPlus.getSuperName().equalsIgnoreCase(
							subPlus.getSubName())) {
						JOptionPane.showMessageDialog(ui.getFrmOoJdrew(),
								"Duplicate variable names not allowed",
								"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					Vector v1 = typeQuery.findAllSuperClassesOfEverything();
					Vector v2 = typeQuery.findAllSubClassesOfEverything();
					String sol = "";
					Iterator vit1 = v1.iterator();
					Iterator vit2 = v2.iterator();
					int count = 0;
					// Debug -> Prints out all the solutions for easy Copy and
					// Paste
					sol = "% Taxonomy Facts: \n";
					while (vit1.hasNext()) {
						count++;
						sol = sol + "subsumes(" + vit1.next().toString() + ","
								+ vit1.next().toString() + ")." + "\n";
					}
					ui.setSolutionTextAreaText(sol);
					// Debug

					it = v1.iterator();

					Object[][] rowdata = new Object[2][2];

					rowdata[0][0] = "?" + subPlus.getSuperName();
					rowdata[0][1] = (String) it.next();

					rowdata[1][0] = "?" + subPlus.getSubName();
					rowdata[1][1] = (String) it.next();

					String[] colnames = new String[] { "Variable", "Binding" };

					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));

					if (it.hasNext()) {
						ui.setBtnNextSolutionEnabled(true);
					}

				}
			} else if (predicate
					.equalsIgnoreCase(TypeQueryParserRuleML.SUBSUMES)) {
				sub = rmlTParser.parseElementsSubsumesAndSubsumesPlus(elements);
				// rel rel
				if (!sub.getSuperVar() && !sub.getSubVar()) {
					ui.setSolutionTextAreaText(""
							+ typeQuery.isDirectSuperClass(sub.getSuperName(),
									sub.getSubName()));
					// var rel
				} else if (sub.getSuperVar() && !sub.getSubVar()) {
					t1Var = true;
					term1VarName = sub.getSuperName();

					String[] superClasses = typeQuery.getDirectSuperClasses(sub
							.getSubName());

					for (int i = 0; i < superClasses.length; i++)
						System.out.println(superClasses[i]);

					Object[][] rowdata = new Object[2][2];

					rowdata[0][0] = "?" + sub.getSuperName();
					rowdata[0][1] = superClasses[0];

					String[] colnames = new String[] { "Variable", "Binding" };

					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));
					Vector nextVector = new Vector();
					for (int i = 1; i < superClasses.length; i++)
						nextVector.add(superClasses[i]);

					it = nextVector.iterator();

					if (it.hasNext()) {
						ui.setBtnNextSolutionEnabled(true);
					}

					// rel var
				} else if (!sub.getSuperVar() && sub.getSubVar()) {
					t2Var = true;
					term2VarName = sub.getSubName();

					String[] subClasses = typeQuery.getDirectSubClasses(sub
							.getSuperName());

					for (int i = 0; i < subClasses.length; i++)
						System.out.println(subClasses[i]);

					Object[][] rowdata = new Object[2][2];

					rowdata[0][0] = "?" + sub.getSubName();
					rowdata[0][1] = subClasses[0];

					String[] colnames = new String[] { "Variable", "Binding" };

					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));
					Vector nextVector = new Vector();
					for (int i = 1; i < subClasses.length; i++)
						nextVector.add(subClasses[i]);

					it = nextVector.iterator();
					if (it.hasNext()) {
						ui.setBtnNextSolutionEnabled(true);
					}
					// var var
				} else if (sub.getSuperVar() && sub.getSubVar()) {
					t1Var = true;
					t2Var = true;
					term2VarName = sub.getSubName();
					term1VarName = sub.getSuperName();

					if (sub.getSuperName().equalsIgnoreCase(sub.getSubName())) {
						JOptionPane.showMessageDialog(ui.getFrmOoJdrew(),
								"Duplicate variable names not allowed",
								"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					Vector v1 = typeQuery
							.findAllDirectSuperClassesOfEverything();
					Vector v2 = typeQuery.findAllDirectSubClassesOfEverything();
					String sol = "";
					Iterator vit1 = v1.iterator();
					Iterator vit2 = v2.iterator();
					int count = 0;
					// Debug -> Prints out all the solutions for easy Copy and
					// Paste
					sol = "% Taxonomy Facts: \n";
					while (vit1.hasNext()) {
						count++;
						sol = sol + "subsumes(" + vit1.next().toString() + ","
								+ vit1.next().toString() + ")." + "\n";
					}
					ui.setSolutionTextAreaText(sol);
					// Debug

					it = v1.iterator();

					Object[][] rowdata = new Object[2][2];

					rowdata[0][0] = "?" + sub.getSuperName();
					rowdata[0][1] = (String) it.next();

					rowdata[1][0] = "?" + sub.getSubName();
					rowdata[1][1] = (String) it.next();

					String[] colnames = new String[] { "Variable", "Binding" };

					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));

					if (it.hasNext()) {
						ui.setBtnNextSolutionEnabled(true);
					}

				}

			} else if (predicate.equalsIgnoreCase(TypeQueryParserRuleML.LUB)) {

				lub = rmlTParser.parseElementsGLBandLUB(elements);

				if (lub.getResultVar()) {

					ArrayList<String> terms = lub.getTerms();

					String[] lubArray = new String[terms.size()];

					for (int i = 0; i < terms.size(); i++)
						lubArray[i] = terms.get(i);

					String leastUpperBound = typeQuery
							.leastUpperBound(lubArray);

					Object[][] rowdata = new Object[2][2];

					rowdata[0][0] = "?" + lub.getResultVarName();
					rowdata[0][1] = leastUpperBound;

					String[] colnames = new String[] { "Variable", "Binding" };

					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));

				} else if (!lub.getResultVar()) {

					Object[][] rowdata = new Object[2][2];
					String[] colnames = new String[] { "Variable", "Binding" };
					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));
					ui.setSolutionTextAreaText("");

					ArrayList<String> terms = lub.getTerms();

					String[] lubArray = new String[terms.size()];

					for (int i = 0; i < terms.size(); i++)
						lubArray[i] = terms.get(i);

					String leastUpperBound = typeQuery
							.leastUpperBound(lubArray);
					ui.setSolutionTextAreaText(leastUpperBound);
				}
			} else if (predicate.equalsIgnoreCase(TypeQueryParserRuleML.GLB)) {

				glb = rmlTParser.parseElementsGLBandLUB(elements);

				if (glb.getResultVar()) {

					ArrayList<String> terms = glb.getTerms();

					String[] glbArray = new String[terms.size()];

					for (int i = 0; i < terms.size(); i++)
						glbArray[i] = terms.get(i);

					String greatestLowerBound = typeQuery
							.greatestLowerBound(glbArray);

					Object[][] rowdata = new Object[2][2];

					rowdata[0][0] = "?" + glb.getResultVarName();
					rowdata[0][1] = greatestLowerBound;

					String[] colnames = new String[] { "Variable", "Binding" };

					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));

				} else if (!glb.getResultVar()) {

					Object[][] rowdata = new Object[2][2];
					String[] colnames = new String[] { "Variable", "Binding" };
					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));
					ui.setSolutionTextAreaText("");

					ArrayList<String> terms = glb.getTerms();

					String[] glbArray = new String[terms.size()];

					for (int i = 0; i < terms.size(); i++) {
						glbArray[i] = terms.get(i);
					}

					String greatestLowerBound = typeQuery
							.greatestLowerBound(glbArray);
					ui.setSolutionTextAreaText(greatestLowerBound);
				}

			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(ui.getFrmOoJdrew(), ex.getMessage(),
					"Type Query Parser Exeception", JOptionPane.ERROR_MESSAGE);
		}
	}

	// TODO: This method was copied from the old GUI and has been modified to
	// work with the current code base. This code should be rewritten in a much
	// cleaner fashion.
	private void issuePOSLTypeQuery(String query) {
		Object[][] resetRow = new Object[2][2];
		String[] resetCol = new String[] { "Variable", "Binding" };

		ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
				resetRow, resetCol));

		ui.setBtnNextSolutionEnabled(false);

		// It is an iterator that is used to map all the solutions to bindings
		it = null;
		// Creating a QueryTypes objects
		QueryTypes typeQuery = new QueryTypes();

		if (query.equals("")) {
			return;
		}

		try {

			t1Var = false;
			t2Var = false;
			term1VarName = "";
			term2VarName = "";
			ui.setSolutionTextAreaText("");

			TypeQueryParserPOSL poslTParser = new TypeQueryParserPOSL(query);
			Term[] queryTerms = poslTParser.parseForPredicate();
			String predicate = poslTParser.getPredicate();

			if (predicate.equalsIgnoreCase(TypeQueryParserPOSL.SUBSUMESPLUS)) {

				subPlus = poslTParser
						.parseElementsSubsumesAndSubsumesPlus(queryTerms);

				// rel rel
				if (!subPlus.getSuperVar() && !subPlus.getSubVar()) {
					ui.setSolutionTextAreaText(""
							+ typeQuery.isSuperClass(subPlus.getSuperName(),
									subPlus.getSubName()));
					// var rel get all super classes
				} else if (subPlus.getSuperVar() && !subPlus.getSubVar()) {
					t1Var = true;
					term1VarName = subPlus.getSuperName();

					String[] superClasses = typeQuery
							.findAllSuperClasses(subPlus.getSubName());

					Object[][] rowdata = new Object[2][2];
					rowdata[0][0] = "?" + subPlus.getSuperName();
					rowdata[0][1] = superClasses[0];
					String[] colnames = new String[] { "Variable", "Binding" };
					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));

					Vector<String> nextVector = new Vector<String>();
					for (int i = 1; i < superClasses.length; i++)
						nextVector.add(superClasses[i]);

					it = nextVector.iterator();

					if (it.hasNext()) {
						ui.setBtnNextSolutionEnabled(true);
					}

					// rel var get all sub classes
				} else if (!subPlus.getSuperVar() && subPlus.getSubVar()) {
					t2Var = true;
					term2VarName = subPlus.getSubName();
					String[] subClasses = typeQuery.findAllSubClasses(subPlus
							.getSuperName());

					for (int i = 0; i < subClasses.length; i++)
						System.out.println(subClasses[i]);

					Object[][] rowdata = new Object[2][2];

					rowdata[0][0] = "?" + subPlus.getSubName();
					rowdata[0][1] = subClasses[0];

					String[] colnames = new String[] { "Variable", "Binding" };

					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));
					Vector nextVector = new Vector();
					for (int i = 1; i < subClasses.length; i++)
						nextVector.add(subClasses[i]);

					it = nextVector.iterator();

					if (it.hasNext()) {
						ui.setBtnNextSolutionEnabled(true);
					}

					// var var get all relations
				} else if (subPlus.getSuperVar() && subPlus.getSubVar()) {
					t1Var = true;
					t2Var = true;
					term2VarName = subPlus.getSubName();
					term1VarName = subPlus.getSuperName();

					if (subPlus.getSuperName().equalsIgnoreCase(
							subPlus.getSubName())) {
						JOptionPane.showMessageDialog(ui.getFrmOoJdrew(),
								"Duplicate variable names not allowed",
								"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					Vector v1 = typeQuery.findAllSuperClassesOfEverything();
					Vector v2 = typeQuery.findAllSubClassesOfEverything();
					String sol = "";
					Iterator vit1 = v1.iterator();
					Iterator vit2 = v2.iterator();
					int count = 0;
					// Debug -> Prints out all the solutions for easy Copy and
					// Paste
					sol = "% Taxonomy Facts: \n";
					while (vit1.hasNext()) {
						count++;
						sol = sol + "subsumes(" + vit1.next().toString() + ","
								+ vit1.next().toString() + ")." + "\n";
					}
					ui.setSolutionTextAreaText(sol);
					// Debug

					it = v1.iterator();

					Object[][] rowdata = new Object[2][2];

					rowdata[0][0] = "?" + subPlus.getSuperName();
					rowdata[0][1] = (String) it.next();

					rowdata[1][0] = "?" + subPlus.getSubName();
					rowdata[1][1] = (String) it.next();

					String[] colnames = new String[] { "Variable", "Binding" };

					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));

					if (it.hasNext()) {
						ui.setBtnNextSolutionEnabled(true);
					}

				}
				// subsumesPlus
			} else if (predicate.equalsIgnoreCase(TypeQueryParserPOSL.SUBSUMES)) {
				sub = poslTParser
						.parseElementsSubsumesAndSubsumesPlus(queryTerms);
				// rel rel
				if (!sub.getSuperVar() && !sub.getSubVar()) {
					ui.setSolutionTextAreaText(""
							+ typeQuery.isDirectSuperClass(sub.getSuperName(),
									sub.getSubName()));
					// var rel
				} else if (sub.getSuperVar() && !sub.getSubVar()) {
					t1Var = true;
					term1VarName = sub.getSuperName();

					String[] superClasses = typeQuery.getDirectSuperClasses(sub
							.getSubName());

					for (int i = 0; i < superClasses.length; i++)
						System.out.println(superClasses[i]);

					Object[][] rowdata = new Object[2][2];

					rowdata[0][0] = "?" + sub.getSuperName();
					rowdata[0][1] = superClasses[0];

					String[] colnames = new String[] { "Variable", "Binding" };

					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));
					Vector nextVector = new Vector();
					for (int i = 1; i < superClasses.length; i++)
						nextVector.add(superClasses[i]);

					it = nextVector.iterator();

					if (it.hasNext()) {
						ui.setBtnNextSolutionEnabled(true);
					}

					// rel var
				} else if (!sub.getSuperVar() && sub.getSubVar()) {
					t2Var = true;
					term2VarName = sub.getSubName();

					String[] subClasses = typeQuery.getDirectSubClasses(sub
							.getSuperName());

					for (int i = 0; i < subClasses.length; i++)
						System.out.println(subClasses[i]);

					Object[][] rowdata = new Object[2][2];

					rowdata[0][0] = "?" + sub.getSubName();
					rowdata[0][1] = subClasses[0];

					String[] colnames = new String[] { "Variable", "Binding" };

					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));
					Vector nextVector = new Vector();
					for (int i = 1; i < subClasses.length; i++)
						nextVector.add(subClasses[i]);

					it = nextVector.iterator();
					if (it.hasNext()) {
						ui.setBtnNextSolutionEnabled(true);
					}
					// var var
				} else if (sub.getSuperVar() && sub.getSubVar()) {
					t1Var = true;
					t2Var = true;
					term2VarName = sub.getSubName();
					term1VarName = sub.getSuperName();

					if (sub.getSuperName().equalsIgnoreCase(sub.getSubName())) {
						JOptionPane.showMessageDialog(ui.getFrmOoJdrew(),
								"Duplicate variable names not allowed",
								"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					Vector v1 = typeQuery
							.findAllDirectSuperClassesOfEverything();
					Vector v2 = typeQuery.findAllDirectSubClassesOfEverything();
					String sol = "";
					Iterator vit1 = v1.iterator();
					Iterator vit2 = v2.iterator();
					int count = 0;
					// Debug -> Prints out all the solutions for easy Copy and
					// Paste
					sol = "% Taxonomy Facts: \n";
					while (vit1.hasNext()) {
						count++;
						sol = sol + "subsumes(" + vit1.next().toString() + ","
								+ vit1.next().toString() + ")." + "\n";
					}
					ui.setSolutionTextAreaText(sol);
					// Debug

					it = v1.iterator();

					Object[][] rowdata = new Object[2][2];

					rowdata[0][0] = "?" + sub.getSuperName();
					rowdata[0][1] = (String) it.next();

					rowdata[1][0] = "?" + sub.getSubName();
					rowdata[1][1] = (String) it.next();

					String[] colnames = new String[] { "Variable", "Binding" };

					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));

					if (it.hasNext()) {
						ui.setBtnNextSolutionEnabled(true);
					}
				}
			}// subsumes
			else if (predicate.equalsIgnoreCase(TypeQueryParserPOSL.LUB)) {

				lub = poslTParser.parseElementsGLBandLUB(queryTerms);

				if (lub.getResultVar()) {

					ArrayList<String> terms = lub.getTerms();

					String[] lubArray = new String[terms.size()];

					for (int i = 0; i < terms.size(); i++)
						lubArray[i] = terms.get(i);

					String leastUpperBound = typeQuery
							.leastUpperBound(lubArray);

					Object[][] rowdata = new Object[2][2];

					rowdata[0][0] = "?" + lub.getResultVarName();
					rowdata[0][1] = leastUpperBound;

					String[] colnames = new String[] { "Variable", "Binding" };

					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));

				} else if (!lub.getResultVar()) {

					Object[][] rowdata = new Object[2][2];
					String[] colnames = new String[] { "Variable", "Binding" };
					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));
					ui.setSolutionTextAreaText("");

					ArrayList<String> terms = lub.getTerms();

					String[] lubArray = new String[terms.size()];

					for (int i = 0; i < terms.size(); i++)
						lubArray[i] = terms.get(i);

					String leastUpperBound = typeQuery
							.leastUpperBound(lubArray);
					ui.setSolutionTextAreaText(leastUpperBound);
				}
			}// LUB
			else if (predicate.equalsIgnoreCase(TypeQueryParserRuleML.GLB)) {

				glb = poslTParser.parseElementsGLBandLUB(queryTerms);

				if (glb.getResultVar()) {

					ArrayList<String> terms = glb.getTerms();

					String[] glbArray = new String[terms.size()];

					for (int i = 0; i < terms.size(); i++)
						glbArray[i] = terms.get(i);

					String greatestLowerBound = typeQuery
							.greatestLowerBound(glbArray);

					Object[][] rowdata = new Object[2][2];

					rowdata[0][0] = "?" + glb.getResultVarName();
					rowdata[0][1] = greatestLowerBound;

					String[] colnames = new String[] { "Variable", "Binding" };

					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));

				} else if (!glb.getResultVar()) {

					Object[][] rowdata = new Object[2][2];
					String[] colnames = new String[] { "Variable", "Binding" };
					ui.setVariableBindingsTableModel(new javax.swing.table.DefaultTableModel(
							rowdata, colnames));
					ui.setSolutionTextAreaText("");

					ArrayList<String> terms = glb.getTerms();

					String[] glbArray = new String[terms.size()];

					for (int i = 0; i < terms.size(); i++) {
						glbArray[i] = terms.get(i);
					}

					String greatestLowerBound = typeQuery
							.greatestLowerBound(glbArray);
					ui.setSolutionTextAreaText(greatestLowerBound);
				}
			}// GLB

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(ui.getFrmOoJdrew(), ex.getMessage(),
					"Type Query Parser Exeception", JOptionPane.ERROR_MESSAGE);
		}
	}
}
