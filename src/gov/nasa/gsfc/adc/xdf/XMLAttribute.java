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
    public Object setAttribValue(Object objValue) {
      attribValue = objValue;
      return attribValue;
    }

    /** Set the type of value held by this XMLAttribute.
    */
    public String setAttribType(String strType) {
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
    public Object getAttribValue() {
       return attribValue;
    }

    /** Get the XMLAttribute value type.
    */
    public String getAttribType() {
       return attribType;
    }

    public Object clone () throws CloneNotSupportedException{

      XMLAttribute cloneObj = null;

      cloneObj = (XMLAttribute) super.clone();

      // need to deep copy the fields here too
      cloneObj.attribType = new String(this.attribType);
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
      if (attribValue instanceof Axis) {
        cloneObj.attribValue =((Axis) this.attribValue).clone();
      }
      if (attribValue instanceof List) {
        cloneObj.attribValue = Collections.synchronizedList(new ArrayList(((List) this.attribValue).size()));
        int stop = ((List)this.attribValue).size();
        for (int i = 0; i < stop; i ++) {
          Object obj = ((List)this.attribValue).get(i);
          if (obj instanceof Axis) {
            ((List)cloneObj.attribValue).add(((Axis) obj).clone());
          }
          else {
            if (obj instanceof Field) {
              ((List)cloneObj.attribValue).add(((Field) obj).clone());
            }
            else {
              if (obj instanceof FieldAxis) {
                ((List)cloneObj.attribValue).add(((FieldAxis) obj).clone());
              }
              else {
                if (obj instanceof Note) {
                  ((List)cloneObj.attribValue).add(((Note) obj).clone());
                }
                else {
                  if (obj instanceof Value) {
                    ((List)cloneObj.attribValue).add(((Value) obj).clone());
                  }
                }
              }

            }
          }

        }
      }

      return cloneObj;

    }

  } // end of internal Class XMLAttribute
