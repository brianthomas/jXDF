
// XDF XMLAttribute Class
// CVS $Id$

// XMLAttribute.java Copyright (C) 2000 Brian Thomas,
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

  //
  // Internal Classes
  //

  /** Stores values of XML-based attributes of the XDF object.
      These attributes will be used to re-construct an XDF file/stream
      from the Java object.
  */
  public class XMLAttribute implements Cloneable {

    protected Object attribValue;
    protected String attribType;

    /** Constructor takes object reference and type.
    */
    // Shouldnt type be an emunerated list from the Constants class?
    // NOT just any arbitrary string can go here.
    public XMLAttribute (Object objValue, String strType) {
      attribValue = objValue;
      attribType = strType;
    }

    /** Set the value of this XMLAttribute.
    */
    public synchronized Object  setAttribValue(Object objValue) {
      attribValue = objValue;
      return attribValue;
    }

    /** Set the type of value held by this XMLAttribute.
    */
    public synchronized String setAttribType(String strType) {
      if ( !Utility.isValidXMLAttributeType(strType))
      {
        Log.error("Type not a defined constant for XMLAttribute");
        return null;
      }

      // ok, set it
      attribType = strType;
      return attribType;
    }

    /** Get the value of this XMLAttribute.
    */
    public synchronized Object  getAttribValue() {
       return attribValue;
    }

    /** Get the XMLAttribute value type.
    */
    public synchronized String  getAttribType() {
       return attribType;
    }

    public Object clone () throws CloneNotSupportedException{

      synchronized (this) {
        XMLAttribute cloneObj = null;cloneObj= (XMLAttribute) super.clone();
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

} // end of internal Class XMLAttribute

/* Modification History
 * 
 * $Log$
 * Revision 1.7  2000/11/08 20:25:34  thomas
 * Added header information/log mod history -b.t.
 *
 * 
 */

