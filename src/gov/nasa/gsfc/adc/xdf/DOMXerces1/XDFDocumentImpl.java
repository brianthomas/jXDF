

// XDF DOM XDFDocumentImpl
// CVS $Id$

// XDFDocumentImpl.java Copyright (C) 2001 Brian Thomas,
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

package gov.nasa.gsfc.adc.xdf.DOMXerces1;

import gov.nasa.gsfc.adc.xdf.Specification;
import gov.nasa.gsfc.adc.xdf.Log;
import gov.nasa.gsfc.adc.xdf.XDFDocument;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.IOException;

import org.w3c.dom.Element;

/** 
    Read any XML Document into a specialized DOM document -- xdf.DOM.XDFDocumentImpl.
 */

// Based on Xerces DocumentImpl class.
public class XDFDocumentImpl extends DocumentImpl 
implements XDFDocument
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

   /** Write this XDF Document out to the indicated file. The file will be clobbered
       by the output, so it is advisable to check for the existence of the file
       *before* using this method if you are worried about losing prior information.
    */
   public void toXMLFile (String filename)
   throws java.io.IOException
   {

    // open file writer
      Writer fileout = new BufferedWriter (new FileWriter(filename));
      // FileWriter fileout = new FileWriter(filename);
      toXMLWriter(fileout);
      fileout.close();

   }


   /** Write the XML representation of this XDF document out to the supplied
       OutputStream object. 
       @deprecated Use the toXMLWriter method instead.
    */
   public void toXMLOutputStream (
                                   OutputStream outputstream 
                                 )
   throws java.io.IOException
   {

      Writer outputWriter = new BufferedWriter(new OutputStreamWriter(outputstream));
      toXMLWriter(outputWriter);
      outputWriter.close();

   }

   /** Get the XML representation of this XDF document as a String.
    */
   public String toXMLString () 
   {

      StringWriter  writer = new StringWriter();        // Writer will be a String
      try {
        toXMLWriter(writer);
      } catch (java.io.IOException e) { 
         e.printStackTrace();
      }

      return writer.toString();

   }


   /** Write the XML representation of this XDF document out to the supplied
       Writer object. 
    */
   public void toXMLWriter (
                                Writer outputWriter
                           )
   throws java.io.IOException
   {

     initBasicXMLWriter(outputWriter, null);
   }

   // 
   // Protected Methods
   //

   protected void initBasicXMLWriter (
                                            Writer outputWriter,
                                            String indent
                                     )
   throws java.io.IOException
   {

      Specification spec = Specification.getInstance();
      OutputFormat format  = new OutputFormat( this );  // Serialize DOM

      if (spec.isPrettyXDFOutput()) {

         if (indent == null) 
            indent = spec.getPrettyXDFOutputIndentation();

         int indentsize = indent.length();

         format.setIndent(indentsize);
         format.setIndenting(true);
      } else {
         format.setIndenting(false);
      }

      XDFSerializer serial = new XDFSerializer( outputWriter, format );

      serial.asDOMSerializer();                      // As a DOM Serializer
      serial.serialize( this.getDocumentElement() );

      // return outputWriter;

   }

}

