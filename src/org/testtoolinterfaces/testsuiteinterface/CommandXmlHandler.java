package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.TestInterface;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

/**
 * XmlHandler to read the command part of an XML file
 * <command interface="...">
 *   ...
 * </command>

 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */
public class CommandXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "command";
	public static final String DEFAULT_INTERFACE_NAME = "Default";
	
	private static final String ATTRIBUTE_INTERFACE = "interface";

	private String myCommand;
	private TestInterface myInterface;
    
    private TestInterfaceList myInterfaces;

	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader		The XML Reader
	 * @param anInterfaceList	A list of Supported Interfaces
	 */
	public CommandXmlHandler( XMLReader anXmlReader,
	                           TestInterfaceList anInterfaceList )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR, "CommandXmlHandler( anXmlreader )", true);

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
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_INTERFACE))
		    	{
		    		String iFaceName = att.getValue(i);
		    		if ( ! iFaceName.isEmpty() )
		    		{
			    		myInterface = myInterfaces.getInterface(iFaceName);
						if( myInterface == null )
						{
							throw new TestSuiteException( "Unknown interface: " + iFaceName );
						}
			    		Trace.println( Trace.ALL, "        myInterface -> " + iFaceName);
		    		}
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
		Trace.print(Trace.SUITE, "handleCharacters( " 
		            + aValue, true );
		String command = aValue.trim();
		if ( ! command.isEmpty() )
		{
			myCommand += command;
		}
	}

	@Override
	public void handleEndElement(String aQualifiedName)
	{
    	//nop
	}
	
	@Override
	public void handleGoToChildElement(String aQualifiedName)
	{
    	//nop
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
    	//nop
	}

	/**
	 * @return the command
	 */
	public String getCommand()
	{
		return myCommand;
	}

	/**
	 * @return the interface
	 */
	public TestInterface getInterface()
	{
		return myInterface;
	}
	
	@Override
	public void reset()
	{
		Trace.println(Trace.SUITE);

		myCommand = "";
		myInterface = myInterfaces.getInterface( DEFAULT_INTERFACE_NAME );
	}
}
