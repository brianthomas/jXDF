
/*
    CVS $Id$

    readAndWriteAnyXMLDocument.java Copyright (C) 2001 Brian Thomas
    ADC/GSFC-NASA, Code 631, Greenbelt MD, 20771

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


import gov.nasa.gsfc.adc.xdf.*;
import gov.nasa.gsfc.adc.xdf.DOM.*;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import org.apache.xml.serialize.OutputFormat;

import java.io.IOException;
import java.io.StringWriter;
import java.io.OutputStreamWriter;

public class readAndWriteAnyXMLDocument {

  public static void main(String[] argv) {

      gov.nasa.gsfc.adc.xdf.DOM.Parser parser = new gov.nasa.gsfc.adc.xdf.DOM.Parser ();
      parser.useValidation(true);

      try {
         parser.parse(argv[0]); 
      } catch (java.io.IOException e ) {
         e.printStackTrace();
      } catch (org.xml.sax.SAXException e ) {
         e.printStackTrace();
      }

      Specification spec = Specification.getInstance();
      spec.setPrettyXDFOutput(true);

      Document doc = parser.getDocument();
      doc.normalize();

      // PRINT it OUT

      if (false) {

         // we may use convenience method to print it
         System.err.println(((XDFDocumentImpl) doc).toXMLString());

      } else {

         // OR alternatively, we may use the XDFSerializer
         OutputFormat    format  = new OutputFormat( doc );   //Serialize DOM
         format.setIndent(3);
         OutputStreamWriter  writer = new OutputStreamWriter(System.out);        
         XDFSerializer    serial = new XDFSerializer( System.out, format );

         try {
            serial.asDOMSerializer();                            // As a DOM Serializer
            serial.serialize( doc.getDocumentElement() );
         } catch (java.io.IOException e) {
            e.printStackTrace();
         }

      }

  }
}


