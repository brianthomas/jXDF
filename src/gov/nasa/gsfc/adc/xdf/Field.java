
/*
 * Field
 *
 * (See bottom of file for modification history.)
 *
 * Written by members of the Astronomical Data Center (ADC) at NASA's Goddard 
 * Space Flight Center in Greenbelt, Maryland.  (http://adc.gsfc.nasa.gov)
 */

// CVS $Id$

package gov.nasa.gsfc.adc.xdf;


/**
 * Field 
 */
public class Field extends Tickmark {
// Attributes of tickmark:
//  private String name_;
//  private Axis   axis_;
    private String units_;

    /**
     * Default no-argument constructor.
     */
    public Field() {
    }

    /**
     * Returns the units of the Field.
     */
    public String getUnits() {
        return new String();  // string? hurm.. fixme
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
