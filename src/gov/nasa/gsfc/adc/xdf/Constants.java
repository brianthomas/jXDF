

// XDF Constants Class
// CVS $Id$

// Constants.java Copyright (C) 2000 Brian Thomas,
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
 * stores constants
 * @version $Revision$
 */

public abstract class Constants {

  public static final String STRING_TYPE = "String";
  public static final String LIST_TYPE   = "List";
  public static final String OBJECT_TYPE = "Object";
  public static final String INTEGER_TYPE = "Integer";
  public static final String DOUBLE_TYPE  = "Double";
  // public static final String STRING_OR_NUMBER_TYPE = "StringOrNumber";

  //store the enum list of XMLattributeTypes
  public static final String[] XMLATTRIBUTE_TYPE_LIST = new String[5];

  static {
    XMLATTRIBUTE_TYPE_LIST[0]=STRING_TYPE;
    XMLATTRIBUTE_TYPE_LIST[1]= LIST_TYPE;
    XMLATTRIBUTE_TYPE_LIST[2] = OBJECT_TYPE ;
    XMLATTRIBUTE_TYPE_LIST[3] = INTEGER_TYPE;
    XMLATTRIBUTE_TYPE_LIST[4] = DOUBLE_TYPE;
  }
    //XMLATTRIBUTE_TYPE_LIST[4] = STRING_OR_NUMBER_TYPE;

  public static final String NEW_LINE = System.getProperty("line.separator");
  public static final int INIT_ATTRIBUTE_HASH_SIZE = 20;

  public static final String BIG_ENDIAN = "BigEndian";
  public static final String LITTLE_ENDIAN = "LittleEndian";
  //store the enum list of endians
  public static final String[] ENDIANS_LIST =  new String[2];
  static {
    ENDIANS_LIST[0] = BIG_ENDIAN;
    ENDIANS_LIST[1] = LITTLE_ENDIAN;
  }

  public static final String INTEGER_TYPE_DECIMAL = "decimal";
  public static final String INTEGER_TYPE_HEX = "hexadecimal";
  public static final String INTEGER_TYPE_OCTAL = "octal";
  //store the enum list of integer types
  public static final String[] INTEGER_TYPE_LIST = new String[3];
  static {
    INTEGER_TYPE_LIST[0]=INTEGER_TYPE_DECIMAL;
    INTEGER_TYPE_LIST[1]=INTEGER_TYPE_HEX;
    INTEGER_TYPE_LIST[2]=INTEGER_TYPE_OCTAL;
  }

  public static final String IO_ENCODING_UTF_8 = "UTF-8";
  public static final String IO_ENCODING_UTF_16 = "UTF-16";
  public static final String IO_ENCODING_ISO_8859_1 = "ISO-8859-1";
  public static final String IO_ENCODING_ANSI = "ANSI";
  //store the enum list of encodings
  public static final String[] IO_ENCODINGS_LIST = new String[4];
  static {
    IO_ENCODINGS_LIST[0]=IO_ENCODING_UTF_8;
    IO_ENCODINGS_LIST[1]=IO_ENCODING_UTF_16;
    IO_ENCODINGS_LIST[2]=IO_ENCODING_ISO_8859_1 ;
    IO_ENCODINGS_LIST[3]=IO_ENCODING_ANSI;
  }

  public static final String DATATYPE_INTEGER = "integer";
  public static final String DATATYPE_FIXED = "fixed";
  public static final String DATATYPE_EXPONENTIAL = "expenential";
  public static final String DATATYPE_STRING = "string";
  public static final String DATATYPE_URL = "url";
  public static final String[] DATATYPE_LIST = new String[5];

  static {
    DATATYPE_LIST[0]=DATATYPE_INTEGER;
    DATATYPE_LIST[1]=DATATYPE_FIXED;
    DATATYPE_LIST[2]=DATATYPE_EXPONENTIAL;
    DATATYPE_LIST[3]=DATATYPE_STRING;
    DATATYPE_LIST[4]=DATATYPE_URL;
  }

   public static final String DATA_ENCODING_UUENCODED = "uuencoded";
   public static final String DATA_ENCODING_BASE64 = "base64";
   public static final String[] DATA_ENCODING_LIST = new String[2];
   static {
    DATA_ENCODING_LIST[0]=DATA_ENCODING_UUENCODED;
    DATA_ENCODING_LIST[1]=DATA_ENCODING_BASE64;
   }

   public static final String DATA_COMPRESSION_ZIP = "zip";
   public static final String DATA_COMPRESSION_GZIP = "gzip";
   public static final String DATA_COMPRESSION_BZIP2 = "bzip2";
   public static final String DATA_COMPRESSION_XMILL = "XMILL";
   public static final String DATA_COMPRESSION_COMPRESS = "compress";

   public static final String[] DATA_COMPRESSION_LIST = new String[5];
   static {
    DATA_COMPRESSION_LIST[0]=DATA_COMPRESSION_ZIP;
    DATA_COMPRESSION_LIST[1]=DATA_COMPRESSION_GZIP;
    DATA_COMPRESSION_LIST[2]=DATA_COMPRESSION_BZIP2;
    DATA_COMPRESSION_LIST[3]=DATA_COMPRESSION_XMILL;
    DATA_COMPRESSION_LIST[4]=DATA_COMPRESSION_COMPRESS;
   }

   public static final String VALUE_INEQUALITY_LESS_THAN = "lessThan";
   public static final String VALUE_INEQUALITY_LESS_THAN_OR_EQUAL = "lessThanOrEqual";
   public static final String VALUE_INEQUALITY_GREATER_THAN = "greaterThan";
   public static final String VALUE_INEQUALITY_GREATER_THAN_OR_EQUAL = "greaterThanOrEqual";

   public static final String[] VALUE_INEQUALITY_LIST = new String[4];
   static {
    VALUE_INEQUALITY_LIST[0]= VALUE_INEQUALITY_LESS_THAN;
    VALUE_INEQUALITY_LIST[1]= VALUE_INEQUALITY_LESS_THAN_OR_EQUAL ;
    VALUE_INEQUALITY_LIST[2]= VALUE_INEQUALITY_GREATER_THAN;
    VALUE_INEQUALITY_LIST[3]= VALUE_INEQUALITY_GREATER_THAN_OR_EQUAL;
   }

   public static final String VALUE_SPECIAL_INFINITE = "infinite";
   public static final String VALUE_SPECIAL_INFINITE_NEGATIVE = "infiniteNegative";
   public static final String VALUE_SPECIAL_NODATA= "noData";

   public static final String[] VALUE_SPECIAL_LIST = new String[3];
   static {
    VALUE_SPECIAL_LIST[0] = VALUE_SPECIAL_INFINITE;
    VALUE_SPECIAL_LIST[1] = VALUE_SPECIAL_INFINITE_NEGATIVE;
    VALUE_SPECIAL_LIST[2] = VALUE_SPECIAL_NODATA;
   }

}

/* Modification History
 * 
 * $Log$
 * Revision 1.10  2000/11/20 22:03:48  thomas
 * Split up XMLAttribute type NUMBER_TYPE into
 * INTEGER_TYPE and DOUBLE_TYPE. This allows for
 * some needed handling in the SaxDocHandler when
 * parsing data for the formatted read. Put prior NUMBER_TYPE
 * attributes into appropriate new category. -b.t.
 *
 * Revision 1.9  2000/11/16 19:53:03  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.8  2000/11/08 19:36:32  thomas
 * Trimmed import path; added GPL statement and Mod History. -b.t.
 *
 *
 */
