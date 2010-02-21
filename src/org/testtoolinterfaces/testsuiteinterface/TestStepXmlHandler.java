package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.TestScript;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepFactory;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
//import org.xml.sax.Locator;
import org.xml.sax.XMLReader;



/**
 * @author Arjan Kranenburg 
 * 
 * <initialize|action|check|restore sequence=...>
 *  <description>
 *  ...
 *  </description>
 *  <command>
 *  ...
 *  </command>
 *  <script>
 *  ...
 *  </script>
 * </initialize|action|check|restore>
 */
public class TestStepXmlHandler extends XmlHandler
{
	private static final String PARAM_SEQUENCE = "sequence";

	private static final String DESCRIPTION_ELEMENT = "description";
	private static final String SCRIPT_ELEMENT = "script";

	private GenericTagAndStringXmlHandler myDescriptionXmlHandler;
//	private CommandXmlHandler myCommandXmlHandler;
	private ScriptXmlHandler myScriptXmlHandler;

	private TestStepFactory myFactory;
	private int myCurrentSequence = 0;
	private String myCurrentDescription = "";
	private TestScript myCurrentExecutionScript;

	public TestStepXmlHandler( XMLReader anXmlReader, TestStepFactory aFactory, TestStep.ActionType aTag )
	{
		super(anXmlReader, aTag.toString());
		Trace.println(Trace.LEVEL.CONSTRUCTOR, "ActionXmlHandler( anXmlreader, " + aTag + " )", true);

		myFactory = aFactory;
				
	    myDescriptionXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, DESCRIPTION_ELEMENT);
		this.addStartElementHandler(DESCRIPTION_ELEMENT, myDescriptionXmlHandler);
		myDescriptionXmlHandler.addEndElementHandler(DESCRIPTION_ELEMENT, this);

//		myCommandXmlHandler = new CommandXmlHandler(anXmlReader);
//		this.addStartElementHandler(CommandXmlHandler.START_ELEMENT, myCommandXmlHandler);
//		myCommandXmlHandler.addEndElementHandler(CommandXmlHandler.START_ELEMENT, this);

		myScriptXmlHandler = new ScriptXmlHandler(anXmlReader);
		this.addStartElementHandler(SCRIPT_ELEMENT, myScriptXmlHandler);
		myScriptXmlHandler.addEndElementHandler(SCRIPT_ELEMENT, this);
	}

    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		Trace.println(Trace.LEVEL.SUITE, "processElementAttributes( "
				+ aQualifiedName + " )", true );
    	if (aQualifiedName.equalsIgnoreCase(this.getStartElement()))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
		    	if (att.getQName(i).equalsIgnoreCase(PARAM_SEQUENCE))
		    	{
		    		myCurrentSequence = Integer.valueOf( att.getValue(i) ).intValue();
		    		Trace.println( Trace.LEVEL.ALL, "        myCurrentSequence -> " + myCurrentSequence);
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
    		myCurrentDescription  = myDescriptionXmlHandler.getValue();
        	myDescriptionXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ScriptXmlHandler.START_ELEMENT))
    	{
    		myCurrentExecutionScript = myScriptXmlHandler.getScript();
    		myScriptXmlHandler.reset();
    	}
		// else nothing (ignored)
	}

	public TestStep getActionStep()
	{
		Trace.println(Trace.LEVEL.SUITE);

// TODO Switch on script or command
		
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

		TestStep testStep = myFactory.create( TestStep.ActionType.valueOf(this.getStartElement()),
		                                      myCurrentExecutionScript.getType(),
		                                      myCurrentSequence,
		                                      myCurrentDescription,
		                                      myCurrentExecutionScript.getExecutionScript(),
		                                      myCurrentExecutionScript.getParameters() );

		return testStep;
	}

	public void reset()
	{
		Trace.println(Trace.LEVEL.SUITE);
		myCurrentSequence = 0;
		myCurrentDescription = "";
		myCurrentExecutionScript = null;
	}
}
