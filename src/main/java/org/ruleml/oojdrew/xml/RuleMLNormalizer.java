package org.ruleml.oojdrew.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;

import org.ruleml.oojdrew.parsing.RuleMLFormat;

public class RuleMLNormalizer {

    /**
     * Supported XSL versions (1.x and 2.0)
     */
    public enum XSLVersion {
        XSL1X, XSL20
    }

    // System property string which specifies the XML transformer factory to use
    private final String TransformerFactoryProperty = "javax.xml.transform.TransformerFactory";
    
    // SAXON XML transformer (http://saxon.sourceforge.net/)
    private final String SaxonTransformer = "net.sf.saxon.TransformerFactoryImpl";
    // XALAN (default) XML transformer
    private final String XalanTransformer = "org.apache.xalan.processor.TransformerFactoryImpl";

    // XML indent used for document formatting 
    private final int XmlIndent = 2;

    /**
     * Initializes a RuleML normalizer which uses XSL either in version 1.x or
     * in version 2.0
     * 
     * @param xslVersion
     *            The XSL version to use (XSL 1.x or XSL 2.0)
     */
    public RuleMLNormalizer(XSLVersion xslVersion) {
        if (xslVersion == XSLVersion.XSL20) {
            System.setProperty(TransformerFactoryProperty, SaxonTransformer);
        } else {
            // Use XALAN (default) for XML transforming
            System.setProperty(TransformerFactoryProperty, XalanTransformer);
        }
    }
    
    /**
     * Initializes a RuleML normalizer which uses XSL in version 1.x
     */
    public RuleMLNormalizer() {
        this(XSLVersion.XSL1X);
    }

    /**
     * Normalizes a given RuleML string with the given format.
     * 
     * @param input
     *            The input which should be normalized
     * 
     * @param rmlFormat
     *            The RuleML format (e.g. RuleML 1.0)
     * 
     * @return The normalized RuleML string
     * 
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     * @throws IOException 
     * @throws ParsingException 
     * @throws UnsupportedEncodingException 
     */
    public String normalize(String input, RuleMLFormat rmlFormat)
            throws TransformerFactoryConfigurationError, TransformerException, ParsingException, UnsupportedEncodingException {
        String normalizerXSLT = getXSLTNormalizer(rmlFormat);
        InputStream xsltStream = RuleMLNormalizer.class.getResourceAsStream(normalizerXSLT);
        StreamSource xsltStreamSource = new StreamSource(xsltStream);

        ByteArrayInputStream xmlStream = new ByteArrayInputStream(input.getBytes());
        StreamSource xmlSource = new StreamSource(xmlStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamResult outputTarget = new StreamResult(outputStream);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();        
        Transformer xmlTransformer = transformerFactory.newTransformer(xsltStreamSource);

        xmlTransformer.transform(xmlSource, outputTarget);
        String result = outputTarget.getOutputStream().toString();
        
        // Format normalized document
        try {
            result = formatDocument(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
    private String formatDocument(String xmlDocument) throws ParsingException, IOException {
        // Create formatted document
        Builder builder = new Builder();
        StringReader stringReader = new StringReader(xmlDocument);
        Document formattedDocument = builder.build(stringReader);
        
        // Write formatted document to output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(outputStream);
        serializer.setIndent(XmlIndent);      
        serializer.write(formattedDocument);
        
        return outputStream.toString();
    }
    
    /**
     * Get the XSLT for the given RuleML format. By now, RuleML 1.0 and
     * RuleML 0.91 are supported.
     * 
     * @param rmlFormat
     *            The RuleML format (e.g. RuleML 1.0)
     * 
     * @return The XSLT corresponding to the RuleML format
     * 
     * @throws UnsupportedEncodingException
     *             If the RuleML format is not supported
     */
    private String getXSLTNormalizer(RuleMLFormat rmlFormat) throws UnsupportedEncodingException {
        switch (rmlFormat) {
        case RuleML100:
            return "100_normalizer.quarantine.xslt";
        case RuleML91:
            return "091_normalizer.xslt";
        default:
            String msg = String.format("RuleML format is not supported (%s).", rmlFormat.getVersionName());
            throw new UnsupportedEncodingException(msg);
        }
    }
}
