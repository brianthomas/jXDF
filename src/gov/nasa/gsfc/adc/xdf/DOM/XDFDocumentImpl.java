

// XDF DOM XDFDocumentImpl
// CVS $Id$

// DocumentImpl.java Copyright (C) 2001 Brian Thomas,
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

package gov.nasa.gsfc.adc.xdf.DOM;

import gov.nasa.gsfc.adc.xdf.Specification;
import gov.nasa.gsfc.adc.xdf.Log;

import org.apache.xerces.dom.DocumentImpl;
// import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xml.serialize.OutputFormat;

import java.io.StringWriter;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** 
    Read any XML Document into a specialized DOM document -- xdf.DOM.XDFDocumentImpl.
 */

// Based on Xerces DocumentImpl class.
public class XDFDocumentImpl extends DocumentImpl 
implements Document
{

   // 
   // Fields
   //

   //
   // Constructors
   //

   //
   // Get/Set Methods 
   //

   public void setDocumentElement (Element elem) {
      this.replaceChild(elem, this.getDocumentElement());
   }

   //
   // Other Public Methods
   //
   public String toXMLString () {

      Specification spec = Specification.getInstance();
      OutputFormat format  = new OutputFormat( this );  // Serialize DOM

      if (spec.isPrettyXDFOutput()) {
         int indentsize = spec.getPrettyXDFOutputIndentationLength();
         format.setIndent(indentsize);
         format.setIndenting(true);
      } else { 
         format.setIndenting(false);
      }

      StringWriter  writer = new StringWriter();        // Writer will be a String
      // XMLSerializer serial = new XMLSerializer( writer, format );
      XDFSerializer serial = new XDFSerializer( writer, format );

      try {
         serial.asDOMSerializer();                      // As a DOM Serializer
         serial.serialize( this.getDocumentElement() );
      } catch (IOException e) {
         e.printStackTrace();
      }

      return writer.toString();

   }

}

/* Modification History:
 *
 * $Log$
 * Revision 1.1  2001/08/31 19:57:45  thomas
 * Initial Version
 *
 *
 */
