

// XDF Note Class
// CVS $Id$


// Note.java Copyright (C) 2000 Brian Thomas,
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

/**
     holds a note object
 */
public class Note extends BaseObject {

   //
   // Fields
   //

   /* XML attribute names */
   private static final String MARK_XML_ATTRIBUTE_NAME = new String("mark");
   private static final String ID_XML_ATTRIBUTE_NAME = new String("noteId");
   private static final String IDREF_XML_ATTRIBUTE_NAME = new String("noteIdRef");
   private static final String LOCATION_XML_ATTRIBUTE_NAME = new String("location");
   private static final String VALUE_XML_ATTRIBUTE_NAME = new String("value");

   //
   // Constructors
   //

   /** The no-argument constructor.
    */
   public Note ()
   {
      init();
   }

   /**  This constructor takes a string as an initializer of
        the value of this note. The string is inserted as the
        note text.
    */
   public Note (String text)
   {
      init();
      addText(text);
   }

   /**  This constructor takes a Java Hashtable as an initializer of
        the XML attributes of the object to be constructed. The
        Hashtable key/value pairs coorespond to the class XDF attribute
        names and their desired values.
    */
   public Note ( Hashtable InitXDFAttributeTable )
   {

      // init the XML attributes (to defaults)
      init();

      // init the value of selected XML attributes to HashTable values
      hashtableInitXDFAttributes(InitXDFAttributeTable);
   }

   //
   // Get/Set Methods
   //

   /** get the *location* attribute.
      @return the current *location* attribute.
    */
   public String getLocation() {
      return (String) ((XMLAttribute) attribHash.get(LOCATION_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /**set the *location* attribute.
   */
   public void setLocation(String strValue)
   {
      ((XMLAttribute) attribHash.get(LOCATION_XML_ATTRIBUTE_NAME)).setAttribValue(strValue);
   }

  /** get the *mark* attribute.
      @return the current *mark* attribute.
    */
   public String getMark() {
      return (String) ((XMLAttribute) attribHash.get(MARK_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /**set the *mark* attribute.
   */
   public void setMark (String strValue)
   {
      ((XMLAttribute) attribHash.get(MARK_XML_ATTRIBUTE_NAME)).setAttribValue(strValue);
   }

   /** get the *noteId* attribute.
      @return the current *noteId* attribute.
    */
   public String getNoteId() {
      return (String) ((XMLAttribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /**set the *noteId* attribute.
   */
   public void setNoteId (String strValue)
   {
      ((XMLAttribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).setAttribValue(strValue);
   }

   /** get the *noteIdRef* attribute.
      @return the current *noteIdRef* attribute.
    */
   public String getNoteIdRef() {
      return (String) ((XMLAttribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /**set the *noteIdRef* attribute.
   */
   public void setNoteIdRef (String strValue)
   {
      ((XMLAttribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).setAttribValue(strValue);
   }

   /** get the *value* (PCDATA) attribute.
    */
   public String getValue() {
      return (String) ((XMLAttribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /**set the *value* attribute (PCDATA).
   */
   public void setValue (String strValue)
   {
      ((XMLAttribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strValue);
   }

   //
   // Other Public Methods
   //

   /** append text into this Note instance.
     * @return true on success, false on failure.
    */

  // wish I could use String.concat(String) here, but it doesnt seem
  // to work. Ho hum, perhaps investigate why later on. -b.t.
   public boolean addText (String text) {

      StringBuffer newValue = new StringBuffer ();

      String currentValue;
      if ( (currentValue = getValue()) != null ) // yes, this can happen
         newValue.append(currentValue);

      newValue.append(text); // append in new text

      setValue(newValue.toString()); // (re)set the PCDATA string
      return true;
   }

   /** Indicate the datacell that this note applies to within an array.
    */
   public void setLocator (Locator noteLocation) {
      Log.errorln("Note.setLocation method not implemented yet.");
   }

   /** Get a locator set to the coordinates that this note identifies as its
       location within a given array.
    */
   public Locator getLocator () {
      Log.errorln("Note.getLocation method not implemented yet (returning null).");
      return (Locator) null;
   }

   public Object clone() throws CloneNotSupportedException {
    return super.clone();
   }

   //
   // Protected Methods
   //

   /** a special private method used by constructor methods to
       conviently build the XML attribute list for a given class.
    */
   protected void init()
   {

      classXDFNodeName = "note";

      // order matters! these are in *reverse* order of their
      // occurence in the XDF DTD
      attribOrder.add(0, VALUE_XML_ATTRIBUTE_NAME);
      attribOrder.add(0, LOCATION_XML_ATTRIBUTE_NAME);
      attribOrder.add(0, IDREF_XML_ATTRIBUTE_NAME);
      attribOrder.add(0, ID_XML_ATTRIBUTE_NAME);
      attribOrder.add(0, MARK_XML_ATTRIBUTE_NAME);

      attribHash.put(VALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
      attribHash.put(LOCATION_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
      attribHash.put(IDREF_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
      attribHash.put(ID_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
      attribHash.put(MARK_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));

   }

}

