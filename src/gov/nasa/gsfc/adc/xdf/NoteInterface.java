
// XDF NoteInterface Class
// CVS $Id$

// NoteInterface.java Copyright (C) 2001 Brian Thomas,
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

/**  NoteInterface shows the common signature of notes held in 
 *   data structures derived from the XDF data language.
 */

public interface NoteInterface extends BaseObjectInterface {

   public String getMark();
   public void setMark (String mark);

   public String getValue ();
   public void setValue (String value);

   public String getNoteId();
   public void setNoteId (String id);

   public String getNoteIdRef();
   public void setNoteIdRef (String id);

   public String getLocation();
   public void setLocation(String where);
   // these arent used yet.
   // public void setLocator (Locator noteLocation);
   // public Locator getLocator ();

   public boolean addText (String text);

}

/* Modification History:
 *
 * $Log$
 * Revision 1.2  2001/06/28 16:50:54  thomas
 * changed add method(s) to return boolean.
 *
 * Revision 1.1  2001/05/04 20:05:53  thomas
 * Initial version
 *
 *
 */

