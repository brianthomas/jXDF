
// XDFDocument Interface 
// CVS $Id$

// XDFDocument.java Copyright (C) 2001 Brian Thomas,
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

import org.w3c.dom.Document;

import java.io.Writer;
import java.io.OutputStream;

/**  XDFDocument interface aggregates the common signature of XDF DOM Documents
 *     regardless of underlying DOM implementation (Xerces 1,2 or Crimson, etc).
 */

public interface XDFDocument extends Document 
{

   public String toXMLString ();
   public void toXMLWriter (Writer outputWriter) throws java.io.IOException;
   public void toXMLFile (String fileName) throws java.io.IOException;
   public void toXMLOutputStream (OutputStream o) throws java.io.IOException;

}

