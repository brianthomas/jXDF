
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

   /* XML attribute names */
   protected static final String TYPE_XML_ATTRIBUTE_NAME = new String("type");

   // stores notation entries for the XMLDeclaration
   protected HashSet XMLNotationHash = new HashSet();

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

  /** Set the NotationHash for this XDF object. Each entry in the passed HashSet
      will be a Hashtable containing the keys 'name' 'publicId' and 'systemId'.
      This information will be printed out with other XMLDeclarations in a 
      toXMLFileHandle call that prints the XML declaration (e.g. DOCTYPE header). 
  */
  public void setXMLNotationHash (HashSet hash) {
     XMLNotationHash = hash;
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

       //  To be valid XML, we always start an XML block with an
       //  XML declaration (e.g. somehting like "<?xml standalone="no"?>").
       //  Here we deal with  printing out XML Declaration && its attributes
       if ((XMLDeclAttribs !=null) &&(!XMLDeclAttribs.isEmpty())) {
          indent = "";
          writeXMLDeclToOutputStream(outputstream, XMLDeclAttribs);
       }
 
       super.toXMLOutputStream ( outputstream, indent,
                                 dontCloseNode, newNodeNameString, 
                                 noChildObjectNodeName);
   }

   public void toXMLOutputStream (
                                   OutputStream outputstream,
                                   String indent,
                                   boolean dontCloseNode,
                                   String newNodeNameString,
                                   String noChildObjectNodeName
                                 )
   throws java.io.IOException
   {

/*
     // prepare XMLDeclaration
      Hashtable XMLDeclAttribs = new Hashtable();
      XMLDeclAttribs.put("standalone", new String("no"));
      XMLDeclAttribs.put("dtdName", Specification.getInstance().getXDFDTDName());
      XMLDeclAttribs.put("rootName", Specification.getInstance().getXDFRootNodeName());
*/

      this.toXMLOutputStream(outputstream, null, indent, dontCloseNode, newNodeNameString, noChildObjectNodeName);
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

   /**
     *  @return the current *type* attribute. This specifies what the name of the 
     *  dataformat actually is. If undefined, then the structure is *vanilla* XDF.
     */
   protected void setType(String type) {
     ((XMLAttribute) attribHash.get(TYPE_XML_ATTRIBUTE_NAME)).setAttribValue(type);
   }


   // 
   // Private Methods
   //


 /** Write the XML Declaration to the indicated OutputStream.
   */
  protected void writeXMLDeclToOutputStream ( OutputStream outputstream,
                                            Hashtable XMLDeclAttribs
                                          )
  throws java.io.IOException
  {

    // initial statement
    writeOut(outputstream, "<?xml");
    writeOut(outputstream, " version=\"" + Specification.getInstance().getXMLSpecVersion() + "\"");

    // print attributes
    Enumeration keys = XMLDeclAttribs.keys();
    while ( keys.hasMoreElements() )
    {
      String attribName = (String) keys.nextElement();
      if (attribName.equals("version") ) {
         Log.errorln("XMLDeclAttrib hash has version attribute, not allowed and ignoring.");
      } else if ( attribName.equals("dtdName") || attribName.equals("rootName") ) {
         // skip over it
      } else
         writeOut(outputstream, " " + attribName + "=\"" + XMLDeclAttribs.get(attribName) + "\"");
    }
    writeOut(outputstream, " ?>");

    if (Specification.getInstance().isPrettyXDFOutput())
        writeOut(outputstream, Constants.NEW_LINE); 

    // Print the DOCTYPE DECL only if right info exists
    if (XMLDeclAttribs.containsKey("rootName")
        && XMLDeclAttribs.containsKey("dtdName"))
    {
        //Nope always print the DOCTYPE DECL 
/*
        //print the DOCTYPE DECL IF its a structure node
        if(classXDFNodeName != null && 
            classXDFNodeName.equals(Specification.getInstance().getXDFStructureNodeName()) )
        {
*/
            writeOut(outputstream, "<!DOCTYPE " + XMLDeclAttribs.get("rootName") + " SYSTEM \""
                                   + XMLDeclAttribs.get("dtdName") +"\"");
            // any entities need to now be written.
            // check for entities in href's
            ArrayList hrefObjList = findAllChildHrefObjects();

            StringBuffer entityString = new StringBuffer ();
            StringBuffer notationString = new StringBuffer ();

           // if we have any, then we must print out
            if (hrefObjList.size() > 0) {

               if (Specification.getInstance().isPrettyXDFOutput())
                  entityString.append(Constants.NEW_LINE);

               // whip thru the list of href objects to get entities
               synchronized (hrefObjList) {
                  Iterator iter = hrefObjList.iterator(); // Must be in synchronized block
                  while (iter.hasNext()) {
                     Href hrefObj = (Href) iter.next();
                     if (Specification.getInstance().isPrettyXDFOutput())
                        entityString.append(Specification.getInstance().getPrettyXDFOutputIndentation());

                     entityString.append("<!ENTITY " + hrefObj.getName());
                     if (hrefObj.getPubId() != null)
                        entityString.append(" PUBLIC \"" + hrefObj.getPubId() + "\"");
                     if (hrefObj.getSysId() != null)
                        entityString.append(" SYSTEM \"" + hrefObj.getSysId() + "\"");
                     if (hrefObj.getNdata() != null)
                        entityString.append(" NDATA " + hrefObj.getNdata());
                     entityString.append(">");

                     if (Specification.getInstance().isPrettyXDFOutput())
                        entityString.append(Constants.NEW_LINE);
                  }
               }

            }

            // Now do notation stuff 
            synchronized (XMLNotationHash) { // argh, needed?  
               Iterator iter = XMLNotationHash.iterator(); // Must be in synchronized block

               while (iter.hasNext()) {
                  Hashtable notationHash = (Hashtable) iter.next();
                  if (notationHash.containsKey("name"))
                  {

                     if (Specification.getInstance().isPrettyXDFOutput())
                         notationString.append(Specification.getInstance().getPrettyXDFOutputIndentation());

                     notationString.append("<!NOTATION " + notationHash.get("name"));

                     if (notationHash.containsKey("publicId"))
                        notationString.append(" PUBLIC \"" + notationHash.get("publicId") + "\"");

                     if (notationHash.containsKey("systemId"))
                        notationString.append(" SYSTEM \"" + notationHash.get("systemId") + "\"");

                     notationString.append(">");

                     if (Specification.getInstance().isPrettyXDFOutput())
                        notationString.append(Constants.NEW_LINE);

                  }
                   else
                  {
                     Log.warnln("Notation entry lacks name, ignoring entry\n");
                  }
               }
            }

            if(entityString.length() > 0 || notationString.length() > 0 ) {
               writeOut(outputstream, " [");
               if(entityString.length() > 0)
                  writeOut(outputstream, entityString.toString());
               if (notationString.length() > 0 )
                  writeOut(outputstream, notationString.toString());
               writeOut(outputstream, "]");
            }

            writeOut(outputstream, ">");
/*
        } // end of DOCTYPE decl 
*/

        if (Specification.getInstance().isPrettyXDFOutput())
            writeOut(outputstream, Constants.NEW_LINE);

    } else
      Log.errorln("Passed XMLDeclAttributes table lacks either dtdName or rootName entries, ignoring DOCTYPE line printout");

  }

} // end of XDF class 

/* Modification History:
 *
 * $Log$
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


