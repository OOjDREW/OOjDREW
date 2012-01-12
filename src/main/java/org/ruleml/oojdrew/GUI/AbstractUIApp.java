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
import java.util.prefs.PreferenceChangeListener;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.ruleml.oojdrew.Configuration;
import org.ruleml.oojdrew.Reasoner;
import org.ruleml.oojdrew.SyntaxFormat;
import org.ruleml.oojdrew.parsing.POSLParser;
import org.ruleml.oojdrew.parsing.RDFSParser;
import org.ruleml.oojdrew.parsing.RuleMLFormat;
import org.ruleml.oojdrew.parsing.RuleMLParser;
import org.ruleml.oojdrew.parsing.SubsumesParser;
import org.ruleml.oojdrew.util.Types;
import org.ruleml.oojdrew.util.Util;
import org.ruleml.oojdrew.xml.RuleMLNormalizer;
import org.ruleml.oojdrew.xml.RuleMLValidator;

/**
 * This class serves as a general UI app
 * 
 *
 */
public abstract class AbstractUIApp implements UISettingsController, PreferenceChangeListener {

    protected Configuration config;
    protected UI ui;
    protected PreferenceDialogUI preferenceDialogUI;
    protected DebugConsole debugConsole;
    protected PreferenceManager preferenceManager;
    protected Logger logger;

    protected POSLParser poslParser;
    protected RuleMLParser rmlParser;
    
    protected RDFSParser rdfsParser;
    protected SubsumesParser subsumesParser;

    protected Reasoner reasoner;

    protected AbstractUIApp(Configuration config, PreferenceManager preferenceManager,
            PreferenceDialogUI preferenceDialogUI, UI ui, Logger logger, DebugConsole debugConsole,
            RDFSParser rdfsParser, POSLParser poslParser, RuleMLParser rmlParser,
            SubsumesParser subsumesParser, Reasoner reasoner) {
        this.config = config;
        this.preferenceManager = preferenceManager;
        this.ui = ui;
        this.preferenceDialogUI = preferenceDialogUI;
        this.debugConsole = debugConsole;
        this.logger = logger;
        this.rdfsParser = rdfsParser;
        this.poslParser = poslParser;
        this.rmlParser = rmlParser;
        this.subsumesParser = subsumesParser;
        this.reasoner = reasoner;
        
        TextPaneAppender tpa = new TextPaneAppender(new PatternLayout("%-5p %d [%t]:  %m%n"), "Debug");
        tpa.setTextPane(debugConsole.getTextPane());
        logger.addAppender(tpa);

        ui.setController(this);
        preferenceDialogUI.setSettingsController(this);
        config.addPreferenceChangeListener(this);
        preferenceChange(null);
    }

    public void syncUIWithSettings() {
        preferenceDialogUI.setSpinnerTextAreaFontSizeValue(config.getTextAreaFontSize());
        preferenceDialogUI.setSpinnerUIFontSizeValue(config.getUIFontSize());
        preferenceDialogUI.setLinkFontSizes(config.getLinkFontSizes());
        preferenceDialogUI.setLookAndFeel(config.getLookAndFeel());
        preferenceDialogUI.setRuleMLFormat(config.getRuleMLFormat());
        preferenceDialogUI.setLoggingLevel(config.getLogLevel());
    }

    public void applySettingsFromUI() {
        config.setTextAreaFontSize(preferenceDialogUI.getSpinnerTextAreaFontSizeValue());
        config.setUIFontSize(preferenceDialogUI.getSpinnerUIFontSizeValue());
        config.setLinkFontSizes(preferenceDialogUI.getLinkFontSizes());
        config.setLookAndFeel(preferenceDialogUI.getSelectedLookAndFeel());
        config.setSelectedRuleMLFormat(preferenceDialogUI.getRuleMLFormat());
        config.setLogLevel(preferenceDialogUI.getLoggingLevel());
    }

    public void showDebugConsole() {
        debugConsole.setVisible(true);
    }

    public void showPreferenceDialog() {
        preferenceDialogUI.setVisible(true);
    }

    public void preferenceChange(PreferenceChangeEvent evt) {
        int uiPreferenceChanges = config.getUiPreferenceChangeCount();
        if (uiPreferenceChanges > 0) {
            if (uiPreferenceChanges == 1) {
                preferenceDialogUI.updateUI();
                ui.updateUI();
                debugConsole.updateUI();
            }
            config.decreaseUiPreferenceChangeCount();
        } else {
            logger.setLevel(config.getLogLevel());
        }
    }

    private boolean showOpenForAppendDialog() {
        return 0 == JOptionPane.showConfirmDialog(ui.getFrmOoJdrew(), "Append content?",
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
            int httpConnectionTimeout = config.getHttpConnectionTimeout();
            String pageContent;
            try {
                pageContent = Util.readFromURIWithTimeout(uri, httpConnectionTimeout);
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
        SyntaxFormat format = ui.getTypeInformationInputFormat();

        // Reset the type system
        Types.reset();

        if (format == SyntaxFormat.RDFS) {
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
        SyntaxFormat knowledgeBaseFormat = ui.getKnowledgeBaseInputFormat();
        String knowledgeBase = ui.getKnowledgeBaseTextAreaText();

        if (!knowledgeBase.isEmpty()) {
            if (knowledgeBaseFormat == SyntaxFormat.RULEML) {
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
        String msg;
        if (e.getMessage() != null) {
            msg = e.getMessage();
        } else if (e.getCause() != null && e.getCause().getMessage() != null) {
            msg = e.getCause().getMessage();
        } else {
            msg = String.format("Unknown error occured (%s)", e.getClass().getName());
        }

        JOptionPane.showMessageDialog(ui.getFrmOoJdrew(), msg, "Error", JOptionPane.ERROR_MESSAGE);
        logger.error(msg);
    }

    protected void showInformationDialog(String title, String message) {
        JOptionPane.showMessageDialog(ui.getFrmOoJdrew(), message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    protected void validateRuleMLDocument() {
        String content = ui.getKnowledgeBaseTextAreaText();
        RuleMLValidator validator = new RuleMLValidator();
        try {
            validator.validateRuleMLDocument(content);
            showInformationDialog("Successfully validated document.", "Validation successful");
        } catch (Exception e) {
            defaultExceptionHandler(e);
        }
    }

    protected void normalizeRuleMLDocument() {
        RuleMLFormat rmlFormat = config.getRuleMLFormat();
        String input = ui.getTextForCurrentEditingTab();

        RuleMLNormalizer normalizer = new RuleMLNormalizer();
        try {
            String normalizedRuleML = normalizer.normalize(input, rmlFormat);
            ui.setTextForCurrentEditingTab(normalizedRuleML);
        } catch (Exception e) {
            defaultExceptionHandler(e);
        }
    }
}
