

// XDF DOM XDFElementImpl
// CVS $Id$

// ElementImpl.java Copyright (C) 2001 Brian Thomas,
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

import gov.nasa.gsfc.adc.xdf.Constants;
import gov.nasa.gsfc.adc.xdf.Reader;
import gov.nasa.gsfc.adc.xdf.XDF;
import gov.nasa.gsfc.adc.xdf.Log;

import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.CoreDocumentImpl;

import  org.apache.xml.serialize.OutputFormat;
import  org.apache.xml.serialize.Serializer;
import  org.apache.xml.serialize.SerializerFactory;
import  org.apache.xml.serialize.XMLSerializer;
import  org.apache.xml.serialize.DOMSerializer;

import java.io.StringWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** 
 */

// Based on Xerces ElementImpl class.
public class XDFElementImpl extends ElementImpl 
implements Element
{

   // 
   // Fields
   //
   private XDF myXDFObject = null;

   //
   // Constructors
   //

   // public XDFElementImpl (CoreDocumentImpl owner, Element baseElement ) 
   public XDFElementImpl (Document owner, Element baseElement ) 
   throws java.io.IOException
   {
      super((CoreDocumentImpl) owner, Constants.XDF_ROOT_NODE_NAME);
      // now use passed element to initialize XDF object held by this node

      Reader reader = new Reader ();
      String xmlContent = getXMLStringElemRepresentation(baseElement);
      myXDFObject = reader.parseString(xmlContent);
      
   }

   //
   // Get/Set Methods 
   //

   public XDF getXDFObject () {
       return myXDFObject;
   }

   public void setXDFObject (XDF object) 
   {
       myXDFObject = object;
   }

   //
   // Other Public Methods
   //

   public String toXMLString () {
      if (myXDFObject != null) {
         return myXDFObject.toXMLString();
      } else {
         String rootNodeName = "<" + Constants.XDF_ROOT_NODE_NAME +"/>";
         return rootNodeName; 
      }
   }

   // 
   // Private Methods
   //
   private String getXMLStringElemRepresentation (Element elem) {

      if (!(elem instanceof ElementImpl)) {
         Log.errorln("Internal Error: XDFElementImpl cannot set XDF element: you are not using XML DOM package with class ElementImpl (Xerces), Ignoring request.");
         return this.toXMLString(); // just return what we currently have 
      }

//      Specification spec = Specification.getInstance();
      OutputFormat format  = new OutputFormat(this.getOwnerDocument());  // Serialize DOM

//      if (spec.isPrettyXDFOutput()) {
//         int indentsize = spec.getPrettyXDFOutputIndentationLength();
//         format.setIndent(indentsize);
         format.setIndenting(true);
/*
      } else { 
         format.setIndenting(false);
      }
*/

      StringWriter  writer = new StringWriter();        // Writer will be a String
      XMLSerializer    serial = new XMLSerializer( writer, format );

      try {
         serial.asDOMSerializer();                      // As a DOM Serializer
         // serial.serialize( this.getDocumentElement() );
         serial.serialize( elem);
      } catch (java.io.IOException e) {
         e.printStackTrace();
      }

      return writer.toString();

   }

}

/* Modification History:
 *
 * $Log$
 * Revision 1.2  2001/09/05 22:04:45  thomas
 * small changes related to moving info from Specification to Constants class
 *
 * Revision 1.1  2001/08/31 19:57:47  thomas
 * Initial Version
 *
 *
 */
