
package gov.nasa.gsfc.adc.xdf;

public class XMLAttribute {

  protected Object attribValue;
  protected String attribType;

  public XMLAttribute (Object objValue, String strType) {
    attribValue = objValue;
    attribType = strType;
  }

  public void setAttribValue(Object objValue) {
    attribValue = objValue;
  }

  public void setAttribType(String strType) {
    attribType = strType;
  }

  public Object getAttribValue() {
    return attribValue;
  }

  public String getAttribType() {
    return attribType;
  }

}

