
// XDF NotesLocationOrder 
// CVS $Id$

// NotesLocationOrder.java Copyright (C) 2000 Brian Thomas,
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
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.OutputStream;

/** A simple object for storing the order in which axis are arranged
    for note objects. 
    @version $Revision$
 */
public class NotesLocationOrder extends BaseObject {

   //
   // Fields
   //

   // axisIdRefORder isnt an XMLAttribute because we have special 
   // handling in the toXDFOutputStream method for it. -b.t.
   ArrayList axisIdRefOrder = new ArrayList();
 
   String indexNodeName = "index";

   //
   // Constructors
   //

   /** No-argument constructor. 
    */
   public NotesLocationOrder() {
      init();
   }

   //
   // Public Methods
   //

   /**getValue: get the *value* (PCDATA) attribute. 
   */
   public ArrayList getAxisOrderList() {
      return axisIdRefOrder;
   }

   /** Set the list of axisIdRefs held by this object.
       List must be a list of String objects holding valid axisIdRefs
   */
   public void setAxisOrderList ( List orderList )
   {
      axisIdRefOrder = (ArrayList) orderList;
   }

   // 
   // Protected Methods
   //

   public void toXDFOutputStream (  OutputStream outputstream, 
                                    Hashtable XMLDeclAttribs,
                                    String indent,
                                    boolean dontCloseNode,
                                    String newNodeNameString,
                                    String noChildObjectNodeName
                                  ) 
   {

      String nodeNameString = this.classXDFNodeName;

      // Setup. Sometimes the name of the node we are opening is different from
      // that specified in the classXDFNodeName (*sigh*)
      if (newNodeNameString != null) nodeNameString = newNodeNameString;

      // 0. To be valid XML, we always start an XML block with an
      //    XML declaration (e.g. somehting like "<?xml standalone="no"?>").
      //    Here we deal with  printing out XML Declaration && its attributes
/*
      if ((XMLDeclAttribs !=null) &&(!XMLDeclAttribs.isEmpty())) {
         indent = "";
         writeXMLDeclToOutputStream(outputstream, XMLDeclAttribs);
      }
*/

      // 1. open this node, print its simple XML attributes
      if (sPrettyXDFOutput) writeOut(outputstream, indent); // indent node if desired
      writeOut(outputstream,"<" + nodeNameString + ">");   // print opening statement
      if (sPrettyXDFOutput) writeOut(outputstream, Constants.NEW_LINE);

      String newindent = indent + sPrettyXDFOutputIndentation; // bump up the indentation
      // 2. Print out the axisIdRefs as child nodes 
      Iterator iter = axisIdRefOrder.iterator();
      while (iter.hasNext()) {
         String axisIdRef = (String) iter.next();

         if (sPrettyXDFOutput) writeOut(outputstream, newindent); // indent node if desired
         writeOut(outputstream,"<" + indexNodeName + " axisIdRef=\""+axisIdRef+"\">");
         if (sPrettyXDFOutput) writeOut(outputstream, Constants.NEW_LINE);
      }

      // 3. Close this node
      if (sPrettyXDFOutput) writeOut(outputstream, indent); // indent node if desired
      writeOut(outputstream,"</" + nodeNameString + ">");   // print opening statement
      if (sPrettyXDFOutput) writeOut(outputstream, Constants.NEW_LINE);

   }

   // 
   // Private Methods
   //

   /** A special private method used by constructor methods to
       conviently build the XML attribute list for a given class.
    */
   private void init()
   {
       classXDFNodeName = "locationOrder";
   };


}

/* Modification History:
 *
 * $Log$
 * Revision 1.1  2000/11/02 19:44:58  thomas
 * Initial Version. -b.t.
 *
 *
 */
