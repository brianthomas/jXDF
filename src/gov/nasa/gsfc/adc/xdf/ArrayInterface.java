
// XDF ArrayInterface Class
// CVS $Id$

// ArrayInterface.java Copyright (C) 2001 Brian Thomas,
// ADC/GSFC-NASA, Code 631, Greenbelt MD, 20771


/*
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

package gov.nasa.gsfc.adc.xdf;

import java.util.List;
import java.util.Hashtable;
import java.util.HashSet;
import java.io.OutputStream;

/**  ArrayInterface aggregates the common signature of XDF derived
 *   data structures.
 */

public interface ArrayInterface 
extends BaseObjectWithXMLElementsInterface,
        ObjectWithUnitsInterface,
        ObjectWithNotesInterface,
        ObjectWithParamsInterface
{

   public String getName ();
   public void setName (String name);

   public String getDescription ();
   public void setDescription (String desc);

   public String getArrayId();
   public void setArrayId (String id);

   public String getAppendTo();
   public void setAppendTo (String id);

   public Object getLessThanValue();
   public void setLessThanValue(Object value);

   public Object getLessThanOrEqualValue();
   public void setLessThanOrEqualValue(Object value);

   public Object getGreaterThanValue();
   public void setGreaterThanValue(Object value);

   public Object getGreaterThanOrEqualValue();
   public void setGreaterThanOrEqualValue(Object value);

   public Object getInfiniteValue();
   public void setInfiniteValue(Object value);

   public Object getInfiniteNegativeValue();
   public void setInfiniteNegativeValue(Object value);

   public Object getNoDataValue();
   public void setNoDataValue(Object value);

   public boolean addAxis (AxisInterface axis);
   public boolean removeAxis(AxisInterface axis);
   public boolean removeAxis(int index);
   public void setAxisList(List axisList);
   public List getAxes();
   public FieldAxis getFieldAxis ();
   public boolean setFieldAxis (FieldAxis fieldAxis);
   public boolean hasFieldAxis();

   public DataCube getDataCube();

   public void setArrayNotes(NotesInterface notesObj);
   public NotesInterface getArrayNotes();

   public DataFormat getDataFormat(); 
   public DataFormat[] getDataFormatList();
   public void setDataFormat(DataFormat dataFormat); 

   public XMLDataIOStyle getXMLDataIOStyle(); 
   public void setXMLDataIOStyle (XMLDataIOStyle object); 

   public int getDimension();
   public int[] getMaxDataIndices();

   // data handling methods
   public Object getData (Locator loc) throws NoDataException;
   public String getStringData(Locator locator) throws NoDataException;
   public int getIntData(Locator locator) throws NoDataException;
   public double getDoubleData(Locator locator) throws NoDataException;
   public boolean removeData (Locator locator);
   public void appendData (Locator locator, String strValue) throws SetDataException; 
   public void setData (Locator locator, double numValue) throws SetDataException;
   public void setData (Locator locator, Double numValue) throws SetDataException;
   public void setData (Locator locator, int numValue) throws SetDataException;
   public void setData (Locator locator, Integer numValue) throws SetDataException;
   public void setData (Locator locator, String strValue ) throws SetDataException;

   public Locator createLocator();

   /* ugh. stupid interface, we shouldnt have these as public methods */
   public int getLongArrayIndex (Locator locator);
   public int getShortArrayIndex (Locator locator);
   public int getShortAxisSize ();

}

/* Modification History:
 *
 * $Log$
 * Revision 1.4  2001/06/26 21:22:25  huang
 * changed return type to boolean for all addObject()
 *
 * Revision 1.3  2001/06/26 19:44:58  thomas
 * added stuff to allow updating of dataCube in situations
 * where the axis size has changed.
 *
 * Revision 1.2  2001/06/18 17:09:09  thomas
 * added setAxisList to interface.
 *
 * Revision 1.1  2001/05/04 20:05:53  thomas
 * Initial version
 *
 *
 */

