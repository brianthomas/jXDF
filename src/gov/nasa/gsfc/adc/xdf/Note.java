
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
    Note.java
 */
public class Note extends BaseObject implements Cloneable {

   // 
   // Fields
   // 

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
      @return: the current *location* attribute.
    */
   public String getLocation() {
      return (String) ((XMLAttribute) attribHash.get("location")).getAttribValue();
   }

   /**set the *location* attribute. 
   */
   public void setLocation(String strValue)
   {
      ((XMLAttribute) attribHash.get("location")).setAttribValue(strValue);
   }

  /** get the *mark* attribute. 
      @return: the current *mark* attribute.
    */
   public String getMark() {
      return (String) ((XMLAttribute) attribHash.get("mark")).getAttribValue();
   }
   
   /**set the *mark* attribute. 
   */
   public void setMark (String strValue)
   {  
      ((XMLAttribute) attribHash.get("mark")).setAttribValue(strValue);
   }

   /** get the *noteId* attribute. 
      @return: the current *noteId* attribute.
    */
   public String getNoteId() {
      return (String) ((XMLAttribute) attribHash.get("noteId")).getAttribValue();
   }
   
   /**set the *noteId* attribute. 
   */
   public void setNoteId (String strValue)
   {  
      ((XMLAttribute) attribHash.get("noteId")).setAttribValue(strValue);
   }

   /** get the *noteIdRef* attribute. 
      @return: the current *noteIdRef* attribute.
    */
   public String getNoteIdRef() {
      return (String) ((XMLAttribute) attribHash.get("noteIdRef")).getAttribValue();
   }

   /**set the *noteIdRef* attribute. 
   */
   public void setNoteIdRef (String strValue)
   {
      ((XMLAttribute) attribHash.get("noteIdRef")).setAttribValue(strValue);
   }

   /** get the *value* (PCDATA) attribute. 
    */
   public String getValue() {
      return (String) ((XMLAttribute) attribHash.get("value")).getAttribValue();
   }

   /**set the *value* attribute (PCDATA). 
   */
   public void setValue (String strValue)
   {
      ((XMLAttribute) attribHash.get("value")).setAttribValue(strValue);
   }

   //
   // Other Public Methods 
   //

   /** append text into this Note instance.
    */
   public void addText (String text) {

      String currentValue = getValue();
      if(currentValue == null) 
         currentValue = new String (); // yes, this can happen
      currentValue.concat(text);
      setValue(currentValue);

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

   //
   // Private Methods
   //

   /** a special private method used by constructor methods to
       conviently build the XML attribute list for a given class.
    */
   private void init()
   {

      classXDFNodeName = "note";

      // order matters! these are in *reverse* order of their
      // occurence in the XDF DTD
      attribOrder.add(0,"value");
      attribOrder.add(0,"location");
      attribOrder.add(0,"noteIdRef");
      attribOrder.add(0,"noteId");
      attribOrder.add(0,"mark");

      attribHash.put("value", new XMLAttribute(null, Constants.STRING_TYPE));
      attribHash.put("location", new XMLAttribute(null, Constants.STRING_TYPE));
      attribHash.put("noteIdRef", new XMLAttribute(null, Constants.STRING_TYPE));
      attribHash.put("noteId", new XMLAttribute(null, Constants.STRING_TYPE));
      attribHash.put("mark", new XMLAttribute(null, Constants.STRING_TYPE));

   }

}

