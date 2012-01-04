/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.ruleml.oojdrew.GUI;

import java.awt.Color;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.QuietWriter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * From James House's TextPanelAppender, part of the log4j package. The
 * source have been adapted and modified. The authors do not provide ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * This is used to append logging output to the text pane that is part of the
 * debug console.
 */
public class TextPaneAppender extends AppenderSkeleton {

    private JTextPane textpane;
    private StyledDocument doc;
    private StringWriter sw;
    private QuietWriter qw;
    private Hashtable<Level, MutableAttributeSet> attributes;
    private PrintWriter pw;
    
    private int maxBufferSize = 65536;

    public TextPaneAppender(Layout layout, String name) {
        this();
        this.layout = layout;
        this.name = name;
    }

    public TextPaneAppender() {
        super();
        setTextPane(new JTextPane());
        createAttributes();

        this.sw = new StringWriter();
        this.qw = new QuietWriter(sw, errorHandler);
        this.pw = new PrintWriter(qw);
    }

    public void close() {
    }

    private void createAttributes() {
        Level[] loggingLevels = new Level[] { Level.OFF, Level.ERROR, Level.WARN, Level.DEBUG, Level.INFO };

        attributes = new Hashtable<Level, MutableAttributeSet>();
        for (Level level : loggingLevels) {
            MutableAttributeSet att = new SimpleAttributeSet();
            attributes.put(level, att);
        }
       
        setColor(Level.ERROR, Color.red);
        setColor(Level.WARN, Color.orange);
        setColor(Level.DEBUG, Color.black);
        setColor(Level.INFO, Color.gray);
    }

    public void append(LoggingEvent event) {
        String text = this.layout.format(event);
        String trace = "";
        
        // Print Stacktrace
        // Quick Hack maybe there is a better/faster way?
        if (event.getThrowableInformation() != null) {
            Throwable t = event.getThrowableInformation().getThrowable();
            t.printStackTrace(pw);
            for (int i = 0; i < sw.getBuffer().length(); i++) {
                if (sw.getBuffer().charAt(i) == '\t') {
                    sw.getBuffer().replace(i, i + 1, "        ");
                }
            }
            trace = sw.toString();
            sw.getBuffer().delete(0, sw.getBuffer().length());
        }

        try {           
            doc.insertString(doc.getLength(), text + trace, attributes.get(event.getLevel()));
            
            if (doc.getLength() > maxBufferSize) {
                doc.remove(0, doc.getLength() - maxBufferSize);
            }
            
        } catch (BadLocationException badex) {
            System.err.println(badex);
        }
               
        textpane.setCaretPosition(doc.getLength());
    }

    public JTextPane getTextPane() {
        return textpane;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTextPane(JTextPane textpane) {
        this.textpane = textpane;
        textpane.setEditable(false);
        textpane.setBackground(Color.lightGray);
        this.doc = textpane.getStyledDocument();
    }

    private void setColor(Level logLevel, Color color) {
        StyleConstants.setForeground(attributes.get(logLevel), color);
    }

    public boolean requiresLayout() {
        return true;
    }

} // TextPaneAppender
