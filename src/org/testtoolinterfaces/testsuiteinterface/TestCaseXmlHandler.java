package org.testtoolinterfaces.testsuiteinterface;

import java.util.ArrayList;

import org.testtoolinterfaces.testsuite.TestCase;
import org.testtoolinterfaces.testsuite.TestCaseFactory;
import org.testtoolinterfaces.testsuite.TestScript;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepArrayList;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.LocatorImpl;



/**
 * @author Arjan Kranenburg 
 * 
 * <testcase id="..." type="..." sequence="...">
 *  <description>...</description>
 *  <requirementId>...</requirementId>
 *  <initialization>
 *   ...
 *  </initialization>
 *  <execution>
 *   ...
 *  </execution>
 *  <script>
 *   ...
 *  </script>
 *  <restore>
 *   ...
 *  </restore>
 * </testcase>
 */

public class TestCaseXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "testcase";
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_TYPE = "type";
	public static final String ATTRIBUTE_SEQUENCE = "sequence";
	
	private static final String DESCRIPTION_ELEMENT = "description";
	private static final String REQUIREMENT_ELEMENT = "requirementId";

	private TestCaseFactory myFactory;
	private String myCurrentTestCaseId = "";
	private String myCurrentTestCaseType = "";
	private String myCurrentDescription = "";
	private int myCurrentSequence = 0;
    private TestScript myCurrentExecutionScript;

    private ArrayList<String> myRequirementIds;
    private TestStepArrayList myInitializationSteps;
    private TestStepArrayList myExecutionSteps;
    private TestStepArrayList myRestoreSteps;

	private GenericTagAndStringXmlHandler myDescriptionXmlHandler;
	private GenericTagAndStringXmlHandler myRequirementIdXmlHandler;
	private TestStepXmlHandler myInitializeXmlHandler;
	private ExecutionXmlHandler myExecutionXmlHandler;
	private ScriptXmlHandler myScriptXmlHandler;
	private TestStepXmlHandler myRestoreXmlHandler;

	/**
	 * @param anXmlReader the xmlReader
	 * @param aBaseDir the baseDir of the parent script 
	 * @param aBaseLogDir a File Object to the Base log
	 * 
	 * @throws NullPointerException if aBaseLogDir is null
	 */
	public TestCaseXmlHandler( XMLReader anXmlReader, TestCaseFactory aFactory )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR);

		myFactory = aFactory;
		myRequirementIds = new ArrayList<String>();
		myInitializationSteps = new TestStepArrayList();
		myExecutionSteps = new TestStepArrayList();
		myRestoreSteps = new TestStepArrayList();

	    myDescriptionXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, DESCRIPTION_ELEMENT);
		this.addStartElementHandler(DESCRIPTION_ELEMENT, myDescriptionXmlHandler);
		myDescriptionXmlHandler.addEndElementHandler(DESCRIPTION_ELEMENT, this);

     	myRequirementIdXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, REQUIREMENT_ELEMENT);
		this.addStartElementHandler(REQUIREMENT_ELEMENT, myRequirementIdXmlHandler);
		myRequirementIdXmlHandler.addEndElementHandler(REQUIREMENT_ELEMENT, this);

    	myInitializeXmlHandler = new TestStepXmlHandler( anXmlReader,
    													 myFactory.getTestStepFactory(),
    													 TestStep.ActionType.initialize );
		this.addStartElementHandler(TestStep.ActionType.initialize.toString(), myInitializeXmlHandler);
		myInitializeXmlHandler.addEndElementHandler(TestStep.ActionType.initialize.toString(), this);

    	myExecutionXmlHandler = new ExecutionXmlHandler(anXmlReader, myFactory.getTestStepFactory());
		this.addStartElementHandler(ExecutionXmlHandler.START_ELEMENT, myExecutionXmlHandler);
		myExecutionXmlHandler.addEndElementHandler(ExecutionXmlHandler.START_ELEMENT, this);

		myScriptXmlHandler = new ScriptXmlHandler(anXmlReader);
		this.addStartElementHandler(ScriptXmlHandler.START_ELEMENT, myScriptXmlHandler);
		myScriptXmlHandler.addEndElementHandler(ScriptXmlHandler.START_ELEMENT, this);

		myRestoreXmlHandler = new TestStepXmlHandler( anXmlReader,
													  myFactory.getTestStepFactory(),
													  TestStep.ActionType.restore );
		this.addStartElementHandler(TestStep.ActionType.restore.toString(), myRestoreXmlHandler);
		myRestoreXmlHandler.addEndElementHandler(TestStep.ActionType.restore.toString(), this);
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
		        	myCurrentTestCaseId = att.getValue(i);
		    	}
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_TYPE))
		    	{
		        	myCurrentTestCaseType = att.getValue(i);
		    	}
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_SEQUENCE))
		    	{
		    		myCurrentSequence = Integer.valueOf( att.getValue(i) ).intValue();
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
    		myCurrentDescription  = myDescriptionXmlHandler.getValue();
        	myDescriptionXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(REQUIREMENT_ELEMENT))
    	{
    		myRequirementIds.add(myRequirementIdXmlHandler.getValue());
    		myRequirementIdXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TestStep.ActionType.initialize.toString()))
    	{
    		myInitializationSteps.add(myInitializeXmlHandler.getActionStep());
    		myInitializeXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ExecutionXmlHandler.START_ELEMENT))
    	{
    		myExecutionSteps = myExecutionXmlHandler.getExecutionSteps();
    		myExecutionXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ScriptXmlHandler.START_ELEMENT))
    	{
    		myCurrentExecutionScript = myScriptXmlHandler.getScript();
    		myScriptXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TestStep.ActionType.restore.toString()))
    	{
    		myRestoreSteps.add(myRestoreXmlHandler.getActionStep());
    		myRestoreXmlHandler.reset();
    	}
		// else nothing (ignored)
	}

	/**
     * @throws SAXParseException 
     */
    public TestCase getTestCase() throws SAXParseException
    {
		Trace.println(Trace.SUITE);

		if ( myCurrentTestCaseId.isEmpty() )
		{
			throw new SAXParseException("Unknown TestCase ID", new LocatorImpl());
		}

		if ( myExecutionSteps.isEmpty() && myCurrentExecutionScript == null )
		{
			throw new SAXParseException("No Execution Steps found for " + myCurrentTestCaseId, new LocatorImpl());
		}

       	TestCase testCase = myFactory.create( myCurrentTestCaseId,
       										  myCurrentTestCaseType,
       										  myCurrentSequence,
       										  myCurrentDescription,
       										  myRequirementIds,
       										  myInitializationSteps.sort(),
       										  myExecutionSteps.sort(),
       										  myCurrentExecutionScript,
       										  myRestoreSteps.sort() );
    	
		return testCase;
    }

	public int getSequence()
	{
		Trace.println(Trace.GETTER, "getSequence() -> " + myCurrentSequence, true);
		return myCurrentSequence;
	}

	public void reset()
	{
		Trace.println(Trace.SUITE);
		myCurrentTestCaseId = "";
		myCurrentTestCaseType = "";
		myCurrentSequence = 0;
		myCurrentDescription = "";
		myCurrentExecutionScript = null;
		
		myRequirementIds = new ArrayList<String>();
		myInitializationSteps = new TestStepArrayList();
		myRestoreSteps = new TestStepArrayList();
	}
}
