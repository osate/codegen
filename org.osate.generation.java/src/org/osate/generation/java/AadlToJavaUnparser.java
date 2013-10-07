
package org.osate.generation.java ;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.debug.BlankDebugEventListener;
import org.eclipse.emf.common.util.EList;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AccessCategory;
import org.osate.aadl2.AccessConnection;
import org.osate.aadl2.AccessType;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.BehavioredImplementation;
import org.osate.aadl2.CallSpecification;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ClassifierValue;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentPrototypeActual;
import org.osate.aadl2.ComponentPrototypeBinding;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Connection;
import org.osate.aadl2.ConnectionEnd;
import org.osate.aadl2.DataAccess;
import org.osate.aadl2.DataImplementation;
import org.osate.aadl2.DataPrototype;
import org.osate.aadl2.DataSubcomponent;
import org.osate.aadl2.DataSubcomponentType;
import org.osate.aadl2.DataType;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.Element;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.Feature;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.ListValue;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.Parameter;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.ProcessSubcomponent;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.PrototypeBinding;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.SubcomponentType;
import org.osate.aadl2.SubprogramAccess;
import org.osate.aadl2.SubprogramCall;
import org.osate.aadl2.SubprogramCallSequence;
import org.osate.aadl2.SubprogramClassifier;
import org.osate.aadl2.SubprogramGroupAccess;
import org.osate.aadl2.SubprogramImplementation;
import org.osate.aadl2.SubprogramSubcomponent;
import org.osate.aadl2.SubprogramSubcomponentType;
import org.osate.aadl2.SubprogramType;
import org.osate.aadl2.ThreadImplementation;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.ThreadType;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitch;
import org.osate.aadl2.util.Aadl2Switch;
import org.osate.aadl2.util.OsateDebug;
import org.osate.annexsupport.AnnexUnparser;
import org.osate.annexsupport.AnnexUnparserProxy;
import org.osate.generation.java.annex.behavior.AadlBaToJavaUnparser;
import org.osate.generation.java.annex.behavior.AadlBaToJavaUnparserAction;
import org.osate.xtext.aadl2.properties.util.GetProperties;

import fr.tpt.aadl.annex.behavior.AadlBaParserAction;
import fr.tpt.aadl.annex.behavior.AadlBaUnParserAction;
import fr.tpt.aadl.annex.behavior.analyzers.TypeHolder;
import fr.tpt.aadl.annex.behavior.utils.AadlBaUtils;
import fr.tpt.aadl.annex.behavior.utils.DimensionException;
import fr.tpt.aadl.ramses.control.support.generator.AadlGenericUnparser;
import fr.tpt.aadl.ramses.control.support.generator.GenerationException;
import fr.tpt.aadl.ramses.control.support.services.ServiceRegistryProvider;
import fr.tpt.aadl.ramses.util.generation.GeneratorUtils;
import fr.tpt.aadl.utils.Aadl2Utils;
import fr.tpt.aadl.utils.PropertyUtils;
import fr.tpt.aadl.utils.names.DataModelProperties;

public class AadlToJavaUnparser extends AadlProcessingSwitch
                             implements AadlGenericUnparser
{
	
	protected static AadlToJavaUnparser _instance;
  private static AadlToJavaUnparser singleton;
  private AadlBaToJavaUnparserAction baToCUnparserAction = new AadlBaToJavaUnparserAction();

  // Types.java
  //protected AadlToJavaSwitchProcess _typesClass ;
  private HashMap<NamedElement, AadlToJavaSwitchProcess> _typesClasses;

  // Subprograms.java
  protected AadlToJavaSwitchProcess _subprogramsClass;
  
  // Activity.java
 // protected AadlToJavaSwitchProcess _activityClass;
  protected AadlToJavaSwitchProcess _currentActivity;
  private HashMap<NamedElement, AadlToJavaSwitchProcess> _activityClasses;
  // Deployment.java
  protected AadlToJavaSwitchProcess _deploymentClass;
  
  // Temporary .c and .h files.
  private AadlToJavaSwitchProcess _currentClass;


  private List<String> _processedTypes;
  private HashMap<NamedElement, Integer> threadTable;
  private int threadIndex;
  
  public Set<NamedElement> additionalUnparsing = new LinkedHashSet<NamedElement>();
  
  // Map Data Access with their relative Data Subcomponent. Relations 
  // are defined in the process implementation via connections.
  private Map<DataAccess, String> _dataAccessMapping = new HashMap<DataAccess, String>();
  
  /**
   * Stack of subprogram classifiers currently uunparsed.
   * Used to resolve prototype dependencies between subprogram calls.
   */
  private List<SubprogramClassifier> subprogramsUnparsingStack = new ArrayList<SubprogramClassifier>();
  
  public static AadlToJavaUnparser getAadlToJavaUnparser()
  {
    if(singleton==null)
      singleton = new AadlToJavaUnparser();
    return singleton;
  }
  
  private AadlToJavaUnparser()
  {
    super() ;
    init() ;
  }
  
  private void init()
  {
	  _instance = this;
	  OsateDebug.osateDebug("[AadlToJavaUnparser] init() called");
	    _typesClasses = new HashMap<NamedElement, AadlToJavaSwitchProcess>();

//    _typesClass = new AadlToJavaSwitchProcess(this) ;
//    _typesClass.addOutputNewline("package generatedcode;");   
//    _typesClass.addOutputNewline("import org.osate.runtime.*;");
//    _typesClass.addOutputNewline("public class Types");
//    _typesClass.addOutputNewline("{");
//    _typesClass.incrementIndent();    
    
    _subprogramsClass = new AadlToJavaSwitchProcess(this) ;
    _subprogramsClass.addOutputNewline("package generatedcode;");   
    _subprogramsClass.addOutputNewline("import org.osate.runtime.*;");
    _subprogramsClass.addOutputNewline("import org.osate.runtime.types.*;");
    _subprogramsClass.addOutputNewline("public class Subprograms");
    _subprogramsClass.addOutputNewline("{");
    _subprogramsClass.incrementIndent();    

    
    _activityClasses = new HashMap<NamedElement, AadlToJavaSwitchProcess>();
//    _activityClass = new AadlToJavaSwitchProcess(this) ;
//    _activityClass.addOutputNewline("package generatedcode;");   
//    _activityClass.addOutputNewline("import org.osate.runtime.*;");
//    _activityClass.addOutputNewline("public class Activity");
//    _activityClass.addOutputNewline("{");
//    _activityClass.incrementIndent();
    _typesClasses = new HashMap<NamedElement, AadlToJavaSwitchProcess>();

    
    _deploymentClass = new AadlToJavaSwitchProcess(this) ;
    _deploymentClass.addOutputNewline("package generatedcode;");   
    _deploymentClass.addOutputNewline("import org.osate.runtime.*;");
    _deploymentClass.addOutputNewline("public class Deployment");
    _deploymentClass.addOutputNewline("{");
    _deploymentClass.incrementIndent();
    
    _processedTypes = new ArrayList<String>() ;

    
  }
  
  public boolean resolveExistingCodeDependencies(NamedElement object,
          AadlToJavaSwitchProcess sourceNameDest) throws Exception
  {


	String sourceName = GenerationUtilsJava.resolveExistingCodeDependencies(object);
	if(sourceName!=null)
	{
	  if(sourceNameDest!=null)
		sourceNameDest.addOutput(sourceName);
	  return true;
	}
	else
		throw new Exception("In component "+object.getName()+": Source_Text " +
	      		"property should also reference a header (.h extension) file");
  }
  
  public List<PrototypeBinding> getCurrentPrototypeBindings(String ctxt)
  {
	  System.out.println("Inherited prototype bindings for " + ctxt);
	  
	  List<PrototypeBinding> bindings = new ArrayList<PrototypeBinding>();
	  for(SubprogramClassifier c: subprogramsUnparsingStack)
	  {
		  System.out.println("  prototype bindings from " + c.getName());
		  List<PrototypeBinding> cBindings = c.getOwnedPrototypeBindings();
		  for(PrototypeBinding b : cBindings)
		  {
			  ComponentPrototypeBinding cpb = (ComponentPrototypeBinding) b;
			  SubcomponentType st = cpb.getActuals().get(0).getSubcomponentType();
			  System.out.println("    prototype binding " + b.getFormal().getName() + " => " + st.getName());
		  }
		  
		  bindings.addAll(cBindings);
	  }
	  return bindings;
  }
  
  public void saveGeneratedFilesContent(File targetDirectory)
  {


    try
    {
      String headerGuard = null ;
      
      for (NamedElement ne : _typesClasses.keySet())
      {
    	  AadlToJavaSwitchProcess sp = _typesClasses.get(ne);
    	  sp.decrementIndent();
          sp.addOutputNewline("}");

          FileWriter typeClass =
                new FileWriter(targetDirectory.getAbsolutePath() + "/" + GenerationUtilsJava.getGenerationJavaType(ne.getQualifiedName())+ ".java") ;
          saveFile(typeClass, sp.getOutput()) ; 
      }
      // Types.java
      
//      _typesClass.decrementIndent();
//      _typesClass.addOutputNewline("}");
      // Do not save the file for now
//      FileWriter typesClass =
//            new FileWriter(targetDirectory.getAbsolutePath() + "/Types.java");
//      saveFile(typesClass, _typesClass.getOutput()) ;
//      
      // Subprograms.java
      _subprogramsClass.decrementIndent();
      _subprogramsClass.addOutputNewline("}");
      FileWriter subprogramsClass =
            new FileWriter(targetDirectory.getAbsolutePath() + "/Subprograms.java") ;
      saveFile(subprogramsClass, _subprogramsClass.getOutput()) ;
      
      // Activity.java
      for (NamedElement ne : _activityClasses.keySet())
      {
    	  AadlToJavaSwitchProcess sp = _activityClasses.get(ne);
    	  sp.decrementIndent();
          sp.addOutputNewline("}");

          FileWriter activityClass =
                new FileWriter(targetDirectory.getAbsolutePath() + "/" + GenerationUtilsJava.getGenerationJavaType(ne.getName())+ "Activity.java") ;
          saveFile(activityClass, sp.getOutput()) ; 
      }

      
      // Deployment.java
      _deploymentClass.decrementIndent();
      _deploymentClass.addOutputNewline("}");
      FileWriter deploymentClass =
            new FileWriter(targetDirectory.getAbsolutePath() + "/Deployment.java") ;
      saveFile(deploymentClass, _deploymentClass.getOutput()) ;
      
      
      clean();

    }
    catch(IOException e)
    {
      // TODO: handle error message.
      e.printStackTrace() ;
    }
  }

  private void clean() {
	this._dataAccessMapping.clear();
  }



  private void saveFile(FileWriter file,
                        String ... content)
  {
    BufferedWriter output ;
    StringBuilder sb = new StringBuilder() ;
    
    for(String s : content)
    {
      sb.append(s) ;
    }
    
    try
    {
      output = new BufferedWriter(file) ;
      
      output.write(sb.toString()) ;
      
      output.close() ;
    }
    catch(IOException e)
    {
      // TODO: handle error message.
      e.printStackTrace() ;
    }
  }
  
  protected void processDataSubcomponentType(DataSubcomponentType dst,
		  AadlToJavaSwitchProcess sourceNameDest, 
		  AadlToJavaSwitchProcess sourceTextDest)
  {
	  processDataSubcomponentType(null, dst, sourceNameDest, sourceTextDest);
  }
  
  protected void processDataSubcomponentType(Classifier owner,
		  DataSubcomponentType dst,
		  AadlToJavaSwitchProcess sourceNameDest, 
		  AadlToJavaSwitchProcess sourceTextDest)
  {

      if(dst instanceof DataPrototype && owner!=null)
      {
    	for(PrototypeBinding pb: owner.getOwnedPrototypeBindings())
    	{
    	  if(pb instanceof ComponentPrototypeBinding
    			&& pb.getFormal().getName().equalsIgnoreCase(dst.getName()))
    	  {
  		    for(ComponentPrototypeActual pa: ((ComponentPrototypeBinding) pb).getActuals())
    		{
    		  if(pa.getSubcomponentType() instanceof DataSubcomponentType)
    	      {
    		    DataSubcomponentType dstActual = (DataSubcomponentType) pa.getSubcomponentType();
    		    processDataSubcomponentType(owner, dstActual, sourceNameDest, sourceTextDest);
    	        break;
    	      }
    	    }
    	  }
    	}
      }
      else
      {
    	String toAdd;
    	toAdd = GenerationUtilsJava.resolveExistingCodeDependencies(dst);
    	if (toAdd == null)
    	{
    		toAdd = GenerationUtilsJava.getGenerationJavaType(dst.getQualifiedName());
    	}
    	sourceNameDest.addOutput(toAdd) ;
      }
  }
  
  boolean processBehavioredType(ComponentType type)
  {
	  return processBehavioredType(type, type);
  }
  
  boolean processBehavioredType(ComponentType type, ComponentType owner)
  {
	  boolean foundRestrictedAnnex = false;
	  for(AnnexSubclause annex : type.getOwnedAnnexSubclauses())
      {
        if(annex.getName().equalsIgnoreCase(AadlBaParserAction.ANNEX_NAME))
        {
        	foundRestrictedAnnex = true;
        	processAnnexSubclause(annex, owner) ;
        	break;
        }
      }
	  if(!foundRestrictedAnnex && type.getExtended()!=null)
		  foundRestrictedAnnex = processBehavioredType(type.getExtended(), owner);
	  return foundRestrictedAnnex;
  }
  
  /**
   * unparses annex subclause
   *
   * @param as
   *            AnnexSubclause object
   */
  public String processAnnexSubclause(AnnexSubclause as, NamedElement owner)
  {
	AadlToJavaSwitchProcess codeUnparser;
	if(owner instanceof SubprogramClassifier)
	{
	  codeUnparser = _subprogramsClass;
	}
	else
	{
	  codeUnparser = _currentActivity;
	}

    // XXX May AadlBaToCUnparser have its own interface ???
    // XXX NO, using interfaces and in particular extension points is an overkill

    AadlBaToJavaUnparser baToCUnparser =
    		(AadlBaToJavaUnparser) baToCUnparserAction.getUnparser() ;

    baToCUnparser.setDataAccessMapping(_dataAccessMapping) ;
    baToCUnparser.setOwner(owner);

    baToCUnparserAction.unparseAnnexSubclause(as,
    		codeUnparser.getIndent()) ;



    baToCUnparser.addIndent_C(codeUnparser.getIndent()) ;
    codeUnparser.addOutput(baToCUnparser.getCodeContent()) ;
    baToCUnparser.getAdditionalHeaders().clear();
    
    if (owner instanceof SubprogramType)
    {
    	subprogramsUnparsingStack.remove(subprogramsUnparsingStack.size()-1);
    }

    for(NamedElement ne: baToCUnparser.getCoreElementsToBeUnparsed())
    {
    	process(ne);
    }
    
    return DONE ;
  }
  
  boolean processBehavioredImplementation(BehavioredImplementation object)
  {
	  return processBehavioredImplementation(object, object);
  }
  
  public boolean processBehavioredImplementation(BehavioredImplementation object, BehavioredImplementation owner)
  {
	  boolean foundRestrictedAnnex = false;
	  for(AnnexSubclause annex : object.getOwnedAnnexSubclauses())
      {
        if(annex.getName().equalsIgnoreCase(AadlBaUnParserAction.ANNEX_NAME))
        {
        	foundRestrictedAnnex = true;
        	processAnnexSubclause(annex, owner) ;
        	break;
        }
      }
	  if(!foundRestrictedAnnex && !object.getOwnedSubprogramCallSequences().isEmpty())
	  {
	  	for(SubprogramCallSequence scs: object.getOwnedSubprogramCallSequences())
	  	{
	  		process(scs);
	  	}
	  	foundRestrictedAnnex=true;
	  }
	  if(!foundRestrictedAnnex && object.getExtended()!=null)
		  foundRestrictedAnnex = processBehavioredImplementation((BehavioredImplementation)object.getExtended(), object);
	  return foundRestrictedAnnex;
  }
  
  
  protected void getJavaTypeDeclarator(NamedElement object)
  {

	AadlToJavaSwitchProcess _typesClass = new AadlToJavaSwitchProcess(_instance);
	_typesClasses.put(object, _typesClass);
	
	
    String id =
          GenerationUtilsJava.getGenerationJavaIdentifier(object.getQualifiedName()) ;
    TypeHolder dataTypeHolder = null ;
    _typesClass.addOutputNewline("package generatedcode;") ;


    try
    {
      dataTypeHolder = AadlBaUtils.getTypeHolder(object) ;
    }
    catch(DimensionException e)
    {
      // TODO: handle error message.
      e.printStackTrace() ;
    }

    EList<PropertyExpression> numberRepresentation =
          PropertyUtils
                .getPropertyExpression(dataTypeHolder.klass,
                                       DataModelProperties.NUMBER_REPRESENTATION) ;
    String numberRepresentationValue = "" ;

    for(PropertyExpression n : numberRepresentation)
    {
      if(n instanceof NamedValue)
      {
        NamedValue el = (NamedValue) n ;
        numberRepresentationValue =
              ((EnumerationLiteral) el.getNamedValue()).getName().toLowerCase() ;
        break;
      }
    }

    // define types the current data type depends on
    EList<PropertyExpression> referencedBaseType =
          PropertyUtils
                .getPropertyExpression(dataTypeHolder.klass,
                                       DataModelProperties.BASE_TYPE) ;

    for(PropertyExpression baseTypeProperty : referencedBaseType)
    {
      if(baseTypeProperty instanceof ListValue)
      {
        ListValue lv = (ListValue) baseTypeProperty ;

        for(PropertyExpression v : lv.getOwnedListElements())
        {
          if(v instanceof ClassifierValue)
          {
            ClassifierValue cv = (ClassifierValue) v ;
            if(_processedTypes.contains(cv.getClassifier().getQualifiedName())==false && cv.getClassifier() instanceof DataSubcomponentType)
            {
              getJavaTypeDeclarator(cv.getClassifier());
              _processedTypes.add(cv.getClassifier().getQualifiedName());
            }
          }
        }
      }
    }
    
    switch ( dataTypeHolder.dataRep )
    {
    // Simple types
      case BOOLEAN :
        _typesClass.addOutputNewline("typedef char " + id + ";") ;
        break ;
      case CHARACTER :
      {
    	  _typesClass.addOutput("typedef ") ;
    	  _typesClass.addOutputNewline(numberRepresentationValue + " char " +
              id + ";") ;
        break ;
      }
      case FIXED :
        break ;
      case FLOAT :
    	  _typesClass.addOutputNewline("typedef float " + id + ";") ;
        break ;
      case INTEGER :
      {
    	  _typesClass.addOutput("typedef ") ;
    	  _typesClass.addOutputNewline(numberRepresentationValue + " int " +
              id + ";") ;
        break ;
      }
      case STRING :
    	  _typesClass.addOutputNewline("typedef char * " + id + ";") ;
        break ;
      // Complex types
      case ENUM :
      {

    	_typesClass.addOutputNewline("public enum " + GenerationUtilsJava.getGenerationJavaType(id)) ;
    	_typesClass.addOutputNewline("{") ;
    	_typesClass.incrementIndent();
    	StringBuilder enumDeclaration = new StringBuilder();
        List<String> stringifiedRepresentation = new ArrayList<String>() ;
        EList<PropertyExpression> dataRepresentation =
              PropertyUtils
                    .getPropertyExpression(dataTypeHolder.klass,
                                           DataModelProperties.REPRESENTATION) ;

        for(PropertyExpression representationProperty : dataRepresentation)
        {
          if(representationProperty instanceof ListValue)
          {
            ListValue lv = (ListValue) representationProperty ;

            for(PropertyExpression v : lv.getOwnedListElements())
            {
              if(v instanceof StringLiteral)
              {
                StringLiteral enumString = (StringLiteral) v ;
                stringifiedRepresentation.add(enumString.getValue()) ;
              }
            }
          }
        }

        EList<PropertyExpression> dataEnumerators =
              PropertyUtils
                    .getPropertyExpression(dataTypeHolder.klass,
                                           DataModelProperties.ENUMERATORS) ;

        for(PropertyExpression enumeratorProperty : dataEnumerators)
        {
          if(enumeratorProperty instanceof ListValue)
          {
            ListValue lv = (ListValue) enumeratorProperty ;
            Iterator<PropertyExpression> it =
                  lv.getOwnedListElements().iterator() ;

            while(it.hasNext())
            {
              PropertyExpression v = it.next() ;

              if(v instanceof StringLiteral)
              {
                StringLiteral enumString = (StringLiteral) v ;
                String rep = "" ;

                if(stringifiedRepresentation.isEmpty() == false)
                {
                  rep =
                        " = " +
                              stringifiedRepresentation.get(lv
                                    .getOwnedListElements().indexOf(v)) ;
                }

                if(it.hasNext())
                {
                  rep += "," ;
                }
                enumDeclaration.append("\t"+id + "_" +
                        enumString.getValue() + rep+"\n");
              }
            }
          }
        }

        _typesClass.addOutput(enumDeclaration.toString()) ;
        
        break ;
      }
      case STRUCT :
      {
    	  
        StringBuilder structDefinition = new StringBuilder("typedef struct " + id + " {\n");
        EList<PropertyExpression> elementNames =
              PropertyUtils
                    .getPropertyExpression(dataTypeHolder.klass,
                                           DataModelProperties.ELEMENT_NAMES) ;
        if(elementNames.isEmpty()==false)
        {
          List<String> stringifiedElementNames = new ArrayList<String>() ;

          for(PropertyExpression elementNameProperty : elementNames)
          {
            if(elementNameProperty instanceof ListValue)
            {
              ListValue lv = (ListValue) elementNameProperty ;

              for(PropertyExpression v : lv.getOwnedListElements())
              {
                if(v instanceof StringLiteral)
                {
                  StringLiteral eltName = (StringLiteral) v ;
                  stringifiedElementNames.add(eltName.getValue()) ;
                }
              }
            }
          }

          EList<PropertyExpression> elementTypes =
                PropertyUtils
                      .getPropertyExpression(dataTypeHolder.klass,
                                             DataModelProperties.BASE_TYPE) ;

          for(PropertyExpression elementTypeProperty : elementTypes)
          {
        	if(elementTypeProperty instanceof ListValue)
        	{
              ListValue lv = (ListValue) elementTypeProperty ;

              for(PropertyExpression v : lv.getOwnedListElements())
              {
                if(v instanceof ClassifierValue)
                {
                  ClassifierValue cv = (ClassifierValue) v ;
                  String type =
                        GenerationUtilsJava.getGenerationJavaIdentifier(cv
                              .getClassifier().getQualifiedName()) ;
                  structDefinition.append("\t"+type +
                        " " +
                        stringifiedElementNames.get(lv.getOwnedListElements()
                              .indexOf(v)) + ";\n") ;
                }
              }
            }
          }
        }
        else
        {
        	
        }
        _typesClass.addOutput(structDefinition.toString());
        _typesClass.addOutputNewline("} " + id + ";") ;
        break ;
      }
      case UNION :
      {
    	StringBuilder unionDeclaration = new StringBuilder("typedef union " + id + " {\n");
        EList<PropertyExpression> elementNames =
              PropertyUtils
                    .getPropertyExpression(dataTypeHolder.klass,
                                           DataModelProperties.ELEMENT_NAMES) ;
        List<String> stringifiedElementNames = new ArrayList<String>() ;

        for(PropertyExpression elementNameProperty : elementNames)
        {
          if(elementNameProperty instanceof ListValue)
          {
            ListValue lv = (ListValue) elementNameProperty ;

            for(PropertyExpression v : lv.getOwnedListElements())
            {
              if(v instanceof StringLiteral)
              {
                StringLiteral eltName = (StringLiteral) v ;
                stringifiedElementNames.add(eltName.getValue()) ;
              }
            }
          }
        }

        EList<PropertyExpression> elementTypes =
              PropertyUtils
                    .getPropertyExpression(dataTypeHolder.klass,
                                           DataModelProperties.BASE_TYPE) ;

        for(PropertyExpression elementTypeProperty : elementTypes)
        {
          if(elementTypeProperty instanceof ListValue)
          {
            ListValue lv = (ListValue) elementTypeProperty ;

            for(PropertyExpression v : lv.getOwnedListElements())
            {
              if(v instanceof ClassifierValue)
              {
                ClassifierValue cv = (ClassifierValue) v ;
                String type =
                      GenerationUtilsJava.getGenerationJavaIdentifier(cv
                            .getClassifier().getQualifiedName()) ;
                unionDeclaration.append("\t"+type +
                      " " +
                      stringifiedElementNames.get(lv.getOwnedListElements()
                            .indexOf(v)) + ";\n") ;
              }
            }
          }
        }
        _typesClass.addOutput(unionDeclaration.toString());
        _typesClass.addOutputNewline("}" + id + ";") ;
        break ;
      }
      case ARRAY :
      {
    	StringBuilder arrayDef = new StringBuilder ("typedef ");
        EList<PropertyExpression> baseType =
              PropertyUtils
                    .getPropertyExpression(dataTypeHolder.klass,
                                           DataModelProperties.BASE_TYPE) ;
        boolean found = false;
        for(PropertyExpression baseTypeProperty : baseType)
        {
          if(baseTypeProperty instanceof ListValue)
          {
            ListValue lv = (ListValue) baseTypeProperty ;

            for(PropertyExpression v : lv.getOwnedListElements())
            {
              if(v instanceof ClassifierValue)
              {
                ClassifierValue cv = (ClassifierValue) v ;
               	if(false == _processedTypes.contains(cv.getClassifier().getQualifiedName()))
               	{
                  getJavaTypeDeclarator(cv.getClassifier());
               	  _processedTypes.add(cv.getClassifier().getQualifiedName());
               	}
               	arrayDef.append(GenerationUtilsJava
                      .getGenerationJavaIdentifier(cv.getClassifier()
                            .getQualifiedName())) ;
                found=true;
                break;
              }
            }
            if(found)
            	break;
          }
        }
        _typesClass.addOutput(arrayDef.toString());
        _typesClass.addOutput(" ") ;
        _typesClass.addOutput(id) ;
        EList<PropertyExpression> arrayDimensions =
              PropertyUtils
                    .getPropertyExpression(dataTypeHolder.klass,
                                           DataModelProperties.DIMENSION) ;

        if(arrayDimensions.isEmpty())
        {
        	_typesClass.addOutput("[]") ;
        }
        else
        {
          for(PropertyExpression dimensionProperty : arrayDimensions)
          {
            if(dimensionProperty instanceof ListValue)
            {
              ListValue lv = (ListValue) dimensionProperty ;
 
              for(PropertyExpression v : lv.getOwnedListElements())
              {
                if(v instanceof IntegerLiteral)
                {
                	_typesClass.addOutput("[") ;
                  IntegerLiteral dimension = (IntegerLiteral) v ;
                  _typesClass.addOutput(Long.toString(dimension.getValue())) ;
                  _typesClass.addOutput("]") ;
                }
              }
            }
          }
        }

        _typesClass.addOutputNewline(";") ;
        break ;
      }
      case UNKNOWN :
      {
        try
        {
        	_typesClass.addOutput("typedef ") ;

          _typesClass.addOutput(" ") ;
          _typesClass.addOutput(id) ;
          _typesClass.addOutputNewline(";") ;
        }
        catch(Exception e)
        {
          return ;
        }

        break ;
      }
    }
  }

  @Override
  protected void initSwitches()
  {
    aadl2Switch = new Aadl2Switch<String>()
    {
      @Override
      public String caseDataType(DataType object)
      {
        if(_processedTypes.contains(object.getQualifiedName()))
        {
          return DONE ;
        }
        _processedTypes.add(object.getQualifiedName());
        
        boolean hasSourceName = false;
        try {
			if (PropertyUtils.getStringValue(object, "Source_Name") != null)
			{
				hasSourceName = true;
			}
		} catch (Exception e) {
			hasSourceName = false;
		}
        
        if (! hasSourceName)
        {
		    getJavaTypeDeclarator((NamedElement) object) ;
		    _typesClasses.get(object).processComments(object) ;
	    }
        return DONE ;
      }

      public String caseAadlPackage(AadlPackage object)
      {
        process(object.getOwnedPublicSection()) ;
        process(object.getOwnedPrivateSection()) ;
        return DONE ;
      }

      /**
       * unparses annex library
       *
       * @param al
       *            AnnexLibrary object
       */
      public String caseAnnexLibrary(AnnexLibrary al)
      {
        String annexName = al.getName() ;
        AnnexUnparser unparser =
              ServiceRegistryProvider.getServiceRegistry()
                    .getUnparser(annexName) ;

        if(unparser != null)
        {
          unparser.unparseAnnexLibrary(al, _currentClass.getIndent()) ;
        }

        return DONE ;
      }

      /**
       * unparses default annex library
       *
       * @param dal
       *            DefaultAnnexLibrary object
       */
      public String caseDefaultAnnexLibrary(DefaultAnnexLibrary dal)
      {
        AnnexUnparser unparser =
              ServiceRegistryProvider.getServiceRegistry().getUnparser("*") ;

        if(unparser != null)
        {
        }

        return DONE ;
      }

      public String caseDataImplementation(DataImplementation object)
      {
    	if(_processedTypes.contains(object.getQualifiedName()))
        {
          return DONE ;
        }
        _processedTypes.add(object.getQualifiedName());
        getJavaTypeDeclarator((NamedElement) object) ;
        _typesClasses.get(object).processComments(object) ;
        return null ;
      }

      public String caseDataSubcomponent(DataSubcomponent object)
      {
        AadlToJavaSwitchProcess unparser ;

        unparser = _currentClass ;


        unparser.processComments(object) ;
        DataSubcomponentType dst = object.getDataSubcomponentType() ;

        processDataSubcomponentType(dst, unparser, _currentClass);

        unparser.addOutput(" ") ;
        unparser.addOutput(GenerationUtilsJava.getGenerationJavaIdentifier(object
              .getQualifiedName())) ;
        unparser.addOutput(GeneratorUtils.getInitialValue(object)) ;
        unparser.addOutputNewline(";") ;

        if(_processedTypes.contains(object.getDataSubcomponentType().getQualifiedName()) == false)
        {
          _processedTypes.add(object.getQualifiedName());
          process(object.getDataSubcomponentType());
        }

        return DONE ;
      }
            
      public String caseProcessImplementation(ProcessImplementation object)
      {
    	threadIndex = 0;
    	threadTable = new HashMap <NamedElement,Integer>();
        buildDataAccessMapping(object) ;
        
        processEList(object.getOwnedThreadSubcomponents()) ;
        
        
        _currentClass = _deploymentClass;
        if ( ! threadTable.isEmpty())
        {
	        for (NamedElement ne : threadTable.keySet())
	        {
	        	_deploymentClass.addOutput ("public static final int ");
	        	_deploymentClass.addOutput(GenerationUtilsJava.getGenerationJavaIdentifier(ne.getQualifiedName()));
	        	_deploymentClass.addOutput (" = ");
	        	_deploymentClass.addOutput (threadTable.get(ne).toString());
	        	_deploymentClass.addOutputNewline(";");
	        	
	        }

        	
//        	int c = 0;
//        	_deploymentClass.addOutputNewline("public enum TaskIdentifier") ;
//        	_deploymentClass.addOutputNewline("{") ;
//        	_deploymentClass.incrementIndent();
//	        for (NamedElement ne : threadTable.keySet())
//	        {
//	        	_deploymentClass.addOutput(GenerationUtilsJava.getGenerationJavaIdentifier(ne.getQualifiedName()));
//		        c++;
//		        if (c < threadTable.size())
//		        {
//		        	_deploymentClass.addOutputNewline(",");
//		        }
//		        else
//		        {
//		        	_deploymentClass.addOutputNewline("");
//		        }
//	        }
//
//	        _deploymentClass.decrementIndent();
//	        _deploymentClass.addOutputNewline("}") ;
        }
        
        
        List<String> dataSubcomponentNames = new ArrayList<String>();
        Map<String, DataSubcomponent> dataSubcomponentMapping = new HashMap<String, DataSubcomponent>();
        for(DataSubcomponent ds: object.getOwnedDataSubcomponents())
        {
          dataSubcomponentNames.add(ds.getName());
          if(false == _dataAccessMapping.containsValue(ds.getName()))
          {
        	  processDataSubcomponentType(ds.getDataSubcomponentType(), _deploymentClass, _deploymentClass);
        	  _deploymentClass.addOutput(" "+ds.getName()) ;
        	  _deploymentClass.addOutput(GeneratorUtils.getInitialValue(ds)) ;
        	  _deploymentClass.addOutputNewline(";") ;
          }
          else
          {
        	  dataSubcomponentMapping.put(ds.getName(), ds);
          }
          process(ds.getDataSubcomponentType());
        }
        // *** Generate deployment.c ***
        if(false == _dataAccessMapping.isEmpty())
        {
          List<String> treatedDeclarations = new ArrayList<String>();
          for(DataAccess d : _dataAccessMapping.keySet())
          {
        	if(treatedDeclarations.contains(_dataAccessMapping.get(d)))
        	{
        		continue;
        	}
        	else if(dataSubcomponentNames.contains(_dataAccessMapping.get(d)))
        	{
        	  DataSubcomponentType dst = d.getDataFeatureClassifier();
        	  String declarationID = _dataAccessMapping.get(d);
              processDataSubcomponentType(dst, _deploymentClass, _deploymentClass);
              _deploymentClass.addOutput(" "+declarationID);
              DataSubcomponent ds = dataSubcomponentMapping.get(_dataAccessMapping.get(d));
              if(ds!=null)
            	  _deploymentClass.addOutput(GeneratorUtils.getInitialValue(ds)) ;
              _deploymentClass.addOutputNewline(";") ;
              treatedDeclarations.add(declarationID);
        	}
          }
        }
        return DONE ;
      }
      
      // Builds the data access mapping via the connections described in the
      // process implementation.
      private void buildDataAccessMapping(ComponentImplementation cptImpl)
      {
        
        EList<Subcomponent> subcmpts = cptImpl.getAllSubcomponents() ;
        
        List<String> dataSubcomponentNames = new ArrayList<String>() ;
        
        // Fetches data subcomponent names.
        for(Subcomponent s : subcmpts)
        {
          if(s instanceof DataSubcomponent)
          {
            dataSubcomponentNames.add(s.getName()) ;
          }
        }
        
        // Binds data subcomponent names with DataAcess objects
        // of threads.
        // See process implementation's connections.
        for(Connection connect : cptImpl.getAllConnections())
        {
          if (connect instanceof AccessConnection &&
             ((AccessConnection) connect).getAccessCategory() == AccessCategory.DATA)
          {

        	if(connect.getAllDestination() instanceof DataSubcomponent)
        	{
        	  DataSubcomponent destination =  (DataSubcomponent) connect.
                                                           getAllDestination() ;
            
              if(Aadl2Utils.contains(destination.getName(), dataSubcomponentNames))
              {
                ConnectedElement source = (ConnectedElement) connect.getSource() ;
                if (source.getConnectionEnd() instanceof DataAccess)
                {
	                DataAccess da = (DataAccess) source.getConnectionEnd() ;
	                _dataAccessMapping.put(da, destination.getName()) ; 
	                
                }
              }
        	}
            else if(connect.getAllSource() instanceof DataSubcomponent)
            {
              DataSubcomponent source =  (DataSubcomponent) connect.
              		getAllSource() ;
              if(Aadl2Utils.contains(source.getName(), dataSubcomponentNames))
              {
                ConnectedElement dest = (ConnectedElement) connect.getDestination() ;
                 
                DataAccess da = (DataAccess) dest.getConnectionEnd() ;
                _dataAccessMapping.put(da, source.getName()) ;
              }
            }
            else if(connect.getAllDestination() instanceof DataAccess
            		&& connect.getAllSource() instanceof DataAccess)
            {
            	if(!(connect.getAllDestination().eContainer() instanceof Thread)
            		&& !(connect.getAllSource().eContainer() instanceof Thread))
            		continue;
            	DataAccess destination = (DataAccess) connect.getAllDestination();
            	DataAccess source = (DataAccess) connect.getAllSource();
            	if(_dataAccessMapping.containsKey(destination) &&
            			!_dataAccessMapping.containsKey(source))
            		_dataAccessMapping.put(source, _dataAccessMapping.get(destination)) ;
            	if(_dataAccessMapping.containsKey(source) &&
            			!_dataAccessMapping.containsKey(destination))
            		_dataAccessMapping.put(destination, _dataAccessMapping.get(source)) ;
            	
            }
          }
        }
      }
      
      public String caseProcessSubcomponent(ProcessSubcomponent object)
      {
        process(object.getComponentImplementation()) ;
        for(NamedElement ne: additionalUnparsing)
        	process(ne);
        additionalUnparsing.clear();
        return DONE ;
      }
      
      public String caseSubprogramSubcomponent(SubprogramSubcomponent object)
      {
    	  process(object.getSubprogramSubcomponentType());
    	  return DONE;
      }
      
      public String caseThreadImplementation(ThreadImplementation object)
      {
    	
    	  _currentActivity = new AadlToJavaSwitchProcess(_instance);
    	  _activityClasses.put(object, _currentActivity);
//        _activityClass = new AadlToJavaSwitchProcess(this) ;
//        _activityClass.addOutputNewline("package generatedcode;");   
//        _activityClass.addOutputNewline("import org.osate.runtime.*;");
//        _activityClass.addOutputNewline("public class Activity");
//        _activityClass.addOutputNewline("{");
//        _activityClass.incrementIndent();
    	for(SubprogramSubcomponent sc:object.getOwnedSubprogramSubcomponents())
    	{
    		process(sc);
    	}
    	if(_processedTypes.contains(object.getQualifiedName()))
        {
          return DONE ;
        }
        _processedTypes.add(object.getQualifiedName());
    	_currentClass = _currentActivity;
        buildDataAccessMapping(object) ;
        process(object.getType()) ;
        _currentClass.addOutputNewline("package generatedcode;") ;

        _currentClass.addOutput("public class ") ;
        _currentClass.addOutput(GenerationUtilsJava
                .getGenerationJavaType(object.getName())) ;
        _currentClass.addOutput("Activity") ;

        _currentClass.addOutputNewline(" extends Thread") ;
        _currentClass.addOutputNewline("{") ;
        _currentClass.incrementIndent() ;

        _currentClass.addOutputNewline("public void run ()") ;
        _currentClass.addOutputNewline("{") ;
        _currentClass.incrementIndent() ;

        for(DataSubcomponent d : object.getOwnedDataSubcomponents())
        {
          process(d) ;
        }
        
        _currentClass.addOutputNewline("while (true) {") ;
        _currentClass.incrementIndent() ;
        
        processBehavioredImplementation(object);
        
        _currentActivity.decrementIndent() ;
        _currentActivity.addOutputNewline("}") ;
        
        _currentActivity.decrementIndent() ;
        _currentActivity.addOutputNewline("}") ;
        
        return null ;
      }

      public String caseSubprogramCallSequence(SubprogramCallSequence object)
      {
    	for(CallSpecification cs : object.getOwnedCallSpecifications())
    	{
    		if(cs instanceof SubprogramCall)
    		{
    			SubprogramCall sc = (SubprogramCall) cs;
    			process(sc);
    			if(cs.eContainer().eContainer() instanceof ThreadImplementation)
    	  		{
    	  			_currentClass = _currentActivity;
    	  		}
    		}
    	}
    	return null ;
      }
      
      public String caseSubprogramType(SubprogramType object)
      {
    	_currentClass = _subprogramsClass;
      	
    	if(_processedTypes.contains(object.getQualifiedName()))
        {
          return DONE ;
        }
        _processedTypes.add(object.getQualifiedName());
    	
        try {
          resolveExistingCodeDependencies(object,  null);
        } catch (Exception e1) { 
          caseSubprogramClassifier((SubprogramClassifier) object);
                
          processBehavioredType(object) ;
          	    
          _subprogramsClass.decrementIndent();
          _subprogramsClass.addOutputNewline("}");
          
        }
  	    
  	    return DONE;
      }
      
      public String caseSubprogramClassifier(SubprogramClassifier object)
      {
    	subprogramsUnparsingStack.add(object);
    	  
        Parameter returnParameter = null;
        List<Feature> orderedFeatureList = null;
        if(object instanceof SubprogramImplementation)
        {
          SubprogramImplementation si = (SubprogramImplementation) object;
          orderedFeatureList = Aadl2Utils.orderFeatures(si.getType()) ;
        }
        else if (object instanceof SubprogramType)
        {
          SubprogramType st = (SubprogramType) object;
          orderedFeatureList = Aadl2Utils.orderFeatures(st) ;
        }
        boolean isReturnParam=false;
        for(Feature param: orderedFeatureList)
        {
          if(param instanceof Parameter)
          {
        	Parameter p = (Parameter) param ;
        	isReturnParam = GenerationUtilsJava.isReturnParameter(p);
          }
        }
        if(!isReturnParam)
        {
          _subprogramsClass.addOutput("public static void ");
        }

        _subprogramsClass.addOutput(GenerationUtilsJava.getGenerationJavaIdentifier(object.getQualifiedName()));

        _subprogramsClass.addOutput("(");
      	
    	boolean first = true;
    	for(Feature f: orderedFeatureList)
    	{
    	  if(f instanceof Parameter)
  		  {
  		    Parameter p = (Parameter) f ;
  		    String paramUsage = Aadl2Utils.getParameter_Usage(p);
  			if(p==returnParameter)
  			  continue;
  			if(first==false)
  			{
  			  _subprogramsClass.addOutput(", ") ;
  			}
  			processDataSubcomponentType(object, p.getDataFeatureClassifier(), _subprogramsClass, _subprogramsClass);
  			processDataSubcomponentType(object, p.getDataFeatureClassifier(), _subprogramsClass, _subprogramsClass);
  			if(Aadl2Utils.isInOutParameter(p) ||
  					Aadl2Utils.isOutParameter(p)
  					|| paramUsage.equalsIgnoreCase("by_reference"))
  			{
  			  _subprogramsClass.addOutput(" * ") ;
  			}
  			_subprogramsClass.addOutput(" "+p.getName());
  			first=false;
  			
  			process(p.getDataFeatureClassifier());
  		  }
  		  else if(f instanceof DataAccess)
  		  {
  			DataAccess da = (DataAccess) f ;
  			if(first==false)
  			{
  			  _subprogramsClass.addOutput(", ") ;
  			}
  			processDataSubcomponentType(object, da.getDataFeatureClassifier(), _subprogramsClass, _currentClass);
  			processDataSubcomponentType(object, da.getDataFeatureClassifier(), _subprogramsClass, _subprogramsClass);
  			if(da.getKind().equals(AccessType.REQUIRES))
  			{
  			  if(Aadl2Utils.isReadWriteDataAccess(da) ||
  					  Aadl2Utils.isWriteOnlyDataAccess(da))
  			  {
  				_subprogramsClass.addOutput(" * ") ;
  			  }
  			  _subprogramsClass.addOutput(" "+da.getName());
  			}
  			first=false;
  			
  			process(da.getDataFeatureClassifier());
  		  }
    	}

    	_subprogramsClass.addOutputNewline(")");
  	    _subprogramsClass.addOutputNewline("{");
  	    _subprogramsClass.incrementIndent();
      	
    	return null;
      }
      
      public String caseSubprogramImplementation(SubprogramImplementation object)
      {
    	_currentClass = _subprogramsClass; 
    	if(_processedTypes.contains(object.getQualifiedName()))
        {
          return DONE ;
        }
        _processedTypes.add(object.getQualifiedName());
        try {
          resolveExistingCodeDependencies(object,  _subprogramsClass);
        } catch (Exception e1) {
      	  caseSubprogramClassifier((SubprogramClassifier) object);
        
    	  for(DataSubcomponent d : object.getOwnedDataSubcomponents())
          {
            process(d) ;
          }

          processBehavioredImplementation(object) ;

          _subprogramsClass.decrementIndent();
          _subprogramsClass.addOutputNewline("}");
        }
        AadlBaToJavaUnparser baToCUnparser =
          		(AadlBaToJavaUnparser) baToCUnparserAction.getUnparser() ;
          _subprogramsClass.addOutput (baToCUnparser.getTypesContent());  
    	return DONE;
      }
      
      public String caseSubprogramCall(SubprogramCall object)
      {
    	  Parameter returnParameter = null;

    	  if(object.getCalledSubprogram() != null)
    	  {
    		  SubprogramType st = null ;
    		  
    		  SubprogramSubcomponentType sct;
    		  if(object.getCalledSubprogram() instanceof SubprogramAccess)
    			  sct = ((SubprogramAccess)object.getCalledSubprogram()).getSubprogramFeatureClassifier();
    		  else
    			  sct = (SubprogramSubcomponentType) object.getCalledSubprogram() ;


    		  if(sct instanceof SubprogramType)
    		  {
    			  st = (SubprogramType) sct ;
    		  }
    		  else
    		  {
    			  SubprogramImplementation si = (SubprogramImplementation) sct ;
    			  st = si.getType() ;
    		  }

    		  List<Feature> orderedFeatureList = Aadl2Utils.orderFeatures(st) ;
    		  List<ConnectionEnd> orderedParamValue = new ArrayList<ConnectionEnd>();
    		  for(Feature f: orderedFeatureList)
    		  {
    			  ConnectionEnd ce = Aadl2Utils.getConnectedEnd(object, f);
    			  orderedParamValue.add(ce);
    		  }

    		  for(Feature param: orderedFeatureList)
    		  {
    			  if(param instanceof Parameter)
    			  {
    				  Parameter p = (Parameter) param ;
    				  boolean isReturnParam = GenerationUtilsJava.isReturnParameter(p);;
    				  
    				  if(isReturnParam)
    				  {
    					  returnParameter = p;
    					  ConnectionEnd ce = orderedParamValue.get(orderedFeatureList.indexOf(p));
						  processConnectionEnd(ce);
						  _currentClass.addOutput(" = ");
    					  break;
    				  }
    			  }
    		  }
    		

    		  try
    		  {
    			resolveExistingCodeDependencies(sct, _currentClass);
    		  }
    		  catch (Exception e1)
    		  {
    			_currentClass.addOutput("Subprograms." + GenerationUtilsJava.getGenerationJavaIdentifier(sct.getQualifiedName()));
    		  }
    		  
    		  if(st != null)
    		  {  
    			  _currentClass.addOutput(" (") ;
    			  boolean first = true;
    			  for(Feature feature: orderedFeatureList)
    			  {
    				  if(feature instanceof Parameter)
    				  {
    					  Parameter p = (Parameter) feature ;
    					  if(p==returnParameter)
    						  continue;
    					  if(first==false)
    						  _currentClass.addOutput(", ") ;
    					  String paramUsage = Aadl2Utils.getParameter_Usage(p);
    					  if(Aadl2Utils.isInOutParameter(p) ||
    							  Aadl2Utils.isOutParameter(p)
    							  || paramUsage.equalsIgnoreCase("by_reference"))
    					  {
    						  _currentClass.addOutput("&") ;
    					  }
    					  ConnectionEnd ce = orderedParamValue.get(orderedFeatureList.indexOf(p));
    					  if(ce != null)
						    processConnectionEnd(ce);
    					  first=false;
    				  }
    				  else if(feature instanceof DataAccess)
    				  {
    					  DataAccess da = (DataAccess) feature ;
    					  if(first==false)
    						  _currentClass.addOutput(", ") ;
    					  if(da.getKind().equals(AccessType.REQUIRES))
    					  {
    						  if(Aadl2Utils.isReadWriteDataAccess(da)
    								  || Aadl2Utils.isWriteOnlyDataAccess(da))
    						  {
    							  _currentClass.addOutput("&") ;
    						  }
    					  }

    					  String name = null ;

    					  // If a data access mapping is provided:
    					  // Transforms any data access into the right data subcomponent
    					  // 's name thanks to the given data access mapping.
    					  ConnectionEnd parentAccess = orderedParamValue.get(orderedFeatureList.indexOf(da));
    					  if(_dataAccessMapping != null && parentAccess instanceof DataAccess)
    					  {
    						  name = _dataAccessMapping.get((DataAccess)parentAccess);
    					  }

    					  if (name != null)
    					  {
    						  _currentClass.addOutput(name);
    					  }
    					  else
    					  {
    						  processConnectionEnd(parentAccess);
    					  }
    					  first=false;
    				  }

    			  }

    			  _currentClass.addOutputNewline(");") ;
    		  }
    		  process(sct);
    	  }
    	  return DONE ;
      }
      
      private void processConnectionEnd(ConnectionEnd ce)
      {
		  if(ce instanceof DataSubcomponent)
			  _currentClass.addOutput(
					  GenerationUtilsJava.getGenerationJavaIdentifier(ce.getQualifiedName()));
		  else
		  {
			_currentClass.addOutput(ce.getName());
		  }
      }
      
      public String caseThreadSubcomponent(ThreadSubcomponent object)
      {
    	  threadTable.put(object, threadIndex++);
        process(object.getComponentImplementation()) ;
        return null ;
      }

      public String caseThreadType(ThreadType object)
      {
        if(_processedTypes.contains(object.getQualifiedName()))
        {
          return null ;
        }

        _processedTypes.add(object.getQualifiedName()) ;
        _currentClass = _currentActivity ;

        for(DataAccess d : object.getOwnedDataAccesses())
        {
          if(d.getKind().equals(AccessType.REQUIRES))
          {
            process(d) ;
          }
        }

        return null ;
      }

      /**
       *  subprogram group access
       */
      public String caseSubprogramGroupAccess(SubprogramGroupAccess object)
      {
        return DONE ;
      }

      /**
       * data access
       */
      public String caseDataAccess(DataAccess object)
      {
        _currentClass.addOutput("extern ") ;
        
        String dataSubprogramName = null ;
        
        if(_dataAccessMapping != null)
        {
          dataSubprogramName = _dataAccessMapping.get(object) ;
        }
        
        
        try
        {
          resolveExistingCodeDependencies(object.getDataFeatureClassifier(),
                               _currentClass);
        }
        catch(Exception e)
        {
          _currentClass.addOutput(GenerationUtilsJava
                .getGenerationJavaIdentifier(object.getDataFeatureClassifier()
                      .getQualifiedName())) ;
        }
        _currentClass.addOutput(" ") ;
        if(dataSubprogramName != null)
        {
          _currentClass.addOutput(dataSubprogramName);
        }
        else
        {
          _currentClass.addOutput(GenerationUtilsJava
                .getGenerationJavaIdentifier(object.getQualifiedName())) ;
        }

        _currentClass.addOutputNewline(";") ;
        
        
        return DONE ;
      }
    } ;
  }

  @Override
  public void process(Element element, File generatedFilePath) 
                                                    throws GenerationException
  {
    AadlToJavaSwitchProcess.process(element) ;
    saveGeneratedFilesContent(generatedFilePath) ;
    // Reset all AadlToCSwitchProcess private attributes !
    init() ;
  }

  @Override
  public void setParameters(Map<Enum<?>, Object> parameters)
  {
    throw new UnsupportedOperationException() ;
  }
}