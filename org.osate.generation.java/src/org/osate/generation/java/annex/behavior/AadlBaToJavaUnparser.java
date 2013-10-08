
package org.osate.generation.java.annex.behavior ;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.AbstractEnumerator;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.EcoreUtil2;
import org.osate.aadl2.AccessType;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.ArrayDimension;
import org.osate.aadl2.BehavioredImplementation;
import org.osate.aadl2.DataAccess;
import org.osate.aadl2.DataClassifier;
import org.osate.aadl2.DataSubcomponent;
import org.osate.aadl2.Element;
import org.osate.aadl2.Feature;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Parameter;
import org.osate.aadl2.ParameterConnectionEnd;
import org.osate.aadl2.PrototypeBinding;
import org.osate.aadl2.Subprogram;
import org.osate.aadl2.SubprogramClassifier;
import org.osate.aadl2.SubprogramImplementation;
import org.osate.aadl2.SubprogramSubcomponentType;
import org.osate.aadl2.SubprogramType;
import org.osate.aadl2.ThreadClassifier;
import org.osate.aadl2.modelsupport.AadlConstants;
import org.osate.aadl2.modelsupport.UnparseText;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.generation.java.AadlToJavaUnparser;
import org.osate.generation.java.GenerationUtilsJava;

import fr.tpt.aadl.annex.behavior.aadlba.Any;
import fr.tpt.aadl.annex.behavior.aadlba.AssignmentAction;
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorActionBlock;
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorActionSequence;
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorActionSet;
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorActions;
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorAnnex;
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorBooleanLiteral;
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorElement;
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorEnumerationLiteral;
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorIntegerLiteral;
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorRealLiteral;
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorState;
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorStringLiteral;
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorTime;
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorTransition;
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorVariable;
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorVariableHolder;
import fr.tpt.aadl.annex.behavior.aadlba.BinaryAddingOperator;
import fr.tpt.aadl.annex.behavior.aadlba.BinaryNumericOperator;
import fr.tpt.aadl.annex.behavior.aadlba.CalledSubprogramHolder;
import fr.tpt.aadl.annex.behavior.aadlba.DataAccessHolder;
import fr.tpt.aadl.annex.behavior.aadlba.DataComponentReference;
import fr.tpt.aadl.annex.behavior.aadlba.DataHolder;
import fr.tpt.aadl.annex.behavior.aadlba.DataSubcomponentHolder;
import fr.tpt.aadl.annex.behavior.aadlba.DispatchCondition;
import fr.tpt.aadl.annex.behavior.aadlba.DispatchConjunction;
import fr.tpt.aadl.annex.behavior.aadlba.DispatchTriggerConditionStop;
import fr.tpt.aadl.annex.behavior.aadlba.DispatchTriggerLogicalExpression;
import fr.tpt.aadl.annex.behavior.aadlba.ElementHolder;
import fr.tpt.aadl.annex.behavior.aadlba.ElementValues;
import fr.tpt.aadl.annex.behavior.aadlba.ElseStatement;
import fr.tpt.aadl.annex.behavior.aadlba.Factor;
import fr.tpt.aadl.annex.behavior.aadlba.ForOrForAllStatement;
import fr.tpt.aadl.annex.behavior.aadlba.IfStatement;
import fr.tpt.aadl.annex.behavior.aadlba.IndexableElement;
import fr.tpt.aadl.annex.behavior.aadlba.IntegerRange;
import fr.tpt.aadl.annex.behavior.aadlba.IntegerValue;
import fr.tpt.aadl.annex.behavior.aadlba.IntegerValueConstant;
import fr.tpt.aadl.annex.behavior.aadlba.IntegerValueVariable;
import fr.tpt.aadl.annex.behavior.aadlba.LockAction;
import fr.tpt.aadl.annex.behavior.aadlba.LogicalOperator;
import fr.tpt.aadl.annex.behavior.aadlba.MultiplyingOperator;
import fr.tpt.aadl.annex.behavior.aadlba.Otherwise;
import fr.tpt.aadl.annex.behavior.aadlba.ParameterHolder;
import fr.tpt.aadl.annex.behavior.aadlba.ParameterLabel;
import fr.tpt.aadl.annex.behavior.aadlba.PortCountValue;
import fr.tpt.aadl.annex.behavior.aadlba.PortDequeueAction;
import fr.tpt.aadl.annex.behavior.aadlba.PortDequeueValue;
import fr.tpt.aadl.annex.behavior.aadlba.PortFreezeAction;
import fr.tpt.aadl.annex.behavior.aadlba.PortFreshValue;
import fr.tpt.aadl.annex.behavior.aadlba.PortSendAction;
import fr.tpt.aadl.annex.behavior.aadlba.Relation;
import fr.tpt.aadl.annex.behavior.aadlba.RelationalOperator;
import fr.tpt.aadl.annex.behavior.aadlba.SimpleExpression;
import fr.tpt.aadl.annex.behavior.aadlba.SubprogramCallAction;
import fr.tpt.aadl.annex.behavior.aadlba.SubprogramHolder;
import fr.tpt.aadl.annex.behavior.aadlba.Term;
import fr.tpt.aadl.annex.behavior.aadlba.TimedAction;
import fr.tpt.aadl.annex.behavior.aadlba.UnaryAddingOperator;
import fr.tpt.aadl.annex.behavior.aadlba.UnaryBooleanOperator;
import fr.tpt.aadl.annex.behavior.aadlba.UnaryNumericOperator;
import fr.tpt.aadl.annex.behavior.aadlba.UnlockAction;
import fr.tpt.aadl.annex.behavior.aadlba.Value;
import fr.tpt.aadl.annex.behavior.aadlba.ValueExpression;
import fr.tpt.aadl.annex.behavior.aadlba.WhileOrDoUntilStatement;
import fr.tpt.aadl.annex.behavior.aadlba.util.AadlBaSwitch;
import fr.tpt.aadl.annex.behavior.analyzers.TypeHolder;
import fr.tpt.aadl.annex.behavior.unparser.AadlBaUnparser;
import fr.tpt.aadl.annex.behavior.utils.AadlBaUtils;
import fr.tpt.aadl.annex.behavior.utils.AadlBaVisitors;
import fr.tpt.aadl.annex.behavior.utils.DimensionException;
import fr.tpt.aadl.ramses.util.generation.GeneratorUtils;
import fr.tpt.aadl.utils.Aadl2Utils;
import fr.tpt.aadl.utils.PropertyUtils;


public class AadlBaToJavaUnparser extends AadlBaUnparser
{

  protected Map<DataAccess, String> _dataAccessMapping = null ;
  protected UnparseText _codeContent = null ;
  protected UnparseText _typesContent = null ;
  protected Set<String> _additionalHeaders = new HashSet<String>() ;
  private NamedElement _owner ;
  
  private List<NamedElement> coreElementsToBeUnparsed = new ArrayList<NamedElement>();
  
  public List<NamedElement> getCoreElementsToBeUnparsed() {
	return coreElementsToBeUnparsed;
  }

public AadlBaToJavaUnparser(AnnexSubclause subclause,
                           String indent,
                           Map<DataAccess, String> dataAccessMapping)
  {
    super(subclause, indent) ;
    _dataAccessMapping = dataAccessMapping ;
  }

  public AadlBaToJavaUnparser()
  {
    super() ;
  }

  public void setDataAccessMapping(Map<DataAccess, String> dataAccessMapping)
  {
    _dataAccessMapping = dataAccessMapping ;
  }
  
  public String getCodeContent()
  {
    return _codeContent.getParseOutput() ;
  }

  
  public String getTypesContent()
  {
    return _typesContent.getParseOutput() ;
  }

  public Set<String> getAdditionalHeaders()
  {
    return _additionalHeaders ;
  }

  public void processEList(UnparseText aadlText,
                           EList<?> list,
                           String separator)
  {
    boolean first = true ;

    for(Iterator<?> it = list.iterator() ; it.hasNext() ;)
    {
      if(!first)
      {
        if(separator == AadlConstants.newlineChar)
        {
          aadlText.addOutputNewline(AadlConstants.emptyString) ;
        }
        else
        {
          aadlText.addOutput(separator) ;
        }
      }

      first = false ;
      Object o = it.next() ;

      if(o instanceof ElementHolder)
      {
        process((ElementHolder) o) ;
      }
      else if(o instanceof AbstractEnumerator)
        aadlText.addOutput(((AbstractEnumerator) o).getName().toLowerCase()) ;
      else if(o instanceof String)
      {
        aadlText.addOutput((String) o) ;
      }
      else
      {
        aadlText.addOutput("processEList: oh my, oh my!!") ;
      }
    }
  }

  public void processEList(UnparseText aadlText,
                           final List<? extends BehaviorElement> list)
  {
    for(Iterator<? extends BehaviorElement> it = list.iterator() ; it.hasNext() ;)
    {
      process(it.next()) ;
    }
  }

  

  protected static String getInitialStateIdentifier(NamedElement component, BehaviorAnnex ba)
  {
    for(BehaviorState s : ba.getStates())
    {
      if(s.isInitial())
      {
    	return (getStateEnumName (component) + "." + getStateEnumIdentifier(component, s) );

      }
    }

    return null ;
  }

  protected static String getTargetLanguageOperator(LogicalOperator operator)
  {
    if(operator.equals(LogicalOperator.AND))
    {
      return "&&" ;
    }

    if(operator.equals(LogicalOperator.OR))
    {
      return "||" ;
    }

    if(operator.equals(LogicalOperator.XOR))
    {
      return "^" ;
    }

    return operator.getLiteral() ;
  }

  protected static String getTargetLanguageOperator(MultiplyingOperator operator)
  {
    if(operator.equals(MultiplyingOperator.DIVIDE))
    {
      return "/" ;
    }

    if(operator.equals(MultiplyingOperator.MULTIPLY))
    {
      return "*" ;
    }

    if(operator.equals(MultiplyingOperator.MOD))
    {
      return "%" ;
    }

    // TODO: find mapping for REM operator
    if(operator.equals(MultiplyingOperator.REM))
    {
      throw new UnsupportedOperationException() ;
    }

    return operator.getLiteral() ;
  }

  protected static String getTargetLanguageOperator(UnaryNumericOperator operator)
  {
    if(operator.equals(UnaryNumericOperator.ABS))
    {
      return "aadl_runtime_abs" ;
    }

    return operator.getLiteral() ;
  }

  protected static String getTargetLanguageOperator(BinaryNumericOperator operator)
  {
    if(operator.equals(UnaryNumericOperator.ABS))
    {
      return "aadl_runtime_exp(" ;
    }

    return operator.getLiteral() ;
  }

  protected static String getTargetLanguageOperator(UnaryBooleanOperator operator)
  {
    if(operator.equals(UnaryBooleanOperator.NOT))
    {
      return "!" ;
    }

    return operator.getLiteral() ;
  }

  protected static String getTargetLanguageOperator(RelationalOperator operator)
  {
    if(operator.equals(RelationalOperator.EQUAL))
    {
      return "==" ;
    }

    if(operator.equals(RelationalOperator.NOT_EQUAL))
    {
      return "!=" ;
    }

    return operator.getLiteral() ;
  }

  private static String getStateEnumIdentifier (NamedElement aadlComponent, BehaviorState state )
  {
      return GenerationUtilsJava.getGenerationJavaIdentifier(aadlComponent.getQualifiedName()+state.getName());
  }
  
  private static String getStateEnumName (NamedElement aadlComponent)
  {
	  return (GenerationUtilsJava.getGenerationJavaIdentifier(aadlComponent
              .getQualifiedName()) +"State") ;
  }
  
  @Override
  protected void initSwitches()
  {
    aadlbaSwitch = new AadlBaSwitch<String>()
    {

	/**
       * Top-level method to unparse "behavior_specification"
       * annexsubclause
       */
      public String caseAnnexSubclause(AnnexSubclause object)
      {
        process((BehaviorAnnex) object) ;
        return DONE ;
      }

      /**
       * Unparse behaviorannex
       */
      public String caseBehaviorAnnex(BehaviorAnnex object)
      {
    	BehaviorAnnex ba = (BehaviorAnnex) object ;
    	NamedElement aadlComponent = _owner ;
    	String aadlComponentCId =
    		  GenerationUtilsJava.getGenerationJavaIdentifier(aadlComponent
                  .getQualifiedName()) ;
        
        _codeContent = new UnparseText() ;
        _typesContent = new UnparseText ();
        _typesContent.addOutput("enum ") ;
        _typesContent.addOutputNewline(getStateEnumName (aadlComponent)) ;
        _typesContent.addOutputNewline("{") ;
        _typesContent.incrementIndent() ;

        for(BehaviorState state : ba.getStates())
        {
          
          String stateCId = getStateEnumIdentifier (aadlComponent, state);
        		  
          _typesContent.addOutput(stateCId);


          if(ba.getStates().indexOf(state) < ba.getStates()
                .size() - 1)
          {
        	  _typesContent.addOutput(",") ;
          }

          _typesContent.addOutputNewline("") ;
        }

        _typesContent.decrementIndent() ;
        
        _typesContent.addOutputNewline("}") ;
        
        
    
        _codeContent.addOutputNewline(aadlComponentCId +
              "State currentState = " + 
      
        AadlBaToJavaUnparser.getInitialStateIdentifier(aadlComponent, ba) + ";") ;

        processEList(_codeContent, ba.getVariables()) ;
        _codeContent.addOutputNewline("while (true)") ;
        _codeContent.addOutputNewline("{") ;
        _codeContent.incrementIndent() ;
        _codeContent.addOutputNewline("switch(currentState)") ;
        _codeContent.addOutputNewline("{") ;
        _codeContent.incrementIndent() ;
        for(BehaviorState state : ba.getStates())
        {
          if(AadlBaVisitors.getTransitionWhereSrc
                (state).isEmpty() == false)
          {
            _codeContent.addOutputNewline
            	("case " + getStateEnumIdentifier(aadlComponent, state) + ":") ;
            processEList(_codeContent, (ArrayList<BehaviorTransition>) AadlBaVisitors.
                         getTransitionWhereSrc(state)) ;
          }
        }
        _codeContent.decrementIndent() ;
        _codeContent.addOutputNewline("}") ;
        _codeContent.decrementIndent() ;
        _codeContent.addOutputNewline("}") ;
        _codeContent.decrementIndent() ;
        
        return DONE ;
      }

      /**
       * Unparse behaviorvariable
       */
      public String caseBehaviorVariable(BehaviorVariable object)
      {
        String sourceName ;
        try
        {
          sourceName = PropertyUtils.
                getStringValue(object.getDataClassifier(), "Source_Name") ;
        }
        catch (Exception e)
        {
          sourceName = GenerationUtilsJava.
                getGenerationJavaType(object.
                                         getDataClassifier().getQualifiedName());
        }
        _codeContent.addOutput(sourceName);
        _codeContent.addOutput(" " + object.getName()) ;
        caseArrayDimensions(object.getArrayDimensions()) ;
        String init = GeneratorUtils.getInitialValue(object.getDataClassifier(), "java") ;
        coreElementsToBeUnparsed.add(object.getDataClassifier());
        if(!init.isEmpty())
        {
        	_codeContent.addOutput(" = "+init) ;
        }
        else
        {
        	_codeContent.addOutput(" = null") ;	
        }
        _codeContent.addOutputNewline(";") ;
        
        
        
        return DONE ;
      }

      /**
       * Unparse arraysize
       */
      public String caseArrayDimensions(EList<ArrayDimension> arrayDimensions)
      {
        for(ArrayDimension ivc : arrayDimensions)
        {
          _codeContent.addOutput("[") ;
          _codeContent.addOutput(Long.toString(ivc.getSize().getSize()));
          _codeContent.addOutput("]") ;
        }

        return DONE ;
      }

      public String caseBehaviorEnumerationLiteral(BehaviorEnumerationLiteral object)
      {
        // ComponentPropertyValue is defined to refer Enumerated data
    	NamedElement component = object.getComponent();
    	if(component!=null)
    	{
    		try
    	    {
    	      String sourceName = PropertyUtils.getStringValue(component, "Source_Name") ;
    	      _codeContent.addOutput(object.getEnumLiteral().getValue());
    	    }
    		catch(Exception e)
    		{
    			_codeContent.addOutput(GenerationUtilsJava.getGenerationJavaType(component.getQualifiedName()) + "." + GenerationUtilsJava.getGenerationJavaIdentifier(component.getQualifiedName())+"_"+object.getEnumLiteral().getValue());
    		}
    	}
    	else
    	{
    		_codeContent.addOutput(object.getEnumLiteral().getValue());
    	}
        return DONE ;
      }
      
      /**
       * Unparse DataComponentReference
       */
      public String caseDataComponentReference(DataComponentReference object)
      {
    	Iterator<DataHolder> itDataHolder = object.getData().iterator() ;
    	process(itDataHolder.next());
        while(itDataHolder.hasNext())
        {
        	_codeContent.addOutput(".");
        	_codeContent.addOutput(((DataHolder)itDataHolder.next()).getElement().getName());
        }

        return DONE ;
      }

      /**
       * Unparse behaviorstate
       * @object: input parameter, destination of a Behavior Transition
       */
      public String caseBehaviorState(BehaviorState object)
      {
        NamedElement aadlComponent = _owner ;
        String aadlComponentCId =
              GenerationUtilsJava.getGenerationJavaIdentifier(aadlComponent
                    .getQualifiedName()) ;
        //if (object.isComplete())

        if(object.isFinal())
        {
          _codeContent.addOutputNewline("return;") ;
        }
        else
        {
          _codeContent.addOutputNewline("currentState = " + getStateEnumName(aadlComponent) + "." + getStateEnumIdentifier(aadlComponent, object) + ";") ;
          _codeContent.addOutputNewline("break;") ;
        }
        return DONE ;
      }

      
      /**
       * Unparse behaviortransition
       */
      public String caseBehaviorTransition(BehaviorTransition object)
      {
        aadlbaText = _codeContent ;
        long num = object.getPriority() ;


        _codeContent.addOutput("// Transition id: " + object.getName()) ;


        if(num != -1) // numeral
        {
          _codeContent.addOutput(" -- Priority " +
                String.valueOf(num)) ;
        }

        _codeContent.addOutputNewline("") ;

        if(object.getCondition() != null)
        {
          if(object.getCondition() instanceof Otherwise)
          {
            _codeContent.addOutput("else") ;
            process(object.getCondition()) ;
          }
          else
          {
            _codeContent.addOutput("if (") ;
            process(object.getCondition()) ;
            _codeContent.addOutputNewline(")") ;
          }
        }
        else
        {
          _codeContent.addOutputNewline("if (true) // no execution condition") ;
        }

        _codeContent.addOutputNewline("{") ;
        _codeContent.incrementIndent() ;

        if(object.getActionBlock() != null)
        {
          process(object.getActionBlock()) ;
        }

        process((BehaviorState) object.getDestinationState()) ;
        _codeContent.decrementIndent() ;
        _codeContent.addOutputNewline("}") ;
        return DONE ;
      }


      public String caseOtherwise(Otherwise object)
      {
        _codeContent.addOutputNewline(" //otherwise") ;
        return DONE ;
      }

      /**
       * Unparse dispatchcondition
       */
      public String caseDispatchCondition(DispatchCondition object)
      {
        throw new UnsupportedOperationException() ;
      }

      public String caseDispatchTriggerConditionStop(DispatchTriggerConditionStop object)
      {
        throw new UnsupportedOperationException() ;
      }

      public String caseDispatchTriggerLogicalExpression(DispatchTriggerLogicalExpression object)
      {
        throw new UnsupportedOperationException() ;
      }

      public String caseDispatchConjunction(DispatchConjunction object)
      {
        throw new UnsupportedOperationException() ;
      }

      public String caseBehaviorActionBlock(BehaviorActionBlock object)
      {
        process(object.getContent()) ;

        if(object.getTimeout() != null)
        {
          throw new UnsupportedOperationException() ;
        }

        return DONE ;
      }

      public String caseBehaviorActionSequence(BehaviorActionSequence object)
      {
        processEList(_codeContent, object.getActions()) ;
        return DONE ;
      }

      public String caseBehaviorActionSet(BehaviorActionSet object)
      {
        throw new UnsupportedOperationException() ;
      }

      
      /**
       * Unparse elsestatement
       */
      public String caseElseStatement(ElseStatement object)
      {
        BehaviorActions lba = object.getBehaviorActions() ;
        _codeContent.addOutput("else ") ;
        _codeContent.addOutputNewline("{") ;
        process(lba) ;
        _codeContent.addOutputNewline("}") ;
        return DONE ;
      }
      
      /**
       * Unparse ifstatement
       */
      public String caseIfStatement(IfStatement object)
      {
        ValueExpression ve = object.getLogicalValueExpression() ;
        BehaviorActions lba = object.getBehaviorActions() ;

        if(object.isElif())
          _codeContent.addOutput("else if (") ;
        else
          _codeContent.addOutput("if (") ;
        
        process(ve) ;
        _codeContent.addOutput(") ") ;

        
        _codeContent.addOutputNewline("{") ;
        process(lba) ;
        _codeContent.addOutputNewline("}") ;

        return DONE ;
      }

      public String toInteger(IntegerValue integerValue)
      {
        if(integerValue instanceof IntegerValueConstant)
        {
          return Long.toString(((BehaviorIntegerLiteral) integerValue)
                .getValue()) ;
        }

        if(integerValue instanceof IntegerValueVariable)
        {
          if(integerValue instanceof BehaviorVariable)
          {
            return ((BehaviorVariable) integerValue).getDataClassifier()
                  .getName();
          }

          if(integerValue instanceof DataComponentReference)
          {
            return ((BehaviorVariable) integerValue).getDataClassifier()
                  .getName();
          }
        }

        return "" ;
      }

      /**
       * Unparse fororforallstatement
       */
      public String caseForOrForAllStatement(ForOrForAllStatement object)
      {
        ElementValues set = object.getIteratedValues() ;

        if(set instanceof IntegerRange)
        {
          IntegerRange range = (IntegerRange) set ;
          String lowerRangeValue = this.toInteger(range.getLowerIntegerValue()) ;
          String upperRangeValue = this.toInteger(range.getUpperIntegerValue()) ;
          _codeContent.addOutputNewline("int iter=0;") ;
          _codeContent.addOutputNewline("for (iter=" + lowerRangeValue +
                ";iter<=" + upperRangeValue + ";iter++)") ;
          _codeContent.addOutputNewline("{") ;
          _codeContent.incrementIndent() ;
          DataClassifier iterativeVariableClassifier = object.getIterativeVariable().getDataClassifier() ;
          try
          {
            GenerationUtilsJava.resolveExistingCodeDependencies(iterativeVariableClassifier);
          } catch(Exception e)
          {
            _codeContent.addOutput(GenerationUtilsJava.
                                    getGenerationJavaIdentifier(iterativeVariableClassifier.
                                    getQualifiedName()));
          }
          
          _codeContent.addOutput(" ") ;
          _codeContent.addOutput(object.getIterativeVariable().getName()) ;
          _codeContent.addOutputNewline(" = iter;") ;
          process(object.getBehaviorActions()) ;
          _codeContent.decrementIndent() ;
          _codeContent.addOutputNewline("}") ;
        }

        if(set instanceof DataComponentReference)
        {
          TypeHolder dataReferenceTypeHolder = null ;

          try
          {
            dataReferenceTypeHolder =
                  AadlBaUtils.getTypeHolder(object.getIteratedValues()) ;
          }
          catch(DimensionException e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace() ;
          }

          int numberOfLoop = dataReferenceTypeHolder.dimension ;

          for(int i = 0 ; i < numberOfLoop ; i++)
          {
            String iteratorID = "iter" + Integer.toString(i) ;
            _codeContent.addOutputNewline("int " + iteratorID + "=0;") ;
            _codeContent.addOutputNewline("for (" + iteratorID + "=0;" +
                  iteratorID + "<" +
                  Long.toString(dataReferenceTypeHolder.dimension_sizes[i]) +
                  ";" + iteratorID + "++)") ;
            _codeContent.addOutputNewline("{") ;
            _codeContent.incrementIndent() ;
          }

          DataClassifier iterativeVariableClassifier = object.getIterativeVariable().getDataClassifier() ;
          try
          {
            String existing = GenerationUtilsJava.resolveExistingCodeDependencies(iterativeVariableClassifier);
            aadlbaText.addOutput(existing);
          }
          catch(Exception e)
          {
            _codeContent.addOutput(GenerationUtilsJava.
                                    getGenerationJavaIdentifier(iterativeVariableClassifier.getQualifiedName()));
          }
          _codeContent.addOutput(" ") ;
          _codeContent.addOutput(object.getIterativeVariable().getName()) ;
          _codeContent.addOutput(" = ") ;
          process(object.getIteratedValues()) ;

          for(int i = 0 ; i < numberOfLoop ; i++)
          {
            String iteratorID = "iter" + Integer.toString(i) ;
            _codeContent.addOutput("[") ;
            _codeContent.addOutput(iteratorID) ;
            _codeContent.addOutput("]") ;
          }

          _codeContent.addOutputNewline(";") ;
          process(object.getBehaviorActions()) ;

          for(int i = 0 ; i < numberOfLoop ; i++)
          {
            _codeContent.decrementIndent() ;
            _codeContent.addOutputNewline("}") ;
          }
        }

        return DONE ;
      }

      public String caseWhileOrDoUntilStatement(WhileOrDoUntilStatement object)
      {
        if(object.isDoUntil())
        {
          return caseDoUntilStatement(object) ;
        }
        else
        {
          return caseWhileStatement(object) ;
        }
      }

      /**
       * Unparse whilestatement
       */
      public String caseWhileStatement(WhileOrDoUntilStatement object)
      {
        //FIXME : TODO : update location reference
        _codeContent.addOutput("while (") ;
        process(object.getLogicalValueExpression()) ;
        _codeContent.addOutputNewline(")") ;
        _codeContent.addOutputNewline("{") ;
        _codeContent.incrementIndent() ;
        process(object.getBehaviorActions()) ;
        _codeContent.decrementIndent() ;
        _codeContent.addOutputNewline("}") ;
        return DONE ;
      }

      /**
       * Unparse dountilstatement
       */
      public String caseDoUntilStatement(WhileOrDoUntilStatement object)
      {
        //FIXME : TODO : update location reference
        _codeContent.addOutputNewline("do") ;
        _codeContent.addOutputNewline("{") ;
        _codeContent.incrementIndent() ;
        process(object.getBehaviorActions()) ;
        _codeContent.decrementIndent() ;
        _codeContent.addOutputNewline("}") ;
        _codeContent.addOutput("while (") ;
        process(object.getLogicalValueExpression()) ;
        _codeContent.addOutputNewline(")") ;
        return DONE ;
      }

      /**
       * Unparse integerrange
       */
      public String caseIntegerRange(IntegerRange object)
      {
        throw new UnsupportedOperationException() ;
      }

      /**
       * Unparse timedaction
       */
      public String caseTimedAction(TimedAction object)
      {
        throw new UnsupportedOperationException() ;
      }

      /**
       * Unparse assignmentaction
       */
      public String caseAssignmentAction(AssignmentAction object)
      {
        process(object.getTarget()) ;
        _codeContent.addOutput(" = ") ;

        if(object instanceof Any)
        {
          throw new UnsupportedOperationException() ;
        }
        else
        {
          process(object.getValueExpression()) ;
        }

        _codeContent.addOutputNewline(";") ;
        return DONE ;
      }
      
      public boolean manageParameterDirection(Parameter formal, ParameterLabel actual)
      {
    	  boolean remainingParenthesis = false;
    	  String usageP = Aadl2Utils.getParameter_Usage(formal);
          if(Aadl2Utils.isInOutParameter(formal) ||
          		Aadl2Utils.isOutParameter(formal) ||
          		usageP.equalsIgnoreCase("by_reference"))
          {
            if(actual instanceof ParameterHolder)
            {
          	  ParameterHolder ph = (ParameterHolder) actual;
          	  String usage = Aadl2Utils.getParameter_Usage(ph.getParameter());
          	  // in out passed to in
          	  if(!Aadl2Utils.isOutParameter(ph.getParameter()) &&
          		!Aadl2Utils.isInOutParameter(ph.getParameter())
          		&& !usage.equalsIgnoreCase("by_reference"))
          		// and ph.getParameter not by reference
          		  _codeContent.addOutput("*") ;
          		  
            }
            else if (actual instanceof DataSubcomponentHolder
          		  || actual instanceof BehaviorVariableHolder)
          	  _codeContent.addOutput("&") ;
            else if (actual instanceof DataComponentReference)
            {
          	  _codeContent.addOutput("&(") ;
          	  remainingParenthesis=true;
            }
            else if(actual instanceof DataAccessHolder)
            {
              DataAccessHolder dah = (DataAccessHolder) actual;
              if(!Aadl2Utils.isReadWriteDataAccess(dah.getDataAccess()) &&
            		  !Aadl2Utils.isWriteOnlyDataAccess(dah.getDataAccess()))
            	  //TODO: and dah.getAccess not by reference
            	  _codeContent.addOutput("*") ;
            }
            else if(actual instanceof ValueExpression)
            {
          	  ValueExpression ve = (ValueExpression) actual;
            	  Value v = ve.getRelations().get(0).
            			  getFirstExpression().getTerms().get(0).
            			  getFactors().get(0).
            			  getFirstValue();
          	  if(v instanceof DataAccessHolder)
          	  {
          		remainingParenthesis = manageParameterDirection(formal, (ParameterLabel) v);
          	  }
          	  else if (v instanceof ParameterHolder)
          	  {
          		remainingParenthesis = manageParameterDirection(formal, (ParameterLabel) v);
          	  }
          	  else
          	  {
          		_codeContent.addOutput("&") ;
          	  }
            }
          }
          else
          {
          	// if p not by_reference
          	if(actual instanceof ParameterHolder)
            {
          	  ParameterHolder ph = (ParameterHolder) actual;
          	  String usagePH = Aadl2Utils.getParameter_Usage(ph.getElement());
          	  // in to inout
          	  if(Aadl2Utils.isOutParameter(ph.getParameter()) ||
              	Aadl2Utils.isInOutParameter(ph.getParameter()) ||
              	  usagePH.equalsIgnoreCase("by_reference"))
          		  _codeContent.addOutput("*") ;
            }
          	else if(actual instanceof DataAccessHolder)
            {
              DataAccessHolder dah = (DataAccessHolder) actual;
              if(Aadl2Utils.isReadWriteDataAccess(dah.getDataAccess()) &&
            		  Aadl2Utils.isWriteOnlyDataAccess(dah.getDataAccess()))
            	  //TODO: and dah.getAccess not by reference
            	  _codeContent.addOutput("*") ;
            }
            else if(actual instanceof ValueExpression)
            {
          	  ValueExpression ve = (ValueExpression) actual;
            	  Value v = ve.getRelations().get(0).
            			  getFirstExpression().getTerms().get(0).
            			  getFactors().get(0).
            			  getFirstValue();
          	  if(v instanceof DataAccessHolder)
          	  {
          		remainingParenthesis = manageParameterDirection(formal, (ParameterLabel) v);
          	  }
          	  else if (v instanceof ParameterHolder)
          	  {
          		remainingParenthesis = manageParameterDirection(formal, (ParameterLabel) v);
          	  }
            }
          }
          return remainingParenthesis;
      }
      
      public boolean manageAccessDirection(DataAccess formal, ParameterLabel actual)
      {
    	  boolean remainingParenthesis=false;
    	  if(Aadl2Utils.isReadWriteDataAccess(formal)
        		  || Aadl2Utils.isWriteOnlyDataAccess(formal))
          {
        	if(actual instanceof DataAccessHolder)
        	{
        	  DataAccessHolder dah = (DataAccessHolder) actual;
        	  // in out passed to in
          	  if(!Aadl2Utils.isReadWriteDataAccess(dah.getDataAccess()) &&
          		!Aadl2Utils.isWriteOnlyDataAccess(dah.getDataAccess()))
          		//TODO: and dah.getAccess not by reference
          		  _codeContent.addOutput("*") ;
        	}
        	else if(actual instanceof DataSubcomponentHolder
        			|| actual instanceof BehaviorVariableHolder)
        		_codeContent.addOutput("&") ;
        	else if (actual instanceof DataComponentReference)
            {
          	  _codeContent.addOutput("&(") ;
          	  remainingParenthesis=true;
            }
          }
          else
          {
        	//TODO: if da not by_reference
        	if(actual instanceof DataAccessHolder)
          	{
          		DataAccessHolder dah = (DataAccessHolder) actual;
          		// in to inout
        		if(Aadl2Utils.isWriteOnlyDataAccess(dah.getDataAccess()) ||
            		Aadl2Utils.isReadWriteDataAccess(dah.getDataAccess()))
        			_codeContent.addOutput("*") ;
          	}
          }
    	  return remainingParenthesis;
      }
      
      public String caseCalledSubprogramHolder(CalledSubprogramHolder object)
      {
        aadlbaText = _codeContent;
        String referencingExistingCode = GenerationUtilsJava.resolveExistingCodeDependencies(object.getElement());
        if(referencingExistingCode!=null)
        	aadlbaText.addOutput(referencingExistingCode);
        else
        {
        	_codeContent.addOutput(GenerationUtilsJava.
        			getGenerationJavaIdentifier(object.getElement().getQualifiedName()));
        }
        return DONE ;
      }
      
      public String caseSubprogramCallAction(SubprogramCallAction object)
      {
        Parameter returnParameter = null;

        if(object.getSubprogram().getElement() != null)
        {
          SubprogramType st = null ;
          SubprogramSubcomponentType sct =
                (SubprogramSubcomponentType) object.getSubprogram().getElement() ;

          AadlToJavaUnparser aadlCUnparser = AadlToJavaUnparser.getAadlToJavaUnparser(); 
          
          if(sct instanceof SubprogramType)
          {
            st = (SubprogramType) sct ;
          }
          else
          {
            SubprogramImplementation si = (SubprogramImplementation) sct ;
            st = si.getType() ;
          }
          additionalSubprogramsToUnparse(sct);
          List<PrototypeBinding> currentBindings = aadlCUnparser.getCurrentPrototypeBindings(
        		  object.getSubprogram().getElement().getName());
          List<Feature> ordereFeatureList = Aadl2Utils.orderFeatures(st, currentBindings) ;

          for(ParameterLabel pl : object.getParameterLabels())
          {
            ParameterConnectionEnd pce =
                  (ParameterConnectionEnd) ordereFeatureList.get(object
                                                                 .getParameterLabels().indexOf(pl)) ;

            if(pce instanceof Parameter)
            {
              Parameter p = (Parameter) pce ;
              boolean isReturnParam = GenerationUtilsJava.isReturnParameter(p);
              if(isReturnParam)
              {
                returnParameter = p;
                process(pl);
                _codeContent.addOutput(" = ") ;
                break;
              }
            }
          }

          process(object.getSubprogram()) ;

          if(st != null)
          {
            _codeContent.addOutput(" (") ;
            boolean first = true;
            boolean remainingParenthesis = false;
            for(ParameterLabel pl : object.getParameterLabels())
            {
              ParameterConnectionEnd pce =
                    (ParameterConnectionEnd) ordereFeatureList.get(object
                                                                   .getParameterLabels().indexOf(pl)) ;

              if(pce instanceof Parameter)
              {
                Parameter p = (Parameter) pce ;
                if(p==returnParameter)
                  continue;
                if(first==false)
                  _codeContent.addOutput(", ") ;
                
                remainingParenthesis = manageParameterDirection(p, pl);

                if(pl instanceof ElementHolder)
                {
                  ElementHolder eh = (ElementHolder) pl;
                  if(eh instanceof ParameterHolder
                		  || eh instanceof DataAccessHolder)
                	  _codeContent.addOutput(eh.getElement().getName());
                  else
                	  _codeContent.addOutput(GenerationUtilsJava.getGenerationJavaIdentifier(eh.getElement().getQualifiedName()));
                } 
                else if(pl instanceof ValueExpression)
                {
              	  ValueExpression ve = (ValueExpression) pl;
              	  Value v = ve.getRelations().get(0).
              			  getFirstExpression().getTerms().get(0).
              			  getFactors().get(0).
              			  getFirstValue();
              	  if(v instanceof ElementHolder)
              	  {
              		ElementHolder eh = (ElementHolder) v;
              		if(eh instanceof ParameterHolder
                  		  || eh instanceof DataAccessHolder)
                  	  _codeContent.addOutput(eh.getElement().getName());
                    else
                  	  _codeContent.addOutput(GenerationUtilsJava.getGenerationJavaIdentifier(eh.getElement().getQualifiedName()));
              	  }
              	  else if(v instanceof DataComponentReference)
              	  {
              		DataComponentReference dcr = (DataComponentReference) v;
              		boolean firstElement = true;
              		for(DataHolder dh: dcr.getData())
              		{
              		  boolean lastElement = (dcr.getData().indexOf(dh)==dcr.getData().size()-1);
              		  if(dh instanceof ParameterHolder
                            || dh instanceof DataAccessHolder
                            || firstElement == false)
                        _codeContent.addOutput(dh.getElement().getName());
                      else
                      {
                        _codeContent.addOutput(GenerationUtilsJava.getGenerationJavaIdentifier(dh.getElement().getQualifiedName()));
                        firstElement=false;
                      }
              		  if(!lastElement)
              			  _codeContent.addOutput(".");
              		}
              	  }
                }
                else
                {
                  process(pl) ;
                }
                if(remainingParenthesis)
                {
                	_codeContent.addOutput(")");
                	remainingParenthesis=false;
                }
                first=false;
              }
              else if(pce instanceof DataAccess)
              {
                DataAccess da = (DataAccess) pce ;
                if(first==false)
                  _codeContent.addOutput(", ") ;
                if(da.getKind().equals(AccessType.REQUIRES))
                {
                  remainingParenthesis = manageAccessDirection(da, pl);  
                }

                String name = null ;

                // If a data access mapping is provided:
                // Transforms any data access into the right data subcomponent
                // 's name thanks to the given data access mapping.
                
                if(_dataAccessMapping != null)
                {
                  ElementHolder eh = null;
                  if(pl instanceof ValueExpression)
                  {
                	  Relation r = ((ValueExpression)pl).getRelations().get(0);
                	  Term t = r.getFirstExpression().getTerms().get(0);
                	  eh = (ElementHolder) t.getFactors().get(0).getFirstValue();  
                  }
                  else if(pl instanceof DataAccessHolder)
                  {
                  	eh = (ElementHolder) pl;
                  }
                  if(eh instanceof DataAccessHolder && 
                		  AadlUtil.getContainingAnnex(eh) instanceof ThreadClassifier)
                  	name = _dataAccessMapping.get((DataAccess)eh.getElement());
                  else if(eh instanceof DataSubcomponentHolder)
                  {
                  	DataSubcomponent ds = (DataSubcomponent) ((DataSubcomponentHolder) eh).getElement(); 
                  	name = GenerationUtilsJava.getGenerationJavaIdentifier(ds.getQualifiedName());
                  }
                }

                if (name != null)
                {
                  _codeContent.addOutput(name);
                }
                else // Otherwise, process parameter label as usual.
                {
                  process((ParameterLabel) pl) ;
                }
                if(remainingParenthesis)
                {
                	_codeContent.addOutput(")");
                	remainingParenthesis=false;
                }
                first=false;
              }

            }

            _codeContent.addOutputNewline(");") ;
          }
        }
        else
        {
          if(object.isSetParameterLabels())
          {
            _codeContent.addOutput(" (") ;
            processEList(_codeContent, object.getParameterLabels(), ", ") ;
            _codeContent.addOutputNewline(");") ;
          }
        }

        return DONE ;
      }
      
      private void additionalSubprogramsToUnparse(SubprogramSubcomponentType sct) {
    	AadlToJavaUnparser aadlCUnparser = AadlToJavaUnparser.getAadlToJavaUnparser();   
    	if(false == aadlCUnparser.additionalUnparsing.contains(sct))
    	  aadlCUnparser.additionalUnparsing.add(sct);
        for(AnnexSubclause as: ((SubprogramClassifier)sct).getAllAnnexSubclauses() )
        {
          if(as instanceof BehaviorAnnex)
          {
        	for(SubprogramHolder otherSpg: EcoreUtil2.getAllContentsOfType(as, SubprogramHolder.class))
        	{
        	  if(true == aadlCUnparser.additionalUnparsing.contains(otherSpg))
        		continue;
        	  aadlCUnparser.additionalUnparsing.add(otherSpg.getSubprogram());
        	  additionalSubprogramsToUnparse((SubprogramSubcomponentType) otherSpg.getSubprogram());
        	}
        	return;
          }
        }
	 }

	public String caseElementHolder(ElementHolder object)
      {
    	NamedElement elt = object.getElement();
    	String id;
    	boolean pointer=false;
    	if(elt instanceof Parameter)
		{
			Parameter p = (Parameter) elt;
			if(false==(object.eContainer() instanceof SubprogramCallAction))
			{
			  String usageP = Aadl2Utils.getParameter_Usage(p);
			  if(Aadl2Utils.isInOutParameter(p)
				  || Aadl2Utils.isOutParameter(p)
				  || usageP.equalsIgnoreCase("by_reference"))
			  {
			    aadlbaText.addOutput("(*");
			    pointer=true;
			  }
			
			}
			id = elt.getName();
			
		}
		else if (elt instanceof DataAccess)
		{
		  DataAccess da = (DataAccess) elt;
		  
		  if(false==(object.eContainer() instanceof SubprogramCallAction))
		  {
			if(Aadl2Utils.isReadWriteDataAccess(da)
			  || Aadl2Utils.isWriteOnlyDataAccess(da))
			{
  			   aadlbaText.addOutput("(*");
			   pointer=true;		
			}
		  }
  		  id = elt.getName();
		}
    	else	
    		id = elt.getQualifiedName();
    	aadlbaText.addOutput(GenerationUtilsJava.getGenerationJavaIdentifier(id));
    	if(pointer)
    		aadlbaText.addOutput(")");
    	if(object instanceof IndexableElement)
    	{	
    	  IndexableElement ie = (IndexableElement) object;
    	  for(IntegerValue iv: ie.getArrayIndexes())
    	  {
    		aadlbaText.addOutput("[");
    		process(iv);
    		aadlbaText.addOutput("]");
    	  }
    	}
        return DONE ;
      }
      
      public String casePortSendAction(PortSendAction object)
      {
        throw new UnsupportedOperationException() ;
      }

      public String casePortFreezeAction(PortFreezeAction object)
      {
        throw new UnsupportedOperationException() ;
      }

      public String casePortDequeueAction(PortDequeueAction object)
      {
        throw new UnsupportedOperationException() ;
      }

      public String caseLockAction(LockAction object)
      {
        throw new UnsupportedOperationException() ;
      }

      public String caseUnlockAction(UnlockAction object)
      {
        throw new UnsupportedOperationException() ;
      }

      /**
       * Unparse behaviortime
       */
      public String caseBehaviorTime(BehaviorTime object)
      {
        throw new UnsupportedOperationException() ;
      }

      public String casePortDequeueValue(PortDequeueValue object)
      {
        throw new UnsupportedOperationException() ;
      }

      public String casePortCountValue(PortCountValue object)
      {
        throw new UnsupportedOperationException() ;
      }

      public String casePortFreshValue(PortFreshValue object)
      {
        throw new UnsupportedOperationException() ;
      }

      /**
       * Unparse booleanliteral
       */
      public String caseBehaviorBooleanLiteral(BehaviorBooleanLiteral object)
      {
        //FIXME : TODO : update location reference
        if(object.isValue())
        {
          _codeContent.addOutput("1") ;
        }
        else
        {
          _codeContent.addOutput("0") ;
        }

        return DONE ;
      }

      /**
       * Unparse stringliteral
       */
      public String caseBehaviorStringLiteral(BehaviorStringLiteral object)
      {
        //FIXME : TODO : update location reference
        _codeContent.addOutput(object.getValue()) ;
        return DONE ;
      }

      public String caseBehaviorRealLiteral(BehaviorRealLiteral object)
      {
        _codeContent.addOutput(String.valueOf(object.getValue())) ;
        return DONE ;
      }

      public String caseBehaviorIntegerLiteral(BehaviorIntegerLiteral object)
      {
        _codeContent.addOutput(Long.toString(object.getValue())) ;
        return DONE ;
      }
      
      /**
       * Unparse valueexpression
       */
      public String caseValueExpression(ValueExpression object)
      {
        Iterator<Relation> itRel = object.getRelations().iterator() ;
        process(itRel.next()) ;

        if(object.isSetLogicalOperators())
        {
          Iterator<LogicalOperator> itOp =
                object.getLogicalOperators().iterator() ;

          while(itRel.hasNext())
          {
            _codeContent.addOutput(" " +
                  AadlBaToJavaUnparser.getTargetLanguageOperator(itOp.next()) +
                  " ") ;
            process(itRel.next()) ;
          }
        }
        return DONE ;
      }

      /**
       * Unparse relation
       */
      public String caseRelation(Relation object)
      {
        process(object.getFirstExpression()) ;

        if(object.getSecondExpression() != null)
        {
          _codeContent.addOutput(" " +
                AadlBaToJavaUnparser.getTargetLanguageOperator(object
                      .getRelationalOperator()) + " ") ;
          process(object.getSecondExpression()) ;
        }

        return DONE ;
      }

      /**
       * Unparse simpleexpression
       */
      public String caseSimpleExpression(SimpleExpression object)
      {
        if(object.getUnaryAddingOperator()!=UnaryAddingOperator.NONE)
        {
          _codeContent.addOutput(object.getUnaryAddingOperator()
                .getLiteral()) ;
        }

        Iterator<Term> itTerm = object.getTerms().iterator() ;
        process(itTerm.next()) ;

        if(object.isSetBinaryAddingOperators())
        {
          Iterator<BinaryAddingOperator> itOp =
                object.getBinaryAddingOperators().iterator() ;

          while(itTerm.hasNext())
          {
            _codeContent.addOutput(" " + itOp.next().getLiteral() + " ") ;
            process(itTerm.next()) ;
          }
        }

        return DONE ;
      }

      /**
       * Unparse term
       */
      public String caseTerm(Term object)
      {
        Iterator<Factor> itFact = object.getFactors().iterator() ;
        process(itFact.next()) ;

        if(object.isSetMultiplyingOperators())
        {
          Iterator<MultiplyingOperator> itOp =
                object.getMultiplyingOperators().iterator() ;

          while(itFact.hasNext())
          {
            _codeContent.addOutput(" " +
                  AadlBaToJavaUnparser.getTargetLanguageOperator(itOp.next()) +
                  " ") ;
            process(itFact.next()) ;
          }
        }

        return DONE ;
      }

      /**
       * Unparse factor
       */
      public String caseFactor(Factor object)
      {
        if(object.getUnaryNumericOperator() != UnaryNumericOperator.NONE)
        {
          _codeContent.addOutput(AadlBaToJavaUnparser
                                  .getTargetLanguageOperator(object
                                                             .getUnaryNumericOperator())) ;
          _codeContent.addOutput("(") ;
        }
        else if(object.getUnaryBooleanOperator()!= UnaryBooleanOperator.NONE)
        {
          _codeContent.addOutput(AadlBaToJavaUnparser
                                  .getTargetLanguageOperator(object
                                                             .getUnaryBooleanOperator())) ;
          _codeContent.addOutput("(") ;
        }



        if(object.getFirstValue() instanceof ValueExpression)
        {
          _codeContent.addOutput("(") ;
          process(object.getFirstValue()) ;
          _codeContent.addOutput(")") ;
        }
        else
        {
          process(object.getFirstValue()) ;
        }

        if(object.getUnaryNumericOperator()!=UnaryNumericOperator.NONE ||
              object.getUnaryBooleanOperator() != UnaryBooleanOperator.NONE)
        {
          _codeContent.addOutput(")") ;
        }

        if(object.getBinaryNumericOperator()!=BinaryNumericOperator.NONE)
        {
          _codeContent.addOutput(" " +
                object.getBinaryNumericOperator().getLiteral() + " ") ;

          if(object.getSecondValue() instanceof ValueExpression)
          {
            _codeContent.addOutput("(") ;
            process(object.getSecondValue()) ;
            _codeContent.addOutput(")") ;
          }
          else
          {
            process(object.getSecondValue()) ;
          }
        }

        return DONE ;
      }
/*
      public String caseComment(Comment object)
      {
        _codeContent.addOutputNewline("// " + object.getBody()) ;
        return DONE ;
      }
*/
    } ;
  }

  public void addIndent_C(String indent)
  {
    while(_codeContent.getIndentString().length() < indent.length())
    {
      _codeContent.incrementIndent() ;
    }
  }



  public void setOwner(NamedElement owner) {
	this._owner = owner;
  }
  
}