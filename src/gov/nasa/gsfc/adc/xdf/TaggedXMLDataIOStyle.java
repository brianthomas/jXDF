// XDF TaggedXMLDataIOStyle Class
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.util.*;
import java.io.*;

// TaggedXMLDataIOStyle.java Copyright (C) 2000 Brian Thomas,
// ADC/GSFC-NASA, Code 631, Greenbelt MD, 20771


/**TaggedXMLDatatIOStytle.java:
 *  @version $Revision$
 */

public class TaggedXMLDataIOStyle extends XMLDataIOStyle {


  //
  //Fields
  //

  public static final String TagToAxisNodeName = "tagToAxis";

  //init hash size to 10, prevent rehashing, which is expansive
  protected Hashtable tagHash = new Hashtable(10);

  //
  //constructors and related methods
  //

  //no-arg contructor
  public TaggedXMLDataIOStyle(Array parentArray) {
    Log.debug("in TaggedXMLDataIOStyle, constructor");
    this.parentArray = parentArray;
  }

  //
  //Get/Set Methods
  //
  public Hashtable setTagHash(Hashtable tagHash) {
    this.tagHash = tagHash;
    return this.tagHash;
  }

  public Hashtable getTagHash() {
    return tagHash;
  }

  //
  //Other PUBLIC Methods
  //
/**setAxisTag: Set an association between an XDF data tag and axis reference.
 * One day we will hopefully be able to support user defined tags, but for the
 * time being you will have to stick to those specified by the XDF DTD
 * (e.g. "d0","d1", ... "d8"). Note that choosing the wrong tag name will break
 * the current XDF DTD, so go with the defaults (e.g. DONT use this method)
 * if you dont know what you are doing here.
 * @parma: tag, axisId
 * @return: tag value if successful, null if not
 */
  public String setAxisTag(String tag, String axisId) {
    if (tag == null || axisId== null) {
      Log.error("Missing information: need tag AND axisId for addAxisTag. Ignoring request. returning null");
      return null;
    }
    //insert in hash table, return tag value
    return (String) tagHash.put(axisId, tag);
  }

  /**getXMLDataIOStyleTags: Return an axis ordered list (ARRAY REF) of tags to
   *  be used to write tagged data.
   */

   public List getAxisTags() {
    List tags = new ArrayList();
    String tag;
    String axisId;
    String tempTag;
    List axisList = getParentArray().getAxisList();
    int counter = axisList.size();
    for (int i = 0; i<axisList.size(); i++) {
      axisId = ((Axis)axisList.get(i)).getAxisId();
      tag = "d" + counter--;  //the default tag
      //should it exist, we use whats in the tag hash
      //otherwise we go with the default as singed above
      tempTag = (String) tagHash.get(axisId);
      if (tempTag!=null)
        tag = tempTag;
      tags.add(tag);
    }
    return tags;
   }

   public void toXDFOutputStream ( OutputStream outputstream,
                                  Hashtable XMLDeclAttribs,
                                  String indent
                                )
  {
    boolean niceOutput = super.sPrettyXDFOutput;
    String myIndent;
    if (indent!=null)
      myIndent = indent;
    else
      myIndent = "";

    String moreIndent = myIndent + super.sPrettyXDFOutputIndentation;

    if (niceOutput)
      writeOut(outputstream, myIndent);

    //open the read block
    writeOut(outputstream, "<"+classXDFNodeName);

    //get attribute info
     Hashtable xmlInfo = getXMLInfo();

    //write out attributes

    ArrayList attribs = (ArrayList) xmlInfo.get("attribList");
    synchronized(attribs) {  //sync, prevent the attribs' structure be changed
      for (int i = 0; i < attribs.size(); i++) {
        Hashtable item = (Hashtable) attribs.get(i);
        writeOut(outputstream, " "+ item.get("name") + "=\"" + item.get("value") + "\"");
      }
    }
    writeOut(outputstream, ">");
    if (niceOutput)
      writeOut(outputstream, Constants.NEW_LINE);

    //write out the tags info
    List tags = Collections.synchronizedList(getAxisTags());
    List axisList = parentArray.getAxisList();
    String axisId;
    String tag;
    for (int i = 0; i <axisList.size(); i++) {
     axisId = ((Axis) axisList.get(i)).getAxisId();
     tag = (String)tags.get(i);
     if (niceOutput) {
      writeOut(outputstream, moreIndent);
     }
     writeOut(outputstream, "<" + TagToAxisNodeName + " axisIdRef=\\" + axisId + "\\" + "tag = \\" + tag + "\\/>");
     if (niceOutput) {
      writeOut(outputstream,Constants.NEW_LINE);
     }
    }

    //close the read block
    if (niceOutput) {
      writeOut(outputstream,indent);
     }
     writeOut(outputstream, "</"+classXDFNodeName+">");
    if (niceOutput) {
      writeOut(outputstream,Constants.NEW_LINE);
     }

  }

   //
   //PROTECTED Methods
   //

   /**removeAxsiTag: Remove an axis tag from the tag hash. This should be PROTECTED
    * and occur only when axis is being removed (ie available to array obj only).
    * @param: axisId to remove
    * @return: tag if successful, null if not
    */
   protected String removeAxisTag(String axisId) {
    return (String) getTagHash().remove(axisId);
   }

}
/* Modification History:
 *
 * $Log$
 * Revision 1.2  2000/10/17 22:03:54  kelly
 * completed the class.  -k.z.
 *
 * Revision 1.1  2000/10/11 19:05:53  kelly
 * created the class.
 *
 */
