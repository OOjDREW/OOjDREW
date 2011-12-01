package org.ruleml.oojdrew.TopDown;

import java.awt.EventQueue;
import javax.swing.UIManager;

import org.ruleml.oojdrew.GUI.SettingsDialogUI;
import org.ruleml.oojdrew.GUI.TopDownUI;

public class TopDownApp {
	private TopDownUI ui;
	private SettingsDialogUI fontSizeDialogUI;
	
	private TopDownApp(TopDownUI ui, SettingsDialogUI fontSizeDialogUI)
	{
		this.ui = ui;
		this.fontSizeDialogUI = fontSizeDialogUI;
	}
	
	public 
	
	
	public static TopDownApp getTopDownApp()
	{
		TopDownUI topDownUI = new TopDownUI();
		SettingsDialogUI fontSizeDialogUI = new SettingsDialogUI(null);
		TopDownApp topDownApp = new TopDownApp(topDownUI, fontSizeDialogUI);
		
		return topDownApp;
	}
	
	public void run()
	{
		EventQueue.invokeLater(new Runnable() {
			public void run() {				
				ui.setFrameVisible(true);
			}			
		});
	}

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
}
