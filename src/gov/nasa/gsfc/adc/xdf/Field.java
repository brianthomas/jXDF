
// XDF Field Class
// CVS $Id$


// Field.java Copyright (C) 2000 Brian Thomas,
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

/** Attribute Description:
 * name--
 * The STRING description (short name) of this Field.
 * description--
 * A String description (long name) of this Field.
 * fieldId--
 * A string holding the field id of this Field.
 * fieldIdRef--
 * A string holding the field id reference to another field.
 * noteList--
 * a list of the Note objects held by this field.
 * dataFormat--
 * an OBJECT REF of the DataFormat object for data within this field.
 * relation--
 * an OBJECT REF  of the Relationship object for this field.
 * units--
 * an OBJECT REF of the Units object of this field. The Units object
 * is used to hold the  Unit objects.
 */

package gov.nasa.gsfc.adc.xdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

/**an Field describes a field at a given index along a fieldAxis
   @version $Revision$
 */

public class Field extends BaseObjectWithXMLElements {

   //
   // Fields
   //

   /* XML attribute names */
   private static final String NAME_XML_ATTRIBUTE_NAME = new String("name");
   private static final String DESCRIPTION_XML_ATTRIBUTE_NAME = new String("description");
   private static final String ID_XML_ATTRIBUTE_NAME = new String("fieldId");
   private static final String IDREF_XML_ATTRIBUTE_NAME = new String("fieldIdRef");
   private static final String CONVERSION_XML_ATTRIBUTE_NAME = "conversion";
   private static final String DATAFORMAT_XML_ATTRIBUTE_NAME = "dataFormat";
   private static final String UNITS_XML_ATTRIBUTE_NAME = "units";
   private static final String RELATION_XML_ATTRIBUTE_NAME = "relation";
   private static final String NOTELIST_XML_ATTRIBUTE_NAME = "noteList";

   //
   // Constructors
   //

   //no-arg constructor
   public Field ()
   {
      init();
   }

   /**  This constructor takes a Java Hashtable as an initializer of
        the XML attributes of the object to be constructed. The
        Hashtable key/value pairs coorespond to the class XDF attribute
        names and their desired values.
     */
   public Field ( Hashtable InitXDFAttributeTable )
   {

      // init the XML attributes (to defaults)
      init();

      // init the value of selected XML attributes to HashTable values
      hashtableInitXDFAttributes(InitXDFAttributeTable);

   }

   //
   // Get/Set Methods
   //

   /** Set the *name* attribute
       @return the current *name* attribute
    */
   public void setName (String strName)
   {
      ((Attribute) attribHash.get(NAME_XML_ATTRIBUTE_NAME)).setAttribValue(strName);
   }

   /**
       @return the current *name* attribute
    */
   public String getName()
   {
      return (String) ((Attribute) attribHash.get(NAME_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *description* attribute
       @return the current *description* attribute
    */
   public void setDescription (String strDesc)
   {
      ((Attribute) attribHash.get(DESCRIPTION_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
   }

   /**
       @return the current *description* attribute
   */
   public String getDescription() {
      return (String) ((Attribute) attribHash.get(DESCRIPTION_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *fieldId* attribute
       @return the current *fieldId* attribute
    */
   public void setFieldId (String strField)
   {
      ((Attribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).setAttribValue(strField);
   }

   /**
       @return the current *fieldId* attribute
    */
   public String getFieldId()
   {
      return (String) ((Attribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *fieldIdRef* attribute
       @return the current *fieldIdRef* attribute
    */
   public void setFieldIdRef (String strField)
   {
      ((Attribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).setAttribValue(strField);
   }

   /**
       @return the current *fieldIdRef* attribute
    */
   public String getFieldIdRef()
   {
      return (String) ((Attribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** 
    *  Set how to convert values of the data in this field. 
    */
   public void setConversion(Conversion value)
   {
        ((Attribute) attribHash.get(CONVERSION_XML_ATTRIBUTE_NAME)).setAttribValue(value);
   }

   /**
    *  Get how to convert values of the data in this field. 
    * @return the current *dataFormat* object
    */
   public Conversion getConversion()
   {
      return (Conversion) ((Attribute) attribHash.get(CONVERSION_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *units* attribute
       @return the current *units* attribute
    */
   public void setUnits (Units units)
   {
      ((Attribute) attribHash.get(UNITS_XML_ATTRIBUTE_NAME)).setAttribValue(units);
   }

   public Units getUnits()
   {
      return (Units) ((Attribute) attribHash.get(UNITS_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /**  set the *dataFormat* attribute
       @return the current *dataFormat* attribute
    */
   public void setDataFormat (DataFormat dataFormat)
   {
       ((Attribute) attribHash.get(DATAFORMAT_XML_ATTRIBUTE_NAME)).setAttribValue(dataFormat);
   }

   /**
   * @return the current *dataFormat* attribute
   */
   public DataFormat getDataFormat()
   {
      return (DataFormat) ((Attribute) attribHash.get(DATAFORMAT_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** Set the *noteList* attribute
       @deprecated You should use add/remove methods instead.
    */
   public void setNoteList(List note)
   {
      ((Attribute) attribHash.get(NOTELIST_XML_ATTRIBUTE_NAME)).setAttribValue(note);
   }

   /**
       @return the current *noteList* attribute
   */
   public List getNoteList()
   {
      return (List) ((Attribute) attribHash.get(NOTELIST_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /**  set the *relationship* attribute
       @return the current *dataFormat* attribute
    */
   public void setRelationship (FieldRelationship fieldRelation)
   {
       ((Attribute) attribHash.get(RELATION_XML_ATTRIBUTE_NAME)).setAttribValue(fieldRelation);
   }
 
   /**
       @return the current *relationship* attribute
   */
   public FieldRelationship getRelationship ()
   {
      return (FieldRelationship) ((Attribute) attribHash.get(RELATION_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   //
   // Other public methods
   //

  /**insert an Note object into the list of notes in this Field object
   * @param Note
   * @return an Note object
   */
  public boolean addNote(Note n) {
    return getNoteList().add(n);
  }

  /** removes an Note object from the list of notes in this Field object
   * @param Note to be removed
   * @return true on success, false on failure
   */
   public boolean removeNote(Note what) {
     return removeFromList(what, getNoteList(), NOTELIST_XML_ATTRIBUTE_NAME);
  }

   /** removes an Note object from the list of notes in this Field object
   * @param  index - list index number of the Note to be removed
   * @return true on success, false on failure
   */
  public boolean removeNote(int index) {
     return removeFromList(index, getNoteList(), NOTELIST_XML_ATTRIBUTE_NAME);
  }

  /**Convenience method which returns a list of the notes held by
   * this object.
   */
  public List getNotes() {
    return getNoteList();
  }

  /**Convenience method which sets the list of the notes held by
   * this object.
   */
  public void setNotes(List noteList) {
    setNoteList(noteList);
  }   

  /**Insert a Unit object into the Units object
   * held in this object.
   * @param unit - Unit to be added
   * @return an Unit object
   */
  public boolean addUnit(Unit unit) {

    Units u = getUnits();
    if (u == null) {
      u = new Units();
      setUnits(u);
    }
    return  u.addUnit(unit);
  }

  /** Remove an Unit object from the Units object held in
   * this object
   * @param what - Unit to be removed
   * @return true if successful, false if not
   */
  public boolean removeUnit(Unit what) {
    Units u = getUnits();
    if (u !=null) {
      if (u.getUnitList().size()==0)
        setUnits(null);
      return u.removeUnit(what);
    }
    else
      return false;
  }

  /** Remove an Unit object from the Units object held in
   * this object
   * @param index - list index number of the Unit to be removed
   * @return true if successful, false if not
   */
  public boolean removeUnit(int index) {
   Units u = getUnits();
    if (u !=null) {
      if (u.getUnitList().size()==0)
        setUnits(null);
      return u.removeUnit(index);
    }
    else
      return false;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

   //
   // Protected Methods
   //

   /** A special private method used by constructor methods to
       conviently build the XML attribute list for a given class.
    */
   protected void init()
   {

     super.init();

     classXDFNodeName = "field";

     // order matters! these are in *reverse* order of their
     // occurence in the XDF DTD
     attribOrder.add(0, NOTELIST_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, RELATION_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, DATAFORMAT_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, UNITS_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, CONVERSION_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, IDREF_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, ID_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, DESCRIPTION_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, NAME_XML_ATTRIBUTE_NAME);

      //set up the attribute hashtable key with the default initial value
     attribHash.put(NOTELIST_XML_ATTRIBUTE_NAME, 
                new Attribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
     attribHash.put(RELATION_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.OBJECT_TYPE));  //double check
     attribHash.put(UNITS_XML_ATTRIBUTE_NAME, new Attribute(new Units(), Constants.OBJECT_TYPE));
     attribHash.put(DATAFORMAT_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.OBJECT_TYPE));
     attribHash.put(CONVERSION_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.OBJECT_TYPE));
     attribHash.put(IDREF_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
     attribHash.put(ID_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
     attribHash.put(DESCRIPTION_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
     attribHash.put(NAME_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));

   };

}


