
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
 * class--
 * "class" of this field. <b>NOT CURRENTLY IMPLEMENTED</b>
 * noteList--
 * a list of the Note objects held by this field.
 * dataFormat--
 * a OBJECT REF of the DataFormat object for data within this field.
 * relation--
 * a  OBJECT REF  of the Relationship object for this field.
 * units--
 * a OBJECT REF of the Units object of this field. The Units object
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

public class Field extends BaseObject {

   //
   // Fields
   //

   /* XML attribute names */
   private static final String NAME_XML_ATTRIBUTE_NAME = new String("name");
   private static final String DESCRIPTION_XML_ATTRIBUTE_NAME = new String("description");
   private static final String ID_XML_ATTRIBUTE_NAME = new String("fieldId");
   private static final String IDREF_XML_ATTRIBUTE_NAME = new String("fieldIdRef");
   private static final String CLASS_XML_ATTRIBUTE_NAME = "class";
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
      ((XMLAttribute) attribHash.get(NAME_XML_ATTRIBUTE_NAME)).setAttribValue(strName);
   }

   /**
       @return the current *name* attribute
    */
   public String getName()
   {
      return (String) ((XMLAttribute) attribHash.get(NAME_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *description* attribute
       @return the current *description* attribute
    */
   public void setDescription (String strDesc)
   {
      ((XMLAttribute) attribHash.get(DESCRIPTION_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
   }

   /**
       @return the current *description* attribute
   */
   public String getDescription() {
      return (String) ((XMLAttribute) attribHash.get(DESCRIPTION_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *fieldId* attribute
       @return the current *fieldId* attribute
    */
   public void setFieldId (String strField)
   {
      ((XMLAttribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).setAttribValue(strField);
   }

   /**
       @return the current *fieldId* attribute
    */
   public String getFieldId()
   {
      return (String) ((XMLAttribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *fieldIdRef* attribute
       @return the current *fieldIdRef* attribute
    */
   public void setFieldIdRef (String strField)
   {
      ((XMLAttribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).setAttribValue(strField);
   }

   /**
       @return the current *fieldIdRef* attribute
    */
   public String getFieldIdRef()
   {
      return (String) ((XMLAttribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *units* attribute
       @return the current *units* attribute
    */
   public void setUnits (Units units)
   {
      ((XMLAttribute) attribHash.get(UNITS_XML_ATTRIBUTE_NAME)).setAttribValue(units);
   }

   /**
       @return the current *units* attribute
    */
   public Units getUnits()
   {
      return (Units) ((XMLAttribute) attribHash.get(UNITS_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /**  set the *dataFormat* attribute
       @return the current *dataFormat* attribute
    */
   public void setDataFormat (DataFormat dataFormat)
   {
       ((XMLAttribute) attribHash.get(DATAFORMAT_XML_ATTRIBUTE_NAME)).setAttribValue(dataFormat);
   }

   /**
   * @return the current *dataFormat* attribute
   */
   public DataFormat getDataFormat()
   {
      return (DataFormat) ((XMLAttribute) attribHash.get(DATAFORMAT_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *noteList* attribute
      @return the current *noteList* attribute
    */
   public void setNoteList(List note)
   {
      ((XMLAttribute) attribHash.get(NOTELIST_XML_ATTRIBUTE_NAME)).setAttribValue(note);
   }

   /**
       @return the current *noteList* attribute
   */
   public List getNoteList()
   {
      return (List) ((XMLAttribute) attribHash.get(NOTELIST_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /**  set the *relationship* attribute
       @return the current *dataFormat* attribute
    */
   public void setRelationship (FieldRelationship fieldRelation)
   {
       ((XMLAttribute) attribHash.get(RELATION_XML_ATTRIBUTE_NAME)).setAttribValue(fieldRelation);
   }
 
   /**
       @return the current *relationship* attribute
   */
   public FieldRelationship getRelationship ()
   {
      return (FieldRelationship) ((XMLAttribute) attribHash.get(RELATION_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   //
   // Other public methods
   //

  /**insert an Note object into the list of notes in this Field object
   * @param Note
   * @return an Note object
   */
  public Note addNote(Note n) {
    getNoteList().add(n);
    return n;
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

  /**Insert a Unit object into the Units object
   * held in this object.
   * @param unit - Unit to be added
   * @return an Unit object
   */
  public Unit addUnit(Unit unit) {

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

     classXDFNodeName = "field";

     // order matters! these are in *reverse* order of their
     // occurence in the XDF DTD
     attribOrder.add(0, NOTELIST_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, RELATION_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, UNITS_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, DATAFORMAT_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, CLASS_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, IDREF_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, ID_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, DESCRIPTION_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, NAME_XML_ATTRIBUTE_NAME);

      //set up the attribute hashtable key with the default initial value
     attribHash.put(NOTELIST_XML_ATTRIBUTE_NAME, 
                new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
     attribHash.put(RELATION_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.OBJECT_TYPE));  //double check
     attribHash.put(UNITS_XML_ATTRIBUTE_NAME, new XMLAttribute(new Units(), Constants.OBJECT_TYPE));
     attribHash.put(DATAFORMAT_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.OBJECT_TYPE));
     attribHash.put(CLASS_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
     attribHash.put(IDREF_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
     attribHash.put(ID_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
     attribHash.put(DESCRIPTION_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
     attribHash.put(NAME_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));

   };

}


/* Modification History:
 *
 * $Log$
 * Revision 1.13  2001/02/07 18:44:04  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.12  2000/11/27 16:57:45  thomas
 * Made init method protected so that extending
 * Dataformats may make use of them. -b.t.
 *
 * Revision 1.11  2000/11/16 19:58:28  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.10  2000/11/08 19:46:29  thomas
 * Trimmed down import path to just needed classes =b.t
 *
 * Revision 1.9  2000/11/06 21:26:21  kelly
 * added clone() -k.z.
 *
 * Revision 1.8  2000/11/02 17:05:29  thomas
 * Units are inited as Unit obj rather than null (kelly);
 * Field Relationship methods added. Clean up of file,
 * set methods return void now. -b.t.
 *
 * Revision 1.6  2000/10/30 18:55:53  thomas
 * Attrib order in init had entry for "paramId" instead
 * of "fieldId". Corrected. -b.t.
 *
 * Revision 1.5  2000/10/27 21:16:48  kelly
 * units are initialized as null now.  changed add/remove units methods.  -k.z.
 *
 * Revision 1.4  2000/10/26 20:27:39  kelly
 * fixed a little documentation
 *
 * Revision 1.3  2000/10/26 20:22:28  kelly
 * completed the class
 *
 * Revision 1.2  2000/09/29 22:03:37  cvs
 * More files
 *
 * Revision 1.1.1.1  2000/09/21 17:53:28  thomas
 * Imported Java Source
 *
 *
 */
