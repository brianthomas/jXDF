
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
    private String    CurrentDatatypeObject;
    private ArrayList CurrentNodePath = new ArrayList();
    private ArrayList CurrentParameterGroupList = new ArrayList();
    private ArrayList CurrentValueGroupList = new ArrayList();

    // References recording the last object of these types created while
    // parsing the document
    private Parameter LastParameterObject;
    private Note      LastNoteObject;
    // private Notes     LastNotesObject;
    private Unit      LastUnitObject;
    private Units     LastUnitsObject;

    private Object LastParameterGroupParentObject;
    private Object LastValueGroupParentObject;

    // keeping track of valueList settings
    private Hashtable CurrentValueListParameter;

    // Data writing stuff
    private int DataNodeLevel = 0; // how deeply nested we are within data nodes 

    // lookup tables holding objects that have id/idref stuff
    private Hashtable AxisObj = new Hashtable();

    // DEFAULT settings. We really should be getting these 
    // from the XDF DTD, NOT setting them here.
    private String DefaultValueListDelimiter = " ";
    private String DefaultValueListRepeatable = "yes";
    private int DefaultValueListSize = 1;
    private int DefaultValueListStart = 1;
    private int DefaultValueListStep = 1;



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
           Log.warn("Warning: UNKNOWN NODE ["+element+"] encountered.\n");
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
        // Log.debugln("H_CharData:["+new String(buf,offset,len)+"]");

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

       //        &data_node_charData($string)
               Log.errorln("Data Character Data NOT yet implemented");

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
        // Log.debugln("Whitespace:["+new String(buf,offset,len)+"]");

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
       startElementHandlerHashtable.put(XDFNodeName.AXISUNITS, new axisUnitsStartElementHandlerFunc());
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
          Log.errorln("Ascii Delimiter Start handler not implemented yet.");
       }
    }

    // asciiDelimiter node end
    class asciiDelimiterEndElementHandlerFunc implements EndElementHandlerAction {
       public void action () { System.out.println("Ascii Delimiter end node handler not implemented yet."); }
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

          CurrentDatatypeObject = "CurrentArray";

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
                    Log.warnln("More than one axis node with axisId=\""+axisId+"\", using latest node.\n" ); 

                 // add this into the list of axis objects
                 AxisObj.put(axisId, newaxis);

             }

             //  If there is a reference object, clone it to get
             //  the new axis
             if (axisIdRef != null) {

                 if (AxisObj.containsKey(axisIdRef)) {

                    BaseObject refAxisObj = (BaseObject) AxisObj.get(axisIdRef);
                    newaxis = (Axis) refAxisObj.clone();

                    // override attrs with those in passed list
                    newaxis.setXMLAttributes(attrs);
           
                 } else {
                    Log.errorln("Error: Reader got an axis with AxisIdRef=\""+axisIdRef+"\" but no previous axis has that id! Ignoring add axis request.");
                    return;
                 }
             }

             // add this axis to the current array object
             CurrentArray.addAxis(newaxis);

             CurrentDatatypeObject = "LastAxis";

          } else {
             Log.errorln("Axis object:"+newaxis+" lacks either axisId or axisIdRef, ignoring!");
          }

       }
    }

    // AXIS UNITS
    //

    class axisUnitsStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 
          Log.errorln("AXIS UNITS Start handler not implemented yet.");
       }
    }

    // BinaryFloatField 
    //

    class binaryFloatFieldStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 
          Log.errorln("Binary Float Field Start handler not implemented yet.");
       }
    }


    // BinaryIntegerField 
    //

    class binaryIntegerFieldStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 
          Log.errorln("Binary Integer Field Start handler not implemented yet.");
       }
    }


    // Datatag 
    //

    class dataTagStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 
          Log.errorln("DATATAG Start handler not implemented yet.");
       }
    }

    class dataTagEndElementHandlerFunc implements EndElementHandlerAction {
       public void action () { 
          Log.errorln("DATATAG End handler not implemented yet.");
       }
    }

    // DATA
    //

    class dataCharDataHandlerFunc implements CharDataHandlerAction {
       public void action (char buf [], int offset, int len) {
          System.out.println("DATA NODE Char Data Handler NOT implemented yet.");
       }
    }

    class dataEndElementHandlerFunc implements EndElementHandlerAction {
       public void action () { 
          Log.errorln("DATA End handler not implemented yet.");
       }
    }

    class dataStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 
          Log.errorln("DATA Start handler not implemented yet.");
       }
    }

    // DATAFORMAT 
    //

    class dataFormatStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 
          Log.errorln("DATAFORMAT Start handler not implemented yet.");
       }
    }


    // EXPONENTFIELD
    //

    class exponentFieldStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {
          Log.errorln("EXPONENTFIELD Start handler not implemented yet.");
       }
    }

    // FIELD
    //

    class fieldStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {
          Log.errorln("FIELD Start handler not implemented yet.");
       }
    }

    // FIELDAXIS 
    //

    class fieldAxisStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {
          Log.errorln("FIELDAXIS Start handler not implemented yet.");
       }
    }


    // FIELDGROUP 
    //

    class fieldGroupEndElementHandlerFunc implements EndElementHandlerAction {
       public void action () {
          Log.errorln("FIELDGROUP End handler not implemented yet.");
       }
    }

    class fieldGroupStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {
          Log.errorln("FIELDGROUP Start handler not implemented yet.");
       }
    }

    // FIELDRELATIONSHIP 
    //

    class fieldRelationshipStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {
          Log.errorln("FIELDRELATIONSHIP Start handler not implemented yet.");
       }
    }

    // FIXEDFIELD
    //

    class fixedFieldStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {
          Log.errorln("FIXEDFIELD Start handler not implemented yet.");
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
          Log.errorln("INTEGERFIELD Start handler not implemented yet.");
       }
    }

    // NOTE
    //

    class noteCharDataHandlerFunc implements CharDataHandlerAction {
       public void action (char buf [], int offset, int len) {
          System.out.println("NOTE Char Data Handler NOT implemented yet.");
       }
    }

    class noteStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {
          Log.errorln("NOTE Start handler not implemented yet.");
       }
    }


    // NOTEINDEX
    //
    
    class noteIndexStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {
          Log.errorln("NOTEINDEX Start handler not implemented yet.");
       }
    }


    // NOTES
    //
    
    class notesEndElementHandlerFunc implements EndElementHandlerAction {
       public void action () {
          Log.errorln("NOTES End handler not implemented yet.");
       }
    }

    class notesStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {
          Log.errorln("NOTES Start handler not implemented yet.");
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
           // newparameter = LastParameterGroupParentObject.addParameter(newparameter);

          } else {
            Log.errorln("Error: weird parent node "+parentNodeName+" for parameter");
            System.exit(-1); // fatal error, shut down 
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
              LastParameterGroupParentObject = CurrentArray;

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
          Log.errorln("READ End handler not implemented yet.");
       }
    }

    class readStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {
          Log.errorln("READ Start handler not implemented yet.");
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
          Log.errorln("REPEAT End handler not implemented yet.");
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
          Log.errorln("STRINGFIELD Start handler not implemented yet.");
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

    class tagToAxisStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 
          Log.errorln("TAGTOAXIS Start handler not implemented yet.");
       }
    }

    // UNIT
    //

    class unitCharDataHandlerFunc implements CharDataHandlerAction {
       public void action (char buf [], int offset, int len) {
          System.out.println("UNIT Char Data Handler NOT implemented yet.");
       }
    }

    class unitStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) { 
          Log.errorln("UNIT Start handler not implemented yet.");
       }
    }

    // VALUE 
    //

    class valueCharDataHandlerFunc implements CharDataHandlerAction {
       public void action (char buf [], int offset, int len) {

          // 1. grab parent node name
          String parentNodeName = getParentNodeName();
          
          // 2. create new object appropriately 
          Value newvalue = new Value();
          // reconsitute the value node PCdata from passed information.
          // and add value to object
          newvalue.setValue( new String (buf, offset, len) );

          // 3. determine where this goes and then insert it 
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

          // 4. add this object to all open groups
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

          // split up string based on declared delimiter
/*
  my $delimiter = '/' . $CURRENT_VALUELIST{'delimiter'};
  if ($CURRENT_VALUELIST{'repeatable'} eq 'yes') {
    $delimiter .= '+/';
  } else {
    $delimiter .= '/';
  }
  my @values;
  eval " \@values = split $delimiter, \$string ";
*/

       }
    }

    class valueListStartElementHandlerFunc implements StartElementHandlerAction {
       public void action (AttributeList attrs) {
 
           // 1. grab parent node name
           String parentNodeName = getParentNodeName();

           // 2. try to determine values from attributes (e.g. algorithm method)
           ArrayList values =  getValueListNodeValues(attrs);

           // IT could be that no values exist because they are stored
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
Log.errorln("ADD AXIS VALUE:["+valuePCDATA+"]");
                    }

              } else if ( parentNodeName.equals(XDFNodeName.VALUEGROUP) )
              {

                   /*
                    ValueGroup lastValueGroup = (ValueGroup)
                         CurrentValueGroupList.get(CurrentValueGroupList.size()-1);
                     newvalueGroup = lastValueGroup.addValueGroup(newvalueGroup);
                   */

/*
        my $method;
        if (ref($LAST_VALUEGROUP_PARENT_OBJECT) eq 'XDF::Parameter') {
           $method = "addValue";
        } elsif (ref($LAST_VALUEGROUP_PARENT_OBJECT) eq 'XDF::Axis') {
           $method = "addAxisValue";
        } else {
           my $name = ref($LAST_VALUEGROUP_PARENT_OBJECT);
           die " ERROR: UNKNOWN valueGroupParent object ($name), can't treat for valueList.\n";
        }

        foreach my $val (@values) {
           push @valueObjList, $LAST_VALUEGROUP_PARENT_OBJECT->$method($val);
        }

*/

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
 * Revision 1.2  2000/10/26 20:42:33  thomas
 * Another interim version. Putting into CVS so I can
 * sync w/ kellys other changes easier. -b.t.
 *
 * Revision 1.1  2000/10/25 17:57:00  thomas
 * Initial Version. -b.t.
 *
 * 
 */

