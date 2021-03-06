
// XDF Utility Class
// CVS $Id$

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

package gov.nasa.gsfc.adc.xdf;


/**
 * Shared (utility) routines.
 * @version $Revision$
 */

  public class Utility {
    public static boolean isValidEndian(String strEndian) {

      // tagged dataformat (which must be all ASCII) will need to have
      // the null value set here.
      // if (strEndian == null) return true;

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

    public static boolean isValidAttributeType(String strAttributeType) {
      String[] attributeTypeList = Constants.XMLATTRIBUTE_TYPE_LIST;
      int stop = attributeTypeList.length;
      for (int i = 0; i< stop; i++) {
        if (strAttributeType.equals(attributeTypeList[i]))
          return true;
      }
      return false;
    }

     public static boolean isValidIOEncoding(String strEncoding) {
      String[] encodingList = Constants.IO_ENCODINGS_LIST;
      int stop = encodingList.length;
      for (int i = 0; i< stop; i++) {
        if (strEncoding.equals(encodingList[i]))
          return true;
      }
      return false;
    }

    public static boolean isValidDatatype(String strDatatype) {

      if (strDatatype == null) return true;

      String[] dataTypeList = Constants.DATATYPE_LIST;
      int stop = dataTypeList.length;
      for (int i = 0; i < stop; i++) {
        if (strDatatype.equals(dataTypeList[i]))
          return true;
      }
      return false;
    }

    public static boolean isValidDataEncoding(String strDataEncoding) {
      String[] dataEncodingList = Constants.DATA_ENCODING_LIST;
      int stop = dataEncodingList.length;
      for (int i = 0; i < stop; i++) {
        if (strDataEncoding.equals(dataEncodingList[i]))
          return true;
      }
      return false;
    }

    public static boolean isValidRelationRole(String strRole) {
       String[] roleList = Constants.RELATION_ROLE_LIST;
       int stop = roleList.length;
       for (int i = 0; i < stop; i++) {
          if (strRole.equals(roleList[i])) return true;
       }
       return false;
    }

    public static boolean isValidDataCompression(String strDataCompression) {
      String[] dataCompressionList = Constants.DATA_COMPRESSION_LIST;
      int stop = dataCompressionList.length;
      for (int i = 0; i < stop; i++) {
        if (strDataCompression.equals(dataCompressionList[i]))
          return true;
      }
      return false;
    }

    public static boolean isValidFloatBits (int bits) {
      int[] bitList = Constants.FLOATING_POINT_BITS_LIST;
      int stop = bitList.length;
      for (int i = 0; i < stop; i++) {
         if (bits == bitList[i]) return true;
      }
      return false;
    }

    public static boolean isValidIntegerBits (int bits) {
      int[] bitList = Constants.INTEGER_BITS_LIST;
      int stop = bitList.length;
      for (int i = 0; i < stop; i++) {
         if (bits == bitList[i]) return true;
      }
      return false;
    }

    public static boolean isValidBinaryIntegerSigned (String strSigned) {

        if ( strSigned != null && 
                (strSigned.equals("yes") || strSigned.equals("no")) ) 
           return true;

        return false;
    }

    public static boolean isValidDataRepeatable (String strRepeatable) {
        if ( strRepeatable != null &&
                (strRepeatable.equals("yes") || strRepeatable.equals("no")) )
           return true;
        return false;
    }

    public static boolean isValidStandalone (String str) {

        if ( str != null &&
                (str.equals("yes") || str.equals("no")) )
           return true;

        return false;
    }

    public static boolean isValidValueSpecial(String strValueSpecial) {
      if (strValueSpecial == null) return true;
      String[] valueSpecialList = Constants.VALUE_SPECIAL_LIST;
      int stop = valueSpecialList.length;
      for (int i = 0; i < stop; i++) {
        if (strValueSpecial.equals(valueSpecialList[i]))
          return true;
      }
      return false;
    }

    public static boolean isValidValueInequality(String strValueInequality) {
      if (strValueInequality == null) return true;
      String[] valueInequalityList = Constants.VALUE_INEQUALITY_LIST;
      int stop = valueInequalityList.length;
      for (int i = 0; i < stop; i++) {
        if (valueInequalityList.equals(valueInequalityList[i]))
          return true;
      }
      return false;
    }

    public static boolean isValidNumberObject (Object numberObj) 
    {
       if (numberObj != null && numberObj instanceof Number) return true;
       return false;
    }


  }  //end of Utility class

