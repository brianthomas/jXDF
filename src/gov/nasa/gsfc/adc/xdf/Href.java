
// XDF Href Class
// CVS $Id$

// Href.java Copyright (C) 2000 Brian Thomas,
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

 /**
  *  The HREF class is nothing more than a simple object that holds information
  *  concerning the href and its associated (XML) ENTITY reference. 
  *  @version $Revision$
  */

 public class Href extends BaseObject {

   //
   // Fields
   //

   /* XML attribute names */
   private static final String NAME_XML_ATTRIBUTE_NAME = new String("name");
   private static final String BASE_XML_ATTRIBUTE_NAME = new String("base");
   private static final String SYSTEM_ID_XML_ATTRIBUTE_NAME = new String("sysId");
   private static final String PUBLIC_ID_XML_ATTRIBUTE_NAME = new String("pubId");
   private static final String NDATA_XML_ATTRIBUTE_NAME = new String("ndata");

   //
   // Constructor
   //

   /** The no argument constructor.
    */
   public Href ()
   {
      init();
   }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public Href ( Hashtable InitXDFAttributeTable )
  {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  //
  //Get/Set methods
  //

  /** set the *base* attribute
   */
  public void setBase(String value) {
     ((XMLAttribute) attribHash.get(BASE_XML_ATTRIBUTE_NAME)).setAttribValue(value);
  }

  /**
   * @return the current *base* attribute
   */
  public String getBase() {
    return (String) ((XMLAttribute) attribHash.get(BASE_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

 /** set the *name* attribute
   */
  public void setName(String value) {
     ((XMLAttribute) attribHash.get(NAME_XML_ATTRIBUTE_NAME)).setAttribValue(value);
  }

  /**
   * @return the current *name* attribute
   */
  public String getName() {
    return (String) ((XMLAttribute) attribHash.get(NAME_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the *sysId* attribute
   */
  public void setSysId(String value) {
     ((XMLAttribute) attribHash.get(SYSTEM_ID_XML_ATTRIBUTE_NAME)).setAttribValue(value);
  }

  /**
   * @return the current *sysId* attribute
   */
  public String getSysId() {
    return (String) ((XMLAttribute) attribHash.get(SYSTEM_ID_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the *pubId* attribute
   */
  public void setPubId(String value) {
     ((XMLAttribute) attribHash.get(PUBLIC_ID_XML_ATTRIBUTE_NAME)).setAttribValue(value);
  }

  /**
   * @return the current *pubId* attribute
   */
  public String getPubId() {
    return (String) ((XMLAttribute) attribHash.get(PUBLIC_ID_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the *ndata* attribute
   */
  public void setNdata(String value) {
     ((XMLAttribute) attribHash.get(NDATA_XML_ATTRIBUTE_NAME)).setAttribValue(value);
  }

  /**
   * @return the current *ndata* attribute
   */
  public String getNdata() {
    return (String) ((XMLAttribute) attribHash.get(NDATA_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  //
  // Protected Methods
  //
  /** Special private method used by constructor methods to
      conviently build the XML attribute list for a given class.
   */
  protected void init()
  {

    classXDFNodeName = "";  //XDF node name (none)

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0, NDATA_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, PUBLIC_ID_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, SYSTEM_ID_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, BASE_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, NAME_XML_ATTRIBUTE_NAME);

    attribHash.put(NDATA_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put(PUBLIC_ID_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put(SYSTEM_ID_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put(BASE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put(NAME_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));

  };

 }

 /* Modification History:
 *
 * $Log$
 * Revision 1.2  2001/02/07 18:44:03  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.1  2001/01/19 19:10:00  thomas
 * *** empty log message ***
 *
 *
 */


