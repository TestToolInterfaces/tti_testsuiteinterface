package org.testtoolinterfaces.testsuiteinterface;

import java.io.File;
import java.io.IOError;

import org.testtoolinterfaces.testsuite.TestCase;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.TTIException;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.XMLReader;

/**
 * Class to read TestCases from a TTI-XML file
 * 
 * @author Arjan Kranenburg
 * @see http://www.testtoolinterfaces.org
 *
 */
public class TestCaseReader
{
	private TestInterfaceList myInterfaceList;
	private boolean myCheckStepParameter;

	/**
	 * Creates the reader
	 * 
	 * @param anInterfaceList		A list of supported interfaces.
	 * @param aCheckStepParameter	Flag to indicate if parameters in steps must be checked.
	 */
	public TestCaseReader( TestInterfaceList anInterfaceList, boolean aCheckStepParameter )
	{
		Trace.println(Trace.CONSTRUCTOR);

		myInterfaceList = anInterfaceList;
		myCheckStepParameter = aCheckStepParameter;
	}

	/**
	 * Creates the reader, without checking the parameters of steps
	 * 
	 * @param anInterfaceList a list of supported interfaces
	 */
	public TestCaseReader( TestInterfaceList anInterfaceList )
	{
		this( anInterfaceList, false );
	}

	/** 
	 * Reads the TTI-XML file
	 * 
	 * @param aTestCaseFile		The TTI-XML file to read
	 * @return	The TestCase
	 * @throws 	TestSuiteException	when something went wrong reading the file.
	 * 			E.g. when the file has no read permissions or is not well-formatted.
	 * @throws	IOError when disaster strikes.
	 */
	public TestCase readTcFile( File aTestCaseFile ) throws TestSuiteException
	{
		Trace.println(Trace.SUITE, "readTcFile( " + aTestCaseFile.getName() + " )", true);

		TestCase testCase;
		try {
			XMLReader xmlReader = XmlHandler.getNewXmlReader();
			TestCaseXmlHandler handler = new TestCaseXmlHandler(xmlReader, myInterfaceList, myCheckStepParameter);

			handler.parse(xmlReader, aTestCaseFile);
			testCase = handler.getTestCase();
		} catch (TTIException e) {
			throw new TestSuiteException( e.getMessage() );
		}
		
		return testCase;
	}
	
//	public TestCase readTcFile( File aTestCaseFile ) throws TestSuiteException
//	{
//		Trace.println(Trace.SUITE, "readTcFile( " + aTestCaseFile.getName() + " )", true);
//
//		// create a parser
//        SAXParserFactory spf = SAXParserFactory.newInstance();
//        spf.setNamespaceAware(false);
//		TestCase testCase;
//		try
//		{
//			SAXParser saxParser = spf.newSAXParser();
//			XMLReader xmlReader = saxParser.getXMLReader();
//
//	        // create a handler
//			TestCaseXmlHandler handler = new TestCaseXmlHandler(xmlReader, myInterfaceList, myCheckStepParameter);
//
//	        // assign the handler to the parser
//	        xmlReader.setContentHandler(handler);
//
//	        // parse the document
//	        xmlReader.parse(aTestCaseFile.getAbsolutePath());
//	        
//	        testCase = handler.getTestCase();
//		}
//		catch (SAXException e)
//		{
//			// Any SAX exception, possibly wrapping another exception.
//			Trace.print(Trace.SUITE, e);
//			throw new TestSuiteException( e.getMessage() );
//		}
//		catch (ParserConfigurationException e)
//		{
//			// If a parser cannot be created which satisfies the requested configuration.
//			Trace.print(Trace.SUITE, e);
//			throw new Error( e );
//		}
//		catch (IOException e)
//		{
//			// An IO exception from the parser, possibly from a byte stream or character stream supplied by the application.
//			// E.g. when the file cannot be read because it doesn't exist or does not have read permissions.
//			Trace.print(Trace.SUITE, e);
//			throw new TestSuiteException( e.getMessage() );
//		}
//
//		return testCase;
//	}
}
