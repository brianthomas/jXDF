/*
 */

package test;

import java.io.Writer;
import java.io.File;
import java.io.IOException;

// import org.w3c.dom.*;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;
import org.xml.sax.AttributeList;
import org.xml.sax.Parser;
import org.xml.sax.HandlerBase;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import org.apache.tools.ant.Task; 
import org.apache.tools.ant.BuildException;

public class ValidateTest extends org.apache.tools.ant.Task 
implements DocumentHandler
{

    private String inputfile;
    private boolean showSuccess = false;

    public void setInputfile (String strfile) {
       inputfile = strfile;
    }

    public void setShowsuccess (boolean val) {
       showSuccess = val;
    }

    public void execute()
    throws BuildException
    {

	try {

	    String uri = "file:" + new File(inputfile).getAbsolutePath();

	    //
	    // turn it into an in-memory object.
	    //

	    Parser		parser;

	    SAXParserFactory spf = SAXParserFactory.newInstance ();

	    String validation = System.getProperty ("javax.xml.parsers.validation", "true");
	    spf.setValidating (true);

	    SAXParser sp = spf.newSAXParser ();
	    parser = sp.getParser ();

	    parser.setDocumentHandler (new ValidateTest());
	    parser.setErrorHandler (new MyErrorHandler ());
	    parser.parse (uri);

	} catch (SAXParseException err) {
	    String message =  "Failed: ** Parsing error" 
		+ ", line " + err.getLineNumber ()
		+ ", uri " + err.getSystemId () + "\n" + err.getMessage();
            throw new BuildException(message);
	    
	} catch (IOException ioerr) {
            throw new BuildException(ioerr);
	} catch (SAXException e) {
	    Exception	x = e;
	    if (e.getException () != null)
		x = e.getException ();
	    // x.printStackTrace ();
            throw new BuildException("Failed:"+x.getMessage());

	} catch (Throwable t) {
	    t.printStackTrace ();
            throw new BuildException("Failed:"+t.getMessage());
	}

        if (showSuccess)
           System.err.println("Success");

    }

    static class MyErrorHandler extends HandlerBase
    {
	// treat validation errors as fatal
	public void error (SAXParseException e)
	throws SAXParseException
	{
	    throw e;
	}

	// dump warnings too
	public void warning (SAXParseException err)
	throws SAXParseException
	{
	    System.out.println ("** Warning" 
		+ ", line " + err.getLineNumber ()
		+ ", uri " + err.getSystemId ());
	    System.out.println("   " + err.getMessage ());
	}
    }


    private Writer	out;


    // here are all the SAX DocumentHandler methods

    public void setDocumentLocator (Locator l)
    {
	// we'd record this if we needed to resolve relative URIs
	// in content or attributes, or wanted to give diagnostics.
    }

    public void startDocument ()
    throws SAXException
    {
/*
	try {
	    out = new OutputStreamWriter (System.out, "UTF8");
	    emit ("<?xml version='1.0' encoding='UTF-8'?>\n");
	} catch (IOException e) {
	    throw new SAXException ("I/O error", e);
	}
*/
    }

    public void endDocument ()
    throws SAXException
    {
/*
	try {
	    out.write ('\n');
	    out.flush ();
	    out = null;
	} catch (IOException e) {
	    throw new SAXException ("I/O error", e);
	}
*/
    }

    public void startElement (String tag, AttributeList attrs)
    throws SAXException
    {
/*
	emit ("<");
	emit (tag);
	if (attrs != null) {
	    for (int i = 0; i < attrs.getLength (); i++) {
		emit (" ");
		emit (attrs.getName (i));
		emit ("\"");
		// XXX this doesn't quote '&', '<', and '"' in the
		// way it should ... needs to scan the value and
		// emit '&amp;', '&lt;', and '&quot;' respectively
		emit (attrs.getValue (i));
		emit ("\"");
	    }
	}
	emit (">");
*/
    }

    public void endElement (String name)
    throws SAXException
    {
/*
	emit ("</");
	emit (name);
	emit (">");
*/
    }

    public void characters (char buf [], int offset, int len)
    throws SAXException
    {
/*
	// NOTE:  this doesn't escape '&' and '<', but it should
	// do so else the output isn't well formed XML.  to do this
	// right, scan the buffer and write '&amp;' and '&lt' as
	// appropriate.

	try {
	    out.write (buf, offset, len);
	} catch (IOException e) {
	    throw new SAXException ("I/O error", e);
	}
*/
    }

    public void ignorableWhitespace (char buf [], int offset, int len)
    throws SAXException
    {
/*
	// this whitespace ignorable ... so we ignore it!

	// this callback won't be used consistently by all parsers,
	// unless they read the whole DTD.  Validating parsers will
	// use it, and currently most SAX nonvalidating ones will
	// also; but nonvalidating parsers might hardly use it,
	// depending on the DTD structure.
*/
    }

    public void processingInstruction (String target, String data)
    throws SAXException
    {
/*
	emit ("<?");
	emit (target);
	emit (" ");
	emit (data);
	emit ("?>");
*/
    }

    // helpers ... wrap I/O exceptions in SAX exceptions, to
    // suit handler signature requirements
    private void emit (String s)
    throws SAXException
    {
	try {
	    out.write (s);
	    out.flush ();
	} catch (IOException e) {
	    throw new SAXException ("I/O error", e);
	}
    }
}
