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

package org.ruleml.oojdrew.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import org.ruleml.oojdrew.util.Util;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.Serializer;

public class XmlUtils {
    
    private static final String NEWLINE = Util.NEWLINE;
    
    // XML indent used for document formatting 
    public static final int XML_INDENT = 3;
    
    /**
     * Convert a XML element into its string representation
     * 
     * @param element
     *            Element to convert
     * 
     * @return The string representation of the element
     */
    public static String elementToString(Element element) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        nu.xom.Serializer serializer = new nu.xom.Serializer(outputStream);
        serializer.setIndent(XML_INDENT);
        serializer.setLineSeparator(NEWLINE);
        try {
            Document doc = new Document(element);
            serializer.write(doc);
            outputStream.close();
        } catch (java.io.IOException e) {
            System.err.println(e.getMessage());
        }
        return outputStream.toString();
    }
    
    /**
     * Format (pretty print) a given XML document
     * 
     * @param xmlDocument
     *            The document to format
     * 
     * @return The formatted document
     * 
     * @throws ParsingException
     * @throws IOException
     */
    public static String formatDocument(String xmlDocument) throws ParsingException, IOException {
        // Create formatted document
        Builder builder = new Builder();
        StringReader stringReader = new StringReader(xmlDocument);
        Document formattedDocument = builder.build(stringReader);

        // Write formatted document to output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(outputStream);
        serializer.setIndent(XML_INDENT);
        serializer.setLineSeparator(NEWLINE);
        serializer.write(formattedDocument);

        return outputStream.toString();
    }
}
