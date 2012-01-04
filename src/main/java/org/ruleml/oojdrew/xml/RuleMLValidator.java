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

import java.io.StringReader;

import nu.xom.Builder;
import nu.xom.ValidityException;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class RuleMLValidator {
    
    private final String SAX_PARSER = "org.apache.xerces.parsers.SAXParser";
    
    public void validateRuleMLDocument(String rmlContent) throws Exception {
        XMLReader xmlReader;
        try {
            xmlReader = XMLReaderFactory.createXMLReader(SAX_PARSER);
            xmlReader.setFeature("http://apache.org/xml/features/validation/schema", true);
        } catch (SAXException e) {
            throw new Exception("Unable to create XML validator");
        }

        Builder builder = new Builder(xmlReader, true);
        StringReader stringReader = new StringReader(rmlContent);
        try {
            builder.build(stringReader);
        } catch (ValidityException e) {
            throw new ValidityException("Document does not validate against the specified XSD.");
        } catch (Exception e) {
            throw new Exception("Error occurred during document validation.");
        }
    }
}
