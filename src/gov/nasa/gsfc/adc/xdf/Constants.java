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
  public static final String BOOLEAN_TYPE = "Boolean";
  //store the enum list of XMLattributeTypes
  public static final List XMLATTRIBUTE_TYPE_LIST = new ArrayList();
  static {
    XMLATTRIBUTE_TYPE_LIST.add(STRING_TYPE);
    XMLATTRIBUTE_TYPE_LIST.add(LIST_TYPE);
    XMLATTRIBUTE_TYPE_LIST.add(OBJECT_TYPE );
    XMLATTRIBUTE_TYPE_LIST.add(NUMBER_TYPE);
    XMLATTRIBUTE_TYPE_LIST.add(BOOLEAN_TYPE);
  }

  public static final String NEW_LINE = System.getProperty("line.separator");
  public static final int INIT_ATTRIBUTE_HASH_SIZE = 20;

  public static final String BIG_ENDIAN = "BigEndian";
  public static final String LITTLE_ENDIAN = "LittleEndian";
  //store the enum list of endians
  public static final List ENDIANS_LIST =  new ArrayList();
  static {
    ENDIANS_LIST.add(BIG_ENDIAN);
    ENDIANS_LIST.add(LITTLE_ENDIAN);
  }

  public static final String INTEGER_TYPE_DECIMAL = "decimal";
  public static final String INTEGER_TYPE_HEX = "hexadecimal";
  public static final String INTEGER_TYPE_OCTAL = "octal";
  //store the enum list of integer types
  public static final List INTEGER_TYPE_LIST = new ArrayList();
  static {
    INTEGER_TYPE_LIST.add(INTEGER_TYPE_DECIMAL);
    INTEGER_TYPE_LIST.add(INTEGER_TYPE_HEX);
    INTEGER_TYPE_LIST.add(INTEGER_TYPE_OCTAL);
  }

  public static final String UTF_8 = "UTF-8";
  public static final String UTF_16 = "UTF-16";
  public static final String ISO_8859_1 = "ISO-8859-1";
  public static final String ANSI = "ANSI";
  //store the enum list of encodings
  public static final List  ENCODINGS_LIST = new ArrayList();
  static {
    ENCODINGS_LIST.add(UTF_8);
    ENCODINGS_LIST.add(UTF_16);
    ENCODINGS_LIST.add(ISO_8859_1 );
    ENCODINGS_LIST.add(ANSI);
  }

}
