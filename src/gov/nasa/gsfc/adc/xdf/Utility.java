// XDF Utility Class
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.util.*;

// Utility.java Copyright (C) 2000 Brian Thomas,
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



/**
 * Utility.java:constains util routines.
 * @version $Revision$
 */

  public class Utility {
    public static boolean isValidEndian(String strEndian) {
      String[] endianList = Constants.ENDIANS_LIST;
      int stop = endianList.length;
      for (int i = 0; i < stop; i++) {
        if (strEndian.equals(endianList[i]))
          return true;
      }

      return false;
    }

    public static boolean isValidIntegerType(String strIntegerType) {
      String[] integerTypeList = Constants.INTEGER_TYPE_LIST;
      int stop = integerTypeList.length;
      for (int i = 0; i < stop; i++) {
        if (strIntegerType.equals(integerTypeList[i]))
          return true;
      }

      return false;
    }

    public static boolean isValidXMLAttributeType(String strAttributeType) {
      String[] attributeTypeList = Constants.XMLATTRIBUTE_TYPE_LIST;
      int stop = attributeTypeList.length;
      for (int i = 0; i< stop; i++) {
        if (strAttributeType.equals(attributeTypeList[i]))
          return true;
      }
      return false;
    }

     public static boolean isValidEncoding(String strEncoding) {
      String[] encodingList = Constants.ENCODINGS_LIST;
      int stop = encodingList.length;
      for (int i = 0; i< stop; i++) {
        if (strEncoding.equals(encodingList[i]))
          return true;
      }
      return false;
    }

    public static boolean isValidDatatype(String strDatatype) {
      String[] dataTypeList = Constants.DATATYPE_LIST;
      int stop = dataTypeList.length;
      for (int i = 0; i < stop; i++) {
        if (strDatatype.equals(dataTypeList[i]))
          return true;
      }
      return false;
    }


  }  //end of Utility class

/* Modification History:
 *
 * $Log$
 * Revision 1.3  2000/10/26 20:11:27  kelly
 * changes related to Constants are made
 *
 * Revision 1.2  2000/10/18 18:48:34  kelly
 * added isValidEncoding method.  -k.z.
 *
 * Revision 1.1  2000/10/16 14:52:41  kelly
 * create the class for util routines.  --k.z.
 *
 */