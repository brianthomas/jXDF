

// XDF Notes Class
// CVS $Id$


// Notes.java Copyright (C) 2000 Brian Thomas,
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
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.io.OutputStream;
import java.io.IOException; 

/**
 *  
 * @version $Revision$
 */

public class Notes extends BaseObject implements NotesInterface {

   //
   // Fields
   //

   /* XML attribute names */
   private static final String NOTELIST_XML_ATTRIBUTE_NAME = new String("noteList");
   private static final String LOCATION_ORDER_XML_ATTRIBUTE_NAME = new String("locationOrder");


   //
   // Constructors 
   //

   /** The no argument constructor.
    */
   public Notes ()
   {
      init();
   }

   /**  This constructor takes a Java Hashtable as an initializer of
        the XML attributes of the object to be constructed. The
        Hashtable key/value pairs coorespond to the class XDF attribute
        names and their desired values.
     */
   public Notes ( Hashtable InitXDFAttributeTable )
   {

      // init the XML attributes (to defaults)
      init();

      // init the value of selected XML attributes to HashTable values
      hashtableInitXDFAttributes(InitXDFAttributeTable);

    }

    //
    // Get/Set Methods
    //

    /** set the *noteList* attribute
        @return the current *noteList* attribute
        @deprecated use the setNotes method.
     */
    public void setNoteList (List notes) {
       ((XMLAttribute) attribHash.get(NOTELIST_XML_ATTRIBUTE_NAME)).setAttribValue(notes);
    }

    /**
        @return the current *noteList* attribute
        @deprecated use the getNotes method.
     */
    public List getNoteList() {
       return (List) ((XMLAttribute) attribHash.get(NOTELIST_XML_ATTRIBUTE_NAME)).getAttribValue();
    }

    /** set the list of notes in this object.
        @return the current list of notes in this object.
     */
    public void setNotes (List noteList) {
       ((XMLAttribute) attribHash.get(NOTELIST_XML_ATTRIBUTE_NAME)).setAttribValue(noteList);
    }

    /**convenience method that returns the list of notes this object holds
     */
    public List getNotes() { return getNoteList(); }

    /** set the locatorOrderList
     */
    public void setLocationOrderList (List axisIdList) {
       NotesLocationOrder orderObj = (NotesLocationOrder)
               ((XMLAttribute) attribHash.get(LOCATION_ORDER_XML_ATTRIBUTE_NAME)).getAttribValue();

       orderObj.setAxisOrderList(axisIdList);
    }

   //
   // Other PUBLIC Methods
   //

   /** Insert a Note object into the list of note held by this object.
       @param note - Note to be added
       @return a Note object if successfull, null if not.
    */
   public boolean addNote (NoteInterface note )
   {
      getNoteList().add(note);
      return true;
   }

   /** Remove a Note object the list of notes held in
       this object
       @param Note to be removed
       @return true if successful, false if not
    */
   public boolean removeNote(NoteInterface what) {
      return removeFromList(what, getNoteList(), NOTELIST_XML_ATTRIBUTE_NAME);
   }

   /** Remove an Note object from the list of notes held in
       this object
       @param index -- list index number of the Note object to be removed
       @return true if successful, false if not
    */
   public boolean removeNote(int index) {
      return removeFromList(index, getNoteList(), NOTELIST_XML_ATTRIBUTE_NAME);
   }

   // quick little change to prevent this node from printing out
   // IF there are no note objects in its notelist
   public void toXMLOutputStream ( OutputStream outputstream,
                                   Hashtable XMLDeclAttribs,
                                   String indent,
                                   boolean dontCloseNode,
                                   String newNodeNameString,
                                   String noChildObjectNodeName
                                 )
   throws java.io.IOException
   {

      if( getNoteList().size() > 0 ) {
          super.toXMLOutputStream ( outputstream, XMLDeclAttribs,
                                    indent, dontCloseNode,
                                    newNodeNameString, noChildObjectNodeName
                                   );
      }

   }

   //
   // Protected Methods
   //

   /** A special protected method used by constructor methods to
       convienently build the XML attribute list for a given class.
    */
   protected void init()
   {

      classXDFNodeName = "notes";

      // order matters! these are in *reverse* order of their
      // occurence in the XDF DTD
      attribOrder.add(0, NOTELIST_XML_ATTRIBUTE_NAME);
      attribOrder.add(0, LOCATION_ORDER_XML_ATTRIBUTE_NAME);

      attribHash.put(NOTELIST_XML_ATTRIBUTE_NAME, new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
      attribHash.put(LOCATION_ORDER_XML_ATTRIBUTE_NAME, new XMLAttribute(new NotesLocationOrder(), Constants.OBJECT_TYPE));

   }

}  // end of Notes Class

 /* Modification History:
  *
  * $Log$
  * Revision 1.10  2001/07/06 19:04:23  thomas
  * toXMLOutputStream and related methods now pass on IOExceptions
  * to the application writer (e.g. they throw the error).
  *
  * Revision 1.9  2001/06/26 21:22:25  huang
  * changed return type to boolean for all addObject()
  *
  * Revision 1.8  2001/05/04 20:30:50  thomas
  * Added Interface stuff.
  *
  * Revision 1.7  2001/02/07 18:44:03  thomas
  * Converted XML attribute decl
  * to use constants (final static fields within the object). These
  * are private decl for now. -b.t.
  *
  * Revision 1.6  2000/11/16 20:02:52  kelly
  * fixed documentation.  -k.z.
  *
  * Revision 1.5  2000/11/08 19:18:07  thomas
  * Changed the name of toXDF* methods to toXML* to
  * better reflect the nature of the output (its not XDF
  * unless you call th emethod from strcuture object;
  * otherwise, it wont validate as XDF; it is still XML
  * however). -b.t.
  *
  * Revision 1.4  2000/11/03 20:26:57  thomas
  * Updated toXDFOutputStream so that the node wont
  * print out IF there are no note objects held within it.
  * -b.t.
  *
  * Revision 1.3  2000/11/02 19:44:45  thomas
  * Initial Version. -b.t.
  *
  *
  */


