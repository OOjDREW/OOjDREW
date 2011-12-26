package org.ruleml.oojdrew.parsing;

/**
 * This is used to indicate which RuleML back-end parsers are supported.
 */
public enum RuleMLFormat {
    RuleML88("RuleML 0.88"), RuleML91("RuleML 0.91"), RuleML100(
            "RuleML 1.0");

    private String versionName;

    RuleMLFormat(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionName() {
        return this.versionName;
    }

    public static RuleMLFormat fromString(String versionName) {
        if (versionName != null) {
            for (RuleMLFormat rmlFormat : RuleMLFormat.values()) {
                if (versionName.equalsIgnoreCase(rmlFormat.versionName)) {
                    return rmlFormat;
                }
            }
        }
        return null;
    }

    public static String[] getVersionNames() {
        String[] versionNames = new String[values().length];
        int i = 0;
        for (RuleMLFormat rmlFormat : values()) {
            versionNames[i] = rmlFormat.versionName;
            i++;
        }
        return versionNames;
    }
}
