

// XDF BaseObjectWithValueList Class
// CVS $Id$


// BaseObjectWithValueList.java Copyright (C) 2001 Brian Thomas,
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
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.Hashtable;
import java.util.Collections;

import java.io.OutputStream;
import java.io.IOException;

/**
     The base object class for XDF objects which may hold internal XML elements.
 */
public abstract class BaseObjectWithValueList extends BaseObject 
{

   //
   // Fields
   //

   protected boolean hasValueListCompactDescription = false;  
   protected String valueListXMLItemName;

   List valueListObjects = Collections.synchronizedList(new ArrayList());

   /**  This constructor takes a Java Hashtable as an initializer of
        the XML attributes of the object to be constructed. The
        Hashtable key/value pairs coorespond to the class XDF attribute
        names and their desired values.
    */
   public BaseObjectWithValueList ( Hashtable InitXDFAttributeTable )
   {

      // init the XML attributes (to defaults)
      init();

      // init the value of selected XML attributes to HashTable values
      hashtableInitXDFAttributes(InitXDFAttributeTable);
   }

   // no-arg constructor
   public BaseObjectWithValueList() {
      init();
   }

   //
   // Get/Set Methods
   //

   protected void setValueListObj (ValueListInterface valueListObj) 
   {

      resetBaseValueListObjects();
      addValueListObj(valueListObj);

   }

   protected boolean addValueListObj (ValueListInterface valueListObj) 
   {

      if (valueListObj == null) return false;

      valueListObjects.add(valueListObj);
      hasValueListCompactDescription = true;
      return true;

   }

   /** reset the list of valueList objects held within this object.
    */
   protected void resetBaseValueListObjects () {

      valueListObjects = Collections.synchronizedList(new ArrayList());

      hasValueListCompactDescription = false;

   }

   /** Return the list of objects which describe the compact
     * description(s) of the value list. Each object in this list
     * conforms to the ValueListInterface format. 
     */
   protected List getValueListObjects () {
      return valueListObjects;
   }

   //
   // Other Public Methods
   //

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
   throws java.io.IOException
   {

    //while writing out, attribHash should not be changed
    synchronized (attribHash) {
      String nodeNameString = this.classXDFNodeName;

      // Setup. Sometimes the name of the node we are opening is different from
      // that specified in the classXDFNodeName (*sigh*)
      if (newNodeNameString != null) nodeNameString = newNodeNameString;

      // 1. open this node, print its simple XML attributes
      if (nodeNameString != null) {

        if (Specification.getInstance().isPrettyXDFOutput())
          writeOut(outputstream, indent); // indent node if desired
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
      String pcdata = (String) xmlInfo.get("PCDATA");

     if ( childObjs.size() > 0 || pcdata != null || noChildObjectNodeName != null)
      {

        // close the opening tag
        if (nodeNameString != null) {
          writeOut(outputstream, ">");
          if ((Specification.getInstance().isPrettyXDFOutput()) && (pcdata == null))
             writeOut(outputstream, Constants.NEW_LINE);
        }

        // deal with object/list XML attributes, if any in our list
        int size = childObjs.size();
        for (int i = 0; i < size; i++) {
          Hashtable item = (Hashtable) childObjs.get(i);

          if (item.get("type") == Constants.LIST_TYPE)
          {

               if (hasValueListCompactDescription && valueListXMLItemName.equals(item.get("name"))) 
               {

                  Iterator iter = valueListObjects.iterator();
                  while(iter.hasNext()) {
                     ValueListInterface valueListObj = (ValueListInterface) iter.next();
                     // Grouping *may* differ between the values held in each ValueLists. To check we
                     // if all valueListObjects are 'kosher' we use the first value in the values 
                     // list of each ValueListObj as a reference object and compare it to all other
                     // values in that list (but not the lists of values in other ValueListObj). Yes, 
                     // this can be slow for large lists of values but is the correct thing to do.
                     List values = valueListObj.getValues();
                     Value valueObj = (Value) values.get(0);

                     // *sigh* Yes, we have to check that all values belong to 
                     // the same groups, or problems will arise in the output. Do that here. 
                     boolean canUseCompactValueDescription = true;
                     Set firstValueGroups = valueObj.openGroupNodeHash;

                     Iterator valueIter = values.iterator();
                     valueIter.next(); // no need to do first group
                     while (valueIter.hasNext()) {
                        Value thisValue = (Value) valueIter.next();
                        if (thisValue != null) {
                           Set thisValuesGroups = thisValue.openGroupNodeHash;
                           if (!firstValueGroups.equals(thisValuesGroups)) { // Note this comparison also does size too 
                              Log.infoln("Cant use short description for values because some values have differing groups! Using long description instead.");
                              canUseCompactValueDescription = false;
                              break;
                           }
                        }
                     }

                     if (canUseCompactValueDescription) {

                        // use compact description
                        indent = dealWithClosingGroupNodes((BaseObject) valueObj, outputstream, indent);
                        indent = dealWithOpeningGroupNodes((BaseObject) valueObj, outputstream, indent);
                        String newindent = indent + Specification.getInstance().getPrettyXDFOutputIndentation();
                        // now print the valuelist itself
                        valueListObj.toXMLOutputStream(outputstream, newindent);
                     } else {

                        // use regular (long) method
                        List objectList = (List) item.get("value");
                        indent = objectListToXMLOutputStream(outputstream, objectList, indent);

                     }

                  }

               } else {

                  // use regular method
                  List objectList = (List) item.get("value");
                  indent = objectListToXMLOutputStream(outputstream, objectList, indent);

               }
          }
          else if (item.get("type") == Constants.OBJECT_TYPE)
          {
            BaseObject containedObj = (BaseObject) item.get("value");
            if (containedObj != null) { // can happen from pre-allocation of axis values, etc (?)
              // shouldnt this be synchronized too??
              synchronized(containedObj) {
                indent = dealWithClosingGroupNodes(containedObj, outputstream, indent);
                indent = dealWithOpeningGroupNodes(containedObj, outputstream, indent);
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

      if (Specification.getInstance().isPrettyXDFOutput()  && nodeNameString != null)
          writeOut(outputstream, Constants.NEW_LINE);
    }//synchronize

   }

   public Object clone() throws CloneNotSupportedException {
      BaseObjectWithValueList cloneObj = (BaseObjectWithValueList) super.clone();

      cloneObj.valueListObjects = Collections.synchronizedList(new ArrayList());
      int stop = this.valueListObjects.size();
      for (int i = 0; i < stop; i++) {
          cloneObj.valueListObjects.add( ((ValueListInterface) this.valueListObjects.get(i)).clone());
      }
      return cloneObj;

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
        hasValueListCompactDescription = false;

   }

}

/** Modification Log 
  *
  * $Log$
  * Revision 1.2  2001/07/13 21:45:04  thomas
  * improved comments
  *
  * Revision 1.1  2001/07/11 22:40:44  thomas
  * Initial Version
  *
  *
  *
*/

