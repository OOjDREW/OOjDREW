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

import java.util.Enumeration;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.UIManager;
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
