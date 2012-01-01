package org.ruleml.oojdrew.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

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

    /**
     * Initializes a RuleML normalizer which uses either XSL version 1.x or version 2.x
     * 
     * @param xslVersion
     *      The XSL version to use (XSL 1.x or XSL 2.0)
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
     * Initializes a RuleML normalizer which uses XSL version 1.x
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
     * @throws UnsupportedEncodingException
     */
    public String normalize(String input, RuleMLFormat rmlFormat)
            throws TransformerFactoryConfigurationError, TransformerException, UnsupportedEncodingException {

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

        // Remove blank lines and return normalized RuleML
        result = result.replaceAll("(?m)^\\s+$[\r|\n]+", "");
        return result;
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
