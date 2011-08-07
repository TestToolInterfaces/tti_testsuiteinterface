package org.testtoolinterfaces.testsuiteinterface;

import java.util.ArrayList;
import java.util.Hashtable;

import org.testtoolinterfaces.testsuite.TestCase;
import org.testtoolinterfaces.testsuite.TestCaseImpl;
import org.testtoolinterfaces.testsuite.TestScript;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepArrayList;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
//import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
//import org.xml.sax.helpers.LocatorImpl;

/**
 * @author Arjan Kranenburg 
 * 
 * <testcase id="..." [any="..."]>
 *  <description>...</description>
 *  <requirementId>...</requirementId>
 *  <prepare>
 *   ...
 *  </prepare>
 *  <execution>
 *   ...
 *  </execution>
 *  <restore>
 *   ...
 *  </restore>
 *  <[any]>
 *   ...
 *  </[any]>
 * </testcase>
 */

public class TestCaseXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "testcase";
	public static final String ATTRIBUTE_ID = "id";
	
	private static final String DESCRIPTION_ELEMENT = "description";
	private static final String REQUIREMENT_ELEMENT = "requirementId";
	
	private static final String PREPARE_ELEMENT = "prepare";
	private static final String EXECUTE_ELEMENT = "execute";
	private static final String RESTORE_ELEMENT = "restore";

	private String myTestCaseId;
	private Hashtable<String, String> myAnyAttributes;

	private String myDescription;
	private TestScript myExecutionScript;
    private ArrayList<String> myRequirementIds;
    private TestStepArrayList myPrepareSteps;
    private TestStepArrayList myExecutionSteps;
    private TestStepArrayList myRestoreSteps;
	private Hashtable<String, String> myAnyElements;
	private String myCurrentAnyValue;

	private GenericTagAndStringXmlHandler myDescriptionXmlHandler;
	private GenericTagAndStringXmlHandler myRequirementIdXmlHandler;
	private TestStepSequenceXmlHandler myPrepareXmlHandler;
	private TestStepSequenceXmlHandler myExecutionXmlHandler;
	private TestStepSequenceXmlHandler myRestoreXmlHandler;

	/**
	 * @param anXmlReader the xmlReader
	 * @param anInterfaceList a list of interfaces
	 * @param aCheckStepParameter flag to indicate if specified parameters of a step must be verified in the interface
	 */
	public TestCaseXmlHandler( XMLReader anXmlReader, TestInterfaceList anInterfaceList, boolean aCheckStepParameter )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR);

		myRequirementIds = new ArrayList<String>();
		myPrepareSteps = new TestStepArrayList();
		myExecutionSteps = new TestStepArrayList();
		myRestoreSteps = new TestStepArrayList();

	    ArrayList<TestStep.StepType> prepRestAllowedTypes = new ArrayList<TestStep.StepType>();
	    prepRestAllowedTypes.add( TestStep.StepType.action );
	    prepRestAllowedTypes.add( TestStep.StepType.set );

	    ArrayList<TestStep.StepType> execAllowedTypes = new ArrayList<TestStep.StepType>();
	    execAllowedTypes.add( TestStep.StepType.action );
	    execAllowedTypes.add( TestStep.StepType.check );
	    execAllowedTypes.add( TestStep.StepType.set );

	    myDescriptionXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, DESCRIPTION_ELEMENT);
		this.addStartElementHandler(DESCRIPTION_ELEMENT, myDescriptionXmlHandler);
		myDescriptionXmlHandler.addEndElementHandler(DESCRIPTION_ELEMENT, this);

     	myRequirementIdXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, REQUIREMENT_ELEMENT);
		this.addStartElementHandler(REQUIREMENT_ELEMENT, myRequirementIdXmlHandler);
		myRequirementIdXmlHandler.addEndElementHandler(REQUIREMENT_ELEMENT, this);

    	myPrepareXmlHandler = new TestStepSequenceXmlHandler( anXmlReader,
    	                                                      PREPARE_ELEMENT,
    	                                                      prepRestAllowedTypes,
    	                                                      anInterfaceList,
    	                                                      aCheckStepParameter );
		this.addStartElementHandler(PREPARE_ELEMENT, myPrepareXmlHandler);
		myPrepareXmlHandler.addEndElementHandler(PREPARE_ELEMENT, this);

		myExecutionXmlHandler = new TestStepSequenceXmlHandler( anXmlReader,
		                                                        EXECUTE_ELEMENT,
		                                                        execAllowedTypes,
		                                                        anInterfaceList,
		                                                        aCheckStepParameter );
		this.addStartElementHandler(EXECUTE_ELEMENT, myExecutionXmlHandler);
		myExecutionXmlHandler.addEndElementHandler(EXECUTE_ELEMENT, this);

		myRestoreXmlHandler = new TestStepSequenceXmlHandler( anXmlReader,
		                                                      RESTORE_ELEMENT,
		                                                      prepRestAllowedTypes,
		                                                      anInterfaceList,
		                                                      aCheckStepParameter );
		this.addStartElementHandler(RESTORE_ELEMENT, myRestoreXmlHandler);
		myRestoreXmlHandler.addEndElementHandler(RESTORE_ELEMENT, this);

		this.reset();
	}
	
    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		Trace.print(Trace.SUITE, "processElementAttributes( " 
	            + aQualifiedName, true );
    	if (aQualifiedName.equalsIgnoreCase(TestCaseXmlHandler.START_ELEMENT))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
	    		Trace.append( Trace.SUITE, ", " + att.getQName(i) + "=" + att.getValue(i) );
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_ID))
		    	{
		        	myTestCaseId = att.getValue(i);
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
		myCurrentAnyValue = myCurrentAnyValue + aValue;
	}

	@Override
	public void handleEndElement(String aQualifiedName)
	{
		if ( ! aQualifiedName.equalsIgnoreCase(START_ELEMENT) )
    	{
			// TODO This will overwrite previous occurrences of the same elements. But that is possible in XML.
			myAnyAttributes.put(aQualifiedName, myCurrentAnyValue);
			myCurrentAnyValue = "";
    	}
	}

	/** 
	 * @param aQualifiedName the name of the childElement
	 */
	public void handleGoToChildElement(String aQualifiedName)
	{
		//nop
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
		Trace.println(Trace.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(DESCRIPTION_ELEMENT))
    	{
    		myDescription  = myDescriptionXmlHandler.getValue();
        	myDescriptionXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(REQUIREMENT_ELEMENT))
    	{
    		myRequirementIds.add(myRequirementIdXmlHandler.getValue());
    		myRequirementIdXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(PREPARE_ELEMENT))
    	{
    		myPrepareSteps = myPrepareXmlHandler.getSteps();
    		myPrepareXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(EXECUTE_ELEMENT))
    	{
    		myExecutionSteps = myExecutionXmlHandler.getSteps();
    		myExecutionXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(RESTORE_ELEMENT))
    	{
    		myRestoreSteps = myRestoreXmlHandler.getSteps();
    		myRestoreXmlHandler.reset();
    	}
    	else
    	{ // Programming fault
			throw new Error( "Child XML Handler returned, but not recognized. The handler was probably defined " +
			                 "in the Constructor but not handled in handleReturnFromChildElement()");
    	}
	}

//	/**
//     * @throws SAXParseException 
//     */
//    public TestCase getTestCase() throws SAXParseException
//    {
//		Trace.println(Trace.SUITE);
//
//		if ( myTestCaseId.isEmpty() )
//		{
//			throw new SAXParseException("Unknown TestCase ID", new LocatorImpl());
//		}
//
//		if ( myExecutionSteps.isEmpty() && myExecutionScript == null )
//		{
//			throw new SAXParseException("No Execution Steps found for " + myTestCaseId, new LocatorImpl());
//		}
//
//       	TestCase testCase = (TestCase) new TestCaseImpl( myTestCaseId,
//       	                                                 myAnyAttributes,
//       	       										  	 myDescription,
//       	       										  	 myRequirementIds,
//       	       										  	 myPrepareSteps.sort(),
//       	       										  	 myExecutionSteps.sort(),
//       	       										  	 myRestoreSteps.sort(),
//       	       										  	 myAnyElements );
//
//		return testCase;
//    }

	/**
     * @throws TestSuiteException 
     */
    public TestCase getTestCase() throws TestSuiteException
    {
		Trace.println(Trace.SUITE);

		if ( myTestCaseId.isEmpty() )
		{
			throw new TestSuiteException("Unknown TestCase ID");
		}

		if ( myExecutionSteps.isEmpty() && myExecutionScript == null )
		{
			throw new TestSuiteException("No Execution Steps found", myTestCaseId);
		}

       	TestCase testCase = (TestCase) new TestCaseImpl( myTestCaseId,
       	                                                 myAnyAttributes,
       	       										  	 myDescription,
       	       										  	 myRequirementIds,
       	       										  	 myPrepareSteps.sort(),
       	       										  	 myExecutionSteps.sort(),
       	       										  	 myRestoreSteps.sort(),
       	       										  	 myAnyElements );

		return testCase;
    }

    public void reset()
	{
		Trace.println(Trace.SUITE);
		myTestCaseId = "";
	    myAnyAttributes = new Hashtable<String, String>();

	    myDescription = "";
		myRequirementIds = new ArrayList<String>();
		myPrepareSteps = new TestStepArrayList();
		myRestoreSteps = new TestStepArrayList();
		myAnyElements = new Hashtable<String, String>();
	    myCurrentAnyValue = "";
	}
}
