
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

// Import needed SAX stuff
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.ParserFactory;
import com.sun.xml.parser.Resolver;

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

    /** Pass a structure object reference to use as the structure to load 
        the file information into. Note that if the passed structure has
        prior information in it, it will remain *unless* overridden by 
        conflicting information in the input source. 
     */
    public Reader(gov.nasa.gsfc.adc.xdf.Structure structure) 
    {
       myDocumentHandler = new SaxDocumentHandler(structure);
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
     */
    public void addStartElementHandlers (Map m) {
       myDocumentHandler.addStartElementHandlers(m);
    }

    /** Merge in external Hashtable into the internal charData handler Hashtable. 
        Keys in the Hashtable are strings describing the node name in
        the XML document that has CDATA and the value is a code reference
        to the class that will handle the event. The class must implement 
        the CharDataAction interface. It is possible to override default
        XDF cdata handlers with this method. 
     */
    public void addCharDataHandlers (Map m) {
       myDocumentHandler.addCharDataHandlers(m);
    }

    /** Merge in external map to the internal endElement handler Hashtable. 
        Keys in the Hashtable are strings describing the node name in
        and the value is a code reference to the class that will handle 
        the event. The class must implement the StartElementAction interface. 
        It is possible to override default XDF startElement handlers with 
        this method. 
    */
    public void addEndElementHandlers (Map m) {
       myDocumentHandler.addEndElementHandlers(m);
    }

    /** Set the structure object that the Reader will parse an InputSource into. 
    */
    public gov.nasa.gsfc.adc.xdf.Structure getReaderStructureObj () 
    {
      return myDocumentHandler.getReaderStructureObj();
    }

    /** Get the structure object that the Reader will parse an InputSource into. 
    */
    public gov.nasa.gsfc.adc.xdf.Structure setReaderStructureObj
           (gov.nasa.gsfc.adc.xdf.Structure structure)
    {
      return myDocumentHandler.setReaderStructureObj(structure);
    }

    /** Parse an InputSource into an XDF Structure object.
        @return: XDF Structure object
     */
    public gov.nasa.gsfc.adc.xdf.Structure parse (InputSource inputsource) 
    throws java.io.IOException
    {

        try {

            Parser parser;
            String parsername = "org.apache.xerces.parsers.SAXParser";


            // create an instance of the parser
            parser = (Parser) ParserFactory.makeParser (parsername);

            // set parser handlers to XDF standard ones
            parser.setDocumentHandler(myDocumentHandler);
            parser.setErrorHandler(new SaxErrorHandler());

            // ok, now we are ready to parse the inputsource 
            parser.parse(inputsource);

        } catch (SAXParseException err) {
            Log.errorln("** Parsing error"+", line "+err.getLineNumber()
                +", uri "+err.getSystemId()+"   " + err.getMessage());

        } catch (SAXException e) {
            Exception   x = e;
            if (e.getException () != null)
                x = e.getException ();
            Log.printStackTrace(x);

        } catch (Throwable e) {
            Log.printStackTrace(e);
        }

        // return the parsed structure object
        return myDocumentHandler.getReaderStructureObj();

    }

    /** Parse a file into an XDF Structure object.
        @return: XDF Structure object
    */
    public gov.nasa.gsfc.adc.xdf.Structure parsefile (String file)
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
        input = Resolver.createInputSource (new File(file));

        // now parse it, return whatever structure is derived 
        return parse(input);

    }

} // end Reader class

//
// External classes (put here because only Reader uses them)  
//

// The parser error Handler
class SaxErrorHandler extends HandlerBase
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
    Log.error("** Warning"+", line "+e.getLineNumber()
               + ", uri " + e.getSystemId()+"   " + e.getMessage());
  }
} // End of SaxErrorHandler class 

/* Modification History:
 *
 * $Log$
 * Revision 1.7  2000/10/25 18:06:23  thomas
 * Trimmed down the import path to be explicit. -b.t.
 *
 * Revision 1.6  2000/10/25 17:56:27  thomas
 * Working beta version. -b.t.
 *
 * 
 */

