
// XDF BaseObject Class
// CVS $Id$

// BaseObject.java Copyright (C) 2000 Brian Thomas,
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

import java.util.*;
import java.io.*;
import org.xml.sax.AttributeList;

/** The base class for most XDF objects.
    XDF is the eXtensible Data Structure, which is an XML format designed
    to contain n-dimensional scientific/mathematical data.
    A diagram of the XDF object structure is included in the package.

    The BaseObject class provides a generalized means of storing, retrieving
    and writing out the XML-based properties of the XDF objects. It also
    provides fields/methods to allow all inheriting XDF objects be
    members of Group objects. Key parts to the BaseObject class include the
    XMLAttributes and the toXDF* methods it provides.
 */
public abstract class BaseObject implements Serializable {

  //
  // Fields
  //

  /** Stores whether nicely formatted XDF should be output from any toXDF*
      method. Nice formatting includes nested indentation and return characters
      to improve human readability of output XDF (but blows up the size of
      the XDF file!).
  */
  public static boolean sPrettyXDFOutput = false;

  /** The indentation string that will be used for every nesting level within an
      output XDF. For example, if the sPrettyXDFOutputIndentation string consists
      of 3 spaces, then a doubly nested node will be indented 6 spaces, its parent
      node will be indented 3 spaces and the root node will not be indented at all.
      You can be creative with the indentation: any sequence of characters is valid
      (no need to just use spaces!).
  */
  public static String sPrettyXDFOutputIndentation = "  ";

  /** The default allocation size for each dimension within all XDF arrays.
      The practical meaning of this field is that it indicates the initial
      size of each XDF Axis/FieldAxis (the number of axis values/fields along
      the axis) and the number of data cells within a dimension of the XDF
      DataCube object. If more axis values/fields/datacells are placed on a
      given Axis/FieldAxis or data in a unallocated spot within the dataCube
      then the package allocates the needed memory and enlarges the
      DataCube/Axis/FieldAxis objects as it is needed.

      This automated allocation is slow however, so it is desirable, IF you
      know how big your arrays will be, to pre-set this value to encompass your
      data set. Doing so will to improve efficency in some cases. Note that if
      you are having keeping all of your data in memory (a multi-dimensional
      dataset) it may be desirable to DECREASE the value.
  */
  public static int sDefaultDataArraySize = 1000;

  /** The XDF (element) node name for a given XDF class. Not all XDF classes
      have a defined node name (ie map to the XDF DTD). The BaseObject class
      is an example of this case.
  */
  protected String classXDFNodeName;

  /** The version of XML that will be output from a toXDF* method call.
  */
  private static String sXMLSpecVersion = "1.0";

  /** The root node name for any XDF document. The root node is a structure
      node with a different name as specified here.
  */
  private static String sXDFRootNodeName = "XDF";

  /** The XDF node name for the structure class.
  */
  private static String sXDFStructureNodeName = "structure"; // bad. we should call the
                                                             // class to find this out. -b.t.

  /** The name of the relevant version of XDF DTD file for this package.
  */
  private static String sXDFDTDName = "XDF_0.17.dtd";

  /** The name of the XMLAttribute which is written out as PCDATA rather than as
      a node attribute (String/Number type XMLAttributes) or child node (Object
      and List type XMLAttributes). At this time only String-type XMLAttributes
      should be named 'value' (yes, it would be an interesting experiment to call
      an Object-type XMLAttribute 'value'!).
  */
  private static String sPCDATAAttribute = "value";

  /** openGroupNodeHash is an internal field used by the toXDF* methods to track
      which group nodes are still open. Probably should go away.
  */
  protected Set openGroupNodeHash = Collections.synchronizedSet(new HashSet());

  /** This field stores object references to those group objects to which a given
      object belongs.
  */
  protected Set groupMemberHash = Collections.synchronizedSet(new HashSet());

  /** A Hashtable to hold the XML attributes.
  */
  protected Hashtable attribHash;

  /** A List to store the order of the XML attributes.
  */
  protected List attribOrder;


  //
  // Constructor Methods
  //

  /** The no-argument constructor.
  */
  public BaseObject() {

    // The heart of the baseObject is that it manages the storage, retrieval
    // and writing out the XMLAttributes for the XDF objects.
    // There are 2 parts to making the XMLAttributes of the base Object
    // work properly: a lookup table of key/value pairs in attribHash and a
    // list containing the  proper order of the attributes.

    // initialize
    attribHash  = new Hashtable(Constants.INIT_ATTRIBUTE_HASH_SIZE);
    attribOrder = Collections.synchronizedList(new ArrayList());

  }

  //
  // Get/Set Methods
  //




  /** The class XDF node name may (or may not exist) for a given XDF java object.
     @return: String on success, null (String Object) on if the node name doesnt exist.
  */
  public String getClassXDFNodeName() {
    return classXDFNodeName;
  }

  /** Return the hashtable of XMLAttribute names and their values.
      @return: Hashtable on success, an empty hashtable is passed back if their
               are no XMLAttributes within a given XDF object class.
  */
  public Hashtable getAttribHash() {
    return attribHash;
  }

  /**set the attributes of this object from the passed attribute Hash reference
   *
   */
  public Hashtable setAttribHash(Hashtable hash) {
    attribHash = hash;
    return attribHash;
  }

  /** Return a list of the proper ordering of the XML attributes of this object.
      @return: List on success, on failure an empty List object is passed back if
               there are no XMLAttributes within a XDF given object class.
  */
  public List getAttribOrder() {
    return attribOrder;
  }

  /** Get the output XDF format style.
      @return: the value of sPrettyXDFOutput field  (which is true if nicely formatted
               XML is to be outputted from any call to a toXDF* method, false if not).
  */
  public static boolean getPrettyXDFOutput() {
    return sPrettyXDFOutput;
  }

  /** Set this to true for nicely formatted XML output from any call to a toXDF* method.
      Setting this value will change the runtime behavior of all XDF Objects within an
      application.
      @return: the value of sPrettyXDFOutput field.
  */
  public static boolean setPrettyXDFOutput (boolean turnOnPrettyOutput) {
    sPrettyXDFOutput = turnOnPrettyOutput;
    return sPrettyXDFOutput;
  }

  /**
      Gets the indentation string that will be used for every nesting level within an
      output XDF. For example, if the string consists of 3 spaces, then a doubly nested
               node will be indented 6 spaces, its parent node will be indented 3 spaces and the
               root node will not be indented at all.
      @return: String object containing XDF output indentation.
  */
  public static String getPrettyXDFOutputIndentation() {
    return sPrettyXDFOutputIndentation;
  }

  /** Set the indentation string for PrettyXDFOutput. You aren't limited to just spaces
     here, ANY sequence of characters may be used to indent your XDF documents.
      @return: String object containing XDF output indentation.
  */
  public static String setPrettyXDFOutputIndentation(String indentString) {
    sPrettyXDFOutputIndentation = indentString;
    return sPrettyXDFOutputIndentation;
  }

  /** Get the default allocation size of each dimension within all XDF arrays.
      @return: non-negative integer with the dimension size.
  */
  public static int getDefaultDataArraySize(){
    return sDefaultDataArraySize;
  }

  /** Set the default allocation size of each dimension within all XDF arrays.
      @return: non-negative integer with the dimension size.
  */
  public static int setDefaultDataArraySize(int arraySize) {
    sDefaultDataArraySize = arraySize;
    return sDefaultDataArraySize;
  }

  /** Get the XML version of this package.
      This cooresponds to the XML spec version that this package
      uses to write out XDF.
  */
  public static String getXMLSpecVersion() {
    return sXMLSpecVersion;
  }

  /** Get the root node name for XDF.
  */
  public static String getXDFRootNodeName() {
    return sXDFRootNodeName;
  }

  /** Get the name of the XDF DTD to which this package corresponds.
  */
  public static String getXDFDTDName() {
    return sXDFDTDName;
  }

  /** Get the name of the XMLAttribute which will be written out as PCDATA.
  */
  public static String getPCDATAAttribute() {
    return sPCDATAAttribute;
  }

  /** Get the Hashtable containing the XMLAttributes for this object.
  */
  public Hashtable getXMLAttributes() {
    return attribHash;
  }

  //
  // Other Public Methods
  //

  /**set the attributes of this object from the passed attribute Hash reference
   * the same as setAttribHash()
   */
  public Hashtable update(Hashtable hash) {
    return setAttribHash(hash);
  }

  /** Add this object to the indicated Group object.
      @return: Group added to on success, null (Group Object) on failure.
  */
  public Group addToGroup(Group groupObject) {

    if (groupObject != null) {
      if (groupMemberHash.add(groupObject)) {  //add in successful
        groupObject.addMemberObject(this);
        return groupObject; // bad, should return based on success of adding
      } else {
        Log.error("Can't add to group.  already a member of the group" +
                           groupObject.getName());
        return null;
      }
    } else {
      Log.error("Error: the group object to add to is null");
      return null;
    }

  }

  /** Remove this object from the indicated Group object.
      @return: Group removed from on success, null (Group Object) on failure.
  */
  public Group removeFromGroup (Group groupObject) {
    if (groupObject != null)  {
      if (groupMemberHash.contains(groupObject))  {
        //this object does belong to the indicated Group object
        groupObject.removeMemberObj(this);
        groupMemberHash.remove(groupObject);
        return groupObject;
      }
      else {
        Log.error("Can't delete from group.  not a member of the group" +
                            groupObject.getName());
        return null;
      }

    }
    else {
      Log.error("Error: The group to add to is null");
      return null;
    }
  }

  /** Determine if this object is a member of the indicated Group object.
      @return: true is it is a member, false if it is not.
  */
  public boolean isGroupMember(Group groupObject) {
    if ( (groupObject != null) && groupMemberHash.contains(groupObject))
      return true;
    else
      return false;
  }

  /** Write this object out to the indicated file. The file will be clobbered
      by the output, so it is advisable to check for the existence of the file
      *before* using this method if you are worried about losing prior information.
      Uses toXDFOutputStream. The passed hashtable will be used to initialize the
      attributes of the XML declaration in the output XDF file.
  */
  public void toXDFFile (String filename, Hashtable XMLDeclAttribs) {

    // open file writer
    try {
      FileOutputStream fileout = new FileOutputStream(filename);
      toXDFOutputStream(fileout, XMLDeclAttribs);
      fileout.close();
    } catch (IOException e) {
      Log.error("Error: toXDFFile method hash trouble writing to "+ filename + " for writing.");
    }

  }

  /** A different invokation style. It has defaults for the XML Declaration
      setting standalone to "no" and version to the value of sXMLSpecVersion.
  */
  public void toXDFFile (String filename) {

     // prepare XMLDeclaration
     Hashtable XMLDeclAttribs = new Hashtable();
     XMLDeclAttribs.put("standalone", new String("no"));
     XMLDeclAttribs.put("version", (String) sXMLSpecVersion);

     toXDFFile(filename, XMLDeclAttribs);

  }

  /** Write this object and all the objects it owns to the supplied
      OutputStream object as XDF. Supplying a populated XMLDeclAttributes
      Hashtable will result in the xml declaration being written at the
      begining of the outputstream (so when you do this, you will
      get well-formmed XML output for ANY object). For obvious reasons, only
      Structure objects will create *valid XDF* output.
  */
  public void toXDFOutputStream (
                                   OutputStream outputstream,
                                   Hashtable XMLDeclAttribs,
                                   String indent,
                                   boolean dontCloseNode,
                                   String newNodeNameString,
                                   String noChildObjectNodeName
                                )
  {

    String nodeNameString = this.classXDFNodeName;

    // Setup. Sometimes the name of the node we are opening is different from
    // that specified in the classXDFNodeName (*sigh*)
    if (newNodeNameString != null) nodeNameString = newNodeNameString;

    // 0. To be valid XML, we always start an XML block with an
    //    XML declaration (e.g. somehting like "<?xml standalone="no"?>").
    //    Here we deal with  printing out XML Declaration && its attributes
    if (!XMLDeclAttribs.isEmpty()) {
        indent = "";
        writeXMLDeclToOutputStream(outputstream, XMLDeclAttribs);
    } 

    // 1. open this node, print its simple XML attributes
    if (nodeNameString != null) {

      if (sPrettyXDFOutput) writeOut(outputstream, indent); // indent node if desired

      // For printing the opening statement we need to invoke a little
      // Voodoo to keep the DTD happy: the first structure node is always
      // called by the root node name instead of the usual nodeNameString
      // We can tell this by checking if this object is derived from class
      // Structure and if XMLDeclAttrib defined/populated with information

      // NOTE: This isnt really the way to do this. We need to check if 'this' is
      // is or has as a superclass xdf.Structure instead of the 'string check' below.

      // check is class Strucuture & XMLDeclAttribs populated?
      if ( nodeNameString.equals(sXDFStructureNodeName) && !XMLDeclAttribs.isEmpty() )
      {
        writeOut(outputstream,"<" + sXDFRootNodeName); // print opening root node statement
      } else {
        writeOut(outputstream,"<" + nodeNameString);   // print opening statement
      }

    }

    // gather info about XMLAttributes in this object/node
    Hashtable xmlInfo = getXMLInfo();

    // 2. Print out string object XML attributes EXCEPT for the one that
    //    matches PCDATAAttribute.
    ArrayList attribs = (ArrayList) xmlInfo.get("attribList");
    // is synchronized here correct?
    synchronized(attribs) {
      for (int i = 0; i < attribs.size(); i++) {
        Hashtable item = (Hashtable) attribs.get(i);
        writeOut(outputstream, " "+ item.get("name") + "=\"" + item.get("value") + "\"");
      }
    }

    // 3. Print out Node PCData or Child Nodes as specified by object ref
    //    XML attributes. The way this stuff occurs will also affect how we
    //    close the node.
    ArrayList childObjs = (ArrayList) xmlInfo.get("childObjList");
    String pcdata = (String) xmlInfo.get("PCDATA");

    if ( childObjs.size() > 0 || pcdata != null || noChildObjectNodeName != null)
    {

      // close the opening tag
      if (nodeNameString != null) {
        writeOut(outputstream, ">");
        if (sPrettyXDFOutput && pcdata == null) writeOut(outputstream, Constants.NEW_LINE);
      }

      // deal with object/list XML attributes, if any in our list
      for (int i = 0; i < childObjs.size(); i++) {
        Hashtable item = (Hashtable) childObjs.get(i);

        if (item.get("type") == Constants.LIST_TYPE)
        {

          List objectList = (List) item.get("value");
          // Im not sure this synchronized wrapper is needed, we are
          // only accessing stuff here.. Also, should synchronzied wrapper
          // occur back in the getXMLInfo method instead where the orig
          // access occured?!?
          synchronized(objectList) {
            Iterator iter = objectList.iterator(); // Must be in synchronized block
            while (iter.hasNext()) {
              BaseObject containedObj = (BaseObject) iter.next();
              if (containedObj != null) { // can happen from pre-allocation of axis values, etc (?)
                String newindent = indent + sPrettyXDFOutputIndentation;
                dealWithOpeningGroupNodes(containedObj, outputstream, indent);
                dealWithClosingGroupNodes(containedObj, outputstream, indent);
                containedObj.toXDFOutputStream(outputstream, new Hashtable(), newindent);
              }
            }
          }
        }
        else if (item.get("type") == Constants.OBJECT_TYPE)
        {
          BaseObject containedObj = (BaseObject) item.get("value");
          if (containedObj != null) { // can happen from pre-allocation of axis values, etc (?)
            // shouldnt this be synchronized too??
            synchronized(containedObj) {
              String newindent = indent + sPrettyXDFOutputIndentation;
              dealWithOpeningGroupNodes(containedObj, outputstream, indent);
              dealWithClosingGroupNodes(containedObj, outputstream, indent);
              containedObj.toXDFOutputStream(outputstream, new Hashtable(), newindent);
            }
          }
        } else {
          // error: weird type, actually shouldnt occur. Is this needed??
          Log.error("Weird error: unknown XML attribute type for item:"+item);
        }

      }

      // print out PCDATA, if any
      if(pcdata != null)  {
        writeOut(outputstream, pcdata);
        //write out closing node after PCDATA, leaving no space between them.
        //k.z. 10/18/2000
        if (!dontCloseNode)
          if ( nodeNameString.equals(sXDFStructureNodeName) && !XMLDeclAttribs.isEmpty() )
          {
            writeOut(outputstream, "</"+sXDFRootNodeName+">");
          } else {
            writeOut(outputstream, "</"+nodeNameString+">");
          }
         if (sPrettyXDFOutput) writeOut(outputstream, Constants.NEW_LINE);
         return ; //now we are done, return!
        //k.z. 10/18/2000
      };

      // if there are no PCDATA or child objects/nodes then
      // we print out noChildObjectNodeName
      if ( childObjs.size() == 0 && pcdata == null && noChildObjectNodeName != null)
      {
        if (sPrettyXDFOutput) writeOut(outputstream, indent);
        writeOut(outputstream, "<" + noChildObjectNodeName + "/>");
        if (sPrettyXDFOutput) writeOut(outputstream, Constants.NEW_LINE);

      }

      // ok, now deal with closing the node
      if (nodeNameString != null) {
        // Needed??
        //dealWithClosingGroupNodes(containedObj, outputstream, indent);

        //Brian: we have to fix this, no whitespace should be allowed between
        //PCDATA and closing node
        if (sPrettyXDFOutput) writeOut(outputstream, indent);
        if (!dontCloseNode)
          if ( nodeNameString.equals(sXDFStructureNodeName) && !XMLDeclAttribs.isEmpty() )
          {
            writeOut(outputstream, "</"+sXDFRootNodeName+">");
          } else {
            writeOut(outputstream, "</"+nodeNameString+">");
          }

      }

    } else {

      if(dontCloseNode) {
         // it may not have sub-objects, but we dont want to close it
         // (happens for group objects)
      } else {
        // no sub-objects, just close this node
        writeOut(outputstream, "/>");
      }

    }

    if (sPrettyXDFOutput) writeOut(outputstream, Constants.NEW_LINE);

  }

  /** A different invokation style for writing this object out to
      the indicated OutputStream.
  */
  public void toXDFOutputStream ( OutputStream outputstream,
                                  Hashtable XMLDeclAttribs,
                                  String indent
                                )
  {
     toXDFOutputStream(outputstream, XMLDeclAttribs, indent, false, null, null);
  }

  /** A different invokation style for writing this object out to
      the indicated OutputStream.
  */
  public void toXDFOutputStream (OutputStream outputstream, Hashtable XMLDeclAttribs)
  {
     //not reseanable to set the indent to sPrettyXDFOutputIndentation --k.z. 10/17
     //toXDFOutputStream(outputstream, XMLDeclAttribs, sPrettyXDFOutputIndentation, false, null, null);
     toXDFOutputStream(outputstream, XMLDeclAttribs,"", false, null, null);
  }

  /** A different invokation style. It has defaults for the XML Declaration
      setting standalone to "no" and version to the value of sXMLSpecVersion.
  */
  public void toXDFOutputStream (OutputStream outputstream, String indent)
  {
     // prepare XMLDeclaration
     Hashtable XMLDeclAttribs = new Hashtable();
     XMLDeclAttribs.put("standalone", new String("no"));
     XMLDeclAttribs.put("version", (String) sXMLSpecVersion);
     toXDFOutputStream(outputstream, XMLDeclAttribs, indent);
  }

  /** A different invokation style. It has defaults for the XML Declaration
      setting standalone to "no" and version to the value of sXMLSpecVersion.
      Indentation starts as "".
  */
  public void toXDFOutputStream (OutputStream outputstream)
  {

     // prepare XMLDeclaration
     Hashtable XMLDeclAttribs = new Hashtable();
     XMLDeclAttribs.put("standalone", new String("no"));
     XMLDeclAttribs.put("version", (String) sXMLSpecVersion);

     toXDFOutputStream(outputstream, XMLDeclAttribs);



  }

  /** Clone an XDF object.
   */
  protected Object clone () {
 
     BaseObject cloneObj = null;

     try {
       cloneObj = (BaseObject) super.clone();

Log.errorln("CLONING Orig:"+this+" Clone:"+cloneObj);

       // Clone the fields

       // XMLAttributes Clone
       cloneObj.attribHash = new Hashtable();
       Enumeration keys = this.attribHash.keys();
       while ( keys.hasMoreElements() )
       {
           Object key = keys.nextElement();
           XMLAttribute XMLAttributeValue = (XMLAttribute) this.attribHash.get((String) key);
           cloneObj.attribHash.put((String) key, XMLAttributeValue.clone()); 
       }


//       cloneObj.classXDFNodeName = this.classXDFNodeName;
//       cloneObj.attribOrder = this.attribOrder;
//       cloneObj.groupMemberHash = Collections.synchronizedSet(new HashSet());
//       cloneObj.openGroupNodeHash = Collections.synchronizedSet(new HashSet());
       
  /** This field stores object references to those group objects to which a given
      object belongs.
  */    


     } catch (java.lang.CloneNotSupportedException e) {
        Log.errorln("Error: Clone not supported by class "+this.getClass().toString());
     }

     return (Object) cloneObj;
  }

  //
  // PROTECTED Methods
  //

  /** A little convience method to save coding time elsewhere.
      This method initializes the XDF attributes of an object from a
      passed Hashtable.
      Hashtable key/value pairs coorespond to the class XDF attribute
      names and their desired values.
  */
  protected void hashtableInitXDFAttributes (Hashtable InitAttributeTable)
  {

    Object attribute;
    Object obj;

    for (int i = 0; i < attribOrder.size(); i++)
    {
      attribute  = attribOrder.get(i);
      obj = InitAttributeTable.get(attribute);

      // only if object exists
      if (obj != null) {
        XMLAttribute toRemove = (XMLAttribute) attribHash.remove(attribute);
        attribHash.put(attribute, new XMLAttribute(obj, toRemove.getAttribType()));
      }

    }

  }

  /** A little convience method to save coding time elsewhere.
     Pass in an object to be removed from the indicated list.
     @return: true on success, false on failure
  */
  protected boolean removeFromList ( Object what, List fromList, String listName ) {
    if (fromList !=null) {
      if ( what !=null) {
        int index = fromList.indexOf(what);
        if (index !=-1) {  //object to be removed is found in the list
          fromList.remove(index);
          return true;
        }
        else {  //cant find the object in the list
          Log.warn("warn: can't find object in" + listName +
                             ", ignoring remove");
          return false;
        }
      }
      else {  //object to remove is null
        Log.error("Error: object to remove is null");
        return false;
      }
    }
    else {  //the list to remove from is null
      Log.error("Error: Passed list to remove from is null, no object to remove");
      return false;
    }
  }

  /** A little convience method to save coding time elsewhere.
      Another way to remove an object from a list. Pass in index of
      the object to be removed.
      @return: true on success, false on failure
  */
  protected boolean removeFromList(int listIndex, List fromList, String listName ) {
    if (fromList !=null) {
      if ( (listIndex >=0) && (listIndex < fromList.size())) { //valid index number
        fromList.remove(listIndex);
        return true;
      }
      else { //invalid index number
        Log.error("Error: passed index out of range.");
        return false;
      }
    }
    else {  //list to remove from is null
      Log.error("Passed list to remove from is null, no object to remove.");
      return false;
    }
  }

  /** Basically this rearranges XMLAttribute information into a more convient
      order for the toXDFOutputstream method.
      @return: Hashtable with 3 entries: attribList--attributes(strings, numbers)
                                         objRefList--the object this class owns
                                         PCDATA--the PCDATA of this element
  */

  /**added another if clause to put attibutes of Number type in the attribList
   * declare as protected, sub-class might use
   * fixed the documentation--k.z. 10/17
   */
  protected Hashtable getXMLInfo () {

    Hashtable xmlInfo = new Hashtable();
    ArrayList attribList = new ArrayList();
    ArrayList objRefList = new ArrayList();

    for (int i = 0; i < attribHash.size(); i++) {
      String attribName = (String) attribOrder.get(i);
      XMLAttribute obj = (XMLAttribute) attribHash.get(attribName);
      if (obj != null && obj.attribValue != null) {
        if ( obj.attribType == Constants.STRING_TYPE)
        {

          if (attribName.equals(sPCDATAAttribute)) {
            xmlInfo.put("PCDATA", obj.attribValue);
          } else {
            Hashtable item = new Hashtable();
            item.put("name", attribName);
            item.put("value", obj.attribValue);
            attribList.add(item);
          }
        }
        else {
          if(obj.attribType == Constants.NUMBER_TYPE) {  //it's an attribute of Number type
            Hashtable item = new Hashtable();
            item.put("name", attribName);
            item.put("value", obj.attribValue.toString());
            attribList.add(item);
          }
          else {// it's an obj ref, add to list
            Hashtable item = new Hashtable();
            item.put("name", attribName);
            item.put("value", obj.attribValue);
            item.put("type", obj.attribType);
            objRefList.add(item);
          }
        }
      }
    }

    xmlInfo.put("attribList", attribList);
    xmlInfo.put("childObjList", objRefList);
    return xmlInfo;
  }

  /** Write message out to specified OutputStream Object.
  */
  //declare as proteced, sub-classes may use --k.z. 10/17/2000
  protected void writeOut ( OutputStream outputstream, String msg ) {

    try {
      outputstream.write(msg.getBytes());
    } catch (IOException e) {
      Log.error("Error: couldnt open OutputStream for writing");
    }
  }

  /** Set the XMLattributes of this object using the passed AttributeList
   */
  // NOTE: this is essentially the Perl update method
  protected void setXMLAttributes (AttributeList attrs) {
     // set object attributes from an AttributeList 
     if (attrs != null) {
        // whip thru the list, setting each value
        for (int i = 0; i < attrs.getLength (); i++)
          ((XMLAttribute) this.attribHash.get(attrs.getName(i))).setAttribValue(attrs.getValue(i));
     }
  }


  //
  // PRIVATE Methods
  //

  /** Method determines if any of the group objects to which the passed object
      belongs are already opened and opens them If they arent already opened.
  */
  private String dealWithOpeningGroupNodes (BaseObject obj, OutputStream outputstream, String indent) {

    // Should *both* groupMemberHash and openGroupNodeHash be synchronized??
    synchronized(obj.groupMemberHash) {
      synchronized(obj.openGroupNodeHash) {
        Iterator iter = obj.groupMemberHash.iterator(); // Must be in synchronized block
        while (iter.hasNext()) {
          Group memberGroup = (Group) iter.next();
          // determine if this group that we belong to is already
          // open or not.
          if(!obj.openGroupNodeHash.contains(memberGroup)) {
            // its *not* already open, so we bump up the indentation,
            // open it and add it to the open group node list.
            indent.concat(sPrettyXDFOutputIndentation);
            memberGroup.toXDFOutputStream(outputstream, indent);
            obj.openGroupNodeHash.add(memberGroup);
          }
        }
      }
    }

    return indent;
  }

  /** Method determines if any of the currently open group objects
      belong to the current object and closes them if they arent.
  */
  private String dealWithClosingGroupNodes (BaseObject obj, OutputStream outputstream, String indent) {

    // Should *both* groupMemberHash and openGroupNodeHash be synchronized??
    synchronized(obj.groupMemberHash) {
      synchronized(obj.openGroupNodeHash) {

        Iterator iter = obj.openGroupNodeHash.iterator(); // Must be in synchronized block
        while (iter.hasNext()) {
          Group openGroup = (Group) iter.next();
          // determine if this group that we belong to is already
          // open or not.
          if(!obj.groupMemberHash.contains(openGroup)) {
            // its *not* a member of this group and its still open,
            // so we need to close it.

            if (sPrettyXDFOutput) writeOut(outputstream, indent);
            writeOut(outputstream, "</" + openGroup.classXDFNodeName + ">");
            if (sPrettyXDFOutput) writeOut(outputstream, Constants.NEW_LINE);
            obj.openGroupNodeHash.remove(openGroup);
            // peel off some indent
            indent = indent.substring(0,indent.length() - sPrettyXDFOutputIndentation.length());
          }
        }
      }
    }

    return indent;
  }


 /** Write the XML Declaration to the indicated OutputStream.
  */
  private void writeXMLDeclToOutputStream ( OutputStream outputstream,
                                            Hashtable XMLDeclAttribs
                                          )
  {

    // initial statement
    writeOut(outputstream, "<?xml");

    // print attributes
    Enumeration keys = XMLDeclAttribs.keys();
    while ( keys.hasMoreElements() )
    {
      Object key = keys.nextElement();
      writeOut(outputstream, " " + (String) key + "=\"" + XMLDeclAttribs.get((String) key) + "\"");
    }
    writeOut(outputstream, " ?>");
    if (sPrettyXDFOutput) writeOut(outputstream, Constants.NEW_LINE);

    // print the DOCTYPE DECL IF its a structure node
    if(classXDFNodeName != null && classXDFNodeName.equals(sXDFStructureNodeName) ) {
      writeOut(outputstream, "<!DOCTYPE " + sXDFRootNodeName + " SYSTEM " + sXDFDTDName + ">");
      if (sPrettyXDFOutput) writeOut(outputstream, Constants.NEW_LINE);
    }

  }

  //
  // Internal Classes
  //

  /** Stores values of XML-based attributes of the XDF object.
      These attributes will be used to re-construct an XDF file/stream
      from the Java object.
  */
  public static class XMLAttribute implements Cloneable {

    protected Object attribValue;
    protected String attribType;

    /** Constructor takes object reference and type.
    */
    // Shouldnt type be an emunerated list from the Constants class?
    // NOT just any arbitrary string can go here.
    public XMLAttribute (Object objValue, String strType) {
      attribValue = objValue;
      attribType = strType;
    }

    /** Set the value of this XMLAttribute.
    */
    public Object setAttribValue(Object objValue) {
      attribValue = objValue;
      return attribValue;
    }

    /** Set the type of value held by this XMLAttribute.
    */
    public String setAttribType(String strType) {
      if ( !Utility.isValidXMLAttributeType(strType))
      {
        Log.error("Type not a defined constant for XMLAttribute");
        return null;
      }

      // ok, set it
      attribType = strType;
      return attribType;
    }

    /** Get the value of this XMLAttribute.
    */
    public Object getAttribValue() {
       return attribValue;
    }

    /** Get the XMLAttribute value type.
    */
    public String getAttribType() {
       return attribType;
    }

    public Object clone () {

       XMLAttribute cloneObj = null;

       try {

          cloneObj = (XMLAttribute) super.clone();

          // need to clone the fields here too
//          cloneObj.attribType = new String(this.attribType);
//          cloneObj.attribValue = this.attribValue.clone();

       } catch (java.lang.CloneNotSupportedException e) {
          Log.errorln("Error: Clone not supported for XMLAttribute.");
       }

       return (Object) cloneObj;
    }

  } // end of internal Class XMLAttribute

} // end of BaseObject Class

/* Modification History:
 *
 * $Log$
 * Revision 1.15  2000/10/24 21:34:24  thomas
 * Added some clone code needed by the Reader (now)
 * and programmers (later, when we have some!) -b.t.
 *
 * Revision 1.14  2000/10/24 15:02:51  thomas
 * Hmm. minor problem with XMLDeclAttribs portion
 * of toXDFOutputStream ("SYSTEM" decl missing, and
 * it threw a warning when the hashtable for XMLDeclAttribs
 * was missing .. whichis silly as most nodes lack it). -b.t.
 *
 * Revision 1.13  2000/10/23 21:32:16  thomas
 * added setXMLAttributes method to BaseObject. This
 * method is functionally similar to Perl BaseObject
 * update method. -b.t.
 *
 * Revision 1.12  2000/10/18 15:06:53  kelly
 * fixed the bug.  now, there is no space between PCDATA and its closing node.  -k.z.
 *
 * Revision 1.11  2000/10/17 22:07:55  kelly
 * --enabled Number attribute in getXMLInfo() (added an if clause)
 * --declared writeOut() and getXMLInfo as protected
 * --constructed default XMLDelAttrib in several *toXDF* routines
 *
 * Revision 1.10  2000/10/16 14:47:24  kelly
 * use enum list to  check valid XMLAttribute type. --k.z.
 *
 * Revision 1.9  2000/10/10 19:12:05  cvs
 * First Feature complete version of the base class.
 * Added in dealWith(Open|Closed)Node private methods
 * used by toXDFOutputStream. -b.t.
 *
 */

