package org.testtoolinterfaces.testsuiteinterface;

import java.io.File;
import java.io.IOError;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.testtoolinterfaces.testsuite.TestCase;
import org.testtoolinterfaces.utils.Trace;
import org.xml.sax.XMLReader;

public class TestCaseReader
{
	/**
	 * 
	 */
	public TestCaseReader()
	{
		Trace.println(Trace.LEVEL.CONSTRUCTOR);
	}

	/** 
	 * @throws IOError when reading fails
	 */
	public TestCase readTcFile( File aTestCaseFile )
	{
		Trace.println(Trace.LEVEL.SUITE, "readTcFile( " + aTestCaseFile.getName() + " )", true);

		// create a parser
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(false);
		TestCase testCase;
		try
		{
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();

	        // create a handler
			TestCaseXmlHandler handler = new TestCaseXmlHandler(xmlReader);

	        // assign the handler to the parser
	        xmlReader.setContentHandler(handler);

	        // parse the document
	        xmlReader.parse(aTestCaseFile.getAbsolutePath());
	        
	        testCase = handler.getTestCase();
		}
		catch (Exception e)
		{
			Trace.print(Trace.SUITE, e);
			throw new IOError( e );
		}

		return testCase;
	}
}
