
// XDF TaggedXMLDataIOStyle Class
// CVS $Id$

// TaggedXMLDataIOStyle.java Copyright (C) 2000 Brian Thomas,
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
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import java.io.Writer;
// import java.io.OutputStream;
import java.io.IOException; 

/** handles tagged IO
 *  @version $Revision$
 */

public class TaggedXMLDataIOStyle extends XMLDataIOStyle {


  //
  //Fields
  //

  public static final String TagToAxisNodeName = "tagToAxis";

  //init hash size to MaxTagDimensions, prevent rehashing, which is expansive
  private Hashtable tagHash = new Hashtable(Constants.MAX_TAGGED_DIMENSIONS);
  private Hashtable axisIdHash = new Hashtable(Constants.MAX_TAGGED_DIMENSIONS);
  private boolean needToCheckAxisOrder = true;

  //
  //constructors and related methods
  //

  //no-arg contructor
  public TaggedXMLDataIOStyle(Array parentArray) 
  {
     super(parentArray);
  }

  public TaggedXMLDataIOStyle(Array parentArray, Hashtable InitXDFAttributeTable) 
  {
     super(parentArray,InitXDFAttributeTable);
  }
  
  //
  //Get/Set Methods
  //

   /** Overrides the parent class method. We want to insure that this isnt set 
       as it doesnt make any sense to do so for tagged data which may not carry
       binary information. 
    */
   public void setEndian (String strEndian)
   {
       Log.warnln("setEndian() got invalid value. Tagged Data May not wrap binary values. Request ignored.");
   }

   /**Set an association between an XDF data tag and axis reference.
      One day we will hopefully be able to support user defined tags, but for the
      time being you will have to stick to those specified by the XDF DTD
      (e.g. "d0","d1", ... "d8"). Note that choosing the wrong tag name will break
      the current XDF DTD, so go with the defaults (e.g. DONT use this method)
      if you dont know what you are doing here.
      @params: tag - tag , axisId - axisId
      @return tag value
    */
   public void setAxisTag(String tag, String axisId) {

      // it would be nicer to be able to apply a regex here instead :P
      if (tag != null && tag.length() > 1 && tag.startsWith("d") ) {

         //insert in hash table, return tag value
         tagHash.put(axisId, tag);
         axisIdHash.put(tag, axisId);
         needToCheckAxisOrder = true;

      } else {
         Log.errorln("setAxisTag() got mal-formed tag string:"+tag+", cannot set.");
      }
   }

   /** Return the cooresponding axis object for the given XML tag 
    */
   public AxisInterface getAxisByTag (String tag) 
   {
      String axisId = (String) axisIdHash.get(tag);

      Iterator iter = getIOAxesOrder().iterator();
      while (iter.hasNext()) { 
         AxisInterface thisAxis = (AxisInterface) iter.next();
         if (thisAxis.getAxisId().equals(axisId)) {
            return thisAxis;
         }
      }
      return (AxisInterface) null;
   }

   /** Return the cooresponding XML tag for the passed axisId
    */
   public String getTagByAxisId (String axisId)
   {
      return (String) tagHash.get(axisId);
   }

   public String getAxisIdByTag (String tag)
   {
      return (String) axisIdHash.get(tag);
   }

   public void setIOAxesOrder(List axisOrderList) {
      Log.errorln("Cant setIOAxesOrder for TaggedXMLDataIOStyle");
   }

   public List getIOAxesOrder () {

         if (needToCheckAxisOrder)
         {
             resetIOAxesOrder();
             needToCheckAxisOrder = false;
         }

         return super.getIOAxesOrder();
   }

   /**getXMLDataIOStyleTags: Return an String array of tags to
    *  be used to write tagged data, return the tags in the order of
    * d0, d1, .., ., d8
    */

   public String[] getAxisTags () 
   {

     // List axisList = getIOAxesOrder(); 
     // List axisList = getParentArray().getAxes();
     int size = getParentArray().getAxes().size();
     String[] tags = new String[size];
     // String tag;

     for (int i = 0; i < size; i++) {
        // String axisId = ((AxisInterface) axisList.get(i)).getAxisId();
        // tags[i] = (String) tagHash.get(axisId);
        tags[i] = "d" + i;
     }

/*
     String axisId;
     String tempTag;

     int counter = stop;
     for (int i = 0; i < stop; i++) {
        counter--;
        tag = "d" + counter;  //the default tag
        //should it exist, we use whats in the tag hash
        //otherwise we go with the default above
        axisId = ((AxisInterface) axisList.get(i)).getAxisId();
        tempTag = (String) tagHash.get(axisId);
        if (tempTag!=null)
           tag = tempTag;
        tags[i] = tag;
     }
*/

     return tags;
   }

   private void updateAxisIdHashFromAxisTags() 
   {

      // these two are not necessarily in order
      // but we work on the theory that if one tag fails to find
      // an axis, all will. 
      String[] tags = getAxisTags();
      List axisList = parentArray.getAxes();
      for (int i = 0, size = tags.length; i < size; i++) 
      {
          // we only set this if doesnt already exist
          if (getAxisIdByTag(tags[i]) == null)
          {
             AxisInterface axisObj = (AxisInterface) axisList.get(i);
             if (axisObj != null) 
                setAxisTag(tags[i], axisObj.getAxisId());
             else 
             {
                Log.errorln("Error:"+this.getClass().toString()+" lacks an axis for the tag:"+tags[i]+" were the number of axes improperly changed within the array?");
                System.exit(-1); // should throw an error
             }
          }
      }
   }


   //
   //Other PUBLIC Methods
   //


   //
   //PROTECTED Methods
   //

   protected void specificIOStyleToXDF( Writer outputWriter, String indent)
   throws java.io.IOException
   {

      updateAxisIdHashFromAxisTags();

      boolean niceOutput = Specification.getInstance().isPrettyXDFOutput();

      //write out the tags info
      String[] tags = getAxisTags();

/*
      List axisList = parentArray.getAxes();
      List axisList = getIOAxesOrder(); // actually, for tagged data order isnt important
                                        // in the least, its tagToAxis that is important.
      int numberOfAxes = axisList.size();   
*/
      if (niceOutput) {
            outputWriter.write( Constants.NEW_LINE);
            outputWriter.write( indent);
      }

      outputWriter.write("<taggedStyle>");
      String moreIndent = indent + Specification.getInstance().getPrettyXDFOutputIndentation();

      for (int i = 0, size = tags.length; i < size; i++) {
         String tag = tags[i];
         String axisId = (String) axisIdHash.get(tag); // ((AxisInterface) axisList.get(i)).getAxisId();
         if (niceOutput) {
            outputWriter.write( Constants.NEW_LINE);
            outputWriter.write( moreIndent);
         }
         outputWriter.write( "<" + TagToAxisNodeName + " axisIdRef=\"");
         writeOutAttribute(outputWriter, axisId);
         outputWriter.write( "\"");
         outputWriter.write( " tag=\"");
         writeOutAttribute(outputWriter, tag);
         outputWriter.write( "\"/>");
      }

      if (niceOutput) {
         outputWriter.write( Constants.NEW_LINE);
         outputWriter.write( indent);
      }

      outputWriter.write("</taggedStyle>");

      // wrap up newline 
      if (niceOutput) {
         outputWriter.write( Constants.NEW_LINE);
      }

   }


   /** Remove an axis tag from the tag hash. This should be PROTECTED
    * and occur only when axis is being removed (ie available to array obj only).
    * @param axisId - axisId to be removed
    * @return tag if successful, null if not
    */
   protected String removeAxisTag (String axisId) {
      String tagName = (String) tagHash.remove(axisId);
      if (tagName != null) 
         axisIdHash.remove(tagName);

      needToCheckAxisOrder = true;

      return tagName;
   }

   //
   // Private Methods
   //

   private void resetIOAxesOrder () {

      List parentAxes = parentArray.getAxes();

      if (tagHash.size() != parentAxes.size()) {
          // not enough in tagHash to do this yet
          return;
      } else {
      
         Hashtable parentAxis = mapAxesToIds(parentAxes);

         synchronized (axesIOList) {
            axesIOList = Collections.synchronizedList(new ArrayList());
            for (int i = 0, size = parentAxes.size(); i < size; i++) {
               String tagName = "d" + i;
               if (axisIdHash.containsKey(tagName)) {
                  String axisId = (String) axisIdHash.get(tagName);
                  AxisInterface axisObj = (AxisInterface) parentAxis.get(axisId);
                  axesIOList.add(0,axisObj);
               } else {
                  Log.errorln("Internal Error: Cant find tag:"+tagName+", in internal hash as expected. Bad choice of AxisTag earlier?");
                  return; 
               }
            }
         }
      }
   }

   private Hashtable mapAxesToIds (List axes) 
   {

       Hashtable table = new Hashtable();
       Iterator iter = axes.iterator();
       while (iter.hasNext()) 
       {
          AxisInterface axisObj = (AxisInterface) iter.next(); 
          table.put(axisObj.getAxisId(), axisObj);
       }
       return table;
   }

}

