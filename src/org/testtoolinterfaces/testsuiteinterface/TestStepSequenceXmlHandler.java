package org.testtoolinterfaces.testsuiteinterface;

import java.util.ArrayList;

import org.testtoolinterfaces.testsuite.TestStep.StepType;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestStepArrayList;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.testsuite.TestStepSimple.SimpleType;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;


/**
 * @author Arjan Kranenburg 
 * 
 * <[tag]>
 *  <action>
 *  ...
 *  </action>
 *  <check>
 *  ...
 *  </check>
 *  <set>
 *  ...
 *  </set>
 * </[tag]>
 * 
 */
public class TestStepSequenceXmlHandler extends XmlHandler
{
    private TestStepArrayList mySteps;

	private TestStepXmlHandler myActionXmlHandler;
	private TestStepXmlHandler myCheckXmlHandler;
	private TestStepSetXmlHandler myTestStepSetXmlHandler;
	
	private ArrayList<StepType> myAllowedStepTypes;
	
	private TestInterfaceList myInterfaceList;
	private boolean myCheckStepParameter;

	public TestStepSequenceXmlHandler( XMLReader anXmlReader,
	                                   String aTag,
	                                   ArrayList<StepType> anAllowedStepTypes,
	                                   TestInterfaceList anInterfaceList,
	                                   boolean aCheckStepParameter )
	{
		super(anXmlReader, aTag);
		Trace.println(Trace.CONSTRUCTOR, "ActionXmlHandler( anXmlreader, " + aTag + ", " + anAllowedStepTypes.size() + " )", true);

		myActionXmlHandler = new TestStepXmlHandler(anXmlReader, SimpleType.action, anInterfaceList, aCheckStepParameter);
		if ( anAllowedStepTypes.contains(StepType.action) )
		{
			this.addStartElementHandler(SimpleType.action.toString(), myActionXmlHandler);
			myActionXmlHandler.addEndElementHandler(SimpleType.action.toString(), this);
		}

		myCheckXmlHandler = new TestStepXmlHandler(anXmlReader, SimpleType.check, anInterfaceList, aCheckStepParameter);
		if ( anAllowedStepTypes.contains(StepType.check) )
		{
			this.addStartElementHandler(SimpleType.check.toString(), myCheckXmlHandler);
			myCheckXmlHandler.addEndElementHandler(SimpleType.check.toString(), this);
		}

		myTestStepSetXmlHandler = null; // Created when needed to prevent loops
		myAllowedStepTypes = anAllowedStepTypes;
		myInterfaceList = anInterfaceList;
		myCheckStepParameter = aCheckStepParameter;

		this.reset();
	}

    public void processElementAttributes(String aQualifiedName, Attributes anAtt)
    {
    	//nop
    }

	@Override
	public void handleStartElement(String aQualifiedName)
	{
    	//nop
	}

	@Override
	public void handleCharacters(String aValue)
	{
		//nop
	}

	@Override
	public void handleEndElement(String aQualifiedName)
	{
    	//nop
	}
	
	@Override
	public void handleGoToChildElement(String aQualifiedName)
	{
     	if (aQualifiedName.equalsIgnoreCase(StepType.set.toString()))
    	{
     		// We'll create a myTestStepSetXmlHandler for TestStep Sets only when we need it.
     		// Otherwise it would create an endless loop.
    		myTestStepSetXmlHandler = new TestStepSetXmlHandler( this.getXmlReader(),
    		                                                     myAllowedStepTypes,
    		                                                     myInterfaceList,
    		                                                     myCheckStepParameter );
    		if ( myAllowedStepTypes.contains(StepType.set) )
    		{
    			this.addStartElementHandler(StepType.set.toString(), myTestStepSetXmlHandler);
    			myTestStepSetXmlHandler.addEndElementHandler(StepType.set.toString(), this);
    		}
    	}
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
				throws TestSuiteException
	{
		Trace.println(Trace.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(StepType.check.toString()))
    	{
    		mySteps.add( myCheckXmlHandler.getActionStep() );
        	myCheckXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(StepType.action.toString()))
    	{
    		mySteps.add( myActionXmlHandler.getActionStep() );
        	myActionXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(StepType.set.toString()))
    	{
    		mySteps.add( this.myTestStepSetXmlHandler.getSet() );
        	myActionXmlHandler.reset();
    	}
    	else
    	{ // Programming fault
			throw new Error( "Child XML Handler returned, but not recognized. The handler was probably defined " +
			                 "in the Constructor but not handled in handleReturnFromChildElement()");
    	}
	}

	/**
	 * @return
	 */
	public TestStepArrayList getSteps()
	{
		Trace.println(Trace.GETTER);
		return mySteps;
	}

	public void reset()
	{
		Trace.println(Trace.SUITE);
		mySteps = new TestStepArrayList();
	}
}
