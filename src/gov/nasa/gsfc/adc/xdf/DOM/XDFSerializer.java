
package gov.nasa.gsfc.adc.xdf.DOM;

import org.apache.xml.serialize.ElementState;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.Writer;
import java.io.OutputStream;
import java.io.IOException;

public class XDFSerializer extends XMLSerializer
{

    /**
     * Constructs a new serializer. The serializer cannot be used without
     * calling {@link #setOutputCharStream} or {@link #setOutputByteStream}
     * first.
     */
    public XDFSerializer()
    {
        super();
    }


    /**
     * Constructs a new serializer. The serializer cannot be used without
     * calling {@link #setOutputCharStream} or {@link #setOutputByteStream}
     * first.
     */
    public XDFSerializer( OutputFormat format )
    {
        super(format);
    }


    /**
     * Constructs a new serializer that writes to the specified writer
     * using the specified output format. If <tt>format</tt> is null,
     * will use a default output format.
     *
     * @param writer The writer to use
     * @param format The output format to use, null for the default
     */
    public XDFSerializer( Writer writer, OutputFormat format )
    {
        super (writer, format);
    }


    /**
     * Constructs a new serializer that writes to the specified output
     * stream using the specified output format. If <tt>format</tt>
     * is null, will use a default output format.
     *
     * @param output The output stream to use
     * @param format The output format to use, null for the default
     */
    public XDFSerializer( OutputStream output, OutputFormat format )
    {
        super (output, format);
    }


    /**
     * Serialize the DOM node. This method is shared across XML, HTML and XHTML
     * serializers and the differences are masked out in a separate {@link
     * #serializeElement}.
     *
     * @param node The node to serialize
     * @see #serializeElement
     * @throws IOException An I/O exception occured while
     *   serializing
     */
    protected void serializeNode( Node node )
        throws IOException
    {
        // Based on the node type call the suitable SAX handler.
        // Only comments entities and documents which are not
        // handled by SAX are serialized directly.
        switch ( node.getNodeType() ) {
           case Node.ELEMENT_NODE : {
              // use a special serializer if this is an XDF document node
              if (node instanceof XDFElementImpl) {
                serializeXDFElement( (Element) node );
              } else {
                serializeElement( (Element) node );
              }
              break;
           }

           default:
              super.serializeNode(node);
              break;
        }
    }

    /**
     * Called to serialize an XDF DOM element. Equivalent to calling {@link
     * #startElement}, {@link #endElement} and serializing everything
     * inbetween, but better optimized.
     */
    protected void serializeXDFElement( Element elem) 
    throws IOException
    {
        ElementState state   = getElementState();
        String       tagName = elem.getTagName();

        if ( isDocumentState() ) {
            // If this is the root element handle it differently.
            // If the first root element in the document, serialize
            // the document's DOCTYPE. Space preserving defaults
            // to that of the output format.
            if ( ! _started )
                startDocument( tagName );
        } else {
            // For any other element, if first in parent, then
            // close parent's opening tag and use the parnet's
            // space preserving.
            if ( state.empty )
                _printer.printText( '>' );
            // Must leave CData section first
            if ( state.inCData )
            {
                _printer.printText( "]]>" );
                state.inCData = false;
            }
            // Indent this element on a new line if the first
            // content of the parent element or immediately
            // following an element.
            if ( _indenting && ! state.preserveSpace &&
                 ( state.empty || state.afterElement || state.afterComment) )
                _printer.breakLine();
        }

        // snarf up the XDF content from the XDFelement and print
        String XDFcontent = ((XDFElementImpl) elem).toXMLString();
        _printer.printText( XDFcontent );
        // _printer.indent(); _printer.unindent();
        // After element but parent element is no longer empty.
        state.afterElement = true;
        state.afterComment = false;
        state.empty = false;

        // flush printer (e.g. cause it to write) if this is the end 
        // of the document
        if ( isDocumentState() )
            _printer.flush();
    }

}
