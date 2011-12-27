// OO jDREW - An Object Oriented extension of the Java Deductive Reasoning Engine for the Web
// Copyright (C) 2011
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

import java.awt.Component;
import java.io.IOException;
import java.util.prefs.PreferenceChangeEvent;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.ruleml.oojdrew.Configuration;
import org.ruleml.oojdrew.Reasoner;
import org.ruleml.oojdrew.parsing.InputFormat;
import org.ruleml.oojdrew.parsing.POSLParser;
import org.ruleml.oojdrew.parsing.RDFSParser;
import org.ruleml.oojdrew.parsing.RuleMLParser;
import org.ruleml.oojdrew.parsing.SubsumesParser;
import org.ruleml.oojdrew.util.SymbolTable;
import org.ruleml.oojdrew.util.Types;
import org.ruleml.oojdrew.util.Util;

public abstract class AbstractUIApp {

    protected Configuration config;
    protected UI ui;
    protected PreferenceDialogUI preferenceDialogUI;
    protected DebugConsole debugConsole;
    protected PreferenceManager preferenceManager;
    protected Logger logger;

    protected RDFSParser rdfsParser;
    protected POSLParser poslParser;
    protected RuleMLParser rmlParser;
    protected SubsumesParser subsumesParser;
    
    protected Reasoner reasoner;

    protected AbstractUIApp(Configuration config,
            PreferenceManager preferenceManager, UI ui,
            PreferenceDialogUI preferenceDialogUI, DebugConsole debugConsole,
            RDFSParser rdfsParser, POSLParser poslParser,
            RuleMLParser rmlParser, SubsumesParser subsumesParser, Reasoner reasoner) {
        this.config = config;
        this.preferenceManager = preferenceManager;
        this.ui = ui;
        this.preferenceDialogUI = preferenceDialogUI;
        this.debugConsole = debugConsole;
        this.logger = Logger.getLogger(this.getClass());
        this.rdfsParser = rdfsParser;
        this.poslParser = poslParser;
        this.rmlParser = rmlParser;
        this.subsumesParser = subsumesParser;
        this.reasoner = reasoner;
    }

    public void syncUIWithSettings() {
        preferenceDialogUI.setSpinnerTextAreaFontSizeValue(config
                .getTextAreaFontSize());
        preferenceDialogUI.setSpinnerUIFontSizeValue(config.getUIFontSize());
        preferenceDialogUI.setLinkFontSizes(config.getLinkFontSizes());
        preferenceDialogUI.setLookAndFeel(config.getSelectedLookAndFeel());
        preferenceDialogUI.setRuleMLFormat(config.getSelectedRuleMLFormat());
        preferenceDialogUI.setLoggingLevel(config.getLogLevel());

        ui.setChckbxmntmValidateRulemlSelected(config
                .getValidateRuleMLEnabled());
    }

    public void applySettingsFromUI() {
        config.setTextAreaFontSize(preferenceDialogUI
                .getSpinnerTextAreaFontSizeValue());
        config.setUIFontSize(preferenceDialogUI.getSpinnerUIFontSizeValue());
        config.setLinkFontSizes(preferenceDialogUI.getLinkFontSizes());
        config.setLookAndFeel(preferenceDialogUI.getSelectedLookAndFeel());
        config.setSelectedRuleMLFormat(preferenceDialogUI.getRuleMLFormat());
        config.setLogLevel(preferenceDialogUI.getLoggingLevel());

        config.setValidateRuleMLEnabled(ui
                .getChckbxmntmValidateRulemlSelected());
    }

    public void showDebugConsole() {
        debugConsole.setVisible(true);
    }

    public void showPreferenceDialog() {
        preferenceDialogUI.setVisible(true);
    }

    public void preferenceChange(PreferenceChangeEvent evt) {
        ui.updateUI();
        preferenceDialogUI.updateUI();
    }

    private boolean showOpenForAppendDialog() {
        return 0 == JOptionPane.showConfirmDialog(null, "Append content?",
                "Append or replace?", JOptionPane.YES_NO_OPTION);
    }

    private void setTextOfCurrentEditingTab(String text, boolean append) {
        if (text == null) {
            return;
        }

        if (append) {
            ui.appendToCurrentEditingTab(text);
        } else {
            ui.setTextForCurrentEditingTab(text);
        }
    }
    
    /**
     * Shows open URI dialog and reads from URL to editing tab
     */
    public void openURI() {
        boolean append = showOpenForAppendDialog();
        String uri = JOptionPane.showInputDialog("Please enter an URI");

        if (uri != null) {
            String pageContent;
            try {
                pageContent = Util.readFromURIWithTimeout(uri, 3000);
            } catch (IOException e) {
                defaultExceptionHandler(e);
                return;
            }
            
            setTextOfCurrentEditingTab(pageContent, append);
        }
    }
    
    public void openFile() {
        boolean append = showOpenForAppendDialog();
        Component parent = ui.getFrmOoJdrew();

        String fileContent;
        try {
            fileContent = Util.selectAndReadFile(parent);
        } catch (IOException e) {
            defaultExceptionHandler(e);
            return;
        }

        setTextOfCurrentEditingTab(fileContent, append);
    }

    public void saveFileAs() {
        String content = ui.getTextForCurrentEditingTab();
        Component parent = ui.getFrmOoJdrew();

        try {
            Util.selectAndSaveToFile(content, parent);
        } catch (IOException e) {
            defaultExceptionHandler(e);
        }
    }

    public void parseTypeInformation() {
        String typeInformation = ui.getTypeDefinitionTextAreaText();
        InputFormat format = ui.getTypeInformationInputFormat();

        // Reset the type system
        Types.reset();

        if (format == InputFormat.InputFormatRFDS) {
            parseRDFSTypes(typeInformation);
        } else {
            parsePOSLTypes(typeInformation);
        }

        // Type information may have changed, time to parse the knowledge base
        // again.
        parseKnowledgeBase();
    }

    private void parseRDFSTypes(String typeInformation) {
        try {
            RDFSParser.parseRDFSString(typeInformation);
        } catch (Exception e) {
            defaultExceptionHandler(e);
            return;
        }
    }

    private void parsePOSLTypes(String typeInformation) {
        try {
            subsumesParser.parseSubsumes(typeInformation);
        } catch (Exception e) {
            defaultExceptionHandler(e);
            return;
        }
    }

    public void parseKnowledgeBase() {
        SymbolTable.reset();
        reasoner.clearClauses();

        InputFormat knowledgeBaseFormat = ui.getKnowledgeBaseInputFormat();
        String knowledgeBase = ui.getKnowledgeBaseTextAreaText();

        if (!knowledgeBase.isEmpty()) {
            if (knowledgeBaseFormat == InputFormat.InputFormatRuleML) {
                parseRuleMLKnowledeBase(knowledgeBase);
            } else {
                parsePOSLKnowledgeBase(knowledgeBase);
            }
        }
    }
    
    protected void parseRuleMLKnowledeBase(String knowledgeBase) {
        rmlParser.clear();

        try {
            rmlParser.parseRuleMLString(knowledgeBase);
            reasoner.loadClauses(rmlParser.iterator());
        } catch (Exception e) {
            defaultExceptionHandler(e);
            return;
        }
    }

    protected void parsePOSLKnowledgeBase(String knowledgeBase) {
        poslParser.reset();

        try {
            poslParser.parseDefiniteClauses(knowledgeBase);
            reasoner.loadClauses(poslParser.iterator());
        } catch (Exception e) {
            defaultExceptionHandler(e);
            return;
        }
    }

    protected void defaultExceptionHandler(Exception e) {
        String msg = String.format("Unknown error occured (%s)", e.getClass()
                .getName());
        if (e.getMessage() != null) {
            msg = e.getMessage();
        } else if (e.getCause() != null && e.getCause().getMessage() != null) {
            msg = e.getCause().getMessage();
        }

        JOptionPane.showMessageDialog(ui.getFrmOoJdrew(), msg, "Error",
                JOptionPane.ERROR_MESSAGE);
        logger.error(msg);
    }
}
