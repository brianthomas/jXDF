// XDF Parameter Class
// CVS $Id$
package gov.nasa.gsfc.adc.xdf;

import java.util.*;

/**
 * Parameter.java:
 * @version $Revision$
 */

 public class Parameter extends BaseObject{

 //
  // Constructor and related methods
  //

  /** The no argument constructor.
   */
  public Parameter ()
  {
    init();
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public Parameter ( Hashtable InitXDFAttributeTable )
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

    classXMLName = "parameter";

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0,"noteList");
    attribOrder.add(0,"valueList");
    attribOrder.add(0,"units");
    attribOrder.add(0,"datatype");
    attribOrder.add(0,"paramIdRef");
    attribOrder.add(0,"paramId");
    attribOrder.add(0,"description");
    attribOrder.add(0,"name");


    attribHash.put("noteList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("valueList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("units", new XMLAttribute(null, Constants.OBJECT_TYPE));
    attribHash.put("datatype", new XMLAttribute(null, Constants.OBJECT_TYPE));
    attribHash.put("paramIdRef", new XMLAttribute(null, Constants.STRING_TYPE));  //double check k.z.
    attribHash.put("paramId", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("description", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("name", new XMLAttribute(null, Constants.STRING_TYPE));

  };

  //
  //Get/Set Methods
  //

  public String setName (String strName)
  {
    return (String) ((XMLAttribute) attribHash.get("name")).setAttribValue(strName);

  }

  public String getName()
  {
    return (String) ((XMLAttribute) attribHash.get("name")).getAttribValue();
  }

  public String setDescription (String strDesc)
  {
    return (String) ((XMLAttribute) attribHash.get("description")).setAttribValue(strDesc);

  }

  public String getDescription() {
    return (String) ((XMLAttribute) attribHash.get("description")).getAttribValue();
  }


  public String setParamId (String strParam)
  {
    return (String) ((XMLAttribute) attribHash.get("paramId")).setAttribValue(strParam);

  }

  public String getParamId()
  {
    return (String) ((XMLAttribute) attribHash.get("paramId")).getAttribValue();
  }

  public String setParamIdRef (String strParam)
  {
    return (String) ((XMLAttribute) attribHash.get("paramIdRef")).setAttribValue(strParam);

  }

  public String getParamIdRef()
  {
    return (String) ((XMLAttribute) attribHash.get("paramIdRef")).getAttribValue();
  }

  public Units setUnits (Units units)
  {
    return (Units) ((XMLAttribute) attribHash.get("units")).setAttribValue(units);

  }

  public Units getUnits()
  {
    return (Units) ((XMLAttribute) attribHash.get("units")).getAttribValue();
  }

  public DataFormat setDatatype(DataFormat datatype)
  {
    return (DataFormat) ((XMLAttribute) attribHash.get("datatype")).setAttribValue(datatype);
  }

  public DataFormat getDatatype()
  {
    return (DataFormat) ((XMLAttribute) attribHash.get("datatype")).getAttribValue();
  }

  public List setValueList(List value) {
    return (List)((XMLAttribute) attribHash.get("valueList")).setAttribValue(value);
  }

  public List getValueList() {
    return (List) ((XMLAttribute) attribHash.get("valueList")).getAttribValue();
  }

  public List setNoteList(List note) {
    return (List)((XMLAttribute) attribHash.get("noteList")).setAttribValue(note);
  }

  public List getNoteList() {
    return (List) ((XMLAttribute) attribHash.get("noteList")).getAttribValue();
  }

  //
  //Other PUBLIC Methods
  //

    /** addValue: add a value to this object
    */
  public Value addValue(Value v) {
    getValueList().add(v);
    return v;
  }

  /**removeValue
   * pass in Value
   */
   public boolean removeValue(Value what) {
     return removeFromList(what, getValueList(), "valueList");
  }



  /**removeValue
   * pass in index
   * function overload
   */
  public boolean removeValue(int what) {
     return removeFromList(what, getValueList(), "valueList");
  }

  /**getValues: A convinience methods.  returns a list of values in this parameter
   */
  public List getValues() {
    return getValueList();
  }

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

  /**getNotes
   */
  public List getNotes() {
    return getNoteList();
  }

  /**addUnit
   */
  public Unit addUnit(Unit unit) {
    return  getUnits().addUnit(unit);
  }

  /**removeUnit
   * pass in Unit
   */
  public boolean removeUnit(Unit what) {
    return getUnits().removeUnit(what);
  }

  /**removeUnit
   * pass in index
   */
  public boolean removeUnit(int what) {
    return getUnits().removeUnit(what);
  }

 }