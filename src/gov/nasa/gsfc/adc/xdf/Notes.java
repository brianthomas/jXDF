

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
import java.io.Writer;

/**
 *  
 * @version $Revision$
 */

public class Notes extends BaseObject {

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
        @deprecated You should use the add/remove methods to manipulate this list.
     */
    public void setNoteList (List notes) {
       ((Attribute) attribHash.get(NOTELIST_XML_ATTRIBUTE_NAME)).setAttribValue(notes);
    }

    /**
        @return the current *noteList* attribute
        @deprecated use the getNotes method.
     */
    public List getNoteList() {
       return (List) ((Attribute) attribHash.get(NOTELIST_XML_ATTRIBUTE_NAME)).getAttribValue();
    }

    /** set the list of notes in this object.
        @return the current list of notes in this object.
     */
    public void setNotes (List noteList) {
       ((Attribute) attribHash.get(NOTELIST_XML_ATTRIBUTE_NAME)).setAttribValue(noteList);
    }

    /**convenience method that returns the list of notes this object holds
     */
    public List getNotes() { return getNoteList(); }

    /** set the locatorOrderList
     */
    public void setLocationOrderList (List axisIdList) {
       NotesLocationOrder orderObj = (NotesLocationOrder)
               ((Attribute) attribHash.get(LOCATION_ORDER_XML_ATTRIBUTE_NAME)).getAttribValue();

       orderObj.setAxisOrderList(axisIdList);
    }

   //
   // Other PUBLIC Methods
   //

   /** Insert a Note object into the list of note held by this object.
       @param note - Note to be added
       @return a Note object if successfull, null if not.
    */
   public boolean addNote (Note note )
   {
      getNoteList().add(note);
      return true;
   }

   /** Remove a Note object the list of notes held in
       this object
       @param Note to be removed
       @return true if successful, false if not
    */
   public boolean removeNote(Note what) {
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

   // 
   // Protected Methods
   // 

   // quick little change to prevent this node from printing out
   // IF there are no note objects in its notelist
   protected String basicXMLWriter ( Writer outputWriter,
                                     String indent,
                                     boolean dontCloseNode,
                                     String newNodeNameString,
                                     String noChildObjectNodeName
                                   )
   throws java.io.IOException
   {

      String retString = null;
      if( getNoteList().size() > 0 ) {
          retString = super.basicXMLWriter ( outputWriter, indent, dontCloseNode,
                                             newNodeNameString, noChildObjectNodeName
                                           );
      }
      return retString;
   }

   /** A special protected method used by constructor methods to
       convienently build the XML attribute list for a given class.
    */
   protected void init()
   {

      super.init();

      classXDFNodeName = "notes";

      // order matters! these are in *reverse* order of their
      // occurence in the XDF DTD
      attribOrder.add(0, NOTELIST_XML_ATTRIBUTE_NAME);
      attribOrder.add(0, LOCATION_ORDER_XML_ATTRIBUTE_NAME);

      attribHash.put(NOTELIST_XML_ATTRIBUTE_NAME, new Attribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
      attribHash.put(LOCATION_ORDER_XML_ATTRIBUTE_NAME, new Attribute(new NotesLocationOrder(), Constants.OBJECT_TYPE));

   }

}  // end of Notes Class

