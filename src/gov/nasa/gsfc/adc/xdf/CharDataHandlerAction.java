
/** The XDF CharDataHandlerAction Interface
    @version $Revision$
*/

// XDF CharDataHandlerAction Interface
// CVS $Id$

// CharDataHandlerAction.java Copyright (C) 2000 Brian Thomas,
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

import org.xml.sax.SAXException;

/** Interface for all start element actions in the Reader start
    element dispatch table. 
*/
public interface CharDataHandlerAction {
  public void action (SaxDocumentHandler handler, char buf [], int offset, int len) throws SAXException;
}

