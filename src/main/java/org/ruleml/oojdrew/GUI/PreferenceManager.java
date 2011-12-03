package org.ruleml.oojdrew.GUI;

import java.util.Enumeration;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;
import org.ruleml.oojdrew.Configuration;

public class PreferenceManager implements PreferenceChangeListener {
	private Configuration config;
	
	public PreferenceManager(Configuration config)
	{
		this.config = config;
		config.addPreferenceChangeListener(this);
		preferenceChange(null);
	}
	
	public void preferenceChange(PreferenceChangeEvent evt) {		
		float newUIFontSize = config.getUIFontSize();
		float newTextAreaFontSize = config.getTextAreaFontSize();
		
	    Enumeration<Object> keys = UIManager.getDefaults().keys();
	    while (keys.hasMoreElements()) {
	    	Object key = keys.nextElement();
	    	Object value = UIManager.get(key);
	    	
	    	if (value instanceof javax.swing.plaf.FontUIResource) {
	    		javax.swing.plaf.FontUIResource font = (javax.swing.plaf.FontUIResource)value;
	    		FontUIResource newFont;
	    		
	    		if(key.toString().equals("TextArea.font")) {
	    			newFont = new FontUIResource(font.deriveFont(newTextAreaFontSize));
	    		} else {
	    			newFont = new FontUIResource(font.deriveFont(newUIFontSize));
    			}
	    		
	    		UIManager.put (key, newFont);
	      	}
	    }
	    
	    try
		{
			UIManager.setLookAndFeel(config.getSelectedLookAndFeel());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
