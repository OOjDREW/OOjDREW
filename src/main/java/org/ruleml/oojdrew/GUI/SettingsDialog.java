package org.ruleml.oojdrew.GUI;

import org.ruleml.oojdrew.Config;


public class SettingsDialog {
	private SettingsDialogUI ui;
	private Config config;
	
	public SettingsDialog()
	{
		this.config = new Config();
		this.ui = new SettingsDialogUI(this);
	}
	
	public void applySettings()
	{
		config.setRuleMLCompatibilityModeEnabled(ui.getChckbxRuleMLCompatibilityModeSelected());
	}
	
	public void syncUI()
	{
		ui.setChckbxRuleMLCompatibilityModeSelected(config.getRuleMLCompatibilityModeEnabled());
	}
	
	public void show()
	{
		ui.setVisible(true);
	}

	//public void 
}
