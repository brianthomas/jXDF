
// XDF ValueListDelimitedList Class
// CVS $Id$

// ValueListDelimitedList.java Copyright (C) 2001 Brian Thomas,
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
import java.util.Iterator;
import java.util.List;

/** Create a list of values from the passed List of Value objects.
  * The ValueList object may be then passed on and used by other objects
  * to populate the list of values they hold.
  * A desirable feature of using the ValueList object is that it result in
  * a more compact format for describing the values so added to other objects
  * when they are written out using the toXMLOutputStream method.
  */

   // internal valueList class for hold algorithm style 
public class ValueListDelimitedList implements ValueListInterface,Cloneable {

   // needed for the algorithm description
   private String valueListDelimiter = Constants.VALUELIST_DELIMITER;
   private List values = new ArrayList();

   private String valueListInfinite;
   private String valueListInfiniteNegative;
   private String valueListNoData;
   private String valueListNotANumber;
   private String valueListUnderflow;
   private String valueListOverflow;

   /** Constructs a valueList object with Values in passed List.
       Care should be taken that none of the Value objects are set
       to the same sequence of characters as the default delimiter.
    */
   public ValueListDelimitedList (List values) {
      setValues(values);
   }

   /** Constructs a valueList object with Values in passed List.
       Care should be taken that none of the Value objects are set
       to the same sequence of characters as the passed delimiter.
    */
   public ValueListDelimitedList (List values, String delimiter) {

      if (delimiter == null) {
         Log.errorln("ERROR: in ValueListDelimitedList() delimiter string can't be null.");
         System.exit(-1);
      }

      setDelimiter(delimiter);
      setValues(values);
   }

   /** Constructs a valueList object with Values in passed List.
       Care should be taken that none of the Value objects are set
       to the same sequence of characters as the passed delimiter.
    */
   public ValueListDelimitedList(List values, String delimiter,
                              String noDataValue,
                              String infiniteValue,
                              String infiniteNegativeValue,
                              String notANumberValue,
                              String overflowValue,
                              String underflowValue )
   {

      if (delimiter == null) {
         Log.errorln("ERROR: in ValueListDelimitedList() delimiter string can't be null.");
         System.exit(-1);      
      }

      setDelimiter(delimiter);

      setNoData(noDataValue);
      setNotANumber(notANumberValue);
      setInfinite(infiniteValue);
      setInfiniteNegative(infiniteNegativeValue);
      setUnderflow(underflowValue);
      setOverflow(overflowValue);

      setValues(values);

   }

   // 
   // Public Methods
   //
   public List getValues () { return values; }

   public void toXMLOutputStream (OutputStream outputstream, String indent)
   throws java.io.IOException
   {

      Writer outputWriter = new BufferedWriter(new OutputStreamWriter(outputstream));
      toXMLWriter (outputWriter, indent, false, null, null);

      // this *shouldnt* be needed, but tests with both Java 1.2.2 and 1.3.0
      // on SUN and Linux platforms show that it is. Hopefully we can remove
      // this in the future.
      outputWriter.flush();

   }

   public void toXMLWriter (
                                Writer outputWriter,
                                String indent
                           )
   throws java.io.IOException
   {
      toXMLWriter (outputWriter, indent, false, null, null);
   }

   public void toXMLWriter (
                                Writer outputWriter,
                                String indent,
                                boolean dontCloseNode,
                                String newNodeNameString,
                                String noChildObjectNodeName
                             )

   throws java.io.IOException
   {

      if (Specification.getInstance().isPrettyXDFOutput())
         outputWriter.write(indent); // indent node if desired

      // no need to have repeatable set to 'yes' would just waste space even if we used this functionality.
      outputWriter.write("<valueList delimiter=\""+valueListDelimiter+"\" repeatable=\"no\"");
      if (valueListNoData != null) outputWriter.write( " noDataValue=\""+valueListNoData+"\"");
      if (valueListInfinite != null) outputWriter.write(" infiniteValue=\""+valueListInfinite+"\"");
      if (valueListInfiniteNegative != null) outputWriter.write(" infiniteNegaiveValue=\""+valueListInfiniteNegative+"\"");
      if (valueListNotANumber != null) outputWriter.write(" notANumberValue=\""+valueListNotANumber+"\"");
      if (valueListUnderflow != null) outputWriter.write(" underflowValue=\""+valueListUnderflow+"\"");
      if (valueListOverflow != null) outputWriter.write(" overflowValue=\""+valueListOverflow+"\"");
      outputWriter.write(">");

      Iterator iter = values.iterator();
      while (iter.hasNext()) {

         Value thisValue = (Value) iter.next();

         String specialValue = thisValue.getSpecial();
         if(specialValue != null) {
            if(specialValue.equals(Constants.VALUE_SPECIAL_INFINITE)) {
               doValuePrint (outputWriter, specialValue, valueListInfinite);
            } else if(specialValue.equals(Constants.VALUE_SPECIAL_INFINITE_NEGATIVE)) {
               doValuePrint (outputWriter, specialValue, valueListInfiniteNegative);
            } else if(specialValue.equals(Constants.VALUE_SPECIAL_NODATA)) {
               doValuePrint (outputWriter, specialValue, valueListNoData);
            } else if(specialValue.equals(Constants.VALUE_SPECIAL_NOTANUMBER)) {
               doValuePrint (outputWriter, specialValue, valueListNotANumber);
            } else if(specialValue.equals(Constants.VALUE_SPECIAL_UNDERFLOW)) {
               doValuePrint (outputWriter, specialValue, valueListUnderflow);
            } else if(specialValue.equals(Constants.VALUE_SPECIAL_OVERFLOW)) {
               doValuePrint (outputWriter, specialValue, valueListOverflow);
            }

         } else {
            outputWriter.write(thisValue.getValue());
         }

         if (iter.hasNext())
            outputWriter.write(valueListDelimiter);
      }
      outputWriter.write("</valueList>");
      if (Specification.getInstance().isPrettyXDFOutput())
          outputWriter.write(Constants.NEW_LINE);

   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   //
   // Private Methods
   //

   private void doValuePrint (Writer outputWriter, String specialValue, String value) 
   throws java.io.IOException
   {
      if (value != null) {
         outputWriter.write(value);
      } else {
         Log.errorln("Error: valueList doesnt have "+specialValue+" defined but value does. Ignoring value.");
      }
   }

   private void setValues (List valueList) { values = valueList; }
   private void setInfinite (String value) { valueListInfinite = value; }
   private void setInfiniteNegative (String value) { valueListInfiniteNegative = value; }
   private void setUnderflow (String value) { valueListUnderflow= value; }
   private void setOverflow (String value) { valueListOverflow= value; }
   private void setNoData(String value) { valueListNoData = value; }
   private void setNotANumber(String value) { valueListNotANumber = value; }
   private void setDelimiter (String value) { valueListDelimiter = value; }

}

/* Modification History:
 *
 * $Log$
 * Revision 1.3  2001/07/26 15:55:42  thomas
 * added flush()/close() statement to outputWriter object as
 * needed to get toXMLOutputStream to work properly.
 *
 * Revision 1.2  2001/07/12 17:53:15  thomas
 * minor bug fix, error handling incorrect in toXMLOutputStream
 *
 * Revision 1.1  2001/07/11 22:40:36  thomas
 * Initial Version
 *
 *
 */

