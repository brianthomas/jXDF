
// XDF ValueListAlgorithm Class
// CVS $Id$

// ValueListAlgorithm.java Copyright (C) 2001 Brian Thomas,
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
import java.io.StringWriter;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Create a list of values from a simple (linear) algorithm. 
  * The ValueList object may be then passed on and used by other objects
  * to populate the list of values they hold.
  * The formula for the creation of new Value objects is as follows:
  * currentValue = currentStep * stepValue + startValue. The 
  * size parameter determines how many values to enter into the
  * object. A desirable feature of using the ValueList object is that it result in
  * a more compact format for describing the values so added to other objects
  * when they are written out using the toXMLOutputStream method.
  */
public class ValueListAlgorithm implements ValueListInterface,Cloneable {

   // Fields
   private int valueListStart = Constants.VALUELIST_START;
   private int valueListSize = Constants.VALUELIST_SIZE;
   private int valueListStep = Constants.VALUELIST_STEP;

   private List values;

   private String valueListInfinite;
   private String valueListInfiniteNegative;
   private String valueListNoData;
   private String valueListNotANumber;
   private String valueListUnderflow;
   private String valueListOverflow;

   // Constructors
   /** Construct a list of values from an algorithm.
    */
   public ValueListAlgorithm (int start, int step, int size) {

      setStart(start);
      setStep(step);
      setSize(size);

      initValuesFromParams();

   }

   public ValueListAlgorithm (int start, int step, int size,
                                     String noDataValue,
                                     String infiniteValue,
                                     String infiniteNegativeValue,
                                     String notANumberValue,
                                     String overflowValue,
                                     String underflowValue ) 
  {

      setStart(start);
      setStep(step);
      setSize(size);

      setNoDataValue(noDataValue);
      setNotANumberValue(notANumberValue);
      setInfiniteValue(infiniteValue);
      setInfiniteNegativeValue(infiniteNegativeValue);
      setUnderflowValue(underflowValue);
      setOverflowValue(overflowValue);

      initValuesFromParams();

  }

  // 
  // Public Methods
  //

// accessor methods
   public int getStart ( ) { return valueListStart; }
   public void setStart (int value) { valueListStart = value; }

   public int getStep( ) { return valueListStep; }
   public void setStep (int value) { valueListStep = value; }

   public int getSize( ) { return valueListSize; }
   public void setSize (int value) { valueListSize = value; }

   public String getNoDataValue () { return valueListNoData; }
   public void setNoDataValue(String value) { valueListNoData = value; }

   public String getInfiniteValue () { return valueListInfinite; }
   public void setInfiniteValue (String value) { valueListInfinite = value; }

   public String getInfiniteNegativeValue () { return valueListInfiniteNegative; }
   public void setInfiniteNegativeValue (String value) { valueListInfiniteNegative = value; }

   public String getNotANumberValue () { return valueListNotANumber; }
   public void setNotANumberValue(String value) { valueListNotANumber = value; }

   public String getOverflowValue () { return valueListOverflow; }
   public void setOverflowValue (String value) { valueListOverflow= value; }

   public String getUnderflowValue () { return valueListUnderflow; }
   public void setUnderflowValue (String value) { valueListUnderflow= value; }



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

   public String toXMLString ()  
   {

     // hurm. Cant figure out how to use BufferedWriter here. fooey.
     Writer outputWriter = (Writer) new StringWriter();
     try {
        // we use this so that newline *isnt* appended onto the last element node
        basicXMLWriter(outputWriter, "", false, null, null);
     } catch (java.io.IOException e) {
        // weird. Out of memorY?
        Log.errorln("Cant got IOException for toXMLWriter() method within toXMLString().");
        Log.printStackTrace(e);
     }

     return outputWriter.toString();

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

      basicXMLWriter(outputWriter, indent, dontCloseNode, newNodeNameString, noChildObjectNodeName);
      if (Specification.getInstance().isPrettyXDFOutput()) //  && nodeNameString != null)
          outputWriter.write(Constants.NEW_LINE);
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
         outputWriter.write(indent); // indent node if desired

      outputWriter.write("<valueList start=\""+valueListStart+"\" step=\""+valueListStep+"\" size=\""+valueListSize+"\"");
      if (valueListNoData != null) outputWriter.write(" noDataValue=\""+valueListNoData+"\"");
      if (valueListInfinite != null) outputWriter.write(" infiniteValue=\""+valueListInfinite+"\"");
      if (valueListInfiniteNegative != null) outputWriter.write(" infiniteNegaiveValue=\""+valueListInfinite+"\"");
      if (valueListNotANumber != null) outputWriter.write(" notANumberValue=\""+valueListNotANumber+"\"");
      if (valueListUnderflow != null) outputWriter.write(" underflowValue=\""+valueListUnderflow+"\"");
      if (valueListOverflow != null) outputWriter.write(" overflowValue=\""+valueListOverflow+"\"");
      outputWriter.write("/>");

/*
      if (Specification.getInstance().isPrettyXDFOutput())
          outputWriter.write(Constants.NEW_LINE);
*/

      return "valueList";
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   // 
   // Private Methods
   //

   private void initValuesFromParams()
   {

      values = Collections.synchronizedList(new ArrayList());

      // now populate values list
      int currentValue = valueListStart;
      int step = valueListStep;
      int size = valueListSize;
      for(int i = 0; i < size; i++) {
         Value thisValue = new Value(currentValue);
         currentValue += step;
         values.add(thisValue);
      }
   }

}

