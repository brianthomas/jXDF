
/** The XDF Sax Doc Handler class.
    @author b.t. (thomas@adc.gsfc.nasa.gov)
    @version $Revision$
*/
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import org.xml.sax.*;

public class SaxDocHandler implements DocumentHandler
{

    // class fields 
    gov.nasa.gsfc.adc.xdf.Structure myStructure = new gov.nasa.gsfc.adc.xdf.Structure();
    MsgHandler       msg = new MsgHandler();
    String currentNode;

    // the version of XDF this version of this class will read 
    String xdfVersion = "0.17"; 


    // The following methods are the SAX DocumentHandler methods
    // ---------------------------------------------------------

    public void setDocumentLocator (Locator l)
    {
        // we'd record this if we needed to resolve relative URIs
        // in content or attributes, or wanted to give diagnostics.
        // Right now, do nothing here.
    }

    public void startDocument()
    throws SAXException
    {
        msg.debug ("<?xml version='1.0' encoding='UTF-8'?>\n");
    }

    public void endDocument()
    throws SAXException
    {
        msg.debug("\n");
    }

    public void ignorableWhitespace(char buf [], int offset, int len)
    throws SAXException
    {
        // this callback won't be used consistently by all parsers,
        // unless they read the whole DTD.  Validating parsers will
        // use it, and currently most SAX nonvalidating ones will
        // also; but nonvalidating parsers might hardly use it,
        // depending on the DTD structure.
        msg.debug(buf, offset, len);
    }

    public void processingInstruction(String target, String data)
    throws SAXException
    {
        msg.debug("<?"+target+" "+data+"?>");
    }

    /**  We do the actual population of values from within this handler.
         This needs to work in tandem with the startDocument,
         endDocument handlers in order to pick the right attribute to
         populate. 
     */
    public void characters (char buf [], int offset, int len)
    throws SAXException
    {
        // NOTE:  this doesn't escape '&' and '<', but it should
        // do so else the output isn't well formed XML.  to do this
        // right, scan the buffer and write '&amp;' and '&lt' as
        // appropriate.
//msg.debug("PRINT CHARACTER DATA[");
        msg.debug(buf, offset, len);
//msg.debug("]\n");
    }


    /** The startElement SAX document handler. This is where most of the action
        and complexity is. 
     */
    public void startElement (String tag, AttributeList attrs)
    throws SAXException
    {

        currentNode = tag;

        msg.debug("<"+tag);
   
        // deal with the attributes
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength (); i++) {
                msg.debug(" ");
                msg.debug(attrs.getName (i));
                msg.debug("\"");
                // XXX this doesn't quote '&', '<', and '"' in the
                // way it should ... needs to scan the value and
                // emit '&amp;', '&lt;', and '&quot;' respectively
                msg.debug(attrs.getValue (i));
                msg.debug("\"");
            }
        }
        msg.debug(">");
    }

    public void endElement (String name)
    throws SAXException
    {
        msg.debug("</"+name+">");
    }

}

