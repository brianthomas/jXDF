
// XDF Attribute Class
// CVS $Id$

// Attribute.java Copyright (C) 2000 Brian Thomas,
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

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;


  /** Stores values of attributes for many classes in the XDF package.
      Attributes may be of string, list or hashtable type. When attributes
      are of string type, they act like XML attributes.
  */
  public class Attribute implements Cloneable {

    protected Object attribValue;
    protected String attribType;

    /** Constructor takes object reference and type.
    */
    // Shouldnt type be an emunerated list from the Constants class?
    // NOT just any arbitrary string can go here.
    public Attribute (Object objValue, String strType) {
      attribValue = objValue;
      attribType = strType;
    }

    /** Set the value of this Attribute.
    */
    public synchronized void setAttribValue (Object objValue) {
      attribValue = objValue;
    }

    /** Set the type of value held by this Attribute.
    */
    public synchronized void setAttribType (String strType) {
      if ( !Utility.isValidAttributeType(strType))
      {
        Log.error("Type not a defined constant for Attribute");
        return;
      }

      // ok, set it
      attribType = strType;

    }

    /** Get the value of this Attribute.
    */
    public synchronized Object  getAttribValue() {
       return attribValue;
    }

    /** Get the Attribute value type.
    */
    public synchronized String  getAttribType() {
       return attribType;
    }

    public Object clone () throws CloneNotSupportedException {

      synchronized (this) {
        Attribute cloneObj = null;cloneObj= (Attribute) super.clone();
          synchronized (cloneObj) {

          // need to deep copy the fields here too
          if (attribValue == null) {
            return cloneObj;
          }
          if (attribValue instanceof String ) {
            cloneObj.attribValue = new String((String) this.attribValue);
            return cloneObj;
         }
         if (attribValue instanceof Integer) {
            cloneObj.attribValue = new Integer(((Integer) this.attribValue).intValue());
            return cloneObj;
         }
         if (attribValue instanceof Double) {
          cloneObj.attribValue = new Double(((Double) this.attribValue).doubleValue());
          return cloneObj;
         }
         if (attribValue instanceof List) {
           cloneObj.attribValue = Collections.synchronizedList(new ArrayList(((List) this.attribValue).size()));
           int stop = ((List)this.attribValue).size();
           for (int i = 0; i < stop; i ++) {
              //List only contains child classes of BaseObject
              Object obj = ((List)this.attribValue).get(i);
              ((List)cloneObj.attribValue).add(((BaseObject) obj).clone());

            }
            return cloneObj;
         }
        //all other classes are child classes of BaseObject
        cloneObj.attribValue = ((BaseObject) this.attribValue).clone();
        return cloneObj;
      }
    }
  }

} // end of internal Class Attribute

/* Modification History
 * 
 * $Log$
 * Revision 1.1  2001/09/13 21:36:59  thomas
 * *** empty log message ***
 *
 * Revision 1.9  2000/11/09 23:26:48  kelly
 * fixed a little documentation.
 *
 * Revision 1.8  2000/11/08 22:30:11  thomas
 * Changed set methods to return void. -b.t.
 *
 * Revision 1.7  2000/11/08 20:25:34  thomas
 * Added header information/log mod history -b.t.
 *
 * 
 */

