
// XDF Structure Class
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.util.*;

/**
 * Structure.java 
 * @author: Kelly Zeng (kelly.zeng@commerceone.com)
 *          Brian Thomas (thomas@adc.gsfc.nasa.gov)
 * @version $Revision$
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

    classXMLName = "structure";

    // order matters! these are in *reverse* order of their 
    // occurence in the XDF DTD
    attribOrder.add(0,"noteList");
    attribOrder.add(0,"arrayList");
    attribOrder.add(0,"structList");
    attribOrder.add(0,"paramList");
    attribOrder.add(0,"description");
    attribOrder.add(0,"name");

    attribHash.put("noteList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("arrayList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("structList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("paramList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("description", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("name", new XMLAttribute(null, Constants.STRING_TYPE));

  };

  //
  //Get/Set Methods
  // take a 2nd look at the return value of the set methods, not efficient, standard???

  public String setName (String strName) 
  {
    ((XMLAttribute) attribHash.get("name")).setAttribValue(strName);
    return getName();
  }

  public String getName() 
  {
    return (String) ((XMLAttribute) attribHash.get("name")).getAttribValue();
  }

  public String setDescription (String strDesc) 
  {
    ((XMLAttribute) attribHash.get("description")).setAttribValue(strDesc);
    return getDescription();
  }

  public String getDescription() {
    return (String) ((XMLAttribute) attribHash.get("description")).getAttribValue();
  }

  public List setParamList(List param) {
    ((XMLAttribute) attribHash.get("paramList")).setAttribValue(param);
    return getParamList();
  }

  public List getParamList() {
    return (List) ((XMLAttribute) attribHash.get("paramList")).getAttribValue();
  }

  public List setStructList(List struct) {
    ((XMLAttribute) attribHash.get("structList")).setAttribValue(struct);
    return getStructList();
  }

  public List getStructList() {
    return (List) ((XMLAttribute) attribHash.get("structList")).getAttribValue();
  }

  public List setArrayList(List array) {
    ((XMLAttribute) attribHash.get("arrayList")).setAttribValue(array);
    return getArrayList();
  }

  public List getArrayList() {
    return (List) ((XMLAttribute) attribHash.get("arrayList")).getAttribValue();
  }


  public Note addNote (Note note) {
  }

  public Note setNote (Note note, int i) {
  }

  public Note removeNote (Note note, int i) {
  }
 
  public List setNoteList(List note) {
    ((XMLAttribute) attribHash.get("noteList")).setAttribValue(note);
    return getNoteList();
  }

  /** getNoteList
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

  /** addNote
   */
  public Note addNote(Note n) {
    getNoteList().add(n);
    return n;
  }

  /**removeNote
   * pass in Note
   */
   public boolean removeNote(Note what) {
     return removeFromList(what, getNoteList(), "noteList");
  }



  /**removeNote
   * pass in index
   * function overload
   */
  public boolean removeNote(int what) {
     return removeFromList(what, getNoteList(), "noteList");
  }

  /**getNote
   */
  public List getNotes() {
    return getNoteList();
  }

  /**addParameter
   */
  public Parameter addParameter(Parameter p) {
    getParamList().add(p);
    return p;
  }
  /**removeParameter
   * pass in Parameter
   */
  public boolean removeParameter(Parameter what) {
    return  removeFromList(what, getParamList(), "paramList");
  }

  /**removeParameter
   * pass in index
   */
  public boolean removeParameter(int what) {
    return removeFromList(what, getParamList(), "paramList");
  }

  public Array addArray(Array array) {
    getArrayList().add(array);
    return array;
  }

  /**removeArray
   * pass in Array
   */
  public boolean removeArray(Array what) {
    return removeFromList(what, getArrayList(), "arrayList");
  }

  /**removeArray
   * pass in index
   */
  public boolean removeArray(int what) {
    return removeFromList(what, getArrayList(), "arrayList");
  }

  /**addParamGroup
   */
  public ParameterGroup addParamGroup (ParameterGroup group) {
    if (group !=null) {
      //add the group to the groupOwnedHash
      paramGroupOwnedHash.add(group);
      return group;
    }
    else
      return null;

  }

  /**remoeParamGroup
   */
  public boolean removeParamGroup(ParameterGroup group) {
    return paramGroupOwnedHash.remove(group);

  }

}
