
// XDF ValueGroup Class
// CVS $Id$

// ValueGroup.java Copyright (C) 2000 Brian Thomas,
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
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

/** ValueGroup:
    @version $Revision$
*/
public class ValueGroup extends Group {

 
    //
    // Fields
    //
    
    /** This field stores object references to those parameter group objects
       to which this array object belongs
    */  
    protected Set valueGroupOwnedHash = Collections.synchronizedSet(new HashSet()); 

    /** No-argument constructor
     */
    public ValueGroup () {
       super(); // use superclass constructor 
       init(); // my init
    }

    /** Constructor taking a hashtable with key/value pairs for
        the object attributes. 
    */
    public ValueGroup (Hashtable InitXDFAttributeTable) {
       super(InitXDFAttributeTable); // use superclass constructor 
       init(); // my init
    }


    // 
    // Public Methods
    //

    /**Insert a ValueGroup object into this object.
       @return:a ValueGroup object reference on success, null on failure.
    */
    public ValueGroup addValueGroup (ValueGroup group) {
       //add the group to the groupOwnedHash
       addMemberObject((Object) group); // paramGroupOwnedHash.add(group);
       return group;
    }

    /** Remove a ValueGroup object from the hashset--valueGroupOwnedHash
        @return: true on success, false on failure
     */
    public boolean removeValueGroup(ValueGroup group) {

       // return paramGroupOwnedHash.remove(group);
       if( removeMemberObject((Object) group) != null)
          return true;
       return false;

    }

    //
    // Private Methods
    //

    private void init () {
       classXDFNodeName = "valueGroup";
    }


}

/* Modification History:
 *
 * $Log$
 * Revision 1.2  2000/10/26 20:41:54  thomas
 * Inserted needed code to bring to initial version -b.t.
 *
 * Revision 1.1  2000/10/11 18:41:08  kelly
 * created the class
 *
 */
