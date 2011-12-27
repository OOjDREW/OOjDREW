package org.ruleml.oojdrew;

import java.util.Iterator;

public interface Reasoner {
    public void clearClauses();
    public void loadClauses(Iterator iterator);
}
