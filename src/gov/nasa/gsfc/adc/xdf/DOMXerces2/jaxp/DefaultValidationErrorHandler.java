
// XDF DefaultValidationErrorHandler (for Xerces2)
// CVS $Id$

// DefaultValidationErrorHandler.java Copyright (C) 2002 Brian Thomas,
// ADC/GSFC-NASA, Code 631, Greenbelt MD, 20771

/*
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

*/


/* Ugh. this is exactly the Apache project code, except for the 
   change in package name. Why did I do this? Because its not publically
   accessable as written originally.!!!!
*/ 

package gov.nasa.gsfc.adc.xdf.DOMXerces2.jaxp;

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
