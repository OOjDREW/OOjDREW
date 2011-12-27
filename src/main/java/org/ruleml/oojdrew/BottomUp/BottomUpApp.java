package org.ruleml.oojdrew.BottomUp;

import java.awt.EventQueue;
import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.ruleml.oojdrew.Config;
import org.ruleml.oojdrew.Configuration;
import org.ruleml.oojdrew.BottomUp.ForwardReasoner.RuleDescriptionLanguage;
import org.ruleml.oojdrew.GUI.AbstractUIApp;
import org.ruleml.oojdrew.GUI.BottomUpUI;
import org.ruleml.oojdrew.GUI.DebugConsole;
import org.ruleml.oojdrew.GUI.PreferenceDialogUI;
import org.ruleml.oojdrew.GUI.PreferenceManager;
import org.ruleml.oojdrew.GUI.TextPaneAppender;
import org.ruleml.oojdrew.GUI.UISettingsController;
import org.ruleml.oojdrew.parsing.InputFormat;
import org.ruleml.oojdrew.parsing.POSLParser;
import org.ruleml.oojdrew.parsing.RDFSParser;
import org.ruleml.oojdrew.parsing.RuleMLFormat;
import org.ruleml.oojdrew.parsing.RuleMLParser;
import org.ruleml.oojdrew.parsing.RuleMLTagNames;
import org.ruleml.oojdrew.parsing.SubsumesParser;
import org.ruleml.oojdrew.util.DefiniteClause;
import org.ruleml.oojdrew.util.SymbolTable;

public class BottomUpApp extends AbstractUIApp implements UISettingsController,
        PreferenceChangeListener {

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

    private BottomUpApp(Configuration config,
            PreferenceManager preferenceManager, BottomUpUI bottomUpUI,
            PreferenceDialogUI preferenceDialogUI, DebugConsole debugConsole,
            RDFSParser rdfsParser, POSLParser poslParser,
            RuleMLParser rmlParser, SubsumesParser subsumesParser,
            ForwardReasoner forwardReasoner) {

        super(config, preferenceManager, bottomUpUI, preferenceDialogUI,
                debugConsole, rdfsParser, poslParser, rmlParser,
                subsumesParser, forwardReasoner);

        ui.setController(this);
        preferenceDialogUI.setSettingsController(this);
        config.addPreferenceChangeListener(this);
        preferenceChange(null);
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
        Configuration config = new Config();
        PreferenceManager preferenceManager = new PreferenceManager(config);
        BottomUpUI bottomUpUI = new BottomUpUI();
        PreferenceDialogUI preferenceDialogUI = new PreferenceDialogUI();

        // Create DebugConsole and logger
        DebugConsole debugConsole = new DebugConsole();

        BasicConfigurator.configure();
        Logger root = Logger.getRootLogger();
        root.setLevel(Level.DEBUG);
        TextPaneAppender tpa = new TextPaneAppender(new PatternLayout(
                "%-5p %d [%t]:  %m%n"), "Debug");
        tpa.setTextPane(debugConsole.getTextPane());
        root.addAppender(tpa);

        // Create the parsers
        RDFSParser rdfsParser = new RDFSParser();
        POSLParser poslParser = new POSLParser();
        RuleMLParser rmlParser = new RuleMLParser(config);
        SubsumesParser subsumesParser = new SubsumesParser();

        // Create a forward reasoning engine
        ForwardReasoner forwardReasoner = new ForwardReasoner();

        // Create a BottomUp application
        BottomUpApp bottomUpApp = new BottomUpApp(config, preferenceManager,
                bottomUpUI, preferenceDialogUI, debugConsole, rdfsParser,
                poslParser, rmlParser, subsumesParser, forwardReasoner);

        return bottomUpApp;
    }

    private BottomUpUI getUI() {
        return (BottomUpUI) super.ui;
    }

    private ForwardReasoner getReasoner() {
        return (ForwardReasoner) super.reasoner;
    }

    private void setReasoner(ForwardReasoner reasoner) {
        super.reasoner = reasoner;
    }

    private boolean updateReasonerLoopCounter() {
        boolean successful = false;
        try {
            getReasoner().setLoopCounter(getUI().getInputLoopCount());
            successful = true;
        } catch (Exception ex1) {
            JOptionPane.showMessageDialog(ui.getFrmOoJdrew(), ex1.getMessage(),
                    "Invalid Number Input", JOptionPane.ERROR_MESSAGE);
        }
        return successful;
    }

    private void checkStratificiation() {
        boolean strat = getReasoner().isStratifiable();
        System.out.println("Is stratifiable: " + strat);
        // Data base is not stratifiable
        if (!strat) {
            // Allowing the user to see the details on why stratification failed
            int ans = JOptionPane
                    .showConfirmDialog(
                            ui.getFrmOoJdrew(),
                            "Knowledge base is not stratifiable. \nWould you like to see more detials?\n",
                            "Non-Stratfiable", JOptionPane.YES_NO_OPTION);
            // If the user wants to see the details, he can
            if (ans == 0) {
                Vector msg = getReasoner().getMessage();
                Iterator msgIterator = msg.iterator();
                String message = "";
                int count = 1;
                while (msgIterator.hasNext()) {
                    message = message + count + ")"
                            + (String) msgIterator.next() + "\n";
                    count++;
                }
                JOptionPane.showMessageDialog(ui.getFrmOoJdrew(), message,
                        "Stratification Violations",
                        JOptionPane.INFORMATION_MESSAGE);

            }
        }
        // Tells the user that the knowledge base is stratifiable
        if (strat) {
            JOptionPane.showMessageDialog(ui.getFrmOoJdrew(),
                    "Knowledge base is stratifiable.", "Stratfiable",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void parseKnowledgeBase() {
        if (updateReasonerLoopCounter()) {
            // Applied from old BottomUpGUI
            // TODO: Check if new instance is required and if loop counter
            // should be set on new instance
            setReasoner(new ForwardReasoner());

            super.parseKnowledgeBase();

            // Testing for stratification if user wants to.
            if (getUI().getStratificationCheckEnabled()) {
                checkStratificiation();
            }
        }
    }

    @Override
    public void parseTypeInformation() {
        if (updateReasonerLoopCounter()) {
            super.parseTypeInformation();
        }
    }

    public void runForwardReasoner() {
        if (!updateReasonerLoopCounter()) {
            return;
        }

        RuleMLFormat rmlFormat = config.getSelectedRuleMLFormat();
        InputFormat inputFormat = ui.getKnowledgeBaseInputFormat();
        getReasoner().printClauses(RuleDescriptionLanguage.POSL, rmlFormat);

        // Run reasoner
        getReasoner().runForwardReasoner();

        System.out.println("Ran Reasoner");

        Hashtable oldFacts = getReasoner().getOldFacts();

        if (oldFacts.containsKey(SymbolTable.IINCONSISTENT)) {
            Vector v = (Vector) oldFacts.get(SymbolTable.IINCONSISTENT);
            if (v.size() > 0) {
                // logger.warn("Knowledge base is inconsistent.");
                JOptionPane.showMessageDialog(ui.getFrmOoJdrew(),
                        "Knowledge base is inconsistent", "Consistency Check",
                        JOptionPane.WARNING_MESSAGE);
            }
        }

        Hashtable rules = getReasoner().getRules();
        Enumeration enumOldFact = oldFacts.elements();

        if (getUI().getSeparateFactsEnabled()) {
            StringBuilder stringBuilder = new StringBuilder();
            if (inputFormat == InputFormat.InputFormatPOSL) {
                String poslFacts = getReasoner().printClauses(
                        RuleDescriptionLanguage.POSL, rmlFormat);
                stringBuilder.append(poslFacts);

                if (getUI().getPrintRulesEnabled()) {
                    stringBuilder.append("\n % Rules : \n");
                    enumOldFact = rules.elements();
                    while (enumOldFact.hasMoreElements()) {
                        Vector rulesv = (Vector) enumOldFact.nextElement();
                        Iterator it = rulesv.iterator();
                        while (it.hasNext()) {
                            DefiniteClause dc = (DefiniteClause) it.next();
                            stringBuilder.append(dc.toPOSLString() + "\n");
                        }
                    }
                }
                getUI().setOutputTextAreaText(stringBuilder.toString());
            } else {
                String ruleMLFacts = getReasoner().printClauses(
                        RuleDescriptionLanguage.RuleML, rmlFormat);
                stringBuilder.append(ruleMLFacts);

                if (getUI().getPrintRulesEnabled()) {

                    stringBuilder.append("\n% Rules : \n");
                    enumOldFact = rules.elements();
                    while (enumOldFact.hasMoreElements()) {
                        Vector rulesv = (Vector) enumOldFact.nextElement();
                        Iterator it = rulesv.iterator();
                        while (it.hasNext()) {
                            DefiniteClause dc = (DefiniteClause) it.next();
                            stringBuilder.append(dc.toRuleMLString(rmlFormat)
                                    + "\n");
                        }
                    }
                }

                getUI().setOutputTextAreaText(stringBuilder.toString());
            }
            return;
        }

        if (inputFormat == InputFormat.InputFormatPOSL) {

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("% Derived Facts:\n\n");
            enumOldFact = oldFacts.elements();
            while (enumOldFact.hasMoreElements()) {
                Vector facts = (Vector) enumOldFact.nextElement();
                Iterator it = facts.iterator();
                while (it.hasNext()) {
                    DefiniteClause dc = (DefiniteClause) it.next();
                    stringBuilder.append(dc.toPOSLString() + "\n");
                }
            }
            // Add the option to print rules or not
            if (getUI().getPrintRulesEnabled()) {
                stringBuilder.append("\n % Rules : \n");
                enumOldFact = rules.elements();
                while (enumOldFact.hasMoreElements()) {
                    Vector rulesv = (Vector) enumOldFact.nextElement();
                    Iterator it = rulesv.iterator();
                    while (it.hasNext()) {
                        DefiniteClause dc = (DefiniteClause) it.next();
                        stringBuilder.append(dc.toPOSLString() + "\n");
                    }
                }
            }

            getUI().setOutputTextAreaText(stringBuilder.toString());
        }

        else {
            RuleMLTagNames rmlTagNames = new RuleMLTagNames(rmlFormat);
            // Can add here to change the current parser so that
            // you can exchange between ruleml 0.88 and 0.91
            Element assertElement = new Element(rmlTagNames.ASSERT);
            Element assertChild = null;
            if (rmlFormat == RuleMLFormat.RuleML91) {
                assertChild = new Element(rmlTagNames.RULEBASE);
            }
            if (rmlFormat == RuleMLFormat.RuleML88) {
                assertChild = new Element("And");
            }
            Attribute a = new Attribute(rmlTagNames.MAPCLOSURE,
                    rmlTagNames.UNIVERSAL);
            assertChild.addAttribute(a);
            assertElement.appendChild(assertChild);

            Enumeration e = oldFacts.elements();
            while (e.hasMoreElements()) {
                Vector facts = (Vector) e.nextElement();
                Iterator it = facts.iterator();
                while (it.hasNext()) {
                    DefiniteClause dc = (DefiniteClause) it.next();

                    if (dc.atoms[0].symbol == SymbolTable.IINCONSISTENT) {
                        continue;
                    }
                    assertChild.appendChild(dc.toRuleML(rmlFormat));
                }
            }
            // Add the option to print rules or not
            if (getUI().getPrintRulesEnabled()) {
                e = rules.elements();
                while (e.hasMoreElements()) {
                    Vector rulesv = (Vector) e.nextElement();
                    Iterator it = rulesv.iterator();
                    while (it.hasNext()) {
                        DefiniteClause dc = (DefiniteClause) it.next();
                        if (dc.atoms[0].symbol == SymbolTable.IINCONSISTENT) {
                            continue;
                        }
                        assertChild.appendChild(dc.toRuleML(rmlFormat));
                    }
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            nu.xom.Serializer sl = new nu.xom.Serializer(outputStream);
            sl.setIndent(3);
            sl.setLineSeparator("\n");
            try {
                Document doc = new Document(assertElement);
                sl.write(doc);
            } catch (java.io.IOException ex) {
                // this.logger.error(ex.getMessage(), ex);
                JOptionPane.showMessageDialog(ui.getFrmOoJdrew(),
                        ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            getUI().setOutputTextAreaText(outputStream.toString());
        }
    }
}
