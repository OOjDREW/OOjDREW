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
import java.util.prefs.Preferences;

/**
 * <p>Title: OO jDREW</p>
 *
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.88</p>
 *s
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * @author Marcel A. Ball
 * @version 0.89
 */
public class Config implements Configuration {
    /**
     * This variable specifies the default output format that is produced
     * when toString() calls are made on Term and DefiniteClause Objects.
     * If this is true then output is in POSL syntax, otherwise RuleML 0.88
     * is produced.
     */
    public static boolean PRPRINT = false;

    /**
     * This variable controls how Variables are printed. If true then the
     * variable name is combined with the variable id in the output,
     * otherwise only the variable name is printed. This is useful as after
     * unification it is possible to have two different variable with the
     * same base name, but the option to disable is there so that the
     * parsing and output libraries can be used to translate from POSL
     * syntax to RuleML 0.88 syntax without chaning the variable names.
     */
    public static boolean PRINTVARID = true;


    /**
     * This variable controls how generated symbols (skolem constants) are
     * printed. If this is set to true, then the generated symbol is output as
     * a regular constant (eg. <Ind>$gensym10000</Ind> or $gensym10000; in
     * RuleML and POSL respectively). If this is set to false then the
     * generated symbol is output the same as it was input (eg. <Skolem /> or _;
     * in RuleML and POSL respectively).
     */
    public static boolean PRINTGENSYMS = false;

    /**
     * This variable controls how system generated oids are printed. If this
     * is set to true, then the generated symbol is output; with the generated
     * symbol printed as determined by PRINTGENSYMS. If it is set to false then
     * generated oids are completely omitted. This variable does not effect the
     * RuleML output, only POSL; in RuleML the oids are always printed, but
     * generated oids are printed based upon the PRINTGENSYMS variable.
     */
    public static boolean PRINTGENOIDS = true;

    /**
     * This variable controls how anonymous varaibles are printed. If this is
     * set to false (the default), then anonymous variables are printed as they
     * are input (<Var /> or ?; for RuleML and POSL respectively). If it is
     * set to true then the generated variable name is output (eg.
     * <Var>$anonvar1</Var> or ?$ANON1; for RuleML and POSL respectively).
     */
    public static boolean PRINTANONVARNAMES = false;
    
    private Preferences preferences;
    
    public Config()
    {
    	this.preferences = Preferences.userNodeForPackage(getClass());
    }
    
    public boolean getValidateRuleMLEnabled()
    {
    	return preferences.getBoolean("ValidateRuleML", false);
    }
    
    public void setValidateRuleMLEnabled(boolean enabled)
    {
    	preferences.putBoolean("ValidateRuleML", enabled);
    }

	public int getTextFontSize() {
		return preferences.getInt("TextFontSize" , 12);
	}

	public void setTextFontSize(int newSize) {
		preferences.putInt("TextFontSize", newSize);
	}

	public void addPreferenceChangeListener(
			PreferenceChangeListener listener) {
		preferences.addPreferenceChangeListener(listener);
	}
}