package org.ruleml.oojdrew.GUI;

import java.awt.Component;

import org.ruleml.oojdrew.SyntaxFormat;

public interface UI {
    
    void setController(AbstractUIApp controller);
    
    void updateUI();
    
    void setChckbxmntmValidateRulemlSelected(boolean enabled);
    boolean getChckbxmntmValidateRulemlSelected();
    
    void setTextForCurrentEditingTab(String text);
    void appendToCurrentEditingTab(String text);
    String getTextForCurrentEditingTab();
    
    String getTypeDefinitionTextAreaText();
    SyntaxFormat getTypeInformationInputFormat();
    
    SyntaxFormat getKnowledgeBaseInputFormat();
    String getKnowledgeBaseTextAreaText();
    
    Component getFrmOoJdrew();
    void setFrameVisible(boolean visible);    
}
