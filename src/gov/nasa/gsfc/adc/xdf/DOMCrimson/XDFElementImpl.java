

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

package gov.nasa.gsfc.adc.xdf.DOMCrimson;

import gov.nasa.gsfc.adc.xdf.Constants;
import gov.nasa.gsfc.adc.xdf.Reader;
import gov.nasa.gsfc.adc.xdf.XDF;
import gov.nasa.gsfc.adc.xdf.Log;

import org.apache.crimson.tree.ElementNode2;
import org.apache.crimson.tree.ElementEx;
import org.apache.crimson.tree.XmlWriteContext;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** 
     An implementation of XDFElement based on the Crimson ElementNode2 class.
     This is an *alpha* level implementation. You should do all your manipulations
     on the XDF object contained within this node, not by using the Crimson ElementNode2
     inherited methods (Dangerous!) which are not currently synced up with the information
     held in the child XDF object. 
 */

public class XDFElementImpl extends ElementNode2
implements Element
{

   // 
   // Fields
   //
   private XDF myXDFObject = null;

   //
   // Constructors
   //

   public XDFElementImpl (String namespaceURI, Element baseElement ) 
   throws java.io.IOException
   {
      super (namespaceURI, Constants.XDF_ROOT_NODE_NAME);
      if (baseElement instanceof ElementNode2) {
         parseBaseElementIntoXDFObject((ElementNode2) baseElement);
      } else {
         Log.errorln("Creating empty XDFElementImpl node: passed element not an instanceof crimson.tree.ElementNode2");
      } 
   }

   public XDFElementImpl (Element baseElement ) 
   throws java.io.IOException
   {
      super (baseElement.getNamespaceURI(), Constants.XDF_ROOT_NODE_NAME);
      if (baseElement instanceof ElementNode2) {
        parseBaseElementIntoXDFObject((ElementNode2) baseElement);
      } else {
        Log.errorln("Creating empty XDFElementImpl node: passed element not an instanceof crimson.tree.ElementNode2");
      }
   }

   //
   // Get/Set Methods 
   //

   public XDF getXDFObject () 
   {
      return ((XDF) getUserObject());
   }

   public void setXDFObject (XDF object) 
   {
      setUserObject(object);
   }

   public void setUserObject (Object object) 
   {

       if (object instanceof XDF) {

         // these things are handled by the Document, not in the XDF object
         ((XDF) object).setXMLDeclaration (null);
         ((XDF) object).setDocumentType(null);
         super.setUserObject((XDF) object);

      } else {
         Log.errorln("Only XDF objects may be set for XDFObjectImpl"); 
      }

   }

   //
   // Other Public Methods
   //

   public String toXMLString () {
      if (myXDFObject != null) 
      {
         return myXDFObject.toXMLString();
      } else {
         String rootNodeName = "<" + Constants.XDF_ROOT_NODE_NAME +"/>";
         return rootNodeName; 
      }
   }

   /** From Crimson package. Use toXML* methods is preferred.
   */
   public void writeXml (XmlWriteContext context)
   throws java.io.IOException
   {
      toXMLWriter(context.getWriter());
   }

   /** From Crimson package. Use toXML* methods is preferred, especially since
       the XDFElementImpl class does not implement writeChildrenXml() method.
    */
   public void writeChildrenXml(XmlWriteContext context)
   throws java.io.IOException
   {

//      super.writeChildrenXml(context); 
      Log.warnln("XDFElementImpl does not implement writeChildrenXml() method.");

   }

   /** 
   */
   public void toXMLWriter (Writer outputWriter)
   throws java.io.IOException
   {

      XDF myXDFObject = getXDFObject();

      if (myXDFObject != null) {
         myXDFObject.toXMLWriter(outputWriter);
      } else {
         // can this happen? hum.. the object is missing/empty, we shouldnt 
         // print anything I think...
         outputWriter.write(toXMLString());
         Log.warnln("XDFElementImpl Node missing child XDF object, writing empty node.");
      }
   }

  /** 
   */
   public void toXMLOutputStream (OutputStream outputstream)
   throws java.io.IOException
   {

      XDF myXDFObject = getXDFObject();

      if (myXDFObject != null) {
         myXDFObject.toXMLOutputStream(outputstream);
      } else {

         Log.warnln("XDFElementImpl Node missing child XDF object, writing empty node.");
         // can this happen? hum.. the object is missing/empty, we shouldnt 
         // print anything I think...
         // super.writeXml(context); 
         Writer outputWriter = new BufferedWriter(new OutputStreamWriter(outputstream));
         outputWriter.write(toXMLString());
         outputWriter.close();

      }
   }


   // 
   // Private Methods
   //

   private void parseBaseElementIntoXDFObject (ElementNode2 baseElementNode)
   throws java.io.IOException
   {

      // use ElementNode to initialize XDF object held by this node
      Reader reader = new Reader ();
      String xmlContent = getXMLStringElemRepresentation(baseElementNode);
      setXDFObject(reader.parseString(xmlContent));

   }

   private String getXMLStringElemRepresentation (ElementNode2 elem) {

      StringWriter writer = new StringWriter(); // Writer will be a String
      XmlWriteContext context = ((XDFDocumentImpl) elem.getOwnerDocument()).createWriteContext(writer,0);

      try {
        elem.writeXml(context);
      } catch (IOException e) {
        Log.errorln("Cant convert XDFElement, string empty");
        Log.printStackTrace(e);
      }      

      return writer.toString();
 
   }

}

