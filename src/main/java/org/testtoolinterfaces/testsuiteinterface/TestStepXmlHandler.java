package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.Parameter;
import org.testtoolinterfaces.testsuite.ParameterArrayList;
import org.testtoolinterfaces.testsuite.TestInterface;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepCommand;
import org.testtoolinterfaces.testsuite.TestStepScript;
import org.testtoolinterfaces.testsuite.TestStepSelection;
import org.testtoolinterfaces.testsuite.TestStepSequence;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.Warning;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <teststep [sequence=...]>
 *  <description>
 *    ...
 *  </description>
 *  <parameter>
 *    ...
 *  </parameter>
 *  <command> ... </command>
 *  ...
 * </teststep>
 * 
 * <teststep [sequence=...]>
 *  <description>
 *    ...
 *  </description>
 *  <parameter>
 *    ...
 *  </parameter>
 *  <script> ... </script>
 *  ...
 * </teststep>
 * 
 * <if [not="true"] [sequence=...]>
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
 * <if [not="true"] [sequence=...]>
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
 * <{tag} [sequence=...]>
 *  <description>
 *    ...
 *  </description>
 *  <command> ... </command>]
 *  <parameter>
 *    ...
 *  </parameter>
 *  ...
 * </{tag}>
 * 
 * <{tag} [sequence=...]>
 *  <description>
 *    ...
 *  </description>
 *  <script> ... </script>
 *  <parameter>
 *    ...
 *  </parameter>
 *  ...
 * </{tag}>
 * 
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */
public class TestStepXmlHandler extends TestEntryXmlHandler
{
	public static final String START_ELEMENT = "teststep";
	public static final String IF_ELEMENT = "if";
	private static final String THEN_ELEMENT = "then";
	private static final String ELSE_ELEMENT = "else";

	private static final String ATTRIBUTE_NOT = "not";

    private TestInterfaceList myInterfaces;
	private boolean myCheckStepParams = false;

	// The sub-handlers
	private CommandXmlHandler myCommandXmlHandler;
	private ScriptXmlHandler myScriptXmlHandler;
	private ParameterXmlHandler myParameterXmlHandler;
	private TestStepSequenceXmlHandler myThenXmlHandler;
	private TestStepSequenceXmlHandler myElseXmlHandler;

	// Needed to create the TestStep
    private ParameterArrayList myParameters;
	
    // In case of a TestStepCommand
	private String myCommand;
	private TestInterface myInterface;

    // In case of a TestStepScript
	private String myScript;
	private String myScriptType;

    // In case of a TestStepSelection
	private boolean myNot=false;
	private TestStepSequence myThenSteps;
	private TestStepSequence myElseSteps;

	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader			The xmlReader
	 * @param aTag					The start-element for this XML Handler
	 * @param anInterfaceList		A list of interfaces
	 * @param aCheckStepParameter	Flag to indicate if specified parameters of a step must be verified in the interface
	 */
	public TestStepXmlHandler( XMLReader anXmlReader,
	                           String aTag,
	                           TestInterfaceList anInterfaceList,
	                           boolean aCheckStepParameter )
	{
		super(anXmlReader, aTag);
		Trace.println(Trace.CONSTRUCTOR, "TestStepXmlHandler( anXmlreader )", true);

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

		if (aTag.equalsIgnoreCase(IF_ELEMENT)) {
    		myThenXmlHandler = new TestStepSequenceXmlHandler(this.getXmlReader(), THEN_ELEMENT, anInterfaceList, myCheckStepParams);
    		this.addElementHandler(myThenXmlHandler);

    		myElseXmlHandler = new TestStepSequenceXmlHandler(this.getXmlReader(), ELSE_ELEMENT, anInterfaceList, myCheckStepParams);
    		this.addElementHandler(myElseXmlHandler);
 		}

		myInterfaces = anInterfaceList;
		myCheckStepParams = aCheckStepParameter;

		this.resetStepHandler();
	}

	/**
	 * Creates the XML Handler for element teststep
	 * 
	 * @param anXmlReader the xmlReader
	 * @param anInterfaceList a list of interfaces
	 * @param aCheckStepParameter flag to indicate if specified parameters of a step must be verified in the interface
	 */
	public TestStepXmlHandler( XMLReader anXmlReader,
	                           TestInterfaceList anInterfaceList,
	                           boolean aCheckStepParameter )
	{
		this( anXmlReader, START_ELEMENT, anInterfaceList, aCheckStepParameter);
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
				Warning.println( "Command \"" + myCommand + "\" is defined twice. Last one is used." );   			
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
				Warning.println( "Script \"" + myScript + "\" is defined twice. Last one is used." );   			
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
			myThenSteps = myThenXmlHandler.getSteps();
			myThenXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ELSE_ELEMENT))
    	{
			myElseSteps = myElseXmlHandler.getSteps();
			myElseXmlHandler.reset();
    	}
    	else {
			super.handleReturnFromChildElement(aQualifiedName, aChildXmlHandler);
    	}
	}

	/**
	 * @return the TestStep
	 * @throws TestSuiteException when not all information is defined
	 */
	public TestStep getStep() throws TestSuiteException
	{
		Trace.println(Trace.SUITE);

		TestStep testStep = null;
		if ( myCommand != null )
		{
			testStep = createTestStepCommand( myInterface, myCommand, myParameters,
					myCheckStepParams, this.getDescription(), this.getSequenceNr() );
		}
		else if ( myScript != null )
		{
			testStep = createTestStepScript( myScript, myScriptType, myParameters,
					this.getDescription(), this.getSequenceNr() );
		}
		else
		{
			throw new TestSuiteException( "Cannot make a TestStep Command, Script, or Selection from given information" );
		}

		if ( this.getStartElement().equalsIgnoreCase(IF_ELEMENT) )
		{
			testStep = createTestStepSelection( testStep );
		}
		
		testStep.setAnyAttributes( this.getAnyAttributes() );
		testStep.setAnyElements( this.getAnyElements() );
		
		return testStep;
	}

	/**
	 * Creates a TestStep command.
	 * 
	 * @return the TestStepCommand
	 * 
	 * @throws TestSuiteException	When the interface does not support the command
	 * 								or when the parameters are not correct defined for the command.
	 * @throws Error				When the interface is null -> programming error.
	 */
	public static TestStepCommand createTestStepCommand( TestInterface anInterface, String aCommand,
			ParameterArrayList aParameters, boolean aCheckParameters,
			String aDescription, int aSequenceNr ) throws Error, TestSuiteException
	{
		if( anInterface == null )
		{
			throw new Error( "Interface cannot be null" );
		}

		if( ! anInterface.hasCommand(aCommand) )
		{
			throw new TestSuiteException( "Command " + aCommand + " not known for interface " + anInterface.getInterfaceName() );
		}

		/*
		 * TODO We should also create them
		 * In XML the parameters could be defined before the command & interface,
		 * and then myParameterXmlHandler.setCurrentInterface() is still set to default.
		 * How to solve?
		 * 
		 */
		if( aCheckParameters )
		{
			anInterface.verifyParameters(aCommand, aParameters);
		}

		TestStepCommand tsCommand = new TestStepCommand( aSequenceNr, aDescription,
				aCommand, anInterface, aParameters );
		
		return tsCommand;
	}

	/**
	 * @return the TestStep as TestStepScript
	 * 
	 */
	public static TestStepScript createTestStepScript( String aScript, String aScriptType,
			ParameterArrayList aParameters,	String aDescription, int aSequenceNr )
	{
		TestStepScript tsScript = new TestStepScript( aSequenceNr, aDescription,
				aScript, aScriptType, aParameters );

		return tsScript;
	}

	/**
	 * @param testStep the if-step
	 * @return the TestStep as TestStepSelection
	 * @throws TestSuiteException 
	 */
	private TestStepSelection createTestStepSelection(TestStep ifStep) throws TestSuiteException
	{
		if ( myThenSteps == null )
		{
			throw new TestSuiteException( "No then-step defined for selection" );
		}

		TestStepSelection tsScript = new TestStepSelection( this.getSequenceNr(), this.getDescription(),
		                              ifStep, myNot, myThenSteps, myElseSteps );

		return tsScript;
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
        myThenSteps = new TestStepSequence();
        myElseSteps = new TestStepSequence();
        
        super.resetEntryHandler();
	}
}
