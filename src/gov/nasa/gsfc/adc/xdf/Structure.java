
// XDF Structure Class
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.util.*;

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


/**
 * Structure.java: XDF::Structure is a means of grouping/associating L<XDF::Parameter> objects, which hold
 * scientific content of the data, and L<XDF::Array> objects which hold the mathematical content
 * of the data. If an XDF::Structure holds a parameter with several XDF::Array objects then the
 * parameter is assumed to be applicable to all of the array child nodes. Sub-structure (e.g. other
 * XDF::Structure objects) may be held within a structure to create more fine-grained associations
 * between parameters and arrays.
 * @version $Revision$
 */

 /**
  * Description of class attributes:
  * name--
  * string containing the name of this XDF::Structure.
  * description
  * scalar string containing the description (long name) of this XDF::Structure.
  * paramList--
  * list reference to the XDF::Parameter objects held by this XDF::Structure.
  * structList
  * list reference to the XDF::Structure objects held by this XDF::Structure.
  * arrayList
  * list reference to the XDF::Array objects held by this XDF::Structure.
 */

public class Structure extends BaseObject {

  //
  //Fields
  //

  protected Set paramGroupOwnedHash = Collections.synchronizedSet(new HashSet());  //double check, init size?

  //
  // Constructor and related methods
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

  /** init -- special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init()
  {

    classXDFNodeName = "structure";

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0,"noteList");
    attribOrder.add(0,"arrayList");
    attribOrder.add(0,"structList");
    attribOrder.add(0,"paramList");
    attribOrder.add(0,"description");
    attribOrder.add(0,"name");

    //set up the attribute hashtable key with the default initial value
    attribHash.put("noteList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("arrayList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("structList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("paramList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("description", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("name", new XMLAttribute(null, Constants.STRING_TYPE));

  };

  //
  //Get/Set Methods
  //

  /**setName: set the *name* attribute
   * @return: the current *name* attribute
   */
  public String setName (String strName)
  {
    return (String) ((XMLAttribute) attribHash.get("name")).setAttribValue(strName);

  }

  /**getName
   * @return: the current *name* attribute
   */
  public String getName()
  {
    return (String) ((XMLAttribute) attribHash.get("name")).getAttribValue();
  }

   /**setDescription: set the *description* attribute
   * @return: the current *description* attribute
   */
  public String setDescription (String strDesc)
  {
    return (String) ((XMLAttribute) attribHash.get("description")).setAttribValue(strDesc);
  }

   /**getDescription
   * @return: the current *description* attribute
   */
  public String getDescription() {
    return (String) ((XMLAttribute) attribHash.get("description")).getAttribValue();
  }

  /**setParamList: set the *paramList* attribute
   * @return: the current *paramList* attribute
   */
  public List setParamList(List param) {
    return (List)((XMLAttribute) attribHash.get("paramList")).setAttribValue(param);
  }

  /**getParamList
   * @return: the current *paramList* attribute
   */
  public List getParamList() {
    return (List) ((XMLAttribute) attribHash.get("paramList")).getAttribValue();
  }

  /**setStructList: set the *structList* attribute
   * @return: the current *structList* attribute
   */
  public List setStructList(List struct) {
    return (List)((XMLAttribute) attribHash.get("structList")).setAttribValue(struct);
  }

  /**getStructList
   * @return: the current *structList* attribute
   */
  public List getStructList() {
    return (List) ((XMLAttribute) attribHash.get("structList")).getAttribValue();
  }

  /**setArrayList: set the *arrayList* attribute
   * @return: the current *arrayList* attribute
   */
  public List setArrayList(List array) {
    return (List) ((XMLAttribute) attribHash.get("arrayList")).setAttribValue(array);
  }

  /**getArrayList
   * @return: the current *arrayList* attribute
   */
  public List getArrayList() {
    return (List) ((XMLAttribute) attribHash.get("arrayList")).getAttribValue();
  }

  /**setNoteList: set the *noteList* attribute
   * @return: the current *noteList* attribute
   */
  public List setNoteList(List note) {
    return (List)((XMLAttribute) attribHash.get("noteList")).setAttribValue(note);
  }

  /**getNoteList
   * @return: the current *noteList* attribute
   */
  public List getNoteList() {
    return (List) ((XMLAttribute) attribHash.get("noteList")).getAttribValue();
  }

  /** setParamGroupOwnedHash
  */
  public Set setParamGroupOwnedHash(Set paramGroup)
  {
    paramGroupOwnedHash = paramGroup;
    return paramGroupOwnedHash;
  }

  /** getParamGroupOwnedHash
  */
  public Set getParamGroupOwnedHash()
  {
    return paramGroupOwnedHash;
  }

  //
  //Other PUBLIC Methods
  //

  /** addNote: insert an XDF::Note object into the noteList
   * @param: XDF::Note
   * @return: an XDF::Note object on success, null on failure
   */
  public Note addNote(Note n) {
    if (n == null) {
      Log.warn("in Structure.addNote(), the Note passed in is null");
      return null;
    }
    getNoteList().add(n);
    return n;
  }

  /**removeNote: removes an XDF::Note object from noteList
   * @param: Note to be removed
   * @return: true on success, false on failure
   */
   public boolean removeNote(Note what) {
     return removeFromList(what, getNoteList(), "noteList");
  }



  /**removeNote: removes an XDF::Note object from noteList
   * @param: list index number
   * @return: true on success, false on failure
   */
  public boolean removeNote(int index) {
     return removeFromList(index, getNoteList(), "noteList");
  }

   /**getNotes
   * @return: the current *noteList* held by this object
   */
  public List getNotes() {
    return getNoteList();
  }

 /** addParameter: insert an XDF::Parameter object into the paramList
   * @param: XDF::Parameter
   * @return: an XDF::Parameter object on success, null on failure
   */
  public Parameter addParameter(Parameter p) {
    if (p == null) {
      Log.warn("in Structure.addParameter, Parameter passed in is null");
      return null;
    }
    getParamList().add(p);
    return p;
  }
  /**removeParameter: removes an XDF::Parameter object from paramList
   * @param: Parameter to be removed
   * @return: true on success, false on failure
   */
  public boolean removeParameter(Parameter what) {
    return  removeFromList(what, getParamList(), "paramList");
  }

  /**removeParameter: removes an XDF::Parameter object from paramList
   * @param: list index number
   * @return: true on success, false on failure
   */
  public boolean removeParameter(int index) {
    return removeFromList(index, getParamList(), "paramList");
  }

  /** addStructure: insert an XDF::Structure object into the structList
   * @param: XDF::Structure
   * @return: an XDF::Structure object on success, null on failure
   */
  public Structure addStructure(Structure s) {
    if (s == null) {
      Log.warn("in Structure.addStructure(), Structure passed in is null");
      return null;
    }
    getStructList().add(s);
    return s;
  }

  /**removeStructure: removes an XDF::Structure object from structList
   * @param: Structure to be removed
   * @return: true on success, false on failure
   */
  public boolean removeStructure(Structure what) {
    return  removeFromList(what, getStructList(), "structList");
  }

  /**removeStructure: removes an XDF::Structure object from structList
   * @param: list index number
   * @return: true on success, false on failure
   */
  public boolean removeStructure(int index) {
    return removeFromList(index, getStructList(), "structList");
  }

  /**addArray: insert an XDF::Array object into the arrayList
   * @param: XDF::Array
   * @return: an XDF::Array object on success, null on failure
   */
  public Array addArray(Array array) {
    if (array == null) {
      Log.warn("in Structure.addArray(), Array passed in is null");
      return null;
    }
    getArrayList().add(array);
    return array;
  }

  /**removeArray: removes an XDF::Array object from arrayList
   * @param: Array to be removed
   * @return: true on success, false on failure
   */
  public boolean removeArray(Array what) {
    return removeFromList(what, getArrayList(), "arrayList");
  }

  /**removeArray: removes an XDF::Array object from arrayList
   * @param: list index number
   * @return: true on success, false on failure
   */
  public boolean removeArray(int index) {
    return removeFromList(index, getArrayList(), "arrayList");
  }

  /**addParamGroup: Insert an XDF::ParameterGroup object into this object.
   * @param: ParameterGroup to be added
   * @return:an XDF::ParameterGroup object reference on success, null on failure.
   */
  public ParameterGroup addParamGroup (ParameterGroup group) {
    if (group !=null) {
      //add the group to the groupOwnedHash
      paramGroupOwnedHash.add(group);
      return group;
    }
    else {
      Log.warn("in Structure.addParamGroup(). ParameterGroup passed in is null");
      return null;
    }
  }

  /**removeParamGroup: Remove an XDF::ParameterGroup object from the hashset--paramGroupOwnedHash
   * @param: ParameterGroup to be removed
   * @return: true on success, false on failure
   */
  public boolean removeParamGroup(ParameterGroup group) {
    if (group == null) {
      Log.warn("in Structure.removeParamGroup().  ParameterGroup passed in is null");
      return false;
    }
    return paramGroupOwnedHash.remove(group);
  }

}
