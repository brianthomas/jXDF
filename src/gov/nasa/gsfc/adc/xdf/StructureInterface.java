
// XDF StructureInterface Class
// CVS $Id$

// StructureInterface.java Copyright (C) 2001 Brian Thomas,
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

/**  StructureInterface gives the common signature of XDF structure derived
 *   data objects.
 */

public interface StructureInterface 
extends BaseObjectWithXMLElementsInterface,
        ObjectWithNotesInterface,
        ObjectWithParamsInterface
   
{

   public String getName ();
   public void setName (String name);

   public String getDescription ();
   public void setDescription (String desc);

   public ArrayInterface addArray (ArrayInterface array);
   public boolean removeArray(ArrayInterface array);
   public boolean removeArray(int index);
   public List getArrayList();
   public void setArrayList(List arrayList);

   public StructureInterface addStructure (StructureInterface structure);
   public boolean removeStructure (StructureInterface structure);
   public boolean removeStructure (int index);
   public List getStructList();
   public void setStructList(List structList);


}

/* Modification History:
 *
 * $Log$
 * Revision 1.1  2001/05/04 20:05:53  thomas
 * Initial version
 *
 *
 */

