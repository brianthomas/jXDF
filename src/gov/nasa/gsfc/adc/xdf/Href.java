
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
  // Constructor and related methods
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
     ((XMLAttribute) attribHash.get("base")).setAttribValue(value);
  }

  /**
   * @return the current *base* attribute
   */
  public String getBase() {
    return (String) ((XMLAttribute) attribHash.get("base")).getAttribValue();
  }

 /** set the *name* attribute
   */
  public void setName(String value) {
     ((XMLAttribute) attribHash.get("name")).setAttribValue(value);
  }

  /**
   * @return the current *name* attribute
   */
  public String getName() {
    return (String) ((XMLAttribute) attribHash.get("name")).getAttribValue();
  }

  /** set the *sysId* attribute
   */
  public void setSysId(String value) {
     ((XMLAttribute) attribHash.get("sysId")).setAttribValue(value);
  }

  /**
   * @return the current *sysId* attribute
   */
  public String getSysId() {
    return (String) ((XMLAttribute) attribHash.get("sysId")).getAttribValue();
  }

  /** set the *pubId* attribute
   */
  public void setPubId(String value) {
     ((XMLAttribute) attribHash.get("pubId")).setAttribValue(value);
  }

  /**
   * @return the current *pubId* attribute
   */
  public String getPubId() {
    return (String) ((XMLAttribute) attribHash.get("pubId")).getAttribValue();
  }

  /** set the *ndata* attribute
   */
  public void setNdata(String value) {
     ((XMLAttribute) attribHash.get("ndata")).setAttribValue(value);
  }

  /**
   * @return the current *ndata* attribute
   */
  public String getNdata() {
    return (String) ((XMLAttribute) attribHash.get("ndata")).getAttribValue();
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
    attribOrder.add(0,"ndata");
    attribOrder.add(0,"pubId");
    attribOrder.add(0,"sysId");
    attribOrder.add(0,"base");
    attribOrder.add(0,"name");

    attribHash.put("ndata", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("pubId", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("sysId", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("base", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("name", new XMLAttribute(null, Constants.STRING_TYPE));

  };

 }

 /* Modification History:
 *
 * $Log$
 * Revision 1.1  2001/01/19 19:10:00  thomas
 * *** empty log message ***
 *
 *
 */


