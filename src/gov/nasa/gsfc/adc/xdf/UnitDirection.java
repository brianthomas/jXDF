
// XDF UnitDirection
// CVS $Id$

// UnitDirection.java Copyright (C) 2000 Brian Thomas,
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

/** UnitDirection
 * @version $Revision$
  */

public class UnitDirection extends BaseObject {

   public UnitDirection () {

   }

   protected void init() {
      resetXMLAttributes();
   }

}

/* Modification History:
 *
 * $Log$
 * Revision 1.4  2001/07/19 21:59:09  thomas
 * trivial change to class decl. line.
 *
 * Revision 1.3  2001/05/10 21:43:06  thomas
 * added resetXMLAttributes to init().
 *
 * Revision 1.2  2000/11/08 20:14:45  thomas
 * Added GPL blurb to header. -b.t.
 *
 * Revision 1.1  2000/10/11 18:40:14  kelly
 * first layout of the class
 *
 */
