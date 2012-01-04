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

package org.ruleml.oojdrew.util;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

public class Util {

    /**
     * Line separator (new line) specified by the operating system
     */
    public static final String NEWLINE = System.getProperty("line.separator");

    /**
     * Read content from a given web address (URI)
     * 
     * @param uri
     *            Address to read content from
     * 
     * @return Content at the given address as a string
     * 
     * @throws IOException
     *             Throws an IOException when web address is not accessible or
     *             when an error occurred during the read process
     */
    public static String readFromURIWithTimeout(String uri, int connectionTimeoutMillis)
            throws IOException {
        // Create new HTTP connection with given timeout
        URL url = new URL(uri);
        URLConnection urlConnection = url.openConnection();

        // Set connection timeout [ms] for HTTP connection
        urlConnection.setConnectTimeout(connectionTimeoutMillis);

        // Open input stream for reading
        InputStream inputStream = urlConnection.getInputStream();

        String result = readStream(new InputStreamReader(inputStream));
        return result;
    }

    /**
     * Show dialog for selecting a file and read file content
     * 
     * @param parent
     *            Reference to parent component (can be null)
     * 
     * @return File content as a string or null if action was cancelled
     * 
     * @throws IOException
     *             Throws an IOException when file is not accessible or when an
     *             error occurred during the read process
     */
    public static String selectAndReadFile(Component parent) throws IOException {
        // Create a file open dialog
        JFileChooser fileOpenDialog = new JFileChooser();
        int dialogResult = fileOpenDialog.showOpenDialog(parent);

        // Show file open dialog and read content from file
        String result = null;
        if (dialogResult == JFileChooser.APPROVE_OPTION) {
            File file = fileOpenDialog.getSelectedFile();

            // Open file for reading and read content
            result = readFile(file);
        }
        return result;
    }

    /**
     * Show dialog for selecting a file and write file content to file
     * 
     * @param content
     *            Content which should be written to a file
     * 
     * @param parent
     *            Reference to parent component (can be null)
     * 
     * @throws IOException
     *             Throws an IOException when file is not accessible or when an
     *             error occurred during the write process
     */
    public static void selectAndSaveToFile(String content, Component parent) throws IOException {
        // Create file save dialog
        @SuppressWarnings("serial")
        JFileChooser fileSaveDialog = new JFileChooser() {
            @Override
            public void approveSelection() {
                File f = getSelectedFile();
                if (f.exists() && getDialogType() == SAVE_DIALOG) {
                    int result = JOptionPane.showConfirmDialog(this,
                            "File already exists, overwrite?", "Existing file",
                            JOptionPane.YES_NO_OPTION);
                    switch (result) {
                    case JOptionPane.YES_OPTION:
                        super.approveSelection();
                        return;
                    case JOptionPane.NO_OPTION:
                        return;
                    }
                }
                super.approveSelection();
            }
        };
        fileSaveDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Show file save dialog and write content to file
        int dialogResult = fileSaveDialog.showSaveDialog(parent);
        if (dialogResult == JFileChooser.APPROVE_OPTION) {
            File file = fileSaveDialog.getSelectedFile();

            // Open file for writing and write content
            writeToFile(new FileWriter(file), content);
        }
    }

    /**
     * Read a given file and return its content as a string
     * 
     * @param file
     *            The file to read content from
     * 
     * @see Util#readStream(Reader)
     */
    public static String readFile(File file) throws FileNotFoundException, IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        
        // Detect character set for Unicode handling
        CharsetDetector charsetDetector = new CharsetDetector();
        charsetDetector.setText(bufferedInputStream);
        CharsetMatch charsetMatch = charsetDetector.detect();
        
        if (charsetMatch == null) {
            throw new IOException("Character set is not supported.");
        }
        
        Reader reader = charsetMatch.getReader();
        return readStream(reader);
    }

    /**
     * Directly read content from a given reader object
     * 
     * @param reader
     *            A reader object
     * 
     * @return Content which has been read from the reader
     * 
     * @throws IOException
     *             Throws an IOException when an error occurred during the read
     *             process
     */
    private static String readStream(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);

        // Read content to buffer
        StringBuilder buffer = new StringBuilder();
        String currentLine;
        while ((currentLine = bufferedReader.readLine()) != null) {
            buffer.append(currentLine);
            buffer.append(NEWLINE);
        }

        // Close stream and return file content
        bufferedReader.close();
        return buffer.toString();
    }

    /**
     * Directly writes content to a given writer object
     * 
     * @param writer
     *            A writer object
     * 
     * @param content
     *            Content which should be written
     * 
     * @throws IOException
     *             Throws an IOException when an error occurred during the write
     *             process
     */
    private static void writeToFile(Writer writer, String content) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        // Write content and close steam
        bufferedWriter.write(content);
        bufferedWriter.close();
    }
}
