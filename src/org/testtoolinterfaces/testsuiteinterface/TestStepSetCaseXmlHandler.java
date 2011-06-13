package org.testtoolinterfaces.testsuiteinterface;

import java.util.ArrayList;

import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepArrayList;
import org.testtoolinterfaces.testsuite.TestStepSetCase;
import org.testtoolinterfaces.testsuite.TestStepSimple;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.Warning;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;


/**
 * @author Arjan Kranenburg 
 * 
 * <case id="..." after="...">
 *  <check>
 *  ...
 *  </check>
 *  <then>
 *  ...
 *  </then>
 * <case>
 * 
 */
public class TestStepSetCaseXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "case";
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_AFTER = "after";
	
	public static final String ELEMENT_CHECK = TestStep.StepType.check.toString();
	public static final String ELEMENT_THEN = "then";
	
	private String myCaseId;
	private String myAfter;

    private TestStepSimple myCheck;
    private TestStepArrayList myThen;

	private TestStepXmlHandler myCheckXmlHandler;
	private TestStepSequenceXmlHandler myTestStepSequenceXmlHandler;

	public TestStepSetCaseXmlHandler( XMLReader anXmlReader,
	                                  ArrayList<TestStep.StepType> anAllowedStepTypes,
	                                  TestInterfaceList anInterfaceList,
	                                  boolean aCheckStepParameter )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR);

    	myCheckXmlHandler = new TestStepXmlHandler(anXmlReader, TestStepSimple.SimpleType.check, anInterfaceList, aCheckStepParameter);
		this.addStartElementHandler(ELEMENT_CHECK, myCheckXmlHandler);
		myCheckXmlHandler.addEndElementHandler(ELEMENT_CHECK, this);

		myTestStepSequenceXmlHandler = new TestStepSequenceXmlHandler(anXmlReader, "then", anAllowedStepTypes, anInterfaceList, aCheckStepParameter);
		this.addStartElementHandler(ELEMENT_THEN, myTestStepSequenceXmlHandler);
		myTestStepSequenceXmlHandler.addEndElementHandler(ELEMENT_THEN, this);

		this.reset();
	}

    public void processElementAttributes(String aQualifiedName, Attributes anAtt)
    {
		Trace.print(Trace.SUITE, "processElementAttributes( " 
		            + aQualifiedName, true );
    	if (aQualifiedName.equalsIgnoreCase(TestStepSetCaseXmlHandler.START_ELEMENT))
    	{
		    for (int i = 0; i < anAtt.getLength(); i++)
		    {
	    		Trace.append( Trace.SUITE, ", " + anAtt.getQName(i) + "=" + anAtt.getValue(i) );
		    	if (anAtt.getQName(i).equalsIgnoreCase(ATTRIBUTE_ID))
		    	{
		        	myCaseId = anAtt.getValue(i);
		    	}
		    	else if (anAtt.getQName(i).equalsIgnoreCase(ATTRIBUTE_AFTER))
		    	{
		        	myAfter = anAtt.getValue(i);
		    	}
		    }
    	}
		Trace.append( Trace.SUITE, " )\n" );
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
		// nop
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
		Trace.println(Trace.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(ELEMENT_CHECK))
    	{
    		try
			{
				myCheck  = myCheckXmlHandler.getActionStep();
			}
			catch (TestSuiteException anException)
			{
				String message = "Cannot read TestStep: " + anException.getMessage();
				Warning.println(message);
				Trace.print(Trace.ALL, message);
			}
        	myCheckXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ELEMENT_THEN))
    	{
    		myThen = this.myTestStepSequenceXmlHandler.getSteps();
    		myTestStepSequenceXmlHandler.reset();
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
	public TestStepSetCase getCase()
	{
		Trace.println(Trace.GETTER);
		// TODO
		return new TestStepSetCase( myCaseId, myAfter, myCheck, myThen );
	}

	public void reset()
	{
		Trace.println(Trace.SUITE);
		myCaseId = "";
		myAfter = "";

    	myCheck = null;
    	myThen = null;
	}
}
