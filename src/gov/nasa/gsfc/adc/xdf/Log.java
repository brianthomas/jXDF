// XDF Log Class
// CVS $Id$
package gov.nasa.gsfc.adc.xdf;
import java.util.*;
import java.io.*;
/**
 * Log.java: to handle debug, error msg, etc.
 * @version $Revision$
 */

public class Log {
  //
  //Fields
  //
  protected int priority ; //hold the priority that is read from the configuration file
  protected OutputStream output ;  //hold the output stream

  //got to find a way to specify relative file path!!!
  public static final String DEFAULT_CONFIG_FILE = "C:/Data/XDF/gov/nasa/gsfc/adc/xdf/XDFLogConfig";   //default configuration file

  public static final OutputStream DEFAULT_OUTPUTSTREAM = System.out;
  public static final int DEFAULT_PRIORITY = 0;

  //
  //constructor and related methods
  //
  /**
   * no arg constructor
   */
   public Log () {
     configure(DEFAULT_CONFIG_FILE);
   }


  //
  //Other Public Methods
  //

  /**
   * configure: read in the properties from the configuration file
   */
   public void configure(String configFileName) {
    Properties props = new Properties();  //the property hashtable
    try {
      FileInputStream istream = new FileInputStream(configFileName);
      props.load(istream);
      istream.close();
    }
    catch (IOException e) {
      System.err.println("Could not read configuration file [" + configFileName+ "].");
     // e.printStackTrace();
      System.err.println("Ignoring configuration file [" + configFileName+"].");
      System.err.println("using default config");
      defaultConfig();
      return;
    }
    // If we reach here, then the config file is alright.
    System.out.println("Reading configuration.");
    configure(props);
  }

  public void defaultConfig() {
    output = DEFAULT_OUTPUTSTREAM;
    priority = DEFAULT_PRIORITY;
  }

  /**
   *  Read configuration options from <code>properties</code>.
  */

   public void configure(Properties properties){

    String outputFormat = properties.getProperty("Output");
    String strPri = properties.getProperty("Priority");
    if (strPri == null)
      priority = DEFAULT_PRIORITY;
    else
      priority = Integer.parseInt(properties.getProperty("Priority"));
    if(outputFormat != null) {
      if ( outputFormat.equalsIgnoreCase("System.out")) {  //output is System.out
        //System.out.println("yes, config read in is System.out");
        output = new PrintStream(System.out);
      }
      else {  //output is a file
        try {
          output = new FileOutputStream(outputFormat, true);  //write to a file, using append
        }
        catch( FileNotFoundException e) {
          System.out.println("error opening the log file to write to");
          System.out.println("logs are by default printed to System.out");
          // use default output
          output = new PrintStream(DEFAULT_OUTPUTSTREAM);
        }
      }
    }
    else { // default
     // System.out.println("yes, this is the default ");
      output = new PrintStream(DEFAULT_OUTPUTSTREAM);
    }
  }

  public void info(String msg) {
    if (priority > Priority.INFO)
      return;
    try {
      output.write(msg.getBytes());
      output.write(Constants.NEW_LINE.getBytes());
    }
    catch (IOException e) {
      System.err.println("error in Log.info()");
      e.printStackTrace();
    }
  }

  public void debug(String msg) {
    if (priority > Priority.DEBUG)
      return;
    try {
      output.write(msg.getBytes());
      output.write(Constants.NEW_LINE.getBytes());
    }
    catch (IOException e) {
      System.err.println("error in Log.debug()");
      e.printStackTrace();
    }
  }
  public void warn(String msg) {
    if (priority > Priority.WARN)
      return;
    try {
      output.write(msg.getBytes());
      output.write(Constants.NEW_LINE.getBytes());
    }
    catch (IOException e) {
      System.err.println("error in Log.warn()");
      e.printStackTrace();
    }
  }

  public void error(String msg) {
    if (priority > Priority.ERROR)
      return;
    try {
      output.write(msg.getBytes());
      output.write(Constants.NEW_LINE.getBytes());
    }
    catch (IOException e) {
      System.err.println("error in Log.error()");
      e.printStackTrace();
    }
  }





}

/**
 * defines constants for priorities in Log class: error|warn|debug|info
 */
class Priority {
  public static final int INFO = 0;
  public static final int DEBUG = 1;
  public static final int WARN = 2;
  public static final int ERROR = 3;
}