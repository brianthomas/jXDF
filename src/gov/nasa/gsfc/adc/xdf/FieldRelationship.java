
// XDF FieldRelationship
// CVS $Id$


// FieldRelationship.java Copyright (C) 2000 Brian Thomas,
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

/** Establishes the relationship between the field which holds this object
    and the one refered to.
    @version $Revision$
 */
public class FieldRelationship extends BaseObject {

   //
   // Fields
   // 

   // allowable roles
   String validRoles[] = {"role1", "role2", "role3"};

   //
   // Constructors
   //

   /** No-argument constructor. 
    */
   public FieldRelationship () {
      init();
   }

   /**  This constructor takes a Java Hashtable as an initializer of
        the XML attributes of the object to be constructed. The
        Hashtable key/value pairs coorespond to the class XDF attribute
        names and their desired values.
    */
   public FieldRelationship ( Hashtable InitXDFAttributeTable )
   {

      // init the XML attributes (to defaults)
      init();

      // init the value of selected XML attributes to HashTable values
      hashtableInitXDFAttributes(InitXDFAttributeTable);

   }

   //
   // Get/Set Methods
   //

   /** Get the value of the role attribute. 
      @return: the current value of the role attribute.
   */
   public String getRole ( )
   {
      return (String) ((XMLAttribute) attribHash.get("role")).getAttribValue();
   }

   /** Set the value of the role attribute. 
   */
   // NOTE: this should be limited to a few choices only. 
   public void setRole (String strRole )
   {
       ((XMLAttribute) attribHash.get("role")).setAttribValue(strRole);
   }

   /** Get the value of the description attribute. 
      @return: the current value of the description attribute.
   */
   public String getDescription ( )
   {
      return (String) ((XMLAttribute) attribHash.get("description")).getAttribValue();
   }

   /** Set the value of the description attribute. 
   */
   public void setDescription (String strDescription )
   {
       ((XMLAttribute) attribHash.get("description")).setAttribValue(strDescription);
   }

   /** Get the value of the fieldIdRefs attribute. 
      @return: the current value of the fieldIdRefs attribute.
   */
   public String getFieldIdRefs ( )
   {
      return (String) ((XMLAttribute) attribHash.get("fieldIdRefs")).getAttribValue();
   }

   /** Set the value of the fieldIdRefs attribute. 
   */
   public void setFieldIdRefs (String strFieldIdRefs )
   {
       ((XMLAttribute) attribHash.get("fieldIdRefs")).setAttribValue(strFieldIdRefs);
   }

   // 
   // Private Methods
   //

   /** A special private method used by constructor methods to
       conviently build the XML attribute list for a given class.
    */
   private void init()
   {

       classXDFNodeName = "relationship";

       // order matters! these are in *reverse* order of their
       // occurence in the XDF DTD
       attribOrder.add(0,"fieldIdRefs");
       attribOrder.add(0,"description");
       attribOrder.add(0,"role");

       //set up the attribute hashtable key with the default initial value
       attribHash.put("fieldIdRefs", new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put("description", new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put("role", new XMLAttribute(null, Constants.STRING_TYPE));

   };


}

/* Modification History:
 *
 * $Log$
 * Revision 1.1  2000/11/02 16:46:50  thomas
 * Initial version. Role checking 'enum list' NOT implemented. -b.t.
 *
 *
 */

