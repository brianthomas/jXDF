
/*
    CVS $Id$

    loadXDFTest.java Copyright (C) 2001 Brian Thomas
    ADC/GSFC-NASA, Code 631, Greenbelt MD, 20771

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


