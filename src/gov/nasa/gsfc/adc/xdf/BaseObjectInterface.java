
// XDF BaseObjectInterface Class
// CVS $Id$

// BaseObjectInterface.java Copyright (C) 2001 Brian Thomas,
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

import java.util.Hashtable;
import java.util.HashSet;
import java.util.List;
import java.io.OutputStream;

/**  BaseObjectInterface aggregates the common signature of XDF derived
 *   data structures.
 */

public interface BaseObjectInterface {

   public String getClassXDFNodeName();

   public Hashtable getXMLAttributes();
   public List getAttribOrder();

   public void setXMLNotationHash (HashSet hash);

   // output methods
   public void toXMLFile (String filename, Hashtable XMLDeclAttribs);
   public void toXMLFile (String filename);
 
   public void toXMLOutputStream (   OutputStream outputstream,
                                     Hashtable XMLDeclAttribs,
                                     String indent,
                                     boolean dontCloseNode,
                                     String newNodeNameString,
                                     String noChildObjectNodeName
                                 );

   public void toXMLOutputStream (  OutputStream outputstream,
                                    Hashtable XMLDeclAttribs,
                                    String indent
                                 );
   public void toXMLOutputStream (OutputStream outputstream, Hashtable XMLDeclAttribs); 
   public void toXMLOutputStream (OutputStream outputstream, String indent); 
   public void toXMLOutputStream (OutputStream outputstream ); 

}

/* Modification History:
 *
 * $Log$
 * Revision 1.1  2001/05/04 20:05:53  thomas
 * Initial version
 *
 *
 */
