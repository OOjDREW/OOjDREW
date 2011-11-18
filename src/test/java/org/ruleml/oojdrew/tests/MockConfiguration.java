package org.ruleml.oojdrew.tests;

import org.ruleml.oojdrew.Configuration;

public class MockConfiguration implements Configuration {
	private boolean ruleMLCompatibilityModeEnabled;
	
	public boolean getRuleMLCompatibilityModeEnabled() {
		return ruleMLCompatibilityModeEnabled;
	}

	public void setRuleMLCompatibilityModeEnabled(boolean enabled) {
		ruleMLCompatibilityModeEnabled = enabled;
	}

}
