
// XDF - the top-level object class of the XDF package 
// CVS $Id$

// XDF.java Copyright (C) 2001 Brian Thomas,
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

// import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class XDF extends Structure implements XDFInterface {

   //
   //Fields
   //

   /* XML attribute names */
   protected static final String TYPE_XML_ATTRIBUTE_NAME = new String("type");

   //
   // Constructor
   //

  /** The no argument constructor.
   */
  public XDF ()
  {

     super();

  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public XDF ( Hashtable InitXDFAttributeTable )
  {

    super(InitXDFAttributeTable);

  }

  //
  // Get/Set Methods
  //

  /**
    *  @return the current *type* attribute. This specifies what the name of the 
    *  dataformat actually is. If undefined, then the structure is *vanilla* XDF.
    */
  public String getType() {
     return (String) ((XMLAttribute) attribHash.get(TYPE_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  //
  //Other PUBLIC Methods
  //

  /** Initialize this XDF object from an XML file.
   * @return the structure read in on success, null on failure.
   */

   public void loadFromXDFFile (String filename)
   {

      // clear out existing settings in our structure
      // with a quick init. Trust java to garbage collect
      // freed objects(!!)
      this.init();

      // create an XDFreader, declare this structure object
      // to be the one it should read into.
      gov.nasa.gsfc.adc.xdf.Reader reader = new gov.nasa.gsfc.adc.xdf.Reader(this);
      try {
        reader.parsefile(filename);
      } catch (java.io.IOException e) {
        Log.printStackTrace(e);
      }

   }

   public Object clone() throws CloneNotSupportedException{
     XDF cloneObj = (XDF) super.clone();

    //deep copy of the paramGroupOwnedHash
     synchronized (this.paramGroupOwnedHash) {
      synchronized(cloneObj.paramGroupOwnedHash) {
        cloneObj.paramGroupOwnedHash = Collections.synchronizedSet(new HashSet(this.paramGroupOwnedHash.size()));
        Iterator iter = this.paramGroupOwnedHash.iterator();
        while (iter.hasNext()) {
          cloneObj.paramGroupOwnedHash.add(((Group)iter.next()).clone());
        }
      }
    }
    return cloneObj;
   }

   // 
   // Protected Methods
   //

   /** Special method used by constructor methods to
     *  convienently build the XML attribute list for a given class.
     */
   // overrides Structure.init() method
   protected void init()
   {

       super.init();

       classXDFNodeName = "XDF";

       attribOrder.add(TYPE_XML_ATTRIBUTE_NAME);
       attribHash.put(TYPE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));

   };


}

/* Modification History:
 *
 * $Log$
 * Revision 1.1  2001/05/04 20:05:53  thomas
 * Initial version
 *
 *
 */


