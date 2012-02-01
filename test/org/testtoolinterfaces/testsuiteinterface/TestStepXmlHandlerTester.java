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
import org.xml.sax.XMLReader;

public class TestStepXmlHandlerTester extends TestCase
{
	public static final String BASE_PATH = "C:\\Users\\Arjan\\Projects\\Testium\\Eclipses\\java\\workspace\\TestSuiteInterface";
	TestInterface_stub myTestInterface;
	TestInterfaceListHelper myTestInterfaceList;
	
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
			myTestInterfaceList.put(myTestInterface);
			
		}


	}

	/**
	 * Test Cases
	 */
	public void testCase_readTestStepCommand_1()
	{
		TestStep step = parseFile("testStep_commandNoParameters.xml");

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
		TestStep step = parseFile("testStep_command1Parameter.xml");

		Assert.assertEquals("Incorrect sequence number", 1, step.getSequenceNr());
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
		TestStep step = parseFile("testStep_command2Parameters.xml");

		Assert.assertEquals("Incorrect sequence number", 1, step.getSequenceNr());
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
	
	private TestStep parseFile(String aFileName)
	{
		SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(false);
        SAXParser saxParser;
		try
		{
			saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();

			// create a handler
			TestStepXmlHandler handler = new TestStepXmlHandler( xmlReader,
			                                                     myTestInterfaceList,
			                                                     false );

	        // assign the handler to the parser
	        xmlReader.setContentHandler(handler);

			File testXmlFilesDir = new File ( "test" + File.separator +
			                                  "org" + File.separator +
			                                  "testtoolinterfaces" + File.separator +
			                                  "testsuiteinterface" + File.separator +
			                                  "testXmlFiles" );

			File xmlTestFile = new File ( testXmlFilesDir, aFileName);

			// parse the document
			xmlReader.parse(xmlTestFile.getAbsolutePath());

	        return handler.getStep();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return new TestStepCommand( 9, "error", myTestInterface );
	}
}
