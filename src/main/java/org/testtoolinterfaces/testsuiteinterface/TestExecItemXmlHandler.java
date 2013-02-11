package org.testtoolinterfaces.testsuiteinterface;

import java.util.ArrayList;

import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestStepSequence;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.XMLReader;

/**
 * XmlHandler to read a test execution item from a TTI-XML file.
 * 
 * <[tag]>
 *  <requirementid>...</requirementid>
 *  <prepare>
 *   ...
 *  </prepare>
 *  <restore>
 *   ...
 *  </restore>
 * </[tag]>
 * 
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */

public class TestExecItemXmlHandler extends TestGroupEntryXmlHandler
{
	
	private static final String REQUIREMENT_ELEMENT = "requirementid";
	
	private static final String PREPARE_ELEMENT = "prepare";
	private static final String RESTORE_ELEMENT = "restore";

    private ArrayList<String> myRequirementIds;
    private TestStepSequence myPrepareSteps;
    private TestStepSequence myRestoreSteps;

	private GenericTagAndStringXmlHandler myRequirementIdXmlHandler;
	private TestStepSequenceXmlHandler myPrepareXmlHandler;
	private TestStepSequenceXmlHandler myRestoreXmlHandler;

	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader the xmlReader
	 * @param anInterfaceList a list of interfaces
	 * @param aCheckStepParameter flag to indicate if specified parameters of a step must be verified in the interface
	 */
	public TestExecItemXmlHandler( XMLReader anXmlReader, String aStartElement,
			TestInterfaceList anInterfaceList, boolean aCheckStepParameter )
	{
		super(anXmlReader, aStartElement);
		Trace.println(Trace.CONSTRUCTOR);

//	    ArrayList<TestStep.StepType> prepRestAllowedTypes = new ArrayList<TestStep.StepType>();
//	    prepRestAllowedTypes.add( TestStep.StepType.action );
//	    prepRestAllowedTypes.add( TestStep.StepType.set );

     	myRequirementIdXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, REQUIREMENT_ELEMENT);
		this.addElementHandler(myRequirementIdXmlHandler);

    	myPrepareXmlHandler = new TestStepSequenceXmlHandler( anXmlReader,
    	                                                      PREPARE_ELEMENT,
//    	                                                      prepRestAllowedTypes,
    	                                                      anInterfaceList,
    	                                                      aCheckStepParameter );
		this.addElementHandler(myPrepareXmlHandler);

		myRestoreXmlHandler = new TestStepSequenceXmlHandler( anXmlReader,
		                                                      RESTORE_ELEMENT,
//		                                                      prepRestAllowedTypes,
		                                                      anInterfaceList,
		                                                      aCheckStepParameter );
		this.addElementHandler(myRestoreXmlHandler);

		this.resetExecItemHandler();
	}
	
	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
			throws TestSuiteException
	{
		Trace.println(Trace.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(REQUIREMENT_ELEMENT))
    	{
    		myRequirementIds.add(myRequirementIdXmlHandler.getValue());
    		myRequirementIdXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(PREPARE_ELEMENT))
    	{
    		myPrepareSteps = myPrepareXmlHandler.getSteps();
    		myPrepareXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(RESTORE_ELEMENT))
    	{
    		myRestoreSteps = myRestoreXmlHandler.getSteps();
    		myRestoreXmlHandler.reset();
    	}
    	else {
			super.handleReturnFromChildElement(aQualifiedName, aChildXmlHandler);
    	}
	}

	/**
	 * @return the requirementIds
	 */
	protected ArrayList<String> getRequirementIds() {
		return myRequirementIds;
	}

	/**
	 * @return the prepareSteps
	 */
	protected TestStepSequence getPrepareSteps() {
		return myPrepareSteps;
	}

	/**
	 * @return the restoreSteps
	 */
	protected TestStepSequence getRestoreSteps() {
		return myRestoreSteps;
	}

	@Override
	public void reset()
	{
		this.resetExecItemHandler();
	}
	
	public final void resetExecItemHandler()
	{
		Trace.println(Trace.SUITE);
	    myRequirementIds = new ArrayList<String>();
		myPrepareSteps = new TestStepSequence();
		myRestoreSteps = new TestStepSequence();

		super.resetGroupEntryHandler();
	}
}
