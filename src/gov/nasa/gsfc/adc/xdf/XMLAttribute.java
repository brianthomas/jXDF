// XDF Parameter Class
// CVS $Id$
package gov.nasa.gsfc.adc.xdf;

/**
 * XMLAttribute.java:to store XML attributes of XDF
 * @author: Brian Thomas (thomas@adc.gsfc.nasa.gov)
 *          Kelly Zeng (kelly.zeng@commerceone.com)
 * @version $Revision$
 */

public class XMLAttribute {

  protected Object attribValue;
  protected String attribType;

  public XMLAttribute (Object objValue, String strType) {
    attribValue = objValue;
    attribType = strType;
  }

  public Object setAttribValue(Object objValue) {
    attribValue = objValue;
    return attribValue;
  }

  public String setAttribType(String strType) {
    attribType = strType;
    return attribType;
  }

  public Object getAttribValue() {
    return attribValue;
  }

  public String getAttribType() {
    return attribType;
  }

}

