
package org.osate.generation.java.annex.behavior ;


import org.osate.aadl2.AnnexSubclause ;
import org.osate.generation.java.AadlToJavaUnparser;

import fr.tpt.aadl.annex.behavior.AadlBaUnParserAction ;
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorElement ;
import fr.tpt.aadl.ramses.control.support.plugins.NamedPlugin ;

// ** AADL RESTRICTED BA HAS TO BE SET AS A STANDALONE ECLIPSE PLUGIN PROJECT ** 

public class AadlBaToJavaUnparserAction extends AadlBaUnParserAction
                                     implements NamedPlugin
{

  public static final String ANNEX_NAME = "c_behavior_specification" ;
  protected AadlBaToJavaUnparser _unparser = null ;
  
  public AadlBaToJavaUnparserAction()
  {
    _unparser = new AadlBaToJavaUnparser() ;
  }
  
  @Override
  public String unparseAnnexSubclause(AnnexSubclause subclause,
                                      String indent)
  {
    return _unparser.process((BehaviorElement) subclause) ;
  }

  public AadlBaToJavaUnparser getUnparser()
  {
    return _unparser ;
  }

  @Override
  public String getRegistryName()
  {
    return ANNEX_NAME ;
  }

  @Override
  public String getPluginName()
  {
    // TODO Auto-generated method stub
    return null ;
  }

  @Override
  public String getPluginId()
  {
    // TODO Auto-generated method stub
    return null ;
  }
}
