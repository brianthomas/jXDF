
/* Ugh. this is exactly the Apache project code, except for the 
  change in package name. Why did I do this? Because its not publically
  accessable as written originally.!!!!
*/ 
package gov.nasa.gsfc.adc.xdf.DOMXerces1.jaxp;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

class DefaultValidationErrorHandler extends DefaultHandler {
    static private int ERROR_COUNT_LIMIT = 10;
    private int errorCount = 0;

    // XXX Fix message i18n
    public void error(SAXParseException e) throws SAXException {
        if (errorCount >= ERROR_COUNT_LIMIT) {
            // Ignore all errors after reaching the limit
            return;
        } else if (errorCount == 0) {
            // Print a warning before the first error
            System.err.println("Warning: validation was turned on but an org.xml.sax.ErrorHandler was not");
            System.err.println("set, which is probably not what is desired.  Parser will use a default");
            System.err.println("ErrorHandler to print the first " +
                               ERROR_COUNT_LIMIT +               " errors.  Please call");
            System.err.println("the 'setErrorHandler' method to fix this.");
        }

        String systemId = e.getSystemId();
        if (systemId == null) {
            systemId = "null";
        }
        String message = "Error: URI=" + systemId +
            " Line=" + e.getLineNumber() +
            ": " + e.getMessage();
        System.err.println(message);
        errorCount++;
    }
}
