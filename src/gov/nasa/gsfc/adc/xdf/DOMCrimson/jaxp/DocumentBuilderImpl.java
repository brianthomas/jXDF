
/*
 */

package gov.nasa.gsfc.adc.xdf.DOMCrimson.jaxp;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

import gov.nasa.gsfc.adc.xdf.DOMCrimson.XDFDocumentImpl;
import gov.nasa.gsfc.adc.xdf.DOMCrimson.XDFDocumentBuilder;

// crimson classes
import org.apache.crimson.parser.XMLReaderImpl;

import org.apache.crimson.tree.DOMImplementationImpl;
import org.apache.crimson.tree.XmlDocument;
// import org.apache.crimson.tree.XmlDocumentBuilder;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @version $Revision$
 */

public class DocumentBuilderImpl extends DocumentBuilder {

    private DocumentBuilderFactory dbf;

    private EntityResolver er = null;
    private ErrorHandler eh = null;
    private XMLReader xmlReader = null;
    private XDFDocumentBuilder builder = null;

    private boolean namespaceAware = false;
    private boolean validating = false;

    DocumentBuilderImpl (DocumentBuilderFactory dbf)
        throws ParserConfigurationException
    {
        this.dbf = dbf;

        xmlReader = new XMLReaderImpl();

        try {
            // Validation
            validating = dbf.isValidating();
            String validation = "http://xml.org/sax/features/validation";
            xmlReader.setFeature(validation, validating);

            // If validating, provide a default ErrorHandler that prints
            // validation errors with a warning telling the user to set an
            // ErrorHandler
            if (validating) {
                xmlReader.setErrorHandler(new DefaultValidationErrorHandler());
            }

            // Namespace related features needed for XDFDocumentBuilder
            String namespaces = "http://xml.org/sax/features/namespaces";
            xmlReader.setFeature(namespaces, true);
            String nsPrefixes =
                    "http://xml.org/sax/features/namespace-prefixes";
            xmlReader.setFeature(nsPrefixes, true);

            // Create XDFDocumentBuilder instance
            builder = new XDFDocumentBuilder();

            // Use builder as the ContentHandler
            xmlReader.setContentHandler(builder);
          
            // org.xml.sax.ext.LexicalHandler
            String lexHandler = "http://xml.org/sax/properties/lexical-handler";
            xmlReader.setProperty(lexHandler, builder);

            // org.xml.sax.ext.DeclHandler
            String declHandler
                = "http://xml.org/sax/properties/declaration-handler";
            xmlReader.setProperty(declHandler, builder);

            // DTDHandler
            xmlReader.setDTDHandler(builder);
        } catch (SAXException e) {
            // Handles both SAXNotSupportedException, SAXNotRecognizedException
            throw new ParserConfigurationException(e.getMessage());
        }

        // Set various builder properties obtained from DocumentBuilderFactory
        namespaceAware = dbf.isNamespaceAware();
        builder.setDisableNamespaces(!namespaceAware);  
        builder.setIgnoreWhitespace(dbf.isIgnoringElementContentWhitespace());
        builder.setExpandEntityReferences(dbf.isExpandEntityReferences());
        builder.setIgnoreComments(dbf.isIgnoringComments());
        builder.setPutCDATAIntoText(dbf.isCoalescing());
    }

    public Document newDocument() {
        return new XDFDocumentImpl();
    }

    public DOMImplementation getDOMImplementation() {
        return new DOMImplementationImpl(); 
    }

    public Document parse(InputSource is) throws SAXException, IOException {
        if (is == null) {
            throw new IllegalArgumentException("InputSource cannot be null");
        }

        if (er != null) {
            xmlReader.setEntityResolver(er);
        }

        if (eh != null) {
            xmlReader.setErrorHandler(eh);      
        }

        xmlReader.parse(is);
        return builder.getDocument();
    }

    public boolean isNamespaceAware() {
        return namespaceAware;
    }

    public boolean isValidating() {
        return validating;
    }

    public void setEntityResolver(org.xml.sax.EntityResolver er) {
        this.er = er;
    }

    public void setErrorHandler(org.xml.sax.ErrorHandler eh) {
        this.eh = eh; 
    }
}
