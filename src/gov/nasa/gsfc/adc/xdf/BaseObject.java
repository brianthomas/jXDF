
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

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.Writer;
import java.io.StringWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.IOException;
import java.io.Serializable;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import org.xml.sax.Attributes;

/** The base class for most XDF objects.
    XDF is the eXtensible Data Structure, which is an XML format designed
    to contain n-dimensional scientific/mathematical data.
    A diagram of the XDF object structure is included in the package.

    The BaseObject class provides a generalized means of storing, retrieving
    and writing out the XML-based properties of the XDF objects. It also
    provides fields/methods to allow all inheriting XDF objects be
    members of Group objects. Key parts to the BaseObject class include the
    Attributes and the toXML* methods it provides.
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
  protected Hashtable attribHash = new Hashtable ();

  /** A List to store the order of the XML attributes.
  */
  protected List attribOrder = Collections.synchronizedList(new ArrayList());

  //
  // Constructor Methods
  //

  /** The no-argument constructor.
  */
  public BaseObject() {

    // The heart of the baseObject is that it manages the storage, retrieval
    // and writing out the Attributes for the XDF objects.
    // There are 2 parts to making the Attributes of the base Object
    // work properly: a lookup table of key/value pairs in attribHash and a
    // list containing the  proper order of the attributes.
    resetAttributes();

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

  /* Return the hashtable of Attribute names and their values.
      @return Hashtable on success, an empty hashtable is passed back if their
               are no Attributes within a given XDF object class.
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

   
  /** Return a list of the proper ordering of the XML attributes of this object.
      @return List on success, on failure an empty List object is passed back if
               there are no Attributes within a XDF given object class.
  */
  public List getAttributeOrder() {
    return attribOrder;
  }

  
  /** Get the Hashtable containing the Attributes for this object.
  */
  public Hashtable getAttributes() {
    return attribHash;
  }

  /** Get a specific Attribute by its name
   */
  public Attribute getAttribute (String attribName) {
     Attribute obj = (Attribute) attribHash.get(attribName);
     return obj;
  }

  /** Get the value of a specific Attribute. Only returns non-null
      if the Attribute exists and is of STRING_TYPE.
   */
  public String getAttributeStringValue(String attribName) 
  {
      String value = null;
      Attribute attrib = getAttribute(attribName);

      /*
      if (attrib != null && attrib.getAttribType() == Constants.STRING_TYPE) {
        value = (String) attrib.getAttribValue();
      }
      */
      if (attrib != null) {
          value = attrib.getAttribValue().toString();
      }

      return value;
  }

  /** Get the value of a specific Attribute. Only returns non-null
      if the Attribute exists and is of STRING_TYPE.
      @deprecated Use the getAttributeStringValue method instead.
   */
  public String getXMLAttributeStringValue(String attribName) 
  {
     return this.getAttributeStringValue(attribName);
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
  public void toXMLFile (String filename) 
  throws java.io.IOException
  {

    // open file writer
 //   try {
      Writer fileout = new BufferedWriter (new FileWriter(filename));
      // FileWriter fileout = new FileWriter(filename);
      toXMLWriter(fileout);
      fileout.close();
/*
    } catch (IOException e) {
      Log.error("Error: toXMLFile method hash trouble writing to "+ filename + " for writing.");
    }
*/

  }

  /** Write this object and all the objects it owns to the supplied
      OutputStream object as XDF. Supplying a populated XMLDeclAttributes
      Hashtable will result in the xml declaration being written at the
      begining of the outputstream (so when you do this, you will
      get well-formmed XML output for ANY object). For obvious reasons, only
      XDF objects will create *valid XDF* output.
      @deprecated Use the toXMLWriter method instead.
  */
  public void toXMLOutputStream (
                                   OutputStream outputstream,
                                   String indent,
                                   boolean dontCloseNode,
                                   String newNodeNameString,
                                   String noChildObjectNodeName
                                )
  throws java.io.IOException
  {

     Writer outputWriter = new BufferedWriter(new OutputStreamWriter(outputstream));
     toXMLWriter (outputWriter, indent, dontCloseNode, newNodeNameString, noChildObjectNodeName);

     // this *shouldnt* be needed, but tests with both Java 1.2.2 and 1.3.0
     // on SUN and Linux platforms show that it is. Hopefully we can remove
     // this in the future.
     outputWriter.close();

  }
 
  public void toXMLWriter ( 
                                Writer outputWriter
                          )
  throws java.io.IOException
  {

      toXMLWriter(outputWriter, "", false, null, null);

  }

  public void toXMLWriter ( 
                                Writer outputWriter,
                                String indent
                             )
  throws java.io.IOException
  {

      toXMLWriter(outputWriter, indent, false, null, null);

  }

  public void toXMLWriter (
                                Writer outputWriter,
                                String indent,
                                boolean dontCloseNode,
                                String newNodeNameString,
                                String noChildObjectNodeName
                             )
  throws java.io.IOException
  {

     String nodeNameString = 
         basicXMLWriter(outputWriter, indent, dontCloseNode, newNodeNameString, noChildObjectNodeName);
     if (Specification.getInstance().isPrettyXDFOutput() && nodeNameString != null)
          outputWriter.write(Constants.NEW_LINE);
  }

  protected String basicXMLWriter ( 
                                Writer outputWriter,
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
          outputWriter.write(indent); // indent node if desired
        outputWriter.write("<" + nodeNameString);   // print opening statement

      }

      // gather info about Attributes in this object/node
      Hashtable xmlInfo = getXMLInfo();

      // 2. Print out string object XML attributes EXCEPT for the one that
      //    matches PCDATAAttribute.
      ArrayList attribs = (ArrayList) xmlInfo.get("attribList");
      // is synchronized here correct?
      synchronized(attribs) {
        int size = attribs.size();
        for (int i = 0; i < size; i++) {
          Hashtable item = (Hashtable) attribs.get(i);
          outputWriter.write(" " + item.get("name") + "=\"");
          // this slows things down, should we use?
          writeOutAttribute(outputWriter, (String) item.get("value"));
        //  outputWriter.write((String) item.get("value"));
          outputWriter.write("\"");
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
          outputWriter.write(">");
          if ((Specification.getInstance().isPrettyXDFOutput()) && (pcdata == null))
             outputWriter.write(Constants.NEW_LINE);
        }

        // deal with object/list XML attributes, if any in our list
        int size = childObjs.size();
        for (int i = 0; i < size; i++) {
          Hashtable item = (Hashtable) childObjs.get(i);

          if (item.get("type") == Constants.LIST_TYPE)
          {

            List objectList = (List) item.get("value");
            indent = objectListToXMLWriter(outputWriter, objectList, indent);  
          }
          else if (item.get("type") == Constants.OBJECT_TYPE)
          {
            BaseObject containedObj = (BaseObject) item.get("value");
            if (containedObj != null) { // can happen from pre-allocation of axis values, etc (?)
              // shouldnt this be synchronized too??
              synchronized(containedObj) {
                indent = dealWithClosingGroupNodes(containedObj, outputWriter, indent);
                indent = dealWithOpeningGroupNodes(containedObj, outputWriter, indent);
                String newindent = indent + Specification.getInstance().getPrettyXDFOutputIndentation();
                containedObj.toXMLWriter(outputWriter, newindent);
              }
            }
          } else {
            // error: weird type, actually shouldnt occur. Is this needed??
            Log.errorln("Weird error: unknown XML attribute type for item:"+item);
          }

        }


        // print out PCDATA, if any
        if(pcdata != null)  {
          outputWriter.write(entifyString(pcdata));
        };

        // if there are no PCDATA or child objects/nodes then
        // we print out noChildObjectNodeName and close the node
        if ( childObjs.size() == 0 && pcdata == null && noChildObjectNodeName != null)
        {

          if (Specification.getInstance().isPrettyXDFOutput())
            outputWriter.write(indent + Specification.getInstance().getPrettyXDFOutputIndentation());

          outputWriter.write("<" + noChildObjectNodeName + "/>");

          if (Specification.getInstance().isPrettyXDFOutput())
            outputWriter.write(Constants.NEW_LINE);

        }

        // ok, now deal with closing the node
        if (nodeNameString != null) {

           indent = dealWithClosingGroupNodes((BaseObject) this, outputWriter, indent);

          if (Specification.getInstance().isPrettyXDFOutput() && pcdata == null)
                outputWriter.write(indent);

          if (!dontCloseNode)
              outputWriter.write("</"+nodeNameString+">");

        }

      } else {

        if (nodeNameString != null) {
	    if (dontCloseNode) {
		// it may not have sub-objects, but we dont want to close it
		// (happens for group objects)
		outputWriter.write(">");
	    } else {
		// no sub-objects, just close this node
		outputWriter.write("/>");
	    }
	}

      }

//      if (Specification.getInstance().isPrettyXDFOutput()  && nodeNameString != null) 
//	  outputWriter.write(Constants.NEW_LINE);

      return nodeNameString;

    } //synchronize

  }

  /** A different invokation style for writing this object out to
      the indicated OutputStream.
      @deprecated Use the toXMLWriter method instead.
  */
  public void toXMLOutputStream ( OutputStream outputstream,
                                  String indent
                                )
  throws java.io.IOException
  {
     toXMLOutputStream(outputstream, indent, false, null, null);
  }

  /** A different invokation style. It has defaults for the XML Declaration
      setting standalone to "no" and version to the value of sXMLSpecVersion.
      Indentation starts as "".
      @deprecated Use the toXMLWriter method instead.
  */
  public void toXMLOutputStream (OutputStream outputstream)
  throws java.io.IOException
  {

     toXMLOutputStream(outputstream, "", false, null, null);

  }

  //
  // PROTECTED Methods
  //

  /** Set the XMLattributes of this object using the passed Attributes.
      An attribute will be appended to the objects list of XML attributes if 
      that attribute doesnt already exist.
   */
  // NOTE: this is essentially the Perl update method
  public void setAttributes (Attributes attrs) {

     synchronized (attribHash) {
      // set object attributes from an Attributes Object
      if (attrs != null) {
          // whip thru the list, setting each value
          int size = attrs.getLength();
          for (int i = 0; i < size; i++) {
             String name = attrs.getQName(i);
             String value = attrs.getValue(i); // yes, Attributes can only return strings 

             // set it as appropriate to the type
             if (name != null && value != null) {
                if (this.attribHash.containsKey(name)) { 
                   setAttribute(name,value);
                } else {
                   addAttribute(name,value);
                }
             }

          }

      }

     } //synchronize
  }

  /**
      @deprecated Use the setAttributes method instead.
   */
  public void setXMLAttributes (Attributes attrs) { this.setAttributes(attrs); }

  public void setAttribute (String name, String value) {

     if (this.attribHash.containsKey(name)) {
     
        String type = ((Attribute) this.attribHash.get(name)).getAttribType();
        if(type.equals(Constants.INTEGER_TYPE))
           // convert string to proper Integer
           ((Attribute) this.attribHash.get(name)).setAttribValue(new Integer(value));
        else if(type.equals(Constants.DOUBLE_TYPE))
           // convert string to proper Double
           ((Attribute) this.attribHash.get(name)).setAttribValue(new Double(value));
        else // string or Object
           ((Attribute) this.attribHash.get(name)).setAttribValue(value);

     } else { // its an add operation 
        Log.errorln("Cannot set XML attribute:"+name+" as it doesnt exist, use addAttribute() instead.");
     }

  }

  /**
      @deprecated Use the setAttribute method instead.
   */
  public void setXMLAttribute (String name, String value) { this.setAttribute(name,value); }

  /* Appends on an XML attribute into the object.
      @return: true if successfull.
  */
  // private for now, I dont see the need to allow users to add 'object' or 'number'
  // or etc. type XML attributes other than 'string'
  private boolean addAttribute (String name, Object value, String type) {

    if (!this.attribHash.containsKey(name)) { 

       this.attribOrder.add(name);

       //set up the attribute hashtable key with the default initial value
       this.attribHash.put(name, new Attribute(value, type));

       return true;

    } else {

       Log.warnln("Cannot addAttribute("+name+") as it already exists, use setAttribute() instead.");
       return false;

    }

  }

  /** Appends on an XML attribute into the object.
      @return: true if successfull.
   */
  public boolean addAttribute (String name, String value) {
     return addAttribute(name, (Object) value, Constants.STRING_TYPE);
  }

  /**
       Return a string representation of the corresponnding XML representation of this object.
   */
  public String toXMLString () 
  {

     // hurm. Cant figure out how to use BufferedWriter here. fooey.
     Writer outputWriter = (Writer) new StringWriter();
     try {
        // we use this so that newline *isnt* appended onto the last element node
        basicXMLWriter(outputWriter, "", false, null, null);
     } catch (java.io.IOException e) { 
        // weird. Out of memorY?
        Log.errorln("Cant got IOException for toXMLWriter() method within toXMLString().");
        Log.printStackTrace(e);
     }

     return outputWriter.toString();

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
     // Attributes Clone, deep copy
     synchronized (this.attribHash) {
      synchronized (cloneObj.attribHash) {
        cloneObj.attribHash = new Hashtable();
        Enumeration keys = this.attribHash.keys();
        while (keys.hasMoreElements()) {
          String key = (String) keys.nextElement();
          Attribute AttributeValue = (Attribute) this.attribHash.get(key);
          cloneObj.attribHash.put(key, AttributeValue.clone());
        }
        return cloneObj;
      }
    }

  }

  protected void resetAttributes() { 
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
          Attribute toRemove = (Attribute) attribHash.remove(attribute);
          attribHash.put(attribute, new Attribute(obj, toRemove.getAttribType()));
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

   protected String objectListToXMLWriter(Writer outputWriter, List objectList, String indent)
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
               indent = dealWithClosingGroupNodes(containedObj, outputWriter, indent);
               indent = dealWithOpeningGroupNodes(containedObj, outputWriter, indent);
               String newindent = indent + Specification.getInstance().getPrettyXDFOutputIndentation();
               containedObj.toXMLWriter(outputWriter, newindent);
            }
         }
      }

      return indent;
   }


  /** Basically this rearranges Attribute information into a more convient
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
      synchronized (attribOrder) {

      Iterator iter = attribOrder.iterator();
      while (iter.hasNext()) {
        String attribName = (String) iter.next();

        Attribute obj = (Attribute) attribHash.get(attribName);
        if (obj != null && obj.attribValue != null) {
          if ( obj.attribType == Constants.STRING_TYPE)
          {

            if (attribName.equals(Constants.PCDATA_ATTRIBUTE)) {
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
      }

      xmlInfo.put("attribList", attribList);
      xmlInfo.put("childObjList", objRefList);
    } //synchronize
      return xmlInfo;
  }

  /** Write message out to specified OutputStream Object.
  */
/*
  //declare as proteced, sub-classes may use --k.z. 10/17/2000
  protected void writeOut ( OutputStream outputstream, String msg ) 
  throws java.io.IOException
  {
      outputstream.write(msg.getBytes());
  }
*/

/*
  protected void writeOut ( OutputStream outputstream, char c ) 
  throws java.io.IOException
  {
      outputstream.write(c);
  }
*/

  /** Write out string object formatted so it may be a proper XML 
      (XDF) string in a node attribute. Basically, cannonical XML expects
      entities for such characters as quotation, apostrophe, lessthan, greater
      than signs and ampersands. Also, we entify the newLine and carriageReturn
      characters too.
   */
  protected void writeOutAttribute ( Writer outputWriter, String text) 
  throws java.io.IOException
  {

     outputWriter.write(entifyString(text));

/*
     StringCharacterIterator iter = new StringCharacterIterator(text);

     for(char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) 
     {

        switch (c) {
           // do what "Canonical XML" expects
           case '<':  outputWriter.write("&lt;"); continue;
           case '>':  outputWriter.write("&gt;"); continue;
           case '&':  outputWriter.write("&amp;"); continue;
           case '\'': outputWriter.write("&apos;"); continue;
           case '"':  outputWriter.write("&quot;"); continue;
           // now for our XDF specific stuff
           case '\n': outputWriter.write("&#010;"); continue;
           case '\r': outputWriter.write("&#013;"); continue;
           // all other characters
           default:   outputWriter.write(c); continue;
        }

     }
*/

  }

  protected String entifyString ( String text)
  {

       StringBuffer newStringBuf = new StringBuffer();
       StringCharacterIterator iter = new StringCharacterIterator(text);

       for(char c = iter.first(); c != CharacterIterator.DONE; c = iter.next())
       {

          switch (c) {
             // do what "Canonical XML" expects
             case '<':  newStringBuf.append("&lt;"); continue;
             case '>':  newStringBuf.append("&gt;"); continue;
             case '&':  newStringBuf.append("&amp;"); continue;
             case '\'': newStringBuf.append("&apos;"); continue;
             case '"':  newStringBuf.append("&quot;"); continue;
/*
             // now for our XDF specific stuff
             case '\n': newStringBuf.append("&#010;"); continue;
             case '\r': newStringBuf.append("&#013;"); continue;
*/
             // all other characters
             default:   newStringBuf.append(c); continue;
          }

       }

       return newStringBuf.toString();
  }

  /** Method determines if any of the group objects to which the passed object
      belongs are already opened and opens them If they arent already opened.
  */
  protected String dealWithOpeningGroupNodes (BaseObject obj, Writer outputWriter, String indent) 
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
            memberGroup.toXMLWriter(outputWriter, newIndent.toString(), true, null, null);
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
  protected String dealWithClosingGroupNodes (BaseObject obj, Writer outputWriter, String indent) 
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

            if (Specification.getInstance().isPrettyXDFOutput()) 
                outputWriter.write(indent);
            outputWriter.write("</" + openGroup.classXDFNodeName + ">");
            if (Specification.getInstance().isPrettyXDFOutput()) 
                outputWriter.write(Constants.NEW_LINE);
            this.openGroupNodeHash.remove(openGroup);
            // peel off some indent
            indent = indent.substring(0,indent.length() - Specification.getInstance().getPrettyXDFOutputIndentation().length());
          }
        }
      }
    }

    return indent;
  }

} // end of BaseObject Class

