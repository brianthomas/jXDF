
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

public class Locator {
  protected Array parentArray;
  public Locator(Array array) {
    Log.debug("in Locator(Array)");
    parentArray = array;
  }
    private org.log4j.Category    log;      //error and debug logging facility
    private DataModel dm_;
    private ArrayList datumIndexes_;  //List of idexes, one for each Axis
    private Map       indexMap_ = new HashMap(6);


    /** 
     * This should not be publicly available -- you can only get a Locator
     * by calling DataModel.getLocator().
     */
    Locator(DataModel dataModel) {
        dm_ = dataModel;
        // Get access to our log
        log = org.log4j.Category.getInstance("xdf");
        Axis[] axes = dm_.getAxes();
        for (int i = 0; i < axes.length; i++) {
            indexMap_.put(axes[i], new Integer(0));
        }
    }

    /** 
     * Set one coordinate of a location in n dimensions, by Axis and an
     * index.
     * For a location to be fully specified, you must call setCoordinate()
     * for every Axis in the DataModel.
     */
    public void setCoordinate(Axis axis, int index) {
        indexMap_.put(axis, new Integer(index));
    }

    /**
     * Returns the index of the currently set coordinate along the specified
     * axis.
     */
    public int getCoordinate(Axis axis) {
        return ((Integer)indexMap_.get(axis)).intValue();
    }

    /** 
     * Set one coordinate of a location in n dimensions, with an index 
     * reference (which includes information about the relevent Axis).
     * For a location to be fully specified, you must call setCoordinate()
     * for every Axis in the DataModel.
     */
    public void setCoordinate() {
        log.warn("Not implemented yet.");
        //fixme
    }

    /**
     * Returns true if a coordinate has been set for every Axis in the
     * DataModel.
     */
    public boolean isFullySpecified() {
        log.warn("Not implemented yet.");
        return true;  //fixme
    }

}

/* Modification History:
 *
 * $Log$
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
