package org.testtoolinterfaces.testsuiteinterface;


import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.testtoolinterfaces.testsuite.TestStepArrayList;
import org.testtoolinterfaces.testsuite.TestStepFactory;
import org.testtoolinterfaces.testsuite.TestStepFactoryImpl;
import org.testtoolinterfaces.testsuiteinterface.ExecutionXmlHandler;
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
		TestStepArrayList steps = parseFile("testExecutionFile.xml");

    	Assert.assertEquals("Incorrect number of test steps", 3, steps.size());

    	Assert.assertEquals("Incorrect sequence Nr", 1, steps.get(0).getSequenceNr());
    	Assert.assertEquals("Incorrect sequence Nr", 5, steps.get(1).getSequenceNr());
    	Assert.assertEquals("Incorrect sequence Nr", 3, steps.get(2).getSequenceNr());

    	Assert.assertEquals( "Incorrect TestStep",
    						 "A description of the first action step.",
    						 steps.get(0).getDescription() );
    	Assert.assertEquals( "Incorrect TestStep",
    						 "A description of the second action step.",
    						 steps.get(1).getDescription() );
    	Assert.assertEquals( "Incorrect TestStep",
				 			 "A description of the check step.",
				 			 steps.get(2).getDescription() );

    	Assert.assertEquals("Incorrect Command", "actionScript_1", steps.get(0).getCommand());
    	Assert.assertEquals("Incorrect Command", "actionScript_3", steps.get(1).getCommand());
    	Assert.assertEquals("Incorrect Command", "checkScript_1", steps.get(2).getCommand());

	}
	
	public void testCase_noSteps()
	{
		TestStepArrayList steps = parseFile("testExecutionFile_noSteps.xml");

    	Assert.assertEquals("Incorrect number of test steps", 0, steps.size());
	}
	
	private TestStepArrayList parseFile(String aFileName)
	{
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(false);
        SAXParser saxParser;
		try
		{
			saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();

	        // create a handler
			TestStepFactory testStepFactory = new TestStepFactoryImpl();
	        ExecutionXmlHandler handler = new ExecutionXmlHandler(xmlReader, testStepFactory);

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

	        return handler.getExecutionSteps();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return new TestStepArrayList();
	}
}
