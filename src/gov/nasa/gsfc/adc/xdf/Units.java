// XDF Units Class
// CVS $Id$


package gov.nasa.gsfc.adc.xdf;
import java.util.*;

/**
 * Units.java:
 * @author: Brian Thomas (thomas@adc.gsfc.nasa.gov)
 *          Kelly Zeng (kelly.zeng@commerceone.com)
 * @version $Revision$
 */

 public class Units extends BaseObject {
  //
  //Fields
  //

  protected String XDFNodeName;
  //double check
  String unitDivideSymbol = "/";
  String classNoUnitChildNodeName = "unitless";

 //
  // Constructor and related methods
  //

  /** The no argument constructor.
   */
  public Units ()
  {
    init();
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public Units ( Hashtable InitXDFAttributeTable )
  {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  }


 /** init -- special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init()
  {

    classXDFNodeName = "units";
    XDFNodeName = "units";

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0,"unitList");
    attribOrder.add(0,"system");
    attribOrder.add(0,"factor");

    attribHash.put("unitList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("system", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("factor", new XMLAttribute(null, Constants.NUMBER_TYPE));
  }
  //
  //Get/Set Methods

  /**setXDFNodeName: change teh XDF node name for this object.
   *
   */
  public String setXDFNodeName(String strName) {
    XDFNodeName = strName;
    return XDFNodeName;
  }
  public Number setFactor (Number factor) {
    return (Number) ((XMLAttribute) attribHash.get("factors")).setAttribValue(factor);
  }

  public Number getFactor () {
    return (Number) ((XMLAttribute) attribHash.get("factor")).getAttribValue();
  }

  public String setSystem (String system) {
    return (String) ((XMLAttribute) attribHash.get("system")).setAttribValue(system);
  }

  public String getSystem () {
    return (String) ((XMLAttribute) attribHash.get("system")).getAttribValue();
  }

  public List setUnitList(List units) {
    return (List)((XMLAttribute) attribHash.get("unitList")).setAttribValue(units);
  }

  public List getUnitList() {
    return (List) ((XMLAttribute) attribHash.get("unitList")).getAttribValue();
  }

  public List getUnits() {
    return getUnitList();
  }

  //
  //Other PUBLIC Methods
  //

  /**addUnit
   * */

  public Unit addUnit(Unit unit) {
    getUnitList().add(unit);
    return unit;
  }

   /**removeUnit
   * pass in Unit
   */
   public boolean removeUnit(Unit what) {
     return removeFromList(what, getUnitList(), "unitList");
  }

  /**removeUnit
   * pass in index
   * function overload
   */
  public boolean removeUnit(int what) {
     return removeFromList(what, getUnitList(), "unitList");
  }



  //not done yet, 10/2/2000, k.z.
  public String value() {
    return null;
  }

  //not done yet, 10/2/2000, k.z.
  public void toXDFFileHandle() {
  }

 }
