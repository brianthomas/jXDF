
/** The XDF SaxDocumentHandler class.
    Pieced together from a SAX example file (which one??). 
    @version $Revision$
*/

// XDF SaxDocumentHandler Class
// CVS $Id$

// SaxDocumentHandler.java Copyright (C) 2000 Brian Thomas,
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
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Enumeration;
import java.util.Iterator;
import java.lang.Character;

// Import needed SAX stuff
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.SAXException;

import org.xml.sax.InputSource;

import java.io.Reader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

// these can problably be dropped
import java.io.FileReader;

/** 
 */
public class SaxDocumentHandler extends HandlerBase {

    // 
    // Fields
    //

    // designate the version of the XDF DTD that this doc handler
    // is written for, it should match up with the BaseObject sXDFDTDName
    // Not clear that checking between other than baseobject DTDName and
    // the declared document version is needed (so eliminate this field..)
    private static final String sHandlerXDFDTDName = "XDF_0.17.dtd";

    // The XDF structure that is populated by the XDF DocumentHandler
    private Structure XDF; 

    // Options for the document handler
    private Hashtable Options;

    // dispatch table action handler hashtables
    private Hashtable startElementHandlerHashtable; // start node handler
    private Hashtable charDataHandlerHashtable;     // charData handler
    private Hashtable endElementHandlerHashtable;   // end node handler

    // References to the current working structure/array
    private Structure CurrentStructure;   
    private Array     CurrentArray;   
    private Object    CurrentDatatypeObject;
    private ArrayList CurrentNodePath = new ArrayList();
    private ArrayList CurrentFormatObjectList = new ArrayList ();
    // group objects
    private ArrayList CurrentParameterGroupList = new ArrayList();
    private ArrayList CurrentFieldGroupList = new ArrayList();
    private ArrayList CurrentValueGroupList = new ArrayList();

    // the last object created by a startElementNodeActionHandler
    private Object ParentObject; 
    private Object CurrentObject; 
    private ArrayList CurrentObjectList = new ArrayList(); 
    private boolean UpdateCurrentObject = false; 

    // needed to capture internal entities.
    private HashSet Notation = new HashSet();
//    private Hashtable Entity = new Hashtable();
    private Hashtable UnParsedEntity = new Hashtable();

    // GLOBALs for saving these between dataFormat/read node and later when we 
    // know what kind of DataFormat/DataIOStyle object we really have
    private Hashtable ValueAttribs = new Hashtable();
    private Hashtable DataIOStyleAttribs = new Hashtable();
    private AttributeList DataFormatAttribs;

    // for tagged reads only. Keeps track of which data tags are open
    // so we know which datacell in the current datacube to shunt the 
    // data to.
    private Hashtable DataTagCount = new Hashtable();

    private int INPUTREADSIZE  = 32768; // byte buffer for reads 

    // References recording the last object of these types created while
    // parsing the document
    private Parameter LastParameterObject;
    private Field     LastFieldObject;
    private Note      LastNoteObject;
    private Unit      LastUnitObject;
    private Units     LastUnitsObject;

    // store some of the parent objects for various nodes
    private Object LastParameterGroupParentObject;
    private Object LastFieldGroupParentObject;
    private Object LastValueGroupParentObject;

    // Notes stuff
    private ArrayList NoteLocatorOrder = new ArrayList();

    // Keeping track of working valueList node (attributes) settings
    private Hashtable CurrentValueListParameter = new Hashtable();

    // Data writing stuff
    private int CurrentDataTagLevel = 0; // how nested we are within d0/d1/d2 data tags
    private int DataNodeLevel = 0; // how deeply nested we are within data nodes 
    private int DataTagLevel = 0; // the level where the actual char data is
    private Locator TaggedLocatorObj;
    private StringBuffer DATABLOCK;
    private boolean CDATAIsArrayData = false;
    private int MaxDataFormatIndex = 0;     // max allowed index in the DataFormatList[]
    private int CurrentIOCmdIndex = 0;      // For formatted reads, which formattedIOCmd we are currently reading 
    private int CurrentDataFormatIndex = 0; // which dataformat index (in DatFormatList[]) we currently are reading
    private DataFormat DataFormatList[];       // list of CurrentArray.getDataFormatList();
    private int LastFastAxisCoordinate;
    private AxisInterface FastestAxis;
    private ArrayList AxisReadOrder;

    // lookup tables holding objects that have id/idref stuff
    private Hashtable FieldObj = new Hashtable();
    private Hashtable ValueListObj = new Hashtable();
    private Hashtable ValueObj = new Hashtable();
    private Hashtable ParamObj = new Hashtable();
    private Hashtable AxisObj = new Hashtable();
    private Hashtable ReadObj = new Hashtable();
    private Hashtable NoteObj = new Hashtable();

    // this is a BAD thing. I have been having troubles distinguishing between
    // important whitespace (e.g. char data within a data node) and text nodes
    // that are purely for the layout of the XML document. Right now I use the 
    // CRUDE distinquishing characteristic that fluff (eg. only there for the sake
    // of formatting the output doc) text nodes are all whitespace.
    // Used by TAGGED data arrays
    private boolean IgnoreWhitespaceOnlyData = true;

    //
    // Constuctors
    //

    public SaxDocumentHandler ()
    {
       init();
    }

    public SaxDocumentHandler (Structure structure)
    {
       init();
       setReaderStructureObj(structure);
    }

    public SaxDocumentHandler (Hashtable options)
    {
       init();
       Options = options;
    }

    //
    // Non-Sax Public Methods
    //

    /** Get the structure object that the Reader will parse an InputSource into. 
    */
    public Structure getReaderStructureObj () 
    {
      return XDF;
    }

    /** Set the structure object that the Reader will parse an InputSource into. 
    */
    public void setReaderStructureObj (Structure structure)
    {
       XDF = structure; // set the structure to read into to be passed ref. 
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
    // Methods that describe the current parsing
    //

    // get the last object we worked on
    public Object getLastObject() {
       Object lastObject = (Object) null;
       if (CurrentObjectList.size() > 0)
          lastObject = CurrentObjectList.get(CurrentObjectList.size()-1);
       return lastObject;
    }

    public String getCurrentNodeName () {
       int pathSize = CurrentNodePath.size();
       return (String) CurrentNodePath.get((pathSize-1));
    }

    // find unique id name within a list of objects 
    public String findUniqueIdName( Hashtable list, String baseIdName) {

       StringBuffer testName = new StringBuffer(baseIdName);

       while (list.containsKey(testName.toString())) {
           testName.append("0"); // isnt there something better to append here?? 
       }

       return testName.toString();

    }

    // return the element before last 
    public String getParentNodeName () {

       String parentNodeName = null;
       int pathSize = CurrentNodePath.size();

       if (pathSize > 1) {
          parentNodeName = (String) CurrentNodePath.get((pathSize-2));
       }

       return parentNodeName;

    }

    // return 2 elements before last 
    public String getGrandParentNodeName () {

       String gParentNodeName = null;
       int pathSize = CurrentNodePath.size();

       if (pathSize > 2) {
          gParentNodeName = (String) CurrentNodePath.get((pathSize-3));
       }

       return gParentNodeName;

    }

    public String getParentNodeName (String ignoreThisParentName) {

       int pathSize = CurrentNodePath.size();
       int currentNode = 2;
       if (pathSize > 1) 
          while (currentNode <= pathSize) { 
             String testNodeName = (String) CurrentNodePath.get((pathSize-currentNode));
             if(testNodeName.equals(ignoreThisParentName)) {
                // do nothing
             } else 
                return testNodeName; // this is the one. 
             currentNode++;
       }

       return (String) null;

    }

    public String getAttributeListValueByName (AttributeList attrs, String name) {
       if (attrs != null) {
          // whip thru the list, checking each value
          int size = attrs.getLength();
          for (int i = 0; i < size; i++) {
             String attribName = attrs.getName(i);
             if (attribName.equals(name)) return attrs.getValue(i);
          }
       }
       return (String) null;
    }

    public void setCurrentDatatypeObject(Object object) {
        CurrentDatatypeObject = object;
    }

    public Object getCurrentDatatypeObject() {
       return CurrentDatatypeObject;
    }

    public void setCurrentArray(Array array) {
       CurrentArray = array;
    }

    public Array getCurrentArray () {
       return CurrentArray;
    }

    public void setCurrentStructure (Structure structure) {
       CurrentStructure = structure;
    }

    public Structure getCurrentStructure () {
       return CurrentStructure;
    }

    //
    // SAX methods
    //

    /** startElement handler.
     */
    public void startElement (String element, AttributeList attrs)
    throws SAXException
    {

        Log.debugln("H_START:["+element+"]");
        
        Object thisObject = (Object) null;

        // add "element" to current path (??) 
        CurrentNodePath.add(element); 

        // if a handler exists, run it, else give a warning
        if ( startElementHandlerHashtable.containsKey(element) ) {

           UpdateCurrentObject = false;

           // run the appropriate start handler
           StartElementHandlerAction event = 
              (StartElementHandlerAction) startElementHandlerHashtable.get(element); 
           thisObject = event.action(this,attrs);

        } else {
           Log.warnln("Warning: UNKNOWN NODE ["+element+"] encountered.");
        }

        CurrentObjectList.add(thisObject);

    }

    public void endElement (String element)
    throws SAXException
    {

        Log.debugln("H_END:["+element+"]");

        // peel off the last element in the current path
        CurrentNodePath.remove(CurrentNodePath.size()-1); 

        // if a handler exists, run it, else give a warning
        if ( endElementHandlerHashtable.containsKey(element) ) {

           // run the appropriate end handler
           EndElementHandlerAction event = (EndElementHandlerAction) 
                   endElementHandlerHashtable.get(element);
           event.action(this);

        } else {

           // do nothing

        }

        // peel off last object in object list
        CurrentObjectList.remove(CurrentObjectList.size()-1);

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
        Log.debugln("H_CharData:["+new String(buf,offset,len)+"]");

        /* we need to know what the current node is in order to 
           know what to do with this data, however, 
           early on when reading the DOCTYPE, other nodes we can get 
           text nodes which are not meaningful to us. Ignore all
           character data until we open the root node.
         */

        String currentNodeName = (String) CurrentNodePath.get(CurrentNodePath.size()-1); 

        if ( charDataHandlerHashtable.containsKey(currentNodeName) ) {

          // run the appropriate character data handler
           CharDataHandlerAction event = (CharDataHandlerAction)
                   charDataHandlerHashtable.get(currentNodeName);
           event.action(this,buf,offset,len);

        } else {

           // perhaps we are reading in data at the moment??

           if (DataNodeLevel > 0) {

              CharDataHandlerAction event = new dataCharDataHandlerFunc();
              event.action(this,buf,offset,len);

           } else {

               // do nothing with other character data

           }

        }

    }

    // 
    // Public SAX methods we dont use
    //

/*
    public void setDocumentLocator (org.xml.sax.Locator l)
    {

        // we'd record this if we needed to resolve relative URIs
        // in content or attributes, or wanted to give diagnostics.
        // Right now, do nothing here.

    }
*/

    public void startDocument()
    throws SAXException
    {
        // do nothing
    }

    public void endDocument()
    throws SAXException
    {
        // do nothing
        //set the notation hash for the XDF structure
        XDF.setXMLNotationHash(Notation);
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
        // Log.debugln("I Whitespace:["+new String(buf,offset,len)+"]");

        // do nothing, method required by interface 
    }

    // not used ?? 
    public void internalEntityDecl( String name, String value)
    throws SAXException
    {

       Log.debugln("H_INTERNAL_ENTITY: "+name+" "+value);

    }

    // not used ?? 
    public void externalEntityDecl ( String name,
                                     String publicId,
                                     String systemId )
    throws SAXException
    {

        Log.debugln("H_EXTERNAL_ENTITY: "+name+" "+publicId+" "+systemId);

    }

    /* Hurm, why doesnt this method treat 'base'?? */
    public void unparsedEntityDecl ( String name,  
                                     String publicId, 
                                     String systemId,
                                     String notationName ) 
    {
        Log.debugln("H_UNPARSED_ENTITY: "+name+" "+publicId+" "+systemId+" "+notationName);

        // create hashtable to hold information about Unparsed entity
        Hashtable information = new Hashtable ();
        information.put("name", name);
        // if (base != null) information.put("base", base);
        if (publicId != null) information.put("pubId", publicId);
        if (systemId != null) information.put("sysId", systemId);
        if (notationName != null) information.put("ndata", notationName);

        // add this to the UnparsedEntity hash
        UnParsedEntity.put(name, information);
    }

    /* Hurm, why doesnt this method treat 'base'?? */
    public void notationDecl (String name, String publicId, String systemId )
    {
        Log.debugln("H_NOTATION: "+name+" "+publicId+" "+systemId);

        // create hash to hold information about notation.
        Hashtable information = new Hashtable ();
        information.put("name", name);
        if (publicId != null) information.put("publicId", publicId);
        if (systemId != null) information.put("systemId", systemId);
       
        // add this to the Notation hash
        Notation.add(information);

    }

    public void processingInstruction(String target, String data)
    throws SAXException
    {
        Log.debugln("H_PROCESSING_INSTRUCTION:"+"<?"+target+" "+data+"?>");
        // do nothing
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
      XDF = new Structure();
      Options = new Hashtable();  
      startElementHandlerHashtable = new Hashtable(); // start node handler
      charDataHandlerHashtable = new Hashtable(); // charData handler
      endElementHandlerHashtable = new Hashtable(); // end node handler

      // initialize the default XDF parser dispatch tables
      initStartHandlerHashtable();
      initCharDataHandlerHashtable();
      initEndHandlerHashtable();

    }

    // This is NOT the best implimentation. In fact, I freely 
    // admit that I dont understand what the entityResolver is 
    // trying to do here.. At this time, it will read a file, 
    // I seriously doubt if this will really work for other URI's 
    // without serious change.
    private String getHrefData (Href hrefObj) {

       String buffer = null;

       // well, we should be doing something with base here, 
       // but arent because it isnt captured by this API. feh.
       // $file = $href->getBase() if $href->getBase();

       if (hrefObj.getSysId() != null) {

          // we assume here that this is a file. hurm.
          String fileName = hrefObj.getSysId();
          int size = 0;

          try {

             java.io.Reader in = null;

             try {
               InputSource input = resolveEntity(hrefObj.getPubId(), hrefObj.getSysId()); 
               in = (java.io.Reader) input.getCharacterStream();
             } catch (SAXException e) {
                Log.printStackTrace(e);
             } catch (NullPointerException e) {
                // in this case the InputSource object is null to request that 
                // the parser open a regular URI connection to the system identifier.
                // In our case, the sysId IS the filename.
                File f = new File(hrefObj.getSysId());
                size = (int) f.length();
                in = (java.io.Reader) new FileReader(new File(hrefObj.getSysId()));
             }

             // ok, got a reader, read the info
             // Need to use a buffered reader here!!!
             if (in != null) {
                char[] data = new char[size]; 
                int chars_read = 0;

                while (chars_read < size) 
                  chars_read += in.read(data, chars_read, size-chars_read);
          
                buffer = new String(data);
             }
  
          } catch (java.io.IOException e) {
             Log.printStackTrace(e);
          }

       } else {
          Log.warnln("Can't read Href data, undefined sysId!");
       }

       return buffer;
    } 


    private void loadHrefDataIntoCurrentArray () {
 
       Href hrefObj = CurrentArray.getDataCube().getHref();

       // well, we should be doing something with base here, 
       // but arent because it isnt captured by this API. feh.
       // $file = $href->getBase() if $href->getBase();

       if (hrefObj.getSysId() != null) {

          try {

             InputStream in = null;

             try {
               InputSource inputSource = resolveEntity(hrefObj.getPubId(), hrefObj.getSysId());
               in = inputSource.getByteStream();
             } catch (SAXException e) {
                Log.printStackTrace(e);
             } catch (NullPointerException e) {
                // in this case the InputSource object is null to request that 
                // the parser open a regular URI connection to the system identifier.
                // In our case, the sysId IS the filename.
                File f = new File(hrefObj.getSysId());
                in = (InputStream) new FileInputStream(new File(hrefObj.getSysId()));
             }

             // ok, got a bytestream, now read the info
             // Need to use a buffered reader here!!!
             if (in != null) {
                // probably could treat endian/nrofDataFormat as a globals too, since thats
                // how we treat the rest of the array parameters
                int nrofDataFormat = DataFormatList.length;
                String endian = CurrentArray.getXMLDataIOStyle().getEndian();
                byte[] data = new byte[INPUTREADSIZE];
                int bytes_read = 0;

                while ( true ) {

                  int readAmount = in.read(data, bytes_read, INPUTREADSIZE-bytes_read);

                  if ( readAmount == -1 )
                  {
                      // pour out remaining buffer into the current array
                     addByteDataToCurrentArray(data, bytes_read, endian, nrofDataFormat);

Log.errorln("Dumping buffer after reading in "+bytes_read+" bytes");

                      break; // EOF reached
                  }

                  bytes_read += readAmount;

                  // we exceeded the size of the buffer, dump to list
                  if ( bytes_read == INPUTREADSIZE) 
                  { 

Log.errorln("Dumping buffer after reading in "+bytes_read+" bytes");

                     // pour out buffer into array
                     addByteDataToCurrentArray(data, bytes_read, endian, nrofDataFormat);
                     bytes_read = 0;
                  }

                }

             }

          } catch (java.io.IOException e) {
             Log.printStackTrace(e);
          }

       } else {
          Log.warnln("Can't read Href data, undefined sysId!");
       }

    }

// the problem: need to keep track of the formatting chars. 
    private void addByteDataToCurrentArray (byte[] data, int amount, String endian, int nrofDataFormat) {

        ArrayList commandList = (ArrayList) ((FormattedXMLDataIOStyle) CurrentArray.getXMLDataIOStyle()).getCommands();
        int nrofIOCmd = commandList.size();
        int bytes_added = 0;

Log.errorln("Adding "+amount+" bytes of data to current array");

        while (bytes_added < amount) {

            FormattedIOCmd currentIOCmd = (FormattedIOCmd) commandList.get(CurrentIOCmdIndex);

            // readCell
            if (currentIOCmd instanceof ReadCellFormattedIOCmd) {

               DataFormat currentDataFormat = DataFormatList[CurrentDataFormatIndex];
               int bytes_to_add = currentDataFormat.numOfBytes();

Log.errorln("Adding "+bytes_to_add+" bytes of data to current array");
//Object objectToAdd = null;

               if ( currentDataFormat instanceof IntegerDataFormat) {

               } else if (currentDataFormat instanceof FixedDataFormat) {

               } else if (currentDataFormat instanceof ExponentialDataFormat) {

Log.errorln("Got Href Data Exponential:["+new String(data,bytes_added,bytes_added+bytes_to_add)+ "]["+bytes_added+"]["+bytes_to_add+"]");

               } else if (currentDataFormat instanceof BinaryFloatDataFormat) {

                  if (bytes_to_add == 4) { 
  
                     Float myValue = convert4bytesToFloat(endian, data, bytes_added);
Log.errorln("Got Href Data Float:["+myValue.toString()+"]["+bytes_added+"]["+bytes_to_add+"]");

                  } else if (bytes_to_add == 8) { 

                    Double myValue = convert8bytesToDouble(endian, data, bytes_added);
Log.errorln("Got Href Data Float:["+myValue.toString()+"]["+bytes_added+"]["+bytes_to_add+"]");

                  } else {
                     Log.errorln("Error: got floating point with bit size != (32|64). Ignoring data.");
                  }

               } else if (currentDataFormat instanceof BinaryIntegerDataFormat) {

                  Integer myValue = convert2bytesToInteger (endian, data, bytes_added);

Log.errorln("Got Href Data Integer:["+myValue.toString()+ "]["+bytes_added+"]["+bytes_to_add+"]");

               } else if (currentDataFormat instanceof StringDataFormat) {

// char[] charList = bytesTo8BitChars(data,bytes_added,bytes_added+bytes_to_add);

Log.errorln("String byte range is :"+bytes_added+" to "+(bytes_added+bytes_to_add));
Log.errorln("Got Href Data String:["+new String(data,bytes_added,(bytes_added+bytes_to_add))+ "]["+bytes_added+"]["+bytes_to_add+"]");

               }

               // advance our global pointer to the current DataFormat
               if (nrofDataFormat > 1)
                  if (CurrentDataFormatIndex == (nrofDataFormat - 1))
                     CurrentDataFormatIndex = 0;
                  else
                     CurrentDataFormatIndex++;

                bytes_added += bytes_to_add;

            } else if (currentIOCmd instanceof SkipCharFormattedIOCmd) {
               
                Integer bytes_to_skip = ((SkipCharFormattedIOCmd) currentIOCmd).getCount();
                bytes_added += bytes_to_skip.intValue();

            } else if (currentIOCmd instanceof RepeatFormattedIOCmd) {
               // shouldnt happen
               Log.errorln("Argh getCommands not working right, got repeat command in addByteData!!!");
               System.exit(-1);
            }

            // advance our global pointer to the current IOCmd
            if (nrofIOCmd> 1)
              if (CurrentIOCmdIndex == (nrofIOCmd - 1))
                 CurrentIOCmdIndex = 0;
              else
                 CurrentIOCmdIndex++;

        }

    }

    // for 16-bit Int
    private Integer convert2bytesToInteger (String endianStyle, byte[] bb, int sbyte) {
      
       int i;
       if(endianStyle.equals("BigEndian"))
           i = (bb[sbyte]&0xFF) << 8  | (bb[sbyte+1]&0xFF);
       else
           i = (bb[sbyte+1]&0xFF) << 8  | (bb[sbyte]&0xFF);

       return new Integer(i);

    }

    // for 32-bit Int
    private Integer convert4bytesToInteger (String endianStyle, byte[] bb, int sbyte) {

       int i;
       if(endianStyle.equals("BigEndian"))
           i = (bb[sbyte]&0xFF) << 24  | (bb[sbyte+1]&0xFF) << 16 | (bb[sbyte+2]&0xFF) << 8 | (bb[sbyte+3]&0xFF);
       else
           i = (bb[sbyte+3]&0xFF) << 24  | (bb[sbyte+2]&0xFF) << 16 | (bb[sbyte+1]&0xFF) << 8 | (bb[sbyte]&0xFF);

       return new Integer(i);

    }

    // for 32-bit Floating point
    private Float convert4bytesToFloat (String endianStyle, byte[] bb, int sbyte) {

       int i;
       if(endianStyle.equals("BigEndian")) 
          i = (bb[sbyte]&0xFF) << 24  | (bb[sbyte+1]&0xFF) << 16 | (bb[sbyte+2]&0xFF) << 8 | (bb[sbyte+3]&0xFF);
       else 
          i = (bb[sbyte+3]&0xFF) << 24  | (bb[sbyte+2]&0xFF) << 16 | (bb[sbyte+1]&0xFF) << 8 | (bb[sbyte]&0xFF);

/*
Log.error("Float bits are: ");
for (int j=sbyte; j<sbyte+4; j++) {
   for(int k=7; k >=0; k--) {
      int newvalue = (bb[j] >> k)&0x01;
      Log.error(""+newvalue);
   }
   Log.error(" ");
}
Log.errorln("");
*/

       return new Float(Float.intBitsToFloat(i));
    }

    // for 64-bit (Double) Floating point
    private Double convert8bytesToDouble (String endianStyle, byte[] bb, int sbyte) {
       int i1;
       int i2;

       if(endianStyle.equals("BigEndian")) 
       { 
          i1 =  (bb[sbyte]&0xFF) << 24 | (bb[sbyte+1]&0xFF) << 16 | (bb[sbyte+2]&0xFF) << 8 | (bb[sbyte+3]&0xFF);
          i2 =  (bb[sbyte+4]&0xFF) << 24 | (bb[sbyte+5]&0xFF) << 16 | (bb[sbyte+6]&0xFF) << 8 | (bb[sbyte+7]&0xFF);
       }
       else 
       {
          i2 =  (bb[sbyte+7]&0xFF) << 24 | (bb[sbyte+6]&0xFF) << 16 | (bb[sbyte+5]&0xFF) << 8 | (bb[sbyte+4]&0xFF);
          i1 =  (bb[sbyte+3]&0xFF) << 24 | (bb[sbyte+2]&0xFF) << 16 | (bb[sbyte+1]&0xFF) << 8 | (bb[sbyte]&0xFF);
       }

       return new Double(Double.longBitsToDouble( ((long) i1) << 32 | ((long)i2&0x00000000ffffffffL) ));

    }

    // yes, we dont treat anumber of things here (ex. short integers) but this is 
    // only a hack until we can do binary data 'right'. -b.t.
    private String convertBinaryDataToString (String endianStyle, 
                                              DataFormat binaryFormatObj, 
                                              String strDataRep ) 
    {

        byte[] bb = strDataRep.getBytes();

        if (binaryFormatObj instanceof BinaryIntegerDataFormat) {

            int i;

            if(((BinaryIntegerDataFormat) binaryFormatObj).numOfBytes() == 2) 
            {
               // 16 bit 
               if(endianStyle.equals("BigEndian")) 
                  i = (bb[0]&0xFF) << 8  | (bb[1]&0xFF);
               else 
                  i = (bb[1]&0xFF) << 8  | (bb[0]&0xFF);

/*
Log.error("integer bits are: ");
int sbyte = 0;
for (int j=sbyte; j<sbyte+2; j++) {
   for(int k=7; k >=0; k--) {
      int newvalue = (bb[j] >> k)&0x01;
      Log.error(""+newvalue);
   }
   Log.error(" ");
}
Log.errorln("");
*/

               strDataRep = new Integer(i).toString();

            } 
            else if(((BinaryIntegerDataFormat) binaryFormatObj).numOfBytes() == 4) 
            {
               // 32 bit (long) 
               if(endianStyle.equals("BigEndian")) 
                  i = (bb[0]&0xFF) << 24  | (bb[1]&0xFF) << 16 | (bb[2]&0xFF) << 8 | (bb[3]&0xFF);
               else 
                  i = (bb[3]&0xFF) << 24  | (bb[2]&0xFF) << 16 | (bb[1]&0xFF) << 8 | (bb[0]&0xFF);

               strDataRep = new Integer(i).toString();
            } 
            else 
            {
               Log.errorln("Cant treat binaryIntegers that arent either 16 or 32 bit. Ignoring value.");
            } 

        } else if (binaryFormatObj instanceof BinaryFloatDataFormat) {

            int i;

            if(((BinaryFloatDataFormat) binaryFormatObj).numOfBytes() == 4) 
            {
               // 32 bit float
               if(endianStyle.equals("BigEndian")) 
                  i = bb[0] << 24  | (bb[1]&0xFF) << 16 | (bb[2]&0xFF) << 8 | (bb[3]&0xFF);
               else
                  i = bb[3] << 24  | (bb[2]&0xFF) << 16 | (bb[1]&0xFF) << 8 | (bb[0]&0xFF);

               float myfloat = Float.intBitsToFloat(i);
               strDataRep = new Float(myfloat).toString();

            } 
            else if(((BinaryFloatDataFormat) binaryFormatObj).numOfBytes() == 8) 
            {
               // 64 bit float
               strDataRep = new String("");

            } 
            else 
            {
               Log.warnln("Got Floating point number with neither 32 or 64 bits, ignoring.");
               strDataRep = new String("");
            } 

        }

        return strDataRep;
    }

    // Placeholder to remind me to do some version checking w/ base class
    private boolean checkXDFDocVersion (String version)
    {
      // if(version != xdfVersion) { return false; } else { return true; }
      return false;
    }

    //
    private void initStartHandlerHashtable () {

       startElementHandlerHashtable.put(XDFNodeName.ARRAY, new arrayStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.AXIS, new axisStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.AXISUNITS, new nullStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.BINARYFLOAT, new binaryFloatFieldStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.BINARYINTEGER, new binaryIntegerFieldStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.DATA, new dataStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.DATAFORMAT, new dataFormatStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.EXPONENT, new exponentFieldStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.FIELD, new fieldStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.FIELDAXIS, new fieldAxisStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.FIELDRELATIONSHIP, new fieldRelationshipStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.FIXED, new fixedFieldStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.FORNODE, new forStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.FIELDGROUP, new fieldGroupStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.INDEX, new noteIndexStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.INTEGER, new integerFieldStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.LOCATIONORDER, new nullStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.NOTE, new noteStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.NOTES, new notesStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.PARAMETER, new parameterStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.PARAMETERGROUP, new parameterGroupStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.READ, new readStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.READCELL, new readCellStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.REPEAT, new repeatStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.ROOT, new rootStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.SKIPCHAR, new skipCharStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.STRING, new stringFieldStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.STRUCTURE, new structureStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.TAGTOAXIS, new tagToAxisStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.TD0, new dataTagStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.TD1, new dataTagStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.TD2, new dataTagStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.TD3, new dataTagStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.TD4, new dataTagStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.TD5, new dataTagStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.TD6, new dataTagStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.TD7, new dataTagStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.TD8, new dataTagStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.TEXTDELIMITER, new asciiDelimiterStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.UNIT, new unitStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.UNITS, new nullStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.UNITLESS, new nullStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.VALUE, new valueStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.VALUEGROUP, new valueGroupStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.VALUELIST, new valueListStartElementHandlerFunc());
       startElementHandlerHashtable.put(XDFNodeName.VECTOR, new vectorStartElementHandlerFunc());

    }

    // 
    private void initCharDataHandlerHashtable () {

       charDataHandlerHashtable.put(XDFNodeName.NOTE, new noteCharDataHandlerFunc());
       charDataHandlerHashtable.put(XDFNodeName.UNIT, new unitCharDataHandlerFunc());
       charDataHandlerHashtable.put(XDFNodeName.VALUE, new valueCharDataHandlerFunc());
       charDataHandlerHashtable.put(XDFNodeName.VALUELIST, new valueListCharDataHandlerFunc());

    }

    //
    private void initEndHandlerHashtable () {

       endElementHandlerHashtable.put(XDFNodeName.DATA, new dataEndElementHandlerFunc());
       endElementHandlerHashtable.put(XDFNodeName.FIELDGROUP, new fieldGroupEndElementHandlerFunc());
       endElementHandlerHashtable.put(XDFNodeName.NOTES, new notesEndElementHandlerFunc());
       endElementHandlerHashtable.put(XDFNodeName.PARAMETERGROUP, new parameterGroupEndElementHandlerFunc());
       endElementHandlerHashtable.put(XDFNodeName.READ, new readEndElementHandlerFunc());
       endElementHandlerHashtable.put(XDFNodeName.REPEAT, new repeatEndElementHandlerFunc());
       endElementHandlerHashtable.put(XDFNodeName.TD0, new dataTagEndElementHandlerFunc());
       endElementHandlerHashtable.put(XDFNodeName.TD1, new dataTagEndElementHandlerFunc());
       endElementHandlerHashtable.put(XDFNodeName.TD2, new dataTagEndElementHandlerFunc());
       endElementHandlerHashtable.put(XDFNodeName.TD3, new dataTagEndElementHandlerFunc());
       endElementHandlerHashtable.put(XDFNodeName.TD4, new dataTagEndElementHandlerFunc());
       endElementHandlerHashtable.put(XDFNodeName.TD5, new dataTagEndElementHandlerFunc());
       endElementHandlerHashtable.put(XDFNodeName.TD6, new dataTagEndElementHandlerFunc());
       endElementHandlerHashtable.put(XDFNodeName.TD7, new dataTagEndElementHandlerFunc());
       endElementHandlerHashtable.put(XDFNodeName.TD8, new dataTagEndElementHandlerFunc());
       endElementHandlerHashtable.put(XDFNodeName.VALUEGROUP, new valueGroupEndElementHandlerFunc());
       endElementHandlerHashtable.put(XDFNodeName.VALUELIST, new valueListEndElementHandlerFunc());

    }

   // To read in data appropriately to dataCube
   // we have to type it appropriately as determined from
   // the CurrentDataFormat object 

   // I just want to say upfront here that YES, we are doing a 
   // crappy job here. Probably desirable to not convert character data
   // into a String object ("thisString") then decode it again back into
   // a double, int, etc. LOADS of overhead, as you can imagine. 
   // We need better routine to directly convert from chars to numbers
   // and so forth..

    private void addDataToCurrentArray ( Locator dataLocator, 
                                         String thisString, 
                                         DataFormat CurrentDataFormat
                                       ) 
    {

// Log.error("addDatatoArray:["+thisString+"]");

       // Note that we dont treat binary data at all here 
       try {

           if ( CurrentDataFormat instanceof StringDataFormat) {
// Log.errorln(" StringDataFormat");
              CurrentArray.setData(dataLocator, thisString);
           } else if ( CurrentDataFormat instanceof FixedDataFormat
                       || CurrentDataFormat instanceof BinaryFloatDataFormat) 
           {
// Log.errorln(" FixedDataFormat");
              Double number = new Double (thisString);
              CurrentArray.setData(dataLocator, number.doubleValue());
           } else if ( CurrentDataFormat instanceof IntegerDataFormat
                       || CurrentDataFormat instanceof BinaryIntegerDataFormat) 
           {
// Log.errorln(" IntegerDataFormat");
              Integer number = new Integer (thisString);
              CurrentArray.setData(dataLocator, number.intValue());
           } else if ( CurrentDataFormat instanceof ExponentialDataFormat) {
// Log.errorln(" ExponentDataFormat");
              // hurm.. this is a stop-gap. Exponential format needs to be
              // preserved better than this, perhaps??. -b.t. 
              Double number = new Double (thisString);
              CurrentArray.setData(dataLocator, number.doubleValue());
           } else {
              Log.warnln("Unknown data format, unable to setData:["+thisString+"], ignoring request");
           }

       } catch (SetDataException e) {
           // bizarre error. Cant add data (out of memory??) :P
           Log.errorln("Unable to setData:["+thisString+"], ignoring request");
           Log.printStackTrace(e);
       }
    }

    // *sigh* lack of regular expression support makes this 
    // more difficult to do. I expect that it will be possible to 
    // break this in various ways if the PCDATA in the XML 
    // document is off. 
    //
    // We should investigate alternative implimentations here, perhaps
    // with StringBuffer, StringReader or StringTkenizer may help
    // performance. -b.t. 
    //
    // Note: right now repeatable is limited to just preventing repeating
    // on delimiting strings, NOT on recordTerminators. Not sure if this
    // behavior is consistent with that in the Perl. -b.t.
    //
    private ArrayList splitStringIntoStringObjects ( String valueListString, 
                                                     String delimiter,
                                                     String repeatable, 
                                                     String terminatingDelimiter
                                                   )
    {

       // the list we will return
       ArrayList values = new ArrayList();

       // parameters 
       int delimiterSize = delimiter.length();
       char delimitChar0 = delimiter.charAt(0);
       int termDelimiterSize = 0;
       char termDelimitChar0;
       int valueListSize = valueListString.length(); 
       boolean isNotRepeatable = repeatable.equals("yes") ? false : true;

       if(terminatingDelimiter != null) {
          termDelimiterSize = terminatingDelimiter.length();
          termDelimitChar0 = terminatingDelimiter.charAt(0);
       }

       // loop over the valueListString and derive values 
       int start = 0;
       while ( start < valueListSize )
       {

          int stop = start + delimiterSize;

          // safety, can happen
          if(stop > valueListSize) stop = valueListSize;

          if ( valueListString.substring(start, stop).compareTo(delimiter) == 0 ) 
          {

            // we hit a delimiter
            start += delimiterSize;

            if (isNotRepeatable) { 
                // gather a value now.

// Log.errorln("ISNOTREPEATABLE CHECK");

                // find the end of this substring
                int end = valueListString.indexOf(delimitChar0, start);

                int termend = 0;

                if(terminatingDelimiter != null)
                   termend = valueListString.indexOf(terminatingDelimiter.charAt(0), start);

// Log.error("end:"+end+" termend:"+termend);

                String valueString;
                if(termend == 0 || end < termend) {

                   // can happen if no terminating delimiter
                   if (end < 0) end = valueListSize;

                   // derive our value from string
                   valueString = valueListString.substring(start, end);

                   // add the value to arrayList 
                   values.add(valueString);

// Log.errorln(" DValue:"+valueString);

                   // this is the last value so terminate the while loop 
                   if ((end+delimiterSize) >= valueListSize ) break;

                   start = end;

                } else {

                   // if (termend < 0) termend = valueListSize;

                   valueString = valueListString.substring(start, termend);

                   // add the value to arrayList 
                   values.add(valueString);

// Log.errorln(" TValue:"+valueString);

                   // this is the last value so terminate the while loop 
                   if ((termend+termDelimiterSize) >= valueListSize ) break;

                   start = termend;
                }

             }

          } else if (termDelimiterSize > 0 
                       && valueListString.substring(start, start + termDelimiterSize).compareTo(terminatingDelimiter) == 0
                    ) 
          { 

             // we hit record terminator(delimiter) 
             start += termDelimiterSize;
           
             // we DONT repeat on record terminators (??)
/*
             if (isNotRepeatable) { 
                // gather values now.

Log.errorln("ISNOTREPEATABLE CHECK");

                // find the end of this substring
                int end = valueListString.indexOf(delimitChar0, start);

                int termend = 0;

                if(terminatingDelimiter != null)
                   termend = valueListString.indexOf(terminatingDelimiter.charAt(0), start);

Log.error("end:"+end+" termend:"+termend);

                String valueString;
                if(termend == 0 || end < termend) {

                   // can happen if no terminating delimiter
                   if (end < 0) end = valueListSize;

                   // derive our value from string
                   valueString = valueListString.substring(start, end);

                   // add the value to arrayList 
                   values.add(valueString);

Log.errorln(" DValue:"+valueString);

                   // this is the last value so terminate the while loop 
                   if ((end+delimiterSize) >= valueListSize ) break;

                   start = end;

                } else {

                   // if (termend < 0) termend = valueListSize;

                   valueString = valueListString.substring(start, termend);

                   // add the value to arrayList 
                   values.add(valueString);

Log.errorln(" TValue:"+valueString);

                   // this is the last value so terminate the while loop 
                   if ((termend+termDelimiterSize) >= valueListSize ) break;

                   start = termend;
                }
*/

          } else {

             // we didnt hit a delimiter, gather values

             // find the end of this substring
             int end = valueListString.indexOf(delimitChar0, start);

             int termend = 0;

             if(terminatingDelimiter != null) 
                 termend = valueListString.indexOf(terminatingDelimiter.charAt(0), start);

// Log.error("end:"+end+" termend:"+termend);

             String valueString;
             if(termend == 0 || end < termend) {

                // can happen if no terminating delimiter
                if (end < 0) end = valueListSize;

                // derive our value from string
                valueString = valueListString.substring(start, end);

                // add the value to arrayList 
                values.add(valueString);

// Log.errorln(" DValue:"+valueString);

                // this is the last value so terminate the while loop 
                if ((end+delimiterSize) >= valueListSize ) break;

                start = end;

             } else {

                // if (termend < 0) termend = valueListSize;

                valueString = valueListString.substring(start, termend);

                // add the value to arrayList 
                values.add(valueString);

// Log.errorln(" TValue:"+valueString);

                // this is the last value so terminate the while loop 
                if ((termend+termDelimiterSize) >= valueListSize ) break;

                start = termend;
             }

          }

       }

       return values;
    }

    // Not the best implementation. Should probably be done 
    // with character arrays. -b.t. 
    private ArrayList formattedSplitStringIntoStringObjects ( String data , 
                                                              FormattedXMLDataIOStyle readObj
                                                            ) 
    {


        ArrayList stringObjList = new ArrayList();
        List commandList = readObj.getCommands();
        // DataFormat dataFormat[] = CurrentArray.getDataFormatList();

        String endian = readObj.getEndian();
        int dataPosition = 0;
        int dataLength = data.length();
        int currentDataFormat = 0;
        int nrofDataFormat = DataFormatList.length;

        // Log.debugln("in formattedSplitString, data :["+data+"]");

        // the extraction loop
        // whip thru the data string, either ignoring or accepting
        // characters in the string as directed by the formatCmdList
        while (dataPosition < dataLength) {

          Iterator formatIter = commandList.iterator();
          while (formatIter.hasNext()) {
             FormattedIOCmd thisCommand = (FormattedIOCmd) formatIter.next();
             if (thisCommand instanceof ReadCellFormattedIOCmd) // ReadCell ==> read some data
             {

                 DataFormat formatObj = DataFormatList[currentDataFormat];
                 int endDataPosition = dataPosition + formatObj.numOfBytes();

                 // add in our object
                 if(endDataPosition > dataLength) { 
                    Log.errorln("Format specification exceeded data width, Bad format?");
                    Log.errorln("Run in debug mode to examine formatted read. Aborting.");
                    System.exit(-1); // throw IOException;
                 }

                 String thisData = data.substring(dataPosition,endDataPosition);

                 // convert binaryData to correct string representation
                 if(formatObj instanceof BinaryIntegerDataFormat 
                     || formatObj instanceof BinaryFloatDataFormat) 
                    thisData = convertBinaryDataToString(endian, formatObj, thisData);

                 // remove leading whitespace from what will be non-string data.
                 if (Character.isWhitespace(thisData.charAt(0)) && 
                      !(formatObj instanceof StringDataFormat)) 
                    thisData = trimLeadingWhitespace(thisData);

                 Log.debugln("Got Formatted DataCell:["+thisData+"]");

                 stringObjList.add(thisData);

                 dataPosition = endDataPosition;
         
                 // advance our pointer to the current DataFormat
                 if (nrofDataFormat > 1) 
                    if (currentDataFormat == (nrofDataFormat - 1)) 
                       currentDataFormat = 0;
                    else 
                       currentDataFormat++;

             } else if (thisCommand instanceof SkipCharFormattedIOCmd) // SkipChar ==> just adv. dataPointer 
             {
                dataPosition += ((SkipCharFormattedIOCmd) thisCommand).getCount().intValue();
             } else {
                Log.errorln("Unknown FormattedIOCmd, aborting formatted read."); // needed check ? 
                break;
             }
          }

        }

        return stringObjList;
    }

    // take off only the leading whitespace from a string.
    // we assume here that we never get all-whitespace data ever,
    // which might lead to problems. -b.t.
    private String trimLeadingWhitespace (String string) {
        int lastWhiteSpacePos = 0;
        while (Character.isWhitespace(string.charAt(lastWhiteSpacePos)))
           lastWhiteSpacePos++;
        return string.substring(lastWhiteSpacePos,string.length());

    }

    // This will get used heavily during data adding. Implimentation
    // is kinda slow too. bleh.
    // isnt there some free code around to do this?
    private boolean stringIsNotAllWhitespace (String thisString) {

       if(thisString.trim().length() == 0) 
           return false;
       return true;
    } 

    private Hashtable attribListToHashtable ( AttributeList attrs ) {

       Hashtable hash = new Hashtable();
       int size = attrs.getLength();
       for (int i = 0; i < size; i++) {
          String name = attrs.getName(i);
          String value; 
          if ((value = attrs.getValue(i)) != null) 
             hash.put(name, value);
       }
         
       return hash;
    }

    // For the case where valueList is storing values in 
    // algorithmic fashion
    private ArrayList getValueListNodeValues () {

       ArrayList values = new ArrayList();

       // parameters for the algorithm
       int size  = Integer.valueOf((String) CurrentValueListParameter.get("size")).intValue();
       int start = Integer.valueOf((String) CurrentValueListParameter.get("start")).intValue();
       int step  = Integer.valueOf((String) CurrentValueListParameter.get("step")).intValue();

       // do the algorithm to populate the values in the ArrayList 
       int numberOfValues = 0;
       int currentValue = start;
       while (numberOfValues < size) 
       {
          values.add(String.valueOf(currentValue));
          currentValue += step;
          numberOfValues++;
       }
       
       return values;
    }
          
    //
    // Internal Classes
    //

    /*
       Now, Some defines based on XDF DTD.
       Change these as needed to reflect new namings of same nodes as they occur.
    */
    public static class XDFNodeName 
    {
       // *sigh* cant decide if making this hashtable is better or not.
       public static final String ARRAY = "array";
       public static final String AXIS = "axis";
       public static final String AXISUNITS= "axisUnits";
       public static final String BINARYFLOAT = "binaryFloat";
       public static final String BINARYINTEGER = "binaryInteger";
       public static final String DATA = "data";
       public static final String DATAFORMAT = "dataFormat";
       public static final String EXPONENT = "exponential";
       public static final String FIELD = "field";
       public static final String FIELDAXIS = "fieldAxis";
       public static final String FIELDRELATIONSHIP = "relation";
       public static final String FIXED = "fixed";
       public static final String FORNODE = "for";
       public static final String FIELDGROUP = "fieldGroup";
       public static final String INDEX = "index";
       public static final String INTEGER = "integer";
       public static final String LOCATIONORDER = "locationOrder";
       public static final String NOTE = "note";
       public static final String NOTES = "notes";
       public static final String PARAMETER = "parameter";
       public static final String PARAMETERGROUP = "parameterGroup";
       public static final String ROOT = "XDF"; // beware setting this to the same name as structure 
       public static final String READ = "read";
       public static final String READCELL = "readCell";
       public static final String REPEAT = "repeat";
       public static final String SKIPCHAR = "skipChars";
       public static final String STRUCTURE = "structure";
       public static final String STRING = "string";
       public static final String TAGTOAXIS = "tagToAxis";
       public static final String TD0 = "d0";
       public static final String TD1 = "d1";
       public static final String TD2 = "d2";
       public static final String TD3 = "d3";
       public static final String TD4 = "d4";
       public static final String TD5 = "d5";
       public static final String TD6 = "d6";
       public static final String TD7 = "d7";
       public static final String TD8 = "d8";
       public static final String TEXTDELIMITER = "textDelimiter";
       public static final String UNIT = "unit";
       public static final String UNITS = "units";
       public static final String UNITLESS = "unitless";
       public static final String VALUELIST = "valueList";
       public static final String VALUE = "value";
       public static final String VALUEGROUP = "valueGroup";
       public static final String VECTOR = "unitDirection";

    } // End of XDFNodeName class 

    // 
    // Dispatch Table Handlers 
    //
  
    // These classes are here because they are used by the SaxDocumentHandler
    // dispatch tables. See interface files for more info.

    // ASCII DELIMITER NODE HANDLERS
    //

    // asciiDelimiter node start
    class asciiDelimiterStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

           DelimitedXMLDataIOStyle readObj = new DelimitedXMLDataIOStyle(CurrentArray);
           readObj.setXMLAttributes(attrs);
           CurrentArray.setXMLDataIOStyle(readObj);

         // is this needed??
         //  CurrentFormatObjectList.add(readObj);

           return readObj;

       }
    }

    // asciiDelimiter node end
    class asciiDelimiterEndElementHandlerFunc implements EndElementHandlerAction {
       public void action (SaxDocumentHandler handler) { 

           // pop off last value
           // CurrentFormatObjectList.remove(CurrentFormatObjectList.size()-1);

       }
    }


    // ARRAY NODE
    //

    // Array node start 
    class arrayStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) { 

          // create new object appropriately 
          Array newarray = new Array();
          newarray.setXMLAttributes(attrs); // set XML attributes from passed list 

          // set current array and add this array to current structure 
          CurrentArray = CurrentStructure.addArray(newarray);

          setCurrentDatatypeObject(CurrentArray);

          return newarray;
       }
    } 

    // AXIS NODE 
    //

    class axisStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) { 

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
                 if (AxisObj.containsKey(axisId)) 
                    Log.warnln("More than one axis node with axisId=\""+axisId+"\", using latest node." ); 

                 // add this into the list of axis objects
                 AxisObj.put(axisId, newaxis);

             }

             //  If there is a reference object, clone it to get
             //  the new axis
             if (axisIdRef != null) {

                 if (AxisObj.containsKey(axisIdRef)) {

                    BaseObject refAxisObj = (BaseObject) AxisObj.get(axisIdRef);
                    try {
                       newaxis = (Axis) refAxisObj.clone();
                    } catch (java.lang.CloneNotSupportedException e) {
                       Log.errorln("Weird error, cannot clone axis object. Aborting read.");
                       System.exit(-1);
                    }

                    // override attrs with those in passed list
                    newaxis.setXMLAttributes(attrs);
                    // give the clone a unique Id and remove IdRef 
                    newaxis.setAxisId(findUniqueIdName(AxisObj,newaxis.getAxisId())); 
                    newaxis.setAxisIdRef(null);

                    // add this into the list of axis objects
                    AxisObj.put(newaxis.getAxisId(), newaxis);
           
                 } else {
                    Log.warnln("Error: Reader got an axis with AxisIdRef=\""+axisIdRef+"\" but no previous axis has that id! Ignoring add axis request.");
                    return (Object) null;
                 }
             }

             // add this axis to the current array object
             CurrentArray.addAxis(newaxis);

             // I dont believe this is actually used
             // CurrentDatatypeObject = newaxis;

          } else {
             Log.errorln("Axis object:"+newaxis+" lacks either axisId or axisIdRef, ignoring!");
          }

          return newaxis;

       }

    }

    // BinaryFloatField 
    //

    class binaryFloatFieldStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) { 

        // create the object
          BinaryFloatDataFormat bfFormat = new BinaryFloatDataFormat();
          bfFormat.setXMLAttributes(attrs);
          bfFormat.setXMLAttributes(DataFormatAttribs); // probably arent any, but who knows.. 

          if (CurrentDatatypeObject instanceof Field) {
              ((Field) CurrentDatatypeObject).setDataFormat(bfFormat);
          } else if (CurrentDatatypeObject instanceof Array) {
              ((Array) CurrentDatatypeObject).setDataFormat(bfFormat);
          } else {
              Log.warnln("Unknown parent object, cant set data type/format in dataTypeObj, ignoring.");
          }

          return bfFormat;

       }
    }


    // BINARYINTEGERFIELD
    //

    class binaryIntegerFieldStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) { 

         // create the object
          BinaryIntegerDataFormat biFormat = new BinaryIntegerDataFormat();
          biFormat.setXMLAttributes(attrs);
          biFormat.setXMLAttributes(DataFormatAttribs); // probably arent any, but who knows.. 

          if (CurrentDatatypeObject instanceof Field) {
              ((Field) CurrentDatatypeObject).setDataFormat(biFormat);
          } else if (CurrentDatatypeObject instanceof Array) {
              ((Array) CurrentDatatypeObject).setDataFormat(biFormat);
          } else {
              Log.warnln("Unknown parent object, cant set data type/format in dataTypeObj, ignoring.");
          }

          return biFormat;

       }
    }


    // DATATAG
    //

    // REMINDER: these functions only get called when tagged data is being read..

    class dataTagStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) { 
          CurrentDataTagLevel++;
          return (Object) null;
       }
    }

    class dataTagEndElementHandlerFunc implements EndElementHandlerAction {
       public void action (SaxDocumentHandler handler) { 

          if (CurrentDataTagLevel == DataTagLevel)
             TaggedLocatorObj.next();

          // bump up DataFormat appropriately
          if (MaxDataFormatIndex > 0) { 
             int currentFastAxisCoordinate = TaggedLocatorObj.getAxisLocation(FastestAxis);
             if ( currentFastAxisCoordinate != LastFastAxisCoordinate ) 
             { 
                LastFastAxisCoordinate = currentFastAxisCoordinate;
                if (CurrentDataFormatIndex == MaxDataFormatIndex)
                   CurrentDataFormatIndex = 0;
                else
                   CurrentDataFormatIndex++;
             }
          }

          CurrentDataTagLevel--;

       }
    }

    // DATA
    //

    class dataCharDataHandlerFunc implements CharDataHandlerAction {
       public void action (SaxDocumentHandler handler, char buf [], int offset, int len) {

          XMLDataIOStyle readObj = CurrentArray.getXMLDataIOStyle();
          String thisString = new String(buf,offset,len);

          if ( readObj instanceof TaggedXMLDataIOStyle ) {

             // dont add this data unless it has more than just whitespace
             if (!IgnoreWhitespaceOnlyData || stringIsNotAllWhitespace(thisString) ) {

                Log.debugln("ADDING TAGGED DATA to ("+TaggedLocatorObj+") : ["+thisString+"]");

                DataTagLevel = CurrentDataTagLevel;

                DataFormat CurrentDataFormat = DataFormatList[CurrentDataFormatIndex];

                // adding data based on what type..
                addDataToCurrentArray(TaggedLocatorObj, thisString, CurrentDataFormat); 

             }

          } else if ( readObj instanceof DelimitedXMLDataIOStyle ||
                    readObj instanceof FormattedXMLDataIOStyle )
          {

              // add it to the datablock if it isnt all whitespace ?? 
              if (!IgnoreWhitespaceOnlyData || stringIsNotAllWhitespace(thisString) ) 
                  DATABLOCK.append(thisString);

           } else {
               Log.errorln("UNSUPPORTED Data Node CharData style:"+readObj.toString()+", Aborting!\n");
               System.exit(-1);
           }

       }
    }

    class dataEndElementHandlerFunc implements EndElementHandlerAction {
       public void action (SaxDocumentHandler handler) { 

          // we stopped reading datanode, lower count by one
          DataNodeLevel--;

          //  we might still be nested within a data node
          // if so, return now to accumulate more data within the DATABLOCK
          if(DataNodeLevel != 0)
             return;

          // now we are ready to read in untagged data (both delimited/formmatted styles) 
          // from the DATABLOCK

          // Note: unfortunately we are reduced to using regex style matching
          // instead of a buffer read in formatted reads. Come back and
          // improve this later if possible.

          XMLDataIOStyle formatObj = CurrentArray.getXMLDataIOStyle();

          if ( formatObj instanceof DelimitedXMLDataIOStyle ||
               formatObj instanceof FormattedXMLDataIOStyle ) 
          {


              // determine the size of the dataFormat (s) in our dataCube
              if (CurrentArray.hasFieldAxis()) {
                 // if there is a field axis, then its set to the number of fields
                 FieldAxis fieldAxis = CurrentArray.getFieldAxis();
                 MaxDataFormatIndex = (fieldAxis.getLength()-1);
              } else {
                 // its homogeneous 
                 MaxDataFormatIndex = 0;
              }

              Locator myLocator = CurrentArray.createLocator();
              myLocator.setIterationOrder(AxisReadOrder);


              // CurrentIOCmdIndex = 0; 
              CurrentDataFormatIndex = 0; 
              ArrayList strValueList;

//              boolean dataHasSpecialIntegers = false;

              // set up appropriate instructions for reading
              if ( formatObj instanceof FormattedXMLDataIOStyle ) {
/*
      $template  = $formatObj->_templateNotation(1);
      $recordSize = $formatObj->bytes();
      $data_has_special_integers = $formatObj->hasSpecialIntegers;
*/

                 // snag the string representation of the values
                 strValueList = formattedSplitStringIntoStringObjects( DATABLOCK.toString(), 
                                                                        ((FormattedXMLDataIOStyle) formatObj)
                                                                                );
                 if (strValueList.size() == 0) {
                    Log.errorln("Error: XDF Reader is unable to acquire formatted data, bad format?");
                    System.exit(-1);
                 }

              } else {

                 // snag the string representation of the values
                 strValueList = splitStringIntoStringObjects( DATABLOCK.toString(), 
                                                ((DelimitedXMLDataIOStyle) formatObj).getDelimiter(), 
                                                ((DelimitedXMLDataIOStyle) formatObj).getRepeatable(), 
                                                ((DelimitedXMLDataIOStyle) formatObj).getRecordTerminator()
                                              );
              }

              // fire data into dataCube
              Iterator iter = strValueList.iterator();
              while (iter.hasNext()) 
              {

                 DataFormat CurrentDataFormat = DataFormatList[CurrentDataFormatIndex];

                 // adding data based on what type..
                 String thisData = (String) iter.next();
                 addDataToCurrentArray(myLocator, thisData, CurrentDataFormat);

                 // bump up DataFormat appropriately
                 if (MaxDataFormatIndex > 0) {
                    int currentFastAxisCoordinate = myLocator.getAxisLocation(FastestAxis);
                    if ( currentFastAxisCoordinate != LastFastAxisCoordinate )
                    {
                       LastFastAxisCoordinate = currentFastAxisCoordinate;
                       if (CurrentDataFormatIndex == MaxDataFormatIndex)
                          CurrentDataFormatIndex = 0;
                       else
                          CurrentDataFormatIndex++;
                    }
                 }

                 myLocator.next();

              }


          } else if ( formatObj instanceof TaggedXMLDataIOStyle )
          {

              // Tagged case: do nothing

          } else {

              Log.errorln("ERROR: Completely unknown DATA IO style:"+formatObj.toString()
                           +" aborting read!");
              System.exit(-1);

          }

       }
    }

    class dataStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) { 

          // we only need to do these things for the first time we enter
          // a data node
          if (DataNodeLevel == 0) {

             // A little 'pre-handling' as href is a specialattribute
             // that will hold an (Href) object rather than string value 
             Href hrefObj = null;
             String hrefValue = getAttributeListValueByName(attrs,"href");
             if (hrefValue != null ) 
             {

                // now we look up the href from the entity list gathered by
                // the parser and transfer relevant info to our Href object 
                hrefObj = new Href();

                Hashtable hrefInfo = (Hashtable) UnParsedEntity.get(hrefValue);

                if (UnParsedEntity.containsKey(hrefValue)) 
                {
                   hrefObj.setName((String) hrefInfo.get("name"));
                   if (hrefInfo.containsKey("base")) 
                      hrefObj.setBase((String) hrefInfo.get("base"));
                   if (hrefInfo.containsKey("sysId")) 
                      hrefObj.setSysId((String) hrefInfo.get("sysId"));
                   if (hrefInfo.containsKey("pubId")) 
                      hrefObj.setPubId((String) hrefInfo.get("pubId"));
                   if (hrefInfo.containsKey("ndata")) 
                      hrefObj.setNdata((String) hrefInfo.get("ndata"));
                } else {
                   // bizarre. It usually means that the unparsed entity handler
                   // isnt working like it should
                   Log.error("Error: UnParsedEntity list lacks entry for :"+hrefValue);
                   Log.errorln(" ignoring request to read data.");
                }

             }

             // update the array dataCube with passed attributes
             CurrentArray.getDataCube().setXMLAttributes(attrs);

             // Clean up. We override the string value of Href and set it as
             // the Href object , if we created it (yeh, sloppy). 
             if (hrefObj != null)
                 CurrentArray.getDataCube().setHref(hrefObj);

             // determine the size of the dataFormat (s) in our dataCube
             if (CurrentArray.hasFieldAxis()) {
                 // if there is a field axis, then its set to the number of fields
                 FieldAxis fieldAxis = CurrentArray.getFieldAxis();
                 MaxDataFormatIndex = (fieldAxis.getLength()-1);
             } else {
                 // its homogeneous 
                 MaxDataFormatIndex = 0;
             }
                  
             // reset to start of which dataformat type we currently are reading
             CurrentDataFormatIndex = 0;    
             // reset to start of which IOCmd we currently are reading
             CurrentIOCmdIndex = 0;    

             // reset the list of dataformats we are reading
             DataFormatList = CurrentArray.getDataFormatList();

          }

          XMLDataIOStyle readObj = CurrentArray.getXMLDataIOStyle();
          FastestAxis = (AxisInterface) CurrentArray.getAxisList().get(0);
          LastFastAxisCoordinate = -1;

          if ( readObj instanceof TaggedXMLDataIOStyle) {
             TaggedLocatorObj = CurrentArray.createLocator();
          } else {
             // A safety. We clear datablock when this is the first datanode we 
             // have entered DATABLOCK is used in cases where we read in untagged data
             if (DataNodeLevel == 0) DATABLOCK = new StringBuffer ();
          }

          // tack in href data 
          if (CurrentArray.getDataCube().getHref() != null) 
              DATABLOCK.append(getHrefData(CurrentArray.getDataCube().getHref()));
              //loadHrefDataIntoCurrentArray();

          // entered a datanode, raise the count 
          // this (partially helps) declare we are now reading data, 
          DataNodeLevel++; 

          return readObj;
       }
    }

    // DATAFORMAT 
    //

    class dataFormatStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) { 

           // save attribs for latter
           DataFormatAttribs = attrs;

           return (Object) null;
       }
    }


    // EXPONENTFIELD
    //

    class exponentFieldStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

         // create the object
          ExponentialDataFormat exponentFormat = new ExponentialDataFormat();
          exponentFormat.setXMLAttributes(attrs);
          exponentFormat.setXMLAttributes(DataFormatAttribs); // probably arent any, but who knows.. 

          if (CurrentDatatypeObject instanceof Field) {
              ((Field) CurrentDatatypeObject).setDataFormat(exponentFormat);
          } else if (CurrentDatatypeObject instanceof Array) { 
              ((Array) CurrentDatatypeObject).setDataFormat(exponentFormat);
          } else {
              Log.warnln("Unknown parent object, cant set data type/format in dataTypeObj, ignoring.");
          }

          return exponentFormat;
       }
    }

    // FIELD
    //

    class fieldStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

          // create new object appropriately 
          Field newfield = new Field();
          newfield.setXMLAttributes(attrs); // set XML attributes from passed list

          // grab the field axis and add the field 
          FieldAxis fieldAxis = CurrentArray.getFieldAxis();
          fieldAxis.addField(newfield);

          String fieldId = newfield.getFieldId();
          String fieldIdRef = newfield.getFieldIdRef();

          // add this object to the lookup table, if it has an ID
          if (fieldId != null) {

             // a warning check, just in case
             if (FieldObj.containsKey(fieldId))
                    Log.warnln("More than one field node with fieldId=\""+fieldId+"\", using latest node." );

             // add this into the list of field objects
             FieldObj.put(fieldId, newfield);

          }

          //  If there is a reference object, clone it to get
          //  the new field
          if (fieldIdRef != null) {

             if (FieldObj.containsKey(fieldIdRef)) {

                BaseObject refFieldObj = (BaseObject) FieldObj.get(fieldIdRef);
                try 
                {
                    newfield = (Field) refFieldObj.clone();
                } catch (java.lang.CloneNotSupportedException e) {
                    Log.errorln("Weird error, cannot clone field object. Aborting read.");
                    System.exit(-1);
                }

                // override attrs with those in passed list
                newfield.setXMLAttributes(attrs);

                // give the clone a unique Id and remove IdRef 
                newfield.setFieldId(findUniqueIdName(FieldObj, newfield.getFieldId())); 
                newfield.setFieldIdRef(null);

                // add this into the list of field objects
                FieldObj.put(newfield.getFieldId(), newfield);


             } else {
                Log.warnln("Error: Reader got an field with FieldIdRef=\""+fieldIdRef+"\" but no previous field has that id! Ignoring add field request.");
                    return (Object) null;
             }
          }


          // add this object to all open field groups
          Iterator iter = CurrentFieldGroupList.iterator();
          while (iter.hasNext()) {
             FieldGroup nextFieldGroupObj = (FieldGroup) iter.next();
             newfield.addToGroup(nextFieldGroupObj);
          }

          CurrentDatatypeObject = newfield;

          LastFieldObject = newfield;

          return newfield;
       }
    }

    // FIELDAXIS 
    //

    class fieldAxisStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

          // create new object appropriately 
          FieldAxis newfieldaxis = new FieldAxis();
          newfieldaxis.setXMLAttributes(attrs); // set XML attributes from passed list 

          // Every axis must have *either* axisId *or* an axisIdRef 
          // else, its illegal!
          String axisId = null;
          String axisIdRef = null;
          if ( (axisId = newfieldaxis.getAxisId()) != null
                || (axisIdRef = newfieldaxis.getAxisIdRef()) != null
             )
          {

             // add this object to the lookup table, if it has an ID
             if (axisId != null) {

                 // a warning check, just in case 
                 if (AxisObj.containsKey(axisId))
                    Log.warnln("More than one axis node with axisId=\""+axisId+"\", using latest node." );

                 // add this into the list of axis objects
                 AxisObj.put(axisId, newfieldaxis);

             }

             //  If there is a reference object, clone it to get
             //  the new axis
             if (axisIdRef != null) {

                 if (AxisObj.containsKey(axisIdRef)) {

                    BaseObject refAxisObj = (BaseObject) AxisObj.get(axisIdRef);
                    try {
                      newfieldaxis = (FieldAxis) refAxisObj.clone();
                    } catch (java.lang.CloneNotSupportedException e) {
                      Log.errorln("Weird error, cannot clone field object. Aborting read.");
                      System.exit(-1);
                    }

                    // override attrs with those in passed list
                    newfieldaxis.setXMLAttributes(attrs);

                    // give the clone a unique Id and remove IdRef 
                    newfieldaxis.setAxisId(findUniqueIdName(AxisObj, newfieldaxis.getAxisId())); 
                    newfieldaxis.setAxisIdRef(null);

                    // add this into the list of axis objects
                    AxisObj.put(newfieldaxis.getAxisId(), newfieldaxis);

                 } else {
                    Log.warnln("Error: Reader got an fieldaxis with AxisIdRef=\""+axisIdRef+"\" but no previous field axis has that id! Ignoring add fieldAxis request.");
                    return (Object) null;
                 }
             }

             // add this axis to the current array object
             CurrentArray.addFieldAxis(newfieldaxis);

          } else {
             Log.errorln("FieldAxis object:"+newfieldaxis+" lacks either axisId or axisIdRef, ignoring!");
          }

          return newfieldaxis;
       }
    }


    // FIELDGROUP 
    //

    class fieldGroupEndElementHandlerFunc implements EndElementHandlerAction {
       public void action (SaxDocumentHandler handler) {
          // peel off the last object in the field group list
          CurrentFieldGroupList.remove(CurrentFieldGroupList.size()-1);
       }
    }

    class fieldGroupStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

          // grab parent node name
          String parentNodeName = getParentNodeName();

          // create new object appropriately 
          FieldGroup newfieldGroup = new FieldGroup();
          newfieldGroup.setXMLAttributes(attrs); // set XML attributes from passed list 

          // determine where this goes and then insert it 
          if( parentNodeName.equals(XDFNodeName.FIELDAXIS) )
          {

              newfieldGroup = CurrentArray.getFieldAxis().addFieldGroup(newfieldGroup);
              LastFieldGroupParentObject = (Object) CurrentArray;

          } else if ( parentNodeName.equals(XDFNodeName.FIELDGROUP) )

          {

              FieldGroup LastFieldGroupObject = (FieldGroup)
                   CurrentFieldGroupList.get(CurrentFieldGroupList.size()-1);
              newfieldGroup = LastFieldGroupObject.addFieldGroup(newfieldGroup);

          } else {

              Log.errorln(" weird parent node $parent_node_name for fieldGroup");
              System.exit(-1); // dump core :)

          }

          // add this object to all open groups
          Iterator iter = CurrentFieldGroupList.iterator();
          while (iter.hasNext()) {
             FieldGroup nextFieldGroupObj = (FieldGroup) iter.next();
             newfieldGroup.addToGroup(nextFieldGroupObj);
          }

          // now add it to the list
          CurrentFieldGroupList.add(newfieldGroup);

          return newfieldGroup;
       }
    }

    // FIELDRELATIONSHIP 
    //

    class fieldRelationshipStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

          // create the object
          FieldRelationship newfieldrelation = new FieldRelationship();
          newfieldrelation.setXMLAttributes(attrs);

          // add in reference object if it exists
          String fieldIdRefs = newfieldrelation.getFieldIdRefs();
          if (fieldIdRefs != null) {
             // not clear what to do here. Leave blank for now
          }
           
          // add this relationship in the field object
          LastFieldObject.setRelationship(newfieldrelation);

          return newfieldrelation;
       }
    }

    // FIXEDFIELD
    //

    class fixedFieldStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

          // create the object
          FixedDataFormat fixedFormat = new FixedDataFormat();
          fixedFormat.setXMLAttributes(attrs);
          fixedFormat.setXMLAttributes(DataFormatAttribs);

          if (CurrentDatatypeObject instanceof Field) { 
              ((Field) CurrentDatatypeObject).setDataFormat(fixedFormat);
          } else if (CurrentDatatypeObject instanceof Array) { 
              ((Array) CurrentDatatypeObject).setDataFormat(fixedFormat);
          } else {
              Log.warnln("Unknown parent object, cant set string data type/format in $dataTypeObj, ignoring.");
          }

          return fixedFormat;
       }
    }

    // FOR
    //

    class forStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

          // for node sets the iteration order for how we will setData
          // in the datacube (important for delimited and formatted reads).
      
          int size = attrs.getLength();
          for (int i = 0; i < size; i++)
          {
             String name = attrs.getName(i);
             if (name.equals("axisIdRef") ) {
                AxisReadOrder.add(AxisObj.get(attrs.getValue(i)));
             } else 
                 Log.warnln("Warning: got weird attribute:"+name+" on for node");
          } 

          return (Object) null;
       }
    }

    // INTEGERFIELD
    //

    class integerFieldStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

         // create the object
          IntegerDataFormat integerFormat = new IntegerDataFormat();
          integerFormat.setXMLAttributes(attrs);
          integerFormat.setXMLAttributes(DataFormatAttribs); // probably arent any, but who knows.. 

          if (CurrentDatatypeObject instanceof Field) {
              ((Field) CurrentDatatypeObject).setDataFormat(integerFormat);
          } else if (CurrentDatatypeObject instanceof Array) {
              ((Array) CurrentDatatypeObject).setDataFormat(integerFormat);
          } else {
              Log.warnln("Unknown parent object, cant set data type/format in dataTypeObj, ignoring.");
          }

          return integerFormat;
       }
    }

    // NOTE
    //

    class noteCharDataHandlerFunc implements CharDataHandlerAction {
       public void action (SaxDocumentHandler handler, char buf [], int offset, int len) {

          // add cdata as text to the last note object 
          String newText = new String(buf,offset,len);
          LastNoteObject.addText(newText);

       }
    }

    class noteStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

           String parentNodeName = getParentNodeName(); 

           // create new object appropriately 
           Note newnote = new Note();
           newnote.setXMLAttributes(attrs); // set XML attributes from passed list 

           String noteId = newnote.getNoteId();
           String noteIdRef = newnote.getNoteIdRef();

           // add this object to the lookup table, if it has an ID
           if (noteId != null) {

              // a warning check, just in case 
              if (NoteObj.containsKey(noteId))
                 Log.warnln("More than one note node with noteId=\""+noteId+"\", using latest node." );

              // add this into the list of note objects
              NoteObj.put(noteId, newnote);

           }

           //  If there is a reference object, clone it to get
           //  the new axis
           if (noteIdRef != null) {

              if (NoteObj.containsKey(noteIdRef)) {

                 BaseObject refNoteObj = (BaseObject) NoteObj.get(noteIdRef);
                 try {
                    newnote = (Note) refNoteObj.clone();
                 } catch (java.lang.CloneNotSupportedException e) {
                    Log.errorln("Weird error, cannot clone note object. Aborting read.");
                    System.exit(-1);
                 }

                 // override attrs with those in passed list
                 newnote.setXMLAttributes(attrs);

                 // give the clone a unique Id and remove IdRef 
                 newnote.setNoteId(findUniqueIdName(NoteObj, newnote.getNoteId())); 
                 newnote.setNoteIdRef(null);

                 // add this into the list of note objects
                 NoteObj.put(newnote.getNoteId(), newnote);

              } else {
                 Log.warnln("Error: Reader got a note with NoteIdRef=\""+noteIdRef+"\" but no previous note has that id! Ignoring add note request.");
                 return (Object) null;
              }
           }

           // add this object to parent object
           if( parentNodeName.equals(XDFNodeName.NOTES) )
           {
              // only NOTES objects appear in arrays, so we can 
              // just add to the current array
              CurrentArray.addNote(newnote);
           } else if ( parentNodeName.equals(XDFNodeName.FIELD) )
           {
              LastFieldObject.addNote(newnote);
           } else if ( parentNodeName.equals(XDFNodeName.PARAMETER) )
           {
              LastParameterObject.addNote(newnote);
           } else 
           {
              Log.warnln( "Unknown parent node: "+parentNodeName+" for note. Ignoring.");
           }

           LastNoteObject = newnote;

           return newnote;

       }
    }


    // NOTEINDEX
    //
    
    class noteIndexStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

          String axisIdRef = (String) null;
          int size = attrs.getLength(); 
          for (int i = 0 ; i < size; i++) {
              if (attrs.getName(i).equals("axisIdRef")) { // bad. hardwired axisIdRef name
                 axisIdRef = attrs.getValue(i);
                 break;
              }
          }
            
          if(axisIdRef != null) {
             NoteLocatorOrder.add(axisIdRef);
          }

          return (Object) null;
       }
    }


    // NOTES
    //
    
    class notesEndElementHandlerFunc implements EndElementHandlerAction {
       public void action (SaxDocumentHandler handler) {

          // reset the location order
          NoteLocatorOrder = new ArrayList ();

       }
    }

    class notesStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

          // do nothing .. this node doenst have any attributes
          // only child nodes. 
          return (Object) null;

       }
    }


    // NULL
    //

    class nullStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {
          // null means do nothing!!
          return (Object) null;
       }
    }


    // PARAMETER
    //
    
    class parameterStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

          // grab parent node name
          String parentNodeName = getParentNodeName();

          // create new object appropriately 
          Parameter newparameter = new Parameter();
          newparameter.setXMLAttributes(attrs); // set XML attributes from passed list 

          // add this object to the lookup table, if it has an ID
          String paramId = newparameter.getParamId();
          if (paramId != null) {

              // a warning check, just in case 
              if (ParamObj.containsKey(paramId))
                 Log.warnln("More than one param node with paramId=\""+paramId+"\", using latest node." );

              // add this into the list of param objects
              ParamObj.put(paramId, newparameter);

          }

          //  If there is a reference object, clone it to get
          //  the new param
          String paramIdRef = newparameter.getParamIdRef();
          if (paramIdRef != null) {

             if (ParamObj.containsKey(paramIdRef)) {

                 Parameter refParamObj = (Parameter) ParamObj.get(paramIdRef);
                 try {
                    newparameter = (Parameter) refParamObj.clone();
                 } catch (java.lang.CloneNotSupportedException e) {
                    Log.errorln("Weird error, cannot clone param object. Aborting read.");
                    System.exit(-1);
                 }

                 // override attrs with those in passed list
                 newparameter.setXMLAttributes(attrs);
                 // give the clone a unique Id and remove IdRef 
                 newparameter.setParamId(findUniqueIdName(ParamObj,newparameter.getParamId()));
                 newparameter.setParamIdRef(null);

                 // add this into the list of param objects
                 ParamObj.put(newparameter.getParamId(), newparameter);

              } else {
                Log.warnln("Error: Reader got an param with ParamIdRef=\""+paramIdRef+"\" but no previous param has that id! Ignoring add param request.");
                 return (Object) null;
              }
          }

          // determine where this goes and then insert it 
          if( parentNodeName.equals(XDFNodeName.ARRAY) ) 
          {

            newparameter = CurrentArray.addParameter(newparameter);

          } else if ( parentNodeName.equals(XDFNodeName.ROOT) 
              || parentNodeName.equals(XDFNodeName.STRUCTURE) )
          {

            newparameter = CurrentStructure.addParameter(newparameter);

          } else if ( parentNodeName.equals(XDFNodeName.PARAMETERGROUP) ) 

          {
            // for now, just add as regular parameter 
            if(LastParameterGroupParentObject instanceof Array) {
               newparameter = ((Array) LastParameterGroupParentObject).addParameter(newparameter);
            } else if(LastParameterGroupParentObject instanceof Structure) {
               newparameter = ((Structure) LastParameterGroupParentObject).addParameter(newparameter);
            }

          } else {
            Log.warnln("Error: weird parent node "+parentNodeName+" for parameter, ignoring");
            return (Object) null;
          }

          // add this object to all open groups
          Iterator iter = CurrentParameterGroupList.iterator();
          while (iter.hasNext()) {
             ParameterGroup nextParamGroupObj = (ParameterGroup) iter.next();
             newparameter.addToGroup(nextParamGroupObj);
          }

          LastParameterObject = newparameter;

          return newparameter;
       }
    }

    // PARAMETERGROUP
    //

    class parameterGroupEndElementHandlerFunc implements EndElementHandlerAction {
       public void action (SaxDocumentHandler handler) {
          // peel off the last object in the parametergroup list
          CurrentParameterGroupList.remove(CurrentParameterGroupList.size()-1);
       }
    }

    class parameterGroupStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

         // grab parent node name
          String parentNodeName = getParentNodeName();

          // create new object appropriately 
          ParameterGroup newparamGroup = new ParameterGroup();
          newparamGroup.setXMLAttributes(attrs); // set XML attributes from passed list 

          // determine where this goes and then insert it 
          if( parentNodeName.equals(XDFNodeName.ARRAY) )
          {

              newparamGroup = CurrentArray.addParamGroup(newparamGroup);
              LastParameterGroupParentObject = (Object) CurrentArray;

          } else if ( parentNodeName.equals(XDFNodeName.ROOT)
              || parentNodeName.equals(XDFNodeName.STRUCTURE) )
          {

              newparamGroup = CurrentStructure.addParamGroup(newparamGroup);
              LastParameterGroupParentObject = CurrentStructure;

          } else if ( parentNodeName.equals(XDFNodeName.PARAMETERGROUP) )

          {

              ParameterGroup LastParamGroupObject = (ParameterGroup) 
                   CurrentParameterGroupList.get(CurrentParameterGroupList.size()-1); 
              newparamGroup = LastParamGroupObject.addParamGroup(newparamGroup);

          } else {

              Log.errorln(" weird parent node $parent_node_name for parameterGroup");
              System.exit(-1); // dump core :)

          }

          // add this object to all open groups
          Iterator iter = CurrentParameterGroupList.iterator(); 
          while (iter.hasNext()) {
             ParameterGroup nextParamGroupObj = (ParameterGroup) iter.next();
             newparamGroup.addToGroup(nextParamGroupObj);
          }

          // now add it to the list
          CurrentParameterGroupList.add(newparamGroup);

          return newparamGroup; 

       }
    }

    // READ
    //

    class readEndElementHandlerFunc implements EndElementHandlerAction {
       public void action (SaxDocumentHandler handler) {

          // obtain the current XMLDataIOStyle Object
          XMLDataIOStyle readObj = CurrentArray.getXMLDataIOStyle();
   
          // initialization for XDF::Reader specific internal GLOBALS
          if ( (readObj instanceof TaggedXMLDataIOStyle) ) {

             // zero out all the tags
             Enumeration keys = DataTagCount.keys(); // slight departure from Perl
             while ( keys.hasMoreElements() )
             {
                 Object key = keys.nextElement();
                 DataTagCount.put((String) key, new Integer(0));
             }

          } else if ( (readObj instanceof DelimitedXMLDataIOStyle) ||
                      (readObj instanceof FormattedXMLDataIOStyle) )
          {

              // do nothing 

          } else {
             Log.errorln("ERROR: Dont know what do with this read style ("+readObj+"), aborting read.");
             System.exit(-1);
          }

       }
    }

    class readStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

          // save these for later, when we know what kind of dataIOstyle we got
          // Argh we really need a clone on AttributeList. Just dumb copy for now.
          DataIOStyleAttribs.clear(); // all old values cleared
          DataIOStyleAttribs = attribListToHashtable(attrs);

          // clear out the format command object array
          // (its used by Formatted reads only, but this is reasonable 
          //  spot to do this).
          CurrentFormatObjectList = new ArrayList ();

          // this will be used in formatted/delimited reads to
          // set the iteration order of the locator that will populate
          // the datacube 
          AxisReadOrder = new ArrayList();

          //  If there is a reference object, clone it to get
          //  the new readObj
          String readIdRef = (String) DataIOStyleAttribs.get("readIdRef");
          if (readIdRef != null) {

             XMLDataIOStyle readObj = null;

             if (ReadObj.containsKey(readIdRef)) {

                XMLDataIOStyle refReadObj = (XMLDataIOStyle) ReadObj.get(readIdRef);
                try {
                   readObj = (XMLDataIOStyle) refReadObj.clone();
                } catch (java.lang.CloneNotSupportedException e) {
                   Log.errorln("Weird error, cannot clone XMLDataIOStyle (read node) object. Aborting read.");
                   System.exit(-1);
                }

                // override attrs with those in passed list
                readObj.hashtableInitXDFAttributes(DataIOStyleAttribs);

                // give the clone a unique Id and remove IdRef 
                readObj.setReadId(findUniqueIdName(ReadObj, readObj.getReadId()));
                readObj.setReadIdRef(null);

                // add this into the list of Read objects
                ReadObj.put(readObj.getReadId(), readObj);

             } else {
                Log.warnln("Error: Reader got a read node with ReadIdRef=\""+readIdRef+"\"");
                Log.warnln("but no previous read node has that id! Ignoring add request.");
                return (Object) null;
             }

             // add read object to Current Array
             CurrentArray.setXMLDataIOStyle(readObj); 

             // clear attrib table since we cloned to get values 
             DataIOStyleAttribs.clear();

             CurrentFormatObjectList.add(readObj);

          }

          return (Object) null;
       }
    }

    // READCELL
    //

    class readCellStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

          // if this is still defined, we havent init'd an
          //  XMLDataIOStyle object for this array yet, do it now. 
          if ( !(DataIOStyleAttribs.isEmpty()) ) {

             // create new object appropriately 
             FormattedXMLDataIOStyle readObj = 
                 new FormattedXMLDataIOStyle (CurrentArray, DataIOStyleAttribs);
             CurrentArray.setXMLDataIOStyle(readObj); 

             String readId = readObj.getReadId();

             // add this object to the lookup table, if it has an ID
             if (readId != null) {

                // a warning check, just in case 
                if (ReadObj.containsKey(readId))
                   Log.warnln("More than one read node with readId=\""+readId+"\", using latest node." );

                // add this into the list of read objects
                ReadObj.put(readId, readObj);

             }

            // Note that we DONT need to check for IdRef here, it should be  
            // impossible to have readIdRef on DataAttributes AND then hit a readCell
            // command.

             DataIOStyleAttribs.clear(); // clear table 
             CurrentFormatObjectList.add(readObj);

          }

          // okey, now that that is taken care off, we will go
          // get the current format (read) object, and add the readCell
          // command to it.
          Object formatObj = (Object) CurrentFormatObjectList.get(CurrentFormatObjectList.size()-1);

          ReadCellFormattedIOCmd readCellObj = new ReadCellFormattedIOCmd();
          readCellObj.setXMLAttributes(attrs);

          if (formatObj instanceof FormattedXMLDataIOStyle) {
             return ((FormattedXMLDataIOStyle) formatObj).addFormatCommand(readCellObj);
          } else if ( formatObj instanceof RepeatFormattedIOCmd ) {
             return ((RepeatFormattedIOCmd) formatObj).addFormatCommand(readCellObj);
          } else {
             Log.warnln("Warning: cant add ReadCellFormattedIOCmd object to parent, ignoring request ");
          }

          return (Object) null;
       }
    }

    // REPEAT
    //

    class repeatEndElementHandlerFunc implements EndElementHandlerAction {
       public void action (SaxDocumentHandler handler) {
          // pop off last value
          CurrentFormatObjectList.remove(CurrentFormatObjectList.size()-1);
       }
    }

    class repeatStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

          // if this is still defined, we havent init'd an
          //  XMLDataIOStyle object for this array yet, do it now. 
          if ( !DataIOStyleAttribs.isEmpty()) {

             // must be formatted style, thats the only style that has 
             // repeat formatted commands
             FormattedXMLDataIOStyle readObj = new FormattedXMLDataIOStyle (CurrentArray, DataIOStyleAttribs);
             CurrentArray.setXMLDataIOStyle(readObj);
 
             String readId = readObj.getReadId();
            // add this object to the lookup table, if it has an ID
             if (readId != null) {

                // a warning check, just in case 
                if (ReadObj.containsKey(readId))
                   Log.warnln("More than one read node with readId=\""+readId+"\", using latest node." );

                // add this into the list of read objects
                ReadObj.put(readId, readObj);

             }

             // Note that we DONT need to check for IdRef here, it should be  
             // impossible to have readIdRef on DataAttributes AND then hit a repeat
             // command.

             DataIOStyleAttribs.clear(); // clear table 
             CurrentFormatObjectList.add(readObj);

          }

          // okey, now that that is taken care off, we will go
          // get the current format (read) object, and add the readCell
          // command to it.
          Object formatObj = (Object) CurrentFormatObjectList.get(CurrentFormatObjectList.size()-1);

          RepeatFormattedIOCmd repeatObj = new RepeatFormattedIOCmd();
          repeatObj.setXMLAttributes(attrs);

          if (formatObj instanceof FormattedXMLDataIOStyle) {
             CurrentFormatObjectList.add(repeatObj);
             return ((FormattedXMLDataIOStyle) formatObj).addFormatCommand(repeatObj);
          } else if ( formatObj instanceof RepeatFormattedIOCmd ) {
             CurrentFormatObjectList.add(repeatObj);
             return ((RepeatFormattedIOCmd) formatObj).addFormatCommand(repeatObj);
          } else {
             Log.warnln("Warning: cant add RepeatFormattedIOCmd object to parent, ignoring request ");
          }

          return (Object) null;

       }
    }

    // ROOT 
    //

    // Root node start 
    class rootStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) { 
          // The root node is just a "structure" node,
          // but is always the first one.
          XDF.setXMLAttributes(attrs); // set XML attributes from passed list 
          CurrentStructure = XDF;      // current working structure is now the root 
                                       // structure

          // if this global option is set in the reader, we use it
          if(Options.contains("DefaultDataDimensionSize")) { 
             int value = ((Integer) Options.get("DefaultDataDimensionSize")).intValue();
             Specification.getInstance().setDefaultDataArraySize(value);
          }

          return CurrentStructure;
       }
    }

    // SKIPCHAR
    //

    class skipCharStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) { 

          // if this is still defined, we havent init'd an
          //  XMLDataIOStyle object for this array yet, do it now. 
          if ( !DataIOStyleAttribs.isEmpty()) {

             // If we see a skipChar command, then we must have Formatted data IO style
             FormattedXMLDataIOStyle readObj = new FormattedXMLDataIOStyle (CurrentArray, DataIOStyleAttribs);
             CurrentArray.setXMLDataIOStyle(readObj);

             String readId = readObj.getReadId();
             // add this object to the lookup table, if it has an ID
             if (readId != null) {

                // a warning check, just in case 
                if (ReadObj.containsKey(readId))
                   Log.warnln("More than one read node with readId=\""+readId+"\", using latest node." );

                // add this into the list of read objects
                ReadObj.put(readId, readObj);

             }

            // Note that we DONT need to check for IdRef here, it should be  
            // impossible to have readIdRef on DataAttributes AND then hit a skipChar
            // command.

             DataIOStyleAttribs.clear(); // clear out table 
             CurrentFormatObjectList.add(readObj);

          }

          // okey, now that that is taken care off, we will go
          // get the current format (read) object, and add the readCell
          // command to it.
          Object formatObj = (Object) CurrentFormatObjectList.get(CurrentFormatObjectList.size()-1);

          SkipCharFormattedIOCmd skipObj = new SkipCharFormattedIOCmd();
          skipObj.setXMLAttributes(attrs);

          if (formatObj instanceof FormattedXMLDataIOStyle) {
             return ((FormattedXMLDataIOStyle) formatObj).addFormatCommand(skipObj);
          } else if ( formatObj instanceof RepeatFormattedIOCmd ) {
             return ((RepeatFormattedIOCmd) formatObj).addFormatCommand(skipObj);
          } else {
             Log.warnln("Warning: cant add SkipCharFormattedIOCmd object to parent, ignoring request ");
          }

          return (Object) null;

       }
    }

    // STRINGFIELD
    //

    class stringFieldStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) { 

         // create the object
          StringDataFormat stringFormat = new StringDataFormat();
          stringFormat.setXMLAttributes(attrs);
          stringFormat.setXMLAttributes(DataFormatAttribs); // probably arent any, but who knows.. 

          if (CurrentDatatypeObject instanceof Field) {
              ((Field) CurrentDatatypeObject).setDataFormat(stringFormat);
          } else if (CurrentDatatypeObject instanceof Array) {
              ((Array) CurrentDatatypeObject).setDataFormat(stringFormat);
          } else {
              Log.warnln("Unknown parent object, cant set data type/format in dataTypeObj, ignoring");
          }

          return stringFormat;

       }
    }


    // STRUCTURE
    //

    class structureStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) { 
          Log.errorln("STRUCTURE Start handler not implemented yet.");
          return (Object) null;
       }
    }

    // TAGTOAXIS
    //

    // Our purpose here: configure the TaggedXMLDataIOStyle with axis/tag associations.
    class tagToAxisStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) { 

          // well, if we see tagToAxis nodes, must have tagged data, the 
          // default style. No need for initing further. 

//          if ( defined $DataIOStyle_Attrib_Ref) {
//              $CURRENT_ARRAY->XmlDataIOStyle(new XDF::TaggedXMLDataIOStyle($DataIOStyle_Attrib_Ref))
//          }

         // I cant imagine any need for this in Java. In Perl even?
//          $DataIOStyle_Attrib_Ref = undef;

          String tagname = new String ();
          String axisIdRefname = new String();

          // pickup overriding values from attribute list
          int size = attrs.getLength(); 
          for (int i = 0; i < size; i++)
          {
              String name = attrs.getName(i);
              if ( name.equals("tag") ) {
                 tagname = attrs.getValue(i);
              } else if ( name.equals("axisIdRef")) {
                 axisIdRefname = attrs.getValue(i);
              }
          }

          // works?
          ((TaggedXMLDataIOStyle) CurrentArray.getXMLDataIOStyle()).setAxisTag(tagname, axisIdRefname);

          return (Object) null;

       }
    }

    // UNIT
    //

    class unitCharDataHandlerFunc implements CharDataHandlerAction {
       public void action (SaxDocumentHandler handler, char buf [], int offset, int len) {

          LastUnitObject.setValue(new String(buf,offset,len));

       }
    }

    class unitStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) { 

          //  grab parent node name
          String gParentNodeName = getGrandParentNodeName();

          // create new object appropriately 
          Unit newunit = new Unit();
          newunit.setXMLAttributes(attrs);

          // determine where this goes and then insert it 
          if( gParentNodeName.equals(XDFNodeName.PARAMETER) )
          {

              newunit = LastParameterObject.addUnit(newunit);

          } else if ( gParentNodeName.equals(XDFNodeName.FIELD) )
          {

              newunit = LastFieldObject.addUnit(newunit);

          } else if ( gParentNodeName.equals(XDFNodeName.AXIS) )
          {

              // yes, axis is correct here, cant add units to a fieldAxis 
              // (only to fields!)
              Axis lastAxisObject = (Axis) CurrentArray.getAxisList().get(CurrentArray.getAxisList().size()-1);
              newunit = lastAxisObject.addUnit(newunit);

          } else if ( gParentNodeName.equals(XDFNodeName.ARRAY) )
          {

              newunit = CurrentArray.addUnit(newunit);

          } else {
              Log.warnln("Unknown grandparent object, cant add unit, ignoring.");
          }

          LastUnitObject = newunit;

          return newunit;

       }
    }

    // VALUE 
    //

   class valueStartElementHandlerFunc implements StartElementHandlerAction {
      public Object action (SaxDocumentHandler handler, AttributeList attrs) {

          ValueAttribs.clear(); // clear out old values, if any
          // save these for later, when we know what kind of dataIOstyle we got
          // Argh we really need a clone on AttributeList. Just dumb copy for now.
          ValueAttribs = attribListToHashtable(attrs);

          //  If there is a reference object, clone it to get
          //  the new value
          String valueIdRef = (String) ValueAttribs.get("valueIdRef");
          if (valueIdRef != null) {

             Value newvalue = null;

             if (ValueObj.containsKey(valueIdRef)) {

                 Value refValueObj = (Value) ValueObj.get(valueIdRef);
                 try {
                    newvalue = (Value) refValueObj.clone();
                 } catch (java.lang.CloneNotSupportedException e) {
                    Log.errorln("Weird error, cannot clone value object. Aborting read.");
                    System.exit(-1);
                 }

                 // override attrs with those in passed list
                 newvalue.setXMLAttributes(attrs);
                 // give the clone a unique Id and remove IdRef 
                 newvalue.setValueId(findUniqueIdName(ValueObj,newvalue.getValueId()));
                 newvalue.setValueIdRef(null);

                 // add this into the list of value objects
                 ValueObj.put(newvalue.getValueId(), newvalue);

              } else {
                Log.warnln("Error: Reader got an value with ValueIdRef=\""+valueIdRef+"\" but no previous value has that id! Ignoring add value request.");
                 return (Object) null;
              }

              if (newvalue != null) {
                 // well, we got a value. That means we have to add it in. 
                 // sigh. Yes, some repeat code from valueCharDataHandler. Too lazy 
                 // to properly turn it into a sub-routine. -b.t.

                 //  grab parent node name
                 // this special call will find the first parent node name 
                 // that doesnt match XDFNodeName.VALUEGROUP
                 String parentNodeName = getParentNodeName(XDFNodeName.VALUEGROUP);

                 // determine where this goes and then insert it 
                 if( parentNodeName.equals(XDFNodeName.PARAMETER) )
                 {

                    newvalue = LastParameterObject.addValue(newvalue);

                 } else if ( parentNodeName.equals(XDFNodeName.AXIS) )
                 {

                    List axisList = (List) CurrentArray.getAxisList();
                    Axis lastAxisObject = (Axis) axisList.get(axisList.size()-1);
                    newvalue = lastAxisObject.addAxisValue(newvalue);

//          } else if ( parentNodeName.equals(XDFNodeName.VALUEGROUP) )
//          {
//
                 } else {
                    Log.errorln("Error: weird parent node "+parentNodeName+" for value.");
                    System.exit(-1); // fatal error, shut down 
                 }

                 // Now add this object to all open groups
                 Iterator iter = CurrentValueGroupList.iterator();
                 while (iter.hasNext()) {
                    ValueGroup nextValueGroupObj = (ValueGroup) iter.next();
                    newvalue.addToGroup(nextValueGroupObj);
                 }

                 // since we added object here, clear the attributes now.
                 ValueAttribs.clear();
              }
          }

          return (Object) null; 
      }
   }

   class valueCharDataHandlerFunc implements CharDataHandlerAction {
      public void action (SaxDocumentHandler handler, char buf [], int offset, int len) {

          //  grab parent node name
          // this special call will find the first parent node name 
          // that doesnt match XDFNodeName.VALUEGROUP
          String parentNodeName = getParentNodeName(XDFNodeName.VALUEGROUP);
          // String parentNodeName = getParentNodeName();
          
          // create new object appropriately 
          Value newvalue = new Value();
          newvalue.hashtableInitXDFAttributes(ValueAttribs);
          // reconsitute the value node PCdata from passed information.
          // and add value to object
          newvalue.setValue( new String (buf, offset, len) );

         // add this object to the lookup table, if it has an ID
          String valueId = newvalue.getValueId();
          if (valueId != null) {

              // a warning check, just in case 
              if (ValueObj.containsKey(valueId))
                 Log.warnln("More than one value node with valueId=\""+valueId+"\", using latest node." );

              // add this into the list of value objects
              ValueObj.put(valueId, newvalue);

          }

          //  If there is a reference object, clone it to get
          //  the new value
          String valueIdRef = newvalue.getValueIdRef();
          if (valueIdRef != null) {

             if (ValueObj.containsKey(valueIdRef)) {

                 Value refValueObj = (Value) ValueObj.get(valueIdRef);
                 try {
                    newvalue = (Value) refValueObj.clone();
                 } catch (java.lang.CloneNotSupportedException e) {
                    Log.errorln("Weird error, cannot clone value object. Aborting read.");
                    System.exit(-1);
                 }

                 // override attrs with those in passed list
                 newvalue.hashtableInitXDFAttributes(ValueAttribs);

                 // give the clone a unique Id and remove IdRef 
                 newvalue.setValueId(findUniqueIdName(ValueObj,newvalue.getValueId()));
                 newvalue.setValueIdRef(null);

                 // add this into the list of value objects
                 ValueObj.put(newvalue.getValueId(), newvalue);

              } else {
                 Log.warnln("Error: Reader got an value with ValueIdRef=\""+valueIdRef+"\" but no previous value has that id! Ignoring add value request.");
                 return;
              }
          }

          // determine where this goes and then insert it 
          if( parentNodeName.equals(XDFNodeName.PARAMETER) )
          {

              newvalue = LastParameterObject.addValue(newvalue);

          } else if ( parentNodeName.equals(XDFNodeName.AXIS) ) 
          {

              List axisList = (List) CurrentArray.getAxisList();
              Axis lastAxisObject = (Axis) axisList.get(axisList.size()-1);
              newvalue = lastAxisObject.addAxisValue(newvalue);

//          } else if ( parentNodeName.equals(XDFNodeName.VALUEGROUP) )
//          {
//
//

          } else {
             Log.errorln("Error: weird parent node "+parentNodeName+" for value.");
             System.exit(-1); // fatal error, shut down 
          }

          // 4. add this object to all open groups
          Iterator iter = CurrentValueGroupList.iterator();
          while (iter.hasNext()) {
             ValueGroup nextValueGroupObj = (ValueGroup) iter.next();
             newvalue.addToGroup(nextValueGroupObj);
          }

          ValueAttribs.clear();
       }
    }

    // VALUEGROUP 
    //

    class valueGroupEndElementHandlerFunc implements EndElementHandlerAction {
       public void action (SaxDocumentHandler handler) {
          CurrentValueGroupList.remove(CurrentValueGroupList.size()-1);
       }
    }

    class valueGroupStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {

          // 1. grab parent node name
          String parentNodeName = getParentNodeName();

          // 2. create new object appropriately 
          ValueGroup newvalueGroup = new ValueGroup();
          newvalueGroup.setXMLAttributes(attrs); // set XML attributes from passed list 

          // 3. determine where this goes and then insert it 
          if( parentNodeName.equals(XDFNodeName.AXIS) )
          {

              // get the last axis
              List axisList = (List) CurrentArray.getAxisList();
              Axis lastAxisObject = (Axis) axisList.get(axisList.size()-1);
              newvalueGroup = lastAxisObject.addValueGroup(newvalueGroup);

              LastValueGroupParentObject = lastAxisObject;

          } else if ( parentNodeName.equals(XDFNodeName.PARAMETER) )
          {

              newvalueGroup = LastParameterObject.addValueGroup(newvalueGroup);
              LastValueGroupParentObject = LastParameterObject;

          } else if ( parentNodeName.equals(XDFNodeName.VALUEGROUP) )

          {

             ValueGroup lastValueGroup = (ValueGroup) 
                    CurrentValueGroupList.get(CurrentValueGroupList.size()-1);
             newvalueGroup = lastValueGroup.addValueGroup(newvalueGroup);

          } else {
             Log.errorln("Error: weird parent node "+parentNodeName+" for "+XDFNodeName.VALUEGROUP);
             System.exit(-1); // fatal error, shut down 
          }

          // 4. add this object to all open value groups
          Iterator iter = CurrentValueGroupList.iterator();
          while (iter.hasNext()) {
             ValueGroup nextValueGroupObj = (ValueGroup) iter.next();
             newvalueGroup.addToGroup(nextValueGroupObj);
          }

          // now add it to the list
          CurrentValueGroupList.add(newvalueGroup);

          return newvalueGroup;

       }
    }

    // VALUELIST 
    //

    class valueListCharDataHandlerFunc implements CharDataHandlerAction {
       public void action (SaxDocumentHandler handler, char buf [], int offset, int len) {

          // IF we get here, we have the delmited case for populating
          // a value list.

          // 1. set up information we need
          // our string that we will parse
          String valueListString = new String (buf, offset, len);
          String delimiter = (String) CurrentValueListParameter.get("delimiter");
          String repeatable = (String) CurrentValueListParameter.get("repeatable");

          // 2. reconsitute information stored in CurrentValueListParameter table 
          String parentNodeName = (String) CurrentValueListParameter.get("parentNodeName");

          // 3. split up string into values based on declared delimiter
          // and snag the string representation of the values
          ArrayList strValueList = 
              splitStringIntoStringObjects(valueListString, delimiter, repeatable, null );

          // 4. add these values to the lookup table, if the original valueList had an ID
          String valueListId = (String) CurrentValueListParameter.get("valueListId");
          if (valueListId != null) {

              // a warning check, just in case 
              if (ValueListObj.containsKey(valueListId))
                 Log.warnln("More than one valueList node with valueListId=\""+valueListId+"\", using latest node." );

              // add the valueList array into the list of valueList objects
              ValueListObj.put(valueListId, strValueList);

          }

          //  5. If there is a reference object, clone it to get
          //     the new valueList
          String valueListIdRef = (String) CurrentValueListParameter.get("valueListIdRef");
          if (valueListIdRef != null) {

             if (ValueListObj.containsKey(valueListIdRef)) {

                 // Just a simple clone since we have stored the ArrayList rather than the
                 // ValueList object (which actually doesnt exist. :P
                 ArrayList refValueListObj = (ArrayList) ValueListObj.get(valueListIdRef);
                 strValueList = (ArrayList) refValueListObj.clone();

// This is missing. Should allow override.
/*
                 // override attrs with those in passed list
              //   strValueList.setXMLAttributes(attrs);
                 // give the clone a unique Id and remove IdRef 
              //   strValueList.setValueListId(findUniqueIdName(ValueListObj,strValueList.getValueListId()));
              //   strValueList.setValueListIdRef(null);

                 // add this into the list of valueList objects
              //   ValueListObj.put(strValueList.getValueListId(), strValueList);
*/

              } else {
                 Log.warnln("Error: Reader got an valueList with ValueListIdRef=\""+valueListIdRef+"\" but no previous valueList has that id! Ignoring add valueList request.");
                 return;
              }
          }


          // 6. determine where these values go and then insert them
          //   (hurm. could have reused code here. lazy. feh.
          if( parentNodeName.equals(XDFNodeName.AXIS) )
          {

             // get the last axis
             List axisList = (List) CurrentArray.getAxisList();
             Axis lastAxisObject = (Axis) axisList.get(axisList.size()-1);

             // now create value objects, add them to groups 
             Iterator iter = strValueList.iterator();
             while (iter.hasNext())
             {
                String valueString = (String) iter.next();

                // add the value to the axis
                Value newvalue = new Value(valueString);
                lastAxisObject.addAxisValue(newvalue);

                // add this object to all open value groups
                Iterator groupIter = CurrentValueGroupList.iterator();
                while (groupIter.hasNext())
                {
                   ValueGroup nextValueGroupObj = (ValueGroup) groupIter.next();
                   newvalue.addToGroup(nextValueGroupObj);
                }
             }

          } 
           else if( parentNodeName.equals(XDFNodeName.PARAMETER) )
          {

             // now create value objects, add them to groups 
             Iterator iter = strValueList.iterator();
             while (iter.hasNext())
             {
                String valueString = (String) iter.next();

                // add the value to the axis
                Value newvalue = new Value(valueString);
                LastParameterObject.addValue(newvalue);

                // add this object to all open value groups
                Iterator groupIter = CurrentValueGroupList.iterator();
                while (groupIter.hasNext())
                {
                   ValueGroup nextValueGroupObj = (ValueGroup) groupIter.next();
                   newvalue.addToGroup(nextValueGroupObj);
                }
             }

          } 
          else 
          {
             Log.errorln("Error: weird parent node "+parentNodeName+" for "+XDFNodeName.VALUELIST+", aborting read.");
             System.exit(-1);
          } 

          CurrentValueListParameter.put("isDelimitedCase", "true"); // notify that we did the list 

       }
    }


    // there is undoubtably some code-reuse spots missed in this function.
    // get it later when Im not being lazy. -b.t.
    class valueListStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {
 
           // 1. re-init
           CurrentValueListParameter.clear();

           // 2. populate ValueListparameters from attribute list 
           CurrentValueListParameter = attribListToHashtable(attrs);

           // 3. populate ValueListparameters w/ parent name 
           String parentNodeName = getParentNodeName(XDFNodeName.VALUEGROUP);
           CurrentValueListParameter.put("parentNodeName", parentNodeName);

           // 4. set this parameter to false to indicate the future is not
           // yet determined for this :)
           CurrentValueListParameter.put("isDelimitedCase", "false"); 

           return (Object) null;

       }
    }

    class valueListEndElementHandlerFunc implements EndElementHandlerAction {
       public void action (SaxDocumentHandler handler) {

          // generate valuelist values from algoritm IF we need to
          // (e.g. values where'nt in a delimited cdata list)
          // check to see if we didnt alrealy parse from a delmited string.
          if ( ((String) CurrentValueListParameter.get("isDelimitedCase")).equals("true") ) 
             return; // we already did the list, leave here 


          // 1. grab parent node name
          String parentNodeName = (String) CurrentValueListParameter.get("parentNodeName");

          // 2. Find the list of values. If there is a reference object, clone it to get
          //    the new values list, otherwise, determine values from attributes 
          //    (e.g. algorithm method)
          ArrayList values = null;
          String valueListIdRef = (String) CurrentValueListParameter.get("valueListIdRef");
          if (valueListIdRef != null) {
             if (ValueListObj.containsKey(valueListIdRef)) {

                 // Just a simple clone since we have stored the ArrayList rather than the
                 // ValueList object (which actually doesnt exist. :P
                 ArrayList refValueListObj = (ArrayList) ValueListObj.get(valueListIdRef);
                 values  = (ArrayList) refValueListObj.clone();

             } else {
                 Log.warnln("Error: Reader got an valueList with ValueListIdRef=\""+valueListIdRef+"\" but no previous valueList has that id! Ignoring add valueList request.");
                 return;
              }

          } else { 
             values = getValueListNodeValues();
          } 

          // 4. add these values to the lookup table, if the original valueList had an ID
          String valueListId = (String) CurrentValueListParameter.get("valueListId");
          if (valueListId != null) {

              // a warning check, just in case 
              if (ValueListObj.containsKey(valueListId))
                 Log.warnln("More than one valueList node with valueListId=\""+valueListId+"\", using latest node." );

              // add the valueList array into the list of valueList objects
              ValueListObj.put(valueListId, values);

          }

          // 5. Populate correct parent node w/ values 
          if(values.size() > 0 ) { // needed safety?

             ArrayList valueObjList = new ArrayList();

             if( parentNodeName.equals(XDFNodeName.AXIS) )
             {

                    // get the last axis
                    List axisList = (List) CurrentArray.getAxisList();
                    Axis lastAxisObject = (Axis) axisList.get(axisList.size()-1);

                    Iterator iter = values.iterator();
                    while (iter.hasNext()) {
                        String valuePCDATA = (String) iter.next();
                        Value value = new Value (valuePCDATA);
                        valueObjList.add(lastAxisObject.addAxisValue(value));
                    }

             } 
             else if ( parentNodeName.equals(XDFNodeName.VALUEGROUP) )
             {

                if ( LastValueGroupParentObject instanceof Parameter )
                {

                    Parameter myParamObject = (Parameter) LastValueGroupParentObject;

                    Iterator iter = values.iterator();
                    while (iter.hasNext()) {
                        String valuePCDATA = (String) iter.next();
                        Value value = new Value (valuePCDATA);
                        valueObjList.add(myParamObject.addValue(value));
                    }

                } else if ( LastValueGroupParentObject instanceof Axis )
                {

                    // get the last axis
                    Axis myAxisObject = (Axis) LastValueGroupParentObject;

                    Iterator iter = values.iterator();
                    while (iter.hasNext()) {
                        String valuePCDATA = (String) iter.next();
                        Value value = new Value (valuePCDATA);
                        valueObjList.add(myAxisObject.addAxisValue(value));
                    }

                } else {
                    Log.warnln("Error: unknown valueGroupParent "+LastValueGroupParentObject+
                               " cant treat for "+XDFNodeName.VALUELIST);
                    return; // bail 

                }

             } else if ( parentNodeName.equals(XDFNodeName.PARAMETER) )
             {

                 Iterator iter = values.iterator();
                 while (iter.hasNext())
                 {
                    String valuePCDATA = (String) iter.next();
                    Value value = new Value (valuePCDATA);
                    valueObjList.add(LastParameterObject.addValue(value));
                 }

             } else {
                 Log.errorln("Error: weird parent node "+parentNodeName+" for "+XDFNodeName.VALUELIST);
                 System.exit(-1); // fatal error, shut down 
             }
          }

// Need to do something wi/ ValueObjList HERE?? 


       }
    }

    // VECTOR
    //

    class vectorStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {
          Log.errorln("VECTOR Start handler not implemented yet.");
          return (Object) null;
       }
    }


} // End of SaxDocumentHandler class 

/* Modification History:
 *
 * $Log$
 * Revision 1.27  2001/01/29 05:05:42  thomas
 * Code for reading binary values. The implemented code
 * is a travesty, it converts bytes to Strings, then decides
 * how to make strings into numbers and so forth. There
 * is some better (but still lousy) code for reading in
 * bytes directly into the array. Lots of needless globals
 * and messy code. This will need further work. -b.t.
 *
 * Revision 1.26  2001/01/22 22:11:58  thomas
 * Added id/idref struff to valueList, value and parameter. Read
 * id/idref stuff fixed. valueList values may now be added to
 * parameter objects. -b.t.
 *
 * Revision 1.25  2001/01/19 22:34:28  thomas
 * Fixed handling of readId/IdRef stuff. Added Id/IdRef stuff
 * for value, parameter. valuelist is not done yet. -b.t.
 *
 * Revision 1.24  2001/01/19 17:23:03  thomas
 * Fixed Href stuff to DTD standard. Now using
 * notation and entities at the beginning of the
 * file. -b.t.
 *
 * Revision 1.23  2001/01/17 18:28:46  thomas
 * removed unneeded debug comments. -b.t.
 *
 * Revision 1.22  2001/01/17 18:08:25  thomas
 * Minor bug, changed exponential node name
 * from "exponent" to "exponential". -b.t.
 *
 * Revision 1.21  2000/11/27 22:40:44  thomas
 * Fix to allow attribute text to have newline, carriage
 * returns in them (print out as entities: &#010; and
 * &#013;) This allows files printed out to be read back
 * in again(yeah!). Also, a fix to ValueGroup, added
 * method of getParentNode(ignoreThisNode) to allow
 * us to find the correct parent. Now, axis node is getting
 * its child values correctly. Need to check that this is
 * in place for ParameterGroup and FieldGroup. -b.t.
 *
 * Revision 1.20  2000/11/27 19:58:52  thomas
 * Added repeatable functionality to splitstringInotObj
 * method. -b.t.
 *
 * Revision 1.19  2000/11/22 22:04:06  thomas
 * Cloned objects for Id/IDRef stuff *shouldnt* have
 * an idRef after cloning. Inserted line to set this
 * attribute to null in the new object. -b.t.
 *
 * Revision 1.18  2000/11/22 21:23:24  thomas
 * Implimented Formatted Reads. Fixed AxisId/IdRef
 * problem of not having unique id on cloned axis.
 * (in fact, fixed this for all other id/idRef type objects
 * too). -b.t.
 *
 * Revision 1.17  2000/11/20 22:09:05  thomas
 * *** empty log message ***
 *
 * Revision 1.16  2000/11/17 22:29:23  thomas
 * Some changes to allow formattedIO, not finished with
 * the data handler yet tho. -b.t.
 *
 * Revision 1.15  2000/11/17 16:01:43  thomas
 * Minor bug fix, needed to switch to using Specification
 * class. -b.t.
 *
 * Revision 1.14  2000/11/10 06:14:43  thomas
 * Added set/get for Current/Array/Structure/DatatypeObject
 * methods . -b.t.
 *
 * Revision 1.13  2000/11/10 05:49:44  thomas
 * Updated start/end element handlers to update
 * CUrrentObjectList appropriately. Added getLastObject
 * method. -b.t.
 *
 * Revision 1.12  2000/11/09 23:04:56  thomas
 * Updated version, made changes to allow extension
 * to other dataformats (e.g. FITSML). -b.t.
 *
 * Revision 1.11  2000/11/09 04:24:12  thomas
 * Implimented small efficiency improvements to traversal
 * loops. -b.t.
 *
 * Revision 1.10  2000/11/08 22:30:11  thomas
 * Changed set methods to return void. -b.t.
 *
 * Revision 1.9  2000/11/07 21:53:53  thomas
 * 2 Fixes: null problem wi/ reading in axisUnits and
 * reading in of valueList from an algoritm. -b.t.
 *
 * Revision 1.8  2000/11/06 14:47:32  thomas
 * First Cut at delimited implimented. Problem in
 * Locator (?) prevents it from being fully functional.
 * -b.t.
 *
 * Revision 1.7  2000/11/03 20:10:07  thomas
 * Now tagged data is read into the dataCube with the
 * correct formatting. -b.t.
 *
 * Revision 1.6  2000/11/02 19:45:13  thomas
 * Updated to read in Notes objects. -b.t.
 *
 * Revision 1.5  2000/11/01 22:14:03  thomas
 * Updated for new cloning scheme. -b.t.
 *
 * Revision 1.4  2000/11/01 21:59:31  thomas
 * Implimented ValueGroup, FieldGroup fully. -b.t.
 *
 * Revision 1.3  2000/10/31 20:38:00  thomas
 * This version is ALMOST capable of full ascii tagged
 * read. Problems that remain are grouping, notes location
 * and note.addText function is off a bit. Also consideration
 * of dataFormat when addData occurs needs to be implemented.
 * -b.t.
 *
 * Revision 1.2  2000/10/26 20:42:33  thomas
 * Another interim version. Putting into CVS so I can
 * sync w/ kellys other changes easier. -b.t.
 *
 * Revision 1.1  2000/10/25 17:57:00  thomas
 * Initial Version. -b.t.
 *
 * 
 */

