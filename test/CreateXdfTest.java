
/*
    CVS $Id$

    CreateXdfTest.java Copyright (C) 2001 Brian Thomas
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

import java.io.*;
import java.util.*;

import gov.nasa.gsfc.adc.xdf.*;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

/* Here is a little program showing how to build an XDF object in
 * a program from scratch, rather than from loading from a file.
 */

// based on original code in CreateXDF.java written by Ping Huang, huang@roamer.gsfc.nasa.gov
// modified to present code by Brian Thomas, thomas@adc.gsfc.nasa.gov 

public class CreateXdfTest extends org.apache.tools.ant.Task
{

    // these control auto-generation of data
    private static final int ROW = 5;
    private static final int COL = 2;

    // runtime parameters
    private String outputfile = "out.xml";
    private String externalFileEntityName = "xdfTest";
    private String externalFileName = "xdfTest.dat";
    private String myXMLDataIOStyle = "tagged";
    private boolean showSuccess = false;
    private boolean useExternalFile = false;

    //
    // public methods
    //

    public void setOutputfile (String strfile) 
    {
       outputfile = strfile;
    }
 
    public void setWritedatatoseparatefile (boolean val) 
    {
         useExternalFile = val;
    }

    public void setDataiostyle (String type) 
    {
       myXMLDataIOStyle = type;
    }

    public void setShowsuccess (boolean val) {
       showSuccess = val;
    }

    public void setEntityname (String value) 
    {
       externalFileEntityName = value;
    }

    public void setEntityfilename (String value) 
    {
       externalFileName = value;
    }

    public void execute()
    throws BuildException
    {


        try 
        {

            XDF xdf = new XDF();
            XMLDeclaration xmlDecl = new XMLDeclaration(); 
            DocumentType doctype = new DocumentType(xdf); 
            Array array = new Array();
         
            xdf.setXMLDeclaration(xmlDecl);
            xdf.setDocumentType(doctype);
            
            FieldAxis fieldAxis = new FieldAxis();
            fieldAxis.setName("Column");
            fieldAxis.setAxisId("column_id");
         
            Axis axis = new Axis(ROW);
            axis.setName("row");
            axis.setAxisId("row_id");
         
            FieldGroup fieldGroup = new FieldGroup();
            fieldGroup.setName("fieldGroup");
            fieldGroup.setDescription("fieldGroup description");
         
            IntegerDataFormat dateFormat = new IntegerDataFormat();
            dateFormat.setWidth(new Integer(32));
         
            Field field1 = new Field ();
            field1.setName("field1");
            field1.setDescription("field1-description");
            field1.setFieldId ("field1-id");
            field1.setDataFormat(dateFormat);
         
            Field field2 = new Field ();
            field2.setName("field2");
            field2.setDescription("field2-description");
            field2.setFieldId ("field2-id");    
            field2.setDataFormat(dateFormat);
         
            fieldGroup.addMemberObject(field1);
            fieldGroup.addMemberObject(field2);
            
            fieldAxis.addFieldGroup(fieldGroup);
            fieldAxis.addField(field1);
            fieldAxis.addField(field2);
            
            array.addAxis (axis);
            array.setFieldAxis (fieldAxis);
            
            Locator locator = array.createLocator();
            Vector list = new Vector ();
            
            list.add(axis);
            list.add(fieldAxis);
            locator.setIterationOrder(list);
            
            for (int i=0; i<ROW; i++) {
                for (int j=0; j<COL; j++) {
            	array.setData(locator,i+j);
            	locator.next();
                }
            }
         
            // the following 4 lines to declear the data will be written to
            // a seperate file, xdf_table;
            if (useExternalFile) {
               Entity href = new Entity();
               href.setName(externalFileEntityName);
               href.setSystemId(externalFileName);
               array.setHref(href);
            }
         
            // the following 6 lines to set write out style: formatted style
            // otherwise, data will be in tagged format

            if (myXMLDataIOStyle.equals("formatted")) {
               FormattedXMLDataIOStyle xmlIOStyle = new FormattedXMLDataIOStyle(array);
               List cmdList = new Vector();
               ReadCellFormattedIOCmd readCell = new ReadCellFormattedIOCmd();
               cmdList.add(readCell);
               xmlIOStyle.setFormatCommandList(cmdList);
               array.setXMLDataIOStyle(xmlIOStyle);
            } else if (myXMLDataIOStyle.equals("delimited")) {
               DelimitedXMLDataIOStyle xmlIOStyle = new DelimitedXMLDataIOStyle(array);
               Delimiter delimiter = new Delimiter();
               delimiter.setRepeatable("no");
               xmlIOStyle.setDelimiter(delimiter);
               array.setXMLDataIOStyle(xmlIOStyle);
            }
         
            xdf.addArray(array);
         
            Specification.getInstance().setPrettyXDFOutput(true);
            // Specification.getInstance().setPrettyXDFOutputIndentation("  ");
         
            Writer wo = new FileWriter(new File(outputfile));
            xdf.toXMLWriter(wo);
            wo.flush();
            wo.close();

	} 
        catch (Exception e) 
        {
           throw new BuildException("Failed:"+e.getMessage());
        }

        if (showSuccess)
           System.err.println("Success");



    }
}

