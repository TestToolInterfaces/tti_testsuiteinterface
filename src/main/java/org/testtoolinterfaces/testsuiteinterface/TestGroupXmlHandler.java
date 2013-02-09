package org.testtoolinterfaces.testsuiteinterface;

import java.util.ArrayList;
import java.util.Hashtable;

import org.testtoolinterfaces.testsuite.TestEntry;
import org.testtoolinterfaces.testsuite.TestGroupEntry;
import org.testtoolinterfaces.testsuite.TestGroupEntrySequence;
import org.testtoolinterfaces.testsuite.TestGroup;
import org.testtoolinterfaces.testsuite.TestGroupImpl;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestStepSequence;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.TTIException;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
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
public class TestGroupXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "testgroup";
	private static final String ATTRIBUTE_ID = "id";
	private static final String ATTRIBUTE_SEQUENCE = "sequence";
	
	private static final String DESCRIPTION_ELEMENT = "description";
	private static final String REQUIREMENT_ELEMENT = "requirementid";

	private static final String PREPARE_ELEMENT = "prepare";
	private static final String RESTORE_ELEMENT = "restore";

	private String myTestGroupId;
	private Hashtable<String, String> myAnyAttributes;
	private int mySequenceNr;

	private String myDescription;
    private ArrayList<String> myRequirementIds;
    private TestStepSequence myPrepareSteps;
    private TestGroupEntrySequence myTestEntries;
    private TestStepSequence myRestoreSteps;
	private Hashtable<String, String> myAnyElements;
	private String myCurrentAnyValue;

	private GenericTagAndStringXmlHandler myDescriptionXmlHandler;
	private GenericTagAndStringXmlHandler myRequirementIdXmlHandler;
	private TestStepSequenceXmlHandler myPrepareXmlHandler;
	private TestCaseLinkXmlHandler myTestCaseLinkXmlHandler;
	private TestGroupLinkXmlHandler myTestGroupLinkXmlHandler;
	private ForeachEntryXmlHandler myForeachXmlHandler;
	private TestStepSequenceXmlHandler myRestoreXmlHandler;
	
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
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR);
		
	    myDescriptionXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, DESCRIPTION_ELEMENT);
		this.addElementHandler(myDescriptionXmlHandler);

     	myRequirementIdXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, REQUIREMENT_ELEMENT);
		this.addElementHandler(myRequirementIdXmlHandler);

    	myPrepareXmlHandler = new TestStepSequenceXmlHandler( anXmlReader,
    	                                                      PREPARE_ELEMENT,
    	                                                      anInterfaceList,
    	                                                      aCheckStepParameter );
		this.addElementHandler(myPrepareXmlHandler);

		myTestCaseLinkXmlHandler = new TestCaseLinkXmlHandler(anXmlReader);
		this.addElementHandler(myTestCaseLinkXmlHandler);

		myTestGroupLinkXmlHandler = new TestGroupLinkXmlHandler(anXmlReader);
		this.addElementHandler(myTestGroupLinkXmlHandler);

		myForeachXmlHandler = new ForeachEntryXmlHandler(anXmlReader, anInterfaceList, aCheckStepParameter);
		this.addElementHandler(myForeachXmlHandler);

		myRestoreXmlHandler = new TestStepSequenceXmlHandler( anXmlReader,
		                                                      RESTORE_ELEMENT,
		                                                      anInterfaceList,
		                                                      aCheckStepParameter );
		this.addElementHandler(myRestoreXmlHandler);
		
		this.reset();
	}

	@Override
	public void handleStartElement(String aQualifiedName)
	{
   		//nop;
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
			myAnyElements.put(aQualifiedName, myCurrentAnyValue);
			myCurrentAnyValue = "";
    	}
    }

	@Override
    public void processElementAttributes(String aQualifiedName, Attributes anAtt)
    {
		Trace.print(Trace.SUITE, "processElementAttributes( " 
	            + aQualifiedName, true );
     	if (aQualifiedName.equalsIgnoreCase(START_ELEMENT))
    	{
		    for (int i = 0; i < anAtt.getLength(); i++)
		    {
	    		Trace.append( Trace.SUITE, ", " + anAtt.getQName(i) + "=" + anAtt.getValue(i) );
		    	if (anAtt.getQName(i).equalsIgnoreCase(ATTRIBUTE_ID))
		    	{
		        	myTestGroupId = anAtt.getValue(i);
		    	}
		    	else if (anAtt.getQName(i).equalsIgnoreCase(ATTRIBUTE_SEQUENCE))
		    	{
		        	mySequenceNr = Integer.valueOf( anAtt.getValue(i) ).intValue();
		    	}
		    	else
		    	{
		    		myAnyAttributes.put(anAtt.getQName(i), anAtt.getValue(i));
		    	}
		    }
    	}
		Trace.append( Trace.SUITE, " )\n" );
    }

	@Override
	public void handleGoToChildElement(String aQualifiedName)
	{
		//nop
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
				throws TestSuiteException
	{
		Trace.println(Trace.SUITE, "handleReturnFromChildElement( " + aQualifiedName + " )", true);

		if (aQualifiedName.equalsIgnoreCase(DESCRIPTION_ELEMENT))
    	{
    		myDescription = myDescriptionXmlHandler.getValue();
    		myDescriptionXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(REQUIREMENT_ELEMENT))
    	{
    		myRequirementIds.add( myRequirementIdXmlHandler.getValue() );
    		myRequirementIdXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(PREPARE_ELEMENT))
    	{
    		myPrepareSteps = myPrepareXmlHandler.getSteps();
    		myPrepareXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TestGroupLinkXmlHandler.START_ELEMENT))
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

			myTestCaseLinkXmlHandler.reset();
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

	@Override
	public void reset()
	{
		Trace.println(Trace.SUITE);

		myTestGroupId = "";
	    mySequenceNr = 0;
	    myAnyAttributes = new Hashtable<String, String>();

	    myDescription = "";
	    myRequirementIds = new ArrayList<String>();
	    myPrepareSteps = new TestStepSequence();
		myTestEntries = new TestGroupEntrySequence();
	    myRestoreSteps = new TestStepSequence();
		myAnyElements = new Hashtable<String, String>();
	    myCurrentAnyValue = "";
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

		if ( myTestGroupId.isEmpty() )
		{
			throw new TestSuiteException( "Unknown TestGroup ID" );
		}

		TestGroupImpl testGroup = new TestGroupImpl( myTestGroupId,
      	                                                     myDescription,
           	       										  	 mySequenceNr,
      	                                                     myRequirementIds,
      	                                                     myPrepareSteps,
      	                                                     myTestEntries,
      	                                                     myRestoreSteps);
      	testGroup.setAnyAttributes( myAnyAttributes );
      	testGroup.setAnyElements( myAnyElements );

		return testGroup;
	}
}
