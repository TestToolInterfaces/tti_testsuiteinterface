package org.testtoolinterfaces.testsuiteinterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testtoolinterfaces.testsuite.TestInterface;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Mark;
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
    private static final Logger LOG = LoggerFactory.getLogger(CommandXmlHandler.class);

    public static final String START_ELEMENT = "command";
	public static final String DEFAULT_INTERFACE_NAME = "Default";
	
	private static final String ATTRIBUTE_INTERFACE = "interface";

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
		LOG.trace(Mark.CONSTRUCTOR, "{}, {}", anXmlReader, anInterfaceList);

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
		LOG.trace(Mark.SUITE, "{}, {}", aQualifiedName, att);
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
			    		LOG.trace(Mark.ALL, "        myInterface -> {}", iFaceName);
		    		}
		    	}
		    }
    	}
    }

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
		LOG.trace(Mark.SUITE, "");
		myInterface = myInterfaces.getInterface( DEFAULT_INTERFACE_NAME );

		super.reset();
	}
}
