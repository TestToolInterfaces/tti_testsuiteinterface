package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.Parameter;
import org.testtoolinterfaces.testsuite.ParameterArrayList;
import org.testtoolinterfaces.testsuite.ParameterHash;
import org.testtoolinterfaces.testsuite.ParameterVariable;
import org.testtoolinterfaces.testsuite.TestInterface;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

/**
 * XmlHandler to read the parameter part from a TTI-XML file
 *  <parameter id="..." sequence="..." type="...">
 *    <value>...</value>
 *    <parameter>...</parameter>
 *    <variable>...</variable>
 *  </parameter>
 * 
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */
public class ParameterXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "parameter";
	private static final String ATTRIBUTE_ID = "id";
	private static final String ATTRIBUTE_TYPE = "type";
	private static final String ATTRIBUTE_SEQUENCE = "sequence";
	private static final String VALUE_ELEMENT = "value";
	private static final String VARIABLE_ELEMENT = "variable";
	
	private ParameterXmlHandler	myParameterXmlHandler;
	private GenericTagAndStringXmlHandler myValueXmlHandler;
	private GenericTagAndStringXmlHandler myVariableXmlHandler;

	private String myParameterId;
	private String myParameterType;
	private int mySequence;

	private String myValue;
	private ParameterArrayList mySubParameters;	
    private String myVariableName;

    private TestInterface myCurrentInterface;
    
	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader		The XML Reader
	 */
	public ParameterXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR);

		myParameterXmlHandler = null; // Created when needed to prevent loops

		myValueXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, VALUE_ELEMENT);
		this.addStartElementHandler(VALUE_ELEMENT, myValueXmlHandler);
		myValueXmlHandler.addEndElementHandler(VALUE_ELEMENT, this);

		myVariableXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, VARIABLE_ELEMENT);
		this.addStartElementHandler(VARIABLE_ELEMENT, myVariableXmlHandler);
		myVariableXmlHandler.addEndElementHandler(VARIABLE_ELEMENT, this);

		reset();
	}

	@Override
	public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		Trace.print(Trace.SUITE, "processElementAttributes( "
	            + aQualifiedName, true );
    	if (aQualifiedName.equalsIgnoreCase(ParameterXmlHandler.START_ELEMENT))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
				Trace.append( Trace.SUITE, ", " + att.getQName(i) + "=" + att.getValue(i) );
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_ID))
		    	{
		        	myParameterId = att.getValue(i);
		    	}
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_TYPE))
		    	{
		        	myParameterType = att.getValue(i);
		    	}
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_SEQUENCE))
		    	{
		    		mySequence = Integer.valueOf( att.getValue(i) ).intValue();
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
     	if ( aQualifiedName.equalsIgnoreCase(START_ELEMENT) )
    	{
     		// We'll create a ParameterXmlHandler for Sub Parameters only when we need it.
     		// Otherwise it would create an endless loop.
     		myParameterXmlHandler = new ParameterXmlHandler( this.getXmlReader() );
   			this.addStartElementHandler(START_ELEMENT, myParameterXmlHandler);
   			myParameterXmlHandler.addEndElementHandler(START_ELEMENT, this);
    	}
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
		Trace.println(Trace.SUITE);
    	if (aQualifiedName.equalsIgnoreCase( START_ELEMENT ))
    	{
			try
			{
				Parameter subParam = myParameterXmlHandler.getParameter();
	   			mySubParameters.add(subParam);
			}
			catch (TestSuiteException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   			myParameterXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase( VALUE_ELEMENT ))
    	{
			myValue = myValueXmlHandler.getValue().trim();
			myValueXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase( VARIABLE_ELEMENT ))
    	{
    		myVariableName = myVariableXmlHandler.getValue().trim();
    		myVariableXmlHandler.reset();
    	}
    	else
    	{ // Programming fault
			throw new Error( "Child XML Handler returned, but not recognized. The handler was probably defined " +
			                 "in the Constructor but not handled in handleReturnFromChildElement()");
    	}
	}

	/**
	 * @return the parameter
	 * @throws TestSuiteException when parameterId is not set, when there is no value
	 * 							 or when the parameter cannot be created for the current interface.
	 */
	public Parameter getParameter() throws TestSuiteException
	{
		Trace.println(Trace.GETTER, "getParameter()", true);
		if ( myParameterId.isEmpty() )
		{
			throw new TestSuiteException("Unknown Parameter ID");
		}

		Parameter parameter;
		if ( myValue != null )
		{
			if ( myCurrentInterface != null )
			{
				parameter = myCurrentInterface.createParameter( myParameterId, myParameterType, myValue );
			}
			else
			{
				parameter = DefaultParameterCreator.createParameter(myParameterId, myParameterType, myValue);
			}			
		}
		else if ( ! myVariableName.isEmpty() )
		{
			parameter = new ParameterVariable(myParameterId, myVariableName );
		}
		else if ( ! mySubParameters.isEmpty() )
		{
			parameter = new ParameterHash(myParameterId, mySubParameters );
		}
		else
		{
			throw new TestSuiteException("Unknown Value, Variable, or Sub-Parameter");
		}
		parameter.setIndex(mySequence);

		return parameter;
	}

	@Override
	public void reset()
	{
		Trace.println(Trace.SUITE);
		
		myParameterId = "";
		myParameterType = "string";
		mySequence = Integer.MAX_VALUE;

		myValue = null;
	    mySubParameters = new ParameterArrayList();
	    myVariableName = "";
	}

	/**
	 * Sets the Test Interface to the current interface in use
	 * Also sets the Test Interfaces of the child ParameterXmlHandlers (if any)
	 * 
	 * @param anInterface the Current TestInterface to use
	 */
	public void setCurrentInterface(TestInterface anInterface)
	{
		myCurrentInterface = anInterface;
		
		if ( myParameterXmlHandler != null )
		{
			myParameterXmlHandler.setCurrentInterface( anInterface );
		}
	}
}
