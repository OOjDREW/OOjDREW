// OO jDREW Version 0.93
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package jdrew.oo.util;

import java.io.*;
import java.util.*;

import nu.xom.*;
import org.apache.log4j.*;

/**
 * This class implements a parser for type sorts definitions in RDFS syntax;
 * This allows users to define new types than can be used by the reasoning
 * engine.
 *
 * <p>Title: OO jDREW</p>
 *
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.88</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * @author Marcel A. Ball
 * @version 0.89
 */
public class RDFSParser {
    private static String rdfsuri;
    private static String rdfuri;

    private static final String RDF = "rdf";
    private static final String RDFS = "rdfs";
    private static final String RDFSCLASS = "rdfs:Class";
    private static final String RDFDESCRIPTION = "rdf:Description";
    private static final String RDFID = "rdf:ID";
    private static final String RDFABOUT = "rdf:about";
    private static final String RDFRESOURCE = "rdf:resource";
    private static final String RDFSSUBCLASSOF = "rdfs:subClassOf";
    private static final String RDFTYPE = "rdf:type";

    private static final String CLASS = "Class";
    private static final String DESCRIPTION = "Description";
    private static final String ID = "ID";
    private static final String ABOUT = "about";
    private static final String RESOURCE = "resource";
    private static final String SUBCLASSOF = "subClassOf";
    private static final String TYPE = "type";

    private static final String CLASSRES =
            "http://www.w3.org/2000/01/rdf-schema#Class";

   // private static Logger logger = Logger.getLogger("jdrew.oo.util.RDFSParser");

    /**
     * Method to parse an RDFS file and load the type information in the RDFS
     * into the engines type sorts.
     *
     * This method parses the file into a XOM tree and passes the XOM
     * Document object to the parseDocument(Document doc) method.
     *
     * @param filename String A string containing the complete path to the RDFS
     * file.
     *
     * @throws ParsingException Thrown if there is an error in the RDFS file
     * that causes parsing to fail.
     *
     * @throws ValidityException Thrown if the XML document is not wellformed
     * or does not conform to the RDFS DTD.
     *
     * @throws IOException Thrown if there is an error reading the file from
     * disk.
     */
    public static void parseRDFS(String filename) throws ParsingException,
            ValidityException, IOException {
        Builder b = new Builder();
        File f = new File(filename);
        Document doc = b.build(f);
        parseDocument(doc);
    }


    /**
     * Method that parses an RDFS document contained in a string and load the
     * type information in the RDFS into the engines type sorts.
     *
     * This method parses the string to an XOM document and passes the Document
     * object to the parseDocument(Document doc) method.
     *
     * @param contents String
     *
     * @throws ParseException Thrown if there is an error in the RDFS document
     * that causes parsing to fail.
     *
     * @throws ParsingException Thrown if there is an error parsing at an XML
     * level.
     *
     * @throws ValidityException Thrown if the XML document is not wellformed
     * or does not conform to the RDFS DTD.
     *
     * @throws IOException Thrown if there is an error reading the string -
     * this should not occur.
     */
    public static void parseRDFSString(String contents) throws ParseException,
            ParsingException, ValidityException, IOException {
        Builder bl = new Builder();

        StringReader sr = new StringReader(contents);
        Document doc = bl.build(sr);
        
        //System.out.println(doc.toXML());
        parseDocument(doc);
    }

    /**
     * Parses the XOM document object that should represent an RDFS document;
     * containing types that will be added to the type sorts.
     *
     * @param doc Document The XOM document object to parse.
     */
    private static void parseDocument(Document doc) {
        Element root = doc.getRootElement();
        rdfsuri = root.getNamespaceURI("rdfs");
        rdfuri = root.getNamespaceURI("rdf");

        //logger.debug("RDFS Uri: " + rdfsuri);
        //logger.debug("RDF Uri: " + rdfuri);

        Elements els = root.getChildElements();
        for (int i = 0; i < els.size(); i++) {
            Element el = els.get(i);
            //logger.debug("Parsing element: " + el.getQualifiedName());
            if (el.getQualifiedName().equals(RDFSCLASS)) {
                parseClass(el);
            } else if (el.getQualifiedName().equals(RDFDESCRIPTION)) {
                parseDescription(el);
            } else {
                //logger.debug(
                //        "Unrecognized element in RDFS file. Element being ignored.");
            }
            
        }
// should put new type parser here
        Types.makeTypes();
        
    }

    /**
     * This method parses a rdfs:Class element and loads the information
     * contained into the Type system.
     *
     * @param cls Element The XOM element that contains the rdfs:Class object.
     */
    private static void parseClass(Element cls) {
        String typename = cls.getAttributeValue(ID, rdfuri);
        if (typename == null) {
          //  logger.debug(
           //         "rdfs:Class element does not have rdf:ID attribute - trying rdf:about");
            typename = cls.getAttributeValue(ABOUT, rdfuri);
            if (typename == null) {
                //logger.warn("rdfs:Class element does not have and rdf:about or rdf:ID attribute - no typename - type creation failure.");
                return;
            }
        }

        if (typename.indexOf("#") > 0) {
            typename = typename.substring(typename.indexOf("#") + 1);
        }

        Vector v = new Vector();
        Elements els = cls.getChildElements();
        for (int i = 0; i < els.size(); i++) {
            Element el = els.get(i);
            if (el.getQualifiedName().equals(RDFSSUBCLASSOF)) {
                String parentname = el.getAttributeValue(RESOURCE, rdfuri);
                if (parentname.indexOf("#") >= 0) {
                    parentname = parentname.substring(parentname.indexOf("#") +
                            1);
                }
                v.add(parentname);
            }
        }

        String[] parents = new String[v.size()];
        String parentstr = "";
        for (int i = 0; i < parents.length; i++) {
            parents[i] = v.get(i).toString();
            parentstr += v.get(i).toString() + " ";
        }

        //logger.info("Creating Type: " + typename + " parents: " + parentstr);
       // System.out.println("Dies before createType");
        
        Types.storeTempTypes(typename, parents);
       // Types.createDAGNode(typename);
       // Types.createDAGEdges(typename, parents);



}

    /**
     * Parses a rdf:description element and loads the type information into the
     * type sorts.
     *
     * @param desc Element The XOM element object representing the
     * rdf:description element.
     */
    private static void parseDescription(Element desc) {
        String typename = desc.getAttributeValue(ID, rdfuri);
        if (typename == null) {
           // logger.debug(
           //         "rdf:Description element does not have rdf:ID attribute - trying rdf:about");
            typename = desc.getAttributeValue(ABOUT, rdfuri);
            if (typename == null) {
               // logger.warn("rdf:Description element does not have an rdf:about or rdf:ID attribute - no typename - type creation failure.");
                return;
            }
        }

        if (typename.indexOf("#") > 0) {
            typename = typename.substring(typename.indexOf("#") + 1);
        }

        Element type = desc.getFirstChildElement(TYPE, rdfuri);
        if (type == null ||
            !type.getAttributeValue(RESOURCE, rdfuri).equals(CLASSRES)) {
           // logger.warn(
           //         "rdf:Description element has not rdf:type child element, or is not of type " +
            //        CLASSRES + " - ignoring rdf:Description element.");
            return;
        }

        if (typename.indexOf("#") >= 0) {
            typename = typename.substring(typename.indexOf("#") + 1);
        }

        Vector v = new Vector();
        Elements els = desc.getChildElements();
        for (int i = 0; i < els.size(); i++) {
            Element el = els.get(i);
            if (el.getQualifiedName().equals(RDFSSUBCLASSOF)) {
                String parentname = el.getAttributeValue(RESOURCE, rdfuri);
                if (parentname.indexOf("#") >= 0) {
                    parentname = parentname.substring(parentname.indexOf("#") +
                            1);
                }
                v.add(parentname);
            }
        }

        String[] parents = new String[v.size()];
        String parentstr = "";
        for (int i = 0; i < parents.length; i++) {
            parents[i] = v.get(i).toString();
            parentstr += v.get(i).toString() + " ";
        }

        //logger.info("Creating Type: " + typename + " parents: " + parentstr);

        //Types.createType(typename, parents);
        
        Types.storeTempTypes(typename, parents);
        
       // Types.createDAGNode(typename);
       // Types.createDAGEdges(typename, parents);
    }


}
