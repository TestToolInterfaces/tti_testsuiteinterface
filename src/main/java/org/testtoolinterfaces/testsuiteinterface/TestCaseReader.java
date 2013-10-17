package org.testtoolinterfaces.testsuiteinterface;

import java.io.File;
import java.io.IOError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testtoolinterfaces.testsuite.TestCase;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.Mark;
import org.testtoolinterfaces.utils.TTIException;
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
    private static final Logger LOG = LoggerFactory.getLogger(TestCaseReader.class);

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
		LOG.trace(Mark.CONSTRUCTOR, "{}, {}", anInterfaceList, aCheckStepParameter );

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
		LOG.trace(Mark.SUITE, "{}", aTestCaseFile);

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
}
