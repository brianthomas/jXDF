
// XDF Array Class 
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;
import java.util.*;

/** The XDF Array class.
    @author b.t. (thomas@adc.gsfc.nasa.gov)
    @version $Revision$
*/

public class Array {

    MsgHandler msg;
    private java.util.ArrayList axisList;

    // Constructor methods
    public Array () {

       msg = new MsgHandler();
       msg.setDebugMode(true);
       msg.debug("**XDF Array created.\n");

    }

    // ****
    // Public Methods
    // ****

}

