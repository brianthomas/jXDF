// XDF DataCube Class
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.util.*;



// DataCube.java Copyright (C) 2000 Brian Thomas,
// ADC/GSFC-NASA, Code 631, Greenbelt MD, 20771

public class DataCube extends BaseObject {

 //
  //Fields
  //

  protected Array parentArray;

  public DataCube() {
    Log.debug("in DataCube no-arg constructor");
  }

  public Number setDimension(Number dimension) {
    Log.debug("in DataCube, setDimension, function body empty, returning null now");
    return null;
  }

  public Number getDimension() {
    Log.debug("in DataCube, getDimension, function body empty, returning null now");
    return null;
  }

  public Array setParentArray(Array parentArray) {
    Log.debug("in DataCube, setParentArray()");
    this.parentArray = parentArray;
    return parentArray;
  }

  public Array getParentArray() {
    return parentArray;
  }

  public List getMaxDataIndex() {
    Log.debug("in DataCube, getMaxDataIndex(), function body empty, returning null now");
    return null;
  }


  //
  //other PUBLIC methods
  //

  /**incrementDimension: increase the dimension by 1
   * @return: the current dimension ( which is incremented)
   */
  public Number incrementDimension() {
    Number dim = getDimension();
    if (dim == null)
      return setDimension(new Integer(1));
    else
      return setDimension(new Integer(dim.intValue()+1));

  }

  /**decrementDimension: decrease the dimension by 1
   * @return: the current dimension ( which is decremented)
   */
  public Number decrementDimension() {
    Number dim = getDimension();
    if (dim == null) {
      Log.error(" in DataCube, incrementDimentsion, the dimension is undef");
      return null;
    }
    else
      return setDimension(new Integer(dim.intValue()-1));
  }

   /**addData: Append the SCALAR value onto the requested datacell
   */
  public double  addData (Locator locator, double numValue) {
    Log.error("in DataCube, addData(), function body empty, returning 0");
    return 0;
  }

  /**addData: Append the SCALAR value onto the requested datacell
    */
  public String addData (Locator locator, String strValue) {
   Log.error("in DataCube, addData(), function body empty, returning null");
   return null;
  }

  /** setData: Set the SCALAR value of the requested datacell
   * (via L<XDF::DataCube> LOCATOR REF).
   * Overwrites existing datacell value if any.
   */

   public double  setData (Locator locator, double numValue) {
    Log.error("in DataCube, setData(), function body empty, returning 0");
    return 0;
  }

  /** setData: Set the SCALAR value of the requested datacell
   * (via L<XDF::DataCube> LOCATOR REF).
   * Overwrites existing datacell value if any.
   */
  public String  setData (Locator locator, String strValue) {
   Log.error("in DataCube, setData(), function body empty, returning null");
   return null;
  }

  /**removeData : Remove the requested data from the indicated datacell
   * (via L<XDF::DataCube> LOCATOR REF) in the XDF::DataCube held in this Array.
   * B<NOT CURRENTLY IMPLEMENTED>.
   */

   public double  removeData (Locator locator, double numValue) {
    Log.error("in DataCube, removeData(), function body empty, returning 0");
    return 0;
  }

 /**removeData : Remove the requested data from the indicated datacell
   * (via L<XDF::DataCube> LOCATOR REF) in the XDF::DataCube held in this Array.
   * B<NOT CURRENTLY IMPLEMENTED>.
   */
  public String  removeData (Locator locator, String strValue) {
   Log.error("in DataCube, setData(), function body empty, returning null");
   return null;
  }



}

