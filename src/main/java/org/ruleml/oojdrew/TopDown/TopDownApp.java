package org.ruleml.oojdrew.TopDown;

import java.awt.EventQueue;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import org.ruleml.oojdrew.Config;
import org.ruleml.oojdrew.Configuration;
import org.ruleml.oojdrew.GUI.DebugConsole;
import org.ruleml.oojdrew.GUI.FontSizeDialogUI;
import org.ruleml.oojdrew.GUI.FontSizeManager;
import org.ruleml.oojdrew.GUI.TextPaneAppender;
import org.ruleml.oojdrew.GUI.TopDownUI;
import org.ruleml.oojdrew.GUI.UISettingsController;

public class TopDownApp implements UISettingsController, PreferenceChangeListener {
	private Configuration config;
	private TopDownUI ui;
	private FontSizeDialogUI fontSizeDialogUI;
	private DebugConsole debugConsole;
	private FontSizeManager fontSizeManager;
	private Logger logger;
	
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
	
	public void run()
	{
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				logger.debug("Entering event loop");
				ui.setFrameVisible(true);
			}			
		});
	}
	
	public static TopDownApp getTopDownApp()
	{
		// Construct dependencies
		Configuration config = new Config();
		FontSizeManager fontSizeManager = new FontSizeManager(config);
		TopDownUI topDownUI = new TopDownUI();
		FontSizeDialogUI fontSizeDialogUI = new FontSizeDialogUI();
		
		// Create DebugConsole and logger
		DebugConsole debugConsole = new DebugConsole();	
		
        BasicConfigurator.configure();
        Logger root = Logger.getRootLogger();
        root.setLevel(Level.DEBUG);
        TextPaneAppender tpa = new TextPaneAppender(new PatternLayout(
                "%-5p %d [%t]:  %m%n"), "Debug");
        tpa.setTextPane(debugConsole.getTextPane());
        root.addAppender(tpa);
        
        // Create TopDownApp
        TopDownApp topDownApp = new TopDownApp(config, fontSizeManager,
				topDownUI, fontSizeDialogUI, debugConsole);
		
		return topDownApp;
	}
	
	private TopDownApp(Configuration config, FontSizeManager fontSizeManager,
			TopDownUI ui, FontSizeDialogUI fontSizeDialogUI,
			DebugConsole debugConsole)
	{
		this.config = config;
		this.fontSizeManager = fontSizeManager;
		this.ui = ui;
		this.fontSizeDialogUI = fontSizeDialogUI;
		this.debugConsole = debugConsole;
		this.logger = Logger.getLogger(this.getClass());

		ui.setController(this);
		fontSizeDialogUI.setSettingsController(this);
		config.addPreferenceChangeListener(this);
		preferenceChange(null);
	}

	public void syncUIWithSettings() {
		fontSizeDialogUI.setSpinnerTextAreaFontSizeValue(config.getTextAreaFontSize());
		fontSizeDialogUI.setSpinnerUIFontSizeValue(config.getUIFontSize());
		ui.setChckbxmntmValidateRulemlSelected(config.getValidateRuleMLEnabled());
		ui.setChckbxmntmShowDebugConsoleSelected(config.getDebugConsoleVisible());
	}

	public void applySettingsFromUI() {
		config.setTextAreaFontSize(fontSizeDialogUI.getSpinnerTextAreaFontSizeValue());
		config.setUIFontSize(fontSizeDialogUI.getSpinnerUIFontSizeValue());
		config.setValidateRuleMLEnabled(ui.getChckbxmntmValidateRulemlSelected());
		config.setDebugConsoleVisible(ui.getChckbxmntmShowDebugConsoleSelected());
	}

	public void showFontSizeDialog() {
		fontSizeDialogUI.setVisible(true);
	}

	public void preferenceChange(PreferenceChangeEvent evt) {
		ui.updateUI();
		fontSizeDialogUI.updateUI();
		debugConsole.setVisible(config.getDebugConsoleVisible());
	}
	
	private boolean showOpenForAppendDialog()
	{
		return 0 == JOptionPane.showConfirmDialog(null, "Append content?",
				"Append or replace?", JOptionPane.YES_NO_OPTION);
	}
	
	public void openFile() {
        boolean append = showOpenForAppendDialog();
        
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(ui.getFrmOoJdrew());
        
        if(result != JFileChooser.APPROVE_OPTION)
        {
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
			
			while((currentLine = bufferedReader.readLine()) != null)
			{
				stringBuilder.append(currentLine);
				stringBuilder.append(lineSeparator);
			}
			
			fileContents = stringBuilder.toString();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(ui.getFrmOoJdrew(), e.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
			logger.error(e.getMessage());
			return;
		}
        
        if(append)
        {
        	ui.appendToCurrentEditingTab(fileContents);
        }
        else
        {
        	ui.setTextForCurrentEditingTab(fileContents);
        }
	}
	
	public void openURI()
	{
		boolean append = showOpenForAppendDialog();
		String url = JOptionPane.showInputDialog("Please enter an URI");
		
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);
		method.setFollowRedirects(true);
		String contents;

		try {
			client.executeMethod(method);
			contents = method.getResponseBodyAsString();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(ui.getFrmOoJdrew(), e.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
			logger.error(e.getMessage());
			return;
		} finally {
			method.releaseConnection();
		} 		
		
        if(append)
        {
        	ui.appendToCurrentEditingTab(contents);
        }
        else
        {
        	ui.setTextForCurrentEditingTab(contents);
        }
	}
}
