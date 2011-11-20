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

package org.ruleml.oojdrew.tests;

import java.util.prefs.PreferenceChangeListener;

import org.ruleml.oojdrew.Configuration;

public class MockConfiguration implements Configuration {
	private boolean ruleMLCompatibilityModeEnabled;
	
	public boolean getValidateRuleMLEnabled() {
		return ruleMLCompatibilityModeEnabled;
	}

	public void setValidateRuleMLEnabled(boolean enabled) {
		ruleMLCompatibilityModeEnabled = enabled;
	}

	public int getTextFontSize() {
		throw new UnsupportedOperationException();
	}

	public void setTextFontSize(int newSize) {
		throw new UnsupportedOperationException();
	}

	public void addPreferenceChangeListener(PreferenceChangeListener listener) {
	}

}
