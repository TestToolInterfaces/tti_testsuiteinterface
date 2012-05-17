package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.TestInterface;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
//import org.testtoolinterfaces.utils.XmlHandler;
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
public class CommandXmlHandler extends GenericTagAndStringXmlHandler
{
	public static final String START_ELEMENT = "command";
	public static final String DEFAULT_INTERFACE_NAME = "Default";
	
	private static final String ATTRIBUTE_INTERFACE = "interface";

//	private String myCommand;
	private TestInterface myInterface;
    
    private TestInterfaceList myInterfaces;

	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader			The XML Reader
	 * @param anInterfaceList		A list of Supported Interfaces
	 * @throws TestSuiteException	when the 'Default' interface is not in the interfacelist
	 */
	public CommandXmlHandler( XMLReader anXmlReader,
	                          TestInterfaceList anInterfaceList ) throws TestSuiteException
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR, "CommandXmlHandler( anXmlreader )", true);

		myInterfaces = anInterfaceList;
		if( myInterfaces.getInterface(DEFAULT_INTERFACE_NAME) == null )
		{
			throw new TestSuiteException( "No default interface defined: " + DEFAULT_INTERFACE_NAME );
		}
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
							this.reset();
							throw new TestSuiteException( "Unknown interface: " + iFaceName );
						}
			    		Trace.println( Trace.ALL, "        myInterface -> " + iFaceName);
		    		}
		    	}
		    }
    	}
    }

//	@Override
//	public void handleCharacters(String aValue)
//	{
//		Trace.print(Trace.SUITE, "handleCharacters( " 
//		            + aValue + " )", true );
//		String command = aValue.trim();
//		if ( ! command.isEmpty() )
//		{
//			myCommand += command;
//		}
//	}

	/**
	 * @Deprecated use getCommand() and getInterface()
	 */
	public String getValue()
	{
		return getCommand();
	}

	/**
	 * @return the command
	 */
	public String getCommand()
	{
		return super.getValue();
	}

	/**
	 * @return the interface
	 */
	public TestInterface getInterface()
	{
		return myInterface;
	}

	@Override
	public String toString()
	{
		String stringRep = this.getInterface().getInterfaceName() + "->" + this.getCommand();
		return START_ELEMENT + ": " + stringRep;
	}

	@Override
	public void reset()
	{
		Trace.println(Trace.SUITE);
		super.reset();
		myInterface = myInterfaces.getInterface( DEFAULT_INTERFACE_NAME );
	}
}
