  // XDF ReadCellFormattedIOCmd Class
// CVS $Id$


// ReadCellFormattedIOCmd.java Copyright (C) 2000 Brian Thomas,
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
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/** this class handles the readCell ELEMENT  
   @version $Revision$
 */
public class ReadCellFormattedIOCmd extends BaseObject implements FormattedIOCmd {
   //
  //constructor and related methods
  //

  //no-arg constructor
  public ReadCellFormattedIOCmd ()
  {
     init();
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public void ReadCellFormattedIOCmd ( Hashtable InitXDFAttributeTable )
  {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  //
  // Protected Methods
  //

/*
  public void toXMLOutputStream (
                                   OutputStream outputstream,
                                   Hashtable XMLDeclAttribs,
                                   String indent,
                                   boolean dontCloseNode,
                                   String newNodeNameString,
                                   String noChildObjectNodeName
                                )
  {
     writeOut(outputstream, "<" + classXDFNodeName + "/>");
  }
*/

  //
  // Protected Methods
  //

  /** special method used by constructor methods to
      convienently build the XML attribute list for a given class.
   */
  protected void init()
  {
     resetXMLAttributes();
     classXDFNodeName = "readCell";
  }

}


/* Modification History:
 *
 * $Log$
 * Revision 1.4  2001/05/10 21:24:44  thomas
 * added resetXMLAttributes to init().
 * replaced specificIOStyleToXDF w/ appropriate
 * toXMLOutputStream method.
 *
 * Revision 1.3  2001/02/07 18:44:04  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.2  2000/11/16 20:04:55  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.1  2000/11/09 23:42:16  kelly
 * created the class
 *
 *
 */
