package org.osate.generation.ojr.java;

import java.util.LinkedHashSet;
import java.util.Set;

import org.osate.generation.ojr.java.AadlToOjrUnparser.BlackBoardInfo;
import org.osate.generation.ojr.java.AadlToOjrUnparser.QueueInfo;
import org.osate.generation.ojr.java.AadlToOjrUnparser.SampleInfo;

public class PartitionProperties
{
	
  public PartitionProperties(String prefix)
  {
	this.prefix=prefix;
  }
  
  public String prefix = "";
  
  public boolean hasBlackboard = false ; 
  
  public boolean hasQueue = false ;
  
  public boolean hasBuffer = false ;
  
  public boolean hasEvent = false ;
  
  public boolean hasSample = false ;
  
  public Set<BlackBoardInfo> blackboardInfo = new LinkedHashSet<BlackBoardInfo>() ;
  
  public Set<String> eventNames = new LinkedHashSet<String>() ;
  
  public Set<QueueInfo> bufferInfo = new LinkedHashSet<QueueInfo>() ;
  
  public Set<QueueInfo> queueInfo = new LinkedHashSet<QueueInfo>();
  
  public Set<SampleInfo> sampleInfo = new LinkedHashSet<SampleInfo>();
  
}