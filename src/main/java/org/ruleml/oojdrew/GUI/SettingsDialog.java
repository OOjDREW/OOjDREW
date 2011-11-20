// OO jDREW - An Object Oriented extension of the Java Deductive Reasoning Engine for the Web
// Copyright (C) 2005 Marcel Ball
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
		config.setValidateRuleMLEnabled(ui.getChckbxValidateRuleMLSelected());
		config.setTextFontSize(ui.getSpinnerFontSizeValue());
	}
	
	public void syncUI()
	{
		ui.setChckbxValidateRuleMLSelected(config.getValidateRuleMLEnabled());
		ui.setSpinnerFontSizeValue(config.getTextFontSize());
	}
	
	public void show()
	{
		ui.setVisible(true);
	} 
}
