
package test;

import gov.nasa.gsfc.adc.xdf.*;

import java.util.List;
import java.util.Hashtable;
import java.io.IOException;
import java.io.BufferedWriter;
// import java.io.OutputStreamWriter;
import java.io.FileWriter;
import java.io.Writer;
import org.apache.tools.ant.BuildException;

public class loadXDFTest extends org.apache.tools.ant.Task {

  private String inputfile;
  private String outputfile;
  private boolean showSuccess = false;

  public void setInputfile (String strfile) {
     inputfile = strfile;
  }

  public void setShowsuccess (boolean val) {
     showSuccess = val;
  }

  public void setOutputfile (String strfile) {
     outputfile = strfile;
  }

  public void execute() 
  throws BuildException 
  {

     // declare the structure
     XDF s = new XDF();
     Reader r = new Reader();

     r.setReaderXDFStructureObj(s);

     try {

        r.parseFile(inputfile);

     } catch (java.io.IOException e ) { 
        throw new BuildException("Failed: Cant parse file:"+inputfile+"\n"+e.getMessage());
     } catch (Exception e ) { 
        throw new BuildException(e);
     }

     // set the output specification
     Specification.getInstance().setPrettyXDFOutput(true);
     Specification.getInstance().setPrettyXDFOutputIndentation("  ");

     try {

        // Writer myWriter = new BufferedWriter(new OutputStreamWriter(System.err));
        Writer myWriter = new BufferedWriter(new FileWriter(outputfile));
        s.toXMLWriter(myWriter,"",false,null,null); // ok
        myWriter.flush();

     } catch (Exception e ) {
        throw new BuildException("Failed: to re-write XDF object back out.\n"+e.getMessage());
     }

     if (showSuccess) 
        System.err.println("Success");

  }

}


