
// XDF NewLine Class
// CVS $Id$


// NewLine.java Copyright (C) 2001 Brian Thomas,
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

/** <p>
    This class handles the outputting of newline character data for some cases. 
    Normally, "new line" character data will be converted to whitespace upon output by 
    XML parsers. This presents XDF with a problem in appropriately describing and 
    perserving the output format of some formatted and delimited text data (e.g within
    the &lt;read> block). 
    </p>
    <p>
    In order to work around this, both this and the Chars classes were formulated. This class
    will  will insure that a tag, "&lt;newLine/>" (or some such) will be output so that the
    XDF parser will be able to recognize where newLines ought to be when the XML file
    is re-read.
    </p>
    @version $Revision$
 */
public class NewLine extends BaseObject implements OutputCharDataInterface {

  //
  //Fields
  //
  private static final String NEWLINE_XDF_REPRESENTATION = "<newLine/>";

  //
  // Constructor
  //

  //no-arg constructor
  public NewLine ()
  {
     init();
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public void NewLine ( Hashtable InitXDFAttributeTable )
  {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    // hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  //
  //Get/Set methods
  //

  /**getValue: get the character data of this object
   */
  public String getValue() {
     return Constants.NEW_LINE;
  }

  //
  // Protected Methods
  //

  /** special method used by constructor methods to
      convienently build the XML attribute list for a given class.
   */
  protected void init()
  {

     // resetAttributes();
     classXDFNodeName = "newLine";

  }

}

