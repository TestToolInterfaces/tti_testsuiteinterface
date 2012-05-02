package org.testtoolinterfaces.testsuiteinterface;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.testtoolinterfaces.testsuite.ParameterArrayList;
import org.testtoolinterfaces.testsuite.TestInterfaceListHelper;
import org.testtoolinterfaces.testsuite.TestInterface_stub;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepCommand;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.xml.sax.XMLReader;

public class TestStepXmlHandlerTester extends TestCase
{
	TestInterface_stub myTestInterface;
	TestInterfaceListHelper myTestInterfaceList;

	XMLReader myXmlReader;
	TestStepXmlHandler myHandler;

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

        // create a XML Reader
		if ( myXmlReader == null )
		{
			SAXParserFactory spf = SAXParserFactory.newInstance();
	        spf.setNamespaceAware(false);

	        SAXParser saxParser = spf.newSAXParser();
			myXmlReader = saxParser.getXMLReader();
		}

        // create a XML Reader
		if ( myHandler == null )
		{
			myHandler = new TestStepXmlHandler( myXmlReader, myTestInterfaceList, false );
		}
	}

	/**
	 * Test Cases
	 */
	public void testCase_readTestStepCommand_1()
	{
		parseFile("testStep_commandNoParameters.xml", myHandler);
		TestStep step = new TestStepCommand( 9, "error", myTestInterface );
		try
		{
			step = myHandler.getStep();
			myHandler.reset();
		}
		catch (TestSuiteException e)
		{
			e.printStackTrace();
			myHandler.reset();
			fail( "TestStep could not be read" );
		}

		Assert.assertEquals("Incorrect sequence number", 1, step.getSequenceNr());
    	Assert.assertEquals("Incorrect stepType", TestStepCommand.class, step.getClass());

    	TestStepCommand stepCommand = (TestStepCommand) step;
    	Assert.assertEquals("Incorrect Command", "action1", stepCommand.getCommand());
    	Assert.assertEquals("Incorrect Interface", myTestInterface, stepCommand.getInterface());
    	Assert.assertEquals("Incorrect Description", "A description of the first action step.", stepCommand.getDescription());

    	ParameterArrayList Parameters = step.getParameters();
    	Assert.assertEquals("Incorrect number of parameters", 0, Parameters.size());
	}
	
	/**
	 * Test Cases
	 */
	public void testCase_reset()
	{
		parseFile("testStep_commandNoParameters.xml", myHandler);
		myHandler.reset();

		parseFile("testStep_commandNoParameters.xml", myHandler);
		TestStep step = new TestStepCommand( 9, "error", myTestInterface );
		try
		{
			step = myHandler.getStep();
			myHandler.reset();
		}
		catch (TestSuiteException e)
		{
			e.printStackTrace();
			myHandler.reset();
			fail( "TestStep could not be read" );
		}

		Assert.assertEquals("Incorrect sequence number", 1, step.getSequenceNr());
    	Assert.assertEquals("Incorrect stepType", TestStepCommand.class, step.getClass());

    	TestStepCommand stepCommand = (TestStepCommand) step;
    	Assert.assertEquals("Incorrect Command", "action1", stepCommand.getCommand());
    	Assert.assertEquals("Incorrect Interface", myTestInterface, stepCommand.getInterface());
    	Assert.assertEquals("Incorrect Description", "A description of the first action step.", stepCommand.getDescription());

    	ParameterArrayList Parameters = step.getParameters();
    	Assert.assertEquals("Incorrect number of parameters", 0, Parameters.size());
	}
	
	/**
	 * Test Cases
	 */
	public void testCase_readTestStepCommand_2()
	{
		parseFile("testStep_command1Parameter.xml", myHandler);
		TestStep step = new TestStepCommand( 9, "error", myTestInterface );
		try
		{
			step = myHandler.getStep();
			myHandler.reset();
		}
		catch (TestSuiteException e)
		{
			e.printStackTrace();
			myHandler.reset();
			fail( "TestStep could not be read" );
		}

		Assert.assertEquals("Incorrect sequence number", 2, step.getSequenceNr());
    	Assert.assertEquals("Incorrect stepType", TestStepCommand.class, step.getClass());

    	TestStepCommand stepCommand = (TestStepCommand) step;
    	Assert.assertEquals("Incorrect Command", "action1", stepCommand.getCommand());
    	Assert.assertEquals("Incorrect Interface", myTestInterface, stepCommand.getInterface());
    	Assert.assertEquals("Incorrect Description", "A description of the first action step.", stepCommand.getDescription());

    	ParameterArrayList Parameters = step.getParameters();
    	Assert.assertEquals("Incorrect number of parameters", 1, Parameters.size());
    	Assert.assertEquals("Incorrect parameter index", 1, Parameters.get(0).getIndex());
	}
	
	/**
	 * Test Cases
	 */
	public void testCase_readTestStepCommand_3()
	{
		parseFile("testStep_command2Parameters.xml", myHandler);
		TestStep step = new TestStepCommand( 9, "error", myTestInterface );
		try
		{
			step = myHandler.getStep();
			myHandler.reset();
		}
		catch (TestSuiteException e)
		{
			e.printStackTrace();
			myHandler.reset();
			fail( "TestStep could not be read" );
		}

		Assert.assertEquals("Incorrect sequence number", 3, step.getSequenceNr());
    	Assert.assertEquals("Incorrect stepType", TestStepCommand.class, step.getClass());

    	TestStepCommand stepCommand = (TestStepCommand) step;
    	Assert.assertEquals("Incorrect Command", "action1", stepCommand.getCommand());
    	Assert.assertEquals("Incorrect Interface", myTestInterface, stepCommand.getInterface());
    	Assert.assertEquals("Incorrect Description", "A description of the first action step.", stepCommand.getDescription());

    	ParameterArrayList Parameters = step.getParameters();
    	Assert.assertEquals("Incorrect number of parameters", 2, Parameters.size());
    	Assert.assertEquals("Incorrect parameter index", 1, Parameters.get(0).getIndex());
    	Assert.assertEquals("Incorrect parameter index", 3, Parameters.get(1).getIndex());
	}
	
	private void parseFile(String aFileName, TestStepXmlHandler aHandler)
	{
        // assign the handler to the parser
        myXmlReader.setContentHandler(aHandler);

		File testXmlFilesDir = new File ( "src" + File.separator +
                "test" + File.separator +
                "resources" + File.separator +
                "testXmlFiles" );
	
		File xmlTestFile = new File ( testXmlFilesDir, aFileName);

		try
		{
			// parse the document
			myXmlReader.parse(xmlTestFile.getAbsolutePath());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
