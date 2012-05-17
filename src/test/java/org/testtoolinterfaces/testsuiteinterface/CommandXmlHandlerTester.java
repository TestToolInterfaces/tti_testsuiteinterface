package org.testtoolinterfaces.testsuiteinterface;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.testtoolinterfaces.testsuite.TestInterface;
import org.testtoolinterfaces.testsuite.TestInterfaceListHelper;
import org.testtoolinterfaces.testsuite.TestInterface_stub;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class CommandXmlHandlerTester extends TestCase
{
	TestInterface_stub myTestInterface;
	TestInterfaceListHelper myTestInterfaceList;
	
	CommandXmlHandler myCommandXmlHandler;
	XMLReader myXmlReader;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		System.out.println("==========================================================================");
		System.out.println(this.getName() + ":");

		// Create a Test Interface
		if ( myTestInterface == null )
		{
			myTestInterface = new TestInterface_stub("ifName");
			myTestInterface.addCommand( "action1" );
			myTestInterface.addCommand( "action3" );
			myTestInterface.addCommand( "check1" );
		}

        // create a Test Interface List
		if ( myTestInterfaceList == null )
		{
			myTestInterfaceList = new TestInterfaceListHelper();
			myTestInterfaceList.put(new TestInterface_stub(CommandXmlHandler.DEFAULT_INTERFACE_NAME));
			myTestInterfaceList.put(myTestInterface);
			
		}

		if ( myCommandXmlHandler == null )
		{
			SAXParserFactory spf = SAXParserFactory.newInstance();
	        spf.setNamespaceAware(false);

	        SAXParser saxParser = spf.newSAXParser();
	        myXmlReader = saxParser.getXMLReader();

			// create a handler
			myCommandXmlHandler = new CommandXmlHandler( myXmlReader,
			                                             myTestInterfaceList );

	        // assign the handler to the parser
			myXmlReader.setContentHandler(myCommandXmlHandler);
		}

	}

	/**
	 * Test Cases
	 */
	public void testCase_constructor()
	{
		TestInterfaceListHelper testInterfaceList = new TestInterfaceListHelper();
		testInterfaceList.put(new TestInterface_stub(CommandXmlHandler.DEFAULT_INTERFACE_NAME));

		try
		{
			// create a handler
			myCommandXmlHandler = new CommandXmlHandler( myXmlReader,
			                                             testInterfaceList );
		}
		catch (TestSuiteException e)
		{
			e.printStackTrace();
			fail( "Exception thrown " + e.getMessage() );
		}
	}

	/**
	 * Test Cases
	 */
	public void testCase_constructor_noDefaultInterface()
	{
		TestInterfaceListHelper testInterfaceList = new TestInterfaceListHelper();

		try
		{
			// create a handler
			myCommandXmlHandler = new CommandXmlHandler( myXmlReader,
			                                             testInterfaceList );
			fail( "No exception thrown" );
		}
		catch (TestSuiteException e)
		{
			Assert.assertEquals( "Wrong message in exception",
			                     "No default interface defined: " + CommandXmlHandler.DEFAULT_INTERFACE_NAME,
			                     e.getMessage() );
		}
	}

	/**
	 * Test Cases
	 */
	public void testCase_readCommand()
	{
		try
		{
			parseFile("command.xml");
			String command  = myCommandXmlHandler.getCommand();
			TestInterface iface = myCommandXmlHandler.getInterface();
			myCommandXmlHandler.reset();

			Assert.assertEquals("Incorrect sequence number", "action1", command);
	    	Assert.assertEquals("Incorrect interface", "ifName", iface.getInterfaceName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail( "Exception thrown " + e.getMessage() );
		}
	}
		
	/**
	 * Test Cases
	 */
	public void testCase_readCommand_unknownInterface()
	{
		try
		{
			parseFile("command_unknownInterface.xml");
		}
		catch (Exception e)
		{
			Assert.assertEquals("Not a SAXException", SAXException.class, e.getClass());
			Assert.assertEquals("Wrong Exception message", "Unknown interface: unknown", e.getMessage());
		}

		String command  = myCommandXmlHandler.getCommand();
		TestInterface iface = myCommandXmlHandler.getInterface();

		Assert.assertEquals("Incorrect sequence number", "", command);
		Assert.assertNotNull("Interface is null", iface);
    	Assert.assertEquals("Incorrect interface", CommandXmlHandler.DEFAULT_INTERFACE_NAME, iface.getInterfaceName());
	}
		
		
	private void parseFile(String aFileName) throws IOException, SAXException
	{
		File testXmlFilesDir = new File ( "src" + File.separator +
		                                  "test" + File.separator +
		                                  "resources" + File.separator +
		                                  "testXmlFiles" );

		File xmlTestFile = new File ( testXmlFilesDir, aFileName);

		// parse the document
		myXmlReader.parse(xmlTestFile.getAbsolutePath());
	}
}
