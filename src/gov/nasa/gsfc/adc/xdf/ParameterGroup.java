
// CVS $Id$

// ParameterGroup.java Copyright (C) 2000 Brian Thomas,
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
import java.util.Hashtable;

/**
 */
 public class ParameterGroup extends Group {

    //
    // Fields
    //

    /** This field stores object references to those parameter group objects
       to which this array object belongs
    */  

    /** No-argument constructor
     */
    public ParameterGroup () {
       super(); // use superclass constructor 
       init(); // my init
    }

    /** Constructor taking a hashtable with key/value pairs for
        the object attributes. 
    */
    public ParameterGroup (Hashtable InitXDFAttributeTable) {
       super(InitXDFAttributeTable); // use superclass constructor 
       init(); // my init
    }


    // 
    // Public Methods
    //

    /**Insert an ParameterGroup object into this object.
       @return:a ParameterGroup object reference on success, null on failure.
    */
    public ParameterGroup addParamGroup (ParameterGroup group) {
       //add the group to the groupOwnedHash
       addMemberObject((Object) group); 
       return group;
    }

    /** Remove a ParameterGroup object from this object.
        @return: true on success, false on failure
     */
    public boolean removeParamGroup(ParameterGroup group) {

       if( removeMemberObject((Object) group) != null) 
          return true;
       return false; 

    }

    //
    // Private Methods
    //

    private void init () {

       classXDFNodeName = "parameterGroup";

    }

 }

/* Modification History:
 *
 * $Log$
 * Revision 1.6  2000/11/01 21:08:47  thomas
 * Un-did prior (stupid) change. Also removed extraneous
 * reference to paramGroupOwnedHash. -b.t.
 *
 * 
 */
