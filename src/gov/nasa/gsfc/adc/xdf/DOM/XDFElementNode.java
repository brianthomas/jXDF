

// XDF XMLElementNode 
// CVS $Id$

// XMLElementNode.java Copyright (C) 2001 Brian Thomas,
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

/*
import java.util.ArrayList;
*/
import java.util.Hashtable;
import java.io.Writer;
import java.io.IOException;
import java.io.Writer;

import org.apache.crimson.tree.ElementNode;
import org.apache.crimson.tree.XmlWriteContext;

import gov.nasa.gsfc.adc.xdf.XDF;
import gov.nasa.gsfc.adc.xdf.Log;
import gov.nasa.gsfc.adc.xdf.Constants;
import gov.nasa.gsfc.adc.xdf.Specification;
/*
import org.apache.crimson.tree.TextNode;
import org.apache.crimson.tree.CDataNode;
*/
import org.xml.sax.Attributes;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
/*
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
*/


/** 
 */

public class XDFElementNode extends ElementNode implements Cloneable {

   // 
   // Fields
   //
   private XDF myXDFObject;

   //
   // Constructors
   //

   public XDFElementNode (XDF xdfObject) 
   {
      super("XDF");
      // need to do more here
      myXDFObject = new XDF ();
   }

   //
   // Get/Set Methods 
   //

   public XDF getXDFObject() { 
      return myXDFObject;
   }

   public void setXDFObject (XDF object) { 
       myXDFObject = object;
   }

   // must be an XDF object 
   public void setUserObject (Object xdfObject) {
      setXDFObject((XDF) xdfObject);
   }

   public Object getUserObject () {
      return getXDFObject();
   }
   
   //
   // Other Public Methods
   //

   /** just a convience method for setAttribute
    */
   public void setXMLAttribute (String name, String value) { 
      this.setAttribute(name, value);
   }

   /** just a convience method for getAttribute
    */
   public void addXMLAttribute (String name, String value) { 
      this.setAttribute(name, value);
   }

   /** Clears all (XML) attributes in this object.
    */
   public void resetXMLAttributes() {

       if (this.hasAttributes()) {

          NamedNodeMap attribs = this.getAttributes();

          int size = attribs.getLength();
          for (int i = 0; i < size; i++) {
             Attr attrib = (Attr) attribs.item(i);
             attribs.removeNamedItem(attrib.getName());
          }
       }

   }

    // this is the method we need to overload here
    public void writeXml (XmlWriteContext context) 
    throws java.io.IOException
    {
        Writer  out = context.getWriter ();
        myXDFObject.toXMLWriter(out);
    }

    public String toXMLString () {
       String retString = myXDFObject.toXMLString();
       return retString;
    }

   /**
    */
   public void toXMLWriter (
                                   Writer outputWriter,
                                   String indent,
                                   boolean dontCloseNode,
                                   String newNodeNameString,
                                   String noChildObjectNodeName
                                )
   throws java.io.IOException
   {

       toXMLWriter (outputWriter, null, indent, dontCloseNode, newNodeNameString, noChildObjectNodeName);

   }

   public void toXMLWriter (
                                Writer outputWriter,
                                Hashtable XMLDeclAttribs,
                                String indent,
                                boolean dontCloseNode,
                                String newNodeNameString,
                                String noChildObjectNodeName
                             )
   throws java.io.IOException
   {

System.err.println("XDFElementNode calls XDF.toXMLWriter()");
       myXDFObject.toXMLWriter( outputWriter, XMLDeclAttribs, indent, dontCloseNode, 
                                newNodeNameString, noChildObjectNodeName);
System.err.println("XDFElementNode finishs call to XDF.toXMLWriter()");

   }

   /**
    */
   public void toXMLWriter (
                              Writer outputWriter,
                              String indent
                           )
   throws java.io.IOException
   {
      this.toXMLWriter(outputWriter, indent, false, null, null);
   }

   /**
    */
   public void toXMLWriter ( Writer outputWriter)
   throws java.io.IOException
   {
      this.toXMLWriter(outputWriter, "", false, null, null);
   }

   /**
    */
   public Object clone() throws CloneNotSupportedException {
     XDFElementNode cloneObj = (XDFElementNode) super.cloneNode(true); // use deep clone 
     cloneObj.myXDFObject = (XDF) myXDFObject.clone();
     return (Object) cloneObj;
   }

   //
   // Protected Methods
   //

   //
   // Private Methods
   //

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
