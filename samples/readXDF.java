
import gov.nasa.gsfc.adc.xdf.*;

import java.util.List;
import java.util.Hashtable;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;

// a little example program showing some manipulation of XDF
// object after it has been read in.
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

//    System.err.println("-----\nREAD IN STRUCTURE\n-----");

    // this will cause the information in existing structure
    // to be lost
//    s.loadFromXDFFile("out.xml");

    // change the parser to Xerces
//    Specification.getInstance().setXMLParser("org.apache.xerces.parsers.SAXParser");

    // read in with the reader, it accretes new info into
    // existing structure without overriding it.
    try {
      r.parseFile(argv[0]);
    } catch (java.io.IOException e ) { 
      System.err.println("readXDF cant parse file:"+argv[0]);
    }

    // output
    System.err.println("-----\nPRINT OUT STRUCTURE\n-----");

    // set the output specification
    Specification.getInstance().setPrettyXDFOutput(true);
    Specification.getInstance().setPrettyXDFOutputIndentation("  ");

    try {
       s.toXMLOutputStream(System.out, "",false,null,null); // ok

 //      Writer myWriter = new BufferedWriter(new OutputStreamWriter(System.err));
       // s.toXMLWriter(myWriter,"",false,null,null); // ok

  //     myWriter.flush();

    } catch (IOException e ) {
      System.err.println("Cant print out structure");
    }

    // user may not want this part. Could core dump if XDF structure 
    // isnt matching what is expected here
    Array arrayObj = (Array) s.getArrayList().get(0);
    if(arrayObj.getAxes().size() == 1) {
      dump1DArray(arrayObj);
    } else if (arrayObj.getAxes().size() == 2) {
      dump2DArray(arrayObj);
    }

  }

  public static void dump1DArray (Array arrayObj) 
  {

    System.err.println("Axis 0 is :"+((AxisInterface) arrayObj.getAxes().get(0)).getAxisId());

    Locator locator = arrayObj.createLocator();
    // iterate thru first few values of axis 0
    for (int i =0; i<6; i++) {
      try {
         locator.setAxisIndex((AxisInterface) arrayObj.getAxes().get(0), i);
      } catch (AxisLocationOutOfBoundsException e) {
         System.err.println("Locator request out of bounds");
      }

      String value;
      try {
        value = arrayObj.getStringData(locator);
      } catch (NoDataException e) {
        value = new String("");
      }
      System.err.println("Array 0 data cell @("+i+"):"+value);
    }

  }

  public static void dump2DArray (Array arrayObj) 
  {
    
    System.err.println("Axis 0 is :"+((AxisInterface) arrayObj.getAxes().get(0)).getAxisId());
    System.err.println("Axis 1 is :"+((AxisInterface) arrayObj.getAxes().get(1)).getAxisId());

    Locator locator = arrayObj.createLocator();
    // iterate thru first few values of axis 1
    for (int i =0; i<6; i++) {
      try {
         locator.setAxisIndex((AxisInterface) arrayObj.getAxes().get(0), i);
         locator.setAxisIndex((AxisInterface) arrayObj.getAxes().get(1), 0);
      } catch (AxisLocationOutOfBoundsException e) {
         System.err.println("Locator request out of bounds");
      }

      String value;
      try {
        value = arrayObj.getStringData(locator);
      } catch (NoDataException e) {
        value = new String("");
      }
      System.err.println("Array 0 data cell @("+i+",0):"+value);
    }

  }

}


