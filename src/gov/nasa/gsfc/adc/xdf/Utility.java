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
      List endianList = Constants.ENDIANS_LIST;
      for (int i = 0; i < endianList.size(); i++) {
        if (strEndian.equals(endianList.get(i)))
          return true;
      }

      return false;
    }

    public static boolean isValidIntegerType(String strIntegerType) {
      List integerTypeList = Constants.INTEGER_TYPE_LIST;
      for (int i = 0; i < integerTypeList.size(); i++) {
        if (strIntegerType.equals(integerTypeList.get(i)))
          return true;
      }

      return false;
    }

    public static boolean isValidXMLAttributeType(String strAttributeType) {
      List attributeTypeList = Constants.XMLATTRIBUTE_TYPE_LIST;
      for (int i = 0; i< attributeTypeList.size(); i++) {
        if (strAttributeType.equals(attributeTypeList.get(i)))
          return true;
      }
      return false;
    }

  }  //end of Utility class

/* Modification History:
 *
 * $Log$
 * Revision 1.1  2000/10/16 14:52:41  kelly
 * create the class for util routines.  --k.z.
 *
 */