
// XDF ValueListInterface Class
// CVS $Id$

// ValueListInterface.java Copyright (C) 2001 Brian Thomas,
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

import java.io.Writer;
import java.io.OutputStream;
import java.io.IOException;
import java.util.List;

/**  ValueListInterface aggregates the common signature of XDF ValueList Delimited
     and algorithmic style objects.
 */

public interface ValueListInterface 
{
   public void toXMLWriter (Writer outputWriter, String indent) throws java.io.IOException;
   public void toXMLOutputStream (OutputStream o, String indent) throws java.io.IOException;
   public String toXMLString();
   public List getValues();
   public Object clone() throws CloneNotSupportedException;
}

/* Modification History:
 *
 * $Log$
 * Revision 1.4  2001/09/05 22:03:15  thomas
 * added toXMLString method
 *
 * Revision 1.3  2001/09/04 21:18:44  thomas
 * added to XMLOutputStream method
 *
 * Revision 1.2  2001/07/26 15:56:12  thomas
 * toXMLOutputStream => toXMLWriter method.
 *
 * Revision 1.1  2001/07/11 22:40:29  thomas
 * Initial Version
 *
 *
 */

