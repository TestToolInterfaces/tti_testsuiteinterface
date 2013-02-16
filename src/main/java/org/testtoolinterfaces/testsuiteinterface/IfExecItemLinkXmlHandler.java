package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.Parameter;
import org.testtoolinterfaces.testsuite.ParameterArrayList;
import org.testtoolinterfaces.testsuite.TestGroupEntrySelection;
import org.testtoolinterfaces.testsuite.TestGroupEntrySequence;
import org.testtoolinterfaces.testsuite.TestInterface;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.Warning;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <if [not="true"] sequence=...>
 *  <description>
 *    ...
 *  </description>
 *  <parameter>
 *    ...
 *  </parameter>
 *  <command> ... </command>]
 *  <then>...</then>
 *  <else>...</else>
 *  ...
 * </if>
 * 
 * <if [not] sequence=...>
 *  <description>
 *    ...
 *  </description>
 *  <parameter>
 *    ...
 *  </parameter>
 *  <script> ... </script>
 *  <then>...</then>
 *  <else>...</else>
 *  ...
 * </if>
 * 
 * 
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */
public class IfExecItemLinkXmlHandler extends TestGroupEntryXmlHandler
{
	public static final String START_ELEMENT = "if";
	private static final String THEN_ELEMENT = "then";
	private static final String ELSE_ELEMENT = "else";

	private static final String ATTRIBUTE_NOT = "not";

    private TestInterfaceList myInterfaces;
	private boolean myCheckStepParams = false;

	// The sub-handlers
	private CommandXmlHandler myCommandXmlHandler;
	private ScriptXmlHandler myScriptXmlHandler;
	private ParameterXmlHandler myParameterXmlHandler;
	private TestGroupEntrySequenceXmlHandler myThenXmlHandler;
	private TestGroupEntrySequenceXmlHandler myElseXmlHandler;

	// Needed to create the TestStep
	private boolean myNot=false;
    private ParameterArrayList myParameters;
	
    // In case of a TestStepCommand
	private String myCommand;
	private TestInterface myInterface;

    // In case of a TestStepScript
	private String myScript;
	private String myScriptType;

    // In case of a TestStepSelection
	private TestGroupEntrySequence myThenEntries;
	private TestGroupEntrySequence myElseEntries;

	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader			The xmlReader
	 * @param anInterfaceList		A list of interfaces
	 * @param aCheckStepParameter	Flag to indicate if specified parameters of a step must be verified in the interface
	 */
	public IfExecItemLinkXmlHandler( XMLReader anXmlReader,
	                           TestInterfaceList anInterfaceList,
	                           boolean aCheckStepParameter )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR, "IfExecItemLinkXmlHandler( anXmlreader )", true);

		myParameterXmlHandler = new ParameterXmlHandler(anXmlReader);
		this.addElementHandler(myParameterXmlHandler);

		try
		{
			myCommandXmlHandler = new CommandXmlHandler(anXmlReader, anInterfaceList);
		}
		catch (TestSuiteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.addElementHandler(myCommandXmlHandler);

		myScriptXmlHandler = new ScriptXmlHandler(anXmlReader);
		this.addElementHandler(myScriptXmlHandler);

		myThenXmlHandler = new TestGroupEntrySequenceXmlHandler(this.getXmlReader(), THEN_ELEMENT);
		this.addElementHandler(myThenXmlHandler);

		myElseXmlHandler = new TestGroupEntrySequenceXmlHandler(this.getXmlReader(), ELSE_ELEMENT);
		this.addElementHandler(myElseXmlHandler);

		myInterfaces = anInterfaceList;
		myCheckStepParams = aCheckStepParameter;

		this.resetStepHandler();
	}

	@Override
    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		Trace.print(Trace.SUITE, "processElementAttributes( "
				+ aQualifiedName + " )", true );
 		Attributes leftAttributes = new AttributesImpl();

    	if (aQualifiedName.equalsIgnoreCase(this.getStartElement()))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
	    		Trace.append( Trace.SUITE, ", " + att.getQName(i) + "=" + att.getValue(i) );
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_NOT))
		    	{
		    		myNot = true;
		    		// The value, if any, is ignored
		    		Trace.println( Trace.ALL, "        myNot -> true");
		    	} else {
		    		((AttributesImpl) leftAttributes).addAttribute( att.getURI(i), att.getLocalName(i),
		    				att.getQName(i), att.getType(i), att.getValue(i));
		    	}
		    }
    	} else {
    		leftAttributes = att;
    	}
		Trace.append( Trace.SUITE, " )\n" );
		
		super.processElementAttributes(aQualifiedName, leftAttributes);
    }

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
			throws TestSuiteException
	{
		Trace.println(Trace.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(ParameterXmlHandler.START_ELEMENT))
    	{
			try
			{
	    		Parameter parameter = myParameterXmlHandler.getParameter();
	    		myParameters.add(parameter);
			}
			catch (TestSuiteException e)
			{
				Warning.println("Cannot add Parameter: " + e.getMessage());
				Trace.print(Trace.SUITE, e);
			}
    		
    		myParameterXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(CommandXmlHandler.START_ELEMENT))
    	{
    		if ( myCommand != null )
    		{
				Warning.println( "Command is defined twice. Last one is used." );   			
    		}
    		if ( myScript != null )
    		{
				Warning.println( "Both Command and Script are defined" );   			
    		}
    		myCommand  = myCommandXmlHandler.getCommand();
    		myInterface = myCommandXmlHandler.getInterface();
    		myParameterXmlHandler.setCurrentInterface(myInterface);
    		myCommandXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ScriptXmlHandler.ELEMENT_START))
    	{
    		if ( myScript != null )
    		{
				Warning.println( "Script is defined twice. Last one is used." );   			
    		}
    		if ( myCommand != null )
    		{
				Warning.println( "Both Command and Script are defined" );   			
    		}
    		myScript = myScriptXmlHandler.getScript();
    		myScriptType = myScriptXmlHandler.getType();
    		myScriptXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(THEN_ELEMENT))
    	{
			myThenEntries = myThenXmlHandler.getEntries();
			myThenXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ELSE_ELEMENT))
    	{
			myElseEntries = myElseXmlHandler.getEntries();
			myElseXmlHandler.reset();
    	}
    	else {
			super.handleReturnFromChildElement(aQualifiedName, aChildXmlHandler);
    	}
	}

	/**
	 * @return the TestGroupEntrySelection
	 * @throws TestSuiteException when not all information is defined
	 */
	public TestGroupEntrySelection getIf() throws TestSuiteException
	{
		Trace.println(Trace.SUITE);

		TestStep testStep = null;
		if ( myCommand != null )
		{
			testStep = TestStepXmlHandler.createTestStepCommand( myInterface, myCommand, myParameters,
					myCheckStepParams, this.getDescription(), this.getSequenceNr() );
		}
		else if ( myScript != null )
		{
			testStep = TestStepXmlHandler.createTestStepScript( myScript, myScriptType, myParameters,
					this.getDescription(), this.getSequenceNr() );
		}
		else
		{
			throw new TestSuiteException( "Cannot make a TestGroupEntrySelection from given information" );
		}

		TestGroupEntrySelection tgeSelection = createTestGroupEntrySelection( testStep );
		
		tgeSelection.setAnyAttributes( this.getAnyAttributes() );
		tgeSelection.setAnyElements( this.getAnyElements() );
		
		return tgeSelection;
	}

	/**
	 * @param testStep the if-step
	 * @return the TestStep as TestStepSelection
	 * @throws TestSuiteException 
	 */
	private TestGroupEntrySelection createTestGroupEntrySelection(TestStep ifStep) throws TestSuiteException
	{
		String id = this.getId();
		if ( id.isEmpty() )
		{
			throw new TestSuiteException( "Unknown TestGroup ID" );
		}

		if ( myThenEntries == null )
		{
			throw new TestSuiteException( "No then-step defined for selection" );
		}

		TestGroupEntrySelection tgeSelection = new TestGroupEntrySelection( id, this.getDescription(), this.getSequenceNr(),
		                              ifStep, myNot, myThenEntries, myElseEntries );

		return tgeSelection;
	}

	@Override
	public void reset()
	{
		this.resetStepHandler();
	}

	public final void resetStepHandler()
	{
		Trace.println(Trace.SUITE);

		myParameters = new ParameterArrayList();
	
		myCommand = null;
		myInterface = myInterfaces.getInterface( CommandXmlHandler.DEFAULT_INTERFACE_NAME );
		myParameterXmlHandler.setCurrentInterface(myInterface);

		myScript = null;
		myScriptType = "";

		myNot=false;
        myThenEntries = new TestGroupEntrySequence();
        myElseEntries = new TestGroupEntrySequence();
        
        super.resetEntryHandler();
	}
}
