
import java.io.*;
import java.util.*;

import gov.nasa.gsfc.adc.xdf.*;

// a little program showing how to create and populate
// one type of XDF object.

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
        field1.addToGroup(fieldGroup);
	fieldGroup.addMemberObject(field2);
        field2.addToGroup(fieldGroup);
	
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
//		System.out.println("DEBUG: i = " + i + " j= " +j);
		array.setData(locator,i+j);
		locator.next();
	    }
	}

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

