
// XDF DocumentBuilderFactoryImpl 
// CVS $Id$

// DocumentBuilderFactoryImpl.java Copyright (C) 2002 Brian Thomas,
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

package gov.nasa.gsfc.adc.xdf.DOMXerces2.jaxp;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import java.util.Hashtable;

import gov.nasa.gsfc.adc.xdf.DOMXerces2.Parser;

/**
 * @version $Revision$
 */
public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {

    /** These are DocumentBuilderFactory attributes not DOM attributes */
    private Hashtable attributes;

    public DocumentBuilderFactoryImpl() {
   	 
    }

    /**
     * 
     */
    public DocumentBuilder newDocumentBuilder()
        throws ParserConfigurationException 
    {
        try {
            return new DocumentBuilderImpl(this, attributes);
        } catch (SAXException se) {
            // Handles both SAXNotSupportedException, SAXNotRecognizedException
            throw new ParserConfigurationException(se.getMessage());
        }
    }

    /**
     * Allows the user to set specific attributes on the underlying 
     * implementation.
     */
    public void setAttribute(String name, Object value)
        throws IllegalArgumentException
    {
        // XXX This is ugly.  We have to collect the attributes and then
        // later create a DocumentBuilderImpl to verify the attributes.
        if (attributes == null) {
            attributes = new Hashtable();
        }
        attributes.put(name, value);

        // Test the attribute name by possibly throwing an exception
        try {
            new DocumentBuilderImpl(this, attributes);
        } catch (Exception e) {
            attributes.remove(name);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Allows the user to retrieve specific attributes on the underlying 
     * implementation.
     */
    public Object getAttribute(String name)
        throws IllegalArgumentException
    {
        Parser domParser = null;

        try {
            // We create a dummy DocumentBuilderImpl in case the attribute
            // name is not one that is in the attributes hashtable.
            domParser =
                new DocumentBuilderImpl(this, attributes).getDOMParser();
            return domParser.getProperty(name);
        } catch (SAXException se1) {
            // assert(name is not recognized or not supported), try feature
            try {
                boolean result = domParser.getFeature(name);
                // Must have been a feature
                return new Boolean(result);
            } catch (SAXException se2) {
                // Not a property or a feature
                throw new IllegalArgumentException(se1.getMessage());
            }
        }
    }
}
