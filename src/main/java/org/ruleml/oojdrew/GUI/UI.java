package org.ruleml.oojdrew.GUI;

import java.awt.Component;

import org.ruleml.oojdrew.parsing.InputFormat;

public interface UI {
    
    void setController(AbstractUIApp controller);
    
    void updateUI();
    
    void setChckbxmntmValidateRulemlSelected(boolean enabled);
    boolean getChckbxmntmValidateRulemlSelected();
    
    void setTextForCurrentEditingTab(String text);
    void appendToCurrentEditingTab(String text);
    String getTextForCurrentEditingTab();
    
    String getTypeDefinitionTextAreaText();
    InputFormat getTypeInformationInputFormat();
    
    InputFormat getKnowledgeBaseInputFormat();
    String getKnowledgeBaseTextAreaText();
    
    Component getFrmOoJdrew();
    void setFrameVisible(boolean visible);    
}
