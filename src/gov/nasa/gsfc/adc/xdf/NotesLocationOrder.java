

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
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.IOException; 

/** A simple object for storing the order in which axis are arranged
    for note objects. This should probably be a private internal class of Notes. 
    @version $Revision$
 */
public class NotesLocationOrder extends BaseObject {

   //
   // Fields
   //

   // axisIdRefORder isnt an Attribute because we have special 
   // handling in the toXMLOutputStream method for it. -b.t.
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

   protected String basicXMLWriter (
                                Writer outputWriter,
                                String indent,
                                boolean dontCloseNode,
                                String newNodeNameString,
                                String noChildObjectNodeName
                             )

   throws java.io.IOException
   {

      // if the axis list is empty, no need to print this
      if (axisIdRefOrder.size() == 0) {
         return (String) null; 
      }

      String nodeNameString = this.classXDFNodeName;

      // Setup. Sometimes the name of the node we are opening is different from
      // that specified in the classXDFNodeName (*sigh*)
      if (newNodeNameString != null) nodeNameString = newNodeNameString;

      // 1. open this node, print its simple XML attributes
      if (Specification.getInstance().isPrettyXDFOutput()) outputWriter.write(indent); // indent node if desired
      outputWriter.write("<" + nodeNameString + ">");   // print opening statement
      if (Specification.getInstance().isPrettyXDFOutput()) outputWriter.write(Constants.NEW_LINE);

      String newindent = indent + Specification.getInstance().getPrettyXDFOutputIndentation(); // bump up the indentation
      // 2. Print out the axisIdRefs as child nodes 
      Iterator iter = axisIdRefOrder.iterator();
      while (iter.hasNext()) {
         String axisIdRef = (String) iter.next();

         if (Specification.getInstance().isPrettyXDFOutput()) outputWriter.write(newindent); // indent node if desired
         outputWriter.write("<" + indexNodeName + " axisIdRef=\"");
         writeOutAttribute(outputWriter, axisIdRef);
         outputWriter.write( "\"/>");

         if (Specification.getInstance().isPrettyXDFOutput()) outputWriter.write(Constants.NEW_LINE);
      }

      // 3. Close this node
      if (Specification.getInstance().isPrettyXDFOutput()) outputWriter.write(indent); // indent node if desired
      outputWriter.write("</" + nodeNameString + ">");   // print opening statement

//      if (Specification.getInstance().isPrettyXDFOutput()) outputWriter.write( Constants.NEW_LINE);

      return nodeNameString;
   }

   // 
   // Protected Methods
   //

   /** A special method used by constructor methods to
       conviently build the XML attribute list for a given class.
    */
   protected void init()
   {

       resetAttributes();

       classXDFNodeName = "locationOrder";

   };


}

