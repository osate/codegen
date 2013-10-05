

package org.osate.generation.ojr.java;

import java.util.HashMap ;
import java.util.Map ;

import org.osate.aadl2.ProcessImplementation ;

public class ProcessorProperties
{
  public boolean consoleFound = false ;
  
  public boolean stdioFound = false ;
  
  public boolean stdlibFound = false ;
  
  public int requiredStackSize = 0 ;
  
  public Map<ProcessImplementation, Long> requiredStackSizePerPartition = 
        new HashMap<ProcessImplementation, Long>();
  
  public Map<ProcessImplementation, PartitionProperties> partitionProperties = 
        new HashMap<ProcessImplementation, PartitionProperties>();
}