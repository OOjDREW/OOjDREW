package org.ruleml.oojdrew.xml;

import java.io.StringReader;

import nu.xom.Builder;
import nu.xom.ValidityException;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class RuleMLValidator {
    
    private static final String SAX_PARSER = "org.apache.xerces.parsers.SAXParser";
    
    public static void validateRuleMLDocument(String rmlContent) throws Exception {
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
