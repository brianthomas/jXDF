
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
  private boolean nextCellAvailable;
  private boolean prevCellAvailable;
  private int dimension;

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

    // Ping???: correct treatment???
    // if null, set it to null or just ignore and return???
    // also, the num of axies  of the parent array should be checked before a reset???
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

    dimension = axisOrderList.size();

    Log.infoln("Locator.setIterationOrder() has reset the current location to the dataCube origin.");

  }

  /** Set the index of an axis.
   * @param axisObj - the axis Object
            index - the index of the axis
   */
  public void setAxisIndex (AxisInterface axisObj, int index) 
  throws AxisLocationOutOfBoundsException 
  {
    // Ping-??? if not exists, should call addAxis()
    if ((!parentArray.getAxes().contains(axisObj)) ||
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

   /** 
       @return the current value if successful, null if no such Axis exists in this locator.
   */
   public Value getAxisValue (Axis axisObj)
   {

       Integer loc = (Integer) locations.get(axisObj);
 
       if (loc != null)
          return (Value) axisObj.getValueList().get(loc.intValue());

       return null;

   }


   /** Set the index of an axis to the index of a value
       along that axis
    */
   public void setAxisIndexByAxisValue(Axis axisObj, Value valueObj) 
   throws AxisLocationOutOfBoundsException
   {

      if ((!parentArray.getAxes().contains(axisObj)) ||
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

   /** @return true if there are more Next data cells; false if no more data elements
    */
   public boolean hasNext() 
   {
       return nextCellAvailable;
   }

   /** @return true if there are any more Previous data cells; false if no more data elements
    */
  public boolean hasPrev() 
  {
     return prevCellAvailable;
  }


  /**
   * @return an integer array, each element is the position along
   * the corresponding axis
   */
  public int [] getLocation() {
      int [] position = new int [dimension];
      for (int i=0; i<dimension; i++) {
	  AxisInterface axis = (AxisInterface) axisOrderList.get(i);
	  position[i] = ((Integer) locations.get(axis)).intValue();
      }
      return position;
  }


  /**
   * set location according to dimension indexes;
   * e.g. [1,2] means the 2nd row and 3rd column in a 2-d table
   * (index starts at 0).
   * @return boolean: true if the input indexes are within the
   * array axes' range; false, otherwise
   */
  public boolean setLocation(int [] location) {
      if (location.length != dimension)
	  return false; // error msg???
      for (int i=0; i<dimension; i++) {
	  AxisInterface axis = (AxisInterface) axisOrderList.get(i);
	  // Ping??? some general Array dataCell exception class
	  // should be defined to handle this type of errors elegantly
	  if (location[i] >= 0 && location[i] <= axis.getLength()-1)
	      locations.put(axis, new Integer(location[i]));
	  else
	      return false; // error msg ???
      }
      return true;
  }


  /** Change the locator coordinates to the next datacell as
      determined from the locator iteration order.
      Returns false if it must cycle back to the first datacell
      to set a new 'next' location.
   */
  public boolean next() {

    boolean outOfDataCells = true;

    nextCellAvailable = true;

    for (int i = 0; i < dimension; i++) {
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
	nextCellAvailable = false;

    return !outOfDataCells;

  }

  /** Change the locator coordinates to the previous datacell as
   * determined from the locator iteration order.
   * Returns '0' if it must cycle to the last datacell.
   */
  public boolean prev() {
    boolean outOfDataCells = true;

    prevCellAvailable = true;

    for (int i = 0; i < dimension ; i++) {
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

    if (outOfDataCells) 
	prevCellAvailable = false;

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

    /** How many locations his locator may 'visit' in the
        present array. This value is the same as the number of dataCells within 
        the parent Array object of the locator.
     */
    public int numOfLocations () {

       int numOfLocations = 0;
       for (int i = 0, numOfAxes = axisOrderList.size(); i < numOfAxes; i++) { 
           numOfLocations += ((AxisInterface) axisOrderList.get(i)).getLength();
       }

       return numOfLocations;
    }

    /** Create a clone of this locator. The current location  
        of the clone is the same as the parent object.
     */
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
    // (These 3 methods should not be here, solution Ping???)
    //

    /** Add an axis object to the list of axes within this Locator.
        This is used to adjust its axisOrderList and hashtable locations according
     *  to parentArray's axes change. 
     *  @return true on success, false otherwise.
     */
    protected boolean addAxis(AxisInterface addAxisObj) {

       if (!axisOrderList.add(addAxisObj)) {
          return false;
       }
       locations.put(addAxisObj, new Integer(0));
       dimension++;
       return true;

    }

    /** Remove an Axis from the list of axes within this Locator.
        This is used to adjust its axisOrderList and hashtable locations according
     *  to parentArray's axis geometry.
     */
    protected void removeAxis (AxisInterface removeAxisObj) {

       int index = axisOrderList.indexOf(removeAxisObj);
       if (index > -1) { 
          removeAxis(index);
	  dimension--;
       } else  
          Log.warnln("Locator.removeAxis() could not remove Axis from locator.");

    }

    /** Remove an Axis from the list of axes within this Locator.
        This is an alternative implementation.
    */
    protected void removeAxis(int index) {

       Object removedAxisObj = axisOrderList.remove(index);
       if (removedAxisObj != null) {
          locations.remove(removedAxisObj);
	  dimension--;
       }
    }

    //
    // Private Methods
    //

    private void init (Array array) {

       // set the parentArray
       parentArray = array;

      /**now, since we KNOW parentArray is defined
         (has to be instanciated via Array ONLY)
         we can proceed to initialize the axis, index positions
         to the origin (ie index 0 for each axis).
         We choose the parent Array axisList ordering for our
         default location ordering.

	 We also assume every array has data, 
         i.e.  axisList != null
       */

      List axisList = parentArray.getAxes();
      if (axisList == null) {
	  // shoudl thorw an exception, allowed by dtd???
	  // could use jdk1.4 assertion???
	  locations = new Hashtable();
          dimension=0;
	  nextCellAvailable = false;
      } else {
	  locations = new Hashtable(axisList.size());
          dimension = axisList.size();
	  nextCellAvailable = true;
      }

      // at the first element (if any), no PREVious element available
      prevCellAvailable = false;

      axisOrderList = Collections.synchronizedList(new ArrayList());

      // lastly, set the iteration order.
      // this should be a safe call (e.g. the XMLDataIOStyle will be defined,
      // and the ggetIOAxesOrder will return the correct number of unique axes)
      setIterationOrder(parentArray.getXMLDataIOStyle().getIOAxesOrder());

   }



}  //end of Locator class












