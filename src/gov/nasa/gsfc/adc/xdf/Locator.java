
// XDF Locator class
// CVS $Id$

// Locator.java Copyright (C) 2000 Brian Thomas,
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
   Identifies a specific datum location within the n-dimensional data space.
   An instance of Locator is always tied to a particular instance of
   DataModel, which determines the range of the valid axis indices and
   the range of valid datum indices.
 */

 public class Locator implements Cloneable {

  //
  //Fields
  //
  protected Array parentArray;
  protected List axisOrderList;
  private boolean HasNext;

  //hashtable to store the (axis, index) pair
  protected Hashtable locations;

  //constructor
  public Locator (Array array) {
    init(array);
  }

  //
  // Public Get/Set Methods
  //

  public void setIterationOrder (List axisIterationList) {

    if (axisIterationList == null ||
	axisIterationList.size() == 0)
	return;
    axisOrderList.clear();
    int stop = axisIterationList.size();
    for (int i = 0; i < stop; i++) {
      Object axisObj =  axisIterationList.get(i);
      axisOrderList.add(axisObj);
      locations.put(axisObj, new Integer(0));
    }

    Log.infoln("Locator.setIterationOrder() has reset the current location to the dataCube origin.");

  }

  /** Set the index of an axis.
   * @param axisObj - the axis Object
            index - the index of the axis
   */
  public void setAxisIndex (AxisInterface axisObj, int index) 
  throws AxisLocationOutOfBoundsException 
  {
    if ((!parentArray.getAxisList().contains(axisObj)) ||
        (index < 0) ||
        (index > axisObj.getLength()-1) ) {
        throw new AxisLocationOutOfBoundsException();
    }
    //now update the axis and index pair in the hashtable
    locations.put(axisObj, new Integer(index));
  }

  /** Get the current index (for a particular Axis) in this Locator object. 
      @return the current index if successful, -1 if no such Axis exists in this locator.
   */
   public int getAxisIndex (AxisInterface axisObj) 
   {

       Integer loc = (Integer) locations.get(axisObj);
 
       if (loc !=null)
          return loc.intValue();

       return -1;

   }

   /** Get the current index (for a particular Axis) in this Locator object. 
       @return the current value if successful, null if no such Axis exists in this locator.
   */
   public Value getAxisValue (Axis axisObj)
   {

       Integer loc = (Integer) locations.get(axisObj);
 
       if (loc != null)
          return (Value) axisObj.getValueList().get(loc.intValue());

       return (Value) null;

   }




   /** Set the index of an axis to the index of a value
       along that axis
    */
   public void setAxisIndexByAxisValue(Axis axisObj, Value valueObj) 
   throws AxisLocationOutOfBoundsException
   {

      if ((!parentArray.getAxisList().contains(axisObj)) ||
          valueObj == null ) {
          Log.error("Either axisObj is not an Axis ref contained in Locator's parentArray or Value is null");
          Log.errorln("Ignoring request.");
       }

       try {
          setAxisIndex(axisObj, axisObj.getIndexFromAxisValue(valueObj));
       } catch (AxisLocationOutOfBoundsException e) {
          throw e;
       }

   }

  /** @return true if there are more elements, false if no more elements
   */
  public boolean hasNext() 
  {
     return HasNext;
  }

/*
  public boolean hasNext() 
  {

    int size = axisOrderList.size();

    for (int i = 0; i < size ; i++) {
      AxisInterface axis = (AxisInterface) axisOrderList.get(i);
      int index = ((Integer) locations.get(axis)).intValue();
      if (index < (axis.getLength()-1)) {
        return true; // because index not at last position
                     // on that axis it must be true there is a next location 
                     // within the dataCube
      } //else if (index == (axis.getLength()-1)) {
        // at limit of the position 
      //}
    }

    return false;

  }
*/

  /** Change the locator coordinates to the next datacell as
      determined from the locator iteration order.
      Returns false if it must cycle back to the first datacell
      to set a new 'next' location.
   */

  public boolean next() {

    boolean outOfDataCells = true;

    HasNext = true;

    int size = axisOrderList.size();
    for (int i = 0; i < size ; i++) {
      AxisInterface axis = (AxisInterface) axisOrderList.get(i);
      int index = ((Integer) locations.get(axis)).intValue();
      // are we still within the axis?
      if (index < axis.getLength()-1) {
        outOfDataCells = false;
        // advance current index by one 
        index++;
        locations.put(axis, new Integer(index)); 
        break;  //get out of the for loop
      }

      locations.put(axis, new Integer(0)); // reset index back to the origin of this axis 
    }

    // we cycled back to the origin. Set the global
    // to let us know
    if (outOfDataCells) 
	HasNext = false;

    return !outOfDataCells;

  }

  /** Change the locator coordinates to the previous datacell as
   * determined from the locator iteration order.
   * Returns '0' if it must cycle to the last datacell.
   */
  public boolean prev() {
    boolean outOfDataCells = true;

    HasNext = true;

    int size = axisOrderList.size();
    for (int i = 0; i < size ; i++) {
      AxisInterface axis = (AxisInterface) axisOrderList.get(i);
      int index = ((Integer) locations.get(axis)).intValue();
      index--;
      if (index < 0) {
        locations.put(axis, new Integer(axis.getLength()-1));
      }
      else {
        locations.put(axis, new Integer(index));
        outOfDataCells = false;
        break;  //get out of the for loop
      }
    }

    if (outOfDataCells) HasNext = false;

    return !outOfDataCells;
  }

  /**
   * @return an array of Axes, whose order in the array correspondes to
   * the iteration order
   */
  public List getIterationOrder() {
    return axisOrderList;

  }

  //
  // Other Public Methods
  //

  /** reset the locator to the origin
   *
   */
    public void reset() {
      synchronized(locations) {
        Integer origin = new Integer(0);
        Enumeration enum = locations.keys(); // Must be in synchronized block
            while (enum.hasMoreElements()) {
              AxisInterface axis = (AxisInterface) enum.nextElement();
              locations.put(axis, origin);
            }
      }
    }

    public Object clone() throws CloneNotSupportedException{
      Locator cloneObj = (Locator) super.clone();
      //clone the axisOrderList
      synchronized (this.axisOrderList) {
        synchronized (cloneObj.axisOrderList) {
          int stop = this.axisOrderList.size();
          cloneObj.axisOrderList = Collections.synchronizedList(new ArrayList(stop));
          for (int i = 0; i < stop; i ++) {
            cloneObj.axisOrderList.add(this.axisOrderList.get(i));
          }
        }
      }

      //clone the locations, ie. the (axis, index) pair
      synchronized (this.locations) {
        synchronized (cloneObj.locations) {
          int stop = this.locations.size();
          cloneObj.locations = new Hashtable(stop);
           Enumeration keys = this.locations.keys();
           while (keys.hasMoreElements()){
              Object key = keys.nextElement();
              cloneObj.locations.put(key, this.locations.get(key));
           }

        }
       }
       return cloneObj;
    }

    //
    //PROTECTED methods
    //

    /** Add an axis object to the list of axes within this Locator.
        This is used to adjust its axisOrderList and hashtable locations according
     * to parentArray's axes change. 
     */
    protected void addAxis(AxisInterface addAxisObj) {

      axisOrderList.add(addAxisObj);
      locations.put(addAxisObj, new Integer(0));

    }

    /** Remove an Axis from the list of axes within this Locator.
        This is used to adjust its axisOrderList and hashtable locations according
     *  to parentArray's axis geometry.
     */
    protected void removeAxis (AxisInterface removeAxisObj) {

       int index = axisOrderList.indexOf(removeAxisObj);
       if (index > -1) { 
          removeAxis(index);
       } else  
          Log.warnln("Locator.removeAxis() could not remove Axis from locator.");

    }

    /** Remove an Axis from the list of axes within this Locator.
        This is an alternative implementation.
    */
    protected void removeAxis(int index) {

       Object removedAxisObj = axisOrderList.remove(index);
       if (removedAxisObj != null) 
          locations.remove(removedAxisObj);

    }

    //
    // Private Methods
    //

    private void init (Array array) {

       // just created, must be a next cell
       HasNext = true;

       // set the parentArray
       parentArray = array;

      /**now, since we KNOW parentArray is defined
         (has to be instanciated via Array ONLY)
         we can proceed to initialize the axis, index positions
         to the origin (ie index 0 for each axis).
         We choose the parent Array axisList ordering for our
         default location ordering.
       */

      List axisList = parentArray.getAxisList();

      locations = new Hashtable(axisList.size());
      axisOrderList = Collections.synchronizedList(new ArrayList());

      // lastly, set the iteration order.
      setIterationOrder(axisList);

   }



}  //end of Locator class


/* Modification History:
 *
 * $Log$
 * Revision 1.20  2001/04/23 18:52:17  huang
 * clear the axisOrderList before re-ordering
 *
 * Revision 1.19  2001/02/07 18:32:33  thomas
 * Fixed "hasNext" to correct meaning. Was dropping
 * last value within the array from consideration. -b.t
 *
 * Revision 1.18  2001/01/22 22:10:28  thomas
 * Added removeAxis method. Fixed bug in addAxis, making
 *  bad assumption about readAxisordering. -b.t.
 *
 * Revision 1.17  2000/12/13 18:31:26  thomas
 * Minor spelling change. -b.t.
 *
 * Revision 1.16  2000/11/20 22:01:50  thomas
 * Bad bug in setIterationOrder method. Commented
 * out prior method. Instituted init() method and
 * made used part of it as the new setIterationOrder
 * method. -b.t.
 *
 * Revision 1.15  2000/11/20 20:34:29  thomas
 * Removed debugging message. -b.t.
 *
 * Revision 1.14  2000/11/16 20:01:18  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.13  2000/11/09 23:25:18  kelly
 * added hasNext() method
 *
 * Revision 1.12  2000/11/09 04:24:12  thomas
 * Implimented small efficiency improvements to traversal
 * loops. -b.t.
 *
 * Revision 1.11  2000/11/08 22:30:11  thomas
 * Changed set methods to return void. -b.t.
 *
 * Revision 1.10  2000/11/08 19:57:45  thomas
 * Trimmed down import path to just needed classes. -b.t.
 *
 * Revision 1.9  2000/11/06 21:25:19  kelly
 * --added clone()
 * --added addAxis() methods.  it is called when its parentArray adds an axis
 *
 * Revision 1.8  2000/10/31 21:40:15  kelly
 * minor fix
 *
 * Revision 1.7  2000/10/30 18:14:44  kelly
 * conform to the common interface "AxisInterface" for Axis & FieldAxis -k.z.
 *
 * Revision 1.6  2000/10/26 14:26:08  kelly
 * retrieval order is in sync with the axisOrder now (first axis is the fastest).  -k.z.
 *
 * Revision 1.5  2000/10/22 21:11:10  kelly
 * major rework of the class.
 *
 * Revision 1.4  2000/10/18 15:52:30  kelly
 * pretty much completed the class.  -k.z.
 *
 * Revision 1.3  2000/10/10 19:55:09  cvs
 * merged  in Kellys stuff.
 *
 * Revision 1.2  2000/10/10 17:59:10  cvs
 * Updated documentation. Removed Tickmark variable
 * from setCoordinate method (not a valid class anymore!)
 *
 * Revision 1.1.1.1  2000/09/21 17:53:28  thomas
 * Imported Java Source
 *
 *
 */
