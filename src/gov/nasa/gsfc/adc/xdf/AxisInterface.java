// XDF AxisInterface Class
// CVS $Id$

// AxisInterface.java Copyright (C) 2001 Brian Thomas,
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

/**  AxisInterface aggregates the common signature of Axis
 *   and FieldAxis
 */

public interface AxisInterface extends BaseObjectWithXMLElementsInterface {

   public int getLength();
   public String getAxisId();
   public String getAlign();
   /* ugh. shouldnt be public method */
   public void setParentArray(Array parent);

}

/* Modification History:
 *
 * $Log$
 * Revision 1.7  2001/06/26 19:44:59  thomas
 * added stuff to allow updating of dataCube in situations
 * where the axis size has changed.
 *
 * Revision 1.6  2001/05/04 20:17:15  thomas
 * Now extends BaseObjectWithXMLElementsInterface
 *
 * Revision 1.5  2001/02/07 18:42:57  thomas
 * No real change. -b.t.
 *
 * Revision 1.4  2000/11/16 19:50:07  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.3  2000/11/09 23:21:53  kelly
 * added more documentation  -k.z.
 *
 * Revision 1.2  2000/11/08 19:25:30  thomas
 * Added GPL/CVS header to file. -b.t.
 *
 * Revision 1.1  2000/10/30 18:21:09  kelly
 * created the class for the common signature of Axis and FieldAxis.  -k.z.
 *
 */
