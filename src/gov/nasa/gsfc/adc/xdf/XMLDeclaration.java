
// XDF XMLDeclaration Class
// CVS $Id$

// XMLDeclaration.java Copyright (C) 2001 Brian Thomas,
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
import java.io.Writer;
import java.io.OutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

 /**
  *  The XMLDeclaration class is nothing more than a simple object that holds information
  *  concerning the xml declaration of the XDF (root) object.
  *  @version $Revision$
  */

// inheriting from BaseObject is probably overkill here.
 public class XMLDeclaration extends BaseObject {

   //
   // Fields
   //
   private static final String XML_DECL_NODE_NAME = new String("?xml");

   /* XML attribute names */
   private static final String STANDALONE_XML_ATTRIBUTE_NAME = new String("standalone");

   //
   // Constructor
   //

   /** The no argument constructor.
    */
   public XMLDeclaration ()
   {
      init();
   }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public XMLDeclaration ( Hashtable InitXDFAttributeTable )
  {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  //
  //Get/Set methods
  //

  /** Set the standalone attribute. 
   */
  public void setStandalone(String value) {

     if( value != null)
     {
        if(!Utility.isValidStandalone (value) ) { 
           Log.warnln("Warning: "+value+" is not a valid value for the standalone attribute, ignoring set request.");
           return;
        }
     }

     ((Attribute) attribHash.get(STANDALONE_XML_ATTRIBUTE_NAME)).setAttribValue(value);
  }

  /**
   * @return the current standalone attribute
   */
  public String getStandalone() {
    return (String) ((Attribute) attribHash.get(STANDALONE_XML_ATTRIBUTE_NAME)).getAttribValue();
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

      if (Specification.getInstance().isPrettyXDFOutput())
         outputWriter.write(indent);

      String standalone = getStandalone();

      outputWriter.write("<?xml");
      outputWriter.write(" version=\"" + Constants.XML_SPEC_VERSION + "\"");
      if (standalone != null)
         outputWriter.write(" standalone=\"" + standalone+"\"");

      outputWriter.write("?>");

      return "xml";
  }

  //
  // Protected Methods
  //
  /** Special private method used by constructor methods to
      conviently build the XML attribute list for a given class.
   */
  protected void init()
  {

    resetAttributes();

    classXDFNodeName = "";  //XDF node name (none)

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0, STANDALONE_XML_ATTRIBUTE_NAME);

    attribHash.put(STANDALONE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));

  };

 }

 /* Modification History:
 *
 * $Log$
 * Revision 1.1  2001/09/14 18:23:42  thomas
 * Initial Version, obj used by XDF object when it is printed out
 *
 *
 */


