package org.testtoolinterfaces.testsuiteinterface;

import java.util.ArrayList;
import java.util.Hashtable;

import org.testtoolinterfaces.testsuite.TestEntry;
import org.testtoolinterfaces.testsuite.TestEntrySequence;
import org.testtoolinterfaces.testsuite.TestGroup;
import org.testtoolinterfaces.testsuite.TestGroupImpl;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestStepSequence;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;


/**
 * @author Arjan Kranenburg 
 * 
 * <testgroup id="..." [any="..."]>
 *  <description>...</description>
 *  <requirementId>...</requirementId>
 *  <prepare>
 *   ...
 *  </prepare>
 *  <testgrouplink>
 *   ...
 *  </testgrouplink>
 *  <testcaselink>
 *   ...
 *  </testcaselink>
 *  <restore>
 *   ...
 *  </restore>
 *  <[any]>
 *   ...
 *  </[any]>
 * </testgroup>
 * 
 */
public class TestGroupXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "testgroup";
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_SEQUENCE = "sequence";
	
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
    private TestEntrySequence myTestEntries;
    private TestStepSequence myRestoreSteps;
	private Hashtable<String, String> myAnyElements;
	private String myCurrentAnyValue;

	private GenericTagAndStringXmlHandler myDescriptionXmlHandler;
	private GenericTagAndStringXmlHandler myRequirementIdXmlHandler;
	private TestStepSequenceXmlHandler myPrepareXmlHandler;
	private TestCaseLinkXmlHandler myTestCaseLinkXmlHandler;
	private TestGroupLinkXmlHandler myTestGroupLinkXmlHandler;
	private TestStepSequenceXmlHandler myRestoreXmlHandler;
	
	public TestGroupXmlHandler( XMLReader anXmlReader, TestInterfaceList anInterfaceList, boolean aCheckStepParameter )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR);
		
//	    ArrayList<TestStep.StepType> allowedTypes = new ArrayList<TestStep.StepType>();
//	    allowedTypes.add( TestStep.StepType.action );
//	    allowedTypes.add( TestStep.StepType.set );

	    myDescriptionXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, DESCRIPTION_ELEMENT);
		this.addStartElementHandler(DESCRIPTION_ELEMENT, myDescriptionXmlHandler);
		myDescriptionXmlHandler.addEndElementHandler(DESCRIPTION_ELEMENT, this);

     	myRequirementIdXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, REQUIREMENT_ELEMENT);
		this.addStartElementHandler(REQUIREMENT_ELEMENT, myRequirementIdXmlHandler);
		myRequirementIdXmlHandler.addEndElementHandler(REQUIREMENT_ELEMENT, this);

    	myPrepareXmlHandler = new TestStepSequenceXmlHandler( anXmlReader,
    	                                                      PREPARE_ELEMENT,
//    	                                                      allowedTypes,
    	                                                      anInterfaceList,
    	                                                      aCheckStepParameter );
		this.addStartElementHandler(PREPARE_ELEMENT, myPrepareXmlHandler);
		myPrepareXmlHandler.addEndElementHandler(PREPARE_ELEMENT, this);

		myTestCaseLinkXmlHandler = new TestCaseLinkXmlHandler(anXmlReader);
		this.addStartElementHandler(TestCaseLinkXmlHandler.START_ELEMENT, myTestCaseLinkXmlHandler);
		myTestCaseLinkXmlHandler.addEndElementHandler(TestCaseLinkXmlHandler.START_ELEMENT, this);

		myTestGroupLinkXmlHandler = new TestGroupLinkXmlHandler(anXmlReader);
		this.addStartElementHandler(TestGroupLinkXmlHandler.START_ELEMENT, myTestGroupLinkXmlHandler);
		myTestGroupLinkXmlHandler.addEndElementHandler(TestGroupLinkXmlHandler.START_ELEMENT, this);

		myRestoreXmlHandler = new TestStepSequenceXmlHandler( anXmlReader,
		                                                      RESTORE_ELEMENT,
//		                                                      allowedTypes,
		                                                      anInterfaceList,
		                                                      aCheckStepParameter );
		this.addStartElementHandler(RESTORE_ELEMENT, myRestoreXmlHandler);
		myRestoreXmlHandler.addEndElementHandler(RESTORE_ELEMENT, this);
		
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
	/** 
	 * @param aQualifiedName the name of the childElement
	 * 
	 */
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
			myTestEntries.add((TestEntry) myTestGroupLinkXmlHandler.getTestGroupLink());
			myTestGroupLinkXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TestCaseLinkXmlHandler.START_ELEMENT))
    	{
			myTestEntries.add((TestEntry) myTestCaseLinkXmlHandler.getTestCaseLink());
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

	public void reset()
	{
		Trace.println(Trace.SUITE);

		myTestGroupId = "";
	    mySequenceNr = 0;
	    myAnyAttributes = new Hashtable<String, String>();

	    myDescription = "";
	    myRequirementIds = new ArrayList<String>();
	    myPrepareSteps = new TestStepSequence();
		myTestEntries = new TestEntrySequence();
	    myRestoreSteps = new TestStepSequence();
		myAnyElements = new Hashtable<String, String>();
	    myCurrentAnyValue = "";
	}

	public TestGroup getTestGroup() throws TestSuiteException
	{
		Trace.println(Trace.SUITE);

		if ( myTestGroupId.isEmpty() )
		{
			throw new TestSuiteException( "Unknown TestGroup ID" );
		}

      	TestGroup testGroup = (TestGroup) new TestGroupImpl( myTestGroupId,
      	                                                     myDescription,
           	       										  	 mySequenceNr,
      	                                                     myRequirementIds,
      	                                                     myPrepareSteps,
      	                                                     myTestEntries,
      	                                                     myRestoreSteps,
      	                                                     myAnyAttributes,
      	                                                     myAnyElements );

		return testGroup;
	}
}
