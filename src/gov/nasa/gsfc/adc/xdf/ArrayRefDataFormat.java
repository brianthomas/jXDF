

// XDF ArrayRefDataFormat Class
// CVS $Id$

// ArrayRefDataFormat.java Copyright (C) 2003 Brian Thomas,
// XML Group/GSFC-NASA, Code 630.1, Greenbelt MD, 20771

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


package gov.nasa.gsfc.adc.xdf;

import org.xml.sax.Attributes;

/**
   This class describes array in cell data.
   @version $Revision$
 */


public class ArrayRefDataFormat extends StringDataFormat {

   //
   // Fields
   //

   //
   // Constructors
   //

  /** The no argument constructor.
   */
  public ArrayRefDataFormat ()  //DataFormat no-arg constructor should be been called
  {
     init();
  }


   //
   // Set Methods
   //

   //
   // Other PUBLIC Methods
   //

   //
   // Private Methods 
   //

   //
   // Protected Methods 
   //

   /** Special method used by constructor methods to
       convienently build the XML attribute list for a given class.
    */
   protected void init() {

     super.init();

     specificDataFormatName = "arrayRef";

//     generateFormatPattern();

  }

}

