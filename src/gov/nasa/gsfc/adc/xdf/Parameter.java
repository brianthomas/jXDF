
/*
 * Parameter
 *
 * (See bottom of file for modification history.)
 *
 * Written by members of the Astronomical Data Center (ADC) at NASA's Goddard 
 * Space Flight Center in Greenbelt, Maryland.  (http://adc.gsfc.nasa.gov)
 */

// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.util.ArrayList;

/**
 * A named parameter related to an XDF data space.
 */
public class Parameter {
    String   name_;
    Object   value_;
    String   units_;
    String   description_;

    /** 
     * Default no-argument constructor.
     */
    public Parameter() {
    }

    /** 
     * Full constructor.
     */
    public Parameter(String name, Object value,
                     String units, String description) {
    }

    /**
     * Returns the Parameter's name.
     */
    public String getName() {
        return new String();  //fixme
    }

    /**
     * Sets the Parameter's name.
     */
    public void setName(String name) {
    }

    /**
     * Returns the Parameter's value.
     */
    public Object getValue() {
        return new Object();  //fixme
    }

    /**
     * Sets the Parameter's value.
     */
    public void setValue(String value) {
    }

    /**
     * Returns the Parameter's units.
     */
    public String getUnits() {
        return new String();  //fixme
    }

    /**
     * Sets the Parameter's units.
     */
    public void setUnits(String units) {
    }

    /**
     * Returns the Parameter's description.
     */
    public String getDescription() {
        return new String();  //fixme
    }

    /**
     * Sets the Parameter's description.
     */
    public void setDescription(String description) {
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
