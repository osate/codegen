

package org.osate.generation.ojr ;

import java.util.Map;

import fr.tpt.aadl.ramses.transformation.atl.AadlToTargetSpecificAadl;

public class AadlOjrTransformation extends
                                       AadlToTargetSpecificAadl
{
    
  
  @Override
  public void setParameters(Map<Enum<?>, Object> parameters)
  {
    throw new UnsupportedOperationException() ;
  }


  public AadlOjrTransformation()
  {
  	ATL_FILE_NAMES = new String[]
        {"ACG/targets/shared/UninstanciateOverride",
  		 "ACG/targets/shared/SubprogramCallsCommonRefinementSteps",
  		 "ACG/targets/shared/PortsCommonRefinementSteps",
  		 "ACG/targets/shared/DispatchCommonRefinementSteps",
  		 "ACG/targets/shared/BehaviorAnnexCommonRefinementSteps",
  		 "ACG/targets/ojr/ExpandThreadsPorts",
		 "ACG/targets/ojr/BlackboardCommunications",
		 "ACG/targets/ojr/BufferCommunications",
		 "ACG/targets/ojr/EventsCommunications",
		 "ACG/targets/ojr/QueuingCommunications",
		 "ACG/targets/ojr/SamplingCommunications",
  		 "ACG/targets/ojr/ExpandThreadsDispatchProtocol",
  		 "ACG/PeriodicDelayedCommunication/SharedRules"
        };
  }
  
}
