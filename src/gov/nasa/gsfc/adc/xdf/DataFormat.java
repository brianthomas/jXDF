

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

import java.io.OutputStream;
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
/*
   protected static final String LESSTHANVALUE_XML_ATTRIBUTE_NAME = new String("lessThanValue");
   protected static final String LESSTHANOREQUALVALUE_XML_ATTRIBUTE_NAME = new String("lessThanOrEqualValue");
   protected static final String GREATERTHANVALUE_XML_ATTRIBUTE_NAME = new String("greaterThanValue");
   protected static final String GREATERTHANOREQUALVALUE_XML_ATTRIBUTE_NAME = new String("greaterThanOrEqualValue");
   protected static final String INFINITEVALUE_XML_ATTRIBUTE_NAME = new String("infiniteValue");
   protected static final String INFINITENEGATIVEVALUE_XML_ATTRIBUTE_NAME = new String("infiniteNegativeValue");
   protected static final String NODATAVALUE_XML_ATTRIBUTE_NAME = new String("noDataValue");
*/

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
/*
   public  abstract void setLessThanValue(Object Obj);
   public  abstract void setLessThanOrEqualValue(Object Obj);
   public  abstract void setGreaterThanValue(Object Obj) ;
   public  abstract void setGreaterThanOrEqualValue(Object Obj) ;
   public  abstract void setInfiniteValue(Object Obj) ;
   public  abstract void setInfiniteNegativeValue(Object Obj) ;
   public  abstract void setNoDataValue(Object Obj) ;
*/

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
    * @return the current *lessThanValue* attribute
    */
/*
   public Object getLessThanValue()
   {
      return ((XMLAttribute) attribHash.get(LESSTHANVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }
 
   /**
    * @return the current *lessThanOrEqualValue* attribute
    */
/*
   public Object getlessThanOrEqualValue()
   {
     return ((XMLAttribute) attribHash.get(LESSTHANOREQUALVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }
 
   /**
    * @return the current *greaterThanValue* attribute
    */
/*
   public Object getGreaterThanValue()
   {
     return ((XMLAttribute) attribHash.get(GREATERTHANVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }
 
    /**
    * @return the current *greaterThanOrEqualValue* attribute
    */
/*
   public Object getGreaterThanOrEqualValue()
   {
     return ((XMLAttribute) attribHash.get(GREATERTHANOREQUALVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }
 
    /**
    * @return the current *infiniteValue* attribute
    */
/*
   public Object getInfiniteValue()
   {
     return ((XMLAttribute) attribHash.get(INFINITEVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }
 
    /**
    * @return the current *infiniteNegativeValue* attribute
    */
/*
   public Object getInfiniteNegativeValue()
   {
     return ((XMLAttribute) attribHash.get(INFINITENEGATIVEVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }
*/
 
   /**
    * @return the current *noDataValue* attribute
    */
/*
   public Object getNoDataValue()
   {
     return ((XMLAttribute) attribHash.get(NODATAVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }
*/
 
   //
   // Other Public Methods
   //
 
   /** override the base object method to add a little tailoring
    */
   public void toXMLOutputStream (  OutputStream outputstream,
                                    Hashtable XMLDeclAttribs,
                                    String indent,
                                    boolean dontCloseNode,
                                    String newNodeNameString,
                                    String noChildObjectNodeName
   ) {
 
     String nodeNameString = classXDFNodeName;
     // 1. open this node, print its simple XML attributes
       if (Specification.getInstance().isPrettyXDFOutput())
         writeOut(outputstream, indent); // indent node if desired
 
       writeOut(outputstream,"<" + nodeNameString + ">");   // print opening statement
 
       //writeOut the body of DataFormat
       writeOut(outputstream, "<" + specificDataFormatName);
 
     // gather info about XMLAttributes in this object/node
     Hashtable xmlInfo = getXMLInfo();
 
     // 2. Print out string object XML attributes EXCEPT for the one that
     //    matches PCDATAAttribute.
     ArrayList attribs = (ArrayList) xmlInfo.get("attribList");
 
     synchronized(attribs) {
       int stop = attribs.size();
       for (int i = 0; i < stop; i++) {
         Hashtable item = (Hashtable) attribs.get(i);
         writeOut(outputstream, " "+ item.get("name") + "=\"");
         writeOutAttribute(outputstream, (String)item.get("value"));
         writeOut(outputstream, "\"");
       }
     }
 
     //writeout end of the boby
     writeOut(outputstream, "/>");
 
     //writeout closing node
     writeOut(outputstream, "</" + nodeNameString+ ">");
     if (Specification.getInstance().isPrettyXDFOutput())
       writeOut(outputstream, Constants.NEW_LINE);
 
   }
 
    //
    // Private Methods
    //
 
   /** Special private method used by constructor methods to
    *  convienently build XML attribute Order list
    */
   protected void init()
   {
 
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
     attribHash.put( LESSTHANVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
     attribHash.put( LESSTHANOREQUALVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
     attribHash.put( GREATERTHANVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
     attribHash.put( GREATERTHANOREQUALVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
     attribHash.put( INFINITEVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
     attribHash.put( INFINITENEGATIVEVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
     attribHash.put( NODATAVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
*/
 
   }
 
 }  //end of DataFormat class

 /* Modification History:
 *
 * $Log$
 * Revision 1.14  2001/05/04 20:22:25  thomas
 * changed init() to protected.
 *
 * Revision 1.13  2001/04/27 21:28:57  thomas
 * No longer has get/set methods for lessthan, etc.
 * These are in Array and Field classes as per the DTD
 * (for now, we plan to move them back into Dataformat
 * for 0.20 DTD).
 *
 * Revision 1.12  2001/02/07 18:44:04  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.11  2000/11/27 22:39:26  thomas
 * Fix to allow attribute text to have newline, carriage
 * returns in them (print out as entities: &#010; and
 * &#013;) This allows files printed out to be read back
 * in again(yeah!). -b.t.
 *
 * Revision 1.10  2000/11/22 20:42:00  thomas
 * beaucoup changes to make formatted reads work.
 * DataFormat methods now store the "template" or
 * formatPattern that will be needed to print them
 * back out. Removed sprintfNotation, Perl regex and
 * Perl attributes from DataFormat classes. -b.t.
 *
 * Revision 1.9  2000/11/20 22:03:48  thomas
 * Split up XMLAttribute type NUMBER_TYPE into
 * INTEGER_TYPE and DOUBLE_TYPE. This allows for
 * some needed handling in the SaxDocHandler when
 * parsing data for the formatted read. Put prior NUMBER_TYPE
 * attributes into appropriate new category. -b.t.
 *
 * Revision 1.8  2000/11/16 19:56:48  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.7  2000/11/08 22:30:12  thomas
 * Changed set methods to return void. -b.t.
 *
 * Revision 1.6  2000/11/08 19:38:57  thomas
 * Cleaned up import path. -b.t.
 *
 * Revision 1.5  2000/11/08 19:18:07  thomas
 * Changed the name of toXDF* methods to toXML* to
 * better reflect the nature of the output (its not XDF
 * unless you call th emethod from strcuture object;
 * otherwise, it wont validate as XDF; it is still XML
 * however). -b.t.
 *
 * Revision 1.4  2000/10/27 21:15:00  kelly
 * completed *toXDF*.  -k.z.
 *
 * Revision 1.3  2000/10/26 20:14:17  kelly
 * major fix.  get methods are now in this abstract class.  all set methods are declared as abstract.  -k.z.
 *
 * Revision 1.2  2000/10/16 14:48:18  kelly
 * pretty much completed the class.  --k.z.
 *
 */
