package gov.nasa.gsfc.adc.xdf;
import java.util.*;

public class Group extends BaseObject{
  private String name;
  private String description;
  protected Set memberObjHash = Collections.synchronizedSet(new HashSet());

  public Group() {
 //   attribList.add(new XMLAttribute("name", name,);
 //   attribList.add(new XMLAttribute("description", description, );

  }

  public String setName(String str) {
    name = str;
    return name;
  }

  public String getName() {
    return name;
  }

  public String setDescription(String str) {
    description = str;
    return  description ;
  }

  public String getDescription() {
    return description;
  }

  public Object addMemberObject (Object obj) {
    if (obj!=null) {
      if (memberObjHash.add(obj))
        return obj;
      else
        return null;
    }
    else
      return null;
  }

  public Object removeMemberObj (Object obj) {
     if (obj!=null) {
      if (memberObjHash.contains(obj))  {
        memberObjHash.remove(obj);
        return obj;
      }
      else
        return null;
    }
    else
      return null;
  }

  public boolean hasMemberObj (Object obj) {
    if (obj!=null) {
      if (memberObjHash.contains(obj)) {
        return true;
      }
      else
        return false;
    }
    return false;

  }


}


