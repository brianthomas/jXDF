
// XDF DocumentType Class
// CVS $Id$

// DocumentType.java Copyright (C) 2001 Brian Thomas,
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;

import java.io.Writer;
import java.io.StringWriter;

 /**
  *  The DocumentType class is nothing more than a simple object that holds information
  *  concerning the DOCTYPE statement and its associated child entities (href, notation)
  *  for a given XDF object.
  *  @version $Revision$
  */

 public class DocumentType extends BaseObject {

   //
   // Fields
   //
   private static final String DOCTYPE_NODE_NAME = new String ("!DOCTYPE");
   private XDF owner;

   /* XML attribute names */
   private List notationList = Collections.synchronizedList(new ArrayList());
   private String publicId = null;
   private String systemId = null;

   //
   // Constructor
   //

   public DocumentType (XDF xdf)
   {
      owner = xdf;
   }

   //
   // Get/Set methods
   //

   /**
    * @return the current name attribute
    */
   public String getName() {
      return Constants.XDF_ROOT_NODE_NAME;
   }

   /** set the *sysId* attribute
    */
   public void setSystemId (String value) {
      systemId = value;
   }

   /**
    * @return the current systemId attribute
    */
   public String getSystemId () {
     return systemId;
   }

   /** set the *pubId* attribute
    */
   public void setPublicId (String value) {
      publicId = value;
   }

   /**
    * @return the current *pubId* attribute
    */
   public String getPublicId() {
      return publicId;
   }

   /**
    * @return a list of entities
    */
   public List getEntities() {
      ArrayList objList = owner.findAllChildHrefObjects();
      return objList;
   }

   /** 
    * @return a List of Notation entities
    */
   public List getNotations() {
      return notationList;
   }

   public XDF getOwner (XDF obj) {
      return owner;
   }

   // 
   // Other public methods
   //

   public void addNotation (NotationNode obj) {
      notationList.add(obj);
   }
 
   public void removeNotation (NotationNode obj) {
      notationList.remove(obj);
   }

   public boolean removeNotation (String name) {
      Iterator iter = notationList.iterator();
      while (iter.hasNext()) { 
         NotationNode obj = (NotationNode) iter.next();
         if (name.equals(obj.getName())) {
             notationList.remove(obj);
             return true;
         }
      }
      return false;
   }

   //
   // Protected methods
   //

   protected String basicXMLWriter (
                                Writer outputWriter,
                                String indent,
                                boolean dontCloseNode,
                                String newNodeNameString,
                                String noChildObjectNodeName
                             )
   throws java.io.IOException
   {

      if (Specification.getInstance().isPrettyXDFOutput())
         outputWriter.write(indent);

      String sysId = getSystemId(); 
      String pubId = getPublicId(); 

      outputWriter.write("<"+DOCTYPE_NODE_NAME+" "+getName());
      if (pubId != null) 
         outputWriter.write(" PUBLIC \"" + getPublicId() +"\"");
      if (sysId != null) 
         outputWriter.write(" SYSTEM \"" + getSystemId() +"\"");
      else 
         outputWriter.write(" SYSTEM \"" + Constants.XDF_DTD_NAME +"\"");

      // any entities and notations need to now be written.

      List entityObjList = getEntities();
      List notationObjList = getNotations();

      String newindent = indent + Specification.getInstance().getPrettyXDFOutputIndentation();

      // if we have any, then we must print out
      if (entityObjList.size() > 0) {

         outputWriter.write(" [");

         if (Specification.getInstance().isPrettyXDFOutput())
            outputWriter.write(Constants.NEW_LINE);

         // whip thru the list of entity objects
         synchronized (entityObjList) {
            Iterator iter = entityObjList.iterator(); // Must be in synchronized block
            while (iter.hasNext()) {
               Entity entityObj = (Entity) iter.next();
               entityObj.toXMLWriter(outputWriter, newindent); 
            }
         }
      }

      if (notationObjList.size() > 0) {

         if (entityObjList.size() == 0) 
         {
            outputWriter.write(" [");
            if (Specification.getInstance().isPrettyXDFOutput())
               outputWriter.write(Constants.NEW_LINE);
         }

         synchronized (notationObjList) {
            Iterator iter = notationObjList.iterator(); // Must be in synchronized block
            while (iter.hasNext()) {
               NotationNode obj = (NotationNode) iter.next();
               obj.toXMLWriter(outputWriter, newindent);
            }
         }

      }

      if (entityObjList.size() > 0 || notationObjList.size() > 0) 
         outputWriter.write("]");

      outputWriter.write(">");

      return DOCTYPE_NODE_NAME;

    } 

 }

 /* Modification History:
 *
 * $Log$
 * Revision 1.1  2001/09/13 21:37:58  thomas
 * *** empty log message ***
 *
 * Revision 1.2  2001/09/06 15:57:42  thomas
 * changed basicXMLWriter to return String (nodeName); made nodeName private,static field in class
 *
 * Revision 1.1  2001/09/05 21:57:41  thomas
 * Initial Version
 *
 *
 */


