

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
    for note objects. 
    @version $Revision$
 */
public class NotesLocationOrder extends BaseObject {

   //
   // Fields
   //

   // axisIdRefORder isnt an XMLAttribute because we have special 
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

       resetXMLAttributes();

       classXDFNodeName = "locationOrder";

   };


}

/* Modification History:
 *
 * $Log$
 * Revision 1.12  2001/09/06 15:56:41  thomas
 * changed basicXMLWriter to return String (nodeName)
 *
 * Revision 1.11  2001/09/05 22:00:58  thomas
 * removed toXMLoutputstream, toXMLWriter. Made it basicXMLWriter
 *
 * Revision 1.10  2001/07/26 15:55:42  thomas
 * added flush()/close() statement to outputWriter object as
 * needed to get toXMLOutputStream to work properly.
 *
 * Revision 1.9  2001/07/19 21:58:31  thomas
 * yanked XMLDeclAttribs from toXMLOutputStream (only needed
 * in the XDF class)
 *
 * Revision 1.8  2001/07/06 19:04:23  thomas
 * toXMLOutputStream and related methods now pass on IOExceptions
 * to the application writer (e.g. they throw the error).
 *
 * Revision 1.7  2001/05/10 21:19:44  thomas
 * added resetXMLAttributes to init().
 *
 * Revision 1.6  2001/02/07 18:44:03  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.5  2000/11/27 22:39:25  thomas
 * Fix to allow attribute text to have newline, carriage
 * returns in them (print out as entities: &#010; and
 * &#013;) This allows files printed out to be read back
 * in again(yeah!). -b.t.
 *
 * Revision 1.4  2000/11/16 20:03:22  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.3  2000/11/09 05:16:07  thomas
 * Index node wasnt being closed properly! Fixed. -b.t.
 *
 * Revision 1.2  2000/11/08 19:18:07  thomas
 * Changed the name of toXDF* methods to toXML* to
 * better reflect the nature of the output (its not XDF
 * unless you call th emethod from strcuture object;
 * otherwise, it wont validate as XDF; it is still XML
 * however). -b.t.
 *
 * Revision 1.1  2000/11/02 19:44:58  thomas
 * Initial Version. -b.t.
 *
 *
 */
