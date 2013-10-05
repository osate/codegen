
package org.osate.generation.java ;

import java.util.List;
import java.util.Set;

import org.osate.aadl2.BooleanLiteral;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentPrototype;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.DataPrototype;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Parameter;
import org.osate.aadl2.Property;
import org.osate.xtext.aadl2.properties.util.GetProperties;

import fr.tpt.aadl.utils.PropertyUtils;


public class GenerationUtilsJava
{
  
  public final static String THREAD_SUFFIX = "Job" ;

  public static String getGenerationJavaType(String id)
  {
	String res;
	int i;
	
	
	res = id;
	
	res = res.substring(0, 1).toUpperCase() + res.substring(1);
			
	
	
	i = res.indexOf(".");
	while (i != -1)
	{
		String tmp = res.substring(i, i + 2);
		String tmp2 = tmp.toUpperCase().substring(1, 2);
		CharSequence tmp3 = tmp;
		CharSequence tmp4 = tmp2;
		res = res.replace(tmp3, tmp4);
		i = res.indexOf(".");
	}
	i = res.indexOf("::");
	while (i != -1)
	{
		String tmp = res.substring(i, i + 3);
		String tmp2 = tmp.toUpperCase().substring(2,3);
		res = res.replaceAll(tmp, "." + tmp2);
		i = res.indexOf("::");
	}
	
	i = res.indexOf("_");
	while (i != -1)
	{
		String tmp = res.substring(i, i + 2);
		String tmp2 = tmp.toUpperCase().substring(1, 2);
		res = res.replaceAll(tmp, tmp2);
		i = res.indexOf("_");
	}
	

	
    return res;
  }  
  
  public static String getGenerationJavaIdentifier(String id)
  {
	String res;
	int i;
	
	res = id;
	i = res.indexOf("::");
	while (i != -1)
	{
		String tmp = res.substring(i, i + 3);
		String tmp2 = tmp.toUpperCase().substring(2,3);
		res = res.replaceAll(tmp, tmp2);
		i = res.indexOf("::");
	}
	
	i = res.indexOf("_");
	while (i != -1)
	{
		String tmp = res.substring(i, i + 2);
		String tmp2 = tmp.toUpperCase().substring(1, 2);
		res = res.replaceAll(tmp, tmp2);
		i = res.indexOf("_");
	}
	
	i = res.indexOf(".");
	while (i != -1)
	{
		String tmp = res.substring(i, i + 2);
		String tmp2 = tmp.toUpperCase().substring(1, 2);
		CharSequence tmp3 = tmp;
		CharSequence tmp4 = tmp2;
		res = res.replace(tmp3, tmp4);
		i = res.indexOf(".");
	}
	
    return res;
  }

  public static String generateSectionTitle(String object)
  {
    checkSectionObject(object) ;
    int maxChar = 80 ;
    char spacer = ' ' ;
    StringBuilder sb = new StringBuilder() ;
    int titleChar = maxChar - object.length() - 8 ;
    int nbStars = titleChar / 2 ;
    boolean symetric = (titleChar % 2) == 0 ;
    sb.append("/* ") ;

    for(int i = 0 ; i < nbStars ; i++)
    {
      sb.append(spacer) ;
    }

    sb.append(' ') ;
    sb.append(object) ;
    sb.append(' ') ;

    if(false == symetric)
    {
      nbStars++ ;
    }

    for(int i = 0 ; i < nbStars ; i++)
    {
      sb.append(spacer) ;
    }

    sb.append(" */\n") ;
    return sb.toString() ;
  }

  public static String generateSectionComment(String comment)
  {
    checkSectionObject(comment) ;
    int maxChar = 80 ;
    char spacer = ' ' ;
    StringBuilder sb = new StringBuilder() ;
    int titleChar = comment.length() + 4 ;
    sb.append("/* ") ;
    sb.append(comment) ;
    sb.append(' ') ;

    for(int i = titleChar ; i < maxChar - 3 ; i++)
    {
      sb.append(spacer) ;
    }

    sb.append(" */") ;
    return sb.toString() ;
  }

  private static void checkSectionObject(String object)
  {
    if(object.length() > 74) // 80 - 6 length of /*_ x 2
    {
      String errorMsg = "title more than 78 characters" ;
      throw new UnsupportedOperationException(errorMsg) ;
    }
  }

  public static String generateSectionMark()
  {
    return "\n/******************************************************************************/" ;
  }
   
  
  public static boolean isReturnParameter(Parameter p)
  {
	  boolean isReturnParam=false;
	  try {
		isReturnParam =
				  PropertyUtils.getBooleanValue(p, "Return_Parameter") ;
	  } catch (Exception e) {
		  // DO NOT COMIT.
//	    Property prop = GetProperties.lookupPropertyDefinition(p, "Generation_Properties", "Return_Parameter") ;
//		BooleanLiteral bl = (BooleanLiteral) prop.getDefaultValue() ;
//		isReturnParam = bl.getValue();
	  }
	  return isReturnParam;
  }
  
  public static String resolveExistingCodeDependencies(NamedElement object)
  {
	  try
    {
      NamedElement ne = object ;
      String sourceName = PropertyUtils.getStringValue(ne, "Source_Name") ;
      System.out.println("sourceName : "+sourceName);
      return sourceName;
    }
    catch(Exception e)
    {

    	if(object instanceof ComponentType)
      {
        ComponentType c = (ComponentType) object;
        if(c.getOwnedExtension()!=null)
    	  return resolveExistingCodeDependencies(c.getOwnedExtension().getExtended());
      }
      /*else   FIXME: ComponentPrototype */
      else if (object instanceof ComponentImplementation)
      {
        ComponentImplementation ci = (ComponentImplementation) object;
        if(ci.getOwnedExtension()!=null)
    	  return resolveExistingCodeDependencies(ci.getOwnedExtension().getExtended());
      }
      return null ;
    }
  }
}