
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Enumeration;
import java.util.Iterator;
import java.lang.Character;

// Import needed SAX stuff
import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;

/** 
 */
public class SaxDocumentHandler implements DocumentHandler {

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

    // GLOBALs for saving these between dataFormat/read node and later when we 
    // know what kind of DataFormat/DataIOStyle object we really have
    private Hashtable DataIOStyleAttribs;
    private AttributeList DataFormatAttribs;

    // for tagged reads only. Keeps track of which data tags are open
    // so we know which datacell in the current datacube to shunt the 
    // data to.
    private Hashtable DataTagCount = new Hashtable();

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
    private Hashtable CurrentValueListParameter;

    // Data writing stuff
    private int CurrentDataTagLevel = 0; // how nested we are within d0/d1/d2 data tags
    private int DataNodeLevel = 0; // how deeply nested we are within data nodes 
    private int DataTagLevel = 0; // the level where the actual char data is
    private Locator TaggedLocatorObj;
    private StringBuffer DATABLOCK;
    private boolean CDATAIsArrayData = false;
    private int MaxDataFormatIndex = 0;     // max allowed index in the DataFormatList[]
    private int CurrentDataFormatIndex = 0; // which dataformat index (in DatFormatList[]) we currently are reading
    private DataFormat DataFormatList[];       // list of CurrentArray.getDataFormatList();
    private int LastFastAxisCoordinate;
    private AxisInterface FastestAxis;
    private ArrayList AxisReadOrder;

    // lookup tables holding objects that have id/idref stuff
    private Hashtable FieldObj = new Hashtable();
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

    // what is this for?? hurm.. 
    public void setDocumentLocator (org.xml.sax.Locator l)
    {
        // we'd record this if we needed to resolve relative URIs
        // in content or attributes, or wanted to give diagnostics.
        // Right now, do nothing here.

        // do nothing, method required by interface 
    }

    public void startDocument()
    throws SAXException
    {
        // do nothing, method required by interface 
    }

    public void endDocument()
    throws SAXException
    {
        // do nothing, method required by interface 
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

    public void processingInstruction(String target, String data)
    throws SAXException
    {
        // Log.debugln("<?"+target+" "+data+"?>");
        // do nothing, method required by interface 
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
       startElementHandlerHashtable.put(XDFNodeName.VALUE, new nullStartElementHandlerFunc());
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

       // Note that we dont treat binary data at all here 
       try {

           if ( CurrentDataFormat instanceof StringDataFormat) {
              CurrentArray.setData(dataLocator, thisString);
           } else if ( CurrentDataFormat instanceof FixedDataFormat) {
              Double number = new Double (thisString);
              CurrentArray.setData(dataLocator, number.doubleValue());
           } else if ( CurrentDataFormat instanceof IntegerDataFormat) {
              Integer number = new Integer (thisString);
              CurrentArray.setData(dataLocator, number.intValue());
           } else if ( CurrentDataFormat instanceof ExponentialDataFormat) {
              // hurm.. this is a stop-gap. Exponential format needs to be
              // preserved better than this. -b.t. 
              Double number = new Double (thisString);
              CurrentArray.setData(dataLocator, number.doubleValue());
           } else {
              Log.warnln("The dataFormat:"+CurrentDataFormat.toString()+" is not allowed in tagged data.");
              Log.warnln("Unable to setData:["+thisString+"], ignoring request");
           }

       } catch (SetDataException e) {
           // bizarre error. Cant add data (out of memory??) :P
           Log.errorln("Unable to setData:["+thisString+"], ignoring request");
           Log.printStackTrace(e);
       }
    }

    // *sigh* lack of regular expression support makes this 
    // difficult to do. I expect that it will be possible to 
    // break this in various ways if the PCDATA in the XML 
    // document is off. -b.t. 
    //
    // ALSO: the repeatable function is properly implemented for this yet. -b.t.
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

          } else if (termDelimiterSize > 0 
                       && valueListString.substring(start, start + termDelimiterSize).compareTo(terminatingDelimiter) == 0
                    ) 
          { 

             // we hit record terminator(delimiter) 
             start += termDelimiterSize;
 
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
        DataFormat dataFormat[] = CurrentArray.getDataFormatList();

        int dataPosition = 0;
        int dataLength = data.length();
        int currentDataFormat = 0;
        int nrofDataFormat = dataFormat.length;

        Log.debugln("in formattedSplitString, data :["+data+"]");

        // the extraction loop
        // whip thru the data string, either ignoring or accepting
        // characters in the string as directed by the formatCmdList
        while (dataPosition < dataLength) {

          Iterator formatIter = commandList.iterator();
          while (formatIter.hasNext()) {
             FormattedIOCmd thisCommand = (FormattedIOCmd) formatIter.next();
             if (thisCommand instanceof ReadCellFormattedIOCmd) // ReadCell ==> read some data
             {

                 DataFormat formatObj = dataFormat[currentDataFormat];
                 int endDataPosition = dataPosition + formatObj.numOfBytes();

                 // add in our object
                 if(endDataPosition > dataLength) { 
                    Log.errorln("Format specification exceeded data width, Bad format?");
                    Log.errorln("Run in debug mode to examine formatted read. Aborting.");
                    System.exit(-1); // throw IOException;
                 }

                 String thisData = data.substring(dataPosition,endDataPosition);

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
       public static final String EXPONENT = "exponent";
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
                    // give the clone a unique axisId
                    newaxis.setAxisId(findUniqueIdName(AxisObj,newaxis.getAxisId())); 

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

             // update the array dataCube with passed attributes
             CurrentArray.getDataCube().setXMLAttributes(attrs);

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
                // give the clone a unique fieldId
                newfield.setFieldId(findUniqueIdName(FieldObj, newfield.getFieldId())); 

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
                    // give the clone a unique axisId
                    newfieldaxis.setAxisId(findUniqueIdName(AxisObj, newfieldaxis.getAxisId())); 

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
                 // give the clone a unique Id
                 newnote.setNoteId(findUniqueIdName(NoteObj, newnote.getNoteId())); 

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

       // set the locatorOrder in the Notes object
/*
   my $notesObj = $LAST_NOTES_OBJECT;

#   if (ref($notesObj) eq 'XDF::Array') {
#     for (@NOTE_LOCATOR_ORDER) { $notesObj->addAxisIdToLocatorOrder($_); }
#   }
*/

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
          DataIOStyleAttribs = new Hashtable();
          int size = attrs.getLength(); 
          for (int i = 0; i < size; i++) { 
             String name = attrs.getName(i); 
             String value = attrs.getValue(i);
             if (value != null) 
                DataIOStyleAttribs.put(name, value);
          }

          // clear out the format command object array
          // (its used by Formatted reads only, but this is reasonable 
          //  spot to do this).
          CurrentFormatObjectList = new ArrayList ();

          // this will be used in formatted/delimited reads to
          // set the iteration order of the locator that will populate
          // the datacube 
          AxisReadOrder = new ArrayList();

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

             String readId = readObj.getReadId();
             String readIdRef = readObj.getReadIdRef();

             // add this object to the lookup table, if it has an ID
             if (readId != null) {

                // a warning check, just in case 
                if (ReadObj.containsKey(readId))
                   Log.warnln("More than one read node with readId=\""+readId+"\", using latest node." );

                // add this into the list of note objects
                ReadObj.put(readId, readObj);

             }

             //  If there is a reference object, clone it to get
             //  the new readObj
             if (readIdRef != null) {

                if (ReadObj.containsKey(readIdRef)) {

                   BaseObject refReadObj = (BaseObject) ReadObj.get(readIdRef);
                   try {
                      readObj = (FormattedXMLDataIOStyle) refReadObj.clone();
                   } catch (java.lang.CloneNotSupportedException e) {
                      Log.errorln("Weird error, cannot clone FormattedXMLDataIOStyle (read node) object. Aborting read.");
                      System.exit(-1);
                   }

                   // override attrs with those in passed list
                   readObj.hashtableInitXDFAttributes(DataIOStyleAttribs);

                   // give the clone a unique Id
                   readObj.setReadId(findUniqueIdName(ReadObj, readObj.getReadId()));

                   // add this into the list of note objects
                   ReadObj.put(readObj.getReadId(), readObj);

                } else {
                   Log.warnln("Error: Reader got a read node with ReadIdRef=\""+readIdRef+"\" but no previous read node has that id! Ignoring add request.");
                   return (Object) null;
                }
             }


             CurrentArray.setXMLDataIOStyle(readObj); 

             DataIOStyleAttribs = new Hashtable();  // clear table 
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

             // FormattedXMLDataIOStyle readObj = new FormattedXMLDataIOStyle(CurrentArray);
             FormattedXMLDataIOStyle readObj = new FormattedXMLDataIOStyle (CurrentArray, DataIOStyleAttribs);
             // readObj.setXMLAttributes(DataIOStyleAttribs);
             CurrentArray.setXMLDataIOStyle(readObj);

             DataIOStyleAttribs = new Hashtable ();
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

             // FormattedXMLDataIOStyle readObj = new FormattedXMLDataIOStyle(CurrentArray);
             FormattedXMLDataIOStyle readObj = new FormattedXMLDataIOStyle (CurrentArray, DataIOStyleAttribs);
             // readObj.setXMLAttributes(DataIOStyleAttribs);
             CurrentArray.setXMLDataIOStyle(readObj);

             DataIOStyleAttribs = new Hashtable(); // clear out table 
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

    class valueCharDataHandlerFunc implements CharDataHandlerAction {
       public void action (SaxDocumentHandler handler, char buf [], int offset, int len) {

          //  grab parent node name
          String parentNodeName = getParentNodeName();
          
          // create new object appropriately 
          Value newvalue = new Value();
          // reconsitute the value node PCdata from passed information.
          // and add value to object
          newvalue.setValue( new String (buf, offset, len) );

          // determine where this goes and then insert it 
          if( parentNodeName.equals(XDFNodeName.PARAMETER) )
          {

              newvalue = LastParameterObject.addValue(newvalue);

          } else if ( parentNodeName.equals(XDFNodeName.AXIS) ) 
          {

              List axisList = (List) CurrentArray.getAxisList();
              Axis lastAxisObject = (Axis) axisList.get(axisList.size()-1);
              newvalue = lastAxisObject.addAxisValue(newvalue);

          } else if ( parentNodeName.equals(XDFNodeName.VALUEGROUP) )

          {

             // nothing here yet

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

          // our string that we will parse
          String valueListString = new String (buf, offset, len);

          String delimiter = (String) CurrentValueListParameter.get("delimiter");
          String repeatable = (String) CurrentValueListParameter.get("repeatable");

          // reconsitute information stored in CurrentValueListParameter table 
          String parentNodeName = (String) CurrentValueListParameter.get("parentNodeName");

// NOT currently complete. Adds values ONLY to axes. Need one for parameter too. 

          // get the last axis
          List axisList = (List) CurrentArray.getAxisList();
          Axis lastAxisObject = (Axis) axisList.get(axisList.size()-1);

          // split up string into values based on declared delimiter
          // and snag the string representation of the values
          ArrayList strValueList = 
              splitStringIntoStringObjects(valueListString, delimiter, repeatable, null );

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

          CurrentValueListParameter.put("isDelimitedCase", "true"); // notify that we did the list 

       }
    }


    // there is undoubtably some code-reuse spots missed in this function.
    // get it later when Im not being lazy. -b.t.
    class valueListStartElementHandlerFunc implements StartElementHandlerAction {
       public Object action (SaxDocumentHandler handler, AttributeList attrs) {
 
           // 1. re-init
           CurrentValueListParameter = new Hashtable(); 

           // 2. populate ValueListparameters w/ parent name 
           String parentNodeName = getParentNodeName();
           CurrentValueListParameter.put("parentNodeName", parentNodeName);

           // 3. populate ValueListparameters from attribute list 
           int size = attrs.getLength(); 
           for (int i = 0; i < size; i++)
           {
               String value; 
               if ((value = attrs.getValue(i)) != null) 
                  CurrentValueListParameter.put(attrs.getName(i), value);
           }

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

          // 2. try to determine values from attributes (e.g. algorithm method)
          ArrayList values = getValueListNodeValues();

          // 3. Populate correct parent node w/ values 
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

             } else if ( parentNodeName.equals(XDFNodeName.VALUEGROUP) )
             {

/*
   ValueGroup lastValueGroup = (ValueGroup)
   CurrentValueGroupList.get(CurrentValueGroupList.size()-1);
   newvalueGroup = lastValueGroup.addValueGroup(newvalueGroup);
*/

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

// Need to do something wi/ ValueObjList HERE


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

