package org.ruleml.oojdrew.TopDown;

import java.awt.EventQueue;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.UIManager;

import org.ruleml.oojdrew.Config;
import org.ruleml.oojdrew.Configuration;
import org.ruleml.oojdrew.GUI.DebugConsole;
import org.ruleml.oojdrew.GUI.FontSizeDialogUI;
import org.ruleml.oojdrew.GUI.FontSizeManager;
import org.ruleml.oojdrew.GUI.TopDownUI;
import org.ruleml.oojdrew.GUI.UISettingsController;

public class TopDownApp implements UISettingsController, PreferenceChangeListener {
	private Configuration config;
	private TopDownUI ui;
	private FontSizeDialogUI fontSizeDialogUI;
	private DebugConsole debugConsole;
	private FontSizeManager fontSizeManager;
	
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
		DebugConsole debugConsole = new DebugConsole();	
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

		ui.setSettingsController(this);
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
	}
}
