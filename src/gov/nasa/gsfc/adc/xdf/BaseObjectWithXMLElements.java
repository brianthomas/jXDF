

// XDF BaseObjectWithXMLElements Class
// CVS $Id$


// BaseObjectWithXMLElements.java Copyright (C) 2000 Brian Thomas,
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

// import java.util.Hashtable;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Hashtable;
import java.io.OutputStream;

/**
     The base object class for XDF objects which may hold internal XML elements.
 */
public abstract class BaseObjectWithXMLElements extends BaseObject implements Cloneable {

   //
   // Fields
   //

   /* XML attribute names */
//   protected static final String XML_ELEMENTLIST_XML_ATTRIBUTE_NAME = new String("xmlElementList");
   protected List xmlElementList = (List) new ArrayList();

   /**  This constructor takes a Java Hashtable as an initializer of
        the XML attributes of the object to be constructed. The
        Hashtable key/value pairs coorespond to the class XDF attribute
        names and their desired values.
    */
   public BaseObjectWithXMLElements ( Hashtable InitXDFAttributeTable )
   {

      // init the XML attributes (to defaults)
      init();

      // init the value of selected XML attributes to HashTable values
      hashtableInitXDFAttributes(InitXDFAttributeTable);
   }

   // no-arg constructor
   public BaseObjectWithXMLElements() {
      init();
   }

   //
   // Get/Set Methods
   //

   /** get the list of XML Elements held within this object.
      @return list of XMLElement objects.
    */
   public List getXMLElementList() {
      // return (List) ((XMLAttribute) attribHash.get(XML_ELEMENTLIST_XML_ATTRIBUTE_NAME)).getAttribValue();
      return xmlElementList; 
   }

   /**set the *location* attribute.
   */
   public void setXMLElementList(List elements)
   {
      // ((XMLAttribute) attribHash.get(XML_ELEMENTLIST_XML_ATTRIBUTE_NAME)).setAttribValue(elements);
      xmlElementList = elements;
   }

   //
   // Other Public Methods
   //

   /** append an XMLElement into the list of internal elements held by this object.
    */
   public void addXMLElement (XMLElement element) {
      getXMLElementList().add(element);
   }

   /** Indicate the datacell that this note applies to within an array.
    */
   public void removeXMLElement (XMLElement element) {
      getXMLElementList().remove(element);
   }

   /** Write this object and all the objects it owns to the supplied
       OutputStream object as XDF. This method overrides the BaseObject
       version, allowing the XMLElement children to be written out, should
       they exist in the object.
    */
   public void toXMLOutputStream (
                                   OutputStream outputstream,
                                   Hashtable XMLDeclAttribs,
                                   String indent,
                                   boolean dontCloseNode,
                                   String newNodeNameString,
                                   String noChildObjectNodeName
                                 )
   {
      // while writing out, attribHash should not be changed
      synchronized (attribHash) {

         String nodeNameString = this.classXDFNodeName;
         // Setup. Sometimes the name of the node we are opening is different from
         // that specified in the classXDFNodeName (*sigh*)
         if (newNodeNameString != null) nodeNameString = newNodeNameString;
   
/*
         // 0. To be valid XML, we always start an XML block with an
         //    XML declaration (e.g. somehting like "<?xml standalone="no"?>").
         //    Here we deal with  printing out XML Declaration && its attributes
         if ((XMLDeclAttribs !=null) &&(!XMLDeclAttribs.isEmpty())) {
           indent = "";
           writeXMLDeclToOutputStream(outputstream, XMLDeclAttribs);
         }
*/
   
         // 1. open this node, print its simple XML attributes
         if (nodeNameString != null) {
   
           if (Specification.getInstance().isPrettyXDFOutput())
             writeOut(outputstream, indent); // indent node if desired
           // For printing the opening statement we need to invoke a little
           // Voodoo to keep the DTD happy: the first structure node is always
           // called by the root node name instead of the usual nodeNameString
           // We can tell this by checking if this object is derived from class
           // Structure and if XMLDeclAttrib defined/populated with information
   
           // NOTE: This isnt really the way to do this. We need to check if 'this' is
           // is or has as a superclass xdf.Structure instead of the 'string check' below.
   
           // check is class Strucuture & XMLDeclAttribs populated?
   /*
           if ( nodeNameString.equals(Specification.getInstance().getXDFStructureNodeName()) 
                && !XMLDeclAttribs.isEmpty() )
             nodeNameString = Specification.getInstance().getXDFRootNodeName();
   */
   
           writeOut(outputstream,"<" + nodeNameString);   // print opening statement
   
         }
   
         // gather info about XMLAttributes in this object/node
         Hashtable xmlInfo = getXMLInfo();
   
         // 2. Print out string object XML attributes EXCEPT for the one that
         //    matches PCDATAAttribute.
         ArrayList attribs = (ArrayList) xmlInfo.get("attribList");
         // is synchronized here correct?
         synchronized(attribs) {
           int size = attribs.size();
           for (int i = 0; i < size; i++) {
             Hashtable item = (Hashtable) attribs.get(i);
             writeOut(outputstream, " " + item.get("name") + "=\"");
             // this slows things down, should we use?
             //writeOutAttribute(outputstream, (String) item.get("value"));
             writeOut(outputstream, (String) item.get("value"));
             writeOut(outputstream, "\"" );
           }
         }
   
         // 3. Print out Node PCData or Child Nodes as specified by object ref
         //    XML attributes. The way this stuff occurs will also affect how we
         //    close the node.
         ArrayList childObjs = (ArrayList) xmlInfo.get("childObjList");
         List childXMLElements = getXMLElementList();
         String pcdata = (String) xmlInfo.get("PCDATA");
   
        if ( childObjs.size() > 0 || 
             childXMLElements.size() > 0 || 
             pcdata != null || 
             noChildObjectNodeName != null)
         {
           // close the opening tag
           if (nodeNameString != null) {
             writeOut(outputstream, ">");
             if ((Specification.getInstance().isPrettyXDFOutput()) && (pcdata == null))
                writeOut(outputstream, Constants.NEW_LINE);
           }

           // by definition these are printed first 
           int size = childXMLElements.size();
           String childindent = indent + Specification.getInstance().getPrettyXDFOutputIndentation();
           for (int i = 0; i < size; i++) {
              ((XMLElement) childXMLElements.get(i)).toXMLOutputStream(outputstream, childindent);
           }
   
           // deal with object/list XML attributes, if any in our list
           size = childObjs.size();
           for (int i = 0; i < size; i++) {
             Hashtable item = (Hashtable) childObjs.get(i);
   
             if (item.get("type") == Constants.LIST_TYPE)
             {
   
               List objectList = (List) item.get("value");
               // Im not sure this synchronized wrapper is needed, we are
               // only accessing stuff here.. Also, should synchronzied wrapper
               // occur back in the getXMLInfo method instead where the orig
               // access occured?!?
               synchronized(objectList) {
                 Iterator iter = objectList.iterator(); // Must be in synchronized block
                 while (iter.hasNext()) {
                   BaseObject containedObj = (BaseObject) iter.next();
                   if (containedObj != null) { // can happen from pre-allocation of axis values, etc (?)
                     indent = dealWithOpeningGroupNodes(containedObj, outputstream, indent);
                     indent = dealWithClosingGroupNodes(containedObj, outputstream, indent);
                     String newindent = indent + Specification.getInstance().getPrettyXDFOutputIndentation();
                     containedObj.toXMLOutputStream(outputstream, new Hashtable(), newindent);
                   }
                 }
               }
             }
             else if (item.get("type") == Constants.OBJECT_TYPE)
             {
               BaseObject containedObj = (BaseObject) item.get("value");
               if (containedObj != null) { // can happen from pre-allocation of axis values, etc (?)
                 // shouldnt this be synchronized too??
                 synchronized(containedObj) {
                   indent = dealWithOpeningGroupNodes(containedObj, outputstream, indent);
                   indent = dealWithClosingGroupNodes(containedObj, outputstream, indent);
                   String newindent = indent + Specification.getInstance().getPrettyXDFOutputIndentation();
                   containedObj.toXMLOutputStream(outputstream, new Hashtable(), newindent);
                 }
               }
             } else {
               // error: weird type, actually shouldnt occur. Is this needed??
               Log.errorln("Weird error: unknown XML attribute type for item:"+item);
             }
   
           }
   
   
           // print out PCDATA, if any
           if(pcdata != null)  {
             writeOut(outputstream, pcdata);
           };
   
           // if there are no PCDATA or child objects/nodes then
           // we print out noChildObjectNodeName and close the node
           if ( childObjs.size() == 0 && pcdata == null && noChildObjectNodeName != null)
           {
   
             if (Specification.getInstance().isPrettyXDFOutput())
               writeOut(outputstream, indent + Specification.getInstance().getPrettyXDFOutputIndentation());
   
             writeOut(outputstream, "<" + noChildObjectNodeName + "/>");
   
             if (Specification.getInstance().isPrettyXDFOutput())
               writeOut(outputstream, Constants.NEW_LINE);
   
           }
   
          // ok, now deal with closing the node
           if (nodeNameString != null) {
   
              indent = dealWithClosingGroupNodes((BaseObject) this, outputstream, indent);
   
             if (Specification.getInstance().isPrettyXDFOutput() && pcdata == null)
                   writeOut(outputstream, indent);
   
             if (!dontCloseNode)
                 writeOut(outputstream, "</"+nodeNameString+">");
   
           }
   
         } else {

           if (nodeNameString != null) {
	       if (dontCloseNode) {
		   // it may not have sub-objects, but we dont want to close it
		   // (happens for group objects)
		   writeOut(outputstream, ">");
	       } else {
		   // no sub-objects, just close this node
		   writeOut(outputstream, "/>");
	       }
	   }
   
         }
   
         if (Specification.getInstance().isPrettyXDFOutput() && nodeNameString != null ) 
	     writeOut(outputstream, Constants.NEW_LINE);

      } //end synchronize

   }


   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   //
   // Protected Methods
   //

   /** a special method used by constructor methods to
       conviently build the XML attribute list for a given class.
    */
   protected void init()
   {

        resetXMLAttributes();

        // NO! not XML attributes.. should be protected field
        // attribOrder.add(0, XML_ELEMENTLIST_XML_ATTRIBUTE_NAME);
        // attribHash.put(XML_ELEMENTLIST_XML_ATTRIBUTE_NAME, new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));

   }

}

