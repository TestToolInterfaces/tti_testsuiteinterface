package org.testtoolinterfaces.testsuiteinterface;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.testtoolinterfaces.testsuite.TestCase;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.Trace;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class TestCaseReader
{
	private TestInterfaceList myInterfaceList;
	private boolean myCheckStepParameter;

	/**
	 * 
	 */
	public TestCaseReader( TestInterfaceList anInterfaceList )
	{
		this( anInterfaceList, false );
	}

	/**
	 * 
	 */
	public TestCaseReader( TestInterfaceList anInterfaceList, boolean aCheckStepParameter )
	{
		Trace.println(Trace.CONSTRUCTOR);

		myInterfaceList = anInterfaceList;
		myCheckStepParameter = aCheckStepParameter;
	}

	/** 
	 * @throws TestSuiteException 
	 */
	public TestCase readTcFile( File aTestCaseFile ) throws TestSuiteException
	{
		Trace.println(Trace.SUITE, "readTcFile( " + aTestCaseFile.getName() + " )", true);

		// create a parser
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(false);
		TestCase testCase;
		try
		{
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();

	        // create a handler
			TestCaseXmlHandler handler = new TestCaseXmlHandler(xmlReader, myInterfaceList, myCheckStepParameter);

	        // assign the handler to the parser
	        xmlReader.setContentHandler(handler);

	        // parse the document
	        xmlReader.parse(aTestCaseFile.getAbsolutePath());
	        
	        testCase = handler.getTestCase();
		}
		catch (SAXException e)
		{
			// Any SAX exception, possibly wrapping another exception.
			Trace.print(Trace.SUITE, e);
			throw new TestSuiteException( e.getMessage() );
		}
		catch (ParserConfigurationException e)
		{
			// If a parser cannot be created which satisfies the requested configuration.
			Trace.print(Trace.SUITE, e);
			throw new Error( e );
		}
		catch (IOException e)
		{
			// An IO exception from the parser, possibly from a byte stream or character stream supplied by the application.
			// E.g. when the file cannot be read because it doesn't exist or does not have read permissions.
			Trace.print(Trace.SUITE, e);
			throw new TestSuiteException( e.getMessage() );
		}

		return testCase;
	}
}
