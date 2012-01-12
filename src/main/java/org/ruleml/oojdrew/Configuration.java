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

package org.ruleml.oojdrew;

import java.util.prefs.PreferenceChangeListener;

import org.apache.log4j.Level;
import org.ruleml.oojdrew.parsing.RuleMLFormat;

public interface Configuration {
    
    void addPreferenceChangeListener(PreferenceChangeListener listener);
    
    public int getUiPreferenceChangeCount();
    public void decreaseUiPreferenceChangeCount();
    
    public int getTextAreaFontSize();
    public void setTextAreaFontSize(int newSize);

    public int getUIFontSize();
    public void setUIFontSize(int newSize);

    public boolean getLinkFontSizes();
    public void setLinkFontSizes(boolean linkFontSizes);

    public String getLookAndFeel();
    public void setLookAndFeel(String lafClassName);

    public RuleMLFormat getRuleMLFormat();
    public void setSelectedRuleMLFormat(RuleMLFormat rmlFormat);
    
    public int getHttpConnectionTimeout();
    public void setHttpConnectionTimeout(int timeoutInMilliseconds);

    public Level getLogLevel();
    public void setLogLevel(Level logLevel);
}
