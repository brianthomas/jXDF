

// XDF DataFormat Class
// CVS $Id$

// DataFormat.java Copyright (C) 2000 Brian Thomas,
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

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *  
 * @version $Revision$
 */


 public abstract class DataFormat extends BaseObject {

   //
   // Constants
   //

   /* XML attribute names */
   protected static final String NODATAVALUE_XML_ATTRIBUTE_NAME = new String("noDataValue");

   //
   //Fields
   //

   // stores the subclass's particular nodeName, "string", "integer", etc.
   protected String specificDataFormatName;
   protected String formatPattern;

   //
   // Constructors
   //

   /** The no argument constructor.
   */
   public DataFormat ()
   {
      init();
   }

   //
   // ABSTRACT methods
   //
 
   public abstract int numOfBytes(); //return the number of bytes
   public abstract void setNoDataValue(Object Obj);

   //
   // Public Get/Set Methods
   //

   /** The pattern is the (Message|Decimal)Format pattern that should 
       be used to print out data within the slice of the array covered 
       by this object. This method is used by the dataCube in its toXMLOutput method. 
    */
 
   public String getFormatPattern ( ) {
      return formatPattern;
   }
 
   /**
    * @return the current *noDataValue* attribute
    */
   public Object getNoDataValue()
   {
      return ((Attribute) attribHash.get(NODATAVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }
 
   //
   // Other Public Methods
   //
 
   protected String basicXMLWriter (   Writer outputWriter,
                                   String indent,
                                   boolean dontCloseNode,
                                   String newNodeNameString,
                                   String noChildObjectNodeName
                                 )
   throws java.io.IOException
   {
 
     String nodeNameString = classXDFNodeName;
     // 1. open this node, print its simple XML attributes
       if (Specification.getInstance().isPrettyXDFOutput())
         outputWriter.write(indent); // indent node if desired
 
       outputWriter.write("<" + nodeNameString + ">");   // print opening statement
 
       //write out the body of DataFormat
       outputWriter.write( "<" + specificDataFormatName);
 
     // gather info about Attributes in this object/node
     Hashtable xmlInfo = getXMLInfo();
 
     // 2. Print out string object XML attributes EXCEPT for the one that
     //    matches PCDATAAttribute.
     ArrayList attribs = (ArrayList) xmlInfo.get("attribList");
 
     synchronized(attribs) {
       int stop = attribs.size();
       for (int i = 0; i < stop; i++) {
         Hashtable item = (Hashtable) attribs.get(i);
         outputWriter.write( " "+ item.get("name") + "=\"");
         outputWriter.write( (String)item.get("value"));
         outputWriter.write( "\"");
       }
     }
 
     //writeout end of the boby
     outputWriter.write( "/>");
 
     //writeout closing node
     outputWriter.write( "</" + nodeNameString+ ">");

     //if (Specification.getInstance().isPrettyXDFOutput())
     // outputWriter.write( Constants.NEW_LINE);
 
     return nodeNameString;

   }
 
    //
    // Private Methods
    //
 
   /** Special private method used by constructor methods to
    *  convienently build XML attribute Order list
    */
   protected void init()
   {
 
     super.init();

     classXDFNodeName = "dataFormat";
 
     // order matters! these are in *reverse* order of their
     // occurence in the XDF DTD
     //the order of the attributes that all sub-classses should have
/*
     attribOrder.add(0, NODATAVALUE_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, INFINITENEGATIVEVALUE_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, INFINITEVALUE_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, GREATERTHANOREQUALVALUE_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, GREATERTHANVALUE_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, LESSTHANOREQUALVALUE_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, LESSTHANVALUE_XML_ATTRIBUTE_NAME);
*/
 
/*
     // typing here is just filler for latter
     attribHash.put( LESSTHANVALUE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
     attribHash.put( LESSTHANOREQUALVALUE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
     attribHash.put( GREATERTHANVALUE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
     attribHash.put( GREATERTHANOREQUALVALUE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
     attribHash.put( INFINITEVALUE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
     attribHash.put( INFINITENEGATIVEVALUE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
     attribHash.put( NODATAVALUE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
*/
 
   }
 
 }  //end of DataFormat class

