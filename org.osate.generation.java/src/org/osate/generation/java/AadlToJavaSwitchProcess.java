
package org.osate.generation.java ;

import org.eclipse.emf.common.util.EList ;
import org.osate.aadl2.Comment ;
import org.osate.aadl2.Element ;
import org.osate.aadl2.modelsupport.UnparseText ;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitch ;

public class AadlToJavaSwitchProcess
{

  private static AadlProcessingSwitch aadlSwitch ;

  private static void setAadlSwitch(AadlProcessingSwitch s)
  {
    aadlSwitch = s ;
  }

  private UnparseText unparserContent ;

  public AadlToJavaSwitchProcess(AadlProcessingSwitch s)
  {
    unparserContent = new UnparseText() ;
    setAadlSwitch(s) ;
  }

  public void addOutput(String more)
  {
    unparserContent.addOutput(more) ;
  }

  public void addOutputNewline(String more)
  {
    unparserContent.addOutputNewline(more) ;
  }

  public void processComments(final Element obj)
  {
    if(obj != null)
    {
      EList<Comment> el = obj.getOwnedComments() ;

      for(Comment comment : el)
      {
        String str = comment.getBody() ;

        if(!str.startsWith("--"))
        {
          str = "// " + str.substring(2) ;
        }

        unparserContent.addOutputNewline(str) ;
      }
    }
  }

  public static void process(Element elt)
  {
    aadlSwitch.process(elt) ;
  }

  public String getIndent()
  {
    return unparserContent.getIndentString() ;
  }

  public String getOutput()
  {
    return unparserContent.getParseOutput() ;
  }

  public void incrementIndent()
  {
    unparserContent.incrementIndent() ;
  }

  public void decrementIndent()
  {
    unparserContent.decrementIndent() ;
  }
}