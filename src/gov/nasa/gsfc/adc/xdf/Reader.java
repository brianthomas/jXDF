
/** The XDF Reader class.
    Pieced together from a SAX example file (which one??). 
    @author b.t. (thomas@adc.gsfc.nasa.gov)
    @version $Revision$
*/

// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.io.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.ParserFactory;

import com.sun.xml.parser.Resolver;

public class Reader extends SaxDocHandler
{

    // constructor methods 
    public Reader() {
        msg.setDebugMode(false);  // set true here to print parsed doc to STDOUT 
    }

    public Reader(String version) {
        this();

        if(thisIsXDFVersion(version) != true) {
           msg.error("The version of the SAX document hander's you are using ");
           msg.error("are incompatable with your requested version! (" +version+" vs. "+xdfVersion+")");
        }
    }

    // Methods
    public boolean thisIsXDFVersion(String version) 
    {
       if(version != xdfVersion) { return false; } else { return true; }
    }

    public gov.nasa.gsfc.adc.xdf.Structure parseFile (String file) 
    throws IOException
    {
        InputSource     input;

        try {
            //
            // Turn the filename into an input source
            //
            // NOTE:  The input source must have a "system ID" if
            // there are relative URLs in the input document.  The
            // static resolver methods handle that automatically
            // in most cases.
            //
            input = Resolver.createInputSource (new File(file));

            //
            // turn it into an in-memory object.
            //
            Parser parser;

            parser = (Parser) ParserFactory.makeParser ();
            parser.setDocumentHandler (this);
            parser.setErrorHandler (new MySaxErrorHandler());
            parser.parse(input);

        } catch (SAXParseException err) {
            msg.error("** Parsing error"+", line "+err.getLineNumber()
                +", uri "+err.getSystemId()+"   " + err.getMessage());

        } catch (SAXException e) {
            Exception   x = e;
            if (e.getException () != null)
                x = e.getException ();
            x.printStackTrace ();

        } catch (Throwable t) {
            t.printStackTrace ();
        }

        return myStructure; // what is this?? 
    }

    private setDocumentHandler ( ) {

    }

    static class MySaxErrorHandler extends HandlerBase
    {
        // treat validation errors as fatal
        public void error (SAXParseException e)
        throws SAXParseException
        {
            throw e;
        }

        // dump warnings too
        public void warning (SAXParseException e)
        throws SAXParseException
        {
            //msg.error("** Warning"+", line "+e.getLineNumber()
            System.err.print("** Warning"+", line "+e.getLineNumber()
                + ", uri " + e.getSystemId()+"   " + e.getMessage());
        }
    }

}

