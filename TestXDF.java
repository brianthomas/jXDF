
// RCS: $Id$

// simple code to test the XDF package

import gov.nasa.gsfc.adc.*;

public class TestXDF {

    static MsgHandler msg = new MsgHandler();

    // the main routine 
    public static void main(String[] argv) {

        msg.setDebugMode(true);
        msg.debug("Test XDF Program Started ("+argv.length+")\n");
       
        // Test loop
        for (int i = 0; i < argv.length ; i++ ) {

           // test loading XML data
           xdf.Structure xdfStruct = new xdf.Structure(argv[i]);

           // manipulate data tests...

        }

        // finished
        msg.debug("Test XDF Program Stopped.\n");

    }   //End of main

}

