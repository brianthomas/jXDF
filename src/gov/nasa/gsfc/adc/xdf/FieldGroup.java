

// CVS $Id$

// FieldGroup.java Copyright (C) 2000 Brian Thomas,
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
 public class FieldGroup extends Group {

    //
    // Fields
    //

    /** This field stores object references to those field group objects
       to which this array object belongs
    */

    /** No-argument constructor
     */
    public FieldGroup () {
       super(); // use superclass constructor
       init(); // my init
    }

    /** Constructor taking a hashtable with key/value pairs for
        the object attributes.
    */
    public FieldGroup (Hashtable InitXDFAttributeTable) {
       super(InitXDFAttributeTable); // use superclass constructor
       init(); //my init

    }


    //
    // Public Methods
    //

    /**Insert a FieldGroup object into this object.
       @return true on success, false on failure. 
    */
    public boolean addFieldGroup (FieldGroup group) {
       //add the group to the groupOwnedHash
       return addMemberObject((Object) group);
    }

    /** Remove a FieldGroup object from this object.
        @return true on success, false on failure
     */
    public boolean removeFieldGroup(FieldGroup group) {
       return removeMemberObject((Object) group);
    }

    //
    // Protected Methods
    //

    protected void init () {

       super.init();

       classXDFNodeName = "fieldGroup";

    }

 }

/* Modification History:
 *
 * $Log$
 * Revision 1.5  2001/06/28 16:50:54  thomas
 * changed add method(s) to return boolean.
 *
 * Revision 1.4  2001/05/04 20:25:37  thomas
 * added super.init() to init().
 *
 * Revision 1.3  2000/11/27 16:57:45  thomas
 * Made init method protected so that extending
 * Dataformats may make use of them. -b.t.
 *
 * Revision 1.2  2000/11/16 19:58:59  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.1  2000/11/01 21:09:40  thomas
 * Initial Version. -b.t.
 *
 * 
 */

