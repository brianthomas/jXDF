
/*
 */


package gov.nasa.gsfc.adc.xdf.DOMXerces1.jaxp;

import gov.nasa.gsfc.adc.xdf.DOMXerces1.Parser;

import org.apache.xerces.dom.DOMImplementationImpl;

import java.util.Hashtable;
import java.util.Enumeration;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;

// import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
// import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.DefaultHandler;


/**
   An implementation of the DocumentBuilder for Xerces v1.x of the 
   XDFDOM. Code was taken from Xerces project (version 1.x) 
   DocumentBuilderImpl class.
 * @version $Revision$
 */

public class DocumentBuilderImpl extends DocumentBuilder {

    /** Xerces features */
    static final String XERCES_FEATURE_PREFIX =
                                        "http://apache.org/xml/features/";
    static final String CREATE_ENTITY_REF_NODES_FEATURE =
                                        "dom/create-entity-ref-nodes";
    static final String INCLUDE_IGNORABLE_WHITESPACE =
                                        "dom/include-ignorable-whitespace";

    private EntityResolver er = null;
    private ErrorHandler eh = null;
    private Parser domParser = null;

    private boolean namespaceAware = false;
    private boolean validating = false;

    DocumentBuilderImpl(DocumentBuilderFactory dbf, Hashtable dbfAttrs)
        throws SAXNotRecognizedException, SAXNotSupportedException
    {

        // sadly, this is the only change we need to make, too bad no
        // 'setDOMParser' method exists.
        domParser = new Parser(); // create XDF parser

        // Validation
        validating = dbf.isValidating();
        String validation = "http://xml.org/sax/features/validation";
        domParser.setFeature(validation, validating);

        // If validating, provide a default ErrorHandler that prints
        // validation errors with a warning telling the user to set an
        // ErrorHandler
        if (validating) {
            setErrorHandler(new DefaultValidationErrorHandler());
        }

        // "namespaceAware" ==  SAX Namespaces feature
        namespaceAware = dbf.isNamespaceAware();
        domParser.setFeature("http://xml.org/sax/features/namespaces",
                             namespaceAware);

        // Set various parameters obtained from DocumentBuilderFactory
        domParser.setFeature(XERCES_FEATURE_PREFIX +
                             INCLUDE_IGNORABLE_WHITESPACE,
                             !dbf.isIgnoringElementContentWhitespace());
        domParser.setFeature(XERCES_FEATURE_PREFIX +
                             CREATE_ENTITY_REF_NODES_FEATURE,
                             !dbf.isExpandEntityReferences());

        // XXX No way to control dbf.isIgnoringComments() or
        // dbf.isCoalescing()

        setDocumentBuilderFactoryAttributes(dbfAttrs);
    }

    /**
     * Set any DocumentBuilderFactory attributes of our underlying DOM Parser
     *
     * Note: code does not handle possible conflicts between DOM Parser
     * attribute names and JAXP specific attribute names,
     * eg. DocumentBuilderFactory.setValidating()
     */
    private void setDocumentBuilderFactoryAttributes(Hashtable dbfAttrs)
        throws SAXNotSupportedException, SAXNotRecognizedException
    {
        if (dbfAttrs != null) {
            for (Enumeration e = dbfAttrs.keys(); e.hasMoreElements();) {
                String name = (String)e.nextElement();
                Object val = dbfAttrs.get(name);
                if (val instanceof Boolean) {
                    // Assume feature
                    domParser.setFeature(name, ((Boolean)val).booleanValue());
                } else {
                    // Assume property
                    domParser.setProperty(name, val);
                }
            }
        }
    }

    /**
     * Non-preferred: use the getDOMImplementation() method instead of this
     * one to get a DOM Level 2 DOMImplementation object and then use DOM
     * Level 2 methods to create a DOM Document object.
     */
    public Document newDocument() {
        return new org.apache.xerces.dom.DocumentImpl();
    }

// this is not quite right... we want to have XDFDOMImplementation returned.
    public DOMImplementation getDOMImplementation() {
        return DOMImplementationImpl.getDOMImplementation();
    }

    public Document parse(InputSource is) throws SAXException, IOException {
        if (is == null) {
            throw new IllegalArgumentException("InputSource cannot be null");
        }

        if (er != null) {
            domParser.setEntityResolver(er);
        }

        if (eh != null) {
            domParser.setErrorHandler(eh);      
        }

        domParser.parse(is);
        return domParser.getDocument();
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
        // If app passes in a ErrorHandler of null, then ignore all errors
        // and warnings
        this.eh = (eh == null) ? new DefaultHandler() : eh;
    }

    // 
    // private methods 
    //

    Parser getDOMParser() {
        return domParser;
    }
}
