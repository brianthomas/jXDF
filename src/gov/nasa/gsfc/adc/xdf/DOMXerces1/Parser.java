
// XDF Parser (for Xerces1) 
// CVS $Id$

// Parser.java Copyright (C) 2001 Brian Thomas,
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

import gov.nasa.gsfc.adc.xdf.Log;
import gov.nasa.gsfc.adc.xdf.Constants;

// import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xml.serialize.OutputFormat;

import java.io.IOException;

import org.apache.xerces.parsers.DOMParser;

import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.InputSource; 
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** 
    Read any XML Document into a specialized DOM document -- xdf.DOM.Document.
    It is preferable to use the jaxp interface instead of calling this parser
    directly.

 */

// Based on Xerces DOMParser class.
public class Parser extends DOMParser 
{

   // 
   // Fields
   //
   private String documentClass = "gov.nasa.gsfc.adc.xdf.DOMXerces1.XDFDocumentImpl";

   // default params for parsing 
   private static String ValidationFeature    = "http://xml.org/sax/features/validation";
   private static String NameSpacesFeature    = "http://xml.org/sax/features/namespaces";
   private static String SchemaSupportFeature = "http://apache.org/xml/features/validation/schema";
   private static String SchemaFullSupportFeature = "http://apache.org/xml/features/validation/schema-full-checking";
   private static String DeferredDOMFeature   = "http://apache.org/xml/features/dom/defer-node-expansion";

   //
   // Constructors
   //

   // no-op
   public Parser() 
   {
      super();

      // set up default configuration
      useValidation(false);
      useNameSpaces(true);
      useSchemaSupport(true);
      useFullSchemaSupport(false);
      useDeferredDOM(true);

      try {
         this.setDocumentClassName(documentClass);
      } catch (SAXNotRecognizedException e) {
         Log.errorln("Cant instanciate parser, missing class "+documentClass);
         Log.printStackTrace(e); 
      } catch (SAXNotSupportedException ex) {
         Log.errorln("Cant instanciate parser, missing class "+documentClass);
         Log.printStackTrace(ex); 
      }
   }

   //
   // Get/Set Methods 
   //

   /** Set whether or not to use the DeferredDOM feature. 
    */
   public void useDeferredDOM (boolean useDeferredDOM) 
   {
      try {
         setFeature( DeferredDOMFeature , useDeferredDOM );
      } catch (SAXNotRecognizedException e) { 
         Log.warnln("Feature:DeferredDOM is not recognized, ignoring useDeferredDOM() request");
      } catch (SAXNotSupportedException e) { 
         Log.printStackTrace(e);
      }
   }

   /** Set whether or not to use the NameSpaces feature. 
    */
   public void useNameSpaces (boolean useNameSpaces) 
   {
      try {
         setFeature( NameSpacesFeature, useNameSpaces );
      } catch (SAXNotRecognizedException e) { 
         Log.warnln("Feature:NameSpacesFeature is not recognized, ignoring useNameSpaces() request");
      } catch (SAXNotSupportedException e) { 
         Log.printStackTrace(e);
      }
   }

   /** Set whether or not to use the (partial) schema support feature.
    */
   public void useSchemaSupport (boolean useSchema ) 
   {
      try {
         setFeature( SchemaSupportFeature, useSchema);
      } catch (SAXNotRecognizedException e) { 
         Log.warnln("Feature:SchemaSupport is not recognized, ignoring useSchemaSupport() request");
      } catch (SAXNotSupportedException e) { 
         Log.printStackTrace(e);
      }
   }

   /** Set whether or not to use the full schema support feature.
    */
   public void useFullSchemaSupport (boolean useFullSchemaSupport ) 
   {
      try {
         setFeature( SchemaFullSupportFeature, useFullSchemaSupport );
      } catch (SAXNotRecognizedException e) { 
         Log.warnln("Feature:SchemaFullSupport is not recognized, ignoring useFullSchemaSupport() request");
      } catch (SAXNotSupportedException e) { 
         Log.printStackTrace(e);
      }
   }

   /** Set whether or not to use validation.
    */
   public void useValidation (boolean useValidation) 
   {
      try {
         setFeature( ValidationFeature, useValidation);
      } catch (SAXNotRecognizedException e) { 
         Log.warnln("Feature:Validation is not recognized, ignoring useValidation() request");
      } catch (SAXNotSupportedException e) { 
         Log.printStackTrace(e);
      }
   }

   //
   // Other Public Methods
   //

   public void parse(InputSource source)
   throws SAXException, IOException
   {
      super.parse(source);
      convertDocument();
   }

   public void parse (String systemId)
   throws SAXException,IOException
   {
       // needs to be done this way, otherwise, we get a double parse.
     /*
       super.parse(systemId);
       convertDocument();
      */
       parse(new InputSource(systemId));
   }

   //
   // Protected Methods
   //

   //
   // Private Methods
   //

   // converting XDF ElementImpl nodes to XDFElementImpl nodes
   private void convertDocument ()
   {

       // XDFDocumentImpl doc = (XDFDocumentImpl) this.getDocument();
       Document doc = (Document) this.getDocument();

       Element rootElement = (Element) doc.getDocumentElement();
       Element newRoot = convertElement(rootElement, doc);

       ((XDFDocumentImpl) doc).setDocumentElement(newRoot);

   }

   private static Element convertElement(Element elem, Document doc) {

      if (elem.getNodeName().equals(Constants.XDF_ROOT_NODE_NAME)) {

         try {
            XDFElementImpl newXdfElement = new XDFElementImpl(doc, elem);
            elem.getParentNode().replaceChild((Element) newXdfElement, elem);
            elem = (Element) newXdfElement;
         } catch (IOException e) {
            Log.errorln("IO Error encountered: cannot create XDFElement node. Ignoring request.");
         }

      } else {

         NodeList nodeList = elem.getChildNodes();
         for ( int i = 0, size = nodeList.getLength(); i < size; i++ ) {
            Node item = nodeList.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
               convertElement ( (Element) item, doc );
            }
         }


      }

      return elem;
   }

}

