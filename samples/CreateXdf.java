
/*
    CVS $Id$

    CreateXdf.java Copyright (C) 2001 Ping Huang
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

import java.io.*;
import java.util.*;

import gov.nasa.gsfc.adc.xdf.*;

/* Here is a little program showing how to build an XDF object in
 * a program from scratch, rather than from loading from a file.
 */

// written by Ping Huang, huang@roamer.gsfc.nasa.gov

public class CreateXdf
{
    private static final int ROW = 5;
    private static final int COL = 2;

    public static void main (String [] args) 
	throws SetDataException, IOException
    {
	XDF xdf = new XDF();
	Structure structure = new Structure();
	Array array = new Array();
	
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
	Entity href = new Entity();
	href.setName("xdf_table");
	href.setSystemId("xdf_table");
	array.setHref(href);

	// the following 6 lines to set write out style: formatted style
	// otherwise, data will be in tagged format
	/*
	FormattedXMLDataIOStyle xmlIOStyle = new FormattedXMLDataIOStyle(array);
	List cmdList = new Vector();
	ReadCellFormattedIOCmd readCell = new ReadCellFormattedIOCmd();
	cmdList.add(readCell);
	xmlIOStyle.setFormatCommandList(cmdList);
	array.setXMLDataIOStyle(xmlIOStyle);
	*/

	structure.addArray(array);
	xdf.addStructure(structure);

	System.out.println("-----\nPRINT OUT STRUCTURE\n-----");

	Specification.getInstance().setPrettyXDFOutput(true);
	Specification.getInstance().setPrettyXDFOutputIndentation("  ");

	Writer wo = new OutputStreamWriter(System.out);
	xdf.toXMLWriter(wo);
	wo.flush();
	wo.close();
    }
}

