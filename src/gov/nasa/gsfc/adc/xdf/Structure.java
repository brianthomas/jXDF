

// XDF Structure Class
// CVS $Id$

// Structure.java Copyright (C) 2000 Brian Thomas,
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Structure is a means of grouping/associating Parameter objects, which hold
 * scientific content of the data, and Array objects which hold the mathematical content
 * of the data. If a Structure holds a parameter with several Array objects then the
 * parameter is assumed to be applicable to all of the array child nodes. Sub-structure (e.g. other
 * Structure objects) may be held within a structure to create more fine-grained associations
 * between parameters and arrays.
 * @version $Revision$
 */

 /**
  * Description of class attributes:
  * name--
  * string containing the name of this Structure.
  * description
  * scalar string containing the description (long name) of this Structure.
  * paramList--
  * list reference to the Parameter objects held by this Structure.
  * structList
  * list reference to the Structure objects held by this Structure.
  * arrayList
  * list reference to the Array objects held by this Structure.
 */

public class Structure extends BaseObjectWithXMLElements {

   //
   //Fields
   //

   /* XML attribute names */
   private static final String NAME_XML_ATTRIBUTE_NAME = new String("name");
   private static final String DESCRIPTION_XML_ATTRIBUTE_NAME = new String("description");
   private static final String PARAMETERLIST_XML_ATTRIBUTE_NAME = new String("paramList");
   private static final String STRUCTURELIST_XML_ATTRIBUTE_NAME = new String("structList");
   private static final String ARRAYLIST_XML_ATTRIBUTE_NAME = new String("arrayList");
   private static final String NOTELIST_XML_ATTRIBUTE_NAME = new String("noteList");


   /** This field stores object references to those parameter group objects
    * to which this parameter object belongs
    */
   protected Set paramGroupOwnedHash = Collections.synchronizedSet(new HashSet());

   //
   // Constructor
   //

  /** The no argument constructor.
   */
  public Structure ()
  {

      // init the XML attributes (to defaults)
      init();

  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public Structure ( Hashtable InitXDFAttributeTable )
  {

     // init the XML attributes (to defaults)
     init();

     // init the value of selected XML attributes to HashTable values
     hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  //
  // Get/Set Methods
  //

  /**set the *name* attribute
   */
  public void setName (String strName)
  {
      ((XMLAttribute) attribHash.get(NAME_XML_ATTRIBUTE_NAME)).setAttribValue(strName);
  }

  /**
   * @return the current *name* attribute
   */
  public String getName()
  {
      return (String) ((XMLAttribute) attribHash.get(NAME_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

   /**set the *description* attribute
   */
  public void setDescription (String strDesc)
  {
     ((XMLAttribute) attribHash.get(DESCRIPTION_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
  }

   /**
   * @return the current *description* attribute
   */
  public String getDescription() {
    return (String) ((XMLAttribute) attribHash.get(DESCRIPTION_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /**set the *paramList* attribute
   */
  public void setParamList(List param) {
     ((XMLAttribute) attribHash.get(PARAMETERLIST_XML_ATTRIBUTE_NAME)).setAttribValue(param);
  }

  /**
   * @return the current *paramList* attribute
   * @deprecated use getParameters method instead
   */
  public List getParamList() {
    return (List) ((XMLAttribute) attribHash.get(PARAMETERLIST_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /**
  * @return the current *paramList* attribute
  */
  public List getParameters() {
    return (List) ((XMLAttribute) attribHash.get(PARAMETERLIST_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /**set the *structList* attribute
   */
  public void setStructList(List structList) {
    ((XMLAttribute) attribHash.get(STRUCTURELIST_XML_ATTRIBUTE_NAME)).setAttribValue(structList);
  }

  /**
   * @return the current *structList* attribute
   */
  public List getStructList() {
    return (List) ((XMLAttribute) attribHash.get(STRUCTURELIST_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /**set the *arrayList* attribute
   */
  public void setArrayList(List array) {
     ((XMLAttribute) attribHash.get(ARRAYLIST_XML_ATTRIBUTE_NAME)).setAttribValue(array);
  }

  /**
   * @return the current *arrayList* attribute
   */
  public List getArrayList() {
    return (List) ((XMLAttribute) attribHash.get(ARRAYLIST_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /**set the *noteList* attribute
   */
  public void setNotes (List notes) {
    ((XMLAttribute) attribHash.get(NOTELIST_XML_ATTRIBUTE_NAME)).setAttribValue(notes);
  }

  /**
   * @return the current list of notes held by this structure.
   */
  public List getNotes () {
    return (List) ((XMLAttribute) attribHash.get(NOTELIST_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /**
   * @return the current *noteList* attribute
   * @deprecated Outdated method. Use getNotes instead.
   */
  public List getNoteList () {
    return (List) ((XMLAttribute) attribHash.get(NOTELIST_XML_ATTRIBUTE_NAME)).getAttribValue();
  } 

  /**
  */
  public void setParamGroupOwnedHash(Set paramGroup)
  {
    paramGroupOwnedHash = paramGroup;
  }

  /** getParamGroupOwnedHash
  */
  public Set getParamGroupOwnedHash()
  {
    return paramGroupOwnedHash;
  }


  //
  // Protected Get/set
  //

  //
  //Other PUBLIC Methods
  //

  /**  insert an Note object into the noteList
   * @param n - Note to be added
   * @return an Note object
   */
  public boolean addNote(Note n) {
    getNoteList().add(n);
    return true;
  }

  /**removes an Note object from noteList
   * @param what - Note to be removed
   * @return true on success, false on failure
   */
   public boolean removeNote(Note what) {
     return removeFromList(what, getNoteList(), NOTELIST_XML_ATTRIBUTE_NAME);
  }



  /** removes an Note object from noteList
   * @param index - list index number of the Note to be removed
   * @return true on success, false on failure
   */
  public boolean removeNote(int index) {
     return removeFromList(index, getNoteList(), NOTELIST_XML_ATTRIBUTE_NAME);
  }

 /** Insert a Parameter object into the paramter list.  
   * @param p - Parameter
   * @return an Parameter object
   */
  public boolean addParameter(Parameter p) {
    getParamList().add(p);
    return true;
  }
  /** removes an Parameter object from the parameter list. 
   * @param what - Parameter to be removed
   * @return true on success, false on failure
   */
  public boolean removeParameter(Parameter what) {
    return  removeFromList(what, getParamList(), PARAMETERLIST_XML_ATTRIBUTE_NAME);
  }

  /** removes an Parameter object from paramList
   * @param index - list index number of the Parameter to be removed
   * @return true on success, false on failure
   */
  public boolean removeParameter(int index) {
    return removeFromList(index, getParamList(), PARAMETERLIST_XML_ATTRIBUTE_NAME);
  }

  /**  insert an Structure object into the structList
   * @param s - Structure to be added
   * @return an Structure object
   */
  public boolean addStructure(Structure s) {
    getStructList().add(s);
    return true;
  }

  /** removes an Structure object from structList
   * @param what - Structure to be removed
   * @return true on success, false on failure
   */
  public boolean removeStructure(Structure what) {
    return  removeFromList(what, getStructList(), STRUCTURELIST_XML_ATTRIBUTE_NAME);
  }

  /** removes an Structure object from structList
   * @param index - list index number of the Structure to be removed
   * @return true on success, false on failure
   */
  public boolean removeStructure(int index) {
    return removeFromList(index, getStructList(), STRUCTURELIST_XML_ATTRIBUTE_NAME);
  }

  /** insert an Array object into the arrayList
   * @param array - Array to be added
   * @return an Array object
   */
  public boolean addArray(Array array) {
    getArrayList().add(array);
    return true;
  }

  /** removes an Array object from arrayList
   * @param what - Array to be removed
   * @return true on success, false on failure
   */
  public boolean removeArray(Array what) {
    return removeFromList(what, getArrayList(), ARRAYLIST_XML_ATTRIBUTE_NAME);
  }

  /** emoves an Array object from arrayList
   * @param index - list index number of the Array to be removed
   * @return true on success, false on failure
   */
  public boolean removeArray(int index) {
    return removeFromList(index, getArrayList(), ARRAYLIST_XML_ATTRIBUTE_NAME);
  }

  /** Insert an ParameterGroup object into this object.
   * @param ParameterGroup to be added
   * @return an ParameterGroup object reference
   */
  public boolean addParamGroup (ParameterGroup group) {
    //add the group to the groupOwnedHash
    paramGroupOwnedHash.add(group);
    return true;
  }

  /** Remove an ParameterGroup object from the hashset--paramGroupOwnedHash
   * @param group - ParameterGroup to be removed
   * @return true on success, false on failure
   */
  public boolean removeParamGroup(ParameterGroup group) {
    return paramGroupOwnedHash.remove(group);
  }

  public Object clone() throws CloneNotSupportedException
  {

     Structure cloneObj = (Structure) super.clone();

     //deep copy of the paramGroupOwnedHash
     synchronized (this.paramGroupOwnedHash) {
        synchronized(cloneObj.paramGroupOwnedHash) {
          cloneObj.paramGroupOwnedHash = Collections.synchronizedSet(new HashSet(this.paramGroupOwnedHash.size()));
          Iterator iter = this.paramGroupOwnedHash.iterator();
          while (iter.hasNext()) {
            cloneObj.paramGroupOwnedHash.add(((Group)iter.next()).clone());
          }
        }
      }

      return cloneObj;

   }

   // 
   // Protected Methods
   //

   /** Special method used by constructor methods to
     *  convienently build the XML attribute list for a given class.
     */
   // overrides BaseObjectw/XMLElements.init() method
   protected void init()
   {
     super.init();

     classXDFNodeName = "structure";

     // order matters! these are in *reverse* order of their
     // occurence in the XDF DTD
     attribOrder.add(0, NOTELIST_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, ARRAYLIST_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, STRUCTURELIST_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, PARAMETERLIST_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, DESCRIPTION_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, NAME_XML_ATTRIBUTE_NAME);

     //set up the attribute hashtable key with the default initial value
     attribHash.put(NOTELIST_XML_ATTRIBUTE_NAME, new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
     attribHash.put(ARRAYLIST_XML_ATTRIBUTE_NAME, new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
     attribHash.put(STRUCTURELIST_XML_ATTRIBUTE_NAME, new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
     attribHash.put(PARAMETERLIST_XML_ATTRIBUTE_NAME, new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
     attribHash.put(DESCRIPTION_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
     attribHash.put(NAME_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));

  };


}

/* Modification History:
 *
 * $Log$
 * Revision 1.21  2001/07/11 22:35:21  thomas
 * Changes related to adding valueList or removeal of unneeded interface files.
 *
 * Revision 1.20  2001/06/26 21:22:25  huang
 * changed return type to boolean for all addObject()
 *
 * Revision 1.19  2001/05/10 21:41:06  thomas
 * minor typesetting changes.
 *
 * Revision 1.18  2001/05/04 20:36:51  thomas
 * moved out type attrib and some methods to XDF class.
 * some small changes to accomodate inheriting XDF from Structure.
 *
 * Revision 1.17  2001/05/02 18:16:39  thomas
 * Minor changes related to API standardization effort.
 *
 * Revision 1.16  2001/02/07 18:44:03  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.15  2000/11/16 20:09:19  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.14  2000/11/09 23:04:56  thomas
 * Updated version, made changes to allow extension
 * to other dataformats (e.g. FITSML). -b.t.
 *
 * Revision 1.13  2000/11/09 04:32:05  thomas
 * Minor 'hack' to add the 'type' attribute. Strictly
 * speaking, this attribute only occurs on the XDF
 * root node and not all structures. In the future
 * we either need to spawn a new class for the Root
 * node or figure another workaround. -b.t.
 *
 * Revision 1.12  2000/11/08 22:30:11  thomas
 * Changed set methods to return void. -b.t.
 *
 * Revision 1.11  2000/11/08 20:10:04  thomas
 * Trimmed down import path to just needed classes -b.t
 *
 * Revision 1.10  2000/11/06 21:17:10  kelly
 * added clone()  -k.z.
 *
 * Revision 1.9  2000/10/23 18:32:39  thomas
 * Changed to allow reading in of XDF files. loadfromXDFfile
 * method implemented. -b.t.
 *
 * Revision 1.8  2000/10/10 19:15:22  cvs
 * Added History section to end of file. -b.t.
 *
 *
 */


