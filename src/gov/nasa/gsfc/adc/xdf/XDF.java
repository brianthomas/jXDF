
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
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class XDF extends Structure {

   //
   //Fields
   //
   private XDFDocumentType documentType;

   /* XML attribute names */
   protected static final String TYPE_XML_ATTRIBUTE_NAME = new String("type");

   // stores notation entries for the XMLDeclaration
//   protected HashSet XMLNotationHash = new HashSet();

   //
   // Constructors
   //

   /** The no argument constructor.
    */
   public XDF ()
   {

      // init the XML attributes (to defaults)
      init();

   }

   /**  This constructor takes a Java Hashtable as an initializer of
        the XML attributes of the object to be constructed. The
        Hashtable key/value pairs coorespond to the class XDF attribute
        names and their desired values.
    */
   public XDF ( Hashtable InitXDFAttributeTable )
   {

      // init the XML attributes (to defaults)
      init();

      // init the value of selected XML attributes to HashTable values
      hashtableInitXDFAttributes(InitXDFAttributeTable);

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

  /* Set the NotationHash for this XDF object. Each entry in the passed HashSet
      will be a Hashtable containing the keys 'name' 'publicId' and 'systemId'.
      This information will be printed out with other XMLDeclarations in a 
      toXMLFileHandle call that prints the XML declaration (e.g. DOCTYPE header). 
  */
/*
  public void setXMLNotationHash (HashSet hash) {
     XMLNotationHash = hash;
  }
*/

  public XDFDocumentType getDocumentType() {
     return documentType;
  }

  public void setDocumentType (XDFDocumentType docType) {
     documentType = docType;
  }

  //
  //Other PUBLIC Methods
  //

  /** Initialize this XDF object from an XML file containing XDF data.
   * @return the structure read in on success, null on failure.
   */

   public void loadFromXMLFile (String filename)
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

   /** Write this object and all the objects it owns to the supplied
       OutputStream object as XDF. Supplying a populated XMLDeclAttributes
       Hashtable will result in the xml declaration being written at the
       begining of the outputstream (so when you do this, you will
       get well-formmed XML output for ANY object). For obvious reasons, only
       XDF objects will create *valid XDF* output.
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

       Writer outputWriter = new BufferedWriter(new OutputStreamWriter(outputstream));
       toXMLWriter (outputWriter, XMLDeclAttribs, indent, dontCloseNode, newNodeNameString, noChildObjectNodeName);

       // this *shouldnt* be needed, but tests with both Java 1.2.2 and 1.3.0
       // on SUN and Linux platforms show that it is. Hopefully we can remove
       // this in the future.
       outputWriter.flush();

   }

   public void toXMLWriter (
                                Writer outputWriter,
                                Hashtable XMLDeclAttribs,
                                String indent,
                                boolean dontCloseNode,
                                String newNodeNameString,
                                String noChildObjectNodeName
                             )
   throws java.io.IOException
   {


       //  To be valid XML, we always start an XML block with an
       //  XML declaration (e.g. somehting like "<?xml standalone="no"?>").
       //  Here we deal with  printing out XML Declaration && its attributes
       if (XMLDeclAttribs != null) { //  &&(!XMLDeclAttribs.isEmpty())) {
          indent = "";
          writeXMLHeader(outputWriter, XMLDeclAttribs, true);
       }
 
       toXMLWriter ( outputWriter, indent, dontCloseNode, newNodeNameString, noChildObjectNodeName);

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

       documentType = new XDFDocumentType(this);

       classXDFNodeName = "XDF";

       attribOrder.add(TYPE_XML_ATTRIBUTE_NAME);
       attribHash.put(TYPE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));

   };

   /**
     *  @return the current *type* attribute. This specifies what the name of the 
     *  dataformat actually is. If undefined, then the structure is *vanilla* XDF.
     */
   protected void setType(String type) {
     ((XMLAttribute) attribHash.get(TYPE_XML_ATTRIBUTE_NAME)).setAttribValue(type);
   }


  /** Write the XML Declaration/Doctype to the indicated OutputStream.
   */
  protected void writeXMLHeader( Writer outputWriter,
                                 Hashtable XMLDeclAttribs,
                                 boolean writeDoctype
                               )
  throws java.io.IOException
  {

    // initial statement
    outputWriter.write("<?xml");
    outputWriter.write(" version=\"" + Constants.XML_SPEC_VERSION + "\"");

    // print attributes
    Enumeration keys = XMLDeclAttribs.keys();
    while ( keys.hasMoreElements() )
    {
      String attribName = (String) keys.nextElement();
      if (attribName.equals("version") ) {
         Log.errorln("XMLDeclAttrib hash has version attribute, not allowed and ignoring.");
      } else
         outputWriter.write(" " + attribName + "=\"" + XMLDeclAttribs.get(attribName) + "\"");
    }
    outputWriter.write("?>");

    if (Specification.getInstance().isPrettyXDFOutput())
        outputWriter.write(Constants.NEW_LINE); 

    if ( writeDoctype ) {
       this.getDocumentType().toXMLWriter(outputWriter, "");
    }

  }

} // end of XDF class 

/* Modification History:
 *
 * $Log$
 * Revision 1.7  2001/09/05 21:58:18  thomas
 *  moved XMLNotation/Decl stuff out to new classes
 *
 * Revision 1.6  2001/07/26 15:55:42  thomas
 * added flush()/close() statement to outputWriter object as
 * needed to get toXMLOutputStream to work properly.
 *
 * Revision 1.5  2001/07/19 22:01:30  thomas
 * put XMLDeclAttribs into toXMLOutputStream (only needed
 * in the XDF class)
 * added  XMLNotationHash stuff (again, only needed in XDF class)
 *
 * Revision 1.4  2001/07/11 22:35:21  thomas
 * Changes related to adding valueList or removeal of unneeded interface files.
 *
 * Revision 1.3  2001/07/06 19:04:23  thomas
 * toXMLOutputStream and related methods now pass on IOExceptions
 * to the application writer (e.g. they throw the error).
 *
 * Revision 1.2  2001/05/10 21:44:07  thomas
 * small change to constructors related to inheritance.
 * moved appropriate code for XMLDecl writing to
 * this class for the toXMLoutputStream method.
 *
 * Revision 1.1  2001/05/04 20:05:53  thomas
 * Initial version
 *
 *
 */


