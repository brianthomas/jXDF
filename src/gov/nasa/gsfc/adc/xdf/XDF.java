
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
   private XMLDeclaration xmlDeclaration;
   private DocumentType documentType;

   /* XML attribute names */
   protected static final String TYPE_XML_ATTRIBUTE_NAME = new String("type");

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
     return (String) ((Attribute) attribHash.get(TYPE_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** Get the XMLDeclaration object which describes the XML declaration to 
      be printed when toXMLWriter/toXMLOutputStream methods are called for this object.
   */
  public XMLDeclaration getXMLDeclaration() {
     return xmlDeclaration;
  }

  /** Set the XMLDeclaration object which describes the XML declaration to 
      be printed when toXMLWriter/toXMLOutputStream methods are called for this object.
   */
  public void setXMLDeclaration (XMLDeclaration obj) {
     xmlDeclaration = obj;
  }

  /** Get the DocumentType object which describes the XML DOCTYPE declaration to 
      be printed when toXMLWriter/toXMLOutputStream methods are called for this object.
   */ 
  public DocumentType getDocumentType() {
     return documentType;
  }

  /** Set the DocumentType object which describes the XML DOCTYPE declaration to 
      be printed when toXMLWriter/toXMLOutputStream methods are called for this object.
   */
  public void setDocumentType (DocumentType docType) {
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

   protected String basicXMLWriter (
                                Writer outputWriter,
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
       writeXMLHeader(outputWriter, indent);

       return super.basicXMLWriter ( outputWriter, indent, dontCloseNode, newNodeNameString, noChildObjectNodeName);

   }


   /** Special method used by constructor methods to
     *  convienently build the XML attribute list for a given class.
     */
   // overrides Structure.init() method
   protected void init()
   {
       super.init();

       // xmlDeclaration = new XMLDeclaration ();
       // documentType = new DocumentType(this);

       classXDFNodeName = "XDF";

       attribOrder.add(TYPE_XML_ATTRIBUTE_NAME);
       attribHash.put(TYPE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));

   };

   /**
     *  @return the current *type* attribute. This specifies what the name of the 
     *  dataformat actually is. If undefined, then the structure is *vanilla* XDF.
     */
  protected void setType(String type) {
    ((Attribute) attribHash.get(TYPE_XML_ATTRIBUTE_NAME)).setAttribValue(type);
  }


  /** Write the XML Declaration/Doctype to the indicated OutputStream.
   */
  protected void writeXMLHeader( Writer outputWriter, String indent)
  throws java.io.IOException
  {

    // initial statement
    if (xmlDeclaration != null) 
       xmlDeclaration.toXMLWriter(outputWriter, indent);

    if ( documentType != null) 
       documentType.toXMLWriter(outputWriter, indent);

  }

} // end of XDF class 

/* Modification History:
 *
 * $Log$
 * Revision 1.9  2001/09/14 18:23:09  thomas
 * Changed handling of XML header stuff. Put in hooks for XMLDeclaration obj, etc
 *
 * Revision 1.8  2001/09/13 21:39:25  thomas
 * name change to either XMLAttribute, XMLNotation, XDFEntity, XMLElementNode class forced small change in this file
 *
 * Revision 1.7  2001/09/05 21:58:18  thomas
 *  moved NotationNode/Decl stuff out to new classes
 *
 * Revision 1.6  2001/07/26 15:55:42  thomas
 * added flush()/close() statement to outputWriter object as
 * needed to get toXMLOutputStream to work properly.
 *
 * Revision 1.5  2001/07/19 22:01:30  thomas
 * put XMLDeclAttribs into toXMLOutputStream (only needed
 * in the XDF class)
 * added  NotationNodeHash stuff (again, only needed in XDF class)
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


