package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.Parameter;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.LocatorImpl;



/**
 * @author Arjan Kranenburg 
 * 
 *  <parameter id="..." sequence="..." type="...">
 *    ...
 *  </parameter>
 * 
 */
public class CommandParameterXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "parameter";
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_TYPE = "type";
	public static final String ATTRIBUTE_SEQUENCE = "sequence";
	public static final String VALUE_ELEMENT = "value";
	
//	private ParameterFactory myFactory;
	private String myCurrentParameterId = "";
//	private String myCurrentParameterType = "";
	private int myCurrentSequence = Integer.MAX_VALUE;
    private String myCurrentValue = "";

	public CommandParameterXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.LEVEL.CONSTRUCTOR);
	}

    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		Trace.print(Trace.SUITE, "processElementAttributes( "
	            + aQualifiedName, true );
    	if (aQualifiedName.equalsIgnoreCase(CommandParameterXmlHandler.START_ELEMENT))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
				Trace.append( Trace.SUITE, ", " + att.getQName(i) + "=" + att.getValue(i) );
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_ID))
		    	{
		        	myCurrentParameterId = att.getValue(i);
		    	}
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_TYPE))
		    	{
//		        	myCurrentParameterType = att.getValue(i);
		    	}
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_SEQUENCE))
		    	{
		    		myCurrentSequence = Integer.valueOf( att.getValue(i) ).intValue();
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
		if (aValue != "\n") // ignore empty lines
		{
			myCurrentValue = aValue.trim();

			Trace.println(Trace.LEVEL.SUITE, "handleCharacters( "
		            + myCurrentValue + " )", true);
		}
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
		// nop
	}

	public Parameter getParameter() throws SAXParseException
	{
		Trace.println(Trace.LEVEL.GETTER, "getParameter()", true);
		if ( myCurrentParameterId.isEmpty() )
		{
			throw new SAXParseException("Unknown Parameter ID", new LocatorImpl());
		}

//		Parameter parameter = myFactory.create( myCurrentParameterId,
//		                                        myCurrentParameterType,
//		                                        myCurrentSequence,
//		                                        myCurrentValue );
		Parameter parameter = new Parameter(myCurrentParameterId, myCurrentValue);
		parameter.setIndex(myCurrentSequence);

		return parameter;
	}

	public void reset()
	{
		Trace.println(Trace.LEVEL.SUITE);
		
		myCurrentParameterId = "";
//		myCurrentParameterType = "";
		myCurrentSequence = Integer.MAX_VALUE;
	    myCurrentValue = "";
	}
}
