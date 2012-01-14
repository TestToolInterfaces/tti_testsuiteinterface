package org.testtoolinterfaces.testsuiteinterface;


import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.testtoolinterfaces.testsuite.TestEntry;
import org.testtoolinterfaces.testsuite.TestInterface_stub;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepCommand;
import org.testtoolinterfaces.testsuite.TestStepSequence;
import org.testtoolinterfaces.testsuiteinterface.TestStepSequenceXmlHandler;
import org.xml.sax.XMLReader;


public class ExecutionXmlHandlerTester extends TestCase
{
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		System.out.println("==========================================================================");
		System.out.println(this.getName() + ":");
		
	}

	/**
	 * Test Cases
	 */
	public void testCase_getExecutionSteps()
	{
		TestStepSequence stepSeq = parseFile("testExecutionFile.xml");
		TestStep[] steps = stepSeq.toArray();

    	Assert.assertEquals("Incorrect number of test steps", 3, steps.length);

    	Assert.assertEquals("Incorrect sequence Nr", 1, steps[0].getSequenceNr());
    	Assert.assertEquals("Incorrect sequence Nr", 3, steps[1].getSequenceNr());
    	Assert.assertEquals("Incorrect sequence Nr", 5, steps[2].getSequenceNr());

    	Assert.assertEquals( "Incorrect TestStep",
    						 TestEntry.TYPE.Step,
    						 steps[0].getType() );
    	Assert.assertEquals( "Incorrect TestStep description",
    						 "A description of the first action step.",
    						 steps[0].getDescription() );

    	Assert.assertEquals( "Incorrect TestStep",
    						 TestEntry.TYPE.Step,
    						 steps[1].getType() );
    	Assert.assertEquals( "Incorrect TestStep description",
				 			 "A description of the check step.",
				 			 steps[1].getDescription() );

    	Assert.assertEquals( "Incorrect TestStep",
    						 TestEntry.TYPE.Step,
    						 steps[2].getType() );
    	Assert.assertEquals( "Incorrect TestStep description",
    						 "A description of the second action step.",
    						 steps[2].getDescription() );

    	Assert.assertEquals("Incorrect Command", "action1", ((TestStepCommand) steps[0]).getCommand());
    	Assert.assertEquals("Incorrect Command", "action3", ((TestStepCommand) steps[2]).getCommand());
    	Assert.assertEquals("Incorrect Command", "check1", ((TestStepCommand) steps[1]).getCommand());
	}
	
	public void testCase_noSteps()
	{
		TestStepSequence steps = parseFile("testExecutionFile_noSteps.xml");

    	Assert.assertEquals("Incorrect number of test steps", 0, steps.size());
	}
	
	private TestStepSequence parseFile(String aFileName)
	{
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(false);
        SAXParser saxParser;
		try
		{
			saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();

	        // create a Test Interface List
			TestInterfaceListHelper testInterfaceList = new TestInterfaceListHelper();
			TestInterface_stub testInterface = new TestInterface_stub("ifName");
			testInterface.addCommand( "action1" );
			testInterface.addCommand( "action3" );
			testInterface.addCommand( "check1" );
			testInterfaceList.put(testInterface);

			// create a handler
			TestStepSequenceXmlHandler handler = new TestStepSequenceXmlHandler( xmlReader,
			                                                                     "execute",
			                                                                     testInterfaceList,
			                                                                     false );

	        // assign the handler to the parser
	        xmlReader.setContentHandler(handler);

			File jarFile = new File(  this.getClass().getProtectionDomain()
				                         						.getCodeSource()
				                         						.getLocation()
				                         						.toURI() );
			File testXmlFilesDir = new File ( jarFile.getParent(),  "test" + File.separator + 
														"org" + File.separator +
														"testtoolinterfaces" + File.separator +
														"testsuiteinterface" + File.separator +
														"testXmlFiles");
			File xmlTestFile = new File ( testXmlFilesDir, aFileName);
	        // parse the document
	        xmlReader.parse(xmlTestFile.getAbsolutePath());

	        return handler.getSteps();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return new TestStepSequence();
	}
}
