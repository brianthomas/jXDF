
import gov.nasa.gsfc.adc.xdf.XDF;
import gov.nasa.gsfc.adc.xdf.Specification;
import gov.nasa.gsfc.adc.xdf.Reader;
import gov.nasa.gsfc.adc.xdf.AxisInterface;
import gov.nasa.gsfc.adc.xdf.Array;
import gov.nasa.gsfc.adc.xdf.Locator;
import gov.nasa.gsfc.adc.xdf.AxisLocationOutOfBoundsException;
import gov.nasa.gsfc.adc.xdf.NoDataException;

import java.util.Hashtable;
import java.io.IOException;

public class readXDF {

  //public readXDF() { }

  public static void main(String[] argv) {

    // declare the structure
    XDF s = new XDF();
    Reader r = new Reader();

    r.setReaderXDFStructureObj(s);

    // set a few attributes
    s.setName("First structure");
    s.setDescription("First descript");

//    System.out.println("-----\nREAD IN STRUCTURE\n-----");

    // this will cause the information in existing structure
    // to be lost
//    s.loadFromXDFFile("out.xml");

    // change the parser to Xerces
    Specification.getInstance().setXMLParser("org.apache.xerces.parsers.SAXParser");

    // read in with the reader, it accretes new info into
    // existing structure without overriding it.
    try {
      r.parsefile(argv[0]);
    } catch (java.io.IOException e ) { }

    // output
 //   System.out.println("-----\nPRINT OUT STRUCTURE\n-----");

    // set the output specification
    Specification.getInstance().setPrettyXDFOutput(true);
    Specification.getInstance().setPrettyXDFOutputIndentation("  ");

    Array arrayObj = (Array) s.getArrayList().get(0);

    Locator locator = arrayObj.createLocator();
    try {
      locator.setAxisIndex((AxisInterface) arrayObj.getAxes().get(0), 1);
      locator.setAxisIndex((AxisInterface) arrayObj.getAxes().get(1), 2);
    } catch (AxisLocationOutOfBoundsException e) {
    }
 
    String value;
    try { 
      value = arrayObj.getStringData(locator);
    } catch (NoDataException e) {
      value = new String("");
    }

    try {
       s.toXMLOutputStream(System.out);
    } catch (java.io.IOException e ) { }

    System.out.println("Array 0 data cell (1,2):"+value);

  }
}


