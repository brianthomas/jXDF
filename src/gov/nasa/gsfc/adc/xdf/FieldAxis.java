
// XDF FieldAxis Class
// CVS $Id$

// FieldAxis.java Copyright (C) 2000 Brian Thomas,
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

import java.util.Hashtable;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;


public class FieldAxis extends BaseObject implements AxisInterface{

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

  //
  //Get/Set Methods
  //

  /** set the *name* attribute
   */
  public void setName (String strName)
  {
     ((XMLAttribute) attribHash.get("name")).setAttribValue(strName);
  }

   /**getName
   * @return: the current *name* attribute
   */
  public String getName()
  {
    return (String) ((XMLAttribute) attribHash.get("name")).getAttribValue();
  }

   /** set the *description* attribute
   */
  public void setDescription (String strDesc)
  {
     ((XMLAttribute) attribHash.get("description")).setAttribValue(strDesc);
  }

  /**getDescription
   * @return: the current *description* attribute
   */
  public String getDescription() {
    return (String) ((XMLAttribute) attribHash.get("description")).getAttribValue();
  }

  /** set the *axisId* attribute
   */
  public void setAxisId (String strAxisId)
  {
     ((XMLAttribute) attribHash.get("axisId")).setAttribValue(strAxisId);

  }

   /**getAxisId
   * @return: the current *axisId* attribute
   */
  public String getAxisId()
  {
    return (String) ((XMLAttribute) attribHash.get("axisId")).getAttribValue();
  }

  /** set the *axisIdRef* attribute
   */
  public void setAxisIdRef (String strAxisIdRef)
  {
     ((XMLAttribute) attribHash.get("axisIdRef")).setAttribValue(strAxisIdRef);

  }

   /**getAxisIdRef
   * @return: the current *axisId* attribute
   */
  public String getAxisIdRef()
  {
    return (String) ((XMLAttribute) attribHash.get("axisIdRef")).getAttribValue();
  }

  /** set the *fieldList* attribute
   */
  public void setFieldList(List field) {
     ((XMLAttribute) attribHash.get("fieldList")).setAttribValue(field);
  }

  /**getFieldList
   * @return: the current *fieldList* attribute
   */
  public List getFieldList() {
    return (List) ((XMLAttribute) attribHash.get("fieldList")).getAttribValue();
  }

  /** 
  */
  public void setFieldGroupOwnedHash(Set fieldGroup)
  {
    fieldGroupOwnedHash = fieldGroup;
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

  //
  //Other PUBLIC Methods
  //

  /**addField: adds a field to this fieldAxis.
   * @param: field to be added
   * @return: field object ref on success, null on failure
   *
   */
  public Field addField (Field field) {
     if (field == null) {
      Log.warn("in FieldAxis.addField(),Field passed in is null");
      return null;
    }
    getFieldList().add(field);
    length++;
    return field;
  }

  /**getField: returns the field object at specified index on success, null on failure
   *
   */
  public Field getField (int index) {
    if ((index < 0) || (index > getFieldList().size()-1))  {//index out of range
      Log.error("in Field, getField().  index out of range");
      return null;
    }
    return (Field) getFieldList().get(index);
  }

  /**getFields: convience method that returns all field object held in this FieldAxis
   * object.
   * @return: a list of field object reference(ordered by field axis index)
   */
  public List getFields() {
    return getFieldList();
  }

  /**setField: Set the field object at indicated index.
   */

  public void setField(int index, Field field) {

    if ((index < 0) || (index > getFieldList().size()-1))  //index out of range
      return;

    if (index == getFieldList().size()-1) 
       getFieldList().add(field); //add a field
    else 
       getFieldList().set(index, field);  //replace the old field with the new one

  }

  public Field removeField(int index) {
    Log.error("in FieldAxis, removeField, method empty");
    return null;
  }

  public Field removeField(Field field) {
    Log.error("in FieldAxis, removeField, method empty");
    return null;
  }

  public DataFormat[] getDataFormatList() {
    DataFormat[] dataFormatList = new DataFormat[length];
    List fieldList = getFieldList();
    for (int i = 0; i < length; i++)
      dataFormatList[i]=(((Field) fieldList.get(i)).getDataFormat());
    return dataFormatList;
  }


  /**Insert a FieldGroup object into this object.
   * @param: FieldGroup to be added
   * @return:an FieldGroup object reference on success, null on failure.
   */
  public FieldGroup addFieldGroup (FieldGroup group) {
    //add the group to the groupOwnedHash
    fieldGroupOwnedHash.add(group);
    return group;

  }

  /** Remove a FieldGroup object from this object.
   * @param: FieldGroup to be removed
   * @return: true on success, false on failure
   */
  public boolean removeFieldGroup(FieldGroup group) {
    return fieldGroupOwnedHash.remove(group);
  }

  public Object clone() throws CloneNotSupportedException {
    FieldAxis cloneObj = (FieldAxis) super.clone();

     synchronized (this.fieldGroupOwnedHash) {
      synchronized(cloneObj.fieldGroupOwnedHash) {
        cloneObj.fieldGroupOwnedHash = Collections.synchronizedSet(new HashSet(this.fieldGroupOwnedHash.size()));
        Iterator iter = this.fieldGroupOwnedHash.iterator();
        while (iter.hasNext()) {
          cloneObj.fieldGroupOwnedHash.add(iter.next());
        }
      }
    }

    return cloneObj;
  }

  //
  // Private Methods
  //

  /** a special private method used by constructor methods to
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
    attribHash.put("align", new XMLAttribute(null, Constants.STRING_TYPE));  //double check??
    attribHash.put("description", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("name", new XMLAttribute(null, Constants.STRING_TYPE));

    length = 0;

  }

}

 /**
  * Modification History:
  * $Log$
  * Revision 1.10  2000/11/08 22:30:12  thomas
  * Changed set methods to return void. -b.t.
  *
  * Revision 1.9  2000/11/08 19:47:40  thomas
  * Updated header to include GPL blurb. -b.t.
  *
  * Revision 1.8  2000/11/06 21:16:34  kelly
  * added clone()  -k.z.
  *
  * Revision 1.7  2000/11/02 17:52:20  kelly
  * minor mix
  *
  * Revision 1.6  2000/11/01 21:59:58  thomas
  * Added add/removeFieldGroup methods. -b.t.
  *
  * Revision 1.5  2000/10/31 22:09:58  kelly
  * getDataFormatList() now returns DataFormat[], faster.  -k.z.
  *
  * Revision 1.4  2000/10/30 18:47:23  thomas
  * Quick bug fix. AddField method should be "addField"-b.t.
  *
  * Revision 1.3  2000/10/30 18:18:20  kelly
  * more completion  -k.z.
  *
  *
  */


