
/* 
 */

package gov.nasa.gsfc.adc.xdf.DOMCrimson;

import gov.nasa.gsfc.adc.xdf.Constants;
import gov.nasa.gsfc.adc.xdf.Log;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import org.apache.crimson.tree.XmlDocumentBuilder;
import org.apache.crimson.tree.XmlDocument;
import org.apache.crimson.tree.ElementNode2;
import org.apache.crimson.tree.ElementFactory;

/**
 * This class is a SAX2 ContentHandler which converts a stream of parse
 * events into an in-memory DOM document.  After each <em>Parser.parse()</em>
 * invocation returns, a resulting DOM Document may be accessed via the
 * <em>getDocument</em> method.  The parser and its builder should be used
 * together; the builder may be used with only one parser at a time.
 *
 * <P> This builder optionally does XML namespace processing, reporting
 * conformance problems as recoverable errors using the parser's error
 * handler.  
 *
 * <P> Note: element factories are deprecated because they are non-standard
 * and are provided here only for backwards compatibility.  To customize
 * the document, a powerful technique involves using an element factory
 * specifying what element tags (from a given XML namespace) correspond to
 * what implementation classes.  Parse trees produced by such a builder can
 * have nodes which add behaviors to achieve application-specific
 * functionality, such as modifing the tree as it is parsed.
 *
 * <P> The object model here is that XML elements are polymorphic, with
 * semantic intelligence embedded through customized internal nodes.
 * Those nodes are created as the parse tree is built.  Such trees now
 * build on the W3C Document Object Model (DOM), and other models may be
 * supported by the customized nodes.  This allows both generic tools
 * (understanding generic interfaces such as the DOM core) and specialized
 * tools (supporting specialized behaviors, such as the HTML extensions
 * to the DOM core; or for XSL elements) to share data structures.
 *
 * <P> Normally only "model" semantics are in document data structures,
 * but "view" or "controller" semantics can be supported if desired.
 *
 * <P> Elements may choose to intercept certain parsing events directly.
 * They do this by overriding the default implementations of methods
 * in the <em>XmlReadable</em> interface.  This is normally done to make
 * the DOM tree represent application level modeling requirements, rather
 * than matching an XML structure that may not be optimized appropriately.
 *
 * @version $Revision$
 */
public class XDFDocumentBuilder extends XmlDocumentBuilder
{

    // Fields

    /**
     * This is a factory method, used to create an XmlDocument.
     * Subclasses may override this method, for example to provide
     * document classes with particular behaviors, or provide
     * particular factory behaviours (such as returning elements
     * that support the HTML DOM methods, if they have the right
     * name and are in the right namespace).
     */
    public XmlDocument createDocument ()
    {

        // sadly, this is the only thing we have to do, wish we could
        // have had a public 'setDocumentClass' or some such, oh well :P.
	XmlDocument retval = new XDFDocumentImpl ();

        ElementFactory myFactory = getElementFactory();
	if (myFactory != null) {
            retval.setElementFactory(myFactory);
        }

	return retval;
    }

    public void endDocument () 
    throws SAXException
    {
       super.endDocument();
       syncDocumentXDFElements();
    }

    // populate the document XDF objects
    private void syncDocumentXDFElements () 
    {

      Element rootElement = (Element) document.getDocumentElement();
      convertElement(rootElement, document);

    }

    private static void convertElement(Element elem, XmlDocument doc) {

      if (elem.getNodeName().equals(Constants.XDF_ROOT_NODE_NAME)) 
      {

         try {
            XDFElementImpl newXdfElement = new XDFElementImpl (elem);
            elem.getParentNode().replaceChild((Element) newXdfElement, elem);
//            elem = (Element) newXdfElement;
         } catch (IOException e) {
            Log.errorln("IO Error encountered: cannot create XDFElement node. Ignoring request.");
            Log.printStackTrace(e);
            System.exit(-1);
         }

      } else {

         NodeList nodeList = elem.getChildNodes();
         for ( int i = 0, size = nodeList.getLength(); i < size; i++ ) {
            Node item = nodeList.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
               convertElement ( (Element) item, doc );
            }
         }


      }

 //     return elem;
   }

}
