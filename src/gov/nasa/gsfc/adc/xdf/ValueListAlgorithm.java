
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

      setNoData(noDataValue);
      setNotANumber(notANumberValue);
      setInfinite(infiniteValue);
      setInfiniteNegative(infiniteNegativeValue);
      setUnderflow(underflowValue);
      setOverflow(overflowValue);

      initValuesFromParams();

  }

  // 
  // Public Methods
  //
/*
   public int getStart ( ) { return valueListStart; }
   public int getStep( ) { return valueListStep; }
   public int getSize( ) { return valueListSize; }
*/

   public List getValues () { return values; }

   public void toXMLOutputStream (OutputStream outputstream, String indent)
   throws java.io.IOException
   {

      if (Specification.getInstance().isPrettyXDFOutput())
         writeOut(outputstream, indent); // indent node if desired

      writeOut(outputstream, "<valueList start=\""+valueListStart+"\" step=\""+valueListStep+"\" size=\""+valueListSize+"\"");
      if (valueListNoData != null) writeOut(outputstream, " noDataValue=\""+valueListNoData+"\"");
      if (valueListInfinite != null) writeOut(outputstream, " infiniteValue=\""+valueListInfinite+"\"");
      if (valueListInfiniteNegative != null) writeOut(outputstream, " infiniteNegaiveValue=\""+valueListInfinite+"\"");
      if (valueListNotANumber != null) writeOut(outputstream, " notANumberValue=\""+valueListNotANumber+"\"");
      if (valueListUnderflow != null) writeOut(outputstream, " underflowValue=\""+valueListUnderflow+"\"");
      if (valueListOverflow != null) writeOut(outputstream, " overflowValue=\""+valueListOverflow+"\"");
      writeOut(outputstream, "/>");

      if (Specification.getInstance().isPrettyXDFOutput())
          writeOut(outputstream, Constants.NEW_LINE);
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   // 
   // Private Methods
   //

   private void setInfinite (String value) { valueListInfinite = value; }
   private void setInfiniteNegative (String value) { valueListInfiniteNegative = value; }
   private void setUnderflow (String value) { valueListUnderflow= value; }
   private void setOverflow (String value) { valueListOverflow= value; }
   private void setNoData(String value) { valueListNoData = value; }
   private void setNotANumber(String value) { valueListNotANumber = value; }
   private void setStart (int value) { valueListStart = value; }
   private void setStep (int value) { valueListStep = value; }
   private void setSize (int value) { valueListSize = value; }

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

   private void writeOut ( OutputStream outputstream, String msg )
   throws java.io.IOException
   {
      outputstream.write(msg.getBytes());
   }


}

/* Modification History:
 *
 * $Log$
 * Revision 1.1  2001/07/11 22:40:32  thomas
 * Initial Version
 *
 *
 */

