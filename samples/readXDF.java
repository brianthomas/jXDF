

import gov.nasa.gsfc.adc.xdf.Structure;
import gov.nasa.gsfc.adc.xdf.Reader;

import java.util.Hashtable;
import java.io.IOException;

public class readXDF {

  //public readXDF() { }

  public static void main(String[] argv) {

    // declare the structure
    Structure s = new Structure();
    Reader r = new Reader();

    r.setReaderStructureObj(s);

    // set a few attributes
    s.setName("First structure");
    s.setDescription("First descript");

    // this will cause the information in existing structure
    // to be lost
//    s.loadFromXDFFile("out.xml");

    // read in with the reader, it accretes new info into
    // existing structure without overriding it.
    try {
      r.parsefile(argv[0]);
    } catch (java.io.IOException e ) { }

    // output
    s.setPrettyXDFOutput(true);
    s.setPrettyXDFOutputIndentation("   ");
    s.toXMLOutputStream(System.out);

  }
}

