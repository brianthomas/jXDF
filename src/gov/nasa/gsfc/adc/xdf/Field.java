
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
 * "class" of this field. B<NOT CURRENTLY IMPLEMENTED>
 * lessThanValue--
 * The value which indicates the less than symbol ("<") within the data cube
 * for data within the slice corresponding to this field.
 * lessThanOrEqualValue--
 * The value value which indicates the less than equal symbol ("=<") within the data cube
 * for data within the slice corresponding to this field.
 * greaterThanValue--
 * The value which indicates the greater than symbol (">") within the data cube
 * for data within the slice corresponding to this field.
 * greaterThanOrEqualValue--
 * The value which indicates the greater than equal symbol (">=") within the data cube
 *  for data within the slice corresponding to this field.
 * infiniteValue--
 * The value which indicates the infinite value within the data cube
 * for data within the slice corresponding to this field.
 * infiniteNegativeValue--
 * The value which indicates the negative infinite value within the data cube
 * for data within the slice corresponding to this field.
 * noDataValue--
 * The value which indicates the no data value within the data cube
 * for data within the slice corresponding to this field.
 * noteList--
 * a list of the L<XDF::Note> objects held by this field.
 * dataFormat--
 * a OBJECT REF of the L<XDF::DataFormat> object for data within this field.
 * relation--
 * a  OBJECT REF  of the L<XDF::Relationship> object for this field.
 * units--
 * a OBJECT REF of the L<XDF::Units> object of this field. The XDF::Units object
 * is used to hold the L<XDF::Unit> objects.
 */

package gov.nasa.gsfc.adc.xdf;
import java.util.*;

/**Field.java: an XDF::Field describes a field at a given index along a fieldAxis
 * @version $Revision$
 */

public class Field extends BaseObject {

  //
  //Fields
  //

  //
  //constructor and related methods
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
   //Get/Set Methods
   //

   /** Set the *name* attribute
       @return: the current *name* attribute
    */
   public void setName (String strName)
   {
      ((XMLAttribute) attribHash.get("name")).setAttribValue(strName);
   }

   /** getName
       @return: the current *name* attribute
    */
   public String getName()
   {
      return (String) ((XMLAttribute) attribHash.get("name")).getAttribValue();
   }

   /** set the *description* attribute
       @return: the current *description* attribute
    */
   public void setDescription (String strDesc)
   {
      ((XMLAttribute) attribHash.get("description")).setAttribValue(strDesc);
   }

   /** getDescription
       @return: the current *description* attribute
   */
   public String getDescription() {
      return (String) ((XMLAttribute) attribHash.get("description")).getAttribValue();
   }

   /** set the *fieldId* attribute
       @return: the current *fieldId* attribute
    */
   public void setFieldId (String strField)
   {
      ((XMLAttribute) attribHash.get("fieldId")).setAttribValue(strField);
   }

   /** getFieldId
       @return: the current *fieldId* attribute
    */
   public String getFieldId()
   {
      return (String) ((XMLAttribute) attribHash.get("fieldId")).getAttribValue();
   }

   /** set the *fieldIdRef* attribute
       @return: the current *fieldIdRef* attribute
    */
   public void setFieldIdRef (String strField)
   {
      ((XMLAttribute) attribHash.get("fieldIdRef")).setAttribValue(strField);
   }

   /** getFieldIdRef
       @return: the current *fieldIdRef* attribute
    */
   public String getFieldIdRef()
   {
      return (String) ((XMLAttribute) attribHash.get("fieldIdRef")).getAttribValue();
   }

   /** set the *units* attribute
       @return: the current *units* attribute
    */
   public void setUnits (Units units)
   {
      ((XMLAttribute) attribHash.get("units")).setAttribValue(units);
   }

   /** getUnits
       @return: the current *units* attribute
    */
   public Units getUnits()
   {
      return (Units) ((XMLAttribute) attribHash.get("units")).getAttribValue();
   }

   /** setDataFormat: set the *dataFormat* attribute
       @return: the current *dataFormat* attribute
    */
   public void setDataFormat (DataFormat dataFormat)
   {
       ((XMLAttribute) attribHash.get("dataFormat")).setAttribValue(dataFormat);
   }

   /**getDataFormat
   * @return: the current *dataFormat* attribute
   */
   public DataFormat getDataFormat()
   {
      return (DataFormat) ((XMLAttribute) attribHash.get("dataFormat")).getAttribValue();
   }

   /** set the *noteList* attribute
      @return: the current *noteList* attribute
    */
   public void setNoteList(List note) 
   {
      ((XMLAttribute) attribHash.get("noteList")).setAttribValue(note);
   }

   /** getNoteList
       @return: the current *noteList* attribute
   */
   public List getNoteList() 
   {
      return (List) ((XMLAttribute) attribHash.get("noteList")).getAttribValue();
   }

   /** setRelationship: set the *relationship* attribute
       @return: the current *dataFormat* attribute
    */
   public void setRelationship (FieldRelationship fieldRelation)
   {
       ((XMLAttribute) attribHash.get("relation")).setAttribValue(fieldRelation);
   }

   /** getRelationship
       @return: the current *relationship* attribute
   */
   public FieldRelationship getRelationship ()
   {
      return (FieldRelationship) ((XMLAttribute) attribHash.get("relation")).getAttribValue();
   }

   // 
   // Other public methods
   //

  /** addNote: insert an XDF::Note object into the list of notes in this Field object
   * @param: XDF::Note
   * @return: an XDF::Note object on success, null on failure
   */
  public Note addNote(Note n) {
    if (n == null) {
      Log.warn("in Field.addNote(), the Note passed in is null");
      return null;
    }
    getNoteList().add(n);
    return n;
  }

  /**removeNote: removes an XDF::Note object from the list of notes in this Field object
   * @param: Note to be removed
   * @return: true on success, false on failure
   */
   public boolean removeNote(Note what) {
     return removeFromList(what, getNoteList(), "noteList");
  }

   /**removeNote: removes an XDF::Note object from the list of notes in this Field object
   * @param: list index number
   * @return: true on success, false on failure
   */
  public boolean removeNote(int index) {
     return removeFromList(index, getNoteList(), "noteList");
  }

  /**getNotes: Convenience method which returns a list of the notes held by
   * this object.
   */
  public List getNotes() {
    return getNoteList();
  }

  /**addUnit: Insert a Unit object into the Units object
   * held in this object.
   * @param: Unit to be added
   * @return: an XDF::Unit object if successfull, null if not.
   */
  public Unit addUnit(Unit unit) {
    if (unit == null) {
      Log.warn("in Array.addUnit(), the Unit passed in is null");
      return null;
    }
    Units u = getUnits();
    if (u == null) {
      u = new Units();
      setUnits(u);
    }
    return  u.addUnit(unit);
  }

  /**removeUnit: Remove an XDF::Unit object from the XDF::Units object held in
   * this object
   * @param: Unit to be removed
   * @return: true if successful, false if not
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

  /**removeUnit: Remove an XDF::Unit object from the XDF::Units object held in
   * this object
   * @param: list index number
   * @return: true if successful, false if not
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

   //
   // Private Methods
   //

   /** A special private method used by constructor methods to
       conviently build the XML attribute list for a given class.
    */
   private void init()
   {

     classXDFNodeName = "field";

     // order matters! these are in *reverse* order of their
     // occurence in the XDF DTD
     attribOrder.add(0,"noteList");
     attribOrder.add(0,"relation");
     attribOrder.add(0,"units");
     attribOrder.add(0,"dataFormat");
     attribOrder.add(0,"class");
     attribOrder.add(0,"fieldIdRef");
     attribOrder.add(0,"fieldId");
     attribOrder.add(0,"description");
     attribOrder.add(0,"name");

      //set up the attribute hashtable key with the default initial value
     attribHash.put("noteList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
     attribHash.put("relation", new XMLAttribute(null, Constants.OBJECT_TYPE));  //double check
     attribHash.put("units", new XMLAttribute(new Units(), Constants.OBJECT_TYPE));
     attribHash.put("dataFormat", new XMLAttribute(null, Constants.OBJECT_TYPE));
     attribHash.put("class", new XMLAttribute(null, Constants.STRING_TYPE));
     attribHash.put("fieldIdRef", new XMLAttribute(null, Constants.STRING_TYPE));
     attribHash.put("fieldId", new XMLAttribute(null, Constants.STRING_TYPE));
     attribHash.put("description", new XMLAttribute(null, Constants.STRING_TYPE));
     attribHash.put("name", new XMLAttribute(null, Constants.STRING_TYPE));

   };

}


/* Modification History:
 *
 * $Log$
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
