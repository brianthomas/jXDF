
import gov.nasa.gsfc.adc.xdf.*;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;

public class loadXDF {

  //public loadXDF() { }
  private static final String line = "-";
  private static final String space = " ";
  private static final String indent = "  ";

  private static final int halfRowNumberWidth = 2;
  private static final int otherHalfRowNumberWidth = 2;
  private static final int rowNumberWidth = 5;

  public static void main(String[] argv) 
  {

    // declare the structure
    XDF s = new XDF();
    Reader r = new Reader();

    r.setReaderXDFStructureObj(s);

    // set a few attributes
    s.setName("First structure");
    s.setDescription("First descript");

    // change the parser to Xerces
    Specification.getInstance().setXMLParser("org.apache.xerces.parsers.SAXParser");

    // read in with the reader
    try {
       r.parsefile(argv[0]);
       System.out.println("STRUCTURE loaded ok");
    } catch (java.lang.NumberFormatException e ) { 
       System.err.println("Failed to load XDF from file:"+argv[0]);
       Log.printStackTrace(e);
    } catch (Exception e ) { 
       System.err.println("Failed to load XDF from file:"+argv[0]);
       Log.printStackTrace(e);
    }

    // output arrays
    List arrays = s.getArrayList();
    Iterator arrayIter = arrays.iterator();
    while (arrayIter.hasNext()) {
       Array arrayObj = (Array) arrayIter.next();
       dumpArrayPartly(arrayObj, 3); 
    }

  }

  public static void dumpArrayPartly (Array arrayObj, int nrofRows) 
  {
     if (arrayObj.hasFieldAxis()) {
        dumpFieldTablePartly (arrayObj, nrofRows);
     } else {
        dumpTablePartly (arrayObj, nrofRows);
     }
  }

  public static void dumpFieldTablePartly (Array arrayObj, int nrofRows) 
  {

      List axes = arrayObj.getAxes();
      doHeaderStuff(arrayObj, axes);

      // set these 
      FieldAxis fieldAxis = (FieldAxis) axes.get(0);
      Axis rowAxis = (Axis) axes.get(1);
      List fields = fieldAxis.getFields();
      int fieldAxisSize = fieldAxis.getLength();

      // gather formatting information. 
      int[] dataCellWidth = new int[fieldAxisSize];
      int[] halfOfDataCellWidth = new int[fieldAxisSize];
      int[] otherHalfOfDataCellWidth = new int[fieldAxisSize];

      for (int i=0; i<fieldAxisSize; i++) {
         dataCellWidth[i] = ((Field) fields.get(i)).getDataFormat().numOfBytes();
         halfOfDataCellWidth[i] = (dataCellWidth[i]/2)+1;
         otherHalfOfDataCellWidth[i] = dataCellWidth[i] - halfOfDataCellWidth[i];
      }

      String bigIndent = indent;
      for(int i=0; i<rowNumberWidth; i++) { bigIndent += space; }
      System.err.print(bigIndent);

      for(int i=0; i<fieldAxisSize; i++) {
         System.err.print(space);
         printFormattedMessage(i, halfOfDataCellWidth[i]);
         for(int j=0; j<=otherHalfOfDataCellWidth[i]; j++) {
            System.err.print(space);
         }
      }
      System.err.println("");

      System.err.print(bigIndent);
      for(int i=0; i<fieldAxisSize; i++) { 
         String dataFormat = dumpDataFormatToString(((Field)fields.get(i)).getDataFormat());
         printFormattedMessage(dataFormat, halfOfDataCellWidth[i]+1);
         for(int j=0; j<=otherHalfOfDataCellWidth[i]; j++) { System.err.print(space); }
      }
      System.err.println("");

      System.err.print(bigIndent);
      for(int i=0; i<fieldAxisSize; i++) { for(int j=0; j<(dataCellWidth[i]+2); j++) { System.err.print(line); } }
      System.err.println("");

      // now print data
      Locator locator = arrayObj.createLocator();
      for(int j=0; j <nrofRows; j++) {
         System.err.print(indent);
         printFormattedMessage(j, otherHalfRowNumberWidth);
         System.err.print(space+space+"|");
         for(int i=0; i<fieldAxisSize; i++)
         {
            System.err.print(space);
            String dataValue;
            try {
               dataValue = arrayObj.getStringData(locator);
            } catch (NoDataException e) {
               dataValue = space;
            }
            printFormattedMessage(dataValue, dataCellWidth[i]);
            System.err.print(space);
            locator.next();
         }
         System.err.println(" ");
      }

      System.out.println(space);

  }

  public static void dumpTablePartly (Array arrayObj, int nrofRows) 
  {
  
      List axes = arrayObj.getAxes();
      doHeaderStuff(arrayObj, axes);

      // set these 
      Axis colAxis = (Axis) axes.get(0);
      Axis rowAxis = (Axis) axes.get(1);
      int colAxisSize = colAxis.getLength();
      
      // print data information. 
      //

      // We first get sizes of dataCells for formating purposes
      int dataCellWidth = arrayObj.getDataFormat().numOfBytes()+1;
      System.err.println(indent+"DataFormat is:"+dumpDataFormatToString(arrayObj.getDataFormat()));

      int halfOfDataCellWidth = (dataCellWidth/2)+1;
      int otherHalfOfDataCellWidth = dataCellWidth - halfOfDataCellWidth;
      String bigIndent = indent;
      for(int i=0; i<rowNumberWidth; i++) { 
         bigIndent += space;
      }
      System.err.print(bigIndent);
      for(int i=0; i<colAxisSize; i++) { 
         System.err.print(space);
         printFormattedMessage(i, halfOfDataCellWidth); 
         for(int j=0; j<=otherHalfOfDataCellWidth; j++) { 
            System.err.print(space);
         }
      }
      System.err.println("");System.err.print(bigIndent);
      for(int i=0; i<colAxisSize; i++) { for(int j=0; j<(dataCellWidth+2); j++) { System.err.print(line); } }
      System.err.println("");
         
      // now print data
      Locator locator = arrayObj.createLocator();
      for(int j=0; j <nrofRows; j++) { 
         System.err.print(indent);
         printFormattedMessage(j, otherHalfRowNumberWidth);
         System.err.print(space+space+"|");
         for(int i=0; i<colAxisSize; i++) 
         {
            System.err.print(space);
            String dataValue;
            try { 
               dataValue = arrayObj.getStringData(locator);
            } catch (NoDataException e) {
               dataValue = space;
            }
            printFormattedMessage(dataValue, dataCellWidth);
            System.err.print(space);
            locator.next();
         }
         System.err.println(" ");
      }

      System.out.println(space);

  }

  public static String dumpDataFormatToString (DataFormat format) 
  {

      String strValue = "?";
      if (format instanceof StringDataFormat) {
         Integer size = ((StringDataFormat) format).getLength();
         strValue = "A" + size.toString();
      }
      else if (format instanceof IntegerDataFormat) 
      {
         Integer size = ((IntegerDataFormat) format).getWidth();
         strValue = "I" + size.toString();
      }
      else if (format instanceof FloatDataFormat) 
      {
         Integer size = ((FloatDataFormat) format).getWidth();
         Integer prec = ((FloatDataFormat) format).getPrecision();
         Integer expn = ((FloatDataFormat) format).getExponent();
         if (expn.intValue() > 0) 
         {
            strValue = "E" + size.toString() + "." + prec.toString();
         } else {
            strValue = "F" + size.toString() + "." + prec.toString();
         }
      }
     return strValue;
  }

  public static void doHeaderStuff (Array arrayObj, List axes) {

      System.out.println(space);
      System.out.println(indent+"Array: "+arrayObj.getName()+" ");

      // print axis information. 
      //
      for (int i=0, size = axes.size(); i < size; i++) {
         if (axes.get(i) instanceof Axis) {
            System.err.println(indent+"Axis["+i+"]:"+((Axis) axes.get(i)).getAxisId());
         } else {
            System.err.println(indent+"FieldAxis["+i+"]:"+((FieldAxis) axes.get(i)).getAxisId());
         }
      }
  } 

  public static void printFormattedMessage(int value, int size) {
     printFormattedMessage(Integer.toString(value), size);
  }

  public static void printFormattedMessage(String value, int size) {
      int valueSize = value.length();
      for (int i=valueSize; i <size; i++) { 
         System.err.print(space);
      }
      System.err.print(value);
  }

}


