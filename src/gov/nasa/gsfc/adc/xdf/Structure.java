
// XDF Structure Class 

// CVS $Id$

package gov.nasa.gsfc.adc.xdf;
import java.util.*;

/** The XDF Structure class.
    @author b.t. (thomas@adc.gsfc.nasa.gov)
    @version $Revision$
*/

public class Structure {

    MsgHandler msg;
    private java.util.ArrayList arrayList;

    // Constructor methods
    /** Will load and XML file into the Structure object. 
        The file must conform to a known version of the XDF DTD. 
      */
    public Structure(String filename) {
       this();

       /* determine the type of file from its extension  -TBD */
       // loadFile(filename);
       // loadFitsFile(filename);

    }

    public Structure () {
       msg = new MsgHandler();
       msg.setDebugMode(true);
       msg.debug("**XDF Structure created.\n");
    }

    // ****
    // Public Methods
    // ****
    /** Populate the Structure object via an XML file (loaded using SAX). */
    public void loadXmlFile (String filename) {

       msg.debug("\ttrying to load file "+filename+"\n");
       Reader myReader = new Reader();

       try {
          Structure myStructure = myReader.parseFile(filename);
       } catch (java.io.IOException e) { 
          msg.error("Unable to read file "+filename);
       }

    }

}

