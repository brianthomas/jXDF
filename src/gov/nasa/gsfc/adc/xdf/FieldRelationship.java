

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

   /* XML attribute names */
   private static final String ROLE_XML_ATTRIBUTE_NAME = new String("role");
   private static final String DESCRIPTION_XML_ATTRIBUTE_NAME = new String("description");
   private static final String IDREFS_XML_ATTRIBUTE_NAME = new String("fieldIdRefs");

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
      @return the current value of the role attribute.
   */
   public String getRole ( )
   {
      return (String) ((Attribute) attribHash.get(ROLE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** Set the value of the role attribute. 
   */
   // NOTE: this should be limited to a few choices only. 
   public void setRole (String strRole )
   {
       if (Utility.isValidRelationRole(strRole))
          ((Attribute) attribHash.get(ROLE_XML_ATTRIBUTE_NAME)).setAttribValue(strRole);
       else 
          Log.warnln("Invalid FieldRelationship.setRole() value. Ignoring set request.");
   }

   /** Get the value of the description attribute. 
      @return the current value of the description attribute.
   */
   public String getDescription ( )
   {
      return (String) ((Attribute) attribHash.get(DESCRIPTION_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** Set the value of the description attribute. 
   */
   public void setDescription (String strDescription )
   {
       ((Attribute) attribHash.get(DESCRIPTION_XML_ATTRIBUTE_NAME)).setAttribValue(strDescription);
   }

   /** Get the value of the fieldIdRefs attribute. 
      @return the current value of the fieldIdRefs attribute.
   */
   public String getFieldIdRefs ( )
   {
      return (String) ((Attribute) attribHash.get(IDREFS_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** Set the value of the fieldIdRefs attribute. 
   */
   public void setFieldIdRefs (String strFieldIdRefs )
   {
       ((Attribute) attribHash.get(IDREFS_XML_ATTRIBUTE_NAME)).setAttribValue(strFieldIdRefs);
   }

   // 
   // Protected Methods
   //

   /** A special protected method used by constructor methods to
       convienently build the XML attribute list for a given class.
    */
   protected void init()
   {

       resetAttributes();

       classXDFNodeName = "relation";

       // order matters! these are in *reverse* order of their
       // occurence in the XDF DTD
       attribOrder.add(0, IDREFS_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, DESCRIPTION_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, ROLE_XML_ATTRIBUTE_NAME);

       //set up the attribute hashtable key with the default initial value
       attribHash.put(IDREFS_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
       attribHash.put(DESCRIPTION_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
       attribHash.put(ROLE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));

   };


}

/* Modification History:
 *
 * $Log$
 * Revision 1.5  2001/09/13 21:39:25  thomas
 * name change to either XMLAttribute, XMLNotation, XDFEntity, XMLElementNode class forced small change in this file
 *
 * Revision 1.4  2001/05/10 21:17:17  thomas
 * added resetAttributes to init().
 *
 * Revision 1.3  2001/02/07 18:44:04  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.2  2000/11/16 19:59:14  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.1  2000/11/02 16:46:50  thomas
 * Initial version. Role checking 'enum list' NOT implemented. -b.t.
 *
 *
 */

