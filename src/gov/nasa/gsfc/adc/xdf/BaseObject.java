
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

import java.lang.Cloneable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.Serializable;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import org.xml.sax.AttributeList;

/** The base class for most XDF objects.
    XDF is the eXtensible Data Structure, which is an XML format designed
    to contain n-dimensional scientific/mathematical data.
    A diagram of the XDF object structure is included in the package.

    The BaseObject class provides a generalized means of storing, retrieving
    and writing out the XML-based properties of the XDF objects. It also
    provides fields/methods to allow all inheriting XDF objects be
    members of Group objects. Key parts to the BaseObject class include the
    XMLAttributes and the toXML* methods it provides.
 */
public abstract class BaseObject implements Serializable, Cloneable {

  //
  // Fields
  //

  /** The XDF (element) node name for a given XDF class. Not all XDF classes
      have a defined node name (ie map to the XDF DTD). The BaseObject class
      is an example of this case.
  */
  protected String classXDFNodeName;

  /** openGroupNodeHash is an internal field used by the toXML* methods to track
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

  // stores notation entries for the XMLDeclaration
  protected HashSet XMLNotationHash = new HashSet();

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
    resetXMLAttributes();

  }

  //
  // Get/Set Methods
  //

  /** The class XDF node name may (or may not exist) for a given XDF java object.
     @return String on success, null (String Object) if the node name doesnt exist.
  */
  public String getClassXDFNodeName() {
    return classXDFNodeName;
  }

//
// I cant believe that these 2 methods are needed. 
// Removing it for the time being. -b.t. 
//

  /* Return the hashtable of XMLAttribute names and their values.
      @return Hashtable on success, an empty hashtable is passed back if their
               are no XMLAttributes within a given XDF object class.
  */
/*
  public Hashtable getAttribHash() {
    return attribHash;
  }
*/

  /*set the attributes of this object from the passed attribute Hash reference
   *
   */
/*
  public void setAttribHash(Hashtable hash) {
    attribHash = hash;
  }
*/

   
  /** Set the NotationHash for this object. Each entry in the passed HashSet
      will be a Hashtable containing the keys 'name' 'publicId' and 'systemId'.
      This information will be printed out with other XMLDeclarations in a 
      toXMLFileHandle call that prints the XML declaration (e.g. DOCTYPE header). 
  */
  public void setXMLNotationHash (HashSet hash) {
     XMLNotationHash = hash;
  }

  /** Return a list of the proper ordering of the XML attributes of this object.
      @return List on success, on failure an empty List object is passed back if
               there are no XMLAttributes within a XDF given object class.
  */
  public List getAttribOrder() {
    return attribOrder;
  }

  
  /** Get the Hashtable containing the XMLAttributes for this object.
  */
  public Hashtable getXMLAttributes() {
    return attribHash;
  }

  //
  // Other Public Methods
  //

// This isnt quite what I want. Removing for now. -b.t.
  /*set the attributes of this object from the passed attribute Hash reference
   * the same as setAttribHash()
   */
/*
  public Hashtable update (Hashtable hash) {
    setAttribHash(hash);
    return getAttribHash();
  }
*/

  /** Add this object to the indicated Group object.
      @return true on success, false on failure.
  */
  public boolean addToGroup(Group groupObject) {
    if (groupMemberHash.add(groupObject)) {  //add in successful
       groupObject.addMemberObject(this);
       return true; // bad, should return based on success of adding
    } else {
       Log.warnln("Can't add to group.  already a member of the group" + groupObject.getName());
       return false;
    }
  }

  /** Remove this object from the indicated Group object.
      @return Group removed from on success, null (Group Object) on failure.
  */
  public Group removeFromGroup (Group groupObject) {
      if (groupMemberHash.contains(groupObject))  {
        //this object does belong to the indicated Group object
        groupObject.removeMemberObject((Object) this);
        groupMemberHash.remove(groupObject);
        return groupObject;
      }
      else {
        Log.errorln("Can't delete from group.  not a member of the group" +
                            groupObject.getName());
        return null;
      }
  }

  /** Determine if this object is a member of the indicated Group object.
      @return true is it is a member, false if it is not.
  */
  public boolean isGroupMember(Group groupObject) {
    if ( groupMemberHash.contains(groupObject))
      return true;
    else
      return false;
  }

  /** Write this object out to the indicated file. The file will be clobbered
      by the output, so it is advisable to check for the existence of the file
      *before* using this method if you are worried about losing prior information.
      Uses toXMLOutputStream. The passed hashtable will be used to initialize the
      attributes of the XML declaration in the output XDF file.
  */
  public void toXMLFile (String filename, Hashtable XMLDeclAttribs) 
  throws java.io.IOException
  {

    // open file writer
 //   try {
      FileOutputStream fileout = new FileOutputStream(filename);
      toXMLOutputStream(fileout, XMLDeclAttribs);
      fileout.close();
/*
    } catch (IOException e) {
      Log.error("Error: toXMLFile method hash trouble writing to "+ filename + " for writing.");
    }
*/

  }

  /** A different invokation style. It has defaults for the XML Declaration
      setting standalone to "no" and version to the value of sXMLSpecVersion.
  */
  public void toXMLFile (String filename) 
  throws java.io.IOException
  {

     // prepare XMLDeclaration
     Hashtable XMLDeclAttribs = new Hashtable();
     XMLDeclAttribs.put("standalone", new String("no"));
     XMLDeclAttribs.put("rootName", Specification.getInstance().getXDFRootNodeName());
     XMLDeclAttribs.put("dtdName", Specification.getInstance().getXDFDTDName());

     toXMLFile(filename, XMLDeclAttribs);

  }

  /** Write this object and all the objects it owns to the supplied
      OutputStream object as XDF. Supplying a populated XMLDeclAttributes
      Hashtable will result in the xml declaration being written at the
      begining of the outputstream (so when you do this, you will
      get well-formmed XML output for ANY object). For obvious reasons, only
      XDF objects will create *valid XDF* output.
  */
  public void toXMLOutputStream (
                                   OutputStream outputstream,
                                   Hashtable XMLDeclAttribs,
                                   String indent,
                                   boolean dontCloseNode,
                                   String newNodeNameString,
                                   String noChildObjectNodeName
                                )
  throws java.io.IOException
  {

    //while writing out, attribHash should not be changed
    synchronized (attribHash) {
      String nodeNameString = this.classXDFNodeName;

      // Setup. Sometimes the name of the node we are opening is different from
      // that specified in the classXDFNodeName (*sigh*)
      if (newNodeNameString != null) nodeNameString = newNodeNameString;

      // 1. open this node, print its simple XML attributes
      if (nodeNameString != null) {

        if (Specification.getInstance().isPrettyXDFOutput())
          writeOut(outputstream, indent); // indent node if desired
        writeOut(outputstream,"<" + nodeNameString);   // print opening statement

      }

      // gather info about XMLAttributes in this object/node
      Hashtable xmlInfo = getXMLInfo();

      // 2. Print out string object XML attributes EXCEPT for the one that
      //    matches PCDATAAttribute.
      ArrayList attribs = (ArrayList) xmlInfo.get("attribList");
      // is synchronized here correct?
      synchronized(attribs) {
        int size = attribs.size();
        for (int i = 0; i < size; i++) {
          Hashtable item = (Hashtable) attribs.get(i);
          writeOut(outputstream, " " + item.get("name") + "=\"");
          // this slows things down, should we use?
          //writeOutAttribute(outputstream, (String) item.get("value"));
          writeOut(outputstream, (String) item.get("value"));
          writeOut(outputstream, "\"" );
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
          if ((Specification.getInstance().isPrettyXDFOutput()) && (pcdata == null))
             writeOut(outputstream, Constants.NEW_LINE);
        }

        // deal with object/list XML attributes, if any in our list
        int size = childObjs.size();
        for (int i = 0; i < size; i++) {
          Hashtable item = (Hashtable) childObjs.get(i);

          if (item.get("type") == Constants.LIST_TYPE)
          {

            List objectList = (List) item.get("value");
            indent = objectListToXMLOutputStream (outputstream, objectList, indent);  
          }
          else if (item.get("type") == Constants.OBJECT_TYPE)
          {
            BaseObject containedObj = (BaseObject) item.get("value");
            if (containedObj != null) { // can happen from pre-allocation of axis values, etc (?)
              // shouldnt this be synchronized too??
              synchronized(containedObj) {
                indent = dealWithClosingGroupNodes(containedObj, outputstream, indent);
                indent = dealWithOpeningGroupNodes(containedObj, outputstream, indent);
                String newindent = indent + Specification.getInstance().getPrettyXDFOutputIndentation();
                containedObj.toXMLOutputStream(outputstream, new Hashtable(), newindent);
              }
            }
          } else {
            // error: weird type, actually shouldnt occur. Is this needed??
            Log.errorln("Weird error: unknown XML attribute type for item:"+item);
          }

        }


        // print out PCDATA, if any
        if(pcdata != null)  {
          writeOut(outputstream, pcdata);
        };

        // if there are no PCDATA or child objects/nodes then
        // we print out noChildObjectNodeName and close the node
        if ( childObjs.size() == 0 && pcdata == null && noChildObjectNodeName != null)
        {

          if (Specification.getInstance().isPrettyXDFOutput())
            writeOut(outputstream, indent + Specification.getInstance().getPrettyXDFOutputIndentation());

          writeOut(outputstream, "<" + noChildObjectNodeName + "/>");

          if (Specification.getInstance().isPrettyXDFOutput())
            writeOut(outputstream, Constants.NEW_LINE);

        }

        // ok, now deal with closing the node
        if (nodeNameString != null) {

           indent = dealWithClosingGroupNodes((BaseObject) this, outputstream, indent);

          if (Specification.getInstance().isPrettyXDFOutput() && pcdata == null)
                writeOut(outputstream, indent);

          if (!dontCloseNode)
              writeOut(outputstream, "</"+nodeNameString+">");

        }

      } else {

        if (nodeNameString != null) {
	    if (dontCloseNode) {
		// it may not have sub-objects, but we dont want to close it
		// (happens for group objects)
		writeOut(outputstream, ">");
	    } else {
		// no sub-objects, just close this node
		writeOut(outputstream, "/>");
	    }
	}

      }

      if (Specification.getInstance().isPrettyXDFOutput()  && nodeNameString != null) 
	  writeOut(outputstream, Constants.NEW_LINE);
    }//synchronize
  }

  /** A different invokation style for writing this object out to
      the indicated OutputStream.
  */
  public void toXMLOutputStream ( OutputStream outputstream,
                                  Hashtable XMLDeclAttribs,
                                  String indent
                                )
  throws java.io.IOException
  {
     toXMLOutputStream(outputstream, XMLDeclAttribs, indent, false, null, null);

  }

  /** A different invokation style for writing this object out to
      the indicated OutputStream.
  */
  public void toXMLOutputStream (OutputStream outputstream, Hashtable XMLDeclAttribs)
  throws java.io.IOException
  {
     //not reseanable to set the indent to Specification.getInstance().isPrettyXDFOutput()Indentation --k.z. 10/17
     toXMLOutputStream(outputstream, XMLDeclAttribs, new String(""), false, null, null);
  }

  /** A different invokation style. It has defaults for the XML Declaration
      setting standalone to "no" and version to the value of sXMLSpecVersion.
  */
  public void toXMLOutputStream (OutputStream outputstream, String indent)
  throws java.io.IOException
  {
     // prepare XMLDeclaration
     Hashtable XMLDeclAttribs = new Hashtable();
     XMLDeclAttribs.put("standalone", new String("no"));
     XMLDeclAttribs.put("dtdName", Specification.getInstance().getXDFDTDName());
     XMLDeclAttribs.put("rootName", Specification.getInstance().getXDFRootNodeName());
     toXMLOutputStream(outputstream, XMLDeclAttribs, indent);
  }

  /** A different invokation style. It has defaults for the XML Declaration
      setting standalone to "no" and version to the value of sXMLSpecVersion.
      Indentation starts as "".
  */
  public void toXMLOutputStream (OutputStream outputstream)
  throws java.io.IOException
  {

     // prepare XMLDeclaration
     Hashtable XMLDeclAttribs = new Hashtable();
     XMLDeclAttribs.put("standalone", new String("no"));
     XMLDeclAttribs.put("dtdName", Specification.getInstance().getXDFDTDName());
     XMLDeclAttribs.put("rootName", Specification.getInstance().getXDFRootNodeName());

     toXMLOutputStream(outputstream, XMLDeclAttribs);

  }

  //
  // PROTECTED Methods
  //

  /** Set the XMLattributes of this object using the passed AttributeList.
   */
  // NOTE: this is essentially the Perl update method
  public void setXMLAttributes (AttributeList attrs) {

     synchronized (attribHash) {
      // set object attributes from an AttributeList
      if (attrs != null) {
          // whip thru the list, setting each value
          int size = attrs.getLength();
          for (int i = 0; i < size; i++) {
             String name = attrs.getName(i);
             String value = attrs.getValue(i); // yes, AttributeList can only return strings 

             // set it as appropriate to the type
             if (name != null && value != null) {
                String type = ((XMLAttribute) this.attribHash.get(name)).getAttribType();
                if(type.equals(Constants.INTEGER_TYPE))
                   // convert string to proper Integer
                   ((XMLAttribute) this.attribHash.get(name)).setAttribValue(new Integer(value));
                else if(type.equals(Constants.DOUBLE_TYPE))
                   // convert string to proper Double
                   ((XMLAttribute) this.attribHash.get(name)).setAttribValue(new Double(value));
                else
                   ((XMLAttribute) this.attribHash.get(name)).setAttribValue(value);
             }

          }

      }

     } //synchronize
  }

  /** deep copy of this XDF object
      @return an exact copy  of this XDF object
   */
  protected Object clone () throws CloneNotSupportedException{

     //shallow copy for fields
     BaseObject cloneObj = (BaseObject) super.clone();

     // deep copy the attriOrder
     synchronized (this.attribOrder) {
      synchronized (cloneObj.attribOrder) {
        cloneObj.attribOrder = Collections.synchronizedList(new ArrayList());
        int stop = this.attribOrder.size();
        for (int i = 0; i < stop; i++) {
          cloneObj.attribOrder.add(new String((String) this.attribOrder.get(i)));
        }
      }
    }
     // XMLAttributes Clone, deep copy
     synchronized (this.attribHash) {
      synchronized (cloneObj.attribHash) {
        cloneObj.attribHash = new Hashtable();
        Enumeration keys = this.attribHash.keys();
        while (keys.hasMoreElements()) {
          String key = (String) keys.nextElement();
          XMLAttribute XMLAttributeValue = (XMLAttribute) this.attribHash.get(key);
          cloneObj.attribHash.put(key, XMLAttributeValue.clone());
        }
        return cloneObj;
      }
    }

  }

  protected void resetXMLAttributes() { 
    // clear out arrays, etc
    attribHash  = new Hashtable(Constants.INIT_ATTRIBUTE_HASH_SIZE);
    attribOrder = Collections.synchronizedList(new ArrayList());
  }

  /** A little convenience method to save coding time elsewhere.
      This method initializes the XDF attributes of an object from a
      passed Hashtable.
      Hashtable key/value pairs coorespond to the class XDF attribute
      names and their desired values.
  */
  protected void hashtableInitXDFAttributes (Hashtable InitAttributeTable)
  {

    Object attribute;
    Object obj;
    synchronized (attribHash) {
      int size = attribOrder.size();
      for (int i = 0; i < size; i++)
      {
        attribute  = attribOrder.get(i);
        obj = InitAttributeTable.get(attribute);

        // only if object exists
        if (obj != null) {
          XMLAttribute toRemove = (XMLAttribute) attribHash.remove(attribute);
          attribHash.put(attribute, new XMLAttribute(obj, toRemove.getAttribType()));
        }

      }
    }//synchronize

  }

  /** A little convenience method to save coding time elsewhere.
     Pass in an object to be removed from the indicated list.
     @param what - the object to be removed
            formList - the list to remove from
            listName - the name of the list to remove from
     @return true on success, false on failure
  */
  protected boolean removeFromList ( Object what, List fromList, String listName ) {
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

  /** A little convenience method to save coding time elsewhere.
      Another way to remove an object from a list.
      @param listIndex--the index of the object to be removed
            formList--the list to remove from
            listName--the name of the list to remove from
      @return true on success, false on failure
  */
  protected boolean removeFromList(int listIndex, List fromList, String listName ) {
    if ( (listIndex >=0) && (listIndex < fromList.size())) { //valid index number
      fromList.remove(listIndex);
        return true;
    }
    else { //invalid index number
      Log.errorln("Error: removeList was passed index="+listIndex+" which is out of range.");
      return false;
    }

  }

   protected String objectListToXMLOutputStream (OutputStream outputstream, List objectList, String indent)
   throws java.io.IOException
   {

      // Im not sure this synchronized wrapper is needed, we are
      // only accessing stuff here.. Also, should synchronzied wrapper
      // occur back in the getXMLInfo method instead where the orig
      // access occured?!?
      synchronized(objectList) {
         Iterator iter = objectList.iterator(); // Must be in synchronized block
         while (iter.hasNext()) {
            BaseObject containedObj = (BaseObject) iter.next();
            if (containedObj != null) { // can happen from pre-allocation of axis values, etc (?)
               indent = dealWithClosingGroupNodes(containedObj, outputstream, indent);
               indent = dealWithOpeningGroupNodes(containedObj, outputstream, indent);
               String newindent = indent + Specification.getInstance().getPrettyXDFOutputIndentation();
               containedObj.toXMLOutputStream(outputstream, new Hashtable(), newindent);
            }
         }
      }

      return indent;
   }


  /** Basically this rearranges XMLAttribute information into a more convient
      order for the toXMLOutputstream method.
      @return Hashtable with 3 entries:  attribList --> attributes(strings, numbers)
                                         objRefList --> the object this class owns
                                         PCDATA     --> the PCDATA of this element
  */
  protected Hashtable getXMLInfo () {

    Hashtable xmlInfo = new Hashtable();
    ArrayList attribList = new ArrayList();
    ArrayList objRefList = new ArrayList();

    synchronized (attribHash) {
      int size = attribHash.size();
      for (int i = 0; i < size; i++) {
        String attribName = (String) attribOrder.get(i);
        XMLAttribute obj = (XMLAttribute) attribHash.get(attribName);
        if (obj != null && obj.attribValue != null) {
          if ( obj.attribType == Constants.STRING_TYPE)
          {

            if (attribName.equals(Specification.getInstance().getPCDATAAttribute())) {
              xmlInfo.put("PCDATA", obj.attribValue);
            }else {
              Hashtable item = new Hashtable();
              item.put("name", attribName);
              item.put("value", obj.attribValue);
              attribList.add(item);
            }
          }
          else {
            if(obj.attribType == Constants.INTEGER_TYPE ||
               obj.attribType == Constants.DOUBLE_TYPE) {  //it's an attribute of Number type
              Hashtable item = new Hashtable();
              item.put("name", attribName);
              item.put("value", obj.attribValue.toString());
              attribList.add(item);
            }
            else {// add to list if it is not an empty list  -k.z.
              if ((obj.attribType !=Constants.LIST_TYPE) || (((List)obj.attribValue).size()!=0)) {
                Hashtable item = new Hashtable();
                item.put("name", attribName);
                item.put("value", obj.attribValue);
                item.put("type", obj.attribType);
                objRefList.add(item);
              }
            }
          }
        }
      }

      xmlInfo.put("attribList", attribList);
      xmlInfo.put("childObjList", objRefList);
    } //synchronize
      return xmlInfo;
  }

  /** Write message out to specified OutputStream Object.
  */
  //declare as proteced, sub-classes may use --k.z. 10/17/2000
  protected void writeOut ( OutputStream outputstream, String msg ) 
  throws java.io.IOException
  {
 //   try {
      outputstream.write(msg.getBytes());
/*
    } catch (IOException e) {
      Log.error("Error: couldnt open OutputStream for writing");
    }
*/
  }

  protected void writeOut ( OutputStream outputstream, char c ) 
  throws java.io.IOException
  {
 //   try {
      outputstream.write(c);
/*
    } catch (IOException e) {
      Log.error("Error: couldnt open OutputStream for writing");
    }
*/
  }

  /** Write out string object formatted so it may be a proper XML 
      (XDF) string in a node attribute. Basically, newLine and carriageReturn
      entities are substituted in for appropriate characters. 
   */
  protected void writeOutAttribute ( OutputStream outputstream, String text) 
  throws java.io.IOException
  {

     StringCharacterIterator iter = new StringCharacterIterator(text);

     for(char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) 
     {

        if(c == '\n')
           writeOut(outputstream, "&#010;");
        else if(c == '\r')
           writeOut(outputstream, "&#013;");
        else
           writeOut(outputstream, c);

     }

  }

  /** Method determines if any of the group objects to which the passed object
      belongs are already opened and opens them If they arent already opened.
  */
  protected String dealWithOpeningGroupNodes (BaseObject obj, OutputStream outputstream, String indent) 
  throws java.io.IOException
  {

    StringBuffer newIndent = new StringBuffer(indent);

    // Should *both* groupMemberHash and openGroupNodeHash be synchronized??
    synchronized(obj.groupMemberHash) {
      synchronized(this.openGroupNodeHash) {
        Iterator iter = obj.groupMemberHash.iterator(); // Must be in synchronized block
        while (iter.hasNext()) {
          Group memberGroup = (Group) iter.next();
          // determine if this group that we belong to is already
          // open or not.
          if(!this.openGroupNodeHash.contains(memberGroup)) {
            // its *not* already open, so we bump up the indentation,
            // open it and add it to the open group node list.
            newIndent.append(Specification.getInstance().getPrettyXDFOutputIndentation());
            memberGroup.toXMLOutputStream(outputstream, new Hashtable(), newIndent.toString(), true, null, null);
            this.openGroupNodeHash.add(memberGroup);
          }
        }
      }
    }

    return newIndent.toString();
  }

  /** Method determines if any of the currently open group objects
      belong to the current object and closes them if they arent.
  */
  protected String dealWithClosingGroupNodes (BaseObject obj, OutputStream outputstream, String indent) 
  throws java.io.IOException
  {

    // Should *both* groupMemberHash and openGroupNodeHash be synchronized??
    synchronized(obj.groupMemberHash) {
      synchronized(this.openGroupNodeHash) {

        Iterator iter = this.openGroupNodeHash.iterator(); // Must be in synchronized block
        while (iter.hasNext()) {
          Group openGroup = (Group) iter.next();
          // determine if this group that we belong to is already
          // open or not.

          if(!obj.groupMemberHash.contains(openGroup)) {
            // its *not* a member of this group and its still open,
            // so we need to close it.

            if (Specification.getInstance().isPrettyXDFOutput()) writeOut(outputstream, indent);
            writeOut(outputstream, "</" + openGroup.classXDFNodeName + ">");
            if (Specification.getInstance().isPrettyXDFOutput()) writeOut(outputstream, Constants.NEW_LINE);
            this.openGroupNodeHash.remove(openGroup);
            // peel off some indent
            indent = indent.substring(0,indent.length() - Specification.getInstance().getPrettyXDFOutputIndentation().length());
          }
        }
      }
    }

    return indent;
  }


  /** Find all of the child href objects in this object.
   */
  protected ArrayList findAllChildHrefObjects () {

     ArrayList list = new ArrayList();

     if (this instanceof Structure) {

        List arrayList = ((Structure) this).getArrayList();
        synchronized (arrayList) {
           Iterator iter = arrayList.iterator(); // Must be in synchronized block
           while (iter.hasNext()) {
               Array childArray = (Array) iter.next();
               Href hrefObj = childArray.getDataCube().getHref();
               if (hrefObj != null) 
                  list.add(hrefObj);
           }
        } // sychronized arrayList 

	// a temporary fix for recursive href searching
        List structList = ((Structure) this).getStructList();
        synchronized (structList) {
           Iterator iter = structList.iterator(); // Must be in synchronized block
           while (iter.hasNext()) {
               Structure childStruct = (Structure) iter.next();
	       if (childStruct != null) {
		   ArrayList childList = childStruct.findAllChildHrefObjects();
		   if (childList != null && childList.size() > 0) {
		       Iterator childIter = childList.iterator();
		       while (childIter.hasNext())
			   list.add(childIter.next());
		   }
	       }
	   }
        } // sychronized structList 
     }
     return list;
  }

} // end of BaseObject Class

/* Modification History:
 *
 * $Log$
 * Revision 1.49  2001/07/11 22:35:20  thomas
 * Changes related to adding valueList or removeal of unneeded interface files.
 *
 * Revision 1.48  2001/07/06 19:04:23  thomas
 * toXMLOutputStream and related methods now pass on IOExceptions
 * to the application writer (e.g. they throw the error).
 *
 * Revision 1.47  2001/07/06 18:31:47  thomas
 * Fixed bug in group printing in toXMLOutputStream.
 *
 * Revision 1.46  2001/06/28 16:50:54  thomas
 * changed add method(s) to return boolean.
 *
 * Revision 1.45  2001/06/12 20:17:55  huang
 * do not write out brackets if nodeName is null
 *
 * Revision 1.44  2001/06/12 17:17:09  huang
 * donot writeout a newline if the nodeName is null
 *
 * Revision 1.43  2001/05/24 17:23:16  huang
 * fixed a bug in findAllChildHrefObjects()
 *
 * Revision 1.42  2001/05/10 21:07:19  thomas
 * moved attribHash/attribOrder to init method.
 * shift around stuff to XDF file.
 * made setXMLAttributes public (why?, hurm.)
 *
 * Revision 1.41  2001/05/04 21:01:06  thomas
 * made setXMLAttributes public method.
 *
 * Revision 1.40  2001/05/04 20:18:58  thomas
 * Small changes to accomodate new XDF class.
 *
 * Revision 1.39  2001/03/28 21:59:46  thomas
 * Forgot to declare an empty hashset for XMLNotationHash
 * on init. This resulted in bomb on toXMLOutputStream
 * call from objects built by hand. One line fixed.
 *
 * Revision 1.38  2001/02/07 18:44:03  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.37  2001/01/19 22:32:42  thomas
 * Removed setAttribHash, getAttribHash methods.
 * Dont seem to be needed. Also, removed update
 * method, it doenst seem to be used. -b.t.
 *
 * Revision 1.36  2001/01/19 17:25:08  thomas
 * Added ability to set the XML parser. Added ability to
 * save a hashtable of Notation that output XML will use.
 * Fixed href stuff to DTD standard (using entities in DOCTYPE
 * to indicate the filename, etc.) b.t.
 *
 * Revision 1.35  2000/11/27 22:39:25  thomas
 * Fix to allow attribute text to have newline, carriage
 * returns in them (print out as entities: &#010; and
 * &#013;) This allows files printed out to be read back
 * in again(yeah!). -b.t.
 *
 * Revision 1.34  2000/11/27 20:48:37  thomas
 * *** empty log message ***
 *
 * Revision 1.33  2000/11/20 22:04:41  thomas
 * Implimented new INTEGER_TYPE/DOUBLE_TYPE for
 * XMLAttribute printout in toXMLOutputStream. -b.t.
 *
 * Revision 1.32  2000/11/16 19:55:35  kelly
 * fixed documentation.  -k.z. and singleton related stuff.
 *
 * Revision 1.30  2000/11/09 23:04:56  thomas
 * Updated version, made changes to allow extension
 * to other dataformats (e.g. FITSML). -b.t.
 *
 * Revision 1.29  2000/11/09 05:32:35  thomas
 * Print out of XML declaration attributes isnt ordered
 * (as you might expect since they are stored in a hash)
 * This causes problems for Java XML parsers if "version"
 * isnt the first attribute (which we cant insure w/ hashtable).
 * I have made "version" a special attribute, which the
 * user may not set or override in order to insure that it
 * is the first on the line <? xml ?>. -b.t.
 *
 * Revision 1.28  2000/11/09 04:24:11  thomas
 * Implimented small efficiency improvements to traversal
 * loops. -b.t.
 *
 * Revision 1.27  2000/11/08 22:30:12  thomas
 * Changed set methods to return void. -b.t.
 *
 * Revision 1.26  2000/11/08 19:33:30  thomas
 * Trimmed down import path to just needed classes. b.t.
 *
 * Revision 1.25  2000/11/08 19:18:07  thomas
 * Changed the name of toXDF* methods to toXML* to
 * better reflect the nature of the output (its not XDF
 * unless you call th emethod from strcuture object;
 * otherwise, it wont validate as XDF; it is still XML
 * however). -b.t.
 *
 * Revision 1.24  2000/11/06 21:09:06  kelly
 * --minor fix.  wirteOut doesnt check if passed in String is null.
 * --more synchronization on clone.  -k.z.
 *
 * Revision 1.23  2000/11/03 21:23:33  thomas
 * Small change to setXMLAttributes to intercept null
 * values in the attributelist. -b.t.
 *
 * Revision 1.22  2000/11/02 17:55:11  kelly
 * implements Cloneable now.
 *
 * Revision 1.21  2000/11/01 22:14:41  thomas
 * Reversed changes to toXDFOutputStream. Fixed grouping
 * code (mostly in dealWith*nodes mthods). -b.t.
 *
 * Revision 1.20  2000/11/01 21:58:08  kelly
 * moved XMLAttribute out of BaseObject -k.z
 *
 * Revision 1.19  2000/10/31 22:09:08  kelly
 * minor fix.
 *
 * Revision 1.18  2000/10/31 18:41:14  thomas
 * Removed error report from set attributes. -b.t.
 *
 * Revision 1.17  2000/10/27 21:07:57  kelly
 * --fixed a bug in *toXDF* when writing out <units><unitless></units>
 * --fixed a bug when writing out the simplest node
 * --add STRING_OR_NUMBER_TYPE in *toXDF*  -k.z.
 *
 * Revision 1.16  2000/10/25 21:47:53  thomas
 * Minor bug fix. Sync up method removeMemberObject
 * name from Group.java. -b.t.
 *
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

