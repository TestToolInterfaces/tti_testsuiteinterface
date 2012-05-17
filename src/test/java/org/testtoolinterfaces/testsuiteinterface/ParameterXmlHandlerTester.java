package org.testtoolinterfaces.testsuiteinterface;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.testtoolinterfaces.testsuite.Parameter;
import org.testtoolinterfaces.testsuite.ParameterImpl;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class ParameterXmlHandlerTester extends TestCase
{
	ParameterXmlHandler myParameterXmlHandler;
	XMLReader myXmlReader;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		System.out.println("==========================================================================");
		System.out.println(this.getName() + ":");

		if ( myParameterXmlHandler == null )
		{
			SAXParserFactory spf = SAXParserFactory.newInstance();
	        spf.setNamespaceAware(false);

	        SAXParser saxParser = spf.newSAXParser();
	        myXmlReader = saxParser.getXMLReader();

			// create a handler
	        myParameterXmlHandler = new ParameterXmlHandler( myXmlReader );

	        // assign the handler to the parser
			myXmlReader.setContentHandler(myParameterXmlHandler);
		}

	}

	/**
	 * Test Cases
	 */
	public void testCase_readParamterValue()
	{
		try
		{
			parseFile("parameter_value.xml");
			Parameter parameter  = myParameterXmlHandler.getParameter();
			myParameterXmlHandler.reset();

			Assert.assertEquals("Incorrect parameter name", "par1", parameter.getName());
	    	Assert.assertEquals("Incorrect index", 3, parameter.getIndex());
			Assert.assertEquals("Incorrect parameter type", ParameterImpl.class, parameter.getClass());

			ParameterImpl paramVal = (ParameterImpl) parameter;
			Assert.assertEquals("Incorrect parameter value type", String.class, paramVal.getValueType());
			Assert.assertEquals("Incorrect parameter value", "val1", paramVal.getValueAsString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail( "Exception thrown " + e.getMessage() );
		}
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
