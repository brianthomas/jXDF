
// XDF FormattedXMLDataIOStyle Class
// CVS $Id$


// FormattedXMLDataIOStyle.java Copyright (C) 2000 Brian Thomas,
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
import java.io.OutputStream;

/**
   @version $Revision$
 */
public class FormattedXMLDataIOStyle extends XMLDataIOStyle {

  //
  //Fields
  //

  //
  //constructor and related methods
  //

  //no-arg constructor
  public FormattedXMLDataIOStyle ()
  {

     init();
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public FormattedXMLDataIOStyle ( Hashtable InitXDFAttributeTable )
  {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  // 
  // Protected Methods
  //

  protected void specificIOStyleToXDF( OutputStream outputstream,String indent)
  {

  }

  //
  // Private Methods
  //

  /** special private method used by constructor methods to
      conviently build the XML attribute list for a given class.
   */
  private void init()
  {

    classXDFNodeName = "FormattedXMLDataIOStyle";

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0,"name");

     //set up the attribute hashtable key with the default initial value
    attribHash.put("name", new XMLAttribute(null, Constants.STRING_TYPE));

  };

}


/* Modification History:
 *
 * $Log$
 * Revision 1.2  2000/11/01 16:14:13  thomas
 *
 *   Another version, not finished but has more in it that before. -b.t.
 *
 *
 */

