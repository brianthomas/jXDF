
// XDF Entity Class
// CVS $Id$

// Entity.java Copyright (C) 2001 Brian Thomas,
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
import java.io.Writer;
import java.io.OutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

 /**
  *  The Entity class is nothing more than a simple object that holds information
  *  concerning the href and its associated (XML) ENTITY reference. 
  *  @version $Revision$
  */

 public class Entity extends BaseObject {

   //
   // Fields
   //
   private static final String ENTITY_NODE_NAME = new String("!ENTITY");

   /* XML attribute names */
   private static final String NAME_XML_ATTRIBUTE_NAME = new String("name");
//   private static final String BASE_XML_ATTRIBUTE_NAME = new String("base");
   private static final String SYSTEM_ID_XML_ATTRIBUTE_NAME = new String("systemId");
   private static final String PUBLIC_ID_XML_ATTRIBUTE_NAME = new String("publicId");
   private static final String VALUE_XML_ATTRIBUTE_NAME = new String("value");
   private static final String NDATA_XML_ATTRIBUTE_NAME = new String("ndata");

   //
   // Constructor
   //

   /** The no argument constructor.
    */
   public Entity ()
   {
      init();
   }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public Entity ( Hashtable InitXDFAttributeTable )
  {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  //
  //Get/Set methods
  //

  /* set the *base* attribute
   */
//  public void setBase(String value) {
//     ((Attribute) attribHash.get(BASE_XML_ATTRIBUTE_NAME)).setAttribValue(value);
//  }

  /*
   * @return the current *base* attribute
   */
//  public String getBase() {
//    return (String) ((Attribute) attribHash.get(BASE_XML_ATTRIBUTE_NAME)).getAttribValue();
//  }

 /** set the *name* attribute
   */
  public void setName(String value) {
     ((Attribute) attribHash.get(NAME_XML_ATTRIBUTE_NAME)).setAttribValue(value);
  }

  /**
   * @return the current *name* attribute
   */
  public String getName() {
    return (String) ((Attribute) attribHash.get(NAME_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the *systemId* attribute
   */
  public void setSystemId (String value) {
     ((Attribute) attribHash.get(SYSTEM_ID_XML_ATTRIBUTE_NAME)).setAttribValue(value);
  }

  /**
   * @return the current *systemId* attribute
   */
  public String getSystemId() {
    return (String) ((Attribute) attribHash.get(SYSTEM_ID_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the *publicId* attribute
   */
  public void setPublicId(String value) {
     ((Attribute) attribHash.get(PUBLIC_ID_XML_ATTRIBUTE_NAME)).setAttribValue(value);
  }

  /**
   * @return the current *publicId* attribute
   */
  public String getPublicId() {
    return (String) ((Attribute) attribHash.get(PUBLIC_ID_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the *value* attribute
   */
  public void setValue (String value) {
     ((Attribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).setAttribValue(value);
  }

  /**
   * @return the current *value* attribute
   */
  public String getValue() {
    return (String) ((Attribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
  }


  /** set the *ndata* attribute
   */
  public void setNdata(String value) {
     ((Attribute) attribHash.get(NDATA_XML_ATTRIBUTE_NAME)).setAttribValue(value);
  }

  /**
   * @return the current *ndata* attribute
   */
  public String getNdata() {
    return (String) ((Attribute) attribHash.get(NDATA_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  protected String basicXMLWriter (
                                Writer outputWriter,
                                String indent,
                                boolean dontCloseNode,
                                String newNodeNameString,
                                String noChildObjectNodeName
                            )
  throws java.io.IOException
  {

      if (Specification.getInstance().isPrettyXDFOutput())
         outputWriter.write(indent);

      String sysId = getSystemId();
      String pubId = getPublicId();
      String value = getValue();
      String ndata = getNdata();

      outputWriter.write("<"+ENTITY_NODE_NAME+" "+getName());
      if (value != null)
         outputWriter.write(value);
      if (pubId != null)
         outputWriter.write(" PUBLIC \"" + pubId +"\"");
      if (sysId != null)
         outputWriter.write(" SYSTEM \"" + sysId +"\"");
      if (ndata != null)
         outputWriter.write(" NDATA " + ndata +"");

      outputWriter.write(">");

      return ENTITY_NODE_NAME;
  }

  //
  // Protected Methods
  //
  /** Special private method used by constructor methods to
      conviently build the XML attribute list for a given class.
   */
  protected void init()
  {

    resetAttributes();

    classXDFNodeName = "";  //XDF node name (none)

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0, NDATA_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, PUBLIC_ID_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, SYSTEM_ID_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, VALUE_XML_ATTRIBUTE_NAME);
//    attribOrder.add(0, BASE_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, NAME_XML_ATTRIBUTE_NAME);

    attribHash.put(NDATA_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
    attribHash.put(PUBLIC_ID_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
    attribHash.put(SYSTEM_ID_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
 //   attribHash.put(BASE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
    attribHash.put(VALUE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
    attribHash.put(NAME_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));

  };

 }

