

// XDF DOM XDFDocumentImpl
// CVS $Id$

// XDFDocumentImpl.java Copyright (C) 2002 Brian Thomas,
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

package gov.nasa.gsfc.adc.xdf.DOMCrimson;

import gov.nasa.gsfc.adc.xdf.Log;
import gov.nasa.gsfc.adc.xdf.Specification;
import gov.nasa.gsfc.adc.xdf.XDFDocument;
import gov.nasa.gsfc.adc.xdf.XDF;

import org.apache.crimson.tree.XmlDocument;
import org.apache.crimson.tree.XmlWriteContext;

import java.io.Writer;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;

// import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** 
    Read any XML Document into a specialized DOM document -- xdf.DOM.XDFDocumentImpl.
 */

// Based on Xerces DocumentImpl class.
public class XDFDocumentImpl extends XmlDocument
implements XDFDocument 
{

   // 
   // Fields
   //

   //
   // Constructors
   //

   /** A placebo method. The crimson package apparently wont allow replacing
       the document root element with another element object :P. Dont use or
       you will get an error message. This method is here to make this class
       comply with XDFDocument interface and in the hopes that this problem
       is fixed by the Crimson package maintainers in the future. 
    */
   public void setDocumentElement (Element elem) 
   {
     Log.errorln("Crimson API doesnt allow setDocumentElement, sorry.");
/*
      Element oldRoot = this.getDocumentElement();
      this.replaceChild(elem, oldRoot);
*/
   }

   //
   // Other Public Methods
   //

   /** Write the XML representation of this document as a string.
    */
   public String toXMLString () 
   {

      StringWriter writer = new StringWriter();
      XmlWriteContext context = getWriterContext(writer);

      try {
        this.writeXml(context);
      } catch (IOException e) {
        Log.errorln("Cant create string representation of XDF document");
        Log.printStackTrace(e);
      }

      return writer.toString();

   }


   /** Write this document out the supplied Writer.
    */
   public void toXMLWriter (Writer outputWriter) 
   throws java.io.IOException 
   {

      XmlWriteContext context = getWriterContext(outputWriter);

      try {
        this.writeXml(context);
      } catch (IOException e) {
        Log.errorln("toXMLWriter(): Cant create XML representation of XDF document");
        Log.printStackTrace(e);
      }

   }

 /** Write this document the supplied OutputStream object.
      @deprecated Use the toXMLWriter method instead.
  */

   public void toXMLOutputStream (OutputStream o) 
   throws java.io.IOException
   {

      Writer writer = new BufferedWriter(new OutputStreamWriter(o));
      XmlWriteContext context = getWriterContext(writer);

      try {
        this.writeXml(context);
      } catch (IOException e) {
        Log.errorln("toXMLWriter(): Cant create XML representation of XDF document");
        Log.printStackTrace(e);
      }

      writer.close();

   }

  /** Write this document out to the indicated file. The file will be clobbered
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

   //
   // Private Methods
   //

   private XmlWriteContext getWriterContext (Writer writer) 
   {

      Specification spec = Specification.getInstance(); 
      int indentLevel = spec.getPrettyXDFOutputIndentationLength();
      boolean isPretty = spec.isPrettyXDFOutput();
      if (!isPretty) 
      {
        indentLevel = 0;
      }
      XmlWriteContext context = this.createWriteContext(writer, indentLevel);
      return context;

   }


}

