
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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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
public class ValueListAlgorithm extends ValueList {

   //
   // Fields
   //
   private static final String ALGORITHMLIST_XML_ATTRIBUTE_NAME = new String("algorithmList");

   //
   // Constructors
   //

   /** the no-arg Constructor */
   public ValueListAlgorithm () {
      init();
   }

   /** utility constructor. Will create a valueList from a linear polynomial 
       with passed parameters.
    */
   public ValueListAlgorithm (int intercept, int slope, int nrofValues) 
   {
      init();
   }

   /**  This constructor takes a Java Hashtable as an initializer of
        the XML attributes of the object to be constructed. The
        Hashtable key/value pairs coorespond to the class XDF attribute
        names and their desired values.
    */
   public ValueListAlgorithm ( Hashtable InitXDFAttributeTable )
   {

     // init the XML attributes (to defaults)
     init();

     // init the value of selected XML attributes to HashTable values
     hashtableInitXDFAttributes(InitXDFAttributeTable);

   }

   // 
   // Public Methods
   //

   //
   // Accessor methods
   //


   /** get the list of algorithms held by this object
    */
   public List getAlgorithmList ()
   {
      return (List) ((Attribute) attribHash.get(ALGORITHMLIST_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   //
   // Other methods
   //

  /** Insert a Algorithm object into the list of algorithms held by this object.
       @param algorithm - Algorithm to be added
       @return a Algorithm object if successfull, null if not.
    */
   public boolean addAlgorithm (AlgorithmInterface algorithm )
   {
      getAlgorithmList().add(algorithm);
      reloadValuesFromAlgorithms();
      return true;
   }

   /** Remove a Algorithm object the list of algorithms held in
       this object
       @param Algorithm to be removed
       @return true if successful, false if not
    */
   public boolean removeAlgorithm(AlgorithmInterface what) {
      boolean status = removeFromList(what, getAlgorithmList(), ALGORITHMLIST_XML_ATTRIBUTE_NAME);
      reloadValuesFromAlgorithms();
      return status;
   }

   /** Remove an Algorithm object from the list of algorithms held in
       this object
       @param index -- list index number of the Algorithm object to be removed
       @return true if successful, false if not
    */
   public boolean removeAlgorithm(int index) {
      boolean status = removeFromList(index, getAlgorithmList(), ALGORITHMLIST_XML_ATTRIBUTE_NAME);
      reloadValuesFromAlgorithms();
      return status;
   }

   //
   // Protected Methods
   //

   protected void init()
   {

      super.init();

      classXDFNodeName = "valueListAlgorithm";

      // order matters! these are in *reverse* order of their
      // occurence in the XDF DTD
      attribOrder.add(0, ALGORITHMLIST_XML_ATTRIBUTE_NAME);

      attribHash.put(ALGORITHMLIST_XML_ATTRIBUTE_NAME, new Attribute(new Vector(), Constants.LIST_TYPE));

   }

   //
   // Private methods
   //

   private void reloadValuesFromAlgorithms() {

      resetValues();

      List myValues = new Vector();
      Iterator iter = getAlgorithmList().iterator();
      while (iter.hasNext())
      {
          AlgorithmInterface algorithm = (AlgorithmInterface) iter.next();
          List valuesToAdd = algorithm.getValues();
          Iterator vaiter = valuesToAdd.iterator();
          while (vaiter.hasNext())
          {
              Value nextValue = (Value) vaiter.next();
              myValues.add(nextValue);
          }
      }

      setValues(myValues);
   }

}

