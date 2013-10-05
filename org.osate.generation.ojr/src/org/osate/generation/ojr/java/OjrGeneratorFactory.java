/**
 * AADL-RAMSES
 * 
 * Copyright © 2012 TELECOM ParisTech and CNRS
 * 
 * TELECOM ParisTech/LTCI
 * 
 * Authors: see AUTHORS
 * 
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the Eclipse Public License as published by Eclipse,
 * either version 1.0 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Eclipse Public License for more details.
 * You should have received a copy of the Eclipse Public License
 * along with this program.  If not, see 
 * http://www.eclipse.org/org/documents/epl-v10.php
 */

package org.osate.generation.ojr.java;

import org.osate.aadl2.util.OsateDebug;
import org.osate.generation.java.AadlToJavaUnparser;
import org.osate.generation.ojr.AadlOjrTransformation;
import org.osate.generation.ojr.makefile.AadlToOjrMakefileUnparser;

import fr.tpt.aadl.ramses.control.support.generator.AbstractGeneratorFactory;
import fr.tpt.aadl.ramses.control.support.generator.Generator;
import fr.tpt.aadl.ramses.generation.target.specific.AadlTargetSpecificCodeGenerator;
import fr.tpt.aadl.ramses.generation.target.specific.AadlTargetSpecificGenerator;

public class OjrGeneratorFactory extends AbstractGeneratorFactory
{
  public static String OJR_GENERATOR_NAME = "ojr" ;
  
  private static Generator createOjrGenerator()
  {
	  OsateDebug.osateDebug("[OjrGeneratorFactory] createOjrGenerator");
    AadlToOjrUnparser ojrUnparser = new AadlToOjrUnparser() ;
    
    AadlToJavaUnparser genericJavaUnparser = AadlToJavaUnparser.getAadlToJavaUnparser() ; 
    
    AadlToOjrMakefileUnparser ojrMakefileUnparser = new AadlToOjrMakefileUnparser() ;
    
    AadlTargetSpecificCodeGenerator tarSpecCodeGen = new 
                    AadlTargetSpecificCodeGenerator(genericJavaUnparser,
                                                    ojrUnparser,
                                                    ojrMakefileUnparser) ;
    
    AadlOjrTransformation targetTrans = new AadlOjrTransformation();
    
    
    AadlTargetSpecificGenerator result = 
                  new AadlTargetSpecificGenerator(targetTrans, tarSpecCodeGen) ;
    
    result.setRegistryName(OJR_GENERATOR_NAME) ;
    
    return result ;
  }
  
  
  
  public Generator createGenerator()
  {
      return createOjrGenerator();
  }
}
