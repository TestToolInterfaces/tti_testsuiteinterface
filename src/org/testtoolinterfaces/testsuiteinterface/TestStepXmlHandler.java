package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.Parameter;
import org.testtoolinterfaces.testsuite.ParameterArrayList;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepScript;
import org.testtoolinterfaces.testsuite.TestStepSimple;
import org.testtoolinterfaces.testsuite.TestStepSimple.SimpleType;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.Warning;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
//import org.xml.sax.Locator;
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

	private static final String DESCRIPTION_ELEMENT = "description";

	private GenericTagAndStringXmlHandler myDescriptionXmlHandler;
//	private CommandXmlHandler myCommandXmlHandler;
	private TestStepScriptXmlHandler myScriptXmlHandler;
	private CommandParameterXmlHandler myParameterXmlHandler;

	private int mySequence;
	
	private String myDescription;
	private String myExecutionScript;
	private String myScriptType;
    private ParameterArrayList myParameters;

	public TestStepXmlHandler( XMLReader anXmlReader, SimpleType aTag )
	{
		super(anXmlReader, aTag.toString());
		Trace.println(Trace.LEVEL.CONSTRUCTOR, "ActionXmlHandler( anXmlreader, " + aTag + " )", true);

		myDescriptionXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, DESCRIPTION_ELEMENT);
		this.addStartElementHandler(DESCRIPTION_ELEMENT, myDescriptionXmlHandler);
		myDescriptionXmlHandler.addEndElementHandler(DESCRIPTION_ELEMENT, this);

//		myCommandXmlHandler = new CommandXmlHandler(anXmlReader);
//		this.addStartElementHandler(CommandXmlHandler.START_ELEMENT, myCommandXmlHandler);
//		myCommandXmlHandler.addEndElementHandler(CommandXmlHandler.START_ELEMENT, this);

		myScriptXmlHandler = new TestStepScriptXmlHandler(anXmlReader);
		this.addStartElementHandler(TestStepScriptXmlHandler.ELEMENT_START, myScriptXmlHandler);
		myScriptXmlHandler.addEndElementHandler(TestStepScriptXmlHandler.ELEMENT_START, this);

		myParameterXmlHandler = new CommandParameterXmlHandler(anXmlReader);
		this.addStartElementHandler(CommandParameterXmlHandler.START_ELEMENT, myParameterXmlHandler);
		myParameterXmlHandler.addEndElementHandler(CommandParameterXmlHandler.START_ELEMENT, this);
		
		reset();
	}

    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		Trace.println(Trace.LEVEL.SUITE, "processElementAttributes( "
				+ aQualifiedName + " )", true );
    	if (aQualifiedName.equalsIgnoreCase(this.getStartElement()))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_SEQUENCE))
		    	{
		    		mySequence = Integer.valueOf( att.getValue(i) ).intValue();
		    		Trace.println( Trace.LEVEL.ALL, "        mySequence -> " + mySequence);
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
		// TODO Add Command.
		Trace.println(Trace.LEVEL.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(DESCRIPTION_ELEMENT))
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
    	else if (aQualifiedName.equalsIgnoreCase(CommandParameterXmlHandler.START_ELEMENT))
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

	public TestStepSimple getActionStep()
	{
		Trace.println(Trace.LEVEL.SUITE);

		TestStepSimple testStep;
// TODO Switch on script or command
		
// TODO What to do with myInterface?
		
//		TestCommand command = TestCommand.getEmptyInstance();
//		try
//		{
//			command = myCommandXmlHandler.getCommand();
//		}
//		catch (Exception e)
//		{
//			Locator locator = this.getLocator();
//			Warning.println( "No suitable command found in " + this.getStartElement() + "-block at line " 
//					+ locator.getLineNumber() + " of:\n         " + locator.getSystemId() );
//			Warning.println( e.getMessage() );
//			Trace.printException(Trace.LEVEL.ALL, e);
//		}

		testStep = new TestStepScript( TestStep.StepType.valueOf(this.getStartElement()),
		                               mySequence,
		                               myDescription,
		                               myExecutionScript,
		                               myScriptType,
		                               myParameters );

		return testStep;
	}

	public void reset()
	{
		Trace.println(Trace.LEVEL.SUITE);

		mySequence = 0;
		
		myDescription = "";
		myExecutionScript = null;
		myScriptType = "";
		myParameters = new ParameterArrayList();
	}
}
