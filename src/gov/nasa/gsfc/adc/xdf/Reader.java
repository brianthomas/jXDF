
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
import java.util.Hashtable;
import java.util.Map;

// Import needed SAX stuff
import org.w3c.dom.*; // W3c SAX standard bits
import org.xml.sax.*;
import org.xml.sax.helpers.ParserFactory;
import com.sun.xml.parser.Resolver;

/** This class is used to create Java (structure) objects from XDF files/streams.

    There are a few things TODO yet: 
    We also need to allow options to:
    1 - use parser other than Expat (?)
    2 - allow passing of addtional start/end handlers for user-defined nodes

 */
public class Reader
{

    // Fields 
    XDFSaxDocumentHandler myDocumentHandler;

    //
    // Constructor methods 
    //

// NOTE: We need a provision for adding new/overloading handler methods
// not defined by XDF.

    /** No-argument constructor
    */
    public Reader() 
    {
       myDocumentHandler = new XDFSaxDocumentHandler();
    }

    /** Pass a structure object reference to use as the structure to load 
        the file information into. Note that if the passed structure has
        prior information in it, it will remain *unless* overridden by 
        conflicting information in the input source. 
     */
    public Reader(gov.nasa.gsfc.adc.xdf.Structure structure) 
    {
       myDocumentHandler = new XDFSaxDocumentHandler(structure);
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
            parser.setErrorHandler(new XDFSaxErrorHandler());

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
class XDFSaxErrorHandler extends HandlerBase
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
    //Log.error("** Warning"+", line "+e.getLineNumber()
    Log.error("** Warning"+", line "+e.getLineNumber()
               + ", uri " + e.getSystemId()+"   " + e.getMessage());
  }
} // End of XDFSaxErrorHandler class 


// Document Handler class - where the real action is 
class XDFSaxDocumentHandler implements DocumentHandler {

    // 
    // Fields
    //

    // The XDF structure that is populated by the XDF DocumentHandler
    private gov.nasa.gsfc.adc.xdf.Structure XDF; 

    // Options for the document handler
    private Hashtable Options;

    // dispatch table action handler hashtables
    private Hashtable startElementHandlerHashtable; // start node handler
    private Hashtable charDataHandlerHashtable;     // charData handler
    private Hashtable endElementHandlerHashtable;   // end node handler

    // References to the current working structure/array
    private gov.nasa.gsfc.adc.xdf.Structure currentStructure;   
    private gov.nasa.gsfc.adc.xdf.Array     currentArray;   
    private String currentDatatypeObject;

    // lookup tables holding objects that have id/idref stuff
    private Hashtable AxisObj = new Hashtable();

    //
    // Constuctors
    //

    public XDFSaxDocumentHandler ()
    {
       init();
    }

    public XDFSaxDocumentHandler (gov.nasa.gsfc.adc.xdf.Structure structure)
    {
       init();
       setReaderStructureObj(structure);
    }

    public XDFSaxDocumentHandler (Hashtable options)
    {
       init();
       Options = options;
    }

    //
    // Non-Sax Public Methods
    //

    /** Set the structure object that the Reader will parse an InputSource into. 
    */
    public gov.nasa.gsfc.adc.xdf.Structure getReaderStructureObj () 
    {
      return XDF;
    }

    /** Get the structure object that the Reader will parse an InputSource into. 
    */
    public gov.nasa.gsfc.adc.xdf.Structure setReaderStructureObj
           (gov.nasa.gsfc.adc.xdf.Structure structure)
    {

       XDF = structure; // set the structure to read into to be passed ref. 
       return XDF;

    }

    /** Merge in external map to the internal startElement handler Hashtable. 
        Keys in the Hashtable are strings describing the node name in
        and the value is a code reference to the class that will handle 
        the event. The class must implement the StartElementAction interface. 
        It is possible to override default XDF startElement handlers with 
        this method. 
     */
    public void addStartElementHandlers (Map m) {
       startElementHandlerHashtable.putAll(m);
    }

    /** Merge in external Hashtable into the internal charData handler Hashtable. 
        Keys in the Hashtable are strings describing the node name in
        the XML document that has CDATA and the value is a code reference
        to the class that will handle the event. The class must implement 
        the CharDataAction interface. It is possible to override default
        XDF cdata handlers with this method. 
     */
    public void addCharDataHandlers (Map m) {
       charDataHandlerHashtable.putAll(m);
    }

    /** Merge in external map to the internal endElement handler Hashtable. 
        Keys in the Hashtable are strings describing the node name in
        and the value is a code reference to the class that will handle 
        the event. The class must implement the StartElementAction interface. 
        It is possible to override default XDF startElement handlers with 
        this method. 
    */
    public void addEndElementHandlers (Map m) {
       endElementHandlerHashtable.putAll(m);
    }

    //
    // SAX methods
    //

    // what is this for?? Required by the DocumentHandler interface. feh. 
    public void setDocumentLocator (org.xml.sax.Locator l)
    {
        // we'd record this if we needed to resolve relative URIs
        // in content or attributes, or wanted to give diagnostics.
        // Right now, do nothing here.
    }

    /** startElement handler.
     */
    public void startElement (String element, AttributeList attrs)
    throws SAXException
    {

        Log.debugln("H_START:["+element+"]");
        

        // add "element" to current path (??) 
        // push @CURRENT_NODE_PATH, $element

        // if a handler exists, run it, else give a warning
        if ( startElementHandlerHashtable.containsKey(element) ) {

           // run the appropriate start handler
           StartElementHandlerAction event = 
              (StartElementHandlerAction) startElementHandlerHashtable.get(element); 
           event.action(attrs);

        } else {
           Log.warn("Warning: UNKNOWN NODE ["+element+"] encountered.\n");
        }
    }

    public void endElement (String element)
    throws SAXException
    {

        Log.debugln("H_END:["+element+"]");

       // peel off the last element in the current path
       // my $last_element = pop @CURRENT_NODE_PATH;

       // if a handler exists, run it, else give a warning
        if ( endElementHandlerHashtable.containsKey(element) ) {

           // run the appropriate start handler
           EndElementHandlerAction event = (EndElementHandlerAction) 
                   endElementHandlerHashtable.get(element);
           event.action();

        } else {

           // do nothing

        }
    }

    public void startDocument()
    throws SAXException
    {
        Log.debugln("<?xml version='1.0' encoding='UTF-8'?>");
    }

    public void endDocument()
    throws SAXException
    {
        // this space left intentionally blank
    }

    public void ignorableWhitespace(char buf [], int offset, int len)
    throws SAXException
    {
        // Note from the SAX API:
        // this callback won't be used consistently by all parsers,
        // unless they read the whole DTD.  Validating parsers will
        // use it, and currently most SAX nonvalidating ones will
        // also; but nonvalidating parsers might hardly use it,
        // depending on the DTD structure.
        Log.debugln("Whitespace BUF:["+buf+"] OFFSET:["+ offset+"] LEN:["+len+"]");
    }

    public void processingInstruction(String target, String data)
    throws SAXException
    {
        Log.debugln("<?"+target+" "+data+"?>");
    }

    /**  character Data handler
     */
    public void characters (char buf [], int offset, int len)
    throws SAXException
    {
        // NOTE:  this doesn't escape '&' and '<', but it should
        // do so else the output isn't well formed XML.  To do this
        // right, scan the buffer and write '&amp;' and '&lt' as
        // appropriate.
        Log.debugln("CharData BUF:["+buf+"] OFFSET:["+ offset+"] LEN:["+len+"]");
    }

    //
    // Private Methods
    //

    /** called by all constructors. May be used to re-initalize reader. 
     */
    private void init () {

      // set up logging, needed ??
      Log.configure("XDFLogConfig");
      
      // assign/init 'globals' (e.g. object fields)
      XDF = new gov.nasa.gsfc.adc.xdf.Structure();
      Options = new Hashtable();  
      startElementHandlerHashtable = new Hashtable(); // start node handler
      charDataHandlerHashtable = new Hashtable(); // charData handler
      endElementHandlerHashtable = new Hashtable(); // end node handler

      // initialize the default XDF parser dispatch tables
      initStartHandlerHashtable();
      initCharDataHandlerHashtable();
      initEndHandlerHashtable();

    }

    // Placeholder to remind me to do some version checking w/ base class
    private boolean checkXDFDocVersion (String version)
    {
      // if(version != xdfVersion) { return false; } else { return true; }
      return false;
    }

    // required by the DocumentHandler Interface
    private void setDocumentHandler (DocumentHandler myHandler) { }

    //
    private void initStartHandlerHashtable () {

       startElementHandlerHashtable.put(XDFNodeName.ARRAY, new arrayStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.AXIS, new axisStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.ROOT, new rootStartElementHandlerFunc());

    }

    // 
    private void initCharDataHandlerHashtable () {

    }

    //
    private void initEndHandlerHashtable () {

       // endElementHandlerHashtable.put(XDFNodeName.ARRAY, new arrayEndElementHandlerFunc());

    }

    //
    // Internal Classes
    //

    /*
       Now, Some defines based on XDF DTD.
       Change these as needed to reflect new namings of same nodes as they occur.
    */
    static class XDFNodeName 
    {
       // *sigh* cant decide if making this hashtable is better or not.
       protected static final String ARRAY = "array";
       protected static final String AXIS = "axis";
       protected static final String AXISUNITS= "axisUnits";
       protected static final String BINARYFLOAT = "binaryFloat";
       protected static final String binaryInteger = "binaryInteger";
       protected static final String data = "data";
       protected static final String dataFormat = "dataFormat";
       protected static final String exponent = "exponent";
       protected static final String field = "field";
       protected static final String fieldAxis = "fieldAxis";
       protected static final String fixed = "fixed";
       protected static final String fornode = "for";
       protected static final String fieldGroup = "fieldGroup";
       protected static final String index = "index";
       protected static final String integer = "integer";
       protected static final String locationOrder = "locationOrder";
       protected static final String note = "note";
       protected static final String notes = "notes";
       protected static final String parameter = "parameter";
       protected static final String parameterGroup = "parameterGroup";
       protected static final String ROOT = "XDF"; // beware setting this to the same name as structure 
       protected static final String read = "read";
       protected static final String readCell = "readCell";
       protected static final String repeat = "repeat";
       protected static final String relationship = "relation";
       protected static final String skipChar = "skipChars";
       protected static final String structure = "structure";
       protected static final String string = "string";
       protected static final String tagToAxis = "tagToAxis";
       protected static final String td0 = "d0";
       protected static final String td1 = "d1";
       protected static final String td2 = "d2";
       protected static final String td3 = "d3";
       protected static final String td4 = "d4";
       protected static final String td5 = "d5";
       protected static final String td6 = "d6";
       protected static final String td7 = "d7";
       protected static final String td8 = "d8";
       protected static final String textDelimiter = "textDelimiter";
       protected static final String unit = "unit";
       protected static final String units = "units";
       protected static final String unitless = "unitless";
       protected static final String valueList = "valueList";
       protected static final String value = "value";
       protected static final String valueGroup = "valueGroup";
       protected static final String vector = "unitDirection";

    } // End of XDFNodeName class 

    // 
    // Dispatch Table Handlers 
    //

    // ASCII DELIMITER NODE HANDLERS
/*
sub asciiDelimiter_node_end { pop @CURRENT_FORMAT_OBJECT; }

sub asciiDelimiter_node_start {
  my (%attrib_hash) = @_;

  # set the format object in the current array
  my $formatObj = $CURRENT_ARRAY->XmlDataIOStyle(new XDF::DelimitedXMLDataIOStyle(\%attrib_hash));

  push @CURRENT_FORMAT_OBJECT, $formatObj;

}
*/
    // ARRAY NODE
    //

    // Array node start 
    class arrayStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 

          // create new object appropriately 
          Array newarray = new Array();
          newarray.setXMLAttributes(attrs); // set XML attributes from passed list 

          // set current array and add this array to current structure 
          currentArray = currentStructure.addArray(newarray);

          currentDatatypeObject = "currentArray";

       }
    } 

    // AXIS NODE 
    //

    class axisStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 

          // create new object appropriately 
          Axis newaxis = new Axis();
          newaxis.setXMLAttributes(attrs); // set XML attributes from passed list 

          // Every axis must have *either* axisId *or* an axisIdRef 
          // else, its illegal!
          String axisId = null;
          String axisIdRef = null;
          if ( (axisId = newaxis.getAxisId()) != null 
                || (axisIdRef = newaxis.getAxisIdRef()) != null 
             ) 
          { 

             // add this object to the lookup table, if it has an ID
             if (axisId != null) {

                 // a warning check, just in case 
                 if (AxisObj.contains(axisId)) 
                    Log.warnln("More than one axis node with axisId=\""+axisId+"\", using latest node.\n" ); 

                 // add this into the list of axis objects
                 AxisObj.put(axisId, newaxis);

             }

             //  If there is a reference object, clone it to get
             //  the new axis
             if (axisIdRef != null) {

                 if (AxisObj.contains(axisIdRef)) {

 //                   Object refAxisObj = AxisObj.get(axisIdRef);
//                    newaxis = (Axis) refAxisObj.clone();

                    // override attrs with those in passed list
//                    newaxis.setXMLAttributes(attrs);

                 } else {

                    Log.errorln("Error: Reader got an axis with AxisIdRef=\""+axisIdRef+"\" but no previous axis has that id! Ignoring add axis request.");
                    return;

                 }
             }

             // add this axis to the current array object
             currentArray.addAxis(newaxis);

             currentDatatypeObject = "lastAxis";

          } else {

             Log.errorln("Axis object:"+newaxis+" lacks either axisId or axisIdRef, ignoring!");

          }

       }
    }

    // ROOT NODE 
    //

    // Root node start 
    class rootStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 
          // The root node is just a "structure" node,
          // but is always the first one.
          XDF.setXMLAttributes(attrs); // set XML attributes from passed list 
          currentStructure = XDF;      // current working structure is now the root 
                                       // structure

          // if this global option is set in the reader, we use it
          if(Options.contains("DefaultDataDimensionSize")) { 
             int value = ((Integer) Options.get("DefaultDataDimensionSize")).intValue();
             XDF.setDefaultDataArraySize(value);
          }
       }
    }


// generic node end
class genericEndElementHandlerFunc implements EndElementHandlerAction {
   public void action () { System.out.println("End generic node"); }
}

// CDATA EXAMPLE FUNCTION STUFF
class charDataXDFHandlerFunc1 implements CharDataHandlerAction {
  public void action (char buf [], int offset, int len) { 
    System.out.println("call to start XDF node"); 
  }
}

} // End of XDFSaxDocumentHandler class 

// 
// Interfaces for Parser DocumentHandler Actions 
//

interface StartElementHandlerAction {
  public void action (AttributeList attrs);
}

interface CharDataHandlerAction {
  public void action (char buf [], int offset, int len);
}

interface EndElementHandlerAction {
  public void action ();
}

/* Modification History:
 *
 * $Log$
 * Revision 1.4  2000/10/24 17:03:10  thomas
 * This is an interim version. Most of the basic structure
 * is here, but the reader lacks the various handlers
 * needed for various XDF nodes. -b.t.
 *
 * 
 */

