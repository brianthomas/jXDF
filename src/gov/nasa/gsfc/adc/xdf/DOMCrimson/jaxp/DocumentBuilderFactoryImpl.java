
/*
 */

package gov.nasa.gsfc.adc.xdf.DOMCrimson.jaxp;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

/**
   Code taken whole-heartedly from Crimson 1.1ea2 JAXP package.
 * @version $Revision$
 */
public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {

    // By default expand entity references.  This overrides the
    // DocumentBuilderFactory default.  DOM2 allows either behavior so it
    // is not clear what the default should be.  I am choosing the value
    // "true" because that will probably be most useful.
    // XXX The default needs to be clarified in the JAXP 1.1 spec.
    private boolean expandEntityRefs = true;

    public DocumentBuilderFactoryImpl() {
   	 
    }

    /**
     * 
     */
    public DocumentBuilder newDocumentBuilder()
        throws ParserConfigurationException 
    {
	DocumentBuilderImpl db = new DocumentBuilderImpl(this);
        return db;
    }

    /**
     * Specifies that the parser produced by this code will
     * expand entity reference nodes.
     */
    public void setExpandEntityReferences(boolean expandEntityRefs) {
        this.expandEntityRefs = expandEntityRefs;
    }

    /**
     * Indicates whether or not the factory is configured to produce
     * parsers which expand entity reference nodes.
     */
    public boolean isExpandEntityReferences() {
        return expandEntityRefs;
    }

    /**
     * Allows the user to set specific attributes on the underlying 
     * implementation.
     */
    public void setAttribute(String name, Object value)
        throws IllegalArgumentException
    {
        throw new IllegalArgumentException("No attributes are implemented");
    }

    /**
     * Allows the user to retrieve specific attributes on the underlying 
     * implementation.
     */
    public Object getAttribute(String name)
        throws IllegalArgumentException
    {
        throw new IllegalArgumentException("No attributes are implemented");
    }
}
