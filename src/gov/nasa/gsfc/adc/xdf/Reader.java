

/** The XDF Reader class.
    Pieced together from a SAX example file (which one??). 
    @version $Revision$
*/

// XDF Reader Class
// CVS $Id$

// Reader.java Copyright (C) 2000 Brian Thomas,
// ADC/GSFC-NASA, Code 631, Greenbelt MD, 20771

/*
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/


package gov.nasa.gsfc.adc.xdf;

// Import Java stuff
import java.io.File;
// import java.util.Hashtable;
import java.util.Map;
import java.io.StringReader;

// Import needed SAX stuff
// import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;
// import org.xml.sax.helpers.ParserFactory;
//import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.EntityResolver;

import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;

/** This class is used to create Java (structure) objects from XDF files/streams.
 */
public class Reader
{

    // Fields 
    SaxDocumentHandler myDocumentHandler;

    //
    // Constructor methods 
    //

// NOTE: We need a provision for adding new/overloading handler methods
// not defined by XDF.

    /** No-argument constructor
    */
    public Reader() 
    {
       myDocumentHandler = new SaxDocumentHandler();
    }

    /** Pass a structure object reference to use as the XDF structure to load 
        the file information into. Note that if the passed XDF structure has
        prior information in it, it will remain *unless* overridden by 
        conflicting information in the input source. 
     */
    public Reader(XDF xdfObject) 
    {
       myDocumentHandler = new SaxDocumentHandler(xdfObject);
    }

    /** Pass a document handler to use by the Reader. 
     */
    public Reader(SaxDocumentHandler docHandler) 
    {
       myDocumentHandler = docHandler;
    }

    //
    // Public Methods
    //

    /** Merge in external map to the internal startElement handler Hashtable. 
        Keys in the Hashtable are strings describing the node name in
        and the value is a code reference to the class that will handle 
        the event. The class must implement the StartElementAction interface. 
        It is possible to override default XDF startElement handlers with 
        this method. 
        @return true if merged in handlers to existing table, false otherwise.
     */
    public boolean addStartElementHandlers (Map m) {
       return myDocumentHandler.addStartElementHandlers(m);
    }

    /** Merge in external Hashtable into the internal charData handler Hashtable. 
        Keys in the Hashtable are strings describing the node name in
        the XML document that has CDATA and the value is a code reference
        to the class that will handle the event. The class must implement 
        the CharDataAction interface. It is possible to override default
        XDF cdata handlers with this method. 
        @return true if merged in handlers to existing table, false otherwise.
     */
    public boolean addCharDataHandlers (Map m) {
       return myDocumentHandler.addCharDataHandlers(m);
    }

    /** Merge in external map to the internal endElement handler Hashtable. 
        Keys in the Hashtable are strings describing the node name in
        and the value is a code reference to the class that will handle 
        the event. The class must implement the StartElementAction interface. 
        It is possible to override default XDF startElement handlers with 
        this method. 
        @return true if merged in handlers to existing table, false otherwise.
    */
    public boolean addEndElementHandlers (Map m) {
       return myDocumentHandler.addEndElementHandlers(m);
    }

    /** set the default handler for the start elements in the document handler.  
     */
    public void setDefaultStartElementHandler (StartElementHandlerAction handler) {
       myDocumentHandler.setDefaultStartElementHandler(handler);
    }

   /** set the default handler for the end elements in the document handler.  
     */
    public void setDefaultEndElementHandler (EndElementHandlerAction handler) {
       myDocumentHandler.setDefaultEndElementHandler(handler);
    }

    /** set the default handler for character data in the document handler.  
     */
    public void setDefaultCharDataHandler (CharDataHandlerAction handler) {
       myDocumentHandler.setDefaultCharDataHandler(handler);
    }

    /** Get the structure object that the Reader will parse an InputSource into. 
    */
    public XDF getReaderXDFStructureObj () 
    {
      return myDocumentHandler.getReaderXDFStructureObj();
    }

    /** Set the structure object that the Reader will parse an InputSource into. 
    */
    public void setReaderXDFStructureObj (XDF XDFStructure)
    {
       myDocumentHandler.setReaderXDFStructureObj((XDF) XDFStructure);
    }

    /** Parse an InputSource into an XDF Structure object.
        @return XDF (structure) object
     */
    public XDF parse (InputSource inputsource) 
    throws java.io.IOException
    {
        String parsername = Specification.getInstance().getXMLParser();
        return parse(inputsource, parsername);

    }

    /* Parse an InputSource into an XDF Structure object.
       Set to private because we want users to only set XML parser in
       the Specification object, not override it in the method call. -b.t. 
     */
    private XDF parse (InputSource inputsource, String parsername) 
    throws java.io.IOException
    {

        Log.debugln("XDF reader is using the XML parser:"+ parsername);

        try {

/*
            Parser parser;

            // create an instance of the parser
            parser = (Parser) ParserFactory.makeParser (parsername);

            // set parser handlers to XDF standard ones
            parser.setDocumentHandler(myDocumentHandler);
            parser.setDTDHandler(myDocumentHandler);
            parser.setEntityResolver(new myEntityResolver());
            parser.setErrorHandler(new myErrorHandler());

            // ok, now we are ready to parse the inputsource 
            parser.parse(inputsource);
*/

            XMLReader parser = XMLReaderFactory.createXMLReader(parsername);

            // set parser handlers to XDF standard ones
            parser.setDTDHandler(myDocumentHandler);
            parser.setContentHandler(myDocumentHandler);
            parser.setErrorHandler (new myErrorHandler());
            parser.setEntityResolver(new myEntityResolver());
            
            // To set the LexicalHandler for an XML reader, use the setProperty method with the
            // propertyId "http://xml.org/sax/properties/lexical-handler". If the reader does not support
            // lexical events, it will throw a SAXNotRecognizedException or a
            // SAXNotSupportedException when you attempt to register the handler.
            try {
               parser.setProperty("http://xml.org/sax/properties/lexical-handler", myDocumentHandler);
            } catch (org.xml.sax.SAXNotSupportedException e) {
               Log.infoln("This parser does not support LexicalHandlers and does not allow inspection of DTD events by the XDF reader.");
               Log.infoln("This means that XDF objects returned by this parser may not have their XMLDeclaration and DocumentType objects set appropriately.");
               myDocumentHandler.setForceSetXMLHeaderStuffOnXDFObject(true);
            } catch (org.xml.sax.SAXNotRecognizedException e) {
               Log.infoln("This parser does not support LexicalHandlers and does not allow inspection of DTD events by the XDF reader.");
               Log.infoln("This means that XDF objects returned by this parser may not have their XMLDeclaration and DocumentType objects set appropriately.");
               myDocumentHandler.setForceSetXMLHeaderStuffOnXDFObject(true);
            } catch (Exception e) {
               e.printStackTrace();
            }

            // ok, now we are ready to parse the inputsource 
            parser.parse(inputsource);

        } catch (SAXParseException err) {
            String message = "** Parsing error"+", line "+err.getLineNumber()
                +", uri "+err.getSystemId()+"   " + err.getMessage();
            throw new java.io.IOException(message);

        } catch (SAXException e) {
            Exception   x = e;
            if (e.getException () != null)
                x = e.getException ();
            Log.printStackTrace(x);
            throw new java.io.IOException(x.getMessage());

        } catch (Throwable e) {
            Log.printStackTrace(e);
            throw new java.io.IOException(e.getMessage());
        }

        // return the parsed structure object
        return myDocumentHandler.getReaderXDFStructureObj();

    }

    /** Parse a file into an XDF Structure object.
        @return XDF (structure) object
        @deprecated use parseFile method instead.
     */
    public XDF parsefile (String file)
    throws java.io.IOException
    {
       return parseFile(file);
    }

    /** Parse a file into an XDF Structure object.
        @return XDF (structure) object
    */
    public XDF parseFile (String file)
    throws java.io.IOException
    {

        InputSource input;

        //
        // Turn the filename into an input source
        //
        // NOTE:  The input source must have a "system ID" if
        // there are relative URLs in the input document.  The
        // static resolver methods handle that automatically
        // in most cases.
        //
//        input = Resolver.createInputSource (new File(file));
        input = new InputSource (file);


        // now parse it, return whatever structure is derived 
        return parse(input);

    }

    public XDF parseString (String XMLContent)
    throws java.io.IOException
    {

        InputSource input;
        StringReader reader = new StringReader(XMLContent);

        //
        // Turn the filename into an input source
        //
        // NOTE:  The input source must have a "system ID" if
        // there are relative URLs in the input document.  The
        // static resolver methods handle that automatically
        // in most cases.
        //
//        input = Resolver.createInputSource (new File(file));
        input = new InputSource (reader);


        // now parse it, return whatever structure is derived 
        XDF myXDFObject = parse(input);
        return myXDFObject;

    }


} // end Reader class

//
// External classes (put here because only Reader uses them)  
//

// The parser error Handler
class myErrorHandler implements ErrorHandler
{
  // treat validation errors as fatal
  public void error (SAXParseException e)
  throws SAXParseException
  {
    throw e;
  }

  // dump warnings too
  public void warning (SAXParseException e)
  throws SAXParseException
  {
    Log.errorln("** Warning"+", line "+e.getLineNumber()
               + ", uri " + e.getSystemId());
    Log.errorln("   " + e.getMessage()+" **");
  }

  public void fatalError (SAXParseException e)
  throws SAXException
  {
     throw e;
  }

} // End of myErrorHandler class 

// parser EntityResolver
class myEntityResolver implements EntityResolver {

   public InputSource resolveEntity (String publicId, String systemId)
   {

     Log.debugln("CALL to Entity Resolver:"+publicId+" "+systemId);
     if (systemId != null) {
        // return a special input source
        return new InputSource(systemId);
     } else {
        // use the default behaviour
        return null;
     }
   }


} // End of myEntityResolver class 

