package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.TestEntry;
import org.testtoolinterfaces.testsuite.TestGroup;
import org.testtoolinterfaces.testsuite.TestGroupEntry;
import org.testtoolinterfaces.testsuite.TestGroupEntrySequence;
import org.testtoolinterfaces.testsuite.TestGroupImpl;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.TTIException;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.XMLReader;


/**
 * XmlHandler to read the testgroup part from a TTI-XML file.
 * 
 * <testgroup id="..." [any="..."]>
 *  <description>...</description>
 *  <requirementid>...</requirementid>
 *  <prepare>
 *   ...
 *  </prepare>
 *  <testgrouplink>
 *   ...
 *  </testgrouplink>
 *  <testcaselink>
 *   ...
 *  </testcaselink>
 *  <foreach>
 *   ...
 *  </foreach>
 *  <restore>
 *   ...
 *  </restore>
 *  <[any]>
 *   ...
 *  </[any]>
 * </testgroup>
 * 
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */
public class TestGroupXmlHandler extends TestExecItemXmlHandler
{
	public static final String START_ELEMENT = "testgroup";

    private TestGroupEntrySequence myTestEntries;

    private TestCaseLinkXmlHandler myTestCaseLinkXmlHandler;
	private TestGroupLinkXmlHandler myTestGroupLinkXmlHandler;
	private ForeachEntryXmlHandler myForeachXmlHandler;
	
	private int myNextExecutionSequenceNr = 0;

	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader the xmlReader
	 * @param anInterfaceList a list of interfaces
	 * @param aCheckStepParameter flag to indicate if specified parameters of a step must be verified in the interface
	 */
	public TestGroupXmlHandler( XMLReader anXmlReader, TestInterfaceList anInterfaceList, boolean aCheckStepParameter )
	{
		super(anXmlReader, START_ELEMENT, anInterfaceList, aCheckStepParameter);
		Trace.println(Trace.CONSTRUCTOR);

		myTestCaseLinkXmlHandler = new TestCaseLinkXmlHandler(anXmlReader);
		this.addElementHandler(myTestCaseLinkXmlHandler);

		myTestGroupLinkXmlHandler = new TestGroupLinkXmlHandler(anXmlReader);
		this.addElementHandler(myTestGroupLinkXmlHandler);

		myForeachXmlHandler = new ForeachEntryXmlHandler(anXmlReader, anInterfaceList, aCheckStepParameter);
		this.addElementHandler(myForeachXmlHandler);

		this.resetGroupHandler();
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
				throws TestSuiteException
	{
		Trace.println(Trace.SUITE, "handleReturnFromChildElement( " + aQualifiedName + " )", true);

		if (aQualifiedName.equalsIgnoreCase(TestGroupLinkXmlHandler.START_ELEMENT))
    	{
    		TestGroupEntry testEntry = (TestGroupEntry) myTestGroupLinkXmlHandler.getTestGroupLink();
    		setSequenceNr(testEntry);
    		myTestEntries.add( testEntry );

    		myTestGroupLinkXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TestCaseLinkXmlHandler.START_ELEMENT))
    	{
    		TestGroupEntry testEntry = (TestGroupEntry) myTestCaseLinkXmlHandler.getTestCaseLink();
    		setSequenceNr(testEntry);
    		myTestEntries.add( testEntry );

			myTestCaseLinkXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ForeachXmlHandler.START_ELEMENT))
    	{
    		TestGroupEntry testEntry;
			try {
				testEntry = (TestGroupEntry) myForeachXmlHandler.getTestEntryIteration();
			} catch (TTIException e) {
				Trace.print(Trace.SUITE, e);
				throw new TestSuiteException( "Cannot add an iteration of TestGroupEntries", e );
			}
    		setSequenceNr(testEntry);
    		myTestEntries.add( testEntry );

    		myForeachXmlHandler.reset();
    	}
    	else {
			super.handleReturnFromChildElement(aQualifiedName, aChildXmlHandler);
    	}
	}

	/**
	 * @param testEntry
	 */
	private void setSequenceNr(TestEntry testEntry) {
		if (testEntry.getSequenceNr() == 0 ) {
			testEntry.setSequenceNr( this.myNextExecutionSequenceNr );
		} else {
			myNextExecutionSequenceNr = testEntry.getSequenceNr();
		}
		
		myNextExecutionSequenceNr++;
	}

    /**
     * Creates and returns the TestGroup
     * 
     * @return the TestGroup
     * @throws TestSuiteException	When the id is empty.
     */
	public TestGroup getTestGroup() throws TestSuiteException
	{
		Trace.println(Trace.SUITE);

		String id = this.getId();
		if ( id.isEmpty() )
		{
			throw new TestSuiteException( "Unknown TestGroup ID" );
		}

		TestGroupImpl testGroup = new TestGroupImpl( id, this.getDescription(), this.getSequenceNr(),
				this.getRequirementIds(), this.getPrepareSteps(), myTestEntries, this.getRestoreSteps() );

      	testGroup.setAnyAttributes( this.getAnyAttributes() );
      	testGroup.setAnyElements( this.getAnyElements() );

		return testGroup;
	}

	@Override
	public void reset()
	{
		this.resetGroupHandler();
	}

	public final void resetGroupHandler()
	{
		Trace.println(Trace.SUITE);

		myTestEntries = new TestGroupEntrySequence();
		
		super.resetExecItemHandler();
	}
}
