

// CVS $Id$

// Group.java Copyright (C) 2000 Brian Thomas,
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

package gov.nasa.gsfc.adc.xdf;

import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Iterator;

/** This is an abstract class for group objects.
 */
public class Group extends BaseObject {

  // 
  // Fields
  //

  /* XML attribute names */
  private static final String NAME_XML_ATTRIBUTE_NAME = new String("name");
  private static final String DESCRIPTION_XML_ATTRIBUTE_NAME = new String("description");

  protected Set memberObjHash = Collections.synchronizedSet(new HashSet());

  //
  // Constructors
  //

  /** No-argument constructor
  */
  public Group() {

    // init the XML attributes (to defaults)
    init();
  }

  /** Constructor taking a hashtable with key/value pairs for
      the object attributes. 
  */
  public Group(Hashtable InitXDFAttributeTable) {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  //
  //Get/Set Methods
  //

  /** set the *name* attribute
   */
  public void setName (String strName)
  {
     ((XMLAttribute) attribHash.get(NAME_XML_ATTRIBUTE_NAME)).setAttribValue(strName);
  }

  /**getName
   * @return the current *name* attribute
   */
  public String getName()
  {
    return (String) ((XMLAttribute) attribHash.get(NAME_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

   /**set the *description* attribute
   */
  public void setDescription (String strDesc)
  {
     ((XMLAttribute) attribHash.get(DESCRIPTION_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
  }

   /**getDescription
   * @return the current *description* attribute
   */
  public String getDescription() {
    return (String) ((XMLAttribute) attribHash.get(DESCRIPTION_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  //
  //Other PUBLIC Methods
  //

  /** Add an object as a member of this group.
      @return true if successfully adds this object as a member, false otherwise.
  */
  public Object addMemberObject (Object obj) {
    if (memberObjHash.add(obj))
      return obj;
    else
      return null;

  }

  /** Remove an object from membership in this group.
      @return true if successfully removes the object from membership, false otherwise.
  */
  public Object removeMemberObject (Object obj) {
    if (memberObjHash.contains(obj))  {
      memberObjHash.remove(obj);
      return obj;
    }
    else
      return null;

  }

  /** Determine if this group has passed object as a member.
      @return true if contains this object, false otherwise.
  */
  public boolean hasMemberObj (Object obj) {
    if (memberObjHash.contains(obj)) {
      return true;
    }
    else
      return false;
  }

  public Object clone () throws CloneNotSupportedException {
    Group cloneObj = (Group) super.clone();
    synchronized (this.memberObjHash) {
      synchronized (cloneObj.memberObjHash) {
        Iterator iter = this.memberObjHash.iterator();
        while (iter.hasNext()){
          cloneObj.memberObjHash.add(((BaseObject) iter.next()).clone());
        }
      return cloneObj;
      }
    }
  }


  // 
  // Private Methods
  //

  /** Special protected method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init()
  {

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0, DESCRIPTION_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, NAME_XML_ATTRIBUTE_NAME);

    //set up the attribute hashtable key with the default initial value
    attribHash.put(DESCRIPTION_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put(NAME_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
  }


}

/* Modification History:
 *
 * $Log: Group.java,v
 * Revision 1.2  2000/10/10 19:14:59  cvs
 * Added log history section, commenting on methods
 * and fixed the constructor section (added init method)
 * -b.t
 *
 *
 */

