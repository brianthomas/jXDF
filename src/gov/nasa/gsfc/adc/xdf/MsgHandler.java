
// XDF Message Handler Class
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.io.OutputStreamWriter;

/** A simple class to deal with debugging and
    error reporting within the XDF package.
    This class is not intended to be a public
    utility and may be replaced by a more mature
    software package at some future date.

    @author b.t. (thomas@adc.gsfc.nasa.gov)
    @version $Revision$
*/ 

public class MsgHandler {

   boolean debugMode;
  
   /** Constructor method. */ 
   public void MsgHandler () {
      debugMode = false; // init debug mode to be 'off' as default
   }

   /** Set the debugging mode. If true debugging messages are 
       printed to standard out.
    */
   public void setDebugMode (boolean mode) { debugMode = mode; }

   /** Print debugging message to standard out. You have to insert
       the newline in the message if you want it.
    */
   public void debug(String myMsg) { 

      if (debugMode) {
         System.out.print(myMsg);
      }
   }

   /** Print debugging message to standard out. */
   public void debug(char buf [], int offset, int len) { 

     if (debugMode) {
        try {
          OutputStreamWriter out = new OutputStreamWriter (System.out, "UTF8");
          out.write(buf, offset, len);
          out.flush();
        } catch (java.io.UnsupportedEncodingException e) {
        } catch (java.io.IOException e) {
        }
     }

   }

   /** Print error message to standard error. */
   public void error(String errorMsg) { System.err.println(errorMsg); }

   /** Print error message and an exception stack trace to standard error. */
   public void error(String errorMsg, Exception e) { 
      if( e != null ) {
         e.printStackTrace(System.err);
      }
      error(errorMsg);
   }

}
 
