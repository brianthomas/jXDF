
// XDF Locator class
// CVS $Id$

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

import java.util.*;

/**
   Identifies a specific datum location within the n-dimensional data space.
   An instance of Locator is always tied to a particular instance of
   DataModel, which determines the range of the valid axis indexes and
   the range of valid datum indexes.
 */

  //double check, should not inherit from BaseObject as is implemented
 public class Locator {
  protected Array parentArray;
  protected List locationList = Collections.synchronizedList(new ArrayList());
  public Locator(Array array) {
    Log.debug("in Locator(Array)");
    parentArray = array;
    List axisList = parentArray.getAxisList();

    /**now, since we KNOW _parentArray is defined
     * (has to be intanciated via XDF::Array ONLY)
     * we can proceed to initialize the axis, index positions
     * to the origin (ie index 0 for each axis).
     * We choose the parent Array axisList ordering for our
     * default location ordering.
     */
    for (int i = 0; i < axisList.size(); i++) {
      Location location = new Location((Axis) axisList.get(i),0);
      locationList.add(location);
    }
  }

  /**setAxisLocation: set the index of an axis
   * @param: Axis, index
   * @return: index if successful, -1 if not
   */
  public int setAxisLocation (Axis axisObj, int index) {
    if ((!parentArray.getAxisList().contains(axisObj)) ||
        (index < 0) ||
        (index > axisObj.getLength()-1) ) {

        Log.error("either index outof range, or axisObj is not an Axis ref in Locator's parentArray");
        Log.error("ignore request");
        return -1;
      }
      int result= -1;
      for (int i = 0; i < locationList.size(); i++) {
        Location location = (Location) locationList.get(i);
        if (location.axis.equals(axisObj) ) {
          location.index = index;
          result = location.index;
          break;
        }
      }
      return result;
  }

  /**getAxisLocation: get the index of an Axis in the Locator object
   * @param: Axis
   * @return: index if successful, -1 if not
   */
  public int getAxisLocation (Axis axisObj) {
     if ((!parentArray.getAxisList().contains(axisObj)) ) {
        Log.error("axisObj is not an Axis ref contained in Locator's parentArray");
        Log.error("regnore request");
        return -1;
     }
     int result= -1;
     for (int i = 0; i<locationList.size(); i++) {
      Location location = (Location)locationList.get(i);
      if (location.axis.equals(axisObj)) {
        result = location.index;
        break;
      }
     }
     return result;
  }

  /**getAxisLocation: get the index list.
   *  note that the ordering is *still* that of the axisList in Array
   * @return: an array of indices
   */
  public int[] getAxisLocation() {
    int[] indices = new int[locationList.size()];
    Location location;
    for (int i = 0; i <locationList.size(); i++) {
      location = (Location) locationList.get(i);
      indices[i] = location.index;
    }
    return indices;
  }

  /**setAxisLocationAxisValue: set the index of an axis to the index of a value
   * along that axis
   * @return: index if successful, -1 if not
   */
  public int setAxisLocationByAxisValue(Axis axisObj, Value valueObj) {
    if ((!parentArray.getAxisList().contains(axisObj)) ||
        valueObj == null ) {
        Log.error("either axisObj is not an Axis ref contained in Locator's parentArray or Value is null");
        Log.error("regnore request");
        return -1;
     }

     return setAxisLocation(axisObj, axisObj.getIndexFromAxisValue(valueObj));

  }

  /**next: Change the locator coordinates to the next datacell as
   * determined from the locator iteration order.
   *  Returns '0' if it must cycle back to the first datacell
   *  to set a new 'next' location.
   */
  public boolean next() {
    boolean outofDataCells = true;
    Location location;
    for (int i = locationList.size()-1; i >=0; i--) {
      location = (Location) locationList.get(i);
      if (location.index < location.axis.getLength()) {
        outofDataCells = false;
        location.index++;
        break;
      }
      location.index = 0;
    }
    return !outofDataCells;
  }

  /**prev: Change the locator coordinates to the previous datacell as
   * determined from the locator iteration order.
   * Returns '0' if it must cycle to the last datacell.
   */
  public boolean prev() {
    boolean outofDataCell = true;
    Location location;
    for (int i = locationList.size()-1; i >=0; i--) {
      location = (Location) locationList.get(i);
      location.index--;
      if (location.index < 0) {
        location.index = location.axis.getLength();
      }
      else {
        outofDataCell = false;
        break;
      }
    }

    return !outofDataCell;
  }

  public void setIterationOrder(List axisOrderListRef) {
    //have to check the list elements are of type Location, double check
    if (axisOrderListRef == null) {
      Log.error("Locator can't setIterationOrder, axisOrderList arg is null");
      return;
    }
    List oldList = locationList;
    locationList = Collections.synchronizedList(new ArrayList());
    Location location;
    Location oldLocation;
    int index = 0;
    for (int i = 0; i < axisOrderListRef.size(); i++) {
      location = null;  //force garbage collection
      location = (Location) axisOrderListRef.get(i);
      for (int j = 0; j < oldList.size(); j++) {
        oldLocation = null;  //force garbage collection
        oldLocation = (Location) oldList.get(j);
        if (oldLocation.axis.equals(location.axis)) {
          index = oldLocation.index;
          break;
        }
      }  //end of inner for loop
      //add Location in order of the axisOrderListRef
      locationList.add(new Location(location.axis, index));
    } //end of outer for loop

    oldList = null;  //force garbage collection
    return;

  }

  /**getIteration:
   * @return: an array of Axises, whose order in the array correspondes to
   * the iteration order
   */
  public Axis[] getIterationOrder() {
    Axis[] axisList = new Axis[locationList.size()];  //array of Axises
    for (int i = 0; i < locationList.size(); i++) {
      axisList[i]= ((Location)locationList.get(i)).axis;
    }
    return axisList;

  }


  /**reset: reset the locator to the origin
   *
   */
    public void reset() {
      for (int i = 0; i < locationList.size(); i++) {
        ((Location) locationList.get(i)).index = 0;
      }
    }


}  //end of Locator class

  /**Location: class/structure to store the location info in Locator class
   *
   */
  class Location  {
    //Fields
    public Axis axis;
    public int index;

    public Location (Axis axis, int index) {
      this.axis = axis;
      this.index = index;
    }

  }
/* Modification History:
 *
 * $Log$
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
