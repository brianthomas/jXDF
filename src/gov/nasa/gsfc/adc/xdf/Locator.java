
/*
 * Locator
 *
 * $Id$
 *
 * (See bottom of file for modification history.)
 *
 * Written by members of the Astronomical Data Center (ADC) at NASA's Goddard 
 * Space Flight Center in Greenbelt, Maryland.  (http://adc.gsfc.nasa.gov)
 */

// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.util.*;

/**
 * Identifies a specific datum location within the n-dimensional data space.
 * An instance of Locator is always tied to a particular instance of 
 * DataModel, which determines the range of the valid axis indexes and
 * the range of valid datum indexes.
 */
public class Locator {
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
     * Set one coordinate of a location in n dimensions, by Axis and a Tickmark
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
     * Set one coordinate of a location in n dimensions, with a Tickmark
     * reference (which includes information about the relevent Axis).
     * For a location to be fully specified, you must call setCoordinate()
     * for every Axis in the DataModel.
     */
    public void setCoordinate(Tickmark tickmark) {
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
 * Revision 1.1.1.1  2000/09/21 17:53:28  thomas
 * Imported Java Source
 *
 *
 */
