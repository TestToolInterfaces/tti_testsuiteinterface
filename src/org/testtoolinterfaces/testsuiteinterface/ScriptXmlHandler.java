package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.Parameter;
import org.testtoolinterfaces.testsuite.ParameterArrayList;
import org.testtoolinterfaces.testsuite.TestScript;
import org.testtoolinterfaces.testsuite.TestScriptImpl;
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
 *  <script type="...">
 *   <executable>...</executable>
 *   <parameter id="..." type="..." sequence="1">...</parameter>
 *   <parameter id="..." type="..." sequence="2">...</parameter>
 *  </script>
 * 
 */
public class ScriptXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "script";
	public static final String EXECUTABLE_ELEMENT = "executable";

	public static final String TYPE_PARAM = "type";

	private String myScript = "";
	private String myType = "standard";
    private ParameterArrayList myParameters;

	private GenericTagAndStringXmlHandler myExecutableXmlHandler;
	private CommandParameterXmlHandler myParameterXmlHandler;
	
	public ScriptXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.LEVEL.CONSTRUCTOR);

		myParameters = new ParameterArrayList();

		myExecutableXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, EXECUTABLE_ELEMENT);
		this.addStartElementHandler(EXECUTABLE_ELEMENT, myExecutableXmlHandler);
		myExecutableXmlHandler.addEndElementHandler(EXECUTABLE_ELEMENT, this);

		myParameterXmlHandler = new CommandParameterXmlHandler(anXmlReader);
		this.addStartElementHandler(CommandParameterXmlHandler.START_ELEMENT, myParameterXmlHandler);
		myParameterXmlHandler.addEndElementHandler(CommandParameterXmlHandler.START_ELEMENT, this);
	}

    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		Trace.print(Trace.SUITE, "processElementAttributes( "
		            + aQualifiedName, true );
	    	if (aQualifiedName.equalsIgnoreCase(ScriptXmlHandler.START_ELEMENT))
	    	{
			    for (int i = 0; i < att.getLength(); i++)
			    {
					Trace.append( Trace.SUITE, ", " + att.getQName(i) + "=" + att.getValue(i) );
			    	if (att.getQName(i).equalsIgnoreCase(TYPE_PARAM))
			    	{
			    		myType = att.getValue(i);
			    	}
			    }
	    	}
			Trace.append( Trace.SUITE, " )\n");
    }

	@Override
	public void handleStartElement(String aQualifiedName)
	{
		// nop
	}

	@Override
	public void handleCharacters(String aValue)
	{
		// nop
	}

	@Override
	public void handleEndElement(String aQualifiedName)
	{
		// nop
	}

	@Override
	public void handleGoToChildElement(String aQualifiedName)
	{
		// nop
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
		Trace.println(Trace.LEVEL.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(EXECUTABLE_ELEMENT))
    	{
    		myScript = myExecutableXmlHandler.getValue();
    		myExecutableXmlHandler.reset();
    	}
    	if (aQualifiedName.equalsIgnoreCase(CommandParameterXmlHandler.START_ELEMENT))
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
	}

	public TestScript getScript()
	{
		return new TestScriptImpl(myScript, myType, myParameters);
	}
	
	public void reset()
	{
		Trace.println(Trace.LEVEL.SUITE);

		myScript = "";
		myType = "standard";
		myParameters = new ParameterArrayList();
	}
}
