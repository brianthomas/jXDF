
// XDF FieldInterface Class
// CVS $Id$

// FieldInterface.java Copyright (C) 2001 Brian Thomas,
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

/**  FieldInterface aggregates the common signature of XDF derived
 *   data structures.
 */

public interface FieldInterface 
extends BaseObjectWithXMLElementsInterface,
        ObjectWithUnitsInterface, 
        ObjectWithNotesInterface
{

   public String getName ();
   public void setName (String name);

   public String getDescription ();
   public void setDescription (String desc);

   public String getFieldId();
   public void setFieldId (String id);

   public String getFieldIdRef();
   public void setFieldIdRef(String id);

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

   public DataFormat getDataFormat(); 
   public void setDataFormat(DataFormat dataFormat); 

}

/* Modification History:
 *
 * $Log$
 * Revision 1.1  2001/05/04 20:05:53  thomas
 * Initial version
 *
 *
 */

