

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
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.IOException;

/**
     The base object class for XDF objects which may hold internal XML elements.
 */
public abstract class BaseObjectWithXMLElements extends BaseObject 
{

   //
   // Fields
   //

   /* XML attribute names */
//   protected static final String XML_ELEMENTLIST_XML_ATTRIBUTE_NAME = new String("xmlElementList");
   protected List xmlElementList = Collections.synchronizedList(new ArrayList());

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
      @return list of ElementNode objects.
    */
   public List getElementNodeList() {
      // return (List) ((Attribute) attribHash.get(XML_ELEMENTLIST_XML_ATTRIBUTE_NAME)).getAttribValue();
      return xmlElementList; 
   }

   /** Set the *location* attribute.
       @deprecated You should use add/remove methods instead.
   */
   public void setElementNodeList (List elements)
   {
      // ((Attribute) attribHash.get(XML_ELEMENTLIST_XML_ATTRIBUTE_NAME)).setAttribValue(elements);
      xmlElementList = elements;
   }

   //
   // Other Public Methods
   //

   /** append an ElementNode into the list of internal elements held by this object.
     * @return true on success, false on failure.
    */
   public boolean addElementNode (ElementNode element) {
      return getElementNodeList().add(element);
   }

   /** Indicate the datacell that this note applies to within an array.
    */
   public boolean removeElementNode (ElementNode element) {
      return getElementNodeList().remove(element);
   }

   public Object clone() throws CloneNotSupportedException {

      BaseObjectWithXMLElements cloneObj = (BaseObjectWithXMLElements) super.clone();

      cloneObj.xmlElementList = Collections.synchronizedList(new ArrayList());
      int stop = this.xmlElementList.size();
      for (int i = 0; i < stop; i++) {
          cloneObj.xmlElementList.add( ((ElementNode) this.xmlElementList.get(i)).clone());
      }
      return cloneObj;
   }

   //
   // Protected Methods
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

      // while writing out, attribHash should not be changed
      synchronized (attribHash) {

         String nodeNameString = this.classXDFNodeName;
         // Setup. Sometimes the name of the node we are opening is different from
         // that specified in the classXDFNodeName (*sigh*)
         if (newNodeNameString != null) nodeNameString = newNodeNameString;
   
         // 1. open this node, print its simple XML attributes
         if (nodeNameString != null) {
   
           if (Specification.getInstance().isPrettyXDFOutput())
             outputWriter.write(indent); // indent node if desired
           outputWriter.write("<" + nodeNameString);   // print opening statement
   
         }
   
         // gather info about Attributes in this object/node
         Hashtable xmlInfo = getXMLInfo();
   
         // 2. Print out string object XML attributes EXCEPT for the one that
         //    matches PCDATAAttribute.
         ArrayList attribs = (ArrayList) xmlInfo.get("attribList");
         // is synchronized here correct?
         synchronized(attribs) {
           int size = attribs.size();
           for (int i = 0; i < size; i++) {
             Hashtable item = (Hashtable) attribs.get(i);
             outputWriter.write(" " + item.get("name") + "=\"");
             // this slows things down, should we use?
             writeOutAttribute(outputWriter, (String) item.get("value"));
             // outputWriter.write((String) item.get("value"));
             outputWriter.write("\"" );
           }
         }
   
         // 3. Print out Node PCData or Child Nodes as specified by object ref
         //    XML attributes. The way this stuff occurs will also affect how we
         //    close the node.
         ArrayList childObjs = (ArrayList) xmlInfo.get("childObjList");
         List childXMLElements = getElementNodeList();
         String pcdata = (String) xmlInfo.get("PCDATA");
   
        if ( childObjs.size() > 0 || 
             childXMLElements.size() > 0 || 
             pcdata != null || 
             noChildObjectNodeName != null)
         {
           // close the opening tag
           if (nodeNameString != null) {
             outputWriter.write(">");
             if ((Specification.getInstance().isPrettyXDFOutput()) && (pcdata == null))
                outputWriter.write( Constants.NEW_LINE);
           }

           // by definition these are printed first 
           int size = childXMLElements.size();
           String childindent = indent + Specification.getInstance().getPrettyXDFOutputIndentation();
           for (int i = 0; i < size; i++) {
              ((ElementNode) childXMLElements.get(i)).toXMLWriter(outputWriter, childindent);
           }
   
           // deal with object/list XML attributes, if any in our list
           size = childObjs.size();
           for (int i = 0; i < size; i++) {
             Hashtable item = (Hashtable) childObjs.get(i);
   
             if (item.get("type") == Constants.LIST_TYPE)
             {
   
                List objectList = (List) item.get("value");
                indent = objectListToXMLWriter(outputWriter, objectList, indent);

             }
             else if (item.get("type") == Constants.OBJECT_TYPE)
             {

               BaseObject containedObj = (BaseObject) item.get("value");
               if (containedObj != null) { // can happen from pre-allocation of axis values, etc (?)
                 // shouldnt this be synchronized too??
                 synchronized(containedObj) {
                   indent = dealWithClosingGroupNodes(containedObj, outputWriter, indent);
                   indent = dealWithOpeningGroupNodes(containedObj, outputWriter, indent);
                   String newindent = indent + Specification.getInstance().getPrettyXDFOutputIndentation();
                   containedObj.toXMLWriter(outputWriter, newindent);
                 }
               }
             } else {
               // error: weird type, actually shouldnt occur. Is this needed??
               Log.errorln("Weird error: unknown XML attribute type for item:"+item);
             }
   
           }
   
   
           // print out PCDATA, if any
           if(pcdata != null)  {
             outputWriter.write(entifyString(pcdata));
           };
   
           // if there are no PCDATA or child objects/nodes then
           // we print out noChildObjectNodeName and close the node
           if ( childObjs.size() == 0 && pcdata == null && noChildObjectNodeName != null)
           {
   
             if (Specification.getInstance().isPrettyXDFOutput())
               outputWriter.write(indent + Specification.getInstance().getPrettyXDFOutputIndentation());
   
             outputWriter.write( "<" + noChildObjectNodeName + "/>");
   
             if (Specification.getInstance().isPrettyXDFOutput())
               outputWriter.write(Constants.NEW_LINE);
   
           }
   
          // ok, now deal with closing the node
           if (nodeNameString != null) {
   
              indent = dealWithClosingGroupNodes((BaseObject) this, outputWriter, indent);
   
             if (Specification.getInstance().isPrettyXDFOutput() && pcdata == null)
                   outputWriter.write( indent);
   
             if (!dontCloseNode)
                 outputWriter.write( "</"+nodeNameString+">");
   
           }
   
         } else {

           if (nodeNameString != null) {
	       if (dontCloseNode) {
		   // it may not have sub-objects, but we dont want to close it
		   // (happens for group objects)
		   outputWriter.write( ">");
	       } else {
		   // no sub-objects, just close this node
		   outputWriter.write( "/>");
	       }
	   }
   
         }
   
         // if (Specification.getInstance().isPrettyXDFOutput() && nodeNameString != null ) 
	 //    outputWriter.write( Constants.NEW_LINE);

         return nodeNameString;

      } //end synchronize

   }

   /** a special method used by constructor methods to
       conviently build the XML attribute list for a given class.
    */
   protected void init()
   {

        super.init();

//        resetAttributes();

        // NO! not XML attributes.. should be protected field
        // attribOrder.add(0, XML_ELEMENTLIST_XML_ATTRIBUTE_NAME);
        // attribHash.put(XML_ELEMENTLIST_XML_ATTRIBUTE_NAME, new Attribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));

   }

}

