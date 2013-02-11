package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.TestCase;
import org.testtoolinterfaces.testsuite.TestCaseImpl;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestLink;
import org.testtoolinterfaces.testsuite.TestStepSequence;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.XMLReader;

/**
 * XmlHandler to read the testcase part from a TTI-XML file.
 * 
 * <testcase id="..." [any="..."]>
 *  <description>...</description>
 *  <requirementid>...</requirementid>
 *  <prepare>
 *   ...
 *  </prepare>
 *  <execute>
 *   ...
 *  </execute>
 *  <restore>
 *   ...
 *  </restore>
 *  <[any]>
 *   ...
 *  </[any]>
 * </testcase>
 * 
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */

public class TestCaseXmlHandler extends TestExecItemXmlHandler
{
	public static final String START_ELEMENT = "testcase";

	private static final String EXECUTE_ELEMENT = "execute";

	private TestLink myExecutionScript;
    private TestStepSequence myExecutionSteps;

	private TestStepSequenceXmlHandler myExecutionXmlHandler;

	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader the xmlReader
	 * @param anInterfaceList a list of interfaces
	 * @param aCheckStepParameter flag to indicate if specified parameters of a step must be verified in the interface
	 */
	public TestCaseXmlHandler( XMLReader anXmlReader, TestInterfaceList anInterfaceList, boolean aCheckStepParameter )
	{
		super(anXmlReader, START_ELEMENT, anInterfaceList, aCheckStepParameter);
		Trace.println(Trace.CONSTRUCTOR);

//	    ArrayList<TestStep.StepType> execAllowedTypes = new ArrayList<TestStep.StepType>();
//	    execAllowedTypes.add( TestStep.StepType.action );
//	    execAllowedTypes.add( TestStep.StepType.check );
//	    execAllowedTypes.add( TestStep.StepType.set );

		myExecutionXmlHandler = new TestStepSequenceXmlHandler( anXmlReader,
		                                                        EXECUTE_ELEMENT,
//		                                                        execAllowedTypes,
		                                                        anInterfaceList,
		                                                        aCheckStepParameter );
		this.addElementHandler(myExecutionXmlHandler);

		this.resetCaseHandler();
	}
	
	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
			throws TestSuiteException
	{
		Trace.println(Trace.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(EXECUTE_ELEMENT))
    	{
    		myExecutionSteps = myExecutionXmlHandler.getSteps();
    		myExecutionXmlHandler.reset();
    	}
    	else {
			super.handleReturnFromChildElement(aQualifiedName, aChildXmlHandler);
    	}
	}

    /**
     * Creates and returns the TestCase
     * 
     * @return the TestCase
     * @throws TestSuiteException	When the id is empty or execution steps are not defined.
     */
    public TestCase getTestCase() throws TestSuiteException
    {
		Trace.println(Trace.SUITE);

		String id = this.getId();
		if ( id.isEmpty() )
		{
			throw new TestSuiteException("Unknown TestCase ID");
		}

		if ( myExecutionSteps.isEmpty() && myExecutionScript == null )
		{
			throw new TestSuiteException("No Execution Steps found", id);
		}

		TestCaseImpl testCase = new TestCaseImpl( id, this.getDescription(), this.getSequenceNr(),
       	       	this.getRequirementIds(), this.getPrepareSteps(), myExecutionSteps, this.getRestoreSteps() );
       	testCase.setAnyAttributes( this.getAnyAttributes() );
       	testCase.setAnyElements( this.getAnyElements() );

		return testCase;
    }

	@Override
	public void reset()
	{
		this.resetCaseHandler();
	}

	public final void resetCaseHandler()
	{
		Trace.println(Trace.SUITE);
		myExecutionSteps = new TestStepSequence();

		super.resetExecItemHandler();
	}
}
