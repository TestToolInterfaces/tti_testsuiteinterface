package org.testtoolinterfaces.testsuiteinterface;

import java.util.Hashtable;

import org.testtoolinterfaces.testsuite.TestCaseLink;
import org.testtoolinterfaces.testsuite.TestLink;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.Trace;
import org.xml.sax.XMLReader;

/**
 * XmlHandler to read the testcaselink part of an XML file
 * <testcaselink id="..." type="..." sequence="..." [any]="...">
 *   ...
 * </testcaselink>
 * 
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 *
 */
public class TestCaseLinkXmlHandler extends TestLinkXmlHandler
{
	public static final String START_ELEMENT = "testcaselink";

	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader		The XML Reader
	 */
	public TestCaseLinkXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR);
		
		this.reset();
	}

	/**
	 * @return the TestCaseLink
	 * @throws TestSuiteException when the Id is not set or when the link is not defined.
	 */
	public TestCaseLink getTestCaseLink() throws TestSuiteException
	{
		Trace.println(Trace.SUITE);

		TestLink link = this.getLink();
		String id = this.getId();
		
		if ( id.isEmpty() )
		{
			throw new TestSuiteException( "Unknown TestCase ID" );
		}

		if ( link == null )
		{
			throw new TestSuiteException( "Link to TestCase not specified", id, this.getSequence() );
		}

		TestCaseLink testCaseLink = new TestCaseLink( id,
		                                              this.getSequence(),
		                                              link,
		                                              this.getAnyAttributes(),
		                                              new Hashtable<String, String>() );

		return testCaseLink;
	}
}
