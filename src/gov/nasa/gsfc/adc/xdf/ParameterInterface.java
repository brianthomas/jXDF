
// XDF ParameterInterface Class
// CVS $Id$

// ParameterInterface.java Copyright (C) 2001 Brian Thomas,
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


/**  ParameterInterface shows the common signature of (scientific) parameter based
 *   data structures for XDF derived data languages.
 */

public interface ParameterInterface 
extends BaseObjectInterface,
        ObjectWithUnitsInterface, 
        ObjectWithNotesInterface 
{

   public String getName ();
   public void setName (String name);

   public String getDescription ();
   public void setDescription (String desc);

   public String getParamId();
   public void setParamId (String strParam);

   public String getParamIdRef();
   public void setParamIdRef (String strParamRef);

   public String getDatatype();
   public void setDatatype(String strDatatype);

   public List getValueList();
   public void setValueList(List value);
   public Value addValue(Value v);
   public boolean removeValue(Value value);
   public boolean removeValue(int index);

}

/* Modification History:
 *
 * $Log$
 * Revision 1.1  2001/05/04 20:05:53  thomas
 * Initial version
 *
 *
 */

