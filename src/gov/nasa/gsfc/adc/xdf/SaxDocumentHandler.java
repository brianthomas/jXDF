
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

// Import needed SAX stuff
import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;

/** 
 */
class SaxDocumentHandler implements DocumentHandler {

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


    // GLOBALs for saving these between dataFormat/read node and later when we 
    // know what kind of DataFormat/DataIOStyle object we really have
    private AttributeList DataIOStyleAttribs;
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
//my $CDATA_IS_ARRAY_DATA; # Tells us when we are accepting char_data as data 
    private Locator TaggedLocatorObj;
    private StringBuffer DATABLOCK;
    private boolean CDATAIsArrayData = false;
    private int MaxDataFormatIndex = 0;     // max allowed index in the DataFormatList[]
    private int CurrentDataFormatIndex = 0; // which dataformat index (in DatFormatList[]) we currently are reading
    private DataFormat DataFormatList[];       // list of CurrentArray.getDataFormatList();
    private int LastFastAxisCoordinate = 0;
    private AxisInterface FastestAxis;

    // lookup tables holding objects that have id/idref stuff
    private Hashtable AxisObj = new Hashtable();
    private Hashtable NoteObj = new Hashtable();

    // DEFAULT settings. We really should be getting these 
    // from the XDF DTD, NOT setting them here.
    private String DefaultValueListDelimiter = " ";
    private String DefaultValueListRepeatable = "yes";
    private int DefaultValueListSize = 1;
    private int DefaultValueListStart = 1;
    private int DefaultValueListStep = 1;

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

    /** Set the structure object that the Reader will parse an InputSource into. 
    */
    public Structure getReaderStructureObj () 
    {
      return XDF;
    }

    /** Get the structure object that the Reader will parse an InputSource into. 
    */
    public Structure setReaderStructureObj (Structure structure)
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

    /** startElement handler.
     */
    public void startElement (String element, AttributeList attrs)
    throws SAXException
    {

        Log.debugln("H_START:["+element+"]");
        

        // add "element" to current path (??) 
        CurrentNodePath.add(element); 

        // if a handler exists, run it, else give a warning
        if ( startElementHandlerHashtable.containsKey(element) ) {

           // run the appropriate start handler
           StartElementHandlerAction event = 
              (StartElementHandlerAction) startElementHandlerHashtable.get(element); 
           event.action(attrs);

        } else {
           Log.warnln("Warning: UNKNOWN NODE ["+element+"] encountered.");
        }
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
           event.action();

        } else {

           // do nothing

        }
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
           event.action(buf,offset,len);

        } else {

           // perhaps we are reading in data at the moment??

           if (DataNodeLevel > 0) {

              CharDataHandlerAction event = new dataCharDataHandlerFunc();
              event.action(buf,offset,len);

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

    }

    // return the element before last 
    private String getParentNodeName () {
     
       String parentNodeName = null;
       int pathSize = CurrentNodePath.size(); 

       if (pathSize > 1) {
          parentNodeName = (String) CurrentNodePath.get((pathSize-2));
       }

       return parentNodeName;

    }

    // return 2 elements before last 
    private String getGrandParentNodeName () {

       String gParentNodeName = null;
       int pathSize = CurrentNodePath.size();

       if (pathSize > 2) {
          gParentNodeName = (String) CurrentNodePath.get((pathSize-3));
       }

       return gParentNodeName;

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

       // Note that we dont treat binary data at all 
       try {
          
Log.errorln("setData:["+thisString+"]["+dataLocator.getAxisLocation(FastestAxis)+"]");

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
    private ArrayList getValueListNodeValues (AttributeList attrs) {

       ArrayList values = new ArrayList();

       // parameters for the algorithm
       int size  = DefaultValueListSize;
       int start = DefaultValueListStart;
       int step  = DefaultValueListStep;

       // Capture any overridding Valuelist attributes
       // into algorithm parameters
       for (int i = 0; i < attrs.getLength(); i++)
       {
           String name = attrs.getName(i);
           if ( name.equals("size") ) { 
               Integer tmp = new Integer (attrs.getValue(i));
               size = tmp.intValue();
           } else if ( name.equals("step")) {
               Integer tmp = new Integer (attrs.getValue(i));
               step = tmp.intValue();
           } else if ( name.equals("start")) {
               Integer tmp = new Integer (attrs.getValue(i));
               start = tmp.intValue();
           } else if ( name.equals("delimiter")) {
              // IF delimiter is defined, then we ARENT using
              // an algorthm, and should exit here without further ado.
              return values;
           }
       }

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
    static class XDFNodeName 
    {
       // *sigh* cant decide if making this hashtable is better or not.
       protected static final String ARRAY = "array";
       protected static final String AXIS = "axis";
       protected static final String AXISUNITS= "axisUnits";
       protected static final String BINARYFLOAT = "binaryFloat";
       protected static final String BINARYINTEGER = "binaryInteger";
       protected static final String DATA = "data";
       protected static final String DATAFORMAT = "dataFormat";
       protected static final String EXPONENT = "exponent";
       protected static final String FIELD = "field";
       protected static final String FIELDAXIS = "fieldAxis";
       protected static final String FIELDRELATIONSHIP = "relation";
       protected static final String FIXED = "fixed";
       protected static final String FORNODE = "for";
       protected static final String FIELDGROUP = "fieldGroup";
       protected static final String INDEX = "index";
       protected static final String INTEGER = "integer";
       protected static final String LOCATIONORDER = "locationOrder";
       protected static final String NOTE = "note";
       protected static final String NOTES = "notes";
       protected static final String PARAMETER = "parameter";
       protected static final String PARAMETERGROUP = "parameterGroup";
       protected static final String ROOT = "XDF"; // beware setting this to the same name as structure 
       protected static final String READ = "read";
       protected static final String READCELL = "readCell";
       protected static final String REPEAT = "repeat";
       protected static final String SKIPCHAR = "skipChars";
       protected static final String STRUCTURE = "structure";
       protected static final String STRING = "string";
       protected static final String TAGTOAXIS = "tagToAxis";
       protected static final String TD0 = "d0";
       protected static final String TD1 = "d1";
       protected static final String TD2 = "d2";
       protected static final String TD3 = "d3";
       protected static final String TD4 = "d4";
       protected static final String TD5 = "d5";
       protected static final String TD6 = "d6";
       protected static final String TD7 = "d7";
       protected static final String TD8 = "d8";
       protected static final String TEXTDELIMITER = "textDelimiter";
       protected static final String UNIT = "unit";
       protected static final String UNITS = "units";
       protected static final String UNITLESS = "unitless";
       protected static final String VALUELIST = "valueList";
       protected static final String VALUE = "value";
       protected static final String VALUEGROUP = "valueGroup";
       protected static final String VECTOR = "unitDirection";

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
       public void action (AttributeList attrs) {

           DelimitedXMLDataIOStyle readObj = new DelimitedXMLDataIOStyle(CurrentArray);
           readObj.setXMLAttributes(attrs);
           CurrentArray.setXMLDataIOStyle(readObj);

           CurrentFormatObjectList.add(readObj);

       }
    }

    // asciiDelimiter node end
    class asciiDelimiterEndElementHandlerFunc implements EndElementHandlerAction {
       public void action () { 
           // pop off last value
           CurrentFormatObjectList.remove(CurrentFormatObjectList.size()-1);
       }
    }


    // ARRAY NODE
    //

    // Array node start 
    class arrayStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 

          // create new object appropriately 
          Array newarray = new Array();
          newarray.setXMLAttributes(attrs); // set XML attributes from passed list 

          // set current array and add this array to current structure 
          CurrentArray = CurrentStructure.addArray(newarray);

          CurrentDatatypeObject = CurrentArray;

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
           
                 } else {
                    Log.warnln("Error: Reader got an axis with AxisIdRef=\""+axisIdRef+"\" but no previous axis has that id! Ignoring add axis request.");
                    return;
                 }
             }

             // add this axis to the current array object
             CurrentArray.addAxis(newaxis);

             // I dont believe this is actually used
             // CurrentDatatypeObject = newaxis;

          } else {
             Log.errorln("Axis object:"+newaxis+" lacks either axisId or axisIdRef, ignoring!");
          }

       }
    }

    // BinaryFloatField 
    //

    class binaryFloatFieldStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 

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


       }
    }


    // BINARYINTEGERFIELD
    //

    class binaryIntegerFieldStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 

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


       }
    }


    // DATATAG
    //

    // REMINDER: these functions only get called when tagged data is being read..

    class dataTagStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 
          CurrentDataTagLevel++;
       }
    }

    class dataTagEndElementHandlerFunc implements EndElementHandlerAction {
       public void action () { 

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
       public void action (char buf [], int offset, int len) {

          XMLDataIOStyle readObj = CurrentArray.getXMLDataIOStyle();

          if ( readObj instanceof TaggedXMLDataIOStyle ) {

             String thisString = new String(buf,offset,len);
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

              String newString = new String(buf,offset,len);
              // add it to the datablock if it isnt all whitespace ?? 
           //   if ( newString.trim().length() > 0 ) {
                  DATABLOCK.append(newString);
           //   }

           } else {
               Log.errorln("UNSUPPORTED Data Node CharData style:"+readObj.toString()+", Aborting!\n");
               System.exit(-1);
           }

       }
    }

    class dataEndElementHandlerFunc implements EndElementHandlerAction {
       public void action () { 

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

AxisInterface axis0 = (AxisInterface) CurrentArray.getAxisList().get(0); 
AxisInterface axis1 = (AxisInterface) CurrentArray.getAxisList().get(1); 

              CurrentDataFormatIndex = 0; 

//              boolean dataHasSpecialIntegers = false;

              // set up appropriate instructions for reading
              if ( formatObj instanceof FormattedXMLDataIOStyle ) {
/*
      $template  = $formatObj->_templateNotation(1);
      $recordSize = $formatObj->bytes();
      $data_has_special_integers = $formatObj->hasSpecialIntegers;

*/
                 Log.errorln("FORMATTED DATA READ NOT SUPPORTED");

              } else {

                 // snag the string representation of the values
                 ArrayList strValueList = splitStringIntoStringObjects( DATABLOCK.toString(), 
                                                ((DelimitedXMLDataIOStyle) formatObj).getDelimiter(), 
                                                ((DelimitedXMLDataIOStyle) formatObj).getRepeatable(), 
                                                ((DelimitedXMLDataIOStyle) formatObj).getRecordTerminator()
                                              );

                 // fire data into dataCube
                 Iterator iter = strValueList.iterator();
                 while (iter.hasNext()) 
                 {

                    DataFormat CurrentDataFormat = DataFormatList[CurrentDataFormatIndex];

                    // adding data based on what type..
                    addDataToCurrentArray(myLocator, (String) iter.next(), CurrentDataFormat);

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

Log.errorln("Location:["+myLocator.getAxisLocation(axis0)+","+myLocator.getAxisLocation(axis1)+"]");

                    myLocator.next();

                 }

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
       public void action (AttributeList attrs) { 

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
          LastFastAxisCoordinate = 0;

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

       }
    }

    // DATAFORMAT 
    //

    class dataFormatStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 

           // save attribs for latter
           DataFormatAttribs = attrs;

       }
    }


    // EXPONENTFIELD
    //

    class exponentFieldStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {

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

       }
    }

    // FIELD
    //

    class fieldStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {

          // create new object appropriately 
          Field newfield = new Field();
          newfield.setXMLAttributes(attrs); // set XML attributes from passed list

          // grab the field axis and add the field 
          FieldAxis fieldAxis = CurrentArray.getFieldAxis();
          fieldAxis.addField(newfield);

/* ID REF cloning STUFF MISSING@!!! -b.t. */

          // add this object to all open field groups
          Iterator iter = CurrentFieldGroupList.iterator();
          while (iter.hasNext()) {
             FieldGroup nextFieldGroupObj = (FieldGroup) iter.next();
             newfield.addToGroup(nextFieldGroupObj);
          }

          CurrentDatatypeObject = newfield;

          LastFieldObject = newfield;

       }
    }

    // FIELDAXIS 
    //

    class fieldAxisStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {

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

                 } else {
                    Log.warnln("Error: Reader got an fieldaxis with AxisIdRef=\""+axisIdRef+"\" but no previous field axis has that id! Ignoring add fieldAxis request.");
                    return;
                 }
             }

             // add this axis to the current array object
             CurrentArray.addFieldAxis(newfieldaxis);

          } else {
             Log.errorln("FieldAxis object:"+newfieldaxis+" lacks either axisId or axisIdRef, ignoring!");
          }

       }
    }


    // FIELDGROUP 
    //

    class fieldGroupEndElementHandlerFunc implements EndElementHandlerAction {
       public void action () {
          // peel off the last object in the field group list
          CurrentFieldGroupList.remove(CurrentFieldGroupList.size()-1);
       }
    }

    class fieldGroupStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {

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

       }
    }

    // FIELDRELATIONSHIP 
    //

    class fieldRelationshipStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {

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

       }
    }

    // FIXEDFIELD
    //

    class fixedFieldStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {

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
       }
    }

    // FOR
    //

    class forStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {
          Log.errorln("FOR Start handler not implemented yet.");
       }
    }

    // INTEGERFIELD
    //

    class integerFieldStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {

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


       }
    }

    // NOTE
    //

    class noteCharDataHandlerFunc implements CharDataHandlerAction {
       public void action (char buf [], int offset, int len) {

          // add cdata as text to the last note object 
          String newText = new String(buf,offset,len);
          LastNoteObject.addText(newText);

       }
    }

    class noteStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {

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

              } else {
                 Log.warnln("Error: Reader got a note with NoteIdRef=\""+noteIdRef+"\" but no previous note has that id! Ignoring add note request.");
                 return;
              }
           }

           LastNoteObject = newnote;


       }
    }


    // NOTEINDEX
    //
    
    class noteIndexStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {

          String axisIdRef = (String) null;
          for (int i = 0 ; i < attrs.getLength(); i++) {
              if (attrs.getName(i).equals("axisIdRef")) { // bad. hardwired axisIdRef name
                 axisIdRef = attrs.getValue(i);
                 break;
              }
          }
            
          if(axisIdRef != null) {
             NoteLocatorOrder.add(axisIdRef);
          }

       }
    }


    // NOTES
    //
    
    class notesEndElementHandlerFunc implements EndElementHandlerAction {
       public void action () {

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
       public void action (AttributeList attrs) {

          // do nothing .. this node doenst have any attributes
          // only child nodes. 

       }
    }


    // NULL
    //

    class nullStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {
           // null means do nothing!!
       }
    }


    // PARAMETER
    //
    
    class parameterStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {

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
            return;
          }

          // add this object to all open groups
          Iterator iter = CurrentParameterGroupList.iterator();
          while (iter.hasNext()) {
             ParameterGroup nextParamGroupObj = (ParameterGroup) iter.next();
             newparameter.addToGroup(nextParamGroupObj);
          }

          LastParameterObject = newparameter;

       }
    }

    // PARAMETERGROUP
    //

    class parameterGroupEndElementHandlerFunc implements EndElementHandlerAction {
       public void action () {
          // peel off the last object in the parametergroup list
          CurrentParameterGroupList.remove(CurrentParameterGroupList.size()-1);
       }
    }

    class parameterGroupStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {

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

       }
    }

    // READ
    //

    class readEndElementHandlerFunc implements EndElementHandlerAction {
       public void action () {

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
       public void action (AttributeList attrs) {

          // save these for later, when we know what kind of dataIOstyle we got
          DataIOStyleAttribs = attrs;

          // clear out the format command object array
          // (its used by Formatted reads only, but this is reasonable 
          //  spot to do this).
          CurrentFormatObjectList = new ArrayList ();

       }
    }

    // READCELL
    //

    class readCellStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {
          Log.errorln("READCELL Start handler not implemented yet.");
       }
    }

    // REPEAT
    //

    class repeatEndElementHandlerFunc implements EndElementHandlerAction {
       public void action () {
          // pop off last value
          CurrentFormatObjectList.remove(CurrentFormatObjectList.size()-1);
       }
    }

    class repeatStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {
          Log.errorln("REPEAT Start handler not implemented yet.");
       }
    }

    // ROOT 
    //

    // Root node start 
    class rootStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 
          // The root node is just a "structure" node,
          // but is always the first one.
          XDF.setXMLAttributes(attrs); // set XML attributes from passed list 
          CurrentStructure = XDF;      // current working structure is now the root 
                                       // structure

          // if this global option is set in the reader, we use it
          if(Options.contains("DefaultDataDimensionSize")) { 
             int value = ((Integer) Options.get("DefaultDataDimensionSize")).intValue();
             XDF.setDefaultDataArraySize(value);
          }
       }
    }

    // SKIPCHAR
    //

    class skipCharStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 
          Log.errorln("SKIPCHAR Start handler not implemented yet.");
       }
    }

    // STRINGFIELD
    //

    class stringFieldStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 

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

       }
    }


    // STRUCTURE
    //

    class structureStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 
          Log.errorln("STRUCTURE Start handler not implemented yet.");
       }
    }

    // TAGTOAXIS
    //

    // Our purpose here: configure the TaggedXMLDataIOStyle with axis/tag associations.
    class tagToAxisStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 

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
          for (int i = 0; i < attrs.getLength(); i++)
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

       }
    }

    // UNIT
    //

    class unitCharDataHandlerFunc implements CharDataHandlerAction {
       public void action (char buf [], int offset, int len) {

          LastUnitObject.setValue(new String(buf,offset,len));

       }
    }

    class unitStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 

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

              ArrayList axisList = (ArrayList) CurrentArray.getAxisList();
              Axis lastAxisObject = (Axis) axisList.get(axisList.size()-1);
              newunit = lastAxisObject.addUnit(newunit);

          } else if ( gParentNodeName.equals(XDFNodeName.ARRAY) )
          {

              newunit = CurrentArray.addUnit(newunit);

          } else {
              Log.warnln("Unknown grandparent object, cant add unit, ignoring.");
          }

          LastUnitObject = newunit;
       }
    }

    // VALUE 
    //

    class valueCharDataHandlerFunc implements CharDataHandlerAction {
       public void action (char buf [], int offset, int len) {

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
       public void action () {
          CurrentValueGroupList.remove(CurrentValueGroupList.size()-1);
       }
    }

    class valueGroupStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {

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

       }
    }

    // VALUELIST 
    //

    class valueListCharDataHandlerFunc implements CharDataHandlerAction {
       public void action (char buf [], int offset, int len) {

          String valueListString = new String (buf, offset, len);
          // get the last axis
          List axisList = (List) CurrentArray.getAxisList();
          Axis lastAxisObject = (Axis) axisList.get(axisList.size()-1);

          // split up string into values based on declared delimiter
          String delimiter = (String) CurrentValueListParameter.get("delimiter");
          String repeatable = (String) CurrentValueListParameter.get("repeatable");

          // snag the string representation of the values
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

       }
    }


    // there is undoubtably some code-reuse spots missed in this function.
    // get it later when Im not being lazy. -b.t.
    class valueListStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {
 
           // 1. grab parent node name
           String parentNodeName = getParentNodeName();

           // 2. try to determine values from attributes (e.g. algorithm method)
           ArrayList values = getValueListNodeValues(attrs);


           // 3. IT could be that no values exist because they are stored
           // in PCDATA rather than as algorithm (treat in char data handler
           // in this case).
           if(values.size() > 0 ) { // algoritm case 
 
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
                   Log.errorln("Error: unknown valueGroupParent "+LastValueGroupParentObject+
                               " cant treat for "+XDFNodeName.VALUELIST);
                   System.exit(-1); // fatal error, shut down 

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

              // add these new value objects to all open groups
              Iterator iter1 = CurrentValueGroupList.iterator();
              Iterator iter2 = valueObjList.iterator();
              while (iter1.hasNext()) 
              {
                 ValueGroup nextValueGroupObj = (ValueGroup) iter1.next();
                 while (iter2.hasNext()) 
                 {
                    Value nextValueObj = (Value) iter2.next();
                    nextValueObj.addToGroup(nextValueGroupObj);
                 }
                 // reset iter2
                 iter2 = valueObjList.iterator();
              }

           } else { // PCDATA case


              String delimiter = DefaultValueListDelimiter;
              String repeatable = DefaultValueListRepeatable;

              // pickup overriding values from attribute list
              for (int i = 0; i < attrs.getLength(); i++)
              {
                   String name = attrs.getName(i); 
                  if ( name.equals("delimiter") ) { 
                     delimiter = attrs.getValue(i);
                  } else if ( name.equals("repeatable")) {
                     repeatable = attrs.getValue(i);
                  }
              }

              CurrentValueListParameter = new Hashtable(); // re-init

              CurrentValueListParameter.put("parentNode", parentNodeName);
              CurrentValueListParameter.put("delimiter", delimiter);
              CurrentValueListParameter.put("repeatable", repeatable);

           }
       }
    }

    // VECTOR
    //

    class vectorStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {
          Log.errorln("VECTOR Start handler not implemented yet.");
       }
    }


} // End of SaxDocumentHandler class 

/* Modification History:
 *
 * $Log$
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

