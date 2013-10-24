

package org.osate.generation.ojr.java;

import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.DirectionType;
import org.osate.aadl2.ProcessClassifier;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.FeatureInstance;

import fr.tpt.aadl.ramses.generation.c.GenerationUtilsC;
import org.osate.utils.Aadl2Utils;

public class AadlToOjrUtils
{
  private static String getProcessPortName(FeatureInstance fi)
  {
	if(((ComponentInstance)fi.eContainer()).getCategory()==ComponentCategory.PROCESS)
	  return ((ComponentInstance) fi.eContainer()).getName();
	return null;
  }
  public static String getFeatureLocalIdentifier(FeatureInstance fi)
  {
	
    return GenerationUtilsC.getGenerationCIdentifier(getProcessPortName(fi)+"_"+fi.getName());
  }
  
  public static String getFeatureGlobalIdentifier(FeatureInstance fi)
  {
    return GenerationUtilsC.getGenerationCIdentifier(fi.getComponentInstancePath()+"_"+fi.getName()+"_global");
  }
  
  public static String getComponentInstanceIdentifier(ComponentInstance instance)
  {
    return GenerationUtilsC.getGenerationCIdentifier(instance.getComponentInstancePath());
  }
  
}
