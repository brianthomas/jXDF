
// XDF BaseObject Class
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.util.*;
import java.io.*;

/**
 * BaseObject.java: The base class for most XDF objects 
 * @author: Brian Thomas (thomas@adc.gsfc.nasa.gov)
 *          Kelly Zeng (kelly.zeng@commerceone.com)
 * @version: $Revision$
 *
 */


public abstract class BaseObject implements Serializable {

  //
  // Fields
  //

  public static boolean sPrettyXDFOutput = false;
  public static String sPrettyXDFOutputIndentation = "  ";
  public static int sDefaultDataArraySize = 1000;
  public static int sReplaceNewlineWithEntityInOutputAttribute = 1;

  protected String classXMLName; // baseObject class is abstract, never counts as an XML node,
                                 // but all other derived classes are and is defined there

  // could put them into constants
  private static String sXMLStructureVersion = "1.0";
  private static String sXDFRootNodeName = "XDF";
  private static String sXDFStructureNodeName = "structure"; // bad. we should call the class to find this out
  private static String sXDFDTDName = "XDF_0.17.dtd";
  private static String sPCDATAAttribute = "value";

  // need clarification on the get/set methods
  protected Set openGroupNodeHash = Collections.synchronizedSet(new HashSet());
  protected Set groupMemberHash = Collections.synchronizedSet(new HashSet());

  // Hashtable to hold the XML attributes
  protected Hashtable attribHash;

  // list to store the order of the XML attributes
  protected List attribOrder;


  //
  // Constructor Methods
  //

  public BaseObject() {
    attribHash = new Hashtable(Constants.INIT_ATTRIBUTE_HASH_SIZE);
    attribOrder= Collections.synchronizedList(new ArrayList());
  }

  //
  // Get/Set Methods
  //

  public Hashtable getAttribHash() {
    return attribHash;
  }

  public List getAttribOrder() {
    return attribOrder;
  }

  public static boolean getPrettyXDFOutput() {
    return sPrettyXDFOutput;
  }

  public static boolean setPrettyXDFOutput(boolean i) {
    sPrettyXDFOutput = i;
    return sPrettyXDFOutput;
  }

  public static String getPrettyXDFOutputIndentation() {
    return sPrettyXDFOutputIndentation;
  }

  public static String setPrettyXDFOutputIndentation(String str) {
    sPrettyXDFOutputIndentation = str;
    return sPrettyXDFOutputIndentation;
  }

  public static int getDefaultDataArraySize(){
    return sDefaultDataArraySize;
  }

  public static int setDefaultDataArraySize(int i) {
    sDefaultDataArraySize = i;
    return sDefaultDataArraySize;
  }

  public static int getReplaceNewlineWithEntityInOutputAttribute() {
    return sReplaceNewlineWithEntityInOutputAttribute;
  }

  public static int setReplaceNewlineWithEntityInOutputAttribute(int i) {
    sReplaceNewlineWithEntityInOutputAttribute = i;
    return sReplaceNewlineWithEntityInOutputAttribute;
  }

  public static String getXMLStructureVersion() {
    return sXMLStructureVersion;
  }

  public static String  getXDFRootNodeName() {
    return sXDFRootNodeName;
  }

  public static String getXDFDTDName() {
    return sXDFDTDName ;
  }

  public static String getPCDATAAttribute() {
    return sPCDATAAttribute;
  }

  public Hashtable getXMLAttributes() {
    return attribHash;
  }

  //
  // Other Public Methods
  //

  /** addToGroup
  */
  public Group addToGroup(Group g) {

    if (g != null) {
      if (groupMemberHash.add(g)) {
        g.addMemberObject(g);
        return g;
      } else {
        System.out.println("Can't add to group.  already a member of the group" + g.getName());
        return null;
      }
    } else {
      System.out.println("the group to add to is null");
      return null;
    }

  }

  /** removeFromGroup
  */
  public Group removeFromGroup (Group g) {
    if (g!=null)  {
      if (groupMemberHash.contains(g))  {
        g.removeMemberObj(this);
        groupMemberHash.remove(g);
        return g;
      }
      else {
        System.out.println("Can't delete from group.  not a member of the group" + g.getName());
        return null;
      }

    }
    else {
      System.out.println("the group to add to is null");
      return null;
    }
  }

  /** isGroupMember
  */
  public boolean isGroupMember(Group g) {
    if ( (g!=null) && groupMemberHash.contains(g))
      return true;
    else
      return false;
  }

  /** toXDFFile
     Uses toXDFOutputStream
  */
  public void toXDFFile (String filename, Hashtable XMLDeclAttribs) {

    // open file writer
    try {
      FileOutputStream fileout = new FileOutputStream(filename);
      toXDFOutputStream(fileout, XMLDeclAttribs);
      fileout.close();
    } catch (IOException e) {
      System.err.println("Error: toXDFFile method hash trouble writing to "+ filename + " for writing.");
    }

  }

  /** toXDFFile
      Yet another invokation style
  */
  public void toXDFFile (String filename) {

   // prepare XMLDeclaration
    Hashtable XMLDeclAttribs = new Hashtable();
    XMLDeclAttribs.put("standalone", new String("no"));
    XMLDeclAttribs.put("version", (String) sXMLStructureVersion);

    toXDFFile(filename, XMLDeclAttribs);

  }

  /** toXDFOutputStream
      Write this structure and al the objects it owns to the supplied
      OutputStream Object
      This is the full blown version.
  */
  // we dont (yet) treat printing to filehandles or XMLDeclAttribs stuff!
  // just print it to standard out for now.
  public void toXDFOutputStream (
                                   OutputStream o,
                                   Hashtable XMLDeclAttribs,
                                   String indent,
                                   boolean dontCloseNode,
                                   String newNodeNameString,
                                   String noChildObjectNodeName
                                )
  {

    String nodeNameString = this.classXMLName;

    // Setup. Sometimes the name of the node we are opening is different from
    // that specified in the classXMLName (*sigh*)
    if (newNodeNameString != null) nodeNameString = newNodeNameString;

    // 0. To be valid XML, we always start an XML block with an
    //    XML declaration (e.g. somehting like "<?xml standalone="no"?>").
    //    Here we deal with  printing out XML Declaration && its attributes
    if (!XMLDeclAttribs.isEmpty()) {
        indent = "";
        writeXDFDeclToOutputStream(o, XMLDeclAttribs);
    }

    // 1. open this node, print its simple XML attributes
    if (nodeNameString != null) {

      if (sPrettyXDFOutput) writeOut(o,indent); // indent node if desired

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
        writeOut(o,"<" + sXDFRootNodeName); // print opening root node statement
      } else {
        writeOut(o,"<" + nodeNameString);   // print opening statement
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
        writeOut(o, " "+ item.get("name") + "=\"" + item.get("value") + "\"");
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
        writeOut(o, ">");
        if (sPrettyXDFOutput && pcdata == null) writeOut(o, Constants.NEW_LINE);
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
                dealWithOpeningGroupNodes(containedObj, o, indent);
                dealWithClosingGroupNodes(containedObj, o, indent);
                containedObj.toXDFOutputStream(o, new Hashtable(), newindent);
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
              // deal with opening group nodes
              // deal with closing group nodes
              containedObj.toXDFOutputStream(o, new Hashtable(), newindent);
            }
          }
        } else {
          // error: wierd type, actually shouldnt occur. Is this needed??
          System.err.println("Wierd error: unknown XML attribute type for item:"+item);
        }

      }

      // print out PCDATA, if any
      if(pcdata != null) writeOut(o,pcdata);

      // if there are no PCDATA or child objects/nodes then
      // we print out noChildObjectNodeName
      if ( childObjs.size() == 0 && pcdata == null && noChildObjectNodeName != null)
      {
        if (sPrettyXDFOutput) writeOut(o,indent);
        writeOut(o, "<" + noChildObjectNodeName + "/>");
        if (sPrettyXDFOutput) writeOut(o, Constants.NEW_LINE);
      }

      // ok, now deal with closing the node
      if (nodeNameString != null) {
        // indent = deal_with_closing_group_nodes();
        if (sPrettyXDFOutput) writeOut(o, indent);
        if (!dontCloseNode)
          if ( nodeNameString.equals(sXDFStructureNodeName) && !XMLDeclAttribs.isEmpty() )
          {
            writeOut(o, "</"+sXDFRootNodeName+">");
          } else {
            writeOut(o, "</"+nodeNameString+">");
          }

      }

    } else {

      if(dontCloseNode) {
         // it may not have sub-objects, but we dont want to close it
         // (happens for group objects)
      } else {
        // no sub-objects, just close this node
        writeOut(o, "/>");
      }

    }

    if (sPrettyXDFOutput) writeOut(o, Constants.NEW_LINE);

  }

  /** toXDFOutputStream
  */
  public void toXDFOutputStream (OutputStream o, Hashtable XMLDeclAttribs, String indent)
  {
     toXDFOutputStream(o, XMLDeclAttribs, indent, false, null, null);
  }

  /** toXDFOutputStream
  */
  public void toXDFOutputStream (OutputStream o, Hashtable XMLDeclAttribs)
  {
     toXDFOutputStream(o, XMLDeclAttribs, sPrettyXDFOutputIndentation, false, null, null);
  }

  /** toXDFOutputStream
  */
  public void toXDFOutputStream (OutputStream o)
  {

     Hashtable XMLDeclAttribs = new Hashtable();
     toXDFOutputStream(o, XMLDeclAttribs);
  }

  //
  // PROTECTED Methods
  //

  /** hashtableInitXDFAttributes 
   *  A little method to initialize XDF attributes from a given Hashtable.
   *  Hashtable key/value pairs coorespond to the class XDF attribute 
   *  names and their desired values. 
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

  /**removeFromList
   * Pass in object to remove
   */
  protected boolean removeFromList(Object what, List fromList, String listName) {
    if (fromList !=null) {
      if ( what !=null) {
        int index = fromList.indexOf(what);
        if (index !=-1) {
          fromList.remove(index);
          return true;
        }
        else {
          System.out.println("can't find object in" + listName+ ", ignoring remove");
          return false;

        }
      }
      else {
        System.out.println("object to remove is null");
        return false;
      }
    }
    else {
      System.out.println(listName + " to remove from is null, no object to remove");
      return false;
    }
  }

  /** removeFromList
   *  pass in index of the object to be removed
   *  overload
   */
  protected boolean removeFromList(int what, List fromList, String listName) {
    if (fromList !=null) {
      if ( (what >=0) && (what<fromList.size())) {
        fromList.remove(what);
        return true;
      }
      else {
        System.err.println("index out of range");
        return false;
      }
    }
    else {
      System.out.println(listName + " is null, no objects to remove");
      return false;
    }
  }

  //
  // PRIVATE Methods
  //

  /** dealWithOpeningGroupNodes
  */
  private void dealWithOpeningGroupNodes (BaseObject obj, OutputStream o, String indent) {

    // fill in later
    System.err.println("dealWithOpeningGroupNodes called but method is empty!");
  }

  /** dealWithClosingGroupNodes
  */
  private void dealWithClosingGroupNodes (BaseObject obj, OutputStream o, String indent) {
    // fill in later
    System.err.println("dealWithClosingGroupNodes called but method is empty!");
  }

  /** get XML information about XML attributes.
      Seems a redundant method. Hmm..
  */
  private Hashtable getXMLInfo () {

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
        } else { // its an obj ref, add to list
          Hashtable item = new Hashtable();
          item.put("name", attribName);
          item.put("value", obj.attribValue);
          item.put("type", obj.attribType);
          objRefList.add(item);
        }
      }
    }

    xmlInfo.put("attribList", attribList);
    xmlInfo.put("childObjList", objRefList);

    return xmlInfo;
  }

 /** writeXDFDeclToOutputStream
  */
  private void writeXDFDeclToOutputStream (OutputStream o, Hashtable XMLDeclAttribs)
  {

    // initial statement
    writeOut(o, "<?xml");

    // print attributes
    Enumeration keys = XMLDeclAttribs.keys();
    while ( keys.hasMoreElements() )
    {
      Object key = keys.nextElement();
      writeOut(o, " " + (String) key + "=\"" + XMLDeclAttribs.get((String) key) + "\"");
    }
    writeOut(o, " ?>");
    if (sPrettyXDFOutput) writeOut(o,Constants.NEW_LINE); // is this treatment ok, or do we need to consider MSDOS stuff ie \n\r

    // print the DOCTYPE DECL
    writeOut(o, "<!DOCTYPE " + sXDFRootNodeName + " " + sXDFDTDName + ">");
    if (sPrettyXDFOutput) writeOut(o, Constants.NEW_LINE);

  }


 /** writeOut
      Write message out to specified outputstream.
  */
  private void writeOut ( OutputStream o, String msg ) {

    try {
      o.write(msg.getBytes());

    } catch (IOException e) {
      System.err.println("Error: couldnt open stream for writing");
    }


  }

  // end of BaseObject Class

}

