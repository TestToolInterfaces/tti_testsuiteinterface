package org.testtoolinterfaces.testsuiteinterface;

import java.io.File;
import java.util.Iterator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.testtoolinterfaces.testsuite.TestInterfaceListHelper;
import org.testtoolinterfaces.testsuite.TestInterface_stub;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepSequence;
import org.testtoolinterfaces.testsuite.impl.TestStepCommand;
import org.testtoolinterfaces.testsuite.impl.TestStepSelection;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.XMLReader;

public class TestStepSequenceXmlHandlerTester extends TestCase
{
	XMLReader myXmlReader;
	TestStepSequenceXmlHandler myHandler;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		System.out.println("==========================================================================");
		System.out.println(this.getName() + ":");
		
		if ( myXmlReader == null )
		{
			SAXParserFactory spf = SAXParserFactory.newInstance();
	        spf.setNamespaceAware(false);

	        SAXParser saxParser = spf.newSAXParser();
	        myXmlReader = saxParser.getXMLReader();
		}

		if ( myHandler == null )
		{
	        // create a Test Interface List
			TestInterfaceListHelper testInterfaceList = new TestInterfaceListHelper();
			testInterfaceList.put(new TestInterface_stub(CommandXmlHandler.DEFAULT_INTERFACE_NAME));

			TestInterface_stub testInterface = new TestInterface_stub("ifName");
			testInterface.addCommand( "action1" );
			testInterface.addCommand( "action3" );
			testInterface.addCommand( "check1" );
			testInterfaceList.put(testInterface);

			// create a handler
			myHandler = new TestStepSequenceXmlHandler( myXmlReader,
                                                        "execute",
                                                        testInterfaceList,
                                                        false );
			
		}

	}

	/**
	 * Test Cases
	 */
	public void testCase_getExecutionSteps()
	{
		parseFile("testExecutionFile.xml", myHandler);
		TestStepSequence stepSeq = myHandler.getSteps();

		TestStep[] steps = stepSeq.toArray();

    	Assert.assertEquals("Incorrect number of test steps", 4, steps.length);

    	Assert.assertEquals("Incorrect sequence Nr", 1, steps[0].getSequenceNr());
    	Assert.assertEquals("Incorrect sequence Nr", 2, steps[1].getSequenceNr());
    	Assert.assertEquals("Incorrect sequence Nr", 3, steps[2].getSequenceNr());
    	Assert.assertEquals("Incorrect sequence Nr", 5, steps[3].getSequenceNr());

//    	Assert.assertEquals( "Incorrect TestStep",
//    						 TestEntry.TYPE.Step,
//    						 steps[0].getType() );
    	Assert.assertEquals( "Incorrect TestStep description",
    						 "A description of the first action step.",
    						 steps[0].getDescription() );

//    	Assert.assertEquals( "Incorrect TestStep",
//    						 TestEntry.TYPE.Step,
//    						 steps[1].getType() );
    	Assert.assertEquals( "Incorrect TestStep description",
				 			 "A description of the if step.",
				 			 steps[1].getDescription() );

//    	Assert.assertEquals( "Incorrect TestStep",
//    						 TestEntry.TYPE.Step,
//    						 steps[2].getType() );
    	Assert.assertEquals( "Incorrect TestStep description",
				 			 "A description of the check step.",
				 			 steps[2].getDescription() );

//    	Assert.assertEquals( "Incorrect TestStep",
//    						 TestEntry.TYPE.Step,
//    						 steps[3].getType() );
    	Assert.assertEquals( "Incorrect TestStep description",
    						 "A description of the second action step.",
    						 steps[3].getDescription() );

    	Assert.assertEquals("Incorrect Command", "action1", ((TestStepCommand) steps[0]).getCommand());
       	Assert.assertEquals("Incorrect TestStep type", TestStepSelection.class, steps[1].getClass() );

       	TestStepSelection ifStep = (TestStepSelection) steps[1];
       	Assert.assertEquals("Incorrect Command", "action1",  ((TestStepCommand) ifStep.getIfStep()).getCommand());

       	TestStepSequence thenSteps = ifStep.getThenSteps();
       	Iterator<TestStep> thenItr = thenSteps.iterator();
       	Assert.assertEquals("Incorrect Command", "action1",  ((TestStepCommand) thenItr.next()).getCommand());
       	
       	TestStepSequence elseSteps = ifStep.getElseSteps();
       	Iterator<TestStep> elseItr = elseSteps.iterator();
       	Assert.assertEquals("Incorrect Command", "action1",  ((TestStepCommand) elseItr.next()).getCommand());
       	
       	Assert.assertEquals("Incorrect Command", "check1",  ((TestStepCommand) steps[2]).getCommand());
       	Assert.assertEquals("Incorrect Command", "action3", ((TestStepCommand) steps[3]).getCommand());
 	}
	
	public void testCase_noSteps()
	{
		try
		{
			parseFile_withExc("testExecutionFile_noSteps.xml", myHandler);
			fail( "No Exception thrown " );
		}
		catch (Exception e)
		{
			Assert.assertEquals("Incorrect exception", "No (child) XmlHandler defined for execution", e.getMessage());
		}
	}
	
	private void parseFile(String aFileName, XmlHandler aHandler)
	{
		try
		{
			parseFile_withExc(aFileName, aHandler);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void parseFile_withExc(String aFileName, XmlHandler aHandler) throws Exception
	{
		File testXmlFilesDir = new File ( "src" + File.separator +
                "test" + File.separator +
                "resources" + File.separator +
                "testXmlFiles" );

		File xmlTestFile = new File ( testXmlFilesDir, aFileName);

		// parse the document
		myXmlReader.setContentHandler(aHandler);
		myXmlReader.parse(xmlTestFile.getAbsolutePath());
	}
}
