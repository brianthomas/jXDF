

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

/**describe an Axis that consists of Fields
 */

public class FieldAxis extends BaseObjectWithXMLElements implements AxisInterface {

   //
   // Fields
   //

   /* XML attribute names */
   private static final String NAME_XML_ATTRIBUTE_NAME = new String("name");
   private static final String DESCRIPTION_XML_ATTRIBUTE_NAME = new String("description");
   private static final String ALIGN_XML_ATTRIBUTE_NAME = new String("align");
   private static final String ID_XML_ATTRIBUTE_NAME = new String("axisId");
   private static final String IDREF_XML_ATTRIBUTE_NAME = new String("axisIdRef");
   private static final String FIELDLIST_XML_ATTRIBUTE_NAME = new String("fieldList");

   /**length of the FieldAxis
   */
   protected int length;

   /** This field stores object references to those field group objects
    * to which this FieldAxis object belongs
    */
   protected Set fieldGroupOwnedHash = Collections.synchronizedSet(new HashSet());


   //
   // Constructors
   //

   /** The no argument constructor.
    */
   public FieldAxis ()
   {
      init();
   }

   /**
    *  create a fieldAxis with desired dimension
    *  use setFieldList() to set fields
    */
   public FieldAxis (int fieldDimension)
   {
      init();
      length = fieldDimension;
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
   // Get/Set Methods
   //

  /** set the *name* attribute
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

   /** set the *description* attribute
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

 /** set the *align* attribute
   */
  public void setAlign(String strName)
  {
      ((XMLAttribute) attribHash.get(ALIGN_XML_ATTRIBUTE_NAME)).setAttribValue(strName);
  }

   /**
   * @return the current *align* attribute
   */
  public String getAlign()
  {
     return (String) ((XMLAttribute) attribHash.get(ALIGN_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the *axisId* attribute
   */
  public void setAxisId (String strAxisId)
  {
     ((XMLAttribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).setAttribValue(strAxisId);

  }

   /**
   * @return the current *axisId* attribute
   */
  public String getAxisId()
  {
    return (String) ((XMLAttribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the *axisIdRef* attribute
   */
  public void setAxisIdRef (String strAxisIdRef)
  {
     ((XMLAttribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).setAttribValue(strAxisIdRef);

  }

   /**
   * @return the current *axisId* attribute
   */
  public String getAxisIdRef()
  {
    return (String) ((XMLAttribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the *fieldList* attribute
   */
  public void setFieldList(List field) {
     ((XMLAttribute) attribHash.get(FIELDLIST_XML_ATTRIBUTE_NAME)).setAttribValue(field);
  }

  /**
   * @return the current *fieldList* attribute
   */
  public List getFieldList() {
    return (List) ((XMLAttribute) attribHash.get(FIELDLIST_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /**set the fieldGroupOwnedHash
  */
  public void setFieldGroupOwnedHash(Set fieldGroup)
  {
    fieldGroupOwnedHash = fieldGroup;
  }

  /** return the fieldGroupOwnedHash
  */
  public Set getFieldGroupOwnedHash()
  {
    return fieldGroupOwnedHash;
  }

  /** return the length of this axis (eg number of axis value objects)
   *
   */
  public int getLength() {
    return length;
  }

  //
  //Other PUBLIC Methods
  //

  /** adds a field to this fieldAxis.
   * @param field to be added
   * @return a field object that is added
   *
   */
  public Field addField (Field field) {
    getFieldList().add(field);
    length++;
    return field;
  }

  /** returns the field object at specified index on success, null on failure
   *
   */
  public Field getField (int index) {
    if ((index < 0) || (index > getFieldList().size()-1))  {//index out of range
      Log.error("in Field, getField().  index out of range");
      return null;
    }
    return (Field) getFieldList().get(index);
  }

  /** convenience method that returns all field object held in this FieldAxis
   * object.
   * @return a list of field object reference(ordered by field axis index)
   */
  public List getFields() {
    return getFieldList();
  }

  /** Set the field object at indicated index.
   */

  public void setField(int index, Field field) {

    if ((index < 0) || (index > getFieldList().size()-1))  //index out of range
      return;

    if (index == getFieldList().size()-1) 
       getFieldList().add(field); //add a field
    else 
       getFieldList().set(index, field);  //replace the old field with the new one

  }
  /**<b> NOT CURRENTLY IMPLEMENTED</b> needs to define the impact on dataCube
   */
  public Field removeField(int index) {
    Log.error("in FieldAxis, removeField, method empty");
    return null;
  }
 /**<b> NOT CURRENTLY IMPLEMENTED</b> needs to define the impact on dataCube
   */
  public Field removeField(Field field) {
    Log.error("in FieldAxis, removeField, method empty");
    return null;
  }
  /**returns the list of dataFormat
   */
  public DataFormat[] getDataFormatList() {
    DataFormat[] dataFormatList = new DataFormat[length];
    List fieldList = getFieldList();
    for (int i = 0; i < length; i++)
      dataFormatList[i]=(((Field) fieldList.get(i)).getDataFormat());
    return dataFormatList;
  }


  /**Insert a FieldGroup object into this object.
   * @param group - FieldGroup to be added
   * @return an FieldGroup object reference on success, null on failure.
   */
  public FieldGroup addFieldGroup (FieldGroup group) {
    //add the group to the groupOwnedHash
    fieldGroupOwnedHash.add(group);
    return group;

  }

  /** Remove a FieldGroup object from this object.
   * @param FieldGroup to be removed
   * @return true on success, false on failure
   */
  public boolean removeFieldGroup(FieldGroup group) {
    return fieldGroupOwnedHash.remove(group);
  }
  /**deep copy of the FieldAxis
   */
  public Object clone() throws CloneNotSupportedException {
    FieldAxis cloneObj = (FieldAxis) super.clone();

     synchronized (this.fieldGroupOwnedHash) {
      synchronized(cloneObj.fieldGroupOwnedHash) {
        cloneObj.fieldGroupOwnedHash = Collections.synchronizedSet(new HashSet(this.fieldGroupOwnedHash.size()));
        Iterator iter = this.fieldGroupOwnedHash.iterator();
        while (iter.hasNext()) {
          cloneObj.fieldGroupOwnedHash.add(((Group)iter.next()).clone());
        }
      }
    }

    return cloneObj;
  }

  //
  // Protected Methods
  //

  /** A special private method used by constructor methods to
   *  conveniently build the XML attribute list for a given class.
   */
  protected void init ()
  {

    super.init();

    classXDFNodeName = "fieldAxis";

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0, FIELDLIST_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, IDREF_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, ID_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, ALIGN_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, DESCRIPTION_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, NAME_XML_ATTRIBUTE_NAME);

     //set up the attribute hashtable key with the default initial value

     //set the minimum array size(essentially the size of the axis)
    attribHash.put(FIELDLIST_XML_ATTRIBUTE_NAME, new XMLAttribute(Collections.synchronizedList(new ArrayList(Specification.getInstance().getDefaultDataArraySize())), Constants.LIST_TYPE));
    attribHash.put(IDREF_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put(ID_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put(ALIGN_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));  //double check??
    attribHash.put(DESCRIPTION_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put(NAME_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));

    length = 0;

  }

}

 /**
  * Modification History:
  * $Log$
  * Revision 1.16  2001/05/16 22:47:30  huang
  * added/modified several conveniencemethods
  *
  * Revision 1.15  2001/05/04 20:32:15  thomas
  * changed baseObject superclass to BaseObjectWithXMLElements.
  * added super.init() to init().
  *
  * Revision 1.14  2001/02/07 18:44:04  thomas
  * Converted XML attribute decl
  * to use constants (final static fields within the object). These
  * are private decl for now. -b.t.
  *
  * Revision 1.13  2001/01/19 17:24:07  thomas
  * *** empty log message ***
  *
  * Revision 1.12  2000/11/27 16:57:45  thomas
  * Made init method protected so that extending
  * Dataformats may make use of them. -b.t.
  *
  * Revision 1.11  2000/11/16 19:58:41  kelly
  * fixed documentation.  -k.z.
  *
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


