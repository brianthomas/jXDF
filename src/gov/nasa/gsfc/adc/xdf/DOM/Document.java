

// XDF DOM Document
// CVS $Id$

// Document.java Copyright (C) 2001 Brian Thomas,
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

import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import org.apache.crimson.tree.XmlDocument;

/** 
 */

// Based on Crimson parser XmlDocument class.
public class Document extends XmlDocument implements Cloneable {

   // 
   // Fields
   //

   //
   // Constructors
   //

   // no-op
/*
   public Document () 
   {
      super();
      // init(); // doesnt exist, just allow using super-class constructor
   }
*/

   public static Document createDocument (InputStream in, boolean doValidate) 
   throws IOException, SAXException
   {
      return createDocument (new InputSource (in), doValidate);
   }

   public static Document createDocument (String documentURI)
   throws IOException, SAXException
   {
        return createDocument (new InputSource (documentURI), false);
   }

   public static Document createDocument ( String  documentURI, boolean doValidate) 
   throws IOException, SAXException
   {
      return createDocument (new InputSource (documentURI), doValidate);
   }

   public static Document createDocument(InputSource in, boolean doValidate)
        throws IOException, SAXException
   {
      return (Document) createXmlDocument (in, doValidate);
   }

   //
   // Get/Set Methods 
   //

   //
   // Other Public Methods
   //

   /** produces a deep clone of this document
    */
   public Object clone() 
   throws CloneNotSupportedException
   {
      return (Object) super.cloneNode(true);
   } 

   //
   // Protected Methods
   //

   //
   // Private Methods
   //

/*
   private void writeOut ( OutputStream outputstream, String msg )
   throws java.io.IOException
   {
       outputstream.write(msg.getBytes());
   }
*/

}

/* Modification History:
 *
 * $Log$
 * Revision 1.1  2001/07/31 21:08:19  thomas
 * Initial version. Relies on altered crimson parser DOM,
 * so we will be changing quite soon to something better.
 *
 *
 */
