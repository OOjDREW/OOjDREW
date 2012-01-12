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
package org.ruleml.oojdrew.BottomUp;

import java.awt.EventQueue;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.ruleml.oojdrew.Config;
import org.ruleml.oojdrew.Configuration;
import org.ruleml.oojdrew.SyntaxFormat;
import org.ruleml.oojdrew.GUI.AbstractUIApp;
import org.ruleml.oojdrew.GUI.BottomUpUI;
import org.ruleml.oojdrew.GUI.DebugConsole;
import org.ruleml.oojdrew.GUI.PreferenceDialogUI;
import org.ruleml.oojdrew.GUI.PreferenceManager;
import org.ruleml.oojdrew.parsing.POSLParser;
import org.ruleml.oojdrew.parsing.RDFSParser;
import org.ruleml.oojdrew.parsing.RuleMLFormat;
import org.ruleml.oojdrew.parsing.RuleMLParser;
import org.ruleml.oojdrew.parsing.RuleMLTagNames;
import org.ruleml.oojdrew.parsing.SubsumesParser;
import org.ruleml.oojdrew.util.DefiniteClause;
import org.ruleml.oojdrew.util.SymbolTable;
import org.ruleml.oojdrew.util.Util;
import org.ruleml.oojdrew.xml.XmlUtils;

public class BottomUpApp extends AbstractUIApp {

    public static void main(String[] args) {
        // The look and feel must be set before any UI objects are constructed
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a BottomUp application using the factory method
        BottomUpApp app = BottomUpApp.getBottomUpApp();

        // Start it's event loop
        app.run();
    }

    private BottomUpApp(Configuration config, PreferenceManager preferenceManager,
            PreferenceDialogUI preferenceDialogUI, BottomUpUI bottomUpUI, Logger logger,
            DebugConsole debugConsole, RDFSParser rdfsParser, POSLParser poslParser, RuleMLParser rmlParser,
            SubsumesParser subsumesParser, ForwardReasoner forwardReasoner) {

        super(config, preferenceManager, preferenceDialogUI, bottomUpUI, logger, debugConsole, rdfsParser,
                poslParser, rmlParser, subsumesParser, forwardReasoner);
    }

    private void run() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                logger.debug("Entering event loop");
                ui.setFrameVisible(true);
            }
        });
    }

    private static BottomUpApp getBottomUpApp() {
        // Construct dependencies
        Configuration config = new Config(BottomUpApp.class);
        PreferenceManager preferenceManager = new PreferenceManager(config);
        BottomUpUI bottomUpUI = new BottomUpUI();
        PreferenceDialogUI preferenceDialogUI = new PreferenceDialogUI();

        // Create DebugConsole and logger
        DebugConsole debugConsole = new DebugConsole();

        BasicConfigurator.configure();
        Logger logger = Logger.getRootLogger();

        // Create the parsers
        RDFSParser rdfsParser = new RDFSParser();
        POSLParser poslParser = new POSLParser();
        RuleMLParser rmlParser = new RuleMLParser(config);
        SubsumesParser subsumesParser = new SubsumesParser();

        // Create a forward reasoning engine
        ForwardReasoner forwardReasoner = new ForwardReasoner();
        
        // Set global configuration variables
        org.ruleml.oojdrew.Config.PRINTGENOIDS = false;

        // Create a BottomUp application
        BottomUpApp bottomUpApp = new BottomUpApp(config, preferenceManager, preferenceDialogUI, bottomUpUI,
                logger, debugConsole, rdfsParser, poslParser, rmlParser, subsumesParser, forwardReasoner);

        return bottomUpApp;
    }

    private BottomUpUI getUI() {
        return (BottomUpUI) ui;
    }

    private ForwardReasoner getReasoner() {
        return (ForwardReasoner) reasoner;
    }
    
    private void setReasoner(ForwardReasoner reasoner) {
        this.reasoner = reasoner;
    }

    private boolean updateReasonerLoopCounter() {
        boolean successful = false;
        try {
            String loopCounter = getUI().getInputLoopCount();
            getReasoner().setLoopCounter(loopCounter);
            successful = true;
        } catch (Exception ex1) {
            JOptionPane.showMessageDialog(ui.getFrmOoJdrew(), ex1.getMessage(),
                    "Invalid Number Input", JOptionPane.ERROR_MESSAGE);
        }
        return successful;
    }

    public void checkKnowledgeBaseForStratificiation() {
        boolean strat = getReasoner().isStratifiable();

        // Data base is not stratifiable
        if (!strat) {
            // Allowing the user to see the details on why stratification failed
            int dlgResult = JOptionPane.showConfirmDialog(ui.getFrmOoJdrew(),
                    "Knowledge base is not stratifiable. \nWould you like to see more detials?\n",
                    "Non-Stratfiable", JOptionPane.YES_NO_OPTION);
            // If the user wants to see the details, he can
            if (dlgResult == JOptionPane.YES_OPTION) {
                Vector msg = getReasoner().getMessage();
                Iterator msgIterator = msg.iterator();
                String message = "";
                int count = 1;
                while (msgIterator.hasNext()) {
                    message = message + count + ")" + (String) msgIterator.next() + "\n";
                    count++;
                }
                showInformationDialog("Stratification Violations", message);
            }
        }
        // Tells the user that the knowledge base is stratifiable
        if (strat) {
            showInformationDialog("Stratfiable", "Knowledge base is stratifiable.");
        }
    }
    
    @Override
    public void parseKnowledgeBase() {
        SymbolTable.reset();
        setReasoner(new ForwardReasoner());
        
        super.parseKnowledgeBase();
    }

    public void runForwardReasoner() {
        if (!updateReasonerLoopCounter()) {
            return;
        }

        SyntaxFormat outputFormat = getUI().getOutputFormat();
        RuleMLFormat rmlFormat = config.getRuleMLFormat();
        boolean separateFacts = getUI().getSeparateFactsEnabled();
        boolean printRules = getUI().getPrintRulesEnabled();

        // Run reasoner
        getReasoner().runForwardReasoner();
        System.out.println("Ran Reasoner");

        Hashtable oldFacts = getReasoner().getOldFacts();
        if (oldFacts.containsKey(SymbolTable.IINCONSISTENT)) {
            Vector v = (Vector) oldFacts.get(SymbolTable.IINCONSISTENT);
            if (v.size() > 0) {
                logger.warn("Knowledge base is inconsistent.");
                JOptionPane.showMessageDialog(ui.getFrmOoJdrew(),
                        "Knowledge base is inconsistent", "Consistency Check",
                        JOptionPane.WARNING_MESSAGE);
            }
        }

        Hashtable rules = getReasoner().getRules();
        if (separateFacts) {
            StringBuilder stringBuilder = new StringBuilder();

            String facts = getReasoner().printClauses(outputFormat, rmlFormat);
            stringBuilder.append(facts);

            if (printRules) {
                stringBuilder.append(Util.NEWLINE);
                stringBuilder.append("% Rules:");
                stringBuilder.append(Util.NEWLINE);
                appendString(outputFormat, rmlFormat, rules.elements(),
                        stringBuilder);
            }
            getUI().setOutputTextAreaText(stringBuilder.toString());
            
        } else if (outputFormat == SyntaxFormat.POSL) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Util.NEWLINE);
            stringBuilder.append("% Derived Facts:");
            stringBuilder.append(Util.NEWLINE);;
            appendString(outputFormat, rmlFormat, oldFacts.elements(),
                    stringBuilder);
            // Add the option to print rules or not
            if (printRules) {
                stringBuilder.append(Util.NEWLINE);
                stringBuilder.append("% Rules:");
                stringBuilder.append(Util.NEWLINE);
                appendString(outputFormat, rmlFormat, rules.elements(),
                        stringBuilder);
            }
            getUI().setOutputTextAreaText(stringBuilder.toString());
            
        } else {
            RuleMLTagNames rmlTags = new RuleMLTagNames(rmlFormat);
            // Can add here to change the current parser so that
            // you can exchange between RuleML versions
            Element assertElement = new Element(rmlTags.ASSERT);
            Element assertChild = null;
            if (rmlFormat == RuleMLFormat.RuleML88) {
                assertChild = new Element(rmlTags.AND);
            } else {
                assertChild = new Element(rmlTags.RULEBASE);
            }
            Attribute a = new Attribute(rmlTags.MAPCLOSURE, rmlTags.UNIVERSAL);
            assertChild.addAttribute(a);
            assertElement.appendChild(assertChild);

            appendRuleML(assertChild, oldFacts.elements(), rmlFormat);
            // Add the option to print rules or not
            if (printRules) {
                appendRuleML(assertChild, rules.elements(), rmlFormat);
            }

            String rmlString = XmlUtils.elementToString(assertElement);
            getUI().setOutputTextAreaText(rmlString);
        }
    }

    private void appendString(SyntaxFormat outputFormat, RuleMLFormat rmlFormat,
            Enumeration enumeration, StringBuilder stringBuilder) {
        while (enumeration.hasMoreElements()) {
            Vector children = (Vector) enumeration.nextElement();
            Iterator it = children.iterator();
            while (it.hasNext()) {
                DefiniteClause dc = (DefiniteClause) it.next();

                if (outputFormat == SyntaxFormat.POSL) {
                    stringBuilder.append(dc.toPOSLString());
                } else {
                    stringBuilder.append(dc.toRuleMLString(rmlFormat));
                }
                stringBuilder.append("\n");
            }
        }
    }

    private void appendRuleML(Element element, Enumeration enumeration,
            RuleMLFormat rmlFormat) {
        while (enumeration.hasMoreElements()) {
            Vector children = (Vector) enumeration.nextElement();
            Iterator it = children.iterator();
            while (it.hasNext()) {
                DefiniteClause dc = (DefiniteClause) it.next();

                if (dc.atoms[0].symbol == SymbolTable.IINCONSISTENT) {
                    continue;
                }
                element.appendChild(dc.toRuleML(rmlFormat));
            }
        }
    }
}
