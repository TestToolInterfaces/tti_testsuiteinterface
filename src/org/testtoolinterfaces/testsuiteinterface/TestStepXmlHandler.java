package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.Parameter;
import org.testtoolinterfaces.testsuite.ParameterArrayList;
import org.testtoolinterfaces.testsuite.TestInterface;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepCommand;
import org.testtoolinterfaces.testsuite.TestStepScript;
import org.testtoolinterfaces.testsuite.TestStepSimple;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.testsuite.TestStep.StepType;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.Warning;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * @author Arjan Kranenburg 
 * 
 * <action|check sequence=... interface=...>
 *  <description>
 *  ...
 *  </description>
 *  <command>
 *  ...
 *  </command>
 *  <script>
 *  ...
 *  </script>
 * </action|check>
 */
public class TestStepXmlHandler extends XmlHandler
{
	private static final String ATTRIBUTE_SEQUENCE = "sequence";
	private static final String ATTRIBUTE_INTERFACE = "interface";

	private static final String COMMAND_ELEMENT = "command";
	private static final String DESCRIPTION_ELEMENT = "description";

	private GenericTagAndStringXmlHandler myDescriptionXmlHandler;
	private GenericTagAndStringXmlHandler myCommandXmlHandler;
	private TestStepScriptXmlHandler myScriptXmlHandler;
	private ParameterXmlHandler myParameterXmlHandler;

	private boolean myCheckStepParams = false;
	private int mySequence;
	
	private String myCommand;
	private String myInterface;
	private String myDescription;
	private String myExecutionScript;
	private String myScriptType;
    private ParameterArrayList myParameters;
    
    private TestInterfaceList myInterfaces;

	public TestStepXmlHandler( XMLReader anXmlReader,
	                           StepType aTag,
	                           TestInterfaceList anInterfaceList,
	                           boolean aCheckStepParameter )
	{
		super(anXmlReader, aTag.toString());
		Trace.println(Trace.CONSTRUCTOR, "ActionXmlHandler( anXmlreader, " + aTag + " )", true);

		myDescriptionXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, DESCRIPTION_ELEMENT);
		this.addStartElementHandler(DESCRIPTION_ELEMENT, myDescriptionXmlHandler);
		myDescriptionXmlHandler.addEndElementHandler(DESCRIPTION_ELEMENT, this);

		myCommandXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, COMMAND_ELEMENT);
		this.addStartElementHandler(COMMAND_ELEMENT, myCommandXmlHandler);
		myCommandXmlHandler.addEndElementHandler(COMMAND_ELEMENT, this);

		myScriptXmlHandler = new TestStepScriptXmlHandler(anXmlReader);
		this.addStartElementHandler(TestStepScriptXmlHandler.ELEMENT_START, myScriptXmlHandler);
		myScriptXmlHandler.addEndElementHandler(TestStepScriptXmlHandler.ELEMENT_START, this);

		myParameterXmlHandler = new ParameterXmlHandler(anXmlReader);
		this.addStartElementHandler(ParameterXmlHandler.START_ELEMENT, myParameterXmlHandler);
		myParameterXmlHandler.addEndElementHandler(ParameterXmlHandler.START_ELEMENT, this);
		
		myCheckStepParams = aCheckStepParameter;
		myInterfaces = anInterfaceList;

		reset();
	}

	@Override
    public void processElementAttributes(String aQualifiedName, Attributes att) throws TestSuiteException
    {
		Trace.println(Trace.SUITE, "processElementAttributes( "
				+ aQualifiedName + " )", true );
    	if (aQualifiedName.equalsIgnoreCase(this.getStartElement()))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_SEQUENCE))
		    	{
		    		mySequence = Integer.valueOf( att.getValue(i) ).intValue();
		    		Trace.println( Trace.ALL, "        mySequence -> " + mySequence);
    	    	}
		    	else if( att.getQName(i).equalsIgnoreCase(ATTRIBUTE_INTERFACE) )
		    	{
		    		myInterface = att.getValue(i);

		    		TestInterface iFace = myInterfaces.getInterface(myInterface);
					if( iFace == null )
					{
						throw new TestSuiteException( "Unknown interface: " + myInterface );
					}
					myParameterXmlHandler.setCurrentInterface(iFace);

		    		Trace.println( Trace.ALL, "        myInterface -> " + myInterface);
		    	}
		    }
    	}
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
	
	@Override
	public void handleGoToChildElement(String aQualifiedName)
	{
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
		Trace.println(Trace.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(COMMAND_ELEMENT))
    	{
    		myCommand  = myCommandXmlHandler.getValue();
    		myCommandXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(DESCRIPTION_ELEMENT))
    	{
    		myDescription  = myDescriptionXmlHandler.getValue();
        	myDescriptionXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TestStepScriptXmlHandler.ELEMENT_START))
    	{
    		myExecutionScript = myScriptXmlHandler.getScript();
    		myScriptType = myScriptXmlHandler.getScriptType();
    		myScriptXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ParameterXmlHandler.START_ELEMENT))
    	{
			try
			{
	    		Parameter parameter = myParameterXmlHandler.getParameter();
	    		myParameters.add(parameter);
			}
			catch (SAXParseException e)
			{
				Warning.println("Cannot add Parameter: " + e.getMessage());
				Trace.print(Trace.SUITE, e);
			}
    		
    		myParameterXmlHandler.reset();
    	}
		// else nothing (ignored)
	}

	public TestStepSimple getActionStep() throws TestSuiteException
	{
		Trace.println(Trace.SUITE);

		TestStepSimple testStep;
		
		if ( myCommand != null )
		{
			TestInterface iFace = myInterfaces.getInterface(myInterface);
			if( ! iFace.hasCommand(myCommand) )
			{
				throw new TestSuiteException( "Command " + myCommand + " not known for interface " + iFace.getInterfaceName() );
			}
			
			if( myCheckStepParams )
			{
				iFace.verifyParameters(myCommand, myParameters);
			}

			testStep = new TestStepCommand( TestStep.StepType.valueOf(this.getStartElement()),
			                                mySequence,
			                                myDescription,
			                                myCommand,
			                                iFace,
			                                myParameters );

		}
		else if ( myExecutionScript != null )
		{
			testStep = new TestStepScript( TestStep.StepType.valueOf(this.getStartElement()),
			                               mySequence,
			                               myDescription,
			                               myExecutionScript,
			                               myScriptType,
			                               myParameters );

		}
		else
		{
			throw new TestSuiteException( "No command or execution script found" );
		}

		return testStep;
	}

	public void reset()
	{
		Trace.println(Trace.SUITE);

		mySequence = 0;
		
		myCommand = null;
		myInterface = "Default";
		TestInterface defaultInterface = myInterfaces.getInterface(myInterface);
		myParameterXmlHandler.setCurrentInterface(defaultInterface);

		myDescription = "";
		myExecutionScript = null;
		myScriptType = "";
		myParameters = new ParameterArrayList();
	}
}
