// XDF DataFormat Class
// CVS $Id$
package gov.nasa.gsfc.adc.xdf;

import java.util.*;

// DataFormat.java Copyright (C) 2000 Brian Thomas,
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



/**
 * DataFormat.java:
 * @version $Revision$
 */


 public abstract class DataFormat extends BaseObject {

  /** The no argument constructor.
   */
  public DataFormat ()
  {
    init();
  }

  /** init -- special private method used by constructor methods to
   *  conviently build XML attribute Order list
   */
  private void init()
  {

    classXDFNodeName = "dataFormat";

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    //the order of the attributes that all sub-classses should have
    attribOrder.add(0,"noDataValue");
    attribOrder.add(0,"infiniteNegativeValue");
    attribOrder.add(0,"infiniteValue");
    attribOrder.add(0,"greaterThanOrEqualValue");
    attribOrder.add(0,"greaterThanValue");
    attribOrder.add(0,"lessThanOrEqualValue");
    attribOrder.add(0,"lessThanValue");

  }

  //return the number of bytes, used to the the bytes() function in Perl
  public abstract int numOfBytes();


 }  //end of DataFormat class

 /* Modification History:
 *
 * $Log$
 * Revision 1.2  2000/10/16 14:48:18  kelly
 * pretty much completed the class.  --k.z.
 *
 */
