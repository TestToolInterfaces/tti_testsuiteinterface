package org.testtoolinterfaces.testsuiteinterface;

import java.util.ArrayList;

import org.testtoolinterfaces.testsuite.TestEntry;
import org.testtoolinterfaces.testsuite.TestEntryArrayList;
import org.testtoolinterfaces.testsuite.TestGroup;
import org.testtoolinterfaces.testsuite.TestGroupFactory;
import org.testtoolinterfaces.testsuite.TestScript;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepArrayList;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.Warning;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.LocatorImpl;



/**
 * @author Arjan Kranenburg 
 * 
 * <testgroup id="..." type="..." sequence="...">
 *  <description>...</description>
 *  <requirementId>...</requirementId>
 *  <initialization>
 *   ...
 *  </initialization>
 *  <testgroup>
 *   ...
 *  </testgroup>
 *  <testcase>
 *   ...
 *  </testcase>
 *  <script>
 *   ...
 *  </script>
 *  <restore>
 *   ...
 *  </restore>
 * </testgroup>
 * 
 */
public class TestGroupXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "testgroup";
	public static final String ELEMENT_ID = "id";
	public static final String ELEMENT_TYPE = "type";
	public static final String ELEMENT_SEQ = "sequence";
	
	private static final String DESCRIPTION_ELEMENT = "description";
	private static final String REQUIREMENT_ELEMENT = "requirementId";

	private boolean myIsTopTestGroup = true;
	private TestGroupFactory myFactory;
	private String myCurrentTestGroupId = "";
	private String myCurrentTestGroupType = "";
	private int myCurrentTestGroupSeq = 0;
    private String myCurrentDescription = "";

    private ArrayList<String> myRequirementIds;
    private TestStepArrayList myInitializationSteps;
    private TestEntryArrayList myTestEntries;
    private TestScript myCurrentExecutionScript;
    private TestStepArrayList myRestoreSteps;

	private GenericTagAndStringXmlHandler myDescriptionXmlHandler;
	private GenericTagAndStringXmlHandler myRequirementIdXmlHandler;
	private TestStepXmlHandler myInitializeXmlHandler;
	private TestGroupXmlHandler myTestGroupXmlHandler;
	private TestCaseXmlHandler myTestCaseXmlHandler;
	private ScriptXmlHandler myScriptXmlHandler;
	private TestStepXmlHandler myRestoreXmlHandler;
	
	public TestGroupXmlHandler( XMLReader anXmlReader, TestGroupFactory aFactory )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR);
		
		myFactory = aFactory;
	    myRequirementIds = new ArrayList<String>();
	    myInitializationSteps = new TestStepArrayList();
		myTestEntries = new TestEntryArrayList();
	    myRestoreSteps = new TestStepArrayList();

	    myDescriptionXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, DESCRIPTION_ELEMENT);
		this.addStartElementHandler(DESCRIPTION_ELEMENT, myDescriptionXmlHandler);
		myDescriptionXmlHandler.addEndElementHandler(DESCRIPTION_ELEMENT, this);

     	myRequirementIdXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, REQUIREMENT_ELEMENT);
		this.addStartElementHandler(REQUIREMENT_ELEMENT, myRequirementIdXmlHandler);
		myRequirementIdXmlHandler.addEndElementHandler(REQUIREMENT_ELEMENT, this);

    	myInitializeXmlHandler = new TestStepXmlHandler( anXmlReader, 
    													 myFactory.getTestCaseFactory().getTestStepFactory(),
    													 TestStep.ActionType.initialize );
		this.addStartElementHandler(TestStep.ActionType.initialize.toString(), myInitializeXmlHandler);
		myInitializeXmlHandler.addEndElementHandler(TestStep.ActionType.initialize.toString(), this);

		myTestCaseXmlHandler = new TestCaseXmlHandler(anXmlReader, myFactory.getTestCaseFactory());
		this.addStartElementHandler(TestCaseXmlHandler.START_ELEMENT, myTestCaseXmlHandler);
		myTestCaseXmlHandler.addEndElementHandler(TestCaseXmlHandler.START_ELEMENT, this);

		myScriptXmlHandler = new ScriptXmlHandler(anXmlReader);
		this.addStartElementHandler(ScriptXmlHandler.START_ELEMENT, myScriptXmlHandler);
		myScriptXmlHandler.addEndElementHandler(ScriptXmlHandler.START_ELEMENT, this);

		myRestoreXmlHandler = new TestStepXmlHandler( anXmlReader, 
													  myFactory.getTestCaseFactory().getTestStepFactory(),
													  TestStep.ActionType.restore );
		this.addStartElementHandler(TestStep.ActionType.restore.toString(), myRestoreXmlHandler);
		myRestoreXmlHandler.addEndElementHandler(TestStep.ActionType.restore.toString(), this);

		// Note: myTestGroupXmlHandler is only created when needed, otherwise we would create
		//       an endless loop
	}

	@Override
	public void handleStartElement(String aQualifiedName)
	{
   		//nop;
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

    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		Trace.print(Trace.SUITE, "processElementAttributes( " 
	            + aQualifiedName, true );
     	if (aQualifiedName.equalsIgnoreCase(START_ELEMENT))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
	    		Trace.append( Trace.SUITE, ", " + att.getQName(i) + "=" + att.getValue(i) );
		    	if (att.getQName(i).equalsIgnoreCase(ELEMENT_ID))
		    	{
		        	myCurrentTestGroupId = att.getValue(i);
		    	}
		    	if (att.getQName(i).equalsIgnoreCase(ELEMENT_TYPE))
		    	{
		        	myCurrentTestGroupType = att.getValue(i);
		    	}
		    	if (att.getQName(i).equalsIgnoreCase(ELEMENT_SEQ))
		    	{
		        	myCurrentTestGroupSeq = Integer.valueOf( att.getValue(i) ).intValue();
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
	public void handleGoToChildElement(String aQualifiedName) throws SAXParseException
	{
		Trace.println(Trace.SUITE, "handleGoToChildElement( " + aQualifiedName + " )", true );
		if (myIsTopTestGroup)
		{
			// If this <testgroup>-startelement is the top test group, this handler
			// will handle it.
			// The next one is a sub-testgroup, so a new TestGroupXmlHandler has to handle it.
			myIsTopTestGroup = false;
		}
		else
		{
	     	if (aQualifiedName.equalsIgnoreCase(START_ELEMENT))
	    	{
	     		// We'll create a myTestGroupXmlHandler for Sub-Test Groups only when we need it.
	     		// Otherwise it would create an endless loop.
	    		myTestGroupXmlHandler = new TestGroupXmlHandler(this.getXmlReader(), myFactory);
	    		this.addStartElementHandler(TestGroupXmlHandler.START_ELEMENT, myTestGroupXmlHandler);
	    		myTestGroupXmlHandler.addEndElementHandler(TestGroupXmlHandler.START_ELEMENT, this);
	    	}
		}
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
		Trace.println(Trace.SUITE, "handleReturnFromChildElement( " + aQualifiedName + " )", true);

		if (aQualifiedName.equalsIgnoreCase(DESCRIPTION_ELEMENT))
    	{
    		myCurrentDescription = myDescriptionXmlHandler.getValue();
    		myDescriptionXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(REQUIREMENT_ELEMENT))
    	{
    		myRequirementIds.add( myRequirementIdXmlHandler.getValue() );
    		myRequirementIdXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TestStep.ActionType.initialize.toString()))
    	{
    		myInitializationSteps.add( myInitializeXmlHandler.getActionStep() );
    		myInitializeXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TestGroupXmlHandler.START_ELEMENT))
    	{
			try
			{
				myTestEntries.add((TestEntry) myTestGroupXmlHandler.getTestGroup());
			}
			catch (TestGroupReaderException e)
			{
				Warning.println("Cannot add TestGroup: " + e.getMessage());
				Trace.print(Trace.SUITE, e);
			}
    		myTestGroupXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TestCaseXmlHandler.START_ELEMENT))
    	{
			try
			{
				myTestEntries.add((TestEntry) myTestCaseXmlHandler.getTestCase());
			}
			catch (SAXParseException e)
			{
				Warning.println("Cannot add TestCase: " + e.getMessage());
				Trace.print(Trace.SUITE, e);
			}
    		myTestCaseXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ScriptXmlHandler.START_ELEMENT))
    	{
    		myCurrentExecutionScript = myScriptXmlHandler.getScript();
    		myScriptXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TestStep.ActionType.restore.toString()))
    	{
    		myRestoreSteps.add( myRestoreXmlHandler.getActionStep() );
    		myRestoreXmlHandler.reset();
    	}
		// else nothing (ignored)
	}

	public void reset()
	{
		Trace.println(Trace.SUITE);

		myCurrentTestGroupId = "";
		myCurrentTestGroupType = "";
		myCurrentTestGroupSeq = 0;
	    myCurrentDescription = "";
	    myRequirementIds = new ArrayList<String>();
	    myInitializationSteps = new TestStepArrayList();
		myTestEntries = new TestEntryArrayList();
	    myCurrentExecutionScript = null;
	    myRestoreSteps = new TestStepArrayList();
	}

	public TestGroup getTestGroup() throws TestGroupReaderException
	{
		Trace.println(Trace.SUITE);

		if ( myCurrentTestGroupId.isEmpty() )
		{
			throw new TestGroupReaderException( "Unknown TestGroup ID", new LocatorImpl());
		}

      	TestGroup testGroup = myFactory.create( myCurrentTestGroupId,
      											myCurrentTestGroupType,
     											myCurrentTestGroupSeq,
     											myCurrentDescription,
     											myRequirementIds,
     											myInitializationSteps.sort(),
       											myTestEntries.sort(),
       											myCurrentExecutionScript,
       											myRestoreSteps.sort() );
      		
		return testGroup;
	}
}
