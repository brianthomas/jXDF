
// XDF Conversion Class
// CVS $Id$

// Conversion.java Copyright (C) 2003 Brian Thomas,
// XML Group/GSFC-NASA, Code 630.1, Greenbelt MD, 20771

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

/** 
<p>
 An XDF::Conversion object holds mathematical equations that describe 
 how stored data is to be converted to match the meta data description of it.
 Conversion allows expression of an algorithm to be applied to whatever "value"
 or set of values or data is held by the parent node. In the case of an array 
 or field it is the data. In the case of an axis, it applies to the meaning 
 of the axis indice values.
</p>
<p>
 One can string together a sequence of operations to be applied to the 
 stored data values. Applications would have the ability to optionally 
 apply this and to invert it when storing. The sense of the algorithm is
 that familiar to old HP users: RPN ("Reverse Polish Notation").
 For example the algorithm " 10 ^ (50 * ln (45 x + 89)) " would be written 
 in the XML as:
</p>
<pre>
 <conversion>
    <multiply>45</multiply>
    <add>89</add>
    <naturalLogarithm/>
    <multiply>50</multiply>
    <exponentOn>10</exponentOn>
 </conversion>
</pre>
<p>
 where each of the child nodes of conversion specifies a "component" of the 
 conversion. Each of these components are objects in their own right and are
 "owned" by the parent conversion.
</p>
<p>
 Convienient evaluation of any particular value by a conversion may be done using
 the "evaluate" method of the class, see the synopsis for an example.
</p> 
    @version $Revision$
 */
public class Conversion extends BaseObject {

  //
  //Fields
  //

  /* XML attribute names */
  private static final String COMPONENTLIST_XML_ATTRIBUTE_NAME = new String("componentList");

  //
  // Constructor
  //

  //no-arg constructor
  public Conversion ()
  {
     init();
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public Conversion ( Hashtable InitXDFAttributeTable )
  {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public void Conversion ( Hashtable InitXDFAttributeTable )
  {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  //
  //Get/Set methods
  //

  /**
   * @return the current *component list* attribute
   */
  public List getComponentList() {
    return (List) ((Attribute) attribHash.get(COMPONENTLIST_XML_ATTRIBUTE_NAME)).getAttribValue();
  } 
    
  /** A utility method that returns the list of components this object holds
   *
   */
  public List getComponents() {
    return getComponentList();
  } 

  //
  //Other PUBLIC Methods
  //

  /** Insert an Component object into the list of components held in this object
   * @param Component to be added
   * @return an Component object if successfull, null if not.
   */
  public boolean addComponent(ConversionComponentInterface component) {
    getComponentList().add(component);
    return true;
  }

   /** Remove an Component object the list of components held in
   * this object
   * @param what - Component to be removed
   * @return true if successful, false if not
   */
   public boolean removeComponent(ConversionComponentInterface what) {
     return removeFromList(what, getComponentList(), COMPONENTLIST_XML_ATTRIBUTE_NAME);
  }

  /**Remove an Component object at index from the list of components held in
   * this object
   * @param index - list index number of the Component to be removed
   * @return true if successful, false if not
   */
  public boolean removeComponent(int index) {
     return removeFromList(index, getComponentList(), COMPONENTLIST_XML_ATTRIBUTE_NAME);
  }

  /** Evaluate the passed value using the components held by this conversion. Components
      are evaluated in Reverse Polish Notation order.
      @return evaluated value.
   */
  public double evaluate (double value) {
     Double result = this.evaluate(new Double(value));
     return result.doubleValue();
  }

  /** Evaluate the passed value using the components held by this conversion. Components
      are evaluated in Reverse Polish Notation order.
      @return evaluated value.
   */
  public Double evaluate (Double value) {
     
     List components = getComponents();
     if (components.size() > 0) {
        Iterator iter = components.iterator();
        while (iter.hasNext())
        {
            ConversionComponentInterface component = (ConversionComponentInterface) iter.next();
            value = component.evaluate(value);
        }
     }

     return value;
  }

  //
  // Protected Methods
  //

  /** special method used by constructor methods to
      convienently build the XML attribute list for a given class.
   */
  protected void init()
  {


     super.init();

     classXDFNodeName = "conversion";

     // order matters! these are in *reverse* order of their
     // occurence in the XDF DTD
     attribOrder.add(0,COMPONENTLIST_XML_ATTRIBUTE_NAME);

     attribHash.put(COMPONENTLIST_XML_ATTRIBUTE_NAME, 
            new Attribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));


  }

}

