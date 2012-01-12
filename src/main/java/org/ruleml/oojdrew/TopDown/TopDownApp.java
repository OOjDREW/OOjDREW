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

package org.ruleml.oojdrew.TopDown;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import nu.xom.Elements;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.ruleml.oojdrew.Config;
import org.ruleml.oojdrew.Configuration;
import org.ruleml.oojdrew.SyntaxFormat;
import org.ruleml.oojdrew.GUI.AbstractUIApp;
import org.ruleml.oojdrew.GUI.DebugConsole;
import org.ruleml.oojdrew.GUI.PreferenceDialogUI;
import org.ruleml.oojdrew.GUI.PreferenceManager;
import org.ruleml.oojdrew.GUI.TopDownUI;
import org.ruleml.oojdrew.parsing.POSLParser;
import org.ruleml.oojdrew.parsing.RDFSParser;
import org.ruleml.oojdrew.parsing.RuleMLParser;
import org.ruleml.oojdrew.parsing.SubsumesParser;
import org.ruleml.oojdrew.parsing.TypeQueryParserPOSL;
import org.ruleml.oojdrew.parsing.TypeQueryParserRuleML;
import org.ruleml.oojdrew.util.DefiniteClause;
import org.ruleml.oojdrew.util.LUBGLBStructure;
import org.ruleml.oojdrew.util.QueryTypes;
import org.ruleml.oojdrew.util.SubsumesStructure;
import org.ruleml.oojdrew.util.SymbolTable;
import org.ruleml.oojdrew.util.Term;

public class TopDownApp extends AbstractUIApp {

    // TODO: Rewrite all code that uses the following variables
    // These variables were copied from the old UI
    private Iterator queryResultIterator;
    private Iterator typeQueryResultIterator;
    private boolean t1Var;
    private boolean t2Var;
    private String term1VarName;
    private String term2VarName;
    private SubsumesStructure subPlus;
    private SubsumesStructure sub;
    private LUBGLBStructure lub;
    private LUBGLBStructure glb;

    public static void main(String[] args) {
        // The look and feel must be set before any UI objects are constructed
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a TopDownApp using the factory method...
        TopDownApp app = TopDownApp.getTopDownApp();

        // ... and start it's event loop
        app.run();
    }

    public void run() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                logger.debug("Entering event loop");
                getUI().setFrameVisible(true);
            }
        });
    }

    public static TopDownApp getTopDownApp() {
        // Construct dependencies
        Configuration config = new Config(TopDownApp.class);
        PreferenceManager preferenceManager = new PreferenceManager(config);
        TopDownUI topDownUI = new TopDownUI();
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

        // Create the reasoning engine
        BackwardReasoner backwardReasoner = new BackwardReasoner();

        // Create TopDownApp
        TopDownApp topDownApp = new TopDownApp(config, preferenceManager,
                preferenceDialogUI, topDownUI, logger, debugConsole, rdfsParser,
                poslParser, rmlParser, subsumesParser, backwardReasoner);

        return topDownApp;
    }

    private TopDownApp(Configuration config, PreferenceManager preferenceManager,
            PreferenceDialogUI preferenceDialogUI, TopDownUI topDownUI, Logger logger,
            DebugConsole debugConsole, RDFSParser rdfsParser, POSLParser poslParser, RuleMLParser rmlParser,
            SubsumesParser subsumesParser, BackwardReasoner backwardReasoner) {

        super(config, preferenceManager, preferenceDialogUI, topDownUI, logger, debugConsole, rdfsParser,
                poslParser, rmlParser, subsumesParser, backwardReasoner);
    }

    private TopDownUI getUI() {
        return (TopDownUI) super.ui;
    }

    private BackwardReasoner getReasoner() {
        return (BackwardReasoner) super.reasoner;
    }

    private void setReasoner(BackwardReasoner reasoner) {
        super.reasoner = reasoner;
    }

    @Override
    public void parseKnowledgeBase() {
        getUI().setBtnNextSolutionEnabled(false);

        SymbolTable.reset();
        reasoner.clearClauses();
        
        super.parseKnowledgeBase();
    }

    public void issueQuery() {
        String query = getUI().getQueryTextAreaText();
        SyntaxFormat format = getUI().getQueryInputFormat();
        boolean typeQuery = getUI().getTypeQueryCheckboxSelected();

        if (format == SyntaxFormat.RULEML) {
            if (typeQuery) {
                issueRuleMLTypeQuery(query);
            } else {
                issueRuleMLQuery(query);
            }
        } else {
            if (typeQuery) {
                issuePOSLTypeQuery(query);
            } else {
                issuePOSLQuery(query);
            }
        }
    }

    private void issueRuleMLQuery(String query) {
        rmlParser.clear();

        try {
            DefiniteClause dc = rmlParser.parseRuleMLQuery(query);
            processQuery(dc);
        } catch (Exception e) {
            defaultExceptionHandler(e);
        }
    }

    private void issuePOSLQuery(String query) {
        DefiniteClause dc;
        try {
            dc = poslParser.parseQueryString(query);
            processQuery(dc);
        } catch (Exception e) {
            defaultExceptionHandler(e);
        }
    }

    // TODO: This method was copied from the old GUI and has been modified to
    // work with the current code base. This code should be rewritten in a much
    // cleaner fashion.
    private void processQuery(DefiniteClause dc) {
        // TODO: Find a way to use the existing backwardReasoner (for the sake
        // of dependency injection)
        setReasoner(new BackwardReasoner(getReasoner().clauses,
                getReasoner().oids));

        queryResultIterator = getReasoner()
                .iterativeDepthFirstSolutionIterator(dc);
        getUI().setBtnNextSolutionEnabled(true);

        if (!queryResultIterator.hasNext()) {
            javax.swing.tree.DefaultMutableTreeNode root = new DefaultMutableTreeNode(
                    "unknown");
            javax.swing.tree.DefaultTreeModel dtm = new DefaultTreeModel(root);

            getUI().setSolutionTreeModel(dtm);
            getUI().setBtnNextSolutionEnabled(false);

            getUI().setVariableBindingsTableModel(
                    new javax.swing.table.DefaultTableModel(new Object[][] { {
                            null, null } }, new String[] { "Variable",
                            "Binding" }));

        } else {
            BackwardReasoner.GoalList gl = (BackwardReasoner.GoalList) queryResultIterator
                    .next();

            Hashtable varbind = gl.varBindings;

            javax.swing.tree.DefaultMutableTreeNode root = getReasoner()
                    .toTree();

            root.setAllowsChildren(true);

            javax.swing.tree.DefaultTreeModel dtm = new DefaultTreeModel(root);

            getUI().setSolutionTreeModel(dtm);

            int i = 0;
            Object[][] rowdata = new Object[varbind.size()][2];

            Enumeration e = varbind.keys();

            while (e.hasMoreElements()) {
                Object k = e.nextElement();
                Object val = varbind.get(k);
                String ks = (String) k;
                rowdata[i][0] = ks;
                rowdata[i][1] = val;
                i++;
            }
            String[] colnames = new String[] { "Variable", "Binding" };

            getUI().setVariableBindingsTableModel(
                    new javax.swing.table.DefaultTableModel(rowdata, colnames));
        }

        if (!queryResultIterator.hasNext()) {
            getUI().setBtnNextSolutionEnabled(false);
        }
    }

    // TODO: This method was copied from the old GUI and has been modified to
    // work with the current code base. This code should be rewritten in a much
    // cleaner fashion.

    public void nextSolution() {
        boolean typeQuery = getUI().getTypeQueryCheckboxSelected();

        if (typeQuery) {
            nextSolutionForTypeQuery();
        } else {
            nextSolutionForQuery();
        }
    }

    public void nextSolutionForQuery() {
        BackwardReasoner.GoalList gl = (BackwardReasoner.GoalList) queryResultIterator
                .next();
        // System.out.println(gl.toString());
        Hashtable varbind = gl.varBindings;
        javax.swing.tree.DefaultMutableTreeNode root = getReasoner().toTree();
        javax.swing.tree.DefaultTreeModel dtm = new DefaultTreeModel(root);

        // logger.debug("Getting next solution: ");

        getUI().setSolutionTreeModel(dtm);

        int i = 0;
        Object[][] rowdata = new Object[varbind.size()][2];
        Enumeration e = varbind.keys();
        while (e.hasMoreElements()) {
            Object k = e.nextElement();
            Object val = varbind.get(k);
            String ks = (String) k;
            rowdata[i][0] = ks;
            rowdata[i][1] = val;

            i++;
        }
        String[] colnames = new String[] { "Variable", "Binding" };

        getUI().setVariableBindingsTableModel(
                new javax.swing.table.DefaultTableModel(rowdata, colnames));

        if (!queryResultIterator.hasNext()) {
            getUI().setBtnNextSolutionEnabled(false);
        }
    }

    // TODO: This method was copied from the old GUI and has been modified to
    // work with the current code base. This code should be rewritten in a much
    // cleaner fashion.
    public void nextSolutionForTypeQuery() {
        // Var Ind
        if (t1Var == true && t2Var == false) {

            Object[][] rowdata = new Object[2][2];

            rowdata[0][0] = "?" + term1VarName;
            rowdata[0][1] = (String) typeQueryResultIterator.next();

            String[] colnames = new String[] { "Variable", "Binding" };

            getUI().setVariableBindingsTableModel(
                    new javax.swing.table.DefaultTableModel(rowdata, colnames));
        }
        // Ind Var
        if (t1Var == false && t2Var == true) {

            Object[][] rowdata = new Object[2][2];

            rowdata[0][0] = "?" + term2VarName;
            rowdata[0][1] = (String) typeQueryResultIterator.next();

            String[] colnames = new String[] { "Variable", "Binding" };

            getUI().setVariableBindingsTableModel(
                    new javax.swing.table.DefaultTableModel(rowdata, colnames));
        }
        // Var Var
        if (t1Var == true && t2Var == true) {

            Object[][] rowdata = new Object[2][2];

            rowdata[0][0] = "?" + term1VarName;
            rowdata[0][1] = (String) typeQueryResultIterator.next();

            rowdata[1][0] = "?" + term2VarName;
            rowdata[1][1] = (String) typeQueryResultIterator.next();

            String[] colnames = new String[] { "Variable", "Binding" };

            getUI().setVariableBindingsTableModel(
                    new javax.swing.table.DefaultTableModel(rowdata, colnames));
        }

        if (!typeQueryResultIterator.hasNext()) {
            getUI().setBtnNextSolutionEnabled(false);
        }
    }

    // TODO: This method was copied from the old GUI and has been modified to
    // work with the current code base. This code should be rewritten in a much
    // cleaner fashion.
    private void issueRuleMLTypeQuery(String query) {
        Object[][] resetRow = new Object[2][2];
        String[] resetCol = new String[] { "Variable", "Binding" };

        getUI().setVariableBindingsTableModel(
                new javax.swing.table.DefaultTableModel(resetRow, resetCol));

        getUI().setBtnNextSolutionEnabled(false);

        // It is an iterator that is used to map all the solutions to bindings
        typeQueryResultIterator = null;
        // Creating a QueryTypes objects
        QueryTypes typeQuery = new QueryTypes();

        if (query.equals("")) {
            return;
        }

        try {

            // need to get rid of this eventually
            t1Var = false;
            t2Var = false;
            term1VarName = "";
            term2VarName = "";

            getUI().setSolutionTextAreaText("");
            TypeQueryParserRuleML rmlTParser = new TypeQueryParserRuleML(query);
            Elements elements = rmlTParser.parseForPredicate();

            String predicate = rmlTParser.getPredicate();

            if (predicate.equalsIgnoreCase(TypeQueryParserRuleML.SUBSUMESPLUS)) {

                subPlus = rmlTParser
                        .parseElementsSubsumesAndSubsumesPlus(elements);

                // rel rel
                if (!subPlus.getSuperVar() && !subPlus.getSubVar()) {
                    getUI().setSolutionTextAreaText(
                            ""
                                    + typeQuery.isSuperClass(
                                            subPlus.getSuperName(),
                                            subPlus.getSubName()));
                    // var rel get all super classes
                } else if (subPlus.getSuperVar() && !subPlus.getSubVar()) {
                    t1Var = true;
                    term1VarName = subPlus.getSuperName();

                    String[] superClasses = typeQuery
                            .findAllSuperClasses(subPlus.getSubName());

                    Object[][] rowdata = new Object[2][2];
                    rowdata[0][0] = "?" + subPlus.getSuperName();
                    rowdata[0][1] = superClasses[0];
                    String[] colnames = new String[] { "Variable", "Binding" };
                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));

                    Vector<String> nextVector = new Vector<String>();
                    for (int i = 1; i < superClasses.length; i++)
                        nextVector.add(superClasses[i]);

                    typeQueryResultIterator = nextVector.iterator();

                    if (typeQueryResultIterator.hasNext()) {
                        getUI().setBtnNextSolutionEnabled(true);
                    }

                    // rel var get all sub classes
                } else if (!subPlus.getSuperVar() && subPlus.getSubVar()) {
                    t2Var = true;
                    term2VarName = subPlus.getSubName();
                    String[] subClasses = typeQuery.findAllSubClasses(subPlus
                            .getSuperName());

                    for (int i = 0; i < subClasses.length; i++)
                        System.out.println(subClasses[i]);

                    Object[][] rowdata = new Object[2][2];

                    rowdata[0][0] = "?" + subPlus.getSubName();
                    rowdata[0][1] = subClasses[0];

                    String[] colnames = new String[] { "Variable", "Binding" };

                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));

                    Vector nextVector = new Vector();
                    for (int i = 1; i < subClasses.length; i++)
                        nextVector.add(subClasses[i]);

                    typeQueryResultIterator = nextVector.iterator();

                    if (typeQueryResultIterator.hasNext()) {
                        getUI().setBtnNextSolutionEnabled(true);
                    }

                    // var var get all relations
                } else if (subPlus.getSuperVar() && subPlus.getSubVar()) {
                    t1Var = true;
                    t2Var = true;
                    term2VarName = subPlus.getSubName();
                    term1VarName = subPlus.getSuperName();

                    if (subPlus.getSuperName().equalsIgnoreCase(
                            subPlus.getSubName())) {
                        JOptionPane.showMessageDialog(getUI().getFrmOoJdrew(),
                                "Duplicate variable names not allowed",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Vector v1 = typeQuery.findAllSuperClassesOfEverything();
                    Vector v2 = typeQuery.findAllSubClassesOfEverything();
                    String sol = "";
                    Iterator vit1 = v1.iterator();
                    Iterator vit2 = v2.iterator();
                    int count = 0;
                    // Debug -> Prints out all the solutions for easy Copy and
                    // Paste
                    sol = "% Taxonomy Facts: \n";
                    while (vit1.hasNext()) {
                        count++;
                        sol = sol + "subsumes(" + vit1.next().toString() + ","
                                + vit1.next().toString() + ")." + "\n";
                    }
                    getUI().setSolutionTextAreaText(sol);
                    // Debug

                    typeQueryResultIterator = v1.iterator();

                    Object[][] rowdata = new Object[2][2];

                    rowdata[0][0] = "?" + subPlus.getSuperName();
                    rowdata[0][1] = (String) typeQueryResultIterator.next();

                    rowdata[1][0] = "?" + subPlus.getSubName();
                    rowdata[1][1] = (String) typeQueryResultIterator.next();

                    String[] colnames = new String[] { "Variable", "Binding" };

                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));

                    if (typeQueryResultIterator.hasNext()) {
                        getUI().setBtnNextSolutionEnabled(true);
                    }

                }
            } else if (predicate
                    .equalsIgnoreCase(TypeQueryParserRuleML.SUBSUMES)) {
                sub = rmlTParser.parseElementsSubsumesAndSubsumesPlus(elements);
                // rel rel
                if (!sub.getSuperVar() && !sub.getSubVar()) {
                    getUI().setSolutionTextAreaText(
                            ""
                                    + typeQuery.isDirectSuperClass(
                                            sub.getSuperName(),
                                            sub.getSubName()));
                    // var rel
                } else if (sub.getSuperVar() && !sub.getSubVar()) {
                    t1Var = true;
                    term1VarName = sub.getSuperName();

                    String[] superClasses = typeQuery.getDirectSuperClasses(sub
                            .getSubName());

                    for (int i = 0; i < superClasses.length; i++)
                        System.out.println(superClasses[i]);

                    Object[][] rowdata = new Object[2][2];

                    rowdata[0][0] = "?" + sub.getSuperName();
                    rowdata[0][1] = superClasses[0];

                    String[] colnames = new String[] { "Variable", "Binding" };

                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));
                    Vector nextVector = new Vector();
                    for (int i = 1; i < superClasses.length; i++)
                        nextVector.add(superClasses[i]);

                    typeQueryResultIterator = nextVector.iterator();

                    if (typeQueryResultIterator.hasNext()) {
                        getUI().setBtnNextSolutionEnabled(true);
                    }

                    // rel var
                } else if (!sub.getSuperVar() && sub.getSubVar()) {
                    t2Var = true;
                    term2VarName = sub.getSubName();

                    String[] subClasses = typeQuery.getDirectSubClasses(sub
                            .getSuperName());

                    for (int i = 0; i < subClasses.length; i++)
                        System.out.println(subClasses[i]);

                    Object[][] rowdata = new Object[2][2];

                    rowdata[0][0] = "?" + sub.getSubName();
                    rowdata[0][1] = subClasses[0];

                    String[] colnames = new String[] { "Variable", "Binding" };

                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));
                    Vector nextVector = new Vector();
                    for (int i = 1; i < subClasses.length; i++)
                        nextVector.add(subClasses[i]);

                    typeQueryResultIterator = nextVector.iterator();
                    if (typeQueryResultIterator.hasNext()) {
                        getUI().setBtnNextSolutionEnabled(true);
                    }
                    // var var
                } else if (sub.getSuperVar() && sub.getSubVar()) {
                    t1Var = true;
                    t2Var = true;
                    term2VarName = sub.getSubName();
                    term1VarName = sub.getSuperName();

                    if (sub.getSuperName().equalsIgnoreCase(sub.getSubName())) {
                        JOptionPane.showMessageDialog(getUI().getFrmOoJdrew(),
                                "Duplicate variable names not allowed",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Vector v1 = typeQuery
                            .findAllDirectSuperClassesOfEverything();
                    Vector v2 = typeQuery.findAllDirectSubClassesOfEverything();
                    String sol = "";
                    Iterator vit1 = v1.iterator();
                    Iterator vit2 = v2.iterator();
                    int count = 0;
                    // Debug -> Prints out all the solutions for easy Copy and
                    // Paste
                    sol = "% Taxonomy Facts: \n";
                    while (vit1.hasNext()) {
                        count++;
                        sol = sol + "subsumes(" + vit1.next().toString() + ","
                                + vit1.next().toString() + ")." + "\n";
                    }
                    getUI().setSolutionTextAreaText(sol);
                    // Debug

                    typeQueryResultIterator = v1.iterator();

                    Object[][] rowdata = new Object[2][2];

                    rowdata[0][0] = "?" + sub.getSuperName();
                    rowdata[0][1] = (String) typeQueryResultIterator.next();

                    rowdata[1][0] = "?" + sub.getSubName();
                    rowdata[1][1] = (String) typeQueryResultIterator.next();

                    String[] colnames = new String[] { "Variable", "Binding" };

                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));

                    if (typeQueryResultIterator.hasNext()) {
                        getUI().setBtnNextSolutionEnabled(true);
                    }

                }

            } else if (predicate.equalsIgnoreCase(TypeQueryParserRuleML.LUB)) {

                lub = rmlTParser.parseElementsGLBandLUB(elements);

                if (lub.getResultVar()) {

                    ArrayList<String> terms = lub.getTerms();

                    String[] lubArray = new String[terms.size()];

                    for (int i = 0; i < terms.size(); i++)
                        lubArray[i] = terms.get(i);

                    String leastUpperBound = typeQuery
                            .leastUpperBound(lubArray);

                    Object[][] rowdata = new Object[2][2];

                    rowdata[0][0] = "?" + lub.getResultVarName();
                    rowdata[0][1] = leastUpperBound;

                    String[] colnames = new String[] { "Variable", "Binding" };

                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));

                } else if (!lub.getResultVar()) {

                    Object[][] rowdata = new Object[2][2];
                    String[] colnames = new String[] { "Variable", "Binding" };
                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));
                    getUI().setSolutionTextAreaText("");

                    ArrayList<String> terms = lub.getTerms();

                    String[] lubArray = new String[terms.size()];

                    for (int i = 0; i < terms.size(); i++)
                        lubArray[i] = terms.get(i);

                    String leastUpperBound = typeQuery
                            .leastUpperBound(lubArray);
                    getUI().setSolutionTextAreaText(leastUpperBound);
                }
            } else if (predicate.equalsIgnoreCase(TypeQueryParserRuleML.GLB)) {

                glb = rmlTParser.parseElementsGLBandLUB(elements);

                if (glb.getResultVar()) {

                    ArrayList<String> terms = glb.getTerms();

                    String[] glbArray = new String[terms.size()];

                    for (int i = 0; i < terms.size(); i++)
                        glbArray[i] = terms.get(i);

                    String greatestLowerBound = typeQuery
                            .greatestLowerBound(glbArray);

                    Object[][] rowdata = new Object[2][2];

                    rowdata[0][0] = "?" + glb.getResultVarName();
                    rowdata[0][1] = greatestLowerBound;

                    String[] colnames = new String[] { "Variable", "Binding" };

                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));

                } else if (!glb.getResultVar()) {

                    Object[][] rowdata = new Object[2][2];
                    String[] colnames = new String[] { "Variable", "Binding" };
                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));
                    getUI().setSolutionTextAreaText("");

                    ArrayList<String> terms = glb.getTerms();

                    String[] glbArray = new String[terms.size()];

                    for (int i = 0; i < terms.size(); i++) {
                        glbArray[i] = terms.get(i);
                    }

                    String greatestLowerBound = typeQuery
                            .greatestLowerBound(glbArray);
                    getUI().setSolutionTextAreaText(greatestLowerBound);
                }

            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(getUI().getFrmOoJdrew(),
                    ex.getMessage(), "Type Query Parser Exeception",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // TODO: This method was copied from the old GUI and has been modified to
    // work with the current code base. This code should be rewritten in a much
    // cleaner fashion.
    private void issuePOSLTypeQuery(String query) {
        Object[][] resetRow = new Object[2][2];
        String[] resetCol = new String[] { "Variable", "Binding" };

        getUI().setVariableBindingsTableModel(
                new javax.swing.table.DefaultTableModel(resetRow, resetCol));

        getUI().setBtnNextSolutionEnabled(false);

        // It is an iterator that is used to map all the solutions to bindings
        typeQueryResultIterator = null;
        // Creating a QueryTypes objects
        QueryTypes typeQuery = new QueryTypes();

        if (query.equals("")) {
            return;
        }

        try {

            t1Var = false;
            t2Var = false;
            term1VarName = "";
            term2VarName = "";
            getUI().setSolutionTextAreaText("");

            TypeQueryParserPOSL poslTParser = new TypeQueryParserPOSL(query);
            Term[] queryTerms = poslTParser.parseForPredicate();
            String predicate = poslTParser.getPredicate();

            if (predicate.equalsIgnoreCase(TypeQueryParserPOSL.SUBSUMESPLUS)) {

                subPlus = poslTParser
                        .parseElementsSubsumesAndSubsumesPlus(queryTerms);

                // rel rel
                if (!subPlus.getSuperVar() && !subPlus.getSubVar()) {
                    getUI().setSolutionTextAreaText(
                            ""
                                    + typeQuery.isSuperClass(
                                            subPlus.getSuperName(),
                                            subPlus.getSubName()));
                    // var rel get all super classes
                } else if (subPlus.getSuperVar() && !subPlus.getSubVar()) {
                    t1Var = true;
                    term1VarName = subPlus.getSuperName();

                    String[] superClasses = typeQuery
                            .findAllSuperClasses(subPlus.getSubName());

                    Object[][] rowdata = new Object[2][2];
                    rowdata[0][0] = "?" + subPlus.getSuperName();
                    rowdata[0][1] = superClasses[0];
                    String[] colnames = new String[] { "Variable", "Binding" };
                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));

                    Vector<String> nextVector = new Vector<String>();
                    for (int i = 1; i < superClasses.length; i++)
                        nextVector.add(superClasses[i]);

                    typeQueryResultIterator = nextVector.iterator();

                    if (typeQueryResultIterator.hasNext()) {
                        getUI().setBtnNextSolutionEnabled(true);
                    }

                    // rel var get all sub classes
                } else if (!subPlus.getSuperVar() && subPlus.getSubVar()) {
                    t2Var = true;
                    term2VarName = subPlus.getSubName();
                    String[] subClasses = typeQuery.findAllSubClasses(subPlus
                            .getSuperName());

                    for (int i = 0; i < subClasses.length; i++)
                        System.out.println(subClasses[i]);

                    Object[][] rowdata = new Object[2][2];

                    rowdata[0][0] = "?" + subPlus.getSubName();
                    rowdata[0][1] = subClasses[0];

                    String[] colnames = new String[] { "Variable", "Binding" };

                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));
                    Vector nextVector = new Vector();
                    for (int i = 1; i < subClasses.length; i++)
                        nextVector.add(subClasses[i]);

                    typeQueryResultIterator = nextVector.iterator();

                    if (typeQueryResultIterator.hasNext()) {
                        getUI().setBtnNextSolutionEnabled(true);
                    }

                    // var var get all relations
                } else if (subPlus.getSuperVar() && subPlus.getSubVar()) {
                    t1Var = true;
                    t2Var = true;
                    term2VarName = subPlus.getSubName();
                    term1VarName = subPlus.getSuperName();

                    if (subPlus.getSuperName().equalsIgnoreCase(
                            subPlus.getSubName())) {
                        JOptionPane.showMessageDialog(getUI().getFrmOoJdrew(),
                                "Duplicate variable names not allowed",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Vector v1 = typeQuery.findAllSuperClassesOfEverything();
                    Vector v2 = typeQuery.findAllSubClassesOfEverything();
                    String sol = "";
                    Iterator vit1 = v1.iterator();
                    Iterator vit2 = v2.iterator();
                    int count = 0;
                    // Debug -> Prints out all the solutions for easy Copy and
                    // Paste
                    sol = "% Taxonomy Facts: \n";
                    while (vit1.hasNext()) {
                        count++;
                        sol = sol + "subsumes(" + vit1.next().toString() + ","
                                + vit1.next().toString() + ")." + "\n";
                    }
                    getUI().setSolutionTextAreaText(sol);
                    // Debug

                    typeQueryResultIterator = v1.iterator();

                    Object[][] rowdata = new Object[2][2];

                    rowdata[0][0] = "?" + subPlus.getSuperName();
                    rowdata[0][1] = (String) typeQueryResultIterator.next();

                    rowdata[1][0] = "?" + subPlus.getSubName();
                    rowdata[1][1] = (String) typeQueryResultIterator.next();

                    String[] colnames = new String[] { "Variable", "Binding" };

                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));

                    if (typeQueryResultIterator.hasNext()) {
                        getUI().setBtnNextSolutionEnabled(true);
                    }

                }
                // subsumesPlus
            } else if (predicate.equalsIgnoreCase(TypeQueryParserPOSL.SUBSUMES)) {
                sub = poslTParser
                        .parseElementsSubsumesAndSubsumesPlus(queryTerms);
                // rel rel
                if (!sub.getSuperVar() && !sub.getSubVar()) {
                    getUI().setSolutionTextAreaText(
                            ""
                                    + typeQuery.isDirectSuperClass(
                                            sub.getSuperName(),
                                            sub.getSubName()));
                    // var rel
                } else if (sub.getSuperVar() && !sub.getSubVar()) {
                    t1Var = true;
                    term1VarName = sub.getSuperName();

                    String[] superClasses = typeQuery.getDirectSuperClasses(sub
                            .getSubName());

                    for (int i = 0; i < superClasses.length; i++)
                        System.out.println(superClasses[i]);

                    Object[][] rowdata = new Object[2][2];

                    rowdata[0][0] = "?" + sub.getSuperName();
                    rowdata[0][1] = superClasses[0];

                    String[] colnames = new String[] { "Variable", "Binding" };

                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));
                    Vector nextVector = new Vector();
                    for (int i = 1; i < superClasses.length; i++)
                        nextVector.add(superClasses[i]);

                    typeQueryResultIterator = nextVector.iterator();

                    if (typeQueryResultIterator.hasNext()) {
                        getUI().setBtnNextSolutionEnabled(true);
                    }

                    // rel var
                } else if (!sub.getSuperVar() && sub.getSubVar()) {
                    t2Var = true;
                    term2VarName = sub.getSubName();

                    String[] subClasses = typeQuery.getDirectSubClasses(sub
                            .getSuperName());

                    for (int i = 0; i < subClasses.length; i++)
                        System.out.println(subClasses[i]);

                    Object[][] rowdata = new Object[2][2];

                    rowdata[0][0] = "?" + sub.getSubName();
                    rowdata[0][1] = subClasses[0];

                    String[] colnames = new String[] { "Variable", "Binding" };

                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));
                    Vector nextVector = new Vector();
                    for (int i = 1; i < subClasses.length; i++)
                        nextVector.add(subClasses[i]);

                    typeQueryResultIterator = nextVector.iterator();
                    if (typeQueryResultIterator.hasNext()) {
                        getUI().setBtnNextSolutionEnabled(true);
                    }
                    // var var
                } else if (sub.getSuperVar() && sub.getSubVar()) {
                    t1Var = true;
                    t2Var = true;
                    term2VarName = sub.getSubName();
                    term1VarName = sub.getSuperName();

                    if (sub.getSuperName().equalsIgnoreCase(sub.getSubName())) {
                        JOptionPane.showMessageDialog(getUI().getFrmOoJdrew(),
                                "Duplicate variable names not allowed",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Vector v1 = typeQuery
                            .findAllDirectSuperClassesOfEverything();
                    Vector v2 = typeQuery.findAllDirectSubClassesOfEverything();
                    String sol = "";
                    Iterator vit1 = v1.iterator();
                    Iterator vit2 = v2.iterator();
                    int count = 0;
                    // Debug -> Prints out all the solutions for easy Copy and
                    // Paste
                    sol = "% Taxonomy Facts: \n";
                    while (vit1.hasNext()) {
                        count++;
                        sol = sol + "subsumes(" + vit1.next().toString() + ","
                                + vit1.next().toString() + ")." + "\n";
                    }
                    getUI().setSolutionTextAreaText(sol);
                    // Debug

                    typeQueryResultIterator = v1.iterator();

                    Object[][] rowdata = new Object[2][2];

                    rowdata[0][0] = "?" + sub.getSuperName();
                    rowdata[0][1] = (String) typeQueryResultIterator.next();

                    rowdata[1][0] = "?" + sub.getSubName();
                    rowdata[1][1] = (String) typeQueryResultIterator.next();

                    String[] colnames = new String[] { "Variable", "Binding" };

                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));

                    if (typeQueryResultIterator.hasNext()) {
                        getUI().setBtnNextSolutionEnabled(true);
                    }
                }
            }// subsumes
            else if (predicate.equalsIgnoreCase(TypeQueryParserPOSL.LUB)) {

                lub = poslTParser.parseElementsGLBandLUB(queryTerms);

                if (lub.getResultVar()) {

                    ArrayList<String> terms = lub.getTerms();

                    String[] lubArray = new String[terms.size()];

                    for (int i = 0; i < terms.size(); i++)
                        lubArray[i] = terms.get(i);

                    String leastUpperBound = typeQuery
                            .leastUpperBound(lubArray);

                    Object[][] rowdata = new Object[2][2];

                    rowdata[0][0] = "?" + lub.getResultVarName();
                    rowdata[0][1] = leastUpperBound;

                    String[] colnames = new String[] { "Variable", "Binding" };

                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));

                } else if (!lub.getResultVar()) {

                    Object[][] rowdata = new Object[2][2];
                    String[] colnames = new String[] { "Variable", "Binding" };
                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));
                    getUI().setSolutionTextAreaText("");

                    ArrayList<String> terms = lub.getTerms();

                    String[] lubArray = new String[terms.size()];

                    for (int i = 0; i < terms.size(); i++)
                        lubArray[i] = terms.get(i);

                    String leastUpperBound = typeQuery
                            .leastUpperBound(lubArray);
                    getUI().setSolutionTextAreaText(leastUpperBound);
                }
            }// LUB
            else if (predicate.equalsIgnoreCase(TypeQueryParserRuleML.GLB)) {

                glb = poslTParser.parseElementsGLBandLUB(queryTerms);

                if (glb.getResultVar()) {

                    ArrayList<String> terms = glb.getTerms();

                    String[] glbArray = new String[terms.size()];

                    for (int i = 0; i < terms.size(); i++)
                        glbArray[i] = terms.get(i);

                    String greatestLowerBound = typeQuery
                            .greatestLowerBound(glbArray);

                    Object[][] rowdata = new Object[2][2];

                    rowdata[0][0] = "?" + glb.getResultVarName();
                    rowdata[0][1] = greatestLowerBound;

                    String[] colnames = new String[] { "Variable", "Binding" };

                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));

                } else if (!glb.getResultVar()) {

                    Object[][] rowdata = new Object[2][2];
                    String[] colnames = new String[] { "Variable", "Binding" };
                    getUI().setVariableBindingsTableModel(
                            new javax.swing.table.DefaultTableModel(rowdata,
                                    colnames));
                    getUI().setSolutionTextAreaText("");

                    ArrayList<String> terms = glb.getTerms();

                    String[] glbArray = new String[terms.size()];

                    for (int i = 0; i < terms.size(); i++) {
                        glbArray[i] = terms.get(i);
                    }

                    String greatestLowerBound = typeQuery
                            .greatestLowerBound(glbArray);
                    getUI().setSolutionTextAreaText(greatestLowerBound);
                }
            }// GLB

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(getUI().getFrmOoJdrew(),
                    ex.getMessage(), "Type Query Parser Exeception",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
