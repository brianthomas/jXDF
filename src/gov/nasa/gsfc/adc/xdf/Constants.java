// XDF Constants Class
// CVS $Id$
package gov.nasa.gsfc.adc.xdf;
import java.util.*;

/**
 * Constants.java:
 * @version $Revision$
 */

public abstract class Constants {

  public static final String STRING_TYPE = "String";
  public static final String LIST_TYPE   = "List";
  public static final String OBJECT_TYPE = "Object";
  public static final String NUMBER_TYPE = "Number";
  public static final String STRING_OR_NUMBER_TYPE = "StringOrNumber";
  //store the enum list of XMLattributeTypes
  public static final String[] XMLATTRIBUTE_TYPE_LIST = new String[5];
  static {
    XMLATTRIBUTE_TYPE_LIST[0]=STRING_TYPE;
    XMLATTRIBUTE_TYPE_LIST[1]= LIST_TYPE;
    XMLATTRIBUTE_TYPE_LIST[2] = OBJECT_TYPE ;
    XMLATTRIBUTE_TYPE_LIST[3] = NUMBER_TYPE;
    XMLATTRIBUTE_TYPE_LIST [4] = STRING_OR_NUMBER_TYPE;
  }

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

  public static final String UTF_8 = "UTF-8";
  public static final String UTF_16 = "UTF-16";
  public static final String ISO_8859_1 = "ISO-8859-1";
  public static final String ANSI = "ANSI";
  //store the enum list of encodings
  public static final String[] ENCODINGS_LIST = new String[4];
  static {
    ENCODINGS_LIST[0]=UTF_8;
    ENCODINGS_LIST[1]=UTF_16;
    ENCODINGS_LIST[2]=ISO_8859_1 ;
    ENCODINGS_LIST[3]=ANSI;
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




}
