// XDF FieldAxis Class
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.util.*;

// FieldAxis.java Copyright (C) 2000 Brian Thomas,
// ADC/GSFC-NASA, Code 631, Greenbelt MD, 20771




public class FieldAxis extends BaseObject{

  //
 //Fields
 //

  //length of the FieldAxis
  protected int length;

  /** This field stores object references to those field group objects
   * to which this FieldAxis object belongs
  */
  protected Set fieldGroupOwnedHash = Collections.synchronizedSet(new HashSet());

  /** The no argument constructor.
   */
  public FieldAxis ()
  {
    init();

  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public FieldAxis ( Hashtable InitXDFAttributeTable )
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

    classXDFNodeName = "fieldAxis";

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0,"fieldList");
    attribOrder.add(0,"axisIdRef");
    attribOrder.add(0,"axisId");
    attribOrder.add(0,"align");  //not sure what it is???
    attribOrder.add(0,"description");
    attribOrder.add(0,"name");

     //set up the attribute hashtable key with the default initial value

     //set the minimum array size(essentially the size of the axis)
    attribHash.put("fieldList", new XMLAttribute(Collections.synchronizedList(new ArrayList(super.sDefaultDataArraySize)), Constants.LIST_TYPE));
    attribHash.put("axisIdRef", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("axisId", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("aligh", new XMLAttribute(null, Constants.STRING_TYPE));  //double check??
    attribHash.put("description", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("name", new XMLAttribute(null, Constants.STRING_TYPE));

    length = 0;

  }


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

  /**setAxisId: set the *axisId* attribute
   * @return: the current *axisId* attribute
   */
  public String setAxisId (String strAxisId)
  {
    return (String) ((XMLAttribute) attribHash.get("axisId")).setAttribValue(strAxisId);

  }

   /**getAxisId
   * @return: the current *axisId* attribute
   */
  public String getAxisId()
  {
    return (String) ((XMLAttribute) attribHash.get("axisId")).getAttribValue();
  }

  /**setAxisIdRef: set the *axisIdRef* attribute
   * @return: the current *axisIdRef* attribute
   */
  public String setAxisIdRef (String strAxisIdRef)
  {
    return (String) ((XMLAttribute) attribHash.get("axisIdRef")).setAttribValue(strAxisIdRef);

  }

   /**getAxisIdRef
   * @return: the current *axisId* attribute
   */
  public String getAxisIdRef()
  {
    return (String) ((XMLAttribute) attribHash.get("axisIdRef")).getAttribValue();
  }

  /**setFieldList: set the *fieldList* attribute
   * @return: the current *fieldList* attribute
   */
  public List setFieldList(List field) {
    return (List)((XMLAttribute) attribHash.get("fieldList")).setAttribValue(field);
  }

  /**getFieldList
   * @return: the current *fieldList* attribute
   */
  public List getFieldList() {
    return (List) ((XMLAttribute) attribHash.get("fieldList")).getAttribValue();
  }

  /** setFieldGroupOwnedHash
  */
  public Set setFieldGroupOwnedHash(Set fieldGroup)
  {
    fieldGroupOwnedHash = fieldGroup;
    return fieldGroupOwnedHash;
  }

  /** getFieldGroupOwnedHash
  */
  public Set getFieldGroupOwnedHash()
  {
    return fieldGroupOwnedHash;
  }

  /**getLength: Get the length of this axis (eg number of axis value objects)
   *
   */
  public int getLength() {
    return length;
  }

  public List getDataFormatList() {
    return null;
  }
}


