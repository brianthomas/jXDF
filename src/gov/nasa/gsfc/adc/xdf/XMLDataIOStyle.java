// XDF XMLDataIOStyle Class
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.util.*;

// XMLDataIOStyle.java Copyright (C) 2000 Brian Thomas,
// ADC/GSFC-NASA, Code 631, Greenbelt MD, 20771

public abstract class XMLDataIOStyle extends BaseObject {

 //
  //Fields
  //

  protected Array parentArray;

  public XMLDataIOStyle() {
    Log.debug("in XMLDataIOStyle no-arg constructor");
  }

  public Array setParentArray(Array parentArray) {
    Log.debug("in XMLDataIOStyle, setParentArray()");
    this.parentArray = parentArray;
    return parentArray;
  }

  public Array getParentArray() {
    return parentArray;
  }

}
/* Modification History:
 *
 * $Log$
 * Revision 1.1  2000/10/11 19:06:26  kelly
 * created the class
 *
 */
