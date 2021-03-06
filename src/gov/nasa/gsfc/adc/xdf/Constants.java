

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


// Hmm. this file amounts to a header file in C. Might be better to put
// these various things in the objects where they are used. 

/**
 * stores constants
 * @version $Revision$
 */

public abstract class Constants {

  /** The version of XML that will be output from a toXML* method call.
  */
  public static final String XML_SPEC_VERSION = "1.0";

  /** The root node name for any XDF document. The root node is an XDF
      node.
  */
  public static final String XDF_ROOT_NODE_NAME = "XDF";

  /** The name of the relevant version of XDF DTD file for this package.
  */
  public static final String XDF_DTD_NAME = "XDF_018.dtd";

  /** The name of the Attribute which is written out as PCDATA rather than as
      a node attribute (String/Number type Attributes) or child node (Object
      and List type Attributes). At this time only String-type Attributes
      should be named 'value' (yes, it would be an interesting experiment to call
      an Object-type Attribute 'value'!).
  */
  public static final String PCDATA_ATTRIBUTE = "value";

  public static final String XDF_NOTATION_NAME = "xdf";
 
  public static final String XDF_NOTATION_PUBLICID = "application/xdf";

  /** Due to the limitations of DTD's only a limited number of dimensions are supported 
      for tagged data.
   */
  public static final int MAX_TAGGED_DIMENSIONS = 10;

  public static final String STRING_TYPE = "String";
  public static final String LIST_TYPE   = "List";
  public static final String OBJECT_TYPE = "Object";
  public static final String INTEGER_TYPE = "Integer";
  public static final String DOUBLE_TYPE  = "Double";

  // allowable roles for the relation node
  public static final String[] RELATION_ROLE_LIST = { 
                                                       "precision" , "positiveError", "negativeError" , 
                                                       "error" , "sensitivity" , "quality", "weight" , 
                                                       "reference" , "noteMark"
                                                    };

  //store the enum list of XMLattributeTypes
  public static final String[] XMLATTRIBUTE_TYPE_LIST = {
                                           STRING_TYPE, LIST_TYPE, OBJECT_TYPE, 
                                           INTEGER_TYPE,  DOUBLE_TYPE 
                                                        };

  public static final String NEW_LINE = System.getProperty("line.separator");
  public static final int INIT_ATTRIBUTE_HASH_SIZE = 5;

  public static final String BIG_ENDIAN = "BigEndian";
  public static final String LITTLE_ENDIAN = "LittleEndian";

  //store the enum list of endians
  public static final String[] ENDIANS_LIST = { BIG_ENDIAN, LITTLE_ENDIAN };

  public static final String INTEGER_TYPE_DECIMAL = "decimal";
  public static final String INTEGER_TYPE_HEX = "hexadecimal";
  public static final String INTEGER_TYPE_OCTAL = "octal";
  //store the enum list of integer types
  public static final String[] INTEGER_TYPE_LIST = { INTEGER_TYPE_DECIMAL, 
                                                     INTEGER_TYPE_HEX, 
                                                     INTEGER_TYPE_OCTAL };

  /* what bits are allowed in floating point numbers */
  public static final int[] FLOATING_POINT_BITS_LIST = { 32, 64 }; 

  /* what bits are allowed in binary integer numbers */
  public static final int[] INTEGER_BITS_LIST = {  4, 16, 32, 64 };

  public static final String IO_ENCODING_UTF_8 = "UTF-8";
  public static final String IO_ENCODING_UTF_16 = "UTF-16";
  public static final String IO_ENCODING_ISO_8859_1 = "ISO-8859-1";
  public static final String IO_ENCODING_ANSI = "ANSI";
  //store the enum list of encodings
  public static final String[] IO_ENCODINGS_LIST = { IO_ENCODING_UTF_8, IO_ENCODING_UTF_16,
                                                     IO_ENCODING_ISO_8859_1, IO_ENCODING_ANSI };

  public static final String DATATYPE_INTEGER = "integer";
  public static final String DATATYPE_FIXED = "fixed";
  public static final String DATATYPE_STRING = "string";
  public static final String DATATYPE_URL = "url";
  public static final String[] DATATYPE_LIST = { DATATYPE_INTEGER, DATATYPE_FIXED,
                                                 DATATYPE_STRING, DATATYPE_URL };

  public static final String DATA_ENCODING_UUENCODED = "uuencoded";
  public static final String DATA_ENCODING_BASE64 = "base64";
  public static final String[] DATA_ENCODING_LIST = { DATA_ENCODING_UUENCODED, DATA_ENCODING_BASE64 };

  public static final String DATA_COMPRESSION_ZIP = "zip";
  public static final String DATA_COMPRESSION_GZIP = "gzip";
  public static final String DATA_COMPRESSION_BZIP2 = "bzip2";
  public static final String DATA_COMPRESSION_XMILL = "XMILL";
  public static final String DATA_COMPRESSION_COMPRESS = "compress";

  public static final String[] DATA_COMPRESSION_LIST = { DATA_COMPRESSION_ZIP, DATA_COMPRESSION_GZIP, 
                                                         DATA_COMPRESSION_BZIP2, DATA_COMPRESSION_XMILL, 
                                                         DATA_COMPRESSION_COMPRESS };

  public static final String VALUE_INEQUALITY_LESS_THAN = "lessThan";
  public static final String VALUE_INEQUALITY_LESS_THAN_OR_EQUAL = "lessThanOrEqual";
  public static final String VALUE_INEQUALITY_GREATER_THAN = "greaterThan";
  public static final String VALUE_INEQUALITY_GREATER_THAN_OR_EQUAL = "greaterThanOrEqual";

  public static final String[] VALUE_INEQUALITY_LIST = { VALUE_INEQUALITY_LESS_THAN,
                                                         VALUE_INEQUALITY_LESS_THAN_OR_EQUAL,
                                                         VALUE_INEQUALITY_GREATER_THAN,
                                                         VALUE_INEQUALITY_GREATER_THAN_OR_EQUAL 
                                                       };

   public static final String VALUE_SPECIAL_INFINITE = "infinite";
   public static final String VALUE_SPECIAL_INFINITE_NEGATIVE = "infiniteNegative";
   public static final String VALUE_SPECIAL_NODATA= "noData";
   public static final String VALUE_SPECIAL_NOTANUMBER = "notANumber";
   public static final String VALUE_SPECIAL_OVERFLOW = "overflow";
   public static final String VALUE_SPECIAL_UNDERFLOW = "underflow";

   public static final String[] VALUE_SPECIAL_LIST = { VALUE_SPECIAL_INFINITE,  VALUE_SPECIAL_INFINITE_NEGATIVE,
                                                       VALUE_SPECIAL_NODATA, VALUE_SPECIAL_NOTANUMBER,
                                                       VALUE_SPECIAL_UNDERFLOW, VALUE_SPECIAL_OVERFLOW
                                                     };

   public static final int VALUELIST_SIZE = 0;
   public static final int VALUELIST_STEP = 1;
   public static final int VALUELIST_START = 0;
   public static final String VALUELIST_REPEATABLE = "no";
   public static final String VALUELIST_DELIMITER = " ";


}

