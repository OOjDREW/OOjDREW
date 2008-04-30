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
package jdrew.oo.gui;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;

import org.apache.log4j.*;
import org.apache.log4j.helpers.*;
import org.apache.log4j.spi.*;

/**
 * Adatpted from James House's TextPanelAppender, part of the log4j package.
 *
 * This is used to append loggin output to the text pane that is part of the
 * debug console.
 */

public class TextPaneAppender extends AppenderSkeleton {


    JTextPane textpane;
    StyledDocument doc;
    StringWriter sw;
    QuietWriter qw;
    Hashtable attributes;
    Hashtable icons;
    PrintWriter pw;

    private String label;

    private boolean fancy;

    final String LABEL_OPTION = "Label";
    final String COLOR_OPTION_FATAL = "Color.Emerg";
    final String COLOR_OPTION_ERROR = "Color.Error";
    final String COLOR_OPTION_WARN = "Color.Warn";
    final String COLOR_OPTION_INFO = "Color.Info";
    final String COLOR_OPTION_DEBUG = "Color.Debug";
    final String COLOR_OPTION_BACKGROUND = "Color.Background";
    final String FANCY_OPTION = "Fancy";
    final String FONT_NAME_OPTION = "Font.Name";
    final String FONT_SIZE_OPTION = "Font.Size";

    public static Image loadIcon(String path) {
        Image img = null;
        try {
            URL url = ClassLoader.getSystemResource(path);
            img = (Image) (Toolkit.getDefaultToolkit()).getImage(url);
        } catch (Exception e) {
            System.out.println("Exception occured: " + e.getMessage() +
                               " - " + e);
        }
        return (img);
    }

    public TextPaneAppender(Layout layout, String name) {
        this();
        this.layout = layout;
        this.name = name;
        setTextPane(new JTextPane());
        createAttributes();
        createIcons();
    }

    public TextPaneAppender() {
        super();
        setTextPane(new JTextPane());
        createAttributes();
        createIcons();
        this.label = "";
        this.sw = new StringWriter();
        this.qw = new QuietWriter(sw, errorHandler);
        this.pw = new PrintWriter(qw);
        this.fancy = true;
    }

    public
            void close() {

    }

    private void createAttributes() {
        Priority prio[] = Priority.getAllPossiblePriorities();

        attributes = new Hashtable();
        for (int i = 0; i < prio.length; i++) {
            MutableAttributeSet att = new SimpleAttributeSet();
            attributes.put(prio[i], att);
            StyleConstants.setFontSize(att, 14);
        }
        //StyleConstants.setForeground((MutableAttributeSet)attributes.get(Level.ERROR),Color.red);
        //StyleConstants.setForeground((MutableAttributeSet)attributes.get(Level.WARN),Color.orange);
        //StyleConstants.setForeground((MutableAttributeSet)attributes.get(Level.INFO),Color.gray);
        //StyleConstants.setForeground((MutableAttributeSet)attributes.get(Level.DEBUG),Color.black);
    }

    private void createIcons() {

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
            if (fancy) {
                textpane.setEditable(true);
                textpane.setEditable(false);
            }
            doc.insertString(doc.getLength(), text + trace,
                             (MutableAttributeSet) attributes.get(event.
                    getLevel()));
        } catch (BadLocationException badex) {
            System.err.println(badex);
        }
        textpane.setCaretPosition(doc.getLength());
    }

    public
            JTextPane getTextPane() {
        return textpane;
    }

    private
            static
            Color parseColor(String v) {
        StringTokenizer st = new StringTokenizer(v, ",");
        int val[] = {255, 255, 255, 255};
        int i = 0;
        while (st.hasMoreTokens()) {
            val[i] = Integer.parseInt(st.nextToken());
            i++;
        }
        return new Color(val[0], val[1], val[2], val[3]);
    }

    private
            static
            String colorToString(Color c) {
        // alpha component emitted only if not default (255)
        String res = "" + c.getRed() + "," + c.getGreen() + "," + c.getBlue();
        return c.getAlpha() >= 255 ? res : res + "," + c.getAlpha();
    }

    public
            void setLayout(Layout layout) {
        this.layout = layout;
    }

    public
            void setName(String name) {
        this.name = name;
    }


    public
            void setTextPane(JTextPane textpane) {
        this.textpane = textpane;
        textpane.setEditable(false);
        textpane.setBackground(Color.lightGray);
        this.doc = textpane.getStyledDocument();
    }

    private
            void setColor(Priority p, String v) {
        StyleConstants.setForeground(
                (MutableAttributeSet) attributes.get(p), parseColor(v));
    }

    private
            String getColor(Priority p) {
        Color c = StyleConstants.getForeground(
                (MutableAttributeSet) attributes.get(p));
        return c == null ? null : colorToString(c);
    }

    /////////////////////////////////////////////////////////////////////
    // option setters and getters

    public
            void setLabel(String label) {
        this.label = label;
    }

    public
            String getLabel() {
        return label;
    }

    public
            void setColorEmerg(String color) {
        setColor(Level.FATAL, color);
    }

    public
            String getColorEmerg() {
        return getColor(Level.FATAL);
    }

    public
            void setColorError(String color) {
        setColor(Level.ERROR, color);
    }

    public
            String getColorError() {
        return getColor(Level.ERROR);
    }

    public
            void setColorWarn(String color) {
        setColor(Level.WARN, color);
    }

    public
            String getColorWarn() {
        return getColor(Level.WARN);
    }

    public
            void setColorInfo(String color) {
        setColor(Level.INFO, color);
    }

    public
            String getColorInfo() {
        return getColor(Level.INFO);
    }

    public
            void setColorDebug(String color) {
        setColor(Level.DEBUG, color);
    }

    public
            String getColorDebug() {
        return getColor(Level.DEBUG);
    }

    public
            void setColorBackground(String color) {
        textpane.setBackground(parseColor(color));
    }

    public
            String getColorBackground() {
        return colorToString(textpane.getBackground());
    }

    public
            void setFancy(boolean fancy) {
        this.fancy = fancy;
    }

    public
            boolean getFancy() {
        return fancy;
    }

    public
            void setFontSize(int size) {
        Enumeration e = attributes.elements();
        while (e.hasMoreElements()) {
            StyleConstants.setFontSize((MutableAttributeSet) e.nextElement(),
                                       size);
        }
        return;
    }

    public
            int getFontSize() {
        AttributeSet attrSet = (AttributeSet) attributes.get(Level.INFO);
        return StyleConstants.getFontSize(attrSet);
    }

    public
            void setFontName(String name) {
        Enumeration e = attributes.elements();
        while (e.hasMoreElements()) {
            StyleConstants.setFontFamily((MutableAttributeSet) e.nextElement(),
                                         name);
        }
        return;
    }

    public
            String getFontName() {
        AttributeSet attrSet = (AttributeSet) attributes.get(Level.INFO);
        return StyleConstants.getFontFamily(attrSet);
    }

    public
            boolean requiresLayout() {
        return true;
    }
} // TextPaneAppender

